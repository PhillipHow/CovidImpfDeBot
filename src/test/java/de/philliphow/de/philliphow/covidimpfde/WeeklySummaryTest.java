package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.api.models.WeeklySummary;

public class WeeklySummaryTest {

	List<WeeklySummary> example;
	List<VaccinationDataRow> dataRowExample;

	@BeforeEach
	public void setUp() {
		generateTestVaccinationData();
		example = WeeklySummary.generateFrom(dataRowExample);
	}

	private void generateTestVaccinationData() {
		dataRowExample = new ArrayList<>();

		// KW 14
		dataRowExample.add(new VaccinationDataRow("2021-04-10 50 25", "date dosen_differenz_zum_vortag dosen_erst_differenz_zum_vortag"));
		dataRowExample.add(new VaccinationDataRow("2021-04-11 100 50", "date dosen_differenz_zum_vortag dosen_erst_differenz_zum_vortag"));
		// KW 15
		dataRowExample.add(new VaccinationDataRow("2021-04-12 200 100", "date dosen_differenz_zum_vortag dosen_erst_differenz_zum_vortag"));
		dataRowExample.add(new VaccinationDataRow("2021-04-13 300 150", "date dosen_differenz_zum_vortag dosen_erst_differenz_zum_vortag"));
		dataRowExample.add(new VaccinationDataRow("2021-04-14 400 200", "date dosen_differenz_zum_vortag dosen_erst_differenz_zum_vortag"));
	}

	@Test
	public void weeklySummarysIsCorrect() {

		WeeklySummary kw14_2021 = example.get(0);
		assertEquals(14, kw14_2021.getCalendarWeekNumber());
		assertEquals(50 + 100, kw14_2021.getTotalDoses());
		assertEquals(25 + 50, kw14_2021.getTotalFirstDoses());
		assertEquals(2021, kw14_2021.getYear());
		assertEquals(LocalDate.of(2021, 4, 5), kw14_2021.getWeekdayMonday());

		WeeklySummary kw15_2021 = example.get(1);
		assertEquals(15, kw15_2021.getCalendarWeekNumber());
		assertEquals(200 + 300 + 400, kw15_2021.getTotalDoses());
		assertEquals(100 + 150 + 200, kw15_2021.getTotalFirstDoses());
		assertEquals(2021, kw15_2021.getYear());
		assertEquals(LocalDate.of(2021, 4, 12), kw15_2021.getWeekdayMonday());
	}

}
