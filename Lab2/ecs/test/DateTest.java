import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DateTest {

  // TC1
  @Test
  void nextDate_TC1_middleOfJune() {
    assertEquals(new Date(1700, 6, 21), new Date(1700, 6, 20).nextDate());
  }

  // TC2
  @Test
  void nextDate_TC2_middleOfApril() {
    assertEquals(new Date(2005, 4, 16), new Date(2005, 4, 15).nextDate());
  }

  // TC3
  @Test
  void nextDate_TC3_middleOfJuly() {
    assertEquals(new Date(1901, 7, 21), new Date(1901, 7, 20).nextDate());
  }

  // TC4
  @Test
  void nextDate_TC4_middleOfMarch() {
    assertEquals(new Date(3456, 3, 28), new Date(3456, 3, 27).nextDate());
  }

  // TC5
  @Test
  void nextDate_TC5_middleOfFebruaryNonLeap() {
    assertEquals(new Date(1500, 2, 18), new Date(1500, 2, 17).nextDate());
  }

  // TC6
  @Test
  void nextDate_TC6_secondLastDayOfJune() {
    assertEquals(new Date(1700, 6, 30), new Date(1700, 6, 29).nextDate());
  }

  // TC7
  @Test
  void nextDate_TC7_secondLastDayOfNovember() {
    assertEquals(new Date(1800, 11, 30), new Date(1800, 11, 29).nextDate());
  }

  // TC8
  @Test
  void nextDate_TC8_jan29() {
    assertEquals(new Date(3453, 1, 30), new Date(3453, 1, 29).nextDate());
  }

  // TC9
  @Test
  void nextDate_TC9_leapYearFeb29RollsToMarch() {
    assertEquals(new Date(444, 3, 1), new Date(444, 2, 29).nextDate());
  }

  // TC10
  @Test
  void nextDate_TC10_lastDayOfAprilRollsToMay() {
    assertEquals(new Date(2005, 5, 1), new Date(2005, 4, 30).nextDate());
  }

  // TC11
  @Test
  void nextDate_TC11_jan30() {
    assertEquals(new Date(3453, 1, 31), new Date(3453, 1, 30).nextDate());
  }

  // TC12
  @Test
  void nextDate_TC12_march30() {
    assertEquals(new Date(3456, 3, 31), new Date(3456, 3, 30).nextDate());
  }

  // TC13
  @Test
  void nextDate_TC13_lastDayOfJulyRollsToAugust() {
    assertEquals(new Date(1901, 8, 1), new Date(1901, 7, 31).nextDate());
  }

  // TC14
  @Test
  void nextDate_TC14_lastDayOfJanuaryRollsToFebruary() {
    assertEquals(new Date(3453, 2, 1), new Date(3453, 1, 31).nextDate());
  }

  // TC15
  @Test
  void nextDate_TC15_lastDayOfYearRollsToNextYear() {
    assertEquals(new Date(3457, 1, 1), new Date(3456, 12, 31).nextDate());
  }

  // TC16
  @Test
  void nextDate_TC16_feb31ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Date(1500, 2, 31));
  }

  // TC17
  @Test
  void nextDate_TC17_feb29OnNonLeapYearThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Date(1500, 2, 29));
  }

  // TC18
  @Test
  void nextDate_TC18_negativeYearThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Date(-1, 10, 20));
  }

  // TC19
  @Test
  void nextDate_TC19_invalidMonthThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Date(1458, 15, 12));
  }

  // TC20
  @Test
  void nextDate_TC20_negativeDayThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Date(1975, 6, -50));
  }

}
