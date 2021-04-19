package de.philliphow.covidimpfde.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.exceptions.ImpfDashboardApiException;

/***
 * Class for querying and caching daily vaccination data in Germany. Access via
 * singleton pattern. 
 * 
 * Provides two singleton instances, a production instance
 * for querying the actual online data and a debug instance that only queries a local
 * file for testing purposes. See class constants for online and local data source URLs.
 * 
 * @author PhillipHow
 *
 */
public class VaccinationsApiManager extends AbstractTsvApiWithCache<VaccinationDataRow> {

	/***
	 * Singleton instance that queries the real data source (impfdashboard.de)
	 */
	public static VaccinationsApiManager instance = null;
	/***
	 * Singleton instance that queries a local test file (see class constant)
	 */
	public static VaccinationsApiManager debugInstance = null;

	private static final String RESSOURCE_URL = "https://impfdashboard.de/static/data/germany_vaccinations_timeseries_v2.tsv";
	private static final String DEBUG_LOCAL_RESSOURCE_URL = "test-datasets/debug_vaccination_timeseries.tsv";

	private VaccinationsApiManager(boolean debugMode) {
		super(getRessourceUrl(debugMode));
	}

	public VaccinationsApiManager() {
		this(false);
	}

	@Override
	public VaccinationDataRow buildOneRowFrom(String tsvDataRow, String firstRow) {
		return new VaccinationDataRow(tsvDataRow, firstRow);
	}

	/***
	 * Queries the vaccination data API and refreshed the cache if new data has been
	 * found.
	 * 
	 * @return true, if new data has been found since the last call to this method
	 */
	@Override
	public boolean getNewDataIfNecessary() throws ImpfDashboardApiException {
		try {
			return super.getNewDataIfNecessary();
		} catch (IOException exception) {
			throw new ImpfDashboardApiException(exception);
		}
	}

	/***
	 * Singleton pattern getter
	 * 
	 * @param if the instance to return should be an instance that queries the real
	 *        data source, or an local test file.
	 * @return class instance
	 */
	public static VaccinationsApiManager getInstance(boolean debugMode) {
		return debugMode ? getDebugInstance() : getProdInstance();
	}

	private static VaccinationsApiManager getProdInstance() {

		if (instance == null) {
			instance = new VaccinationsApiManager();
			return instance;
		} else {
			return instance;
		}
	}

	private static VaccinationsApiManager getDebugInstance() {

		if (debugInstance == null) {
			debugInstance = new VaccinationsApiManager(true);
			return debugInstance;
		} else {
			return debugInstance;
		}
	}

	private static URL getRessourceUrl(boolean debugMode) {
		try {
			if (debugMode) {
				return new URL(new URL("file:"), DEBUG_LOCAL_RESSOURCE_URL);
			} else {
				return new URL(RESSOURCE_URL);
			}
		} catch (MalformedURLException exception) {
			throw new RuntimeException("url to call is for vaccinations could not be parsed!");
		}

	}

}
