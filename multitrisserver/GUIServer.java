package multitrisserver;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.Socket;
import java.util.LinkedList;
/**
 * This class implements the GUI Server in java. It can be easily included in the Game Logic.
 * Syntax is:
 * GUIServer s = new GUIServer(port); (starts the server as Thread, waiting for clients (GUIs) to connect)
 * s.FLUSH().... sends those commands to all currently connected GUIs.
 * @author Thomas Oster
 */
public class GUIServer implements Runnable
{
	private boolean keepRunning;
	private boolean someoneConnected;
	private ServerSocket sSock;
	private LinkedList<OutputStreamWriter> clients;
	private GameLogic gameLogic;
	
	private void sendString(String s)
	{
		for (int i=0;i<clients.size();i++)
		{
			try
			{
				clients.get(i).write(s+"\n");
				clients.get(i).flush();
			}
			catch(Exception e)
			{
				System.err.println(e);
				try
				{
					this.clients.get(i).close();
				}
				catch (Exception e2)
				{
					System.out.println(e2);
				}
				System.out.println("GUI disconnected.");
				this.clients.remove(i--);
			}
		}
	}
	public void close()
	{
		keepRunning=false;
	}
	public void FLUSH(){sendString("FLUSH");}
	public void SIZE(int w, int h){sendString("SIZE "+w+" "+h);}
	public void MESSAGE(String m){sendString("MESSAGE "+m);}
	public void SET(int y, int x, int color, boolean exploding)
	{
		sendString("SET "+y+" "+x+" "+color+(exploding?" EXPLODE":""));
	}
	public void SET(int y, int x, int color)
	{
		SET(y,x,color,false);
	}
	public void RESET(boolean field, boolean players, boolean colors, boolean points, boolean messages)
	{
		String paket="RESET";
		if (field)
			paket+=" FIELD";
		if (points)
			paket+=" POINTS";
		if (players)
			paket+=" PLAYERS";
		if (players)
			paket+=" COLORS";
		if (messages)
			paket+=" MESSAGES";
	}
	public void POINTS(int player, int points){sendString("POINTS "+player+" "+points);}
	public void AUTH(String auth){sendString("AUTH "+auth);}
	public void COLOR(int number, String color){sendString("COLOR "+number+" "+color);}
	public void PLAYER(int number, String name){sendString("PLAYER "+number+" "+name);}
	public void PLAYER(int number){sendString("PLAYER "+number);}
	public GUIServer(GameLogic gameLogic, int Port)
	{
		this.gameLogic = gameLogic;
		try
		{
			sSock = new ServerSocket(Port);
			clients = new LinkedList<OutputStreamWriter>();
			keepRunning=true;
			someoneConnected=false;
			new Thread(this).start();
			while(!someoneConnected)
				Thread.sleep(200);
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	public void run() 
	{
		while(keepRunning)
		{
			try
			{
				sSock.setSoTimeout(1000);
				Socket clientSocket=sSock.accept();//wait for a client to connect
				
				if(this.someoneConnected) // not the first gui to connect
				{
					this.gameLogic.playersChanged(); // force resending players data
				}
				OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream());
				
				if(this.someoneConnected) // not the first gui to connect
				{
					try
					{
						out.write(this.gameLogic.initStrForNewGui()); // init gui
					}
					catch (Exception e2)
					{
						System.out.println(e2);
					}
				}
				
				someoneConnected=true;
				clients.add(out);
			}
			catch (SocketTimeoutException e)
			{
				//do nothing
			}
			catch (Exception e)
			{
				System.err.println("22"+e);
			}
		}
		clients.clear();
		try
		{
				sSock.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
}
