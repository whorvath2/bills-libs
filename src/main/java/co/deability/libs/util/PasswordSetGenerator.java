package co.deability.libs.util;

import java.util.*;
import java.io.*;

/**
A utility for generating a set of random passwords for a list of users. I created this when I was migrating from one database to another, and I needed to generate a new password for each user.

<p>This class reads a plain text CSV file containing user data, in which it expects to find one user per line. It will append a comma followed by a randomly-generated password (contained in quotes) to the end of each line. It will then write out the file.

<p><b>Important:</b>This class will overwrite the original file.

<p>This class was designed to be used with the Import Users from CSV plugin (v0.5.1) for WordPress, written by PubPoet.

*/
public class PasswordSetGenerator{
	
	/**
	The file from which lines will be read and to which lines with passwords appended will be written.	
	*/
	private final File file;
	/**
	The length of password to generate.	
	*/
	private final int passwordLength;
	/**
	The initial state of the random number generator.	
	*/
	private final long seed;
	/**
	The lines read from the file.	
	*/
	private final List<String> lines;
	/**
	Used for a sanity check to ensure methods are executed in the correct order.	
	*/
	private enum State {NOT_READ, READ, GENERATED};
	/**
	The sanity check value holder.	
	*/
	private State state;
	/**
	Used to validate and evaluate parameters submitted to the main method.
	*/
	private enum Flag {FILE, SEED, LENGTH;
		@Override
		public String toString(){
			switch(this){
				case FILE: return "-f";
				case SEED: return "-s";
				case LENGTH: return "-l";
				default: throw new IllegalArgumentException("Holy crap! You've screwed up an enum!");
			}
		}
	}
	/*
	Specifies how to use this class from the command line, and is shown to the user when illegal paramaters are submitted.
	*/
	private static final String USAGE = "\nUsage: java -cp classpath PasswordSetGenerator -f filePath [-l passwordLength] [-s seed]\n\n";

	/**
	Constructs a PasswordSetGenerator instance in which <code>file</code> points to the file containing	the user data, <code>passwordLength</code> is the desired length of the generated passwords, and <code>seed</code> is the initial value of the internal state for the random number generator.
	<p>Note: <code>lines</code> is initialized to an empty array list; <code>state</code> starts as NOT_READ.
	*/
	private PasswordSetGenerator(File file, int passwordLength, long seed){
		this.file = file;
		this.seed = seed;
		this.passwordLength = passwordLength;
		this.lines = new ArrayList<String>();
		this.state = State.NOT_READ;
	}
	/**
	Allows PasswordSetGenerator to run independently.
	
	@param params The string parameters submitted through the executing environment, most likely on the command line.
	*/
	public static void main(String[] params){
		if (params.length == 0 || params.length > 6){
			System.err.println("Error: Illegal arguments." + USAGE);
			System.exit(1);
		}
		
		String filePath = "";
		File file = null;
		String seedStr = "";
		long seed = 42L; //Might as well default to the meaning of life as a seed
		String passwordLengthStr = "";
		int passwordLength = 20; //Default to 20 character passwords; should be secure enough.
		
		String legalFlags = Arrays.toString(Flag.values()); // "-f, -s, -l"
		
		for (int i = 0; i < params.length; i++){
			String flag = params[i];
			if (!legalFlags.contains(flag)){
				System.err.println("Error: Illegal flags in the arguments." + USAGE);
				System.exit(1);
			}
			i++;
			String argument = params[i];
			if (flag.equals(Flag.FILE.toString())){
				filePath = argument;
				file = new File(filePath);
				if (!file.exists()){
					System.err.println("Error: filePath argument points to a file which does not exist." + USAGE);
					System.exit(1);
				}
			}
			else if (flag.equals(Flag.SEED.toString())){
				seedStr = argument;
				try{
					seed = Long.valueOf(seedStr);
				}
				catch(NumberFormatException e){
					System.err.println("Error: Illegal seed argument; must convert to a primitive long value." + USAGE);
					System.exit(1);
				}
			}
			else if (flag.equals(Flag.LENGTH.toString())){
				passwordLengthStr = argument;
				try{
					passwordLength = Integer.valueOf(passwordLengthStr);
				}
				catch(NumberFormatException e){
					System.err.println("Error: Illegal passwordLength argument; must convert to a primitive integer value." + USAGE);
					System.exit(1);
				}
				if (passwordLength < 8){
					System.out.println("Warning: The password length you've selected is too short to be secure.");
				}
				else if (passwordLength >= 30){
					System.out.println("Warning: The password length you've selected is unusually long. Be sure your system supports long passwords.");
				}
			}
		}
		PasswordSetGenerator generator = getInstance(file, passwordLength, seed);
		generator.createPasswords();
		System.exit(0);
	}
	/**
	A static factory method for constructing and returning instances of PasswordSetGenerator.
	
	@param file The file which contains the lines to which the passwords will be appended.
	@param passwordLength The length of the passwords that will be generated.
	@param seed The seed for the initial state of the random number generator.
	@return An instance of PasswordSetGenerator with fields initialized to the submitted values.
	*/
	public static PasswordSetGenerator getInstance(File file, int passwordLength, long seed){
		return new PasswordSetGenerator(file, passwordLength, seed);
	}
	/**
	Reads <code>file</code>, and returns a List view of the lines it contains.
	
	*/
	private void readLines(){
		//sanity check
		if (state != State.NOT_READ) return;
		
		FileInputStream input = null;
		InputStreamReader stream = null;
		BufferedReader buffer = null;
		try{
			input = new FileInputStream(file);
			stream = new InputStreamReader(input);
			buffer = new BufferedReader(stream);
			String str = null;
			while (true){
				str = buffer.readLine();
				if (str != null){
					lines.add(str.trim()); //Note we're trimming white space
				}
				else{
					break; 
				}
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
		
		state = State.READ;
	}
	/**
	Generates the passwords, and appends one to the end of each element in <code>lines</code>.
	*/
	private void generatePasswords(){
		//sanity check
		if (state != State.READ) return;
		Random random = new Random(seed);
		
		StringBuffer buffer = new StringBuffer(passwordLength);
		//Add ,"password" to the header row
		String str = lines.get(0);
		str += ",\"password\"";
		lines.set(0, str);
		for (int i = 1; i < lines.size(); i++){ // i=1 b/c we are skipping the first line, as it's headers
			
			for (int r = 0; r < passwordLength; r++){
				char c = (char)(random.nextInt(94) + 33);
				//We don't want to use quotes in a csv file with quoted values. :)
				while (c == (char)34){
					c = (char)(random.nextInt(94) + 33);
				}
				buffer.append(c);
			}
			str = lines.get(i);
			assert !Strings.isEmpty(str);
			str = str + ",\"" + buffer.toString() + "\"";
			lines.set(i, str);
			buffer = new StringBuffer(passwordLength);
		}
		state = State.GENERATED;
	}
	/**
	Writes <code>lines</code> out to the file.
	*/
	private void writeLines(){
		//sanity check
		if (state != State.GENERATED) return;
		
		FileWriter fwriter = null;
		BufferedWriter buffer = null;
		PrintWriter pwriter = null;
		
		try{
			fwriter = new FileWriter(file);
			buffer = new BufferedWriter(fwriter);
			pwriter = new PrintWriter(buffer);
			for (int i = 0; i < lines.size(); i++){
				String str = lines.get(i);
				pwriter.println(str);
			}
		}
		catch(FileNotFoundException e){
			System.err.println("Error: The file at " + file.getPath() + " is unexpectedly missing.");
			System.exit(1);
		}
		catch(SecurityException e){
			System.err.println("Error: You do not have permission to overwrite the file at " + file.getPath());			
			System.exit(1);
		}
		catch(IOException e){
			System.err.println("Error: There was an I/O error overwriting the file at " + file.getPath());
			System.exit(1);
		}
		catch(Exception e){
			System.err.println("Error: There was an unexpected exception overwriting the file at ");
			e.printStackTrace();
			System.exit(1);
		}
		finally{
			try{
				if (pwriter != null){
					pwriter.flush();
					pwriter.close();
				}
				if (buffer != null){
					buffer.close();
				}
				if (fwriter != null){
					fwriter.close();
				}
			}
			catch(Exception e){
				System.err.println(e.toString());
			}
		}
	}
	/**
	Runs the essential functions of this class: reads the lines of a csv file, rewrites each line with a randomly-generated password appended to the end, then outputs the results to the same file.
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
