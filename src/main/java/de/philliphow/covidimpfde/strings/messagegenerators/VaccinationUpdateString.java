package de.philliphow.covidimpfde.strings.messagegenerators;

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

		sb.append(getHeader());
		sb.append(getFirstSecondShotUpdate());
		sb.append(getVaccinesUpdate());

		if (dataInterpreter.latestUpdateIsSunday()) {
			sb.append(getWeekSummary());
			sb.append(getCalendarWeeksSummary());
		} else {
			sb.append(getRecordDaysUpdate());
			sb.append(getHerdImmunityEstimation());
		}

		sb.append(getFooter());

		return sb.toString();
	}

	private String getHeader() {

		String dateString = StrUtil.capitalized(StrUtil.date(dataInterpreter.getLatestUpdate().getDate()));
		String updateShots = StrUtil.number(dataInterpreter.getLatestUpdateShotsToday());
		String oneWeekAgoDiff = StrUtil.difference(dataInterpreter.getLatestUpdateDiffOneWeekAgo());

		return String.format(
				"*Impf-Update*\n-----------------------\n*%s* wurden *%s Dosen* verteilt. Dies entspricht einer Veränderung von *%s* im Vergleich zu vor einer Woche, aber meistens erfolgen noch Nachmeldungen.\n\n",
				dateString, updateShots, oneWeekAgoDiff);
	}

	private String getFirstSecondShotUpdate() {

		String shotsTotal = StrUtil.number(dataInterpreter.getTotalShots());
		String firstShotNumber = StrUtil.number(dataInterpreter.getTotalPersonsVaccinatedOnce());
		String firstShotsDiff = StrUtil.difference(dataInterpreter.getLatestUpdateNewFirstShots());
		String secondShotsNumber = StrUtil.number(dataInterpreter.getTotalPersonsVaccintedTwice());
		String secondShotsDiff = StrUtil.difference(dataInterpreter.getLatestUpdateNewSecondShots());
		String firstShotsPercent = StrUtil.percent(dataInterpreter.getPopulationQuotaVaccinatedOnce());
		String secondShotsPercent = StrUtil.percent(dataInterpreter.getPopulationQuotaVaccinatedFull());
		

		return String.format("Dosen insgesamt: *%s*\n1/2 Dosen: *%s* (*%s*), *%s*\n2/2 Dosen: *%s* (*%s*), *%s*\n\n",
				shotsTotal,
				firstShotNumber, firstShotsPercent, firstShotsDiff, secondShotsNumber, secondShotsPercent, secondShotsDiff);
	}

	private String getVaccinesUpdate() {

		StringBuilder sb = new StringBuilder();
		Vaccine.getAll().forEach(vaccine -> sb.append(getVaccineUpdate(vaccine)));
		sb.append("\n");
		return sb.toString();
	}

	private String getVaccineUpdate(Vaccine vaccine) {

		String vaccineName = vaccine.getHumamReadableName();
		String shotsTotal = StrUtil.number(dataInterpreter.getLatestUpdate().getTotalShotsByVaccine(vaccine));
		String dayDiff = StrUtil.difference(dataInterpreter.getLatestUpdateDiffBy(vaccine));
		String percentVaccine = StrUtil.percent(dataInterpreter.getVaccineShare(vaccine));

		return String.format("%s: *%s* (*%s*), *%s*\n", vaccineName, shotsTotal, percentVaccine, dayDiff);
	}

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

	private String getHerdImmunityEstimation() {
		String dailyFirstShotsMovingAverage = StrUtil.number((int) dataInterpreter.getMovingFirstShotAverage());
		String dailyTotalShotsMovingAverage = StrUtil.number((int) dataInterpreter.getMovingTotalShotsAverage());
		String herdImmunitydateFirstShot60 = StrUtil.date(dataInterpreter.getOptimisticOneShotHerdImmunityDate());
		String herdImmunityDateFirstShot80 = StrUtil.date(dataInterpreter.getRealisticOneShotHerdImmunityDate());
		String herdImmunityDateTwoShots60 = StrUtil.date(dataInterpreter.getOptimisticTwoShotHerdImmunityDate());
		String herdImmunityDateTwoShots80 = StrUtil.date(dataInterpreter.getRealisticTwoShotHerdImmunityDate());
		
		return String.format(
				"Würde das Impftempo unverändert bleiben (*%s* Erstdosen in den letzten 2 Wochen), hätten...\n"
				+ "... *%s* *60%%* der Bevölkerung mindestens eine Impfung erhalten.\n"
				+ "... *%s* *80%%* der Bevölkerung mindestens eine Impfung erhalten.\n\n",
				dailyFirstShotsMovingAverage, herdImmunitydateFirstShot60, 
				herdImmunityDateFirstShot80);
	}

	private String getWeekSummary() {
		List<VaccinationDataRow> lastWeek = dataInterpreter.getLastWeek();

		StringBuilder sb = new StringBuilder("Zusammenfassung letzte Woche\n");
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

			sb.append(String.format("%s - *%s*\n", calendarWeek, calendarWeekDoses));
		});

		return String.format("Wochenübersicht\n%s\n", sb.toString());
	}

	private String getFooter() {
		return new MessageFooter(isSubbed, subCount).getTextAsMarkdown();
	}

}
