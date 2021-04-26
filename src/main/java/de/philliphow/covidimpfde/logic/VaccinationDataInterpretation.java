package de.philliphow.covidimpfde.logic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.api.models.WeeklySummary;
import de.philliphow.covidimpfde.strings.messagegenerators.VaccinationUpdateString;

/**
 * Class that draws meaningful conclusions from a {@link List} of
 * {@link VaccinationDataRow}s. Used by {@link VaccinationUpdateString} to
 * assemble the vaccination update String.
 * 
 * @author PhillipHow
 *
 */
public class VaccinationDataInterpretation {

	/**
	 * The number of days included into the moving average, used for herd immunity
	 * date estimation
	 */
	public static final int MOVING_AVERAGE_DAY_COUNT = 14;

	/**
	 * German population See www.destatis.de
	 */
	public static final int GERMAN_POPULATION = 83157201;

	/**
	 * The factor needed to reach herd immunity in the population. See method
	 * {@code getHerdImmunityEstimate} for more discussion about this.
	 */
	public static final double HERD_IMMUNITY_FACTOR_OPTIMISTC = 0.6;
	public static final double HERD_IMMUNITY_FACTOR_REALISTIC = 0.8;

	/**
	 * The data to be used
	 */
	private final List<VaccinationDataRow> data;
	/**
	 * Reference to the latest vaccination update, as this is needed quite often
	 */
	private final VaccinationDataRow latestUpdate;

	public VaccinationDataInterpretation(List<VaccinationDataRow> vaccinations) {
		this.data = new ArrayList<>(vaccinations);
		this.latestUpdate = data.get(data.size() - 1);
	}

	public VaccinationDataRow getLatestUpdate() {
		return latestUpdate;
	}

	/**
	 * @return how many shots have been issued on the last day
	 */
	public int getLatestUpdateShotsToday() {
		return latestUpdate.getShotsToday();
	}

	/**
	 * @return the difference between the shots issued on the last day and the day
	 *         the week before
	 */
	public int getLatestUpdateDiffOneWeekAgo() {
		VaccinationDataRow latest = latestUpdate;
		int oneWeekAgoIndex = data.size() - 1 - 7;
		if (oneWeekAgoIndex < 0)
			return latestUpdate.getShotsToday();
		VaccinationDataRow oneWeekEarlier = data.get(data.size() - 8);
		return latest.getShotsToday() - oneWeekEarlier.getShotsToday();
	}

	/**
	 * @return total shots issued so far. Includes first and second doses
	 */
	public int getTotalShots() {
		return latestUpdate.getTotalShots();
	}

	/**
	 * @return total people that have received at least one vaccine dose
	 */
	public int getTotalPersonsVaccinatedOnce() {
		return latestUpdate.getPersonsVaccinatedOnce();
	}

	/**
	 * @return total people that have finished the vaccination process
	 */
	public int getTotalPersonsVaccintedTwice() {
		return latestUpdate.getPersonsVaccinatedFull();
	}

	/**
	 * @return current quota of the population that has received at least one dose
	 *         of vaccine
	 */
	public double getPopulationQuotaVaccinatedOnce() {
		return latestUpdate.getPopulationQuotaVaccinatedOnce();
	}

	/**
	 * @return current quota of the population that has completed the vaccination
	 *         process
	 */
	public double getPopulationQuotaVaccinatedFull() {
		return latestUpdate.getPopulationQuotaVaccinatedFull();
	}

	/**
	 * @param vaccine
	 * @return new doses issued on the last day by vaccine
	 */
	public int getLatestUpdateDiffBy(Vaccine vaccine) {
		int lastUpdateNumber = latestUpdate.getTotalShotsByVaccine(vaccine);
		int beforeLastUpdateIndex = data.size() - 2;
		if (beforeLastUpdateIndex < 0)
			return 0;
		int beforeLastUpdateNumber = data.get(beforeLastUpdateIndex).getTotalShotsByVaccine(vaccine);

		return lastUpdateNumber - beforeLastUpdateNumber;
	}

	/**
	 * @param vaccine
	 * @return share of given vaccine. Number between 0 and 1
	 */
	public double getVaccineShare(Vaccine vaccine) {
		return (double) latestUpdate.getTotalShotsByVaccine(vaccine) / latestUpdate.getTotalShots();
	}

	/**
	 * Get the "day ranking" of the latest update. A day ranking of 1 indicates that
	 * this has been the most productive day (by doses issued) of the vaccination
	 * campaign, etc.
	 * 
	 * @return the day ranking of the latest update
	 */
	public int getLatestUpdateDayRanking() {

		int todayShots = latestUpdate.getShotsToday();
		int betterDays = 0;
		for (VaccinationDataRow currentDay : data) {
			if (currentDay.getShotsToday() > todayShots) {
				betterDays++;
			}
		}

		return betterDays + 1;
	}

	/**
	 * Gets the data row of the most productive day so far (by doses issued)
	 * 
	 * @return data row of the most productive day
	 */
	public VaccinationDataRow getBestDay() {
		return data.stream().max((d1, d2) -> Integer.compare(d1.getShotsToday(), d2.getShotsToday())).get();
	}

	/**
	 * @return true if the latest update was a sunday
	 */
	public boolean latestUpdateIsSunday() {
		return latestUpdate.getDate().getDayOfWeek().getValue() == 7;
	}

	/**
	 * @return Average number of first doses issued each day during the last
	 *         {@code MOVING_AVERAGE_DAY_COUNT} days
	 */
	public double getMovingFirstShotAverage() {
		int dayCounter = 0;
		int currentVaccinationDayIndex = data.size() - 1;
		int firstDosesInPeriod = 0;

		while (dayCounter < MOVING_AVERAGE_DAY_COUNT && currentVaccinationDayIndex >= 0) {
			firstDosesInPeriod += data.get(currentVaccinationDayIndex).getFirstShotsToday();
			dayCounter++;
			currentVaccinationDayIndex--;
		}

		return 1.0 * firstDosesInPeriod / dayCounter;
	}
	
	public double getMovingTotalShotsAverage() {
		
		int dayCounter = 0;
		int currentVaccinationDayIndex = data.size() - 1;
		int dosesInPeriod = 0;

		while (dayCounter < MOVING_AVERAGE_DAY_COUNT && currentVaccinationDayIndex >= 0) {
			dosesInPeriod += data.get(currentVaccinationDayIndex).getShotsToday();
			dayCounter++;
			currentVaccinationDayIndex--;
		}

		return 1.0 * dosesInPeriod / dayCounter;
	}
	
	/**
	 * Calculates the date on which 60% of the german population will be vaccinated
	 * at least once, if the speed of the last {@code MOVING_AVERAGE_DAY_COUNT} days
	 * is kept. 
	 * @return the herd immunity estimation date
	 */
	public LocalDate getOptimisticOneShotHerdImmunityDate() {
		return getOneShotHerdImmunityDateEstimation(HERD_IMMUNITY_FACTOR_OPTIMISTC);
	}
	
	/**
	 * Calculates the date on which 80% of the german population will be vaccinated
	 * at least once, if the speed of the last {@code MOVING_AVERAGE_DAY_COUNT} days
	 * is kept. 
	 * @return the herd immunity estimation date
	 */
	public LocalDate getRealisticOneShotHerdImmunityDate() {
		return getOneShotHerdImmunityDateEstimation(HERD_IMMUNITY_FACTOR_REALISTIC);
	}
	
	
	/**
	 * Calculates the date on which 60% of the german population will have received 
	 * two doses. Does not consider one-shot vaccines yet!
	 * @return
	 */
	public LocalDate getOptimisticTwoShotHerdImmunityDate() {
		return getTwoShotHerdImmunityDateEstimation(HERD_IMMUNITY_FACTOR_OPTIMISTC);
	}
	
	/**
	 * Calculates the date on which 80% of the german population will have received two
	 * doses. Does not consider one-shot vaccines yet!
	 * @return
	 */
	public LocalDate getRealisticTwoShotHerdImmunityDate() {
		return getTwoShotHerdImmunityDateEstimation(HERD_IMMUNITY_FACTOR_REALISTIC);
	}
	

	/**
	 * Estimates the date to which a certain part of the german population
	 * is vaccinated once. 
	 * 
	 * Uses the following formula: daysNeeded = (GERMAN_POPULATION *
	 * herdImmunityFactory - alreadyVaccinated) * currentFirstShotMovingAverage
	 * 
	 * This method builds on the following assumption:
	 * <li>Herd immunity is reached as soon as a certain factor of the population is
	 * vaccinated once. Although a second dose will be needed to gain full
	 * protection, current research seems to indicate that one dose already offers a
	 * reasonable protection. Furthermore, full immunization by the second doses
	 * will only be a matter of time at that point, due to the amount of vaccine
	 * that will be available (hopefully). That means that the full immunization
	 * will then only be a matter of constant time by that point.
	 * <li>The HERD_IMMNUITY_FACTOR is assumed to be 0.6, although many scientist
	 * estimate it to be 0.8 by now. This might be considered in a future update,
	 * maybe containing estimates with both values.
	 * 
	 * @param herdImmunity number between 0 and 1, how big the percentage of people vaccinated
	 * once should be
	 * @return the date at which point {@code herdImmunityFactor} of the Germany
	 *         population have been vaccinated once, if the speed of first shot vaccinations
	 *         are similar to the last {@code MOPVING_AVERAGE_DAY_COUNT} days
	 */
	private LocalDate getOneShotHerdImmunityDateEstimation(double herdImmunityFactor) {
		double firstShotMovingAverage = getMovingFirstShotAverage();
		int alreadyVaccinatedOnce = latestUpdate.getPersonsVaccinatedOnce();
		double vaccinationGoal = GERMAN_POPULATION * herdImmunityFactor;
		double peopleThatStillNeedToBeVaccinated = vaccinationGoal - alreadyVaccinatedOnce;
		
		if (alreadyVaccinatedOnce > vaccinationGoal) {
			// goal has already been reached, find out when
			return data.stream().filter(row -> row.getPersonsVaccinatedOnce() > vaccinationGoal).findFirst().get()
					.getDate();
		} else {
			int daysNeeded = (int) (peopleThatStillNeedToBeVaccinated / firstShotMovingAverage);
			return getLatestUpdate().getDate().plus(daysNeeded, ChronoUnit.DAYS);
		}
	}
	
	private LocalDate getTwoShotHerdImmunityDateEstimation(double herdImmunityFactor) {
		double shotMovingAverage = getMovingTotalShotsAverage();
		int totalShotsGiven = latestUpdate.getTotalShots();
		double shotGoal = GERMAN_POPULATION * herdImmunityFactor * 2; // 2 doses per person
		double shotsThatAreStillNeeded = shotGoal - totalShotsGiven;
		
		if (totalShotsGiven > shotGoal) {
			// goal has already been reached, find out when
			return data.stream().filter(row -> row.getTotalShots() > shotGoal).findFirst().get()
					.getDate();
		} else {
			int daysNeeded = (int) (shotsThatAreStillNeeded / shotMovingAverage);
			return getLatestUpdate().getDate().plus(daysNeeded, ChronoUnit.DAYS);
		}
		
	}
	

	/**
	 * @return all updates during the last 7 days
	 */
	public List<VaccinationDataRow> getLastWeek() {
		return data.subList(Math.max(data.size() - 7, 0), data.size());
	}

	/**
	 * @param n
	 * @return the last n {@link WeeklySummary}s, with the newest first
	 */
	public List<WeeklySummary> getLastNWeeklySummarys(int n) {
		List<WeeklySummary> weeklySummarys = WeeklySummary.generateFrom(data);
		Collections.reverse(weeklySummarys);

		return weeklySummarys.subList(0, Math.min(n, weeklySummarys.size()));

	}

}