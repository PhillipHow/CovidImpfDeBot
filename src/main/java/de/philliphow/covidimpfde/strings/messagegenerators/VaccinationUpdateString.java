package de.philliphow.covidimpfde.strings.messagegenerators;

import java.time.LocalDate;
import java.util.List;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.logic.DeliveryDataInterpretation;
import de.philliphow.covidimpfde.logic.VaccinationDataInterpretation;
import de.philliphow.covidimpfde.strings.StrUtil;

/**
 * Class for constructing a delivery update string from a
 * {@link DeliveryDataInterpretation}. Note that this class only does string
 * formatting, any calculations on the data are done by a
 * {@link VaccinationDataInterpretation}
 * 
 * @author PhillipHow
 *
 */
public class VaccinationUpdateString implements MessageStringGenerator {

	/**
	 * How many calendar week summaries should be shown on sundays?
	 */
	private static final int CALENDAR_WEEK_SUMMARY_SHOW_WEEKS = 7;

	/**
	 * Population quota milestones to list
	 */
	private static double[] POP_QUOTA_MILESTONES = {0.5, 0.6, 0.7, 0.8, 0.9};
	
	/**
	 * the data interpreter containing the vaccination data
	 */
	private final VaccinationDataInterpretation dataInterpreter;

	/**
	 * If the chat requesting this update has subscribed to daily Updates
	 */
	private final boolean isSubbed;
	
	/**
	 * Number of bot subs
	 */
	private final int subCount;

	public VaccinationUpdateString(VaccinationDataInterpretation vaccinationDataInterpretation, boolean isSubbed,
			int subCount) {
		this.dataInterpreter = vaccinationDataInterpretation;
		this.isSubbed = isSubbed;
		this.subCount = subCount;
	}

	@Override
	public String getTextAsMarkdown() {
		StringBuilder sb = new StringBuilder();

		sb.append(getHeadline());

		if (dataInterpreter.latestUpdateIsSunday()) {
			sb.append(getFirstSecondShotUpdate(false));
			sb.append(getVaccinesUpdate(false));
			sb.append(getWeekSummary());
			sb.append(getCalendarWeeksSummary());
			sb.append(getMilestoneEstimations());
		} else {
			sb.append(getHeader());
			sb.append(getVaccinesUpdate(true));
			sb.append(getFirstSecondShotUpdate(true));
			//sb.append(getRecordDaysUpdate());				//disabled for now - not interesting anymore
			sb.append(getMilestoneEstimations());
		}

		sb.append(getFooter());

		return sb.toString();
	}
	
	private String getHeadline() {
		String updateType = dataInterpreter.latestUpdateIsSunday() ? "Wöchentliches Impf-Update (jetzt mit Booster-Impfungen!)" : "Impf-Update (jetzt mit Booster-Impfungen!)";
		
		return String.format("*%s*\n-----------------------\n", updateType);
	}

	private String getHeader() {

		String dateString = StrUtil.capitalized(StrUtil.date(dataInterpreter.getLatestUpdate().getDate()));
		String updateShots = StrUtil.number(dataInterpreter.getLatestUpdateShotsToday());
		String oneWeekAgoDiff = StrUtil.difference(dataInterpreter.getLatestUpdateDiffOneWeekAgo());
		String boosterShots = StrUtil.number(dataInterpreter.getLatestUpdateThirdShots());

		return String.format(
				"*%s* wurden *%s Dosen* verteilt. Dies entspricht einer Veränderung von *%s* im Vergleich zu vor einer Woche. *%s* davon sind Booster-Impfungen.\n\n",
				dateString, updateShots, oneWeekAgoDiff, boosterShots);
	}

	private String getFirstSecondShotUpdate(boolean showDiff) {

		String shotsTotal = StrUtil.number(dataInterpreter.getTotalShots());
		String firstShotNumber = StrUtil.number(dataInterpreter.getTotalPersonsVaccinatedOnce());
		String firstShotsDiff = StrUtil.difference(dataInterpreter.getLatestUpdateNewFirstShots());
		String firstShotsPercent = StrUtil.percent(dataInterpreter.getPopulationQuotaVaccinatedOnce());
		String secondShotsNumber = StrUtil.number(dataInterpreter.getTotalPersonsVaccintedTwice());
		String secondShotsDiff = StrUtil.difference(dataInterpreter.getLatestUpdateNewSecondShots());
		String secondShotsPercent = StrUtil.percent(dataInterpreter.getPopulationQuotaVaccinatedFull());
		String thirdShotNumber = StrUtil.number(dataInterpreter.getTotalPersonsVaccinatedThrice());
		String thirdShotDiff = StrUtil.difference(dataInterpreter.getLatestUpdateNewThirdShots());
		String thirdShotPercent = StrUtil.percent(dataInterpreter.getPopulationQuotaVaccinatedThrice());
		
		if (showDiff)
			return String.format("Dosen insgesamt: *%s*\n1/2 Dosen: *%s* (*%s*), *%s*\n2/2 Dosen: *%s* (*%s*), *%s*\n3/2 Dosen: *%s* (*%s*), *%s*\n\n",
					shotsTotal,
					firstShotNumber, firstShotsPercent, firstShotsDiff, 
					secondShotsNumber, secondShotsPercent, secondShotsDiff, 
					thirdShotNumber, thirdShotPercent, thirdShotDiff);
		else
			return String.format("Dosen insgesamt: *%s*\n1/2 Dosen: *%s* (*%s*)\n2/2 Dosen: *%s* (*%s*)\n3/2 Dosen: *%s* (*%s*)\n\n",
					shotsTotal,
					firstShotNumber, firstShotsPercent, 
					secondShotsNumber, secondShotsPercent, 
					thirdShotNumber, thirdShotPercent);
	
	}

	private String getVaccinesUpdate(boolean showDiff) {

		StringBuilder sb = new StringBuilder();
		Vaccine.getAll().forEach(vaccine -> sb.append(getVaccineUpdate(vaccine, showDiff)));
		sb.append("\n");
		return sb.toString();
	}

	private String getVaccineUpdate(Vaccine vaccine, boolean showDiff) {

		String vaccineName = vaccine.getHumamReadableName();
		String shotsTotal = StrUtil.number(dataInterpreter.getLatestUpdate().getTotalShotsByVaccine(vaccine));
		String dayDiff = StrUtil.difference(dataInterpreter.getLatestUpdateDiffBy(vaccine));
		String percentVaccine = StrUtil.percent(dataInterpreter.getVaccineShare(vaccine));

		if (showDiff)
			return String.format("%s: *%s* (*%s*), *%s*\n", vaccineName, shotsTotal, percentVaccine, dayDiff);
		else
			return String.format("%s: *%s* (*%s*)\n", vaccineName, shotsTotal, percentVaccine);
	}

	@SuppressWarnings("unused")
	private String getRecordDaysUpdate() {
		int latestUpdateDayRanking = dataInterpreter.getLatestUpdateDayRanking();
		String latestUpdateRanking = StrUtil.placementStr(latestUpdateDayRanking);
		String latestUpdateShots = StrUtil.number(dataInterpreter.getLatestUpdateShotsToday());
		String recordDay = StrUtil.date(dataInterpreter.getBestDay().getDate());
		String recordDayShots = StrUtil.number(dataInterpreter.getBestDay().getShotsToday());

		if (latestUpdateDayRanking != 1) {
			return String.format(
					"*%s* entspricht dem *%s Tag* seit Beginn der Impfkampagne. Der beste Tag war *%s* mit *%s* Impfungen an einem Tag.\n\n",
					latestUpdateShots, latestUpdateRanking, recordDay, recordDayShots);
		} else {
			return String.format("Der heutige Tag stellt mit *%s* Dosen einen neuen Impfrekord auf!\n\n",
					latestUpdateShots);
		}
	}

	private String getMilestoneEstimations() {
		StringBuilder sb = new StringBuilder("*Erstimpfungs-Meilensteinschätzungen*\n");
		
		for(double firstShotQuota : POP_QUOTA_MILESTONES) {
			sb.append(getMilestoneEstimationForFirstShotQuota(firstShotQuota));
		}
		
		String movingTwoWeeksAverageFirst = StrUtil.number((int) dataInterpreter.getMovingFirstShotAverage());
		
		sb.append("_(beruht auf Erstimpfgeschwindigkeit der letzten zwei Wochen: durschnittlich " + movingTwoWeeksAverageFirst + " erstmalig geimpfte Menschen pro Tag)_\n");
		
		return sb.toString();
	}
	
	private String getMilestoneEstimationForFirstShotQuota(double firstShotQuota) {
		String percentageValue = StrUtil.percent(firstShotQuota);
		String date = StrUtil.date(dataInterpreter.getOneShotPopQuotaVaccinatedEstimation(firstShotQuota));
		boolean hasBeenReached = oneShotHerdImmunityGoalHasBeenReached(firstShotQuota);
		
		if (hasBeenReached) {
			return String.format("*%s*: %s ✅\n", percentageValue, date);
		} else {
			return String.format("*%s*: %s\n", percentageValue, date);
		}
	}
	
	private boolean oneShotHerdImmunityGoalHasBeenReached(double firstShotQuota) {
		LocalDate dateEstimation = dataInterpreter.getOneShotPopQuotaVaccinatedEstimation(firstShotQuota);
		LocalDate dateNow = LocalDate.now();
		return dateEstimation.isBefore(dateNow) || dateEstimation.isEqual(dateNow);
	}

	private String getWeekSummary() {
		List<VaccinationDataRow> lastWeek = dataInterpreter.getLastWeek();

		StringBuilder sb = new StringBuilder("*Zusammenfassung letzte Woche*\n");
		lastWeek.forEach(weekDay -> {
			String weekDayString = StrUtil.weekDayShort(weekDay.getDate());
			String dosesWeekDay = StrUtil.number(weekDay.getShotsToday());

			sb.append(String.format("%s - *%s*\n", weekDayString, dosesWeekDay));
		});

		sb.append("\n");

		return sb.toString();
	}

	private String getCalendarWeeksSummary() {
 
		StringBuilder sb = new StringBuilder();
		dataInterpreter.getLastNWeeklySummarys(CALENDAR_WEEK_SUMMARY_SHOW_WEEKS).forEach(weeklySummary -> {
			String calendarWeek = StrUtil.calendarWeek(weeklySummary.getCalendarWeekNumber());
			String calendarWeekDoses = StrUtil.number(weeklySummary.getTotalDoses());
			String calendarWeekFirstDoses = StrUtil.number(weeklySummary.getTotalFirstDoses());

			sb.append(String.format("%s - *%s* (*%s*)\n", calendarWeek, calendarWeekDoses, calendarWeekFirstDoses));
		});

		return String.format("*Wochenübersicht vergebene Dosen*\n_(in Klammern: nur Erst-Dosen)_\n%s\n", sb.toString());
	}

	private String getFooter() {
		return "\n" +  new MessageFooter(isSubbed, subCount).getTextAsMarkdown();
	}

}
