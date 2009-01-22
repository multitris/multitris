package snakeserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class PlayerServer implements Runnable 
{
	private ServerSocket serverSocket;
	private boolean isAlive;
	private LinkedList<Player> playerList;
	public PlayerServer(int Port, LinkedList<Player> playerList)
	{
		try
		{
			this.playerList=playerList;
			serverSocket=new ServerSocket(Port);
			isAlive=true;
			new Thread(this).start();
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(1);
		}
		
	}
	public void run()
	{
		try
		{
			serverSocket.setSoTimeout(200);
		}
		catch (Exception e)
		{}
		while(isAlive)
		{
			try
			{
				Socket client = serverSocket.accept();
				BufferedReader in = new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				SnakeServer.addPlayer(in, 0, 0);
				OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
				out.write("ATTENTION\n");
				out.write("GOFORREST\n");
				out.flush();
			}
			catch (java.net.SocketTimeoutException e)
			{
				//do nothing
			}
			catch (Exception e)
			{
				System.err.println(e);
			}
		}
	}
}
