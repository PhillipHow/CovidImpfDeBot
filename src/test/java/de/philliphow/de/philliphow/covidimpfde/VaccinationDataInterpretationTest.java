package de.philliphow.de.philliphow.covidimpfde;

import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.DATE;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.PERSONS_TOTAL_FIRST;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.PERSONS_TOTAL_SECOND;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.POPULATION_QUOTA_FIRST_SHOT;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.POPULATION_QUOTA_SECOND_SHOT;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.SHOTS_TODAY;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.SHOTS_TODAY_FIRST;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.SHOTS_TOTAL;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.SHOTS_TOTAL_ASTRA;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.SHOTS_TOTAL_BIONTECH;
import static de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField.SHOTS_TOTAL_MODERNA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.api.models.WeeklySummary;
import de.philliphow.covidimpfde.logic.VaccinationDataInterpretation;

public class VaccinationDataInterpretationTest {

	VaccinationDataInterpretation interpretation;

	final double EPSILON = 0.0000001;
	final Vaccine astra = Vaccine.byDeliveryDataIdentifier("astra");
	final Vaccine biontech = Vaccine.byDeliveryDataIdentifier("comirnaty");
	final Vaccine moderna = Vaccine.byDeliveryDataIdentifier("moderna");

	@BeforeEach
	public void setUp() {
		interpretation = null;
	}

	@Test
	public void getLatestUpdateWorks() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TOTAL, 50).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TOTAL, 100).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TOTAL, 150).get());

		assertEquals(daysAgo(1), interpretation.getLatestUpdate().getDate());
		assertEquals(150, interpretation.getLatestUpdate().getTotalShots());

	}

	@Test
	public void getLatestUpdateShotsTodayWorks() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY, 50).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY, 100).get());

		assertEquals(100, interpretation.getLatestUpdateShotsToday());

	}

	@Test
	public void getLastestUpdateDiffOneWeekAgoWorks() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(9)).with(SHOTS_TODAY, 0).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(8)).with(SHOTS_TODAY, 50).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(7)).with(SHOTS_TODAY, 150).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(6)).with(SHOTS_TODAY, 200).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(5)).with(SHOTS_TODAY, 250).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(4)).with(SHOTS_TODAY, 350).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY, 400).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY, 450).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TODAY, 500).get());

		assertEquals(500 - 50, interpretation.getLatestUpdateDiffOneWeekAgo());

	}

	@Test
	public void getLastestUpdateDiffOneWeekAgoWorksWithLessThenOneWeekOfData() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(4)).with(SHOTS_TODAY, 350).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY, 400).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY, 450).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TODAY, 500).get());

		assertEquals(500, interpretation.getLatestUpdateDiffOneWeekAgo());

	}

	@Test
	public void getTotalShotsIsCorrect() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TOTAL, 100).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TOTAL, 200).get());

		assertEquals(200, interpretation.getTotalShots());

	}

	@Test
	public void getTotalPersonsVaccinatedOnceIsCorrect() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(PERSONS_TOTAL_FIRST, 100).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(PERSONS_TOTAL_FIRST, 300).get());

		assertEquals(300, interpretation.getTotalPersonsVaccinatedOnce());

	}

	@Test
	public void getTotalPersonsVaccinatedTwiceIsCorrect() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(PERSONS_TOTAL_SECOND, 200).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(PERSONS_TOTAL_SECOND, 500).get());

		assertEquals(500, interpretation.getTotalPersonsVaccintedTwice());

	}

	@Test
	public void getPopulationQuotaVaccinatedOnceIsCorrect() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(POPULATION_QUOTA_FIRST_SHOT, 0.2).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(POPULATION_QUOTA_FIRST_SHOT, 0.3)
						.get());

		assertEquals(0.3, interpretation.getPopulationQuotaVaccinatedOnce(), EPSILON);

	}

	@Test
	public void getPopulationQuotaVaccinatedFullIsCorrect() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(POPULATION_QUOTA_SECOND_SHOT, 0.3)
						.get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(POPULATION_QUOTA_SECOND_SHOT, 0.4)
						.get());

		assertEquals(0.4, interpretation.getPopulationQuotaVaccinatedFull(), EPSILON);

	}

	@Test
	public void getLatestUpdateDiffByVaccineWorks() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TOTAL_BIONTECH, 100)
						.with(SHOTS_TOTAL_ASTRA, 200).with(SHOTS_TOTAL_MODERNA, 300).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TOTAL_BIONTECH, 150)
						.with(SHOTS_TOTAL_ASTRA, 260).with(SHOTS_TOTAL_MODERNA, 290).get());

		assertEquals(150 - 100, interpretation.getLatestUpdateDiffBy(biontech));
		assertEquals(260 - 200, interpretation.getLatestUpdateDiffBy(astra));
		assertEquals(290 - 300, interpretation.getLatestUpdateDiffBy(moderna));

	}

	@Test
	public void getVaccineShareWorks() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TOTAL_BIONTECH, 300)
						.with(SHOTS_TOTAL_ASTRA, 200).with(SHOTS_TOTAL_MODERNA, 500).with(SHOTS_TOTAL, 1000).get());

		assertEquals(0.3, interpretation.getVaccineShare(biontech), EPSILON);
		assertEquals(0.2, interpretation.getVaccineShare(astra), EPSILON);
		assertEquals(0.5, interpretation.getVaccineShare(moderna), EPSILON);

	}

	@Test
	public void testGetDayRanking() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY, 500).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY, 700).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TODAY, 600).get());

		assertEquals(2, interpretation.getLatestUpdateDayRanking());

	}

	@Test
	public void testGetBestDay() {

		VaccinationDataRow bestDay = new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY, 700)
				.get();

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY, 500).get(), bestDay,
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TODAY, 600).get());

		assertEquals(bestDay, interpretation.getBestDay());

	}

	@Test
	public void testLatestUpdateIsSunday() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, LocalDate.of(2021, 04, 17)).get());

		assertEquals(false, interpretation.latestUpdateIsSunday());

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, LocalDate.of(2021, 04, 18)).get());

		assertEquals(true, interpretation.latestUpdateIsSunday());

	}

	@Test
	public void testGetMovingAverage() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(4)).with(SHOTS_TODAY_FIRST, 100).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY_FIRST, 200).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY_FIRST, 300).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TODAY_FIRST, 400).get());

		assertEquals(250, interpretation.getMovingFirstShotAverage(), EPSILON);

	}

	@Test
	public void testMovingAverageOnlyCountsRightNumberOfDays() {

		List<VaccinationDataRow> data = new ArrayList<>();

		int countDaysNumber = VaccinationDataInterpretation.MOVING_AVERAGE_DAY_COUNT;

		// this day should not be counted
		data.add(new VaccinationDataRowMockBuilder().with(DATE, daysAgo(countDaysNumber - 1))
				.with(SHOTS_TODAY_FIRST, 20000).get());

		// all these days should be counted
		for (int i = countDaysNumber; i > 0; i--) {
			data.add(new VaccinationDataRowMockBuilder().with(DATE, daysAgo(i)).with(SHOTS_TODAY_FIRST, 100).get());
		}

		interpretation = new VaccinationDataInterpretation(data);

		assertEquals(100, interpretation.getMovingFirstShotAverage(), EPSILON);

	}

	@Test
	public void herdImmunityEstimateWorks() {

		int vaccinationGoal = (int) (VaccinationDataInterpretation.HERD_IMMUNITY_FACTOR
				* VaccinationDataInterpretation.GERMAN_POPULATION);

		// if everybody got vaccinated today, herd immunity is reached today
		interpretation = getInterpretationFor(new VaccinationDataRowMockBuilder().with(DATE, daysAgo(0))
				.with(SHOTS_TODAY_FIRST, vaccinationGoal).with(PERSONS_TOTAL_FIRST, vaccinationGoal).get());

		assertEquals(daysAgo(0).plusDays(0), interpretation.getHerdImmunityEstimate());

		// if a third got vaccinated today, herd immunity will be reached in two days

		int vaccinationGoalThird = (int) (1.0 / 3 * vaccinationGoal);
		interpretation = getInterpretationFor(new VaccinationDataRowMockBuilder().with(DATE, daysAgo(0))
				.with(SHOTS_TODAY_FIRST, vaccinationGoalThird).with(PERSONS_TOTAL_FIRST, vaccinationGoalThird).get());
		assertEquals(daysAgo(0).plusDays(2), interpretation.getHerdImmunityEstimate());

	}

	@Test
	public void herdImmunityEstimateStillWorksIfReached() {

		int vaccinationGoal = (int) (VaccinationDataInterpretation.HERD_IMMUNITY_FACTOR
				* VaccinationDataInterpretation.GERMAN_POPULATION);

		int halfVaccinationGoal = (int) (0.5 * vaccinationGoal) + 1;

		// goal reached 2 days ago
		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(3)).with(SHOTS_TODAY_FIRST, halfVaccinationGoal)
						.with(PERSONS_TOTAL_FIRST, halfVaccinationGoal).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(2)).with(SHOTS_TODAY_FIRST, halfVaccinationGoal)
						.with(PERSONS_TOTAL_FIRST, halfVaccinationGoal * 2).get(),
				new VaccinationDataRowMockBuilder().with(DATE, daysAgo(1)).with(SHOTS_TODAY_FIRST, halfVaccinationGoal)
						.with(PERSONS_TOTAL_FIRST, halfVaccinationGoal * 3).get());

		assertEquals(daysAgo(2), interpretation.getHerdImmunityEstimate());
	}

	@Test
	public void lastWeekIsCorrect() {

		List<VaccinationDataRow> data = new ArrayList<>();
		for (int i = 14; i > 0; i--) {
			data.add(new VaccinationDataRowMockBuilder().with(DATE, daysAgo(i)).get());
		}
		interpretation = new VaccinationDataInterpretation(data);

		assertTrue(interpretation.getLastWeek().size() == 7);

		assertTrue(interpretation.getLastWeek().stream()
				.allMatch(row -> row.getDate().isAfter(interpretation.getLatestUpdate().getDate().minusDays(8))));

		assertFalse(interpretation.getLastWeek().stream()
				.anyMatch(row -> row.getDate().isBefore(interpretation.getLatestUpdate().getDate().minusDays(7))));

	}

	@Test
	public void getLastNWeeklySummariesIsCorrect() {

		interpretation = getInterpretationFor(
				new VaccinationDataRowMockBuilder().with(DATE, LocalDate.of(2021, 1, 4)).with(SHOTS_TODAY, 100).get(), // shouldnt
																														// be
																														// in
																														// it
				new VaccinationDataRowMockBuilder().with(DATE, LocalDate.of(2021, 1, 11)).with(SHOTS_TODAY, 200).get(),
				new VaccinationDataRowMockBuilder().with(DATE, LocalDate.of(2021, 1, 12)).with(SHOTS_TODAY, 300).get(),
				new VaccinationDataRowMockBuilder().with(DATE, LocalDate.of(2021, 1, 19)).with(SHOTS_TODAY, 400).get());

		List<WeeklySummary> summaries = interpretation.getLastNWeeklySummarys(2);

		assertEquals(2, summaries.size());

		assertEquals(LocalDate.of(2021, 1, 18), summaries.get(0).getWeekdayMonday());
		assertEquals(400, summaries.get(0).getTotalDoses());
		assertEquals(LocalDate.of(2021, 1, 11), summaries.get(1).getWeekdayMonday());
		assertEquals(200 + 300, summaries.get(1).getTotalDoses());

	}

	private LocalDate daysAgo(int n) {
		return LocalDate.now().minusDays(n);
	}

	private VaccinationDataInterpretation getInterpretationFor(VaccinationDataRow... rows) {
		return new VaccinationDataInterpretation(Arrays.asList(rows));
	}

	class VaccinationDataRowMockBuilder {

		private final Map<String, String> fields = new HashMap<>();

		public VaccinationDataRowMockBuilder with(VaccinationsDataField field, String value) {
			fields.put(field.getFieldName(), value);
			return this;
		}

		public VaccinationDataRowMockBuilder with(VaccinationsDataField field, LocalDate value) {
			fields.put(field.getFieldName(), value.toString());
			return this;
		}

		public VaccinationDataRowMockBuilder with(VaccinationsDataField field, int value) {
			fields.put(field.getFieldName(), Integer.toString(value));
			return this;
		}

		public VaccinationDataRowMockBuilder with(VaccinationsDataField field, double value) {
			fields.put(field.getFieldName(), Double.toString(value));
			return this;
		}

		public VaccinationDataRow get() {
			StringBuilder headers = new StringBuilder();
			StringBuilder row = new StringBuilder();

			fields.forEach((header, value) -> {
				headers.append(header + " ");
				row.append(value + " ");
			});

			return new VaccinationDataRow(row.toString(), headers.toString());
		}

	}

}
