package selenium;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Starts the bundled {@code bookstore5.jar} Spring Boot server once for the whole
 * test JVM and waits until it actually answers on http://localhost:8080/.
 *
 * The starter test shipped with the lab launched the server with a bare
 * {@code ProcessBuilder} and immediately opened the browser, with two problems on
 * a modern machine:
 *   1. the browser navigated before Spring Boot had finished booting
 *      (~8 s), giving {@code net::ERR_CONNECTION_REFUSED};
 *   2. the server's stdout/stderr were never drained, so a full pipe buffer
 *      could stall startup.
 *
 * This helper redirects the server output to a temp file, polls until the app
 * responds, and kills the server via a JVM shutdown hook so a single instance is
 * shared across every test class (avoiding port-8080 conflicts between classes).
 */
final class ServerControl {

  private static final String URL = "http://localhost:8080/";
  private static Process server;

  private ServerControl() {}

  static synchronized void ensureStarted() throws Exception {
    if (isUp()) {
      return; // already running (this or a previously started instance)
    }
    File log = File.createTempFile("bookstore-server", ".log");
    ProcessBuilder pb = new ProcessBuilder("java", "-jar", "bookstore5.jar");
    pb.redirectErrorStream(true);
    pb.redirectOutput(log);
    server = pb.start();

    Runtime.getRuntime().addShutdownHook(new Thread(ServerControl::stop));

    long deadline = System.currentTimeMillis() + 90_000;
    while (System.currentTimeMillis() < deadline) {
      if (isUp()) {
        return;
      }
      if (!server.isAlive()) {
        throw new IllegalStateException(
            "bookstore5.jar exited during startup; see log: " + log.getAbsolutePath());
      }
      Thread.sleep(1000);
    }
    throw new IllegalStateException(
        "bookstore5.jar did not answer on " + URL + " within 90s; see log: " + log.getAbsolutePath());
  }

  private static void stop() {
    if (server != null) {
      server.destroy();
      try {
        if (!server.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
          server.destroyForcibly();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private static boolean isUp() {
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) new URL(URL).openConnection();
      conn.setConnectTimeout(2000);
      conn.setReadTimeout(2000);
      conn.setRequestMethod("GET");
      return conn.getResponseCode() == 200;
    } catch (Exception e) {
      return false;
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }
}
