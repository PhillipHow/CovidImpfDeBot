package de.philliphow.covidimpfde.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.exceptions.ImpfDashboardApiException;

/***
 * Class for querying and caching vaccine delivery data. Access via singleton
 * pattern. 
 * 
 * Provides two singleton instances, a production instance for querying
 * the actual online data and a debug instance that only queries a local file 
 * for testing purposes. See class constants for online and local data source URLs.
 * 
 * @author PhillipHow
 *
 */
public class DeliveryApiManager extends AbstractTsvApiWithCache<DeliveryDataRow> {

	/***
	 * Singleton instance that queries the real data source (impfdashboard.de)
	 */
	private static DeliveryApiManager instance = null;
	/***
	 * Singleton instance that queries a local test file (see class constant)
	 */
	private static DeliveryApiManager debugInstance = null;

	public static final String RESSOURCE_URL = "https://impfdashboard.de/static/data/germany_deliveries_timeseries_v2.tsv";
	public static final String DEBUG_LOCAL_RESSOURCE_URL = "test-datasets/debug_delivery_timeseries.tsv";

	private DeliveryApiManager(boolean debugMode) {
		super(getRessourceUrl(debugMode));
	}

	private DeliveryApiManager() {
		this(false);
	}

	/***
	 * Queries the delivery data API and refreshed the cache if new data has been
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
	 * Returns the current list of vaccine deliveries in Germany. One line
	 * represents all summed up deliveries of a certain vaccine during a certain week. 
	 * Date will be the monday of the delivery week.
	 * 
	 * Note that this method does not query the actual data source, but the cache, and hence never
	 * fails to deliver data. If this is called before {@code getNewDataIfNecessary}
	 * is called, it will always return an empty list.
	 */
	@Override
	public List<DeliveryDataRow> getCurrentData() {

		/*
		 * Vaccine data is provided by the API per local state, so the data needs to be
		 * accumulated to get the federal deliveries.
		 * Also filter negative deliveries
		 */

		Map<String, List<DeliveryDataRow>> localDeliveriesGroupedByFederalDelivery = super.getCurrentData().stream()
				.filter(delivery -> delivery.getDoses() > 0)
				.collect(Collectors.groupingBy(e -> e.getCalendarWeekMonday() + "/" + e.getVaccineIdentifier()));

		List<DeliveryDataRow> federalDeliveries = new ArrayList<>();
		localDeliveriesGroupedByFederalDelivery.forEach((key, deliveryParts) -> {
			LocalDate deliveryDate =deliveryParts.get(0).getCalendarWeekMonday();
			String deliveryVaccine = deliveryParts.get(0).getVaccineIdentifier();

			DeliveryDataRow deliveryPartsAccumulated = new DeliveryDataRow(deliveryDate, deliveryVaccine, "DE", 0);
			for (DeliveryDataRow deliveryPart : deliveryParts) {
				deliveryPartsAccumulated = DeliveryDataRow.combineDeliveryParts(deliveryPartsAccumulated, deliveryPart);
			}
			federalDeliveries.add(deliveryPartsAccumulated);
		});

		federalDeliveries.sort((d1, d2) -> d1.getCalendarWeekMonday().compareTo(d2.getCalendarWeekMonday()));

		return federalDeliveries;
	}
	
	@Override
	public DeliveryDataRow buildOneRowFrom(String tsvRow, String headerRow) {
		return new DeliveryDataRow(tsvRow, headerRow);
	}

	/***
	 * Singleton pattern
	 * 
	 * @param if the instance to return should be an instance that queries the real
	 *        data source, or an local test file.
	 * @return class instance
	 */
	public static DeliveryApiManager getInstance(boolean debugMode) {
		return debugMode ? getDebugInstance() : getProdInstance();
	}

	private static DeliveryApiManager getDebugInstance() {

		if (debugInstance == null) {
			debugInstance = new DeliveryApiManager(true);
			return debugInstance;
		} else {
			return debugInstance;
		}
	}

	private static DeliveryApiManager getProdInstance() {

		if (instance == null) {
			instance = new DeliveryApiManager(false);
			return instance;
		} else {
			return instance;
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
			throw new RuntimeException("url to call is for deliveries could not be parsed!");
		}

	}

}
