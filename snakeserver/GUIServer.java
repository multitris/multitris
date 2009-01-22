package snakeserver;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GUIServer 
{
	private OutputStreamWriter out;
	public GUIServer(int Port)
	{
		try
		{
			ServerSocket ss = new ServerSocket(Port);
			Socket guis = ss.accept();
			out = new OutputStreamWriter(guis.getOutputStream());
			sendString("SIZE "+SnakeServer.GAMEWIDTH+" "+SnakeServer.GAMEHEIGHT);
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(1);
		}
		
	}
	private void sendString(String s)
	{
		try
		{
			out.write(s+"\n");
			out.flush();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	public void FLUSH()
	{
		sendString("FLUSH");
	}
	public void PLAYER(int idx, String color, String name)
	{
		sendString("COLOR "+idx+" "+color);
		sendString("PLAYER "+idx+" "+name);
	}
	public void SET(int x, int y, int color)
	{
		sendString("SET "+y+" "+x+" "+color);
	}
}
