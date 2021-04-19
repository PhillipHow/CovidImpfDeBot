package de.philliphow.covidimpfde.api.models;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A small class to encapsulate all vaccination doses during a calendar week.
 * 
 * @author PhillipHow
 *
 */
public class WeeklySummary {

	private final LocalDate weekMonday;
	private final int weekTotalDoses;

	private WeeklySummary(LocalDate weekMonday, int weekTotalDoses) {
		this.weekMonday = weekMonday;
		this.weekTotalDoses = weekTotalDoses;
	}

	public LocalDate getWeekdayMonday() {
		return weekMonday;
	}

	public int getCalendarWeekNumber() {
		return weekMonday.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
	}

	public int getYear() {
		return weekMonday.getYear();
	}

	public int getTotalDoses() {
		return weekTotalDoses;
	}

	/**
	 * Generates all weekly summaries from the given list of
	 * {@link VaccinationDataRow}s
	 * 
	 * @param vaccinationData vaccination data entries to include
	 * @return all weekly summaries
	 */
	public static List<WeeklySummary> generateFrom(List<VaccinationDataRow> vaccinationData) {
		Map<LocalDate, Integer> calenderWeekMap = getDosesByCalendarWeek(vaccinationData);

		return calenderWeekMap.entrySet().stream()
				.map(weekEntry -> new WeeklySummary(weekEntry.getKey(), weekEntry.getValue()))
				.collect(Collectors.toList());
	}

	/**
	 * Helper method to group the doses by calendar week
	 * 
	 * @param vaccinationData vaccination data entires to include
	 * @return a map that maps all calendar weeks (indicated by their mondays) to
	 *         the issued doses in that week
	 */
	private static Map<LocalDate, Integer> getDosesByCalendarWeek(List<VaccinationDataRow> vaccinationData) {
		Map<LocalDate, Integer> calendarWeekMap = new TreeMap<>();
		for (VaccinationDataRow vaccinationDataRow : vaccinationData) {
			LocalDate dataRowMonday = getMondayFor(vaccinationDataRow.getDate());
			calendarWeekMap.putIfAbsent(dataRowMonday, 0);
			calendarWeekMap.put(dataRowMonday, calendarWeekMap.get(dataRowMonday) + vaccinationDataRow.getShotsToday());
		}
		return calendarWeekMap;
	}

	private static LocalDate getMondayFor(LocalDate date) {
		int dayOfWeek = date.getDayOfWeek().getValue();
		LocalDate monday = date.minusDays(dayOfWeek - 1);
		return monday;
	}

}
