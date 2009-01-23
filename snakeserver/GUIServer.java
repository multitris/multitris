package snakeserver;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GUIServer 
{
	private OutputStreamWriter out;
	private boolean connected;
	public boolean connected()
	{
		return connected;
	}
	public GUIServer(int Port)
	{
		try
		{
			ServerSocket ss = new ServerSocket(Port);
			Socket guis = ss.accept();
			connected=true;
			out = new OutputStreamWriter(guis.getOutputStream());
			sendString("SIZE "+SnakeServer.GAMEWIDTH+" "+SnakeServer.GAMEHEIGHT);
		}
		catch(Exception e)
		{
			connected=false;
			SnakeServer.sendError("Error in GUI-Server:"+e);
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
			connected=false;
			SnakeServer.sendError("GUI disconnected");
		}
	}
	public void MESSAGE(String s)
	{
		sendString("MESSAGE "+s);
	}
	public void FLUSH()
	{
		sendString("FLUSH");
	}
	public void removePLAYER(int idx)
	{
		sendString("PLAYER "+idx);
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
