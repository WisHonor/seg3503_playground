package selenium;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Additional Selenium acceptance tests for the YAMAZONE BookStore.
 *
 * Covers requirement F2 "Browse Catalogue" / Use Case 4:
 *   F2.1 books can be searched by category,
 *   F2.2 an empty category returns the whole catalogue,
 *   alt 1.b no book matching the searched category shows a "no match" message.
 *
 * The catalogue is seeded (see the app's DatabaseInit) with two categories:
 *   - "tech": hall001, hall002        (2 books)
 *   - "kids": lewis001, alexander001, rowling001 (3 books)
 */
class CategorySearchTest {

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
    driver.get("http://localhost:8080/");
    // wait to make sure Selenium is done loading the page
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));
  }

  @AfterEach
  public void tearDown() {
    driver.quit();
  }

  /** F2.1 - searching the "tech" category returns exactly the two tech books. */
  @Test
  void searchByCategoryTechReturnsMatchingBooks() {
    // A screenshot of the application home page, for the submission.
    takeScreenshot("home.png");

    searchCategory("tech");

    List<WebElement> titles = resultTitles();
    assertEquals(2, titles.size(), "The 'tech' category should return 2 books");
    assertNotNull(driver.findElement(By.id("title-hall001")));
    assertNotNull(driver.findElement(By.id("title-hall002")));

    // A screenshot of the search results, for the submission.
    takeScreenshot("search-results-tech.png");
  }

  /** F2.1 - searching the "kids" category returns exactly the three kids books. */
  @Test
  void searchByCategoryKidsReturnsMatchingBooks() {
    searchCategory("kids");

    List<WebElement> titles = resultTitles();
    assertEquals(3, titles.size(), "The 'kids' category should return 3 books");
    assertNotNull(driver.findElement(By.id("title-lewis001")));
    assertNotNull(driver.findElement(By.id("title-alexander001")));
    assertNotNull(driver.findElement(By.id("title-rowling001")));
  }

  /** F2.2 - an empty category returns the whole catalogue (all five books). */
  @Test
  void emptyCategoryReturnsWholeCatalogue() {
    searchCategory("");

    List<WebElement> titles = resultTitles();
    assertEquals(5, titles.size(), "An empty category should return the whole catalogue");
  }

  /** Browse Catalogue alt 1.b - an unknown category shows a no-match message. */
  @Test
  void unknownCategoryShowsNoMatchMessage() {
    searchCategory("doesNotExist");

    assertTrue(resultTitles().isEmpty(), "An unknown category should return no books");
    WebElement content = driver.findElement(By.className("content"));
    assertTrue(content.getText().contains("do not have any item matching category"),
        "A no-match message should be displayed");
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
    // wait for the catalogue page (results content) to load
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("content")));
  }

  /** The list of book-title cells in the results table. */
  private List<WebElement> resultTitles() {
    return driver.findElements(By.xpath("//div[@class='content']//td[starts-with(@id,'title-')]"));
  }

  /** Saves a screenshot of the current page into the submission assets folder. */
  private void takeScreenshot(String fileName) {
    try {
      Path dir = Paths.get(System.getProperty("screenshot.dir", "../assets"));
      Files.createDirectories(dir);
      File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      Files.copy(src.toPath(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      // Screenshots are a submission aid; never fail the test because of them.
      System.err.println("Could not save screenshot " + fileName + ": " + e.getMessage());
    }
  }
}
