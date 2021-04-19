package de.philliphow.covidimpfde.core;

import java.util.Optional;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import de.philliphow.covidimpfde.exceptions.ImpfDashboardApiException;
import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;

/**
 * Reads in the console arguments and starts the Bot
 * See README for instructions on how to start bot from console.
 * 
 * @author PhillipHow
 *
 */
public class Starter {

	public static void main(String[] args) throws ImpfDashboardApiException {

		if (!argsValid(args)) {
			System.out
					.println("Usage: java -jar CovidImpfDEBot (prod | debug) <BotUsername> <BotToken> [<AdminChatId>]");
		} else {

			boolean debugMode = args[0].equals("debug");
			String botUsername = args[1];
			String botToken = args[2];
			Optional<String> adminChatId;

			if (args.length == 4) {
				adminChatId = Optional.of(args[3]);
			} else {
				adminChatId = Optional.empty();
			}

			CovidImpfDeBot bot = new CovidImpfDeBot(botToken, botUsername, adminChatId, debugMode);

			try {
				bot.startAndRunUntilInterrupted();
			} catch (ImpfDashboardApiException exception) {
				Logger.error(exception, "Error getting intial vaccination data!");
			} catch (TelegramApiException exception) {
				Logger.error(exception, "Error creating bot! Username or token might be incorrect.");
			}

		}

	}

	private static boolean argsValid(String[] args) {

		if (args.length != 3 && args.length != 4)
			return false;
		if (!args[0].equals("prod") && !args[0].equals("debug"))
			return false;
		return true;

	}

}
