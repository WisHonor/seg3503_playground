import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DateNextDateOkTest {

  static Stream<Arguments> validDateCases() {
    return Stream.of(
      Arguments.of(new Date(1700, 6, 20),  new Date(1700, 6, 21)),   // TC1
      Arguments.of(new Date(2005, 4, 15),  new Date(2005, 4, 16)),   // TC2
      Arguments.of(new Date(1901, 7, 20),  new Date(1901, 7, 21)),   // TC3
      Arguments.of(new Date(3456, 3, 27),  new Date(3456, 3, 28)),   // TC4
      Arguments.of(new Date(1500, 2, 17),  new Date(1500, 2, 18)),   // TC5
      Arguments.of(new Date(1700, 6, 29),  new Date(1700, 6, 30)),   // TC6
      Arguments.of(new Date(1800, 11, 29), new Date(1800, 11, 30)),  // TC7
      Arguments.of(new Date(3453, 1, 29),  new Date(3453, 1, 30)),   // TC8
      Arguments.of(new Date(444,  2, 29),  new Date(444,  3, 1)),    // TC9
      Arguments.of(new Date(2005, 4, 30),  new Date(2005, 5, 1)),    // TC10
      Arguments.of(new Date(3453, 1, 30),  new Date(3453, 1, 31)),   // TC11
      Arguments.of(new Date(3456, 3, 30),  new Date(3456, 3, 31)),   // TC12
      Arguments.of(new Date(1901, 7, 31),  new Date(1901, 8, 1)),    // TC13
      Arguments.of(new Date(3453, 1, 31),  new Date(3453, 2, 1)),    // TC14
      Arguments.of(new Date(3456, 12, 31), new Date(3457, 1, 1))     // TC15
    );
  }

  @ParameterizedTest
  @MethodSource("validDateCases")
  void nextDate_returnsExpectedDate(Date input, Date expected) {
    assertEquals(expected, input.nextDate());
  }

}
