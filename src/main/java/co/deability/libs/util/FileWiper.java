package co.deability.libs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static co.deability.libs.util.Strings.NL;

/**
 * FileWiper provides a platform-independent means of securely wiping (overwriting, then deleting)
 * files. It may be used to wipe an individual file, a directory (in which case all of the child
 * files and directories will be wiped as well), or an entire file structure housed on a storage
 * device.
 *
 * <p><em>IMPORTANT QUALIFIERS:</em> FileWiper is NOT designed for securely wiping an entire disk!
 * Data in an unused, previously written portion of a disk may be recoverable. This class is
 * strictly for deleting directories and files, and as such it <em>does not comply</em> with
 * industry standards such as DoD 5220.22 or NIST 800-88.</p>
 *
 * <p>FileWiper may be executed from the command line using <code>java -cp [classpath]
 * FileWiper fileToWipe [bytePatternString]</code>. If the path fileToWipe
 * is not fully specified from the root of the file system, the behavior of this class is
 * unspecified, and may be JVM- or OS-dependent. If the bytePatternString is supplied and is longer
 * than 3 bytes after conversion based on the UTF-8 character set, a warning about excessive
 * run time is offered to the user.</p>
 *
 * Note that FileWiper instances are immutable.
 *
 * <p>Exit codes:</p>
 * <ul>
 * <li>0 = The wiping operation completed successfully.</li>
 * <li>1 = The wiping operation failed due to an I/O or user error.</li>
 * </ul>
 *
 * <p>Note: Files that the user is not authorized to read/write are skipped in this
 * implementation. Subclasses may wish to override this behavior to exit on such exceptions.</p>
 *
 * @author Bill Horvath II
 * @version 1.2
 * @copyright (c) 2012 by William Horvath II
 * @license CC-A 2.0 (https://creativecommons.org/licenses/by/2.0/)
 * @attribution Please include a ReadMe file with your distribution that has a credit citing my name
 * and a link to https://billhorvath.com/ Thank you!
 */
public class FileWiper {

	private static final Logger LOG = LoggerFactory.getLogger(FileWiper.class);

	/**
	 * The default array of bytes to use for overwriting a file when they're not supplied by the
	 * user.
	 */
	private static final byte[] DEFAULT_PATTERNS = {Byte.MAX_VALUE, (byte) 0, Byte.MAX_VALUE};

	/**
	 * Specifies how to use this class from the command line, and is shown to the user when illegal
	 * parameters are submitted.
	 */
	private static final String USAGE =
			NL + "  Usage: java -cp \033[1;4mclasspath\033[0m com.billhorvath.libs.util" +
					".FileWiper \033[1;4mfilepath\033[0m [bytePatternString]" + NL + NL;

	/**
	 * Formatted error reports for use with various exceptions.
	 */
	private static final String
			PERMISSIONS_ERR = "You do not have permission to overwrite the file at " +
					"%s%nSkipping...",
			IO_ERR = "There was an I/O error overwriting the file at %s%nSkipping...",
			MISSING_FILE_ERR = "The file at %s is unexpectedly missing.%nSkipping...",
			UNEXPECTED_ERR = "There was an unexpected exception overwriting the file at " +
					"%s%nSkipping...",
			LISTING_FILES_ERR = "Unable to list the files contained in %s",
			PATTERN_SIZE_WARNING = "WARNING: The supplied byte pattern has more than three " +
					"bytes, which may significantly increase run time.";


	/**
	 * The file that will be wiped by this instance of FileWiper.
	 */
	private final File fileToWipe;

	/**
	 * The array of bytes used to overwrite non-directory files prior to deletion. Each byte in
	 * the array is written to every byte position in the file(s) being deleted; thus a file will
	 * be overwritten as many times as the length of this array.
	 */
	private final byte[] overwriters;


	protected FileWiper(File fileToWipe, byte[] overwriters) {
		this.fileToWipe = fileToWipe;
		this.overwriters = overwriters;
	}


	/**
	 * Provides a factory method for acquiring FileWiper instances which will use the {@link
	 * #DEFAULT_PATTERNS default byte pattern} for overwriting files.
	 *
	 * @param fileToWipe The file to be overwritten.
	 * @return A FileWiper instance prepped to wipe <code>fileToWipe</code>.
	 */
	public static FileWiper getInstance(File fileToWipe) {
		return new FileWiper(fileToWipe, DEFAULT_PATTERNS);
	}


	/**
	 * Provides a factory method for acquiring FileWiper instances which will use
	 * the supplied <code>patterns</code> for overwriting files. Note that if patterns is longer
	 * than three bytes, runtime to completion may be unacceptably long.
	 *
	 * @param fileToWipe The file to be overwritten and deleted.
	 * @param overwriters   The byte patterns which will be used to overwrite
	 *                      <code>fileToWipe</code> before it is deleted.
	 * @return A FileWiper instance prepped to wipe <code>fileToWipe</code>.
	 */

	public static FileWiper getInstance(File fileToWipe, byte[] overwriters) {
		if (LOG.isWarnEnabled()) {
			if (overwriters.length >= 4) {
				LOG.warn(PATTERN_SIZE_WARNING);
			}
		}
		return new FileWiper(fileToWipe, overwriters);
	}


	/**
	 * Allows this class to be invoked from the command line. For the operation to complete
	 * successfully, the <code>params</code> argument must be provided, must be of length 1 or 2,
	 * and the first argument must specify the path to an actual file. If invoked with a
	 * file path that points to a directory, the user will receive a confirmation prompt prior to
	 * wiping the directory.
	 *
	 * @param params The user-submitted parameters indicating the file to be wiped (required),
	 *                  and a string whose bytes will be used to overwrite the file prior to
	 *                  deletion (optional.)
	 */
	public static void main(String[] params) {
		if (params == null || params.length == 0 || params.length > 2) {
			System.err.println(USAGE);
			System.exit(1);
		}
		String filePath = params[0];
		byte[] overwriters = (params.length == 2)
				? params[1].getBytes(StandardCharsets.UTF_8)
				: DEFAULT_PATTERNS;
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
			if (overwriters.length > 3){
				Console console = System.console();
				if (console != null){
					console.writer()
							.print(PATTERN_SIZE_WARNING + " Do you wish to continue? (y/n): ");
					console.flush();
					String str = console.readLine();
					console.flush();
					if (Strings.isEmpty(str) || !str.trim().equalsIgnoreCase("y")){
						System.out.println("Response is not 'y'...Exiting...");
						System.exit(0);
					}
				}
			}
 			System.out.println("Wiping file...");
			FileWiper wiper = new FileWiper(checkMe, overwriters);
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
	 * Returns {@code true} if the supplied filePath is valid and points to a file which actually
	 * exists; {@code false} otherwise. "Valid", in this context, means that the supplied filePath
	 * is non-null, not the empty string (""), and not composed entirely of whitespace.
	 *
	 * @return {@code true} if filePath points to a file which exists; {@code false} otherwise.
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
	 * #overwriters}, then deleting the file itself. If fileToWipe is a directory, it's children will
	 * be recursively overwritten in the same fashion.
	 */
	public void wipeAndDelete() {
		if (fileToWipe.isDirectory()) {
			try {
				File[] childFiles = fileToWipe.listFiles();
				if (childFiles == null) {
					if (LOG.isErrorEnabled()) {
						LOG.error(String.format(LISTING_FILES_ERR, fileToWipe.getPath()));
					}
					System.exit(1);
				}
				for (File file : childFiles) {
					FileWiper wiper = new FileWiper(file, this.overwriters);
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
					LOG.warn(String.format(UNEXPECTED_ERR, fileToWipe.getPath()), e);
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
	 * <code>patterns</code>. Note that this method will have no effect on empty files (which
	 * have zero bytes to overwrite), and will skip files when it encounters an error, such as
	 * an IO or security exception. Subclasses may wish to redefine this behavior.
	 */
	protected void overwrite() {

		String filePath = fileToWipe.getPath();
		long size = fileToWipe.length();
		if (size == 0L) return;

		FileOutputStream output = null;
		byte[] overwriteWith = new byte[1];

		for (byte b : overwriters) {
			try {
				overwriteWith[0] = b;
				output = new FileOutputStream(fileToWipe);
				int pointer = 0;
				while (pointer++ < size) {
					output.write(overwriteWith, 0, overwriteWith.length);
				}
			}
			catch (FileNotFoundException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(MISSING_FILE_ERR, filePath), e);
				}
			}
			catch (SecurityException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(PERMISSIONS_ERR, filePath), e);
				}
			}
			catch (IOException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(IO_ERR, filePath), e);
				}
			}
			catch (Exception e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(UNEXPECTED_ERR, e);
				}
			}
			finally {
				try {
					if (output != null) {
						output.close();
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
