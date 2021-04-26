package de.philliphow.covidimpfde.logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.strings.messagegenerators.DeliveryUpdateString;

/**
 * Class that draws meaningful conclusions from a {@link List} of
 * {@link DeliveryDataRow}s. Used by the {@link DeliveryUpdateString} to
 * assemble the delivery overview.
 * 
 * @author PhillipHow
 *
 */
public class DeliveryDataInterpretation {

	/**
	 * The data to be used
	 */
	private final List<DeliveryDataRow> data;
	/**
	 * A reference to the latest delivery in the data set, as this is needed quite
	 * often
	 */
	private final DeliveryDataRow latestDelivery;

	public DeliveryDataInterpretation(List<DeliveryDataRow> allDeliveries) {
		this.data = new ArrayList<>(allDeliveries);
		this.latestDelivery = this.data.get(this.data.size() - 1);
	}

	public DeliveryDataRow getLatestDelivery() {
		return latestDelivery;
	}

	public int getTotalDeliveredDoses() {
		return data.stream().mapToInt(delivery -> delivery.getDoses()).sum();
	}
	
	/**
	 * @return the total number of doses delivered during the last week 
	 */
	public int getLastWeekDelivieredDoses() {
		System.out.println(getLastWeeksDeliveries());
		return getLastWeeksDeliveries().stream().mapToInt(delivery -> delivery.getDoses()).sum();
	}
	
	/*
	 * @return the number of suppliers that delivered during the last week
	 */
	public int getLastWeekNumberOfSuppliers() {
		return getLastWeeksDeliveries().stream()
					.map(delivery -> delivery.getVaccineIdentifier())
					.collect(Collectors.toSet())
					.size();
	}
	
	private List<DeliveryDataRow> getLastWeeksDeliveries() {
		LocalDate lastMonday = latestDelivery.getCalendarWeekMonday();
		return data.stream()
					.filter(delivery -> delivery.getCalendarWeekMonday().equals(lastMonday))
					.collect(Collectors.toList());
	}

	/**
	 * Gets the total number of doses delivered per vaccine. Note: This method also
	 * includes deliveries of new vaccines that might not yet be returned by
	 * {@code Vaccine::getAll}. DO NOT query {@link VaccinationDataRow}s with
	 * {@link Vaccine}s in the returned Map, as the
	 * {@code Vaccine::getVaccinationsDataField} might resolve to null if vaccine is
	 * not yet known.
	 * 
	 * @return a map which contains the total number of delivered doses by vaccine
	 */
	public Map<Vaccine, Integer> getDosesDeliveredByVaccine() {
		Map<Vaccine, Integer> deliveryByVaccine = new HashMap<>();

		for (DeliveryDataRow deliveryDataRow : data) {
			Vaccine vaccine = deliveryDataRow.getVaccine();
			int doses = deliveryDataRow.getDoses();

			deliveryByVaccine.putIfAbsent(vaccine, 0);
			deliveryByVaccine.put(vaccine, deliveryByVaccine.get(vaccine) + doses);
		}

		return deliveryByVaccine;
	}

	/**
	 * Gets the share of each vaccine delivered so far. Note: This method also
	 * includes deliveries of new vaccines that might not yet be returned by
	 * {@code Vaccine::getAll}. DO NOT query {@link VaccinationDataRow}s with
	 * {@link Vaccine}s in the returned Map, as the
	 * {@code Vaccine::getVaccinationsDataField} might resolve to null if vaccine is
	 * not yet known.
	 * 
	 * @return a map which contains the delivery number share between 0 and 1 of
	 *         each vaccine
	 */
	public Map<Vaccine, Double> getShareByVaccine() {
		int totalDoses = getTotalDeliveredDoses();
		Map<Vaccine, Integer> dosesByVaccine = getDosesDeliveredByVaccine();
		Map<Vaccine, Double> shareByVaccine = new HashMap<>();

		dosesByVaccine.forEach((vaccine, doses) -> {
			shareByVaccine.put(vaccine, (double) doses / totalDoses);
		});

		return shareByVaccine;
	}

	/**
	 * @return the biggest delivery so far
	 */
	public DeliveryDataRow getBiggestDelivery() {
		DeliveryDataRow biggestDelivery = null;

		for (DeliveryDataRow current : data) {
			if (biggestDelivery == null || current.getDoses() > biggestDelivery.getDoses())
				biggestDelivery = current;
		}

		return biggestDelivery;
	}

	/**
	 * @param n number of deliveries to be returned.
	 * @return the last deliveries. Might be less then n if not enough delivery
	 *         records are present
	 */
	public List<DeliveryDataRow> getLastNDeliveries(int n) {
		List<DeliveryDataRow> listToReturn = new ArrayList<>(data);
		Collections.reverse(listToReturn);
		return listToReturn.subList(0, Math.min(n, listToReturn.size()));
	}

}
