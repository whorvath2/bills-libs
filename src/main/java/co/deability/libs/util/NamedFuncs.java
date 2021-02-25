package co.deability.libs.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.function.*;


/**
 * A collection of {@link FunctionalInterface functional interfaces} with names (and method names)
 * reflecting their true purpose. These can be used to create cleaner code whose purpose is
 * clearer to those reading it.
 */
public interface NamedFuncs {


	/**
	 * Checks to see if a file path is valid. "Valid" in this context means that it is not null,
	 * not the empty space (""), not composed entirely of whitespace, and points to an
	 * actual file that exists in a file system that's accessible to the JVM in which this
	 * function is operating.
	 */
	@FunctionalInterface
	interface FilePathChecker{

		/**
		 * Returns {@code true} if the input string is not null, not the empty space (""), not
		 * composed entirely of whitespace, and points to a file which exists in the file system
		 * that's accessible to the JVM in which this function is operating; {@code false}
		 * otherwise.
		 *
		 * @param filePath A path to a file which may or may not be valid.
		 *
		 * @return {@code true} if the input string is not null, not the empty space (""), not
		 * composed entirely of whitespace, and points to a file which exists in the file system
		 * that's accessible to the JVM in which this function is operating; {@code false}
		 * otherwise.
		 */
		boolean exists(String filePath);

		default boolean exists(File file){
			return file != null && file.exists();
		}

		/**
		 * A predicate that can be used to filter a stream of files and return only those that
		 * are valid.
		 *
		 * @return {@code true} if the input string is not null, not the empty space (""), not
		 * composed entirely of whitespace, and points to a file which exists in the file system
		 * that's accessible to the JVM in which this function is operating; {@code false}
		 * otherwise.
		 */
		default Predicate<String> validFileFilter(){
			return NamedFuncs.checkThatFile::exists;
		}
	}

	/**
	 * A default implementation of FilePathChecker. Usage example:
	 * <pre>{@code
	 * import static NamedFuncs.checkThatFile;
	 * ...
	 * String filePath = "./my/file/path";
	 * if (checkThatFile.exists(filePath)){
	 *     ...
	 * }
	 * }</pre>
	 */
	FilePathChecker checkThatFile = (filePath) -> {
		try{
			return Strings.isNotEmpty(filePath)
					&& Files.exists(FileSystems.getDefault().getPath(filePath));
		}
		catch (InvalidPathException e){
			return false;
		}
	};

}
