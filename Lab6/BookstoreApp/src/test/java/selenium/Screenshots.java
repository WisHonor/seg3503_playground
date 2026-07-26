package selenium;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Saves browser screenshots into the submission assets folder, so that the
 * pictures of the running application used by the README are produced by the test
 * run itself rather than captured by hand.
 *
 * The destination is Lab6/assets, relative to the Maven project; it can be
 * pointed somewhere else with -Dscreenshot.dir=...
 */
final class Screenshots {

  private Screenshots() {}

  /** Saves a screenshot of the page currently displayed by the driver. */
  static void save(WebDriver driver, String fileName) {
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
