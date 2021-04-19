package de.philliphow.covidimpfde.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import de.philliphow.covidimpfde.exceptions.SubPersistenceException;

/**
 * Class to provide synchronous methods to read the subscription persistence.
 * 
 * @author PhillipHow
 *
 */
public class SubListPersistence {

	/**
	 * The subscription list file name
	 */
	public final static String PERSISTENCE_FILENAME = "subs";

	/**
	 * Subscribes the user with the given id
	 * 
	 * @param chatId the telegram userId
	 * @return true, if the user has not been subscribed before this method was
	 *         called, false otherwise
	 * @throws SubPersistenceException if the subscription file could not be written
	 */
	public boolean subscribe(String chatId) throws SubPersistenceException {

		try {
			List<String> subs = readList();

			if (!subs.contains(chatId)) {
				subs.add(chatId);
				writeList(subs);
				return true;
			} else {
				return false;
			}
		} catch (IOException error) {
			throw new SubPersistenceException(error);
		}
	}

	/**
	 * Unsubscribes the user with the given id
	 * 
	 * @param chatId the telegram userId
	 * @return true, if the user has been subscribed before this method was called,
	 *         false otherwise
	 * @throws SubPersistenceException if the subscription file could not be written
	 */
	public boolean unsubscribe(String chatId) throws SubPersistenceException {

		if (isSubbed(chatId)) {

			try {
				writeList(getAllSubs().stream().filter(sub -> !sub.equals(chatId)).collect(Collectors.toList()));
				return true;
			} catch (IOException exception) {
				throw new SubPersistenceException(exception);
			}

		} else {
			return false;
		}

	}

	/**
	 * Checks if the given user is subscribed
	 * 
	 * @param chatId the telegram userId
	 * @return true if the user is subscribed, false if not
	 * @throws SubPersistenceException if the sub file could not be read
	 */
	public boolean isSubbed(String chatId) throws SubPersistenceException {
		try {
			return readList().contains(chatId);
		} catch (IOException e) {
			throw new SubPersistenceException(e);
		}
	}

	/**
	 * Returns the userIds for all users currently subscribed
	 * 
	 * @return list of userIds
	 * @throws SubPersistenceException if the list could not be read
	 */
	public List<String> getAllSubs() throws SubPersistenceException {
		try {
			return this.readList();
		} catch (IOException e) {
			throw new SubPersistenceException(e);
		}
	}

	private List<String> readList() throws IOException {
		ensureFilesExists();

		File file = new File(PERSISTENCE_FILENAME);
		Scanner scan = new Scanner(file);
		List<String> list = new ArrayList<>();
		while (scan.hasNext()) {
			list.add(scan.next());
		}
		scan.close();
		return list;
	}

	private void writeList(List<String> subList) throws IOException {

		synchronized (PERSISTENCE_FILENAME) {
			ensureFilesExists();

			FileWriter fw = new FileWriter(PERSISTENCE_FILENAME);
			StringBuilder sb = new StringBuilder();
			subList.forEach(chatId -> sb.append(chatId + " "));
			fw.write(sb.toString());
			fw.close();
		}

	}

	private void ensureFilesExists() throws IOException {
		File file = new File(PERSISTENCE_FILENAME);
		if (!file.exists()) {
			FileWriter fw = new FileWriter(PERSISTENCE_FILENAME);
			fw.write("");
			fw.close();
		}
	}
}
