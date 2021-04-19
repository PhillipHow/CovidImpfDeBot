package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.DeliveryApiManager;
import de.philliphow.covidimpfde.api.VaccinationsApiManager;
import de.philliphow.covidimpfde.logic.DeliveryUpdateBuilder;
import de.philliphow.covidimpfde.logic.VaccinationUpdateBuilder;

public class ApiTest {

	@Test
	public void deliveryApiResultCanBeBuildToMessage() throws IOException {

		DeliveryApiManager.getInstance(false).getNewDataIfNecessary();
		assertFalse(DeliveryApiManager.getInstance(false).getCurrentData().isEmpty());
		assertDoesNotThrow(() -> {
			new DeliveryUpdateBuilder().setChatId("aChatId")
					.setContentData(DeliveryApiManager.getInstance(false).getCurrentData()).build();
		});

	}

	@Test
	public void vaccinationApiResultCanBeBuildToMessage() throws IOException {

		VaccinationsApiManager.getInstance(false).getNewDataIfNecessary();
		assertFalse(VaccinationsApiManager.getInstance(false).getCurrentData().isEmpty());
		assertDoesNotThrow(() -> {
			new VaccinationUpdateBuilder().setChatId("aChatId")
					.setContentData(VaccinationsApiManager.getInstance(false).getCurrentData()).build();
		});
	}

}
