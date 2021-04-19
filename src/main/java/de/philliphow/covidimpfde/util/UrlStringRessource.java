package de.philliphow.covidimpfde.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.pmw.tinylog.Logger;

/**
 * Class to read resources found under an {@link URL}. Provides synchronous read
 * access to download them to a {@link String}. URL can also point to local
 * files, if required.
 * 
 * @author PhillipHow
 *
 */
public class UrlStringRessource {

	/**
	 * The resource URL
	 */
	private final URL url;

	/**
	 * Constructs the resource.
	 * 
	 * @param dataSourceUrl the URL of the resource. Can be a web resource or a
	 *                      local file
	 */
	public UrlStringRessource(URL dataSourceUrl) {
		this.url = dataSourceUrl;
	}

	/**
	 * Tries to download the resource at the given URL and returns it as string.
	 * Snippet taken from
	 * https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
	 * 
	 * @return The resource as String
	 * @throws IOException, if the resource is not reachable
	 */
	public String getAsStringSync() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder ressourceLines = new StringBuilder();

		int readLines = 0;
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			ressourceLines.append(inputLine + "\n");
			readLines++;
		}
		Logger.debug("Successfull read " + readLines + " lines from " + url);
		in.close();

		// trim to remove last \n
		return ressourceLines.toString().trim();
	}
}
