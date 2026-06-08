import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DateNextDateExceptionTest {

  static Stream<Arguments> invalidDateCases() {
    return Stream.of(
      Arguments.of(1500,  2,  31),  // TC16: Feb 31 does not exist
      Arguments.of(1500,  2,  29),  // TC17: Feb 29 on non-leap year (1500 not divisible by 400)
      Arguments.of(  -1, 10,  20),  // TC18: negative year
      Arguments.of(1458, 15,  12),  // TC19: month 15 out of range
      Arguments.of(1975,  6, -50)   // TC20: negative day
    );
  }

  @ParameterizedTest
  @MethodSource("invalidDateCases")
  void nextDate_invalidInput_throwsIllegalArgumentException(int year, int month, int day) {
    assertThrows(IllegalArgumentException.class, () -> new Date(year, month, day));
  }

}
