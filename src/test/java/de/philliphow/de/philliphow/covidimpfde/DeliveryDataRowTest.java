package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;

class DeliveryDataRowTest {

	DeliveryDataRow example;
	DeliveryDataRow example2;

	DeliveryDataRow differentFederalDeliveryExample1;
	DeliveryDataRow differentFederalDeliveryExample2;

	@BeforeEach
	public void setUp() {
		example = new DeliveryDataRow("2020-12-26	comirnaty	DE-SL	9750", "date	impfstoff	region	dosen");
		example2 = new DeliveryDataRow("2020-12-26	comirnaty	DE-BW	11500", "date	impfstoff	region	dosen");
		// same vaccine, but one week later
		differentFederalDeliveryExample1 = new DeliveryDataRow("2020-01-07	comirnaty	DE-SH	1300",
				"date	impfstoff	region	dosen");
		// same week, but different vaccine
		differentFederalDeliveryExample2 = new DeliveryDataRow("2021-12-27	astra	DE-NI	1550",
				"date	impfstoff	region	dosen");
	}

	@Test
	public void dateIsCorrect() {
		// we need the monday for 26/12 here, hence 21/12
		assertEquals(LocalDate.of(2020, 12, 21), example.getCalendarWeekMonday());
	}

	@Test
	public void vaccineIdentifierIsCorrect() {
		assertEquals("comirnaty", example.getVaccineIdentifier());
	}

	@Test
	public void vaccineIsCorrect() {
		Vaccine biontech = Vaccine.getAll().stream().filter(v -> v.getDeliveryDataIdentifier().equals("comirnaty"))
				.findFirst().get();
		assertEquals(biontech, example.getVaccine());
	}

	@Test
	public void dosesAreCorrect() {
		assertEquals(9750, example.getDoses());
	}

	@Test
	public void regionIsCorrect() {
		assertEquals("DE-SL", example.getRegion());
	}

	@Test
	public void constructionByValuesWorks() {
		DeliveryDataRow example2 = new DeliveryDataRow(LocalDate.of(2020, 12, 26), "comirnaty", "DE-SL", 9750);
		assertEquals(example, example2);
	}

	@Test
	public void constructionFromBrokenDataFails() {
		String headerRow = "date	impfstoff	region	dosen";
		String dataRow = "2020-12-26	comirnaty	DE-SL";

		Assertions.assertThrows(Exception.class, () -> {
			new DeliveryDataRow(dataRow, headerRow);
		});
	}

	@Test
	public void newFieldDoesNotBreakConstruction() {
		String headerRow = "date	newField	impfstoff	region	dosen";
		String dataRow = "2020-12-26	newFieldValue	comirnaty	DE-SL 9750";
		assertEquals(example, new DeliveryDataRow(dataRow, headerRow));
	}

	@Test
	public void equalsWorks() {
		assertTrue(example.equals(example));
	}

	@Test
	public void combiningPartsIsWorking() {

		DeliveryDataRow combined = DeliveryDataRow.combineDeliveryParts(example, example2);
		assertEquals(combined.getCalendarWeek(), example.getCalendarWeek());
		assertEquals(combined.getCalendarWeekMonday(), example.getCalendarWeekMonday());
		assertEquals(combined.getDoses(), example.getDoses() + example2.getDoses());
		assertEquals(combined.getVaccine(), example.getVaccine());
	}

	@Test
	public void combiningDifferentPartsFails() {

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DeliveryDataRow.combineDeliveryParts(example, differentFederalDeliveryExample1);
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DeliveryDataRow.combineDeliveryParts(example, differentFederalDeliveryExample2);
		});
		
	}

}
