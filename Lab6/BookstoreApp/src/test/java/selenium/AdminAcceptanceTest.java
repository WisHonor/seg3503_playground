package selenium;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Additional Selenium acceptance tests for the administration side of the store.
 *
 * Covers:
 *   F8 - Sign In (Use Case 1), including the invalid credentials alternative 3.a,
 *   F1 - Add Book to Catalogue (Use Case 3),
 *   F7 - Remove Book from Catalogue (Use Case 5).
 *
 * Unlike the shopping cart, the catalogue is stored server side and is shared by
 * every session, so a book added by a test would leak into the other tests (for
 * instance into the catalogue counts asserted by CategorySearchTest). Every test
 * therefore books a unique id, and tearDown removes the book if the test did not
 * remove it itself, leaving the catalogue exactly as it was found.
 */
class AdminAcceptanceTest {

  private static final String ADMIN_URL = "http://localhost:8080/admin";
  private static final String ADMIN_CATALOG_URL = "http://localhost:8080/admin/catalog";
  private static final String STORE_URL = "http://localhost:8080/";
  private static final String TEST_CATEGORY = "seleniumlab";

  private WebDriver driver;

  /** Id of the book added by the running test, or null when nothing must be cleaned up. */
  private String addedBookId;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    ServerControl.ensureStarted();
  }

  @BeforeEach
  void setUp() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    driver.manage().window().maximize();
    addedBookId = null;
  }

  @AfterEach
  public void tearDown() {
    try {
      removeLeftoverBook();
    } finally {
      driver.quit();
    }
  }

  /** F8 / UC1 - signing in with valid credentials opens the admin operations page. */
  @Test
  void signInWithValidCredentialsOpensTheAdminPage() {
    driver.get(ADMIN_URL);
    // an anonymous visitor is sent to the sign in page first
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("loginId")));
    // screenshots of localhost:8080/admin, for the submission
    Screenshots.save(driver, "admin-signin.png");

    submitCredentials("admin", "password");

    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("addBook-form")));
    Screenshots.save(driver, "admin-page.png");
    assertTrue(driver.getCurrentUrl().endsWith("/admin"),
        "A valid sign in should land on the admin page, was " + driver.getCurrentUrl());
    assertTrue(driver.findElement(By.id("addBook-id")).isDisplayed(),
        "The admin page should offer the add book form");
  }

  /** UC1 step 3.a - signing in with wrong credentials shows the error message. */
  @Test
  void signInWithWrongCredentialsShowsTheErrorMessage() {
    driver.get(ADMIN_URL);
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("loginId")));

    submitCredentials("admin", "wrongPassword");

    new WebDriverWait(driver, 60).until(ExpectedConditions.urlContains("error"));
    Screenshots.save(driver, "admin-signin-error.png");
    assertTrue(contentText().contains("Invalid username and/or password"),
        "An invalid sign in should be reported to the user");
    assertTrue(driver.findElement(By.id("loginId")).isDisplayed(),
        "The user should stay on the sign in page");
  }

  /** F1 / UC3 - a book added by the admin shows up in a customer catalogue search. */
  @Test
  void bookAddedByTheAdminAppearsInTheCatalogue() {
    signIn();
    addedBookId = addBook();

    // the customer side of the store, searching the category the book was filed under
    driver.get(STORE_URL);
    searchCategory(TEST_CATEGORY);

    Screenshots.save(driver, "admin-added-book.png");
    WebElement title = driver.findElement(By.id("title-" + addedBookId));
    assertEquals("Selenium Lab Test Book", title.getText(),
        "The newly added book should be listed in its category");
    assertEquals("SEG 3503", driver.findElement(By.id("authors-" + addedBookId)).getText(),
        "The book should be listed with the author that was entered");
  }

  /** F7 / UC5 - a book removed by the admin disappears from the catalogue. */
  @Test
  void bookRemovedByTheAdminDisappearsFromTheCatalogue() {
    signIn();
    String bookId = addBook();
    addedBookId = bookId;

    driver.get(ADMIN_CATALOG_URL);
    WebElement row = driver.findElement(By.id("title-" + bookId));
    assertTrue(row.isDisplayed(), "The book should be in the catalogue before it is removed");

    driver.findElement(By.id("del-" + bookId)).click();
    // the delete button posts to /admin/remove and reloads the page on success
    new WebDriverWait(driver, 60).until(ExpectedConditions.stalenessOf(row));
    addedBookId = null; // removed by the test itself, nothing left to clean up

    assertFalse(contentText().contains(bookId),
        "The removed book should be gone from the admin catalogue");

    driver.get(STORE_URL);
    searchCategory(TEST_CATEGORY);
    assertTrue(contentText().contains("do not have any item matching category"),
        "The category should be empty again on the customer side");
  }

  // ---- helpers -------------------------------------------------------------

  /** Signs in as the administrator and waits for the admin page. */
  private void signIn() {
    driver.get(ADMIN_URL);
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("loginId")));
    submitCredentials("admin", "password");
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("addBook-form")));
  }

  /** Fills the sign in form and submits it. */
  private void submitCredentials(String user, String password) {
    driver.findElement(By.id("loginId")).sendKeys(user);
    driver.findElement(By.id("loginPasswd")).sendKeys(password);
    driver.findElement(By.id("loginBtn")).click();
  }

  /**
   * Fills the add book form on the admin page with a unique book and submits it.
   * The form is posted with AJAX, so this waits for the feedback message.
   *
   * @return the id of the book that was added
   */
  private String addBook() {
    String bookId = uniqueBookId();
    driver.findElement(By.id("addBook-category")).sendKeys(TEST_CATEGORY);
    driver.findElement(By.id("addBook-id")).sendKeys(bookId);
    driver.findElement(By.id("addBook-title")).sendKeys("Selenium Lab Test Book");
    driver.findElement(By.id("addBook-authors")).sendKeys("SEG 3503");
    driver.findElement(By.id("longDescription")).sendKeys("Added by an automated acceptance test.");
    driver.findElement(By.id("cost")).sendKeys("12.50");
    driver.findElement(By.cssSelector("button[name='addBook']")).click();

    new WebDriverWait(driver, 60).until(
        ExpectedConditions.textToBePresentInElementLocated(
            By.id("feedback"), "Successfully added book"));
    return bookId;
  }

  /** F1.1 requires a book id of 5 to 8 characters; this one is always 8. */
  private static String uniqueBookId() {
    return "TST" + String.format("%05d", System.currentTimeMillis() % 100000);
  }

  /** Types a category in the search box and submits the search form. */
  private void searchCategory(String category) {
    WebElement categoryField = driver.findElement(By.id("search"));
    categoryField.clear();
    categoryField.sendKeys(category);
    driver.findElement(By.id("searchBtn")).click();
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.className("content")));
  }

  /** The text of the main area of the current page. */
  private String contentText() {
    return driver.findElement(By.className("content")).getText();
  }

  /**
   * Deletes the book added by the test if it is still in the catalogue, so the
   * catalogue is left in the state the test found it in.
   */
  private void removeLeftoverBook() {
    if (addedBookId == null) {
      return;
    }
    try {
      driver.get(ADMIN_CATALOG_URL);
      List<WebElement> deleteButtons = driver.findElements(By.id("del-" + addedBookId));
      if (!deleteButtons.isEmpty()) {
        WebElement row = driver.findElement(By.id("title-" + addedBookId));
        deleteButtons.get(0).click();
        new WebDriverWait(driver, 60).until(ExpectedConditions.stalenessOf(row));
      }
    } catch (RuntimeException e) {
      // Never mask the real test failure with a clean up failure.
      System.err.println("Could not clean up book " + addedBookId + ": " + e.getMessage());
    }
  }
}
