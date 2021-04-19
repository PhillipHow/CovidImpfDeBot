package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.exceptions.SubPersistenceException;
import de.philliphow.covidimpfde.util.SubListPersistence;

public class SubListPersistenceTest {

	SubListPersistence persistence;
	String user1 = "1234";
	String user2 = "5678";
	String user3 = "9123";

	@BeforeEach
	public void setUp() {
		File file = new File(SubListPersistence.PERSISTENCE_FILENAME);

		if (file.exists())
			file.delete();

		this.persistence = new SubListPersistence();
	}

	@AfterAll
	public static void cleanUp() {
		File file = new File(SubListPersistence.PERSISTENCE_FILENAME);
		file.delete();
	}

	@Test
	public void subscribeNoFileExistsBefore() throws SubPersistenceException {

		persistence.subscribe(user1);
		assertTrue(persistence.isSubbed(user1));
		assertEquals(persistence.getAllSubs().size(), 1);
		assertEquals(persistence.getAllSubs().get(0), user1);

	}

	@Test
	public void subscribeWithFileBefore() throws IOException {

		FileWriter fw = new FileWriter(SubListPersistence.PERSISTENCE_FILENAME);
		fw.write("1 2 3");
		fw.close();

		persistence.subscribe(user1);
		assertTrue(persistence.isSubbed(user1));
		assertEquals(persistence.getAllSubs().size(), 4);
	}

	@Test
	public void subscribeReturnsCorrectBoolean() throws SubPersistenceException {

		assertTrue(persistence.subscribe(user1));
		assertFalse(persistence.subscribe(user1));

	}

	@Test
	public void unsubscribeWithNoFileBefore() throws SubPersistenceException {

		persistence.unsubscribe(user1);
		assertFalse(persistence.isSubbed(user1));

	}

	@Test
	public void unsubscribeWithFileBefore() throws IOException {
		FileWriter fw = new FileWriter(SubListPersistence.PERSISTENCE_FILENAME);
		fw.write(user1 + " " + user2);
		fw.close();

		assertTrue(persistence.isSubbed(user1));
		assertTrue(persistence.isSubbed(user2));
		assertFalse(persistence.isSubbed(user3));

		persistence.unsubscribe(user2);
		assertTrue(persistence.isSubbed(user1));
		assertFalse(persistence.isSubbed(user2));
		assertFalse(persistence.isSubbed(user3));

	}

	@Test
	public void unsubscribeReturnsCorrectBoolean() throws IOException {

		assertFalse(persistence.unsubscribe(user1));

		FileWriter fw = new FileWriter(SubListPersistence.PERSISTENCE_FILENAME);
		fw.write(user1);
		fw.close();

		assertTrue(persistence.unsubscribe(user1));

	}

	@Test
	public void testGetAllSubs() throws IOException {

		persistence.subscribe(user1);
		persistence.subscribe(user2);

		List<String> subs = persistence.getAllSubs();

		assertTrue(subs.contains(user1));
		assertTrue(subs.contains(user2));

	}

	@Test
	public void testMultiThreadWritesWork() throws SubPersistenceException {

		List<Thread> threads = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			String chatId = "AKINDALONGCHATIDWITHTHENUMBER" + i;
			boolean shouldBeInAtTheEnd = i % 2 == 0;

			threads.add(new Thread() {

				@Override
				public void run() {
					try {
						persistence.subscribe(chatId);
						assertTrue(persistence.isSubbed(chatId));
						persistence.unsubscribe(chatId);
						assertFalse(persistence.isSubbed(chatId));
						if (shouldBeInAtTheEnd) {
							persistence.subscribe(chatId);
						}

					} catch (SubPersistenceException e) {
						fail(e);
					}

				}

			});
		}

		threads.forEach(t -> t.run());

		for (int i = 0; i < 100; i++) {
			String chatId = "AKINDALONGCHATIDWITHTHENUMBER" + i;
			boolean shouldBeInAtTheEnd = i % 2 == 0;

			assertEquals(persistence.isSubbed(chatId), shouldBeInAtTheEnd);

		}
	}

}
