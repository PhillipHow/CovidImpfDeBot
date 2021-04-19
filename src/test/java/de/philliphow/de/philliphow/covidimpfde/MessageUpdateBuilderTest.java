package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import de.philliphow.covidimpfde.logic.UpdateMessageBuilder;

public class MessageUpdateBuilderTest {

	/**
	 * Testing with LocalDates as data row elements
	 */
	public UpdateMessageBuilder<LocalDate> example;

	@BeforeEach
	public void setUp() {
		example = new UpdateMessageBuilder<LocalDate>() {

			@Override
			public String getMessageText(List<LocalDate> allDataRows, boolean isSubbed) {
				StringBuilder sb = new StringBuilder();
				sb.append(isSubbed + " ");
				allDataRows.forEach(data -> sb.append(data.toString() + " "));
				return sb.toString();
			}

			@Override
			public LocalDate getDateFor(LocalDate dataRow) {
				return dataRow;
			}
		};
	}

	@Test
	public void throwsExceptionOnBuildWithoutEnoughFields() {

		assertThrows(UnsupportedOperationException.class, () -> example.build());

		setUp();
		example = example.setChatId("chatId");
		assertThrows(UnsupportedOperationException.class, () -> example.build());

		setUp();
		example = example.setContentData(new ArrayList<>());
		assertThrows(UnsupportedOperationException.class, () -> example.build());

	}

	@Test
	public void fieldsAreSetCorrectly() {

		String chatId = "example_chatId";
		boolean isSubbed = true;
		List<LocalDate> data = new ArrayList<>();
		LocalDate element1 = LocalDate.now().minusDays(1);
		LocalDate element2 = LocalDate.now();

		data.add(element1);
		data.add(element2);

		SendMessage message = example.setChatId(chatId).setIsSubbed(isSubbed).setContentData(data).build();

		System.out.println(getWordsOfMessageText(message));
		assertEquals(Boolean.toString(isSubbed), getWordsOfMessageText(message).get(0));
		assertEquals(element1.toString(), getWordsOfMessageText(message).get(1));
		assertEquals(element2.toString(), getWordsOfMessageText(message).get(2));
		assertEquals(chatId, message.getChatId());

	}

	@Test
	public void debugDateIsRespected() {

		String chatId = "example_chatId";
		List<LocalDate> data = new ArrayList<>();
		LocalDate element0 = LocalDate.now().minusDays(2);
		LocalDate element1 = LocalDate.now().minusDays(1);
		LocalDate element2 = LocalDate.now().minusDays(0);

		data.add(element0);
		data.add(element1);
		data.add(element2);

		SendMessage resultMessage = example.setChatId(chatId).setContentData(data)
				.setDebugDate(LocalDate.now().minusDays(1)).build();

		List<String> messageWords = getWordsOfMessageText(resultMessage);

		assertTrue(messageWords.contains(element0.toString()));
		assertTrue(messageWords.contains(element1.toString()));
		assertFalse(messageWords.contains(element2.toString()));

	}

	@Test
	public void chatIdCanAlsoBeSuppliedAsLong() {

		long chatId = 12345;
		List<LocalDate> data = new ArrayList<>();

		SendMessage resultMessage = example.setChatId(chatId).setContentData(data).build();

		assertEquals(Long.toString(chatId), resultMessage.getChatId());

	}

	public List<String> getWordsOfMessageText(SendMessage m) {

		Scanner textScanner = new Scanner(m.getText());
		List<String> words = new ArrayList<>();
		while (textScanner.hasNext())
			words.add(textScanner.next());
		textScanner.close();
		return words;

	}

}
