package com.billhorvath.libs.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class FileWiperTest {

	private File file;

	@BeforeEach
	void setUp() throws IOException {
		this.file = Files.createTempFile("Foobar", "tmp").toFile();
	}

	@AfterEach
	void teardown() {
		this.file.deleteOnExit();
	}

	@Test
	public void fileWiperOverwritesFiles() throws IOException {
		Path path = this.file.toPath();
		Files.write(path, "Overwrite me!".getBytes());
		byte[] overwriters = "overwriters".getBytes();
		byte last = overwriters[overwriters.length - 1];
		FileWiper wiper = FileWiper.getInstance(this.file, overwriters);
		wiper.overwrite();
		byte[] overwritten = Files.readString(path).getBytes();
		for (byte b : overwritten){
			assertEquals(b, last);
		}
	}

	@Test
	public void fileWiperWipesFiles(){
		FileWiper wiper = FileWiper.getInstance(file);
		wiper.wipeAndDelete();
		assertFalse(file.exists());
	}
}
