package selenium;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Additional Selenium acceptance tests for the customer ordering flow.
 *
 * Covers:
 *   F3 / F4  - Order Book, Show Order (Use Cases 6 and 7),
 *   F5.1     - setting the number of copies to 0 removes the book (UC8 alt 1.a),
 *   F6       - Checkout, with F6.1 (13% taxes) and F6.2 ($10 + 5% shipping),
 *              Use Case 9.
 *
 * The shopping cart is kept in the HTTP session, and every test gets a brand new
 * ChromeDriver (so a brand new session), so each test starts from an empty cart
 * and none of them depend on the order in which they run. The catalogue itself is
 * never modified here.
 *
 * Note that loading the welcome page (/) replaces the session shopping cart with
 * an empty one, so once a test has started ordering it navigates only through the
 * menu and the cart icon.
 *
 * Seeded prices used by the assertions: hall001 $39.95, hall002 $49.99.
 */
class OrderAcceptanceTest {

  private static final double HALL001_COST = 39.95;
  private static final double HALL002_COST = 49.99;

  private WebDriver driver;

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
    driver.get("http://localhost:8080/");
    // wait to make sure Selenium is done loading the page
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));
  }

  @AfterEach
  public void tearDown() {
    driver.quit();
  }

  /** F3 / F4 - a book added from the catalogue is listed in the shopping cart. */
  @Test
  void orderedBookIsListedInTheShoppingCart() {
    searchCategory("tech");
    addToCart("hall001");
    openCart();

    // A screenshot of the shopping cart, for the submission.
    Screenshots.save(driver, "order-page.png");

    assertTrue(cartText().contains("hall001"), "The ordered book id should be listed in the cart");
    assertEquals("1", copiesOf("hall001"), "A book ordered once should show 1 copy");
    assertEquals(HALL001_COST, lineTotalOf("hall001"), 0.01,
        "The line total of a single copy should be the unit cost");
  }

  /** UC6 alt 1.a - ordering the same book twice increments the number of copies. */
  @Test
  void orderingTheSameBookTwiceIncrementsTheNumberOfCopies() {
    searchCategory("tech");
    addToCart("hall002");
    addToCart("hall002");
    openCart();

    assertEquals("2", copiesOf("hall002"), "Ordering the same book twice should give 2 copies");
    assertEquals(2 * HALL002_COST, lineTotalOf("hall002"), 0.01,
        "The line total should be two times the unit cost");
  }

  /** F5.1 / UC8 alt 1.a - setting the number of copies to 0 removes the book. */
  @Test
  void settingTheNumberOfCopiesToZeroRemovesTheBook() {
    searchCategory("tech");
    addToCart("hall001");
    addToCart("hall002");
    openCart();
    assertTrue(cartText().contains("hall001"), "Both books should be in the cart to begin with");

    updateCopies("hall001", "0");
    openCart();

    assertFalse(cartText().contains("hall001"), "A book set to 0 copies should leave the order");
    assertTrue(cartText().contains("hall002"), "The other book should still be in the order");
  }

  /** F6 / F6.1 / F6.2 - checkout shows the date, 13% taxes and $10 + 5% shipping. */
  @Test
  void checkoutShowsOrderDateTaxesAndShipping() {
    searchCategory("tech");
    addToCart("hall001");
    openCart();

    driver.findElement(By.cssSelector("button[name='checkout']")).click();
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("checkoutTable")));
    // A screenshot of the checkout page, for the submission.
    Screenshots.save(driver, "checkout-page.png");

    assertFalse(driver.findElement(By.id("order_date")).getText().trim().isEmpty(),
        "The checkout page should show the order date");

    double taxes = money(driver.findElement(By.id("order_taxes")).getText());
    double shipping = money(driver.findElement(By.id("order_shipping")).getText());
    double grandTotal = money(driver.findElement(By.id("order_total")).getText());

    assertEquals(HALL001_COST * 0.13, taxes, 0.01, "Taxes should be 13% of the order total");
    assertEquals(10.0 + HALL001_COST * 0.05, shipping, 0.01,
        "Shipping should be $10 plus 5% of the order total");
    assertEquals(HALL001_COST + taxes + shipping, grandTotal, 0.02,
        "The billed amount should be the order total plus taxes and shipping");
  }

  // ---- helpers -------------------------------------------------------------

  /** Types a category in the search box and submits the search form. */
  private void searchCategory(String category) {
    WebElement categoryField = driver.findElement(By.id("search"));
    categoryField.clear();
    if (!category.isEmpty()) {
      categoryField.sendKeys(category);
    }
    driver.findElement(By.id("searchBtn")).click();
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("cartLink")));
  }

  /** Clicks the "Add to Cart" button of a book and waits for the AJAX call to finish. */
  private void addToCart(String itemId) {
    driver.findElement(By.id("order-" + itemId)).click();
    waitForAjax();
  }

  /**
   * Opens the shopping cart. The cart icon only lives on the catalogue page and
   * /orderPage answers POST only, so the cart is always reached the way a user
   * would: search the catalogue from the menu, then click the cart.
   *
   * The search is made from whatever page the test is on - the menu fragment is
   * part of every page - and never by going back to the welcome page, because
   * loading / puts a brand new (empty) shopping cart in the session.
   */
  private void openCart() {
    searchCategory("");
    driver.findElement(By.id("cartLink")).click();
    new WebDriverWait(driver, 60)
        .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[name='checkout']")));
  }

  /** Sets the number of copies of a book in the order and clicks its Update button. */
  private void updateCopies(String itemId, String copies) {
    WebElement field = driver.findElement(By.id(itemId));
    field.clear();
    field.sendKeys(copies);
    driver.findElement(By.cssSelector("button[name='updateOrder'][value='" + itemId + "']")).click();
    waitForAjax();
  }

  /** The number of copies currently shown for a book in the order. */
  private String copiesOf(String itemId) {
    return driver.findElement(By.id(itemId)).getAttribute("value");
  }

  /** The line total currently shown for a book in the order. */
  private double lineTotalOf(String itemId) {
    return money(driver.findElement(By.id("tot" + itemId)).getText());
  }

  /** The text of the order table. */
  private String cartText() {
    return driver.findElement(By.className("content")).getText();
  }

  /** Turns a displayed currency amount such as "$39.95" into a number. */
  private double money(String formatted) {
    return Double.parseDouble(formatted.replaceAll("[^0-9.-]", ""));
  }

  /**
   * Waits until every jQuery AJAX call has completed. The catalogue and order
   * pages post to /orderItem and /updateOrder in the background, so this is the
   * synchronisation point before navigating away or reading the result.
   */
  private void waitForAjax() {
    new WebDriverWait(driver, 30).until(d -> Boolean.TRUE.equals(
        ((JavascriptExecutor) d).executeScript(
            "return window.jQuery !== undefined && jQuery.active === 0")));
  }
}
