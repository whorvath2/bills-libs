package co.deability.libs.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class NamedFuncsTest {

	@Test
	public void checkThatFileExistsWorksWithNullOrEmptyOrIllegalFilePaths(){
		String filePath = null;
		assertFalse(NamedFuncs.checkThatFile.exists(filePath));
		filePath = "";
		assertFalse(NamedFuncs.checkThatFile.exists(filePath));
		filePath = "  ";
		assertFalse(NamedFuncs.checkThatFile.exists(filePath));
		filePath = "#$%@##\\//\\/;:";
		assertFalse(NamedFuncs.checkThatFile.exists(filePath));
	}

	@Test
	public void checkThatFileExistsWorksWithExistingFile() throws IOException {
		Path path = Files.createTempFile("test",".tmp");
		String filePath = path.toString();
		File file = path.toFile();
		assertTrue(NamedFuncs.checkThatFile.exists(filePath));
		assertTrue(NamedFuncs.checkThatFile.exists(file));
		file.deleteOnExit();
	}
}
