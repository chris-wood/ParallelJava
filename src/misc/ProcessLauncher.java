import java.io.*;
import java.util.*;

public class ProcessLauncher extends Thread
{
	public void run()
	{
		try 
		{
			String line;
			Scanner scan = new Scanner(System.in);

			ProcessBuilder builder = new ProcessBuilder("/bin/bash");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			OutputStream stdin = process.getOutputStream ();
			InputStream stderr = process.getErrorStream ();
			InputStream stdout = process.getInputStream ();

			BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

			String input = scan.nextLine();
			input += "\n";
			writer.write(input);
			writer.flush();

			input = scan.nextLine();
			input += "\n";
			writer.write(input);
			writer.flush();

			while ((line = reader.readLine ()) != null) {
				System.out.println ("Stdout: " + line);
			}

			input = scan.nextLine();
			input += "\n";
			writer.write(input);
			writer.close();

			while ((line = reader.readLine ()) != null) {
				System.out.println ("Stdout: " + line);
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		ProcessLauncher launcher = new ProcessLauncher();
		launcher.start(); // must run from separate thread
	}
}

