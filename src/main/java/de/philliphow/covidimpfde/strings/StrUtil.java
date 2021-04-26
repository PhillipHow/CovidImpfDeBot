package de.philliphow.covidimpfde.strings;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;

/**
 * Util class to get beautifully formatted Strings
 * 
 * @author PhillipHow
 *
 */
public class StrUtil {

	/**
	 * Formats numbers to be readable on first glance, for example 23,5K
	 * 
	 * @param num
	 * @return the formatted number
	 */
	public static String number(int num) {

		if (num < 1000)
			return formatWithDecimals(0).format(1.0 * num);

		if (num < 1000000)
			return formatWithDecimals(0).format(1.0 * num / 1000) + "K";

		return formatWithDecimals(2).format(1.0 * num / 1000000) + "M";
	}

	/**
	 * Formats a number that is a difference value, for example +5 or -4
	 * 
	 * @param deltaNum
	 * @return the formatted number
	 */
	public static String difference(int deltaNum) {
		if (deltaNum >= 0)
			return "+" + number(Math.abs(deltaNum));
		else
			return "-" + number(Math.abs(deltaNum));

	}

	/**
	 * Formats a number that indicates a share as percent String, eg percent(0.521)
	 * returns as "52,10%"
	 * 
	 * @param num, 1 means 100%
	 * @return the formatted String
	 */
	public static String percent(double num) {
		NumberFormat formatter = NumberFormat.getInstance(Locale.GERMAN);
		formatter.setGroupingUsed(true);
		formatter.setMaximumFractionDigits(2);

		return formatter.format(num * 100) + "%";
	}

	/**
	 * Formats a date. If the date is close, Strings like "morgen" (tomorrow) or
	 * "vor 5 Tagen" (5 days ago) might be used.
	 * 
	 * @param date
	 * @return the date string
	 */
	public static String date(LocalDate date) {
		LocalDate then = date;
		LocalDate now = LocalDate.now();

		long dayDifference = ChronoUnit.DAYS.between(now, then);

		if (dayDifference == 2)
			return "übermorgen";
		if (dayDifference == 1)
			return "morgen";
		if (dayDifference == 0)
			return "heute";
		if (dayDifference == -1)
			return "gestern";
		if (dayDifference == -2)
			return "vorgestern";
		if (dayDifference < -2 && dayDifference >= -14)
			return "vor " + -dayDifference + " Tagen";

		DateTimeFormatter format;

		if (then.getYear() == now.getYear())
			format = DateTimeFormatter.ofPattern("d. MMMM", Locale.GERMAN);
		else
			format = DateTimeFormatter.ofPattern("d. MMMM YYYY", Locale.GERMAN);

		return "am " + then.format(format);
	}
	
	/**
	 * @param oneDayOfTheWeek an arbitrary day of the week
	 * @return a string like "2 weeks ago" or "the week of the 4.5."
	 */
	public static String week(LocalDate oneDayOfTheWeek) {
		
		LocalDate thisWeekMonday = DeliveryDataRow.getMondayFor(LocalDate.now());
		LocalDate givenWeekMonday = DeliveryDataRow.getMondayFor(oneDayOfTheWeek);
		
		long dayDifference = givenWeekMonday.until(thisWeekMonday, ChronoUnit.DAYS);
		assert ((dayDifference % 7) == 0);
	

	
		if (dayDifference == 0)
			return "diese Woche";
		if (dayDifference == 7)
			return "letzte Woche";
		if (dayDifference <= 7*6) {
			return  "vor " + (dayDifference/7) + " Wochen";
		}
		
		
		return "in der Woche vom " + givenWeekMonday.get(ChronoField.ALIGNED_WEEK_OF_MONTH) + "." + 
								givenWeekMonday.get(ChronoField.MONTH_OF_YEAR) + ".";
	}

	public static String calendarWeek(int weekNumber) {
		return String.format("KW%s", weekNumber);
	}

	/**
	 * @param date
	 * @return the day of the week in short form
	 */
	public static String weekDayShort(LocalDate date) {
		final DateTimeFormatter format = DateTimeFormatter.ofPattern("E", Locale.GERMAN);
		return date.format(format);
	}

	/**
	 * @param str
	 * @return str with the first later in upper case
	 */
	public static String capitalized(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * @param placement
	 * @return "best", "second best", etc
	 */
	public static String placementStr(int placement) {

		if (placement == 1) {
			return "besten";
		} else if (placement == 2) {
			return "zweitbesten";
		} else if (placement == 3) {
			return "drittbesten";
		} else {
			return placement + ".-besten";
		}

	}

	private static NumberFormat formatWithDecimals(int decimals) {
		NumberFormat formatter = NumberFormat.getInstance(Locale.GERMAN);
		formatter.setGroupingUsed(true);
		formatter.setMaximumFractionDigits(decimals);
		return formatter;
	}

}
