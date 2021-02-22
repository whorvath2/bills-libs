package com.billhorvath.libs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.billhorvath.libs.util.Strings.NL;

/**
 * FileWiper provides a platform-independent means of securely wiping files. It may be used to wipe
 * an individual file, a set of files within a particular directory, or an entire file structure
 * housed on a storage device.
 *
 * <p><b>IMPORTANT QUALIFIER:</b> FileWiper is NOT designed for securely wiping an entire disk!
 * Data in the unused portion of a disk may be recoverable. This class is strictly for deleting
 * directories and files.</p>
 *
 * <p>FileWiper may be executed from the command line using the pattern <code>java -cp [classpath]
 * FileWiper [fileToWipe]</code>. If the path fileToWipe is not fully specified from the root of the
 * file system, the behavior of this class is unspecified, and may be JVM- or OS-dependent.</p>
 *
 * <p>Exit codes:</p>
 * <ul>
 * <li>0 = The wiping operation completed successfully.</li>
 * <li>1 = The wiping operation failed due to an i/o or user error.</li>
 * </ul>
 * <p>Note: Files that the user is not authorized to read/write are skipped in this
 * implementation. Subclasses may wish to override this behavior to exit on these exceptions.</p>
 *
 * @author Bill Horvath II
 * @version 1.0
 * @copyright (c) 2012 by William Horvath II
 * @license CC-A 2.0 (http://creativecommons.org/licenses/by/2.0/)
 * @attribution Please include a ReadMe file with your distribution that has a credit citing my name
 * and a link to http://billhorvath.com/ Thanks!
 */
public class FileWiper {

	private static final Logger LOG = LoggerFactory.getLogger(FileWiper.class);

	/**
	 * The default overwriting byte patterns to use when they're not specified by the user.
	 */
	private static final byte[] DEFAULT_PATTERNS = {Byte.MAX_VALUE, (byte) 0, Byte.MAX_VALUE};

	/**
	 * Specifies how to use this class from the command line, and is shown to the user when illegal
	 * paramaters are submitted.
	 */
	private static final String USAGE =
			NL + "Usage: java -cp [classpath] FileWiper filePath" + NL + NL;

	/**
	 * Formatted error reports for use with various exceptions.
	 */
	private static final String
			PERMISSIONS_ERR = "Error: You do not have permission to overwrite the file at " +
					"%s%nSkipping...",
			IO_ERR = "Error: There was an I/O error overwriting the file at %s%nSkipping...",
			MISSING_FILE_ERR = "Error: The file at %s is unexpectedly missing.%nSkipping...",
			UNEXPECTED_ERR = "Error: There was an unexpected exception overwriting the file at " +
					"%s%nSkipping...",
			LISTING_FILES_ERR = "Error: Unable to list the files contained in %s";


	/**
	 * The file that will be wiped by this instance of FileWiper.
	 */
	private final File fileToWipe;

	/**
	 * The collection of byte patterns used to overwrite each file.
	 */
	private final byte[] patterns;


	private FileWiper(File fileToWipe, byte[] patterns) {
		this.fileToWipe = fileToWipe;
		this.patterns = patterns;
	}


	private FileWiper(File fileToWipe) {
		this(fileToWipe, DEFAULT_PATTERNS);
	}


	/**
	 * Provides a factory method for stamping out instances of FileWiper which will use the {@link
	 * #DEFAULT_PATTERNS default patterns} for overwriting files.
	 *
	 * @param fileToWipe The file to be overwritten.
	 * @return A FileWiper instance prepped to wipe <code>fileToWipe</code>.
	 */
	public static FileWiper getInstance(File fileToWipe) {
		return new FileWiper(fileToWipe, DEFAULT_PATTERNS);
	}


	/**
	 * Provides a factory method for stamping out instances of FileWiper which will use
	 * <code>patterns</code> for overwriting files.
	 *
	 * @param fileToWipe The file to be overwritten.
	 * @param patterns   The byte patterns which will be used to overwrite <code>fileToWipe</code>.
	 * @return A FileWiper instance prepped to wipe <code>fileToWipe</code>.
	 */

	public static FileWiper getInstance(File fileToWipe, byte[] patterns) {
		if (LOG.isWarnEnabled()) {
			if (patterns.length >= 4) {
				LOG.warn("WARNING: patterns has more than three bytes, which may " +
						"significantly increase run time.");
			}
		}
		return new FileWiper(fileToWipe, patterns);
	}


	/**
	 * Allows this class to be invoked from the command line. For the operation to complete
	 * successfully, the <code>params</code> argument must be provided, must be of length 1, and
	 * must point to an actual file.
	 *
	 * @param params The user-submitted paramaters indicating the file to be wiped.
	 */
	public static void main(String[] params) {
		if (params == null || params.length != 1) {
			System.err.println(USAGE);
			System.exit(1);
		}
		String filePath = params[0];
		if (checkFile(filePath)) {
			File checkMe = new File(filePath);
			if (checkMe.isDirectory()) {
				Console console = System.console();
				if (console != null) {
					console.writer()
							.print("Warning: " + filePath + " is a directory. The directory, and " +
									"all of the files it contains, will be wiped. Do you wish to " +
									"continue? (yes/no): ");
					console.flush();
					String str = console.readLine();
					console.flush();
					if (Strings.isEmpty(str)) {
						System.err.println("Error: Empty response. Exiting...");
						System.exit(1);
					}
					str = str.toLowerCase().trim();
					if (!str.equals("yes")) {
						System.out.println("Response is not 'yes'...Exiting...");
						System.exit(0);
					}
				}
			}
			//Uses DEFAULT_PATTERNS to wipe the file.
			System.out.println("Wiping file using default byte patterns...");
			FileWiper wiper = new FileWiper(checkMe);
			wiper.wipeAndDelete();
			System.out.println("...Done!");
			System.exit(0);
		}
		else {
			System.err.println(USAGE);
			System.exit(1);
		}
	}


	/**
	 * Evaluates whether <code>filePath</code> is valid, and whether the file to which it points
	 * actually exists. "Valid", in this context, means that it is non-null, not empty, and not
	 * entirely whitespace.
	 *
	 * @return <code>true</code> if filePath points to a file which exists; <code>false</code>
	 * otherwise.
	 */
	private static boolean checkFile(String filePath) {

		if (Strings.isEmpty(filePath)) {
			if (LOG.isErrorEnabled()){
				LOG.error("Error: The file's path is empty." + NL);
			}
			return false;
		}
		File maybe = new File(filePath);
		if (!maybe.exists()) {
			if (LOG.isErrorEnabled()){
				LOG.error("Error: The specified file doesn't exist." + NL);
			}
			return false;
		}
		return true;
	}


	/**
	 * Wipes this instance's {@link #fileToWipe} by overwriting it with bytes specified by {@link
	 * #patterns}, then deleting the file itself. If fileToWipe is a directory, it's children will
	 * be recursively overwritten in the same fashion.
	 */
	private void wipeAndDelete() {
		if (fileToWipe.isDirectory()) {
			try {
				File[] childFiles = fileToWipe.listFiles();
				if (childFiles == null) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(String.format(LISTING_FILES_ERR, fileToWipe.getPath()));
					}
					System.exit(1);
				}
				for (File file : childFiles) {
					FileWiper wiper = new FileWiper(file, patterns);
					wiper.wipeAndDelete();
				}
				if (!fileToWipe.delete()){
					if (LOG.isWarnEnabled()){
						LOG.warn(String.format(UNEXPECTED_ERR, fileToWipe.getPath()));
					}
				}
			}
			catch (Exception e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(UNEXPECTED_ERR, fileToWipe.getPath()));
				}
			}
		}
		else try {
			overwrite();
			if (!fileToWipe.delete()) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(UNEXPECTED_ERR, fileToWipe.getPath()));
				}
			}
		}
		catch (SecurityException e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn(String.format(PERMISSIONS_ERR, fileToWipe.getPath()));
			}
		}
	}


	/**
	 * Overwrites <code>fileToWipe</code> repeatedly, once with each byte contained in
	 * <code>patterns</code>. Note that this method will skip files when it encounters an error,
	 * such as an IO or security exception. Subclasses may wish to redefine this behavior.
	 */
	protected void overwrite() {
		String filePath = fileToWipe.getPath();
		int size = 0;
		FileInputStream input = null;
		try {
			input = new FileInputStream(fileToWipe);
			while (input.read() != -1) {
				size++;
			}
			input.close();
		}
		catch (FileNotFoundException e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn(String.format(MISSING_FILE_ERR, filePath));
			}
		}
		catch (SecurityException e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn(String.format(PERMISSIONS_ERR, filePath));
			}
		}
		catch (IOException e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn(String.format(IO_ERR, filePath));
			}
		}
		catch (Exception e) {
			if (LOG.isWarnEnabled()){
				LOG.warn(String.format(UNEXPECTED_ERR, filePath));
			}
		}
		finally {
			try {
				if (input != null) {
					input.close();
				}
			}
			catch (Exception e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(e.toString());
				}
			}
		}

		FileOutputStream output = null;
		byte[] pattern = new byte[1];

		for (byte b : patterns) {
			try {
				pattern[0] = b;
				output = new FileOutputStream(fileToWipe);
				int pointer = 0;
				while (pointer < size) {
					output.write(pattern, 0, pattern.length);
					pointer++;
				}
			}
			catch (FileNotFoundException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(MISSING_FILE_ERR, filePath));
				}
			}
			catch (SecurityException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(PERMISSIONS_ERR, filePath));
				}
			}
			catch (IOException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(IO_ERR, filePath));
				}
			}
			catch (Exception e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(UNEXPECTED_ERR);
				}
				e.printStackTrace();
			}
			finally {
				try {
					if (output != null) {
						output.close();
					}
					if (input != null) {
						input.close();
					}
				}
				catch (Exception e) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(e.toString());
					}
				}
			}
		}
	}
}
