package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;

public class VaccinationDataRowTest {

	VaccinationDataRow example;

	@BeforeEach
	public void setUp() {
		example = new VaccinationDataRow(
				"2021-04-14	20049936	738501	662711	75790	14854474	1062422	4133040	14773908	5276028	0.178	0.063	7644926	5826686	1124640	1761928	4910112	4346229	944820	1001588	2734814	1480457	179820	760340",
				"date	dosen_kumulativ	dosen_differenz_zum_vortag	dosen_erst_differenz_zum_vortag	dosen_zweit_differenz_zum_vortag	dosen_biontech_kumulativ	dosen_moderna_kumulativ	dosen_astra_kumulativ	personen_erst_kumulativ	personen_voll_kumulativ	impf_quote_erst	impf_quote_voll	indikation_alter_dosen	indikation_beruf_dosen	indikation_medizinisch_dosen	indikation_pflegeheim_dosen	indikation_alter_erst	indikation_beruf_erst	indikation_medizinisch_erst	indikation_pflegeheim_erst	indikation_alter_voll	indikation_beruf_voll	indikation_medizinisch_voll	indikation_pflegeheim_voll");

	}

	@Test
	public void dateIsCorrect() {
		assertEquals(LocalDate.of(2021, 4, 14), example.getDate());
	}

	@Test
	public void firstShotsTodayIsCorrect() {
		assertEquals(662711, example.getFirstShotsToday());
	}

	@Test
	public void personsVaccinatedFullIsCorrect() {
		assertEquals(5276028, example.getPersonsVaccinatedFull());
	}

	@Test
	public void personsVaccinatedOnceIsCorrect() {
		assertEquals(14773908, example.getPersonsVaccinatedOnce());
	}

	@Test
	public void populationQuotaFullIsCorrect() {
		assertEquals(0.063, example.getPopulationQuotaVaccinatedFull());
	}

	@Test
	public void populationQuotaOnceIsCorrect() {
		assertEquals(0.178, example.getPopulationQuotaVaccinatedOnce());
	}

	@Test
	public void secondShotsTodayIsCorrect() {
		assertEquals(75790, example.getSecondShotsToday());
	}

	@Test
	public void ShotsTodayIsCorrect() {
		assertEquals(738501, example.getShotsToday());
	}

	@Test
	public void totalShotsIsCorrect() {
		assertEquals(20049936, example.getTotalShots());
	}

	@Test
	public void totalShotsByVaccineIsCorrect() {
		Vaccine biontech = Vaccine.byDeliveryDataIdentifier("comirnaty");
		Vaccine astra = Vaccine.byDeliveryDataIdentifier("astra");
		Vaccine moderna = Vaccine.byDeliveryDataIdentifier("moderna");

		System.out.println(astra.getVaccinationsDataField().getFieldName());
		
		assertEquals(14854474, example.getTotalShotsByVaccine(biontech));
		assertEquals(4133040, example.getTotalShotsByVaccine(astra));
		assertEquals(1062422, example.getTotalShotsByVaccine(moderna));

	}

	@Test
	public void unequalLengthOfDataAndHeaderRowThrowsException() {

		Assertions.assertThrows(Exception.class, () -> {

			new VaccinationDataRow(
					"20049936	738501	662711	75790	14854474	1062422	4133040	14773908	5276028	0.178	0.063	7644926	5826686	1124640	1761928	4910112	4346229	944820	1001588	2734814	1480457	179820	760340",
					"date	dosen_kumulativ	dosen_differenz_zum_vortag	dosen_erst_differenz_zum_vortag	dosen_zweit_differenz_zum_vortag	dosen_biontech_kumulativ	dosen_moderna_kumulativ	dosen_astra_kumulativ	personen_erst_kumulativ	personen_voll_kumulativ	impf_quote_erst	impf_quote_voll	indikation_alter_dosen	indikation_beruf_dosen	indikation_medizinisch_dosen	indikation_pflegeheim_dosen	indikation_alter_erst	indikation_beruf_erst	indikation_medizinisch_erst	indikation_pflegeheim_erst	indikation_alter_voll	indikation_beruf_voll	indikation_medizinisch_voll	indikation_pflegeheim_voll");

		});

	}

	@Test
	public void newFieldDoesNotBreakConstruction() {
		VaccinationDataRow exampleWithNewField = new VaccinationDataRow(
				"2021-04-14	NEWFIELD	20049936	738501	662711	75790	14854474	1062422	4133040	14773908	5276028	0.178	0.063	7644926	5826686	1124640	1761928	4910112	4346229	944820	1001588	2734814	1480457	179820	760340",
				"date	NEWFIELDVALUE	dosen_kumulativ	dosen_differenz_zum_vortag	dosen_erst_differenz_zum_vortag	dosen_zweit_differenz_zum_vortag	dosen_biontech_kumulativ	dosen_moderna_kumulativ	dosen_astra_kumulativ	personen_erst_kumulativ	personen_voll_kumulativ	impf_quote_erst	impf_quote_voll	indikation_alter_dosen	indikation_beruf_dosen	indikation_medizinisch_dosen	indikation_pflegeheim_dosen	indikation_alter_erst	indikation_beruf_erst	indikation_medizinisch_erst	indikation_pflegeheim_erst	indikation_alter_voll	indikation_beruf_voll	indikation_medizinisch_voll	indikation_pflegeheim_voll");
		assertEquals(example, exampleWithNewField);
	}

	@Test
	public void equalsWork() {
		assertTrue(example.equals(example));
	}

}
