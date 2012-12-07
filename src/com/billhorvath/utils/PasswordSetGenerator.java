package com.billhorvath.utils;

import java.util.*;
import javax.mail.*;

/**
A utility for generating a set of random passwords for a list of users. I created this when I was migrating from one database to another, and I needed to generate a new password for each user.

<p>This class reads a plain text CSV file containing user data, in which it expects to find one user per line. It will append a comma followed by a randomly-generated password (contained in quotes) to the end of each line. It will then write out the file.

<p>This class was designed to be used with the Import Users from CSV plugin (v0.5.1) for WordPress, written by PubPoet.

*/
public class PasswordSetGenerator{
	
	private final File file;
	private final int passwordLength;
	private final long seed;
	private final List<String> lines;
	private enum State {NOT_READ, READ, GENERATED};
	private State state;
	
	/**
	Constructs a PasswordSetGenerator instance in which <code>file</code> points to the file containing	the user data, <code>webSite</code> points to the location of the Web site where the user can log in, and <code>seed</code> is the initial value of the internal state for the random number generator. 
	*/
	private PasswordSetGenerator(File file, String webSite, int passwordLength, long seed){
		this.file = file;
		this.seed = seed;
		this.passwordLength = passwordLength;
		this.lines = new ArrayList<String>();
		this.state = NOT_READ;
	}
	/**
	Allows PasswordSetGenerator to run independently.
	*/
	public static void main(String[] params){
		
	}
	/**
	A static factory method for constructing and returning instances of PasswordSetGenerator.
	*/
	public static getInstance(File file, String webSite, int passwordLength, long seed){
		
	}
	/**
	Opens <code>file</code>, and returns a List view of the lines it contains.
	
	*/
	private void readLines(){
		//sanity check
		if (state != NOT_READ) return;
		
		FileInputStream input = null;
		InputStreamReader stream = null;
		BufferedReader buffer = null;
		try{
			input = new FileInputStream(file);
			stream = new InputStreamReader(input);
			buffer = new BufferedReader(stream);
			String str = new String();
			while (true){
				str = buffer.readLine();
				if (str != null) ? lines.add(str) : break;
			}
		}
		catch(FileNotFoundException e){
			System.err.println("Error: The file at " + file + " is missing.");
		}
		catch(SecurityException e){
			System.err.println("Error: You do not have permission to read the file at " + file);			
		}
		catch(IOException e){
			System.err.println("Error: There was an I/O error reading the file at " + file);
		}
		catch(Exception e){
			System.err.println("Error: There was an undefined exception reading " + file);
			e.printStackTrace();
		}
		finally{
			try{
				if (buffer != null){
					buffer.close();
				}
				if (stream != null){
					stream.close();
				}
				if (input != null){
					input.close();
				}
			}
			catch(Exception e){
				System.err.println(e.toString());
			}
		}
		
		state = READ;
	}
	/**
	Generates the passwords, and appends one to the end of each element in <code>lines</code>.
	*/
	private void generatePasswords(){
		//sanity check
		if (state != READ) return;
		Random random = new Random(seed);
		
		StringBuffer buffer = new StringBuffer(passwordLength);
		for (int i = 0; i < lines.size(); i++){
			
			for (int r = 0; r < passwordLength; r++){
				char c = random.nextInt(94) + 33;
				buffer.append(c);
			}
			String str = lines.get(i);
			assert !Strings.isEmpty(str);
			str = str + ", \"" + buffer.toString(); + "\""
			lines.set(i, str);
			buffer = new StringBuffer(passwordLength);
		}
		state = GENERATED;
	}
	/**
	Writes the lines out to the file.
	*/
	private void writeLines(){
	
		
		if (state != GENERATED) return;
	}
	/**
		
	*/
	public void createPasswords(){
		System.out.println("Reading file...");
		readLines();
		System.out.println("Generating passwords...");
		generatePasswords();
		System.out.println("Writing file...");
		writeLines();
		System.out.println("...Done!");
	}
}