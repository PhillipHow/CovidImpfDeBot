package de.philliphow.covidimpfde.api;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.pmw.tinylog.Logger;

import de.philliphow.covidimpfde.api.models.AbstractTsvRow;
import de.philliphow.covidimpfde.services.UrlStringRessourceResolver;

/***
 * Abstracts a tsv (tab separated values) file provided by a web API. Gets tsv
 * resources, converts them to a list of T values and provides a cached view on
 * them. Tsv files to be used by this class must provide a tab separated list of
 * column names in their first line, and an arbitrary number of additional lines
 * with data values separated by tabs. The number of data fields in each line
 * must be equal to the number of header fields.
 * 
 * @author PhillipHow
 * @param <T> the type of elements extracted from the given data source
 */
public abstract class AbstractTsvApiWithCache<T extends AbstractTsvRow> {

	private List<T> cache = new ArrayList<>();
	private final URL dataSourceUrl;

	public AbstractTsvApiWithCache(URL dataSourceUrl) {
		this.dataSourceUrl = dataSourceUrl;
	}

	/**
	 * Builds an element of T from a plain string tsv row.
	 * 
	 * @param currentTsvRow the row to be converted
	 * @param headerTsvRow  the first row of the file
	 * @return the element of T
	 */
	protected abstract T buildOneRowFrom(String currentTsvRow, String headerTsvRow);

	/***
	 * Queries the dataSourceUrl for data and refreshes the cache.
	 * 
	 * @return true if new lines have been downloaded, false if no new data has been
	 *         found
	 * @throws IOException if the data source could not be read
	 */
	public boolean getNewDataIfNecessary() throws IOException {

		Logger.debug("Querying {} for new elements", dataSourceUrl);
		List<T> newData = getFreshData();

		if (newData.size() > cache.size()) {
			Logger.debug("Data list length has changed: old {} lines, new {} lines", cache.size(), newData.size());
			cache = newData;
			return true;
		} else {
			Logger.debug("Data list length has not changed - no new elements");
			return false;
		}
	}

	/***
	 * Synchronously queries the data source and returns the data.
	 * 
	 * @return the parsed data rows as list
	 * @throws IOException if the data source could not be reached
	 */
	private List<T> getFreshData() throws IOException {
		String tsvFile = new UrlStringRessourceResolver(this.dataSourceUrl).getAsStringSync();
		List<T> tsvDataRows = new ArrayList<>();

		Scanner dataRowScanner = new Scanner(tsvFile);
		String headerLine = dataRowScanner.nextLine();

		while (dataRowScanner.hasNextLine()) {
			tsvDataRows.add(this.buildOneRowFrom(dataRowScanner.nextLine(), headerLine));
		}

		Logger.debug("The following data fields are available: " + headerLine);

		dataRowScanner.close();
		return tsvDataRows;
	}

	/***
	 * Gets the currently cached data. Note that this method does not query the
	 * actual data source and hence never fails, even if data source is not
	 * reachable. Note that {@code getNewDataIsNecessary} needs to be called at
	 * least once successfully for this method to not return an empty list.
	 * 
	 * @return the current cache contents
	 */
	public List<T> getCurrentData() {
		return new ArrayList<>(cache);
	}
}
