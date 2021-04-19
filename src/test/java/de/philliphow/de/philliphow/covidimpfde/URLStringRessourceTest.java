package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.services.UrlStringRessourceResolver;

public class URLStringRessourceTest {

	private static final String TEST_LOCAL_FILE = "testfile.txt";
	private static final String TEST_LOCAL_FILE_NOT_EXISTS = "askdlflakjerlkajelrklaera.txt";
	private static final String TEST_ONLINE_RESOURCE = "https://jsonplaceholder.typicode.com/todos/1";
	private static final String TEST_ONLINE_RESOURCE_NOT_EXISTS = "https://zerjklaerlkajdakjle.com";
	private static final String TEST_FILE_CONTENT = "test string";

	UrlStringRessourceResolver example;

	@BeforeEach
	public void setUp() throws IOException {
		File f = new File(TEST_LOCAL_FILE);
		FileWriter fw = new FileWriter(f);
		fw.write(TEST_FILE_CONTENT);
		fw.close();
	}

	@AfterEach
	public void tearDown() {
		example = null;
		File f = new File(TEST_LOCAL_FILE);
		f.delete();
	}

	@Test
	public void testLocalFileRead() throws IOException {
		URL url = new URL(new URL("file:"), TEST_LOCAL_FILE);

		example = new UrlStringRessourceResolver(url);

		// trim, as URLStringRessource will add a \n at the end
		assertEquals(TEST_FILE_CONTENT, example.getAsStringSync());
	}

	@Test
	public void testOnlineFileRead() throws IOException {

		URL url = new URL(TEST_ONLINE_RESOURCE);
		example = new UrlStringRessourceResolver(url);

		String result = example.getAsStringSync();

		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	public void testFileNotExistsThrowsException() throws MalformedURLException {

		example = new UrlStringRessourceResolver(new URL(new URL("file:"), TEST_LOCAL_FILE_NOT_EXISTS));

		Assertions.assertThrows(IOException.class, () -> {
			example.getAsStringSync();
		});

	}

	@Test
	public void testOnlineRessourceNotExistThrowsException() throws MalformedURLException {

		example = new UrlStringRessourceResolver(new URL(new URL("file:"), TEST_ONLINE_RESOURCE_NOT_EXISTS));

		Assertions.assertThrows(IOException.class, () -> {
			example.getAsStringSync();
		});

	}

}
