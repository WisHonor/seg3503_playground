import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DateTest {

  @Test
  void nextDate_tc01() {
    Date today = new Date(1700, 6, 20);
    Date expectedTomorrow = new Date(1700, 6, 21);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc02() {
    Date today = new Date(2005, 4, 15);
    Date expectedTomorrow = new Date(2005, 4, 16);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc03() {
    Date today = new Date(1901, 7, 20);
    Date expectedTomorrow = new Date(1901, 7, 21);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc04() {
    Date today = new Date(3456, 3, 27);
    Date expectedTomorrow = new Date(3456, 3, 28);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc05() {
    Date today = new Date(1500, 2, 17);
    Date expectedTomorrow = new Date(1500, 2, 18);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc06() {
    Date today = new Date(1700, 6, 29);
    Date expectedTomorrow = new Date(1700, 6, 30);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc07() {
    Date today = new Date(1800, 11, 29);
    Date expectedTomorrow = new Date(1800, 11, 30);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc08() {
    Date today = new Date(3453, 1, 29);
    Date expectedTomorrow = new Date(3453, 1, 30);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc09() {
    Date today = new Date(444, 2, 29);
    Date expectedTomorrow = new Date(444, 3, 1);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc10() {
    Date today = new Date(2005, 4, 30);
    Date expectedTomorrow = new Date(2005, 5, 1);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc11() {
    Date today = new Date(3453, 1, 30);
    Date expectedTomorrow = new Date(3453, 1, 31);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc12() {
    Date today = new Date(3456, 3, 30);
    Date expectedTomorrow = new Date(3456, 3, 31);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc13() {
    Date today = new Date(1901, 7, 31);
    Date expectedTomorrow = new Date(1901, 8, 1);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc14() {
    Date today = new Date(3453, 1, 31);
    Date expectedTomorrow = new Date(3453, 2, 1);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_tc15() {
    Date today = new Date(3456, 12, 31);
    Date expectedTomorrow = new Date(3457, 1, 1);
    assertEquals(expectedTomorrow, today.nextDate());
  }

  @Test
  void nextDate_invalid_tc16() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(1500, 2, 31)
    );
  }

  @Test
  void nextDate_invalid_tc17() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(1500, 2, 29)
    );
  }

  @Test
  void nextDate_invalid_tc18() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(-1, 10, 20)
    );
  }

  @Test
  void nextDate_invalid_tc19() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(1458, 15, 12)
    );
  }

  @Test
  void nextDate_invalid_tc20() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(1975, 6, -50)
    );
  }

  // --- Additional tests for improved coverage ---

  // Covers: day > 31 branch in setDay
  @Test
  void setDay_dayOver31_throwsException() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(2000, 1, 32)
    );
  }

  // Covers: Feb on leap year, day > 29 branch in setDay
  @Test
  void setDay_feb29LeapYear_valid() {
    Date d = new Date(2000, 2, 29); // 2000 is a leap year (div by 400)
    assertEquals(29, d.getDay());
  }

  // Covers: Feb on leap year, day > 29 throws
  @Test
  void setDay_feb30LeapYear_throwsException() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(2000, 2, 30)
    );
  }

  // Covers: Feb on non-leap year, day == 28 is valid
  @Test
  void setDay_feb28NonLeapYear_valid() {
    Date d = new Date(2001, 2, 28);
    assertEquals(28, d.getDay());
  }

  // Covers: thirty-day month (April=4), day == 30 is valid, day > 30 throws
  @Test
  void setDay_thirtyDayMonth_day30_valid() {
    Date d = new Date(2000, 4, 30);
    assertEquals(30, d.getDay());
  }

  @Test
  void setDay_thirtyDayMonth_day31_throwsException() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(2000, 6, 31)
    );
  }

  // Covers: isLeapYear — century year NOT divisible by 400 (false branch)
  @Test
  void isLeapYear_centuryNotDiv400_false() {
    Date d = new Date(1900, 1, 1); // 1900 % 100 == 0 but 1900 % 400 != 0 => not leap
    assertFalse(d.isLeapYear());
  }

  // Covers: isLeapYear — century year divisible by 400 (true branch)
  @Test
  void isLeapYear_centuryDiv400_true() {
    Date d = new Date(2000, 1, 1); // 2000 % 400 == 0 => leap
    assertTrue(d.isLeapYear());
  }

  // Covers: isLeapYear — non-century, divisible by 4 => leap
  @Test
  void isLeapYear_nonCenturyDiv4_true() {
    Date d = new Date(2004, 1, 1);
    assertTrue(d.isLeapYear());
  }

  // Covers: isLeapYear — non-century, not divisible by 4 => not leap
  @Test
  void isLeapYear_nonCenturyNotDiv4_false() {
    Date d = new Date(2001, 1, 1);
    assertFalse(d.isLeapYear());
  }

  // Covers: nextDate on Feb 28 of non-leap year => Mar 1
  @Test
  void nextDate_feb28NonLeap_givesMarFirst() {
    Date today = new Date(2001, 2, 28);
    Date expected = new Date(2001, 3, 1);
    assertEquals(expected, today.nextDate());
  }

  // Covers: nextDate on Feb 28 of leap year => Feb 29
  @Test
  void nextDate_feb28Leap_givesFeb29() {
    Date today = new Date(2000, 2, 28);
    Date expected = new Date(2000, 2, 29);
    assertEquals(expected, today.nextDate());
  }

  // Covers: isThirtyDayMonth false for month 9 (September)
  @Test
  void nextDate_sep30_givesOctFirst() {
    Date today = new Date(2000, 9, 30);
    Date expected = new Date(2000, 10, 1);
    assertEquals(expected, today.nextDate());
  }

  // Covers: isThirtyDayMonth true for month 11 (November)
  @Test
  void nextDate_nov30_givesDecFirst() {
    Date today = new Date(2000, 11, 30);
    Date expected = new Date(2000, 12, 1);
    assertEquals(expected, today.nextDate());
  }

  // Covers: toString method
  @Test
  void toString_formatsCorrectly() {
    Date d = new Date(2024, 3, 5);
    assertEquals("2024/March/5", d.toString());
  }

  // Covers: equals — same object values => true
  @Test
  void equals_sameDateValues_returnsTrue() {
    Date d1 = new Date(2024, 6, 15);
    Date d2 = new Date(2024, 6, 15);
    assertTrue(d1.equals(d2));
  }

  // Covers: equals — different date => false
  @Test
  void equals_differentDate_returnsFalse() {
    Date d1 = new Date(2024, 6, 15);
    Date d2 = new Date(2024, 6, 16);
    assertFalse(d1.equals(d2));
  }

  // Covers: equals — non-Date object => false
  @Test
  void equals_nonDateObject_returnsFalse() {
    Date d = new Date(2024, 6, 15);
    assertFalse(d.equals("not a date"));
  }

  // Covers: year == 0 (boundary — valid since year >= 0)
  @Test
  void setYear_yearZero_valid() {
    Date d = new Date(0, 1, 1);
    assertEquals(0, d.getYear());
  }

  // Covers: month == 1 boundary
  @Test
  void setMonth_month1_valid() {
    Date d = new Date(2000, 1, 1);
    assertEquals(1, d.getMonth());
  }

  // Covers: month == 12 boundary
  @Test
  void setMonth_month12_valid() {
    Date d = new Date(2000, 12, 31);
    assertEquals(12, d.getMonth());
  }

  // Covers: month == 0 invalid (lower boundary)
  @Test
  void setMonth_month0_throwsException() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Date(2000, 0, 1)
    );
  }

}
