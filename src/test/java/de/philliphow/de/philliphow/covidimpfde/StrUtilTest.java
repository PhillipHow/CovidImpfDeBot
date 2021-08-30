package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.strings.StrUtil;

public class StrUtilTest {

	@Test
	public void smallNumberFormattedCorrectly() {
		assertEquals("123", StrUtil.number(123));
	}

	@Test
	public void thousendNumberReturnsCorrectly() {
		assertEquals("1K", StrUtil.number(1234));
	}

	@Test
	public void millionNumberFormattedCorrectly() {
		assertEquals("1M", StrUtil.number(1000000));
		assertEquals("1,1M", StrUtil.number(1100000));
		assertEquals("1,12M", StrUtil.number(1120000));
		assertEquals("1,12M", StrUtil.number(1124000));
		assertEquals("1,13M", StrUtil.number(1126000));
	}
	
	@Test 
	public void negativeNumbersFormattedCorrectly() {
		assertEquals("-123", StrUtil.number(-123));
		assertEquals("-1K", StrUtil.number(-1234));
		assertEquals("-1,12M", StrUtil.number(-1120000));
	}

	@Test
	public void positiveDifferenceHasPlus() {
		assertEquals("+4", StrUtil.difference(4));
	}

	@Test
	public void negativeDifferenceHasMinus() {
		assertEquals("-4", StrUtil.difference(-4));
	}

	@Test
	public void zeroDifferenceHasPlus() {
		assertEquals("+0", StrUtil.difference(0));
	}

	@Test
	public void percentFormattedCorrectly() {
		assertEquals("12,34%", StrUtil.percent(0.1234));
	}

	@Test
	public void farAwayDatesFormattedCorrectly() {
		assertEquals("am 3. Februar 2010", StrUtil.date(LocalDate.of(2010, 2, 3)));
		assertEquals("am 23. Januar 2050", StrUtil.date(LocalDate.of(2050, 1, 23)));
	}

	@Test
	public void closeDaysFormattedCorrectly() {
		assertEquals("heute", StrUtil.date(LocalDate.now().plusDays(0)));
		assertEquals("morgen", StrUtil.date(LocalDate.now().plusDays(1)));
		assertEquals("übermorgen", StrUtil.date(LocalDate.now().plusDays(2)));
	}

	@Test
	public void aFewDaysAgoFormattedCorrectly() {
		assertEquals("vor 10 Tagen", StrUtil.date(LocalDate.now().minusDays(10)));
		assertEquals("vor 14 Tagen", StrUtil.date(LocalDate.now().minusDays(14)));
		assertNotEquals("vor 15 Tagen", StrUtil.date(LocalDate.now().minusDays(15)));
	}

	@Test
	public void calendarWeekFormattedCorrectly() {
		assertEquals("KW9", StrUtil.calendarWeek(9));
	}

	@Test
	public void weekDayShort() {
		assertEquals("Di", StrUtil.weekDayShort(LocalDate.of(2021, 3, 2)));
		assertEquals("Do", StrUtil.weekDayShort(LocalDate.of(2021, 3, 4)));
	}

	@Test
	public void capitalizedFormattingWorks() {
		assertEquals("Hello you", StrUtil.capitalized("hello you"));
	}

	@Test
	public void placementStringFormattedCorrectly() {
		assertEquals("besten", StrUtil.placementStr(1));
		assertEquals("zweitbesten", StrUtil.placementStr(2));
		assertEquals("drittbesten", StrUtil.placementStr(3));
		assertEquals("4.-besten", StrUtil.placementStr(4));
	}

}
