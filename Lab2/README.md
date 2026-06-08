# SEG3503 Lab 02 — Classes d'équivalence / JUnit Testing

## Overview

This lab covers equivalence class testing and JUnit 5 test development. It is divided into two exercises:

- **Exercise 1** — Manual black-box testing of a user registration web application
- **Exercise 2** — Automated JUnit testing of a `Date.nextDate()` method using both explicit and parameterized test cases

---

## Exercise 1 — Manual Testing of the Registration Application

The registration application is launched with:

```bash
cd Lab2/registration
java --add-opens java.base/java.lang=ALL-UNNAMED -jar user-registration-app-0.1.0.jar
```

> **Note:** The `--add-opens` flag is required because Spring Boot 2.0.x uses CGLIB for class proxying, which needs reflective access to `java.lang.ClassLoader`. This was restricted by the Java module system (JPMS) introduced in Java 9. Without this flag the app fails to start on Java 9+.

The app runs at `http://localhost:8080`.

### Manual Test Cases

| Test Case                         | Input                                                                              | Expected Result                   | Actual Result                     | Verdict |
| --------------------------------- | ---------------------------------------------------------------------------------- | --------------------------------- | --------------------------------- | ------- |
| TC-R1: Valid registration         | Username: `john_doe`, Email: `john@example.com`, Age: `25`, Postal Code: `K1A 0A1` | Account created successfully      | Account created successfully      | Success |
| TC-R2: Empty username             | Username: _(empty)_, Email: `john@example.com`, Age: `25`, Postal Code: `K1A 0A1`  | Error: username is required       | Error: username is required       | Success |
| TC-R3: Invalid email format       | Username: `jane`, Email: `not-an-email`, Age: `30`, Postal Code: `K1A 0A1`         | Error: invalid email address      | Error: invalid email address      | Success |
| TC-R4: Invalid age (negative)     | Username: `alice`, Email: `alice@test.com`, Age: `-5`, Postal Code: `K2B 1C3`      | Error: age must be positive       | Error: age must be positive       | Success |
| TC-R5: Invalid age (too large)    | Username: `bob`, Email: `bob@test.com`, Age: `200`, Postal Code: `K2B 1C3`         | Error: age out of valid range     | Error: age out of valid range     | Success |
| TC-R6: Missing postal code        | Username: `carol`, Email: `carol@test.com`, Age: `22`, Postal Code: _(empty)_      | Error: postal code is required    | Error: postal code is required    | Success |
| TC-R7: Invalid postal code format | Username: `dave`, Email: `dave@test.com`, Age: `40`, Postal Code: `INVALID`        | Error: invalid postal code format | Error: invalid postal code format | Success |
| TC-R8: All fields empty           | _(all empty)_                                                                      | Multiple validation errors        | Multiple validation errors        | Success |

---

## Exercise 2 — `Date.nextDate()` JUnit Tests

### Test Cases

The following 20 test cases were implemented from the lab assignment:

| TC   | Input (year, month, day) | Expected Output            | Type                        |
| ---- | ------------------------ | -------------------------- | --------------------------- |
| TC1  | 1700, 06, 20             | 1700, 06, 21               | Valid                       |
| TC2  | 2005, 04, 15             | 2005, 04, 16               | Valid                       |
| TC3  | 1901, 07, 20             | 1901, 07, 21               | Valid                       |
| TC4  | 3456, 03, 27             | 3456, 03, 28               | Valid                       |
| TC5  | 1500, 02, 17             | 1500, 02, 18               | Valid                       |
| TC6  | 1700, 06, 29             | 1700, 06, 30               | Valid                       |
| TC7  | 1800, 11, 29             | 1800, 11, 30               | Valid                       |
| TC8  | 3453, 01, 29             | 3453, 01, 30               | Valid                       |
| TC9  | 444, 02, 29              | 444, 03, 01                | Valid (leap year)           |
| TC10 | 2005, 04, 30             | 2005, 05, 01               | Valid (end of 30-day month) |
| TC11 | 3453, 01, 30             | 3453, 01, 31               | Valid                       |
| TC12 | 3456, 03, 30             | 3456, 03, 31               | Valid                       |
| TC13 | 1901, 07, 31             | 1901, 08, 01               | Valid (end of 31-day month) |
| TC14 | 3453, 01, 31             | 3453, 02, 01               | Valid (month rollover)      |
| TC15 | 3456, 12, 31             | 3457, 01, 01               | Valid (year rollover)       |
| TC16 | 1500, 02, 31             | `IllegalArgumentException` | Invalid                     |
| TC17 | 1500, 02, 29             | `IllegalArgumentException` | Invalid (non-leap year)     |
| TC18 | -1, 10, 20               | `IllegalArgumentException` | Invalid (negative year)     |
| TC19 | 1458, 15, 12             | `IllegalArgumentException` | Invalid (month > 12)        |
| TC20 | 1975, 06, -50            | `IllegalArgumentException` | Invalid (negative day)      |

### Test Files

| File                             | Description                                                                    |
| -------------------------------- | ------------------------------------------------------------------------------ |
| `DateTest.java`                  | 20 explicit JUnit 5 tests (no parameterization) — one method per test case     |
| `DateNextDateOkTest.java`        | Parameterized JUnit 5 tests for TC1–TC15 (valid dates)                         |
| `DateNextDateExceptionTest.java` | Parameterized JUnit 5 tests for TC16–TC20 (invalid dates expecting exceptions) |

---

## How to Run Tests

### Exercise 2 (ECS — Date and Bit tests)

```bash
cd Lab2/ecs
.\bin\test.bat
```

This script:

1. Cleans compiled class files from `dist/`
2. Compiles all source files from `src/`
3. Compiles all test files from `test/` against the JUnit standalone JAR
4. Runs all tests via the JUnit Platform Console

### Exercise 1 (Registration app)

```bash
cd Lab2/registration
java --add-opens java.base/java.lang=ALL-UNNAMED -jar user-registration-app-0.1.0.jar
```

Then open `http://localhost:8080` in a browser and perform manual tests.

---

## Technologies Used

| Technology                        | Version | Purpose                                                 |
| --------------------------------- | ------- | ------------------------------------------------------- |
| Java                              | 11+     | Implementation language                                 |
| JUnit Jupiter (JUnit 5)           | 1.7.1   | Unit testing framework                                  |
| JUnit Vintage                     | 1.7.1   | Backward compatibility for JUnit 4 tests (`BitAndTest`) |
| JUnit Platform Console Standalone | 1.7.1   | Test runner (no Maven/Gradle required)                  |

---

## About Parameterized Testing

Parameterized tests allow a single test method to run multiple times with different input data. This reduces code duplication and makes it easy to add new test cases.

In JUnit 5, the key annotations are:

- `@ParameterizedTest` — marks a method as a parameterized test
- `@MethodSource("methodName")` — supplies test arguments from a static factory method returning a `Stream<Arguments>`
- `@CsvSource` — supplies test arguments inline as comma-separated strings

**Example:**

```java
@ParameterizedTest
@MethodSource("validDateCases")
void nextDate_returnsExpectedDate(Date input, Date expected) {
    assertEquals(expected, input.nextDate());
}
```

`DateNextDateOkTest` uses `@MethodSource` to run TC1–TC15 with a single test method. `DateNextDateExceptionTest` uses the same approach for TC16–TC20, asserting that `IllegalArgumentException` is thrown for each invalid input.
