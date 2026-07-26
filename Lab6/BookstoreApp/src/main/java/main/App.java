package main;

import java.io.BufferedReader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
        	ProcessBuilder pb = new ProcessBuilder("java", "-jar", "bookstore5.jar");
        	// Send the server's log to this console. Left on the default pipe,
        	// nothing ever drains it: the pipe fills up during Spring Boot's
        	// startup and the server stalls before it binds port 8080.
        	pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        	pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        	Process p = pb.start();
            System.out.println("Press Enter to stop server");
            // wait for Enter to terminate
            new BufferedReader(new java.io.InputStreamReader(System.in)).readLine();
            p.destroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
