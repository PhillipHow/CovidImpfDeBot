package de.philliphow.covidimpfde.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.pmw.tinylog.Logger;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.RequestLogger;

/**
 * Class to read resources found under an {@link URL}. Provides synchronous read
 * access to download them to a {@link String}. URL can also point to local
 * files, if required.
 * 
 * @author PhillipHow
 *
 */
public class UrlStringRessourceResolver {

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
	public UrlStringRessourceResolver(URL dataSourceUrl) {
		this.url = dataSourceUrl;
	}

	public String getAsStringSync() throws IOException {
		
		String protocol = this.url.getProtocol();
		if (protocol.equals("https") || protocol.equals("http"))
			return getHttpRessourceAsStringSync();
		else
			return getLocalResourceAsStringSync();
	}
	
	public String getHttpRessourceAsStringSync() throws IOException {
		
		BasicHttpClient httpClient = new BasicHttpClient(url.toString());
		httpClient.setRequestLogger(new HttpClientDisabledLogger());
	
		
		httpClient.addHeader("Accept-Encoding", "");
		HttpResponse response = httpClient.get("", new ParameterMap());
		
		if (response == null)
			throw new IOException("Ressource not readable!");
		
		return response.getBodyAsString().trim();
			
	
		
	}
	
	
	/**
	 * Tries to download the resource at the given URL and returns it as string.
	 * Snippet taken from
	 * https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
	 * 
	 * @return The resource as String
	 * @throws IOException, if the resource is not reachable
	 */
	public String getLocalResourceAsStringSync() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder ressourceLines = new StringBuilder();
		int readLines = 0;
		String inputLine = in.readLine();
		while (inputLine != null) {
			ressourceLines.append(inputLine + "\n");
			readLines++;
			inputLine = in.readLine();
		}
		System.out.println(inputLine);
		Logger.debug("Successfull read " + readLines + " lines from " + url);
		in.close();

		// trim to remove last \n
		return ressourceLines.toString().trim();
	}
	
	
	
	private class HttpClientDisabledLogger implements RequestLogger {

		@Override
		public boolean isLoggingEnabled() {
			return false;
		}

		@Override
		public void log(String msg) {
			// unused
		}

		@Override
		public void logRequest(HttpURLConnection urlConnection, Object content) throws IOException {
			// unused
		}

		@Override
		public void logResponse(HttpResponse httpResponse) {
			// unused
		}
		
		
		
	}
}
