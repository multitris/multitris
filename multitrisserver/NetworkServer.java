package multitrisserver;
import java.net.*;
import java.io.*;

public class NetworkServer
{
	public static void main(String[] args)
	{
		try {
			ServerSocket listening = null;
			listening = new ServerSocket(2345);
			
			Socket clientSocket = listening.accept();
			
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
												 clientSocket.getInputStream()));
			String inputLine, outputLine;

			out.println("Hallo Client! Server hier.");
			
			while((inputLine = in.readLine()) != null)
			{
				System.out.println("Received: " + inputLine);
				if(inputLine.equals("FUCKYOU"))
					break;
			}
			
			out.close();
			in.close();
			
			clientSocket.close();
			listening.close();
		}
		catch(Exception e) { System.out.println(e); }
	}
}
