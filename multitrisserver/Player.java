package multitrisserver;
import java.io.*;
import java.net.*;

public class Player
{
	private int nr = -1;
	private String name = "Unnamed hero";
	private int color = -1;
	private Socket playerSocket = null;
	private OutputStreamWriter playerSocketOut = null;
	private BufferedReader playerSocketIn = null;
	private PlayerManager parent = null;
	private Stone currentStone = null;
	private int points = 0;
	private String protocolId = "";
	private boolean loggedIn = false;
	
	public Player(Socket playerSocket, PlayerManager parent)
	{
		this.playerSocket = playerSocket;
		this.parent = parent;
		try
		{
			this.playerSocketOut = new OutputStreamWriter(
                    playerSocket.getOutputStream());
			this.playerSocketIn = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
		}
		catch(Exception e)
		{
			System.err.println("1:"+e);
			this.playerSocket = null;
		}
	}
	public int getColor()
	{
		return this.color;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public int getNr()
	{
		return this.nr;
	}
	
	public void setNr(int nr)
	{
		this.nr = nr;
	}
	
	public int getPoints()
	{
		return this.points;
	}
	
	public void setPoints(int points)
	{
		this.points = points;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	private String[] splitAtSpaces(String line, int maxSplits)
	{
		return line.split(" ",maxSplits);
		/*
		String copy = new String(line);
		String[] result = new String[maxSplits];
		for(int i=0;i<result.length;i++)
			result[i] = null;
		
		int i = 0;
		while(i < (maxSplits-1) && copy.indexOf(' ') != -1)
		{
			result[i++] = copy.substring(0, copy.indexOf(' '));
			copy = copy.substring(copy.indexOf(' ')+1);
		}
		
		result[i] = copy;
		return result;
		*/
	}
	
	private void interpreteCommand(String line)
	{
		if(line.startsWith("NORRIS"))
		{
			// whohw, I do not even know how to send a CHUCK but I receive a NORRIS... that's great.
			sendToClient("CHUCK "+splitAtSpaces(line, 3)[1]);
		}
		else if(line.startsWith("IWANTFUN") && ! this.loggedIn)
		{
			String[] parts = this.splitAtSpaces(line, 3);
			if(parts[1] != null)
			{
				this.protocolId = parts[1];
				if(parts[2] != null)
					this.name = parts[2];
				this.parent.playerReady(this);
				this.loggedIn = true;
			}
			else
				this.fuckYou("No fun today.");
		}
		else if(line.equals("LAFONTAINE") || line.equals("STOIBER") || line.equals("TURN") || line.equals("MARIHUANA"))
		{
			if(this.isPlaying())
			{
				if(line.equals("LAFONTAINE"))
					this.parent.tryToMoveLeft(this.currentStone);
				else if(line.equals("STOIBER"))
					this.parent.tryToMoveRight(this.currentStone);
				else if(line.equals("MARIHUANA"))
					this.parent.tryToMoveDown(this.currentStone);
				else
					this.parent.tryToRotate(this.currentStone);
			}
		}
		else
			System.out.println("What the heck: " + line);
	}
	
	public void sendToClient(String msg)
	{
		if(this.isAlive())
		{
			try
			{
				this.playerSocketOut.write(msg+"\n");
				this.playerSocketOut.flush();
			}
			catch (Exception e)
			{
				System.err.println("2:" + e);
				try
				{
					this.playerSocket.close();
				}
				catch (Exception e2)
				{
					System.err.println("3:" + e2);
				}
				this.playerSocket = null;
				parent.playerDied(this);
			}
		}
	}
			
	private void disconnect()
	{
		if(this.playerSocket != null && ! this.playerSocket.isClosed())
		{
			try
			{
				this.playerSocketOut.close();
				this.playerSocket = null;
			}
			catch (Exception e)
			{
				System.err.println("4:" +e);
			}
		}
	}
	
	public void sendWin()
	{
		this.sendToClient("NOTBAD");
	}
	
	public void sendLose()
	{
		this.sendToClient("THATWASMISERABLE");
	}
	
	public void fuckYou(String msg)
	{
		this.sendToClient("FUCKYOU" + (msg == "" ? "" : (" " + msg)));
		this.disconnect();
	}
	
	public void activate(Stone s)
	{
		if(this.isAlive() && this.isReadyForPlaying() && ! this.isPlaying())
		{
			this.currentStone = s;
			s.setColor(this.color);
			
			this.sendToClient("GOFORREST");
		}
	}
	
	public void deactivate()
	{
		this.currentStone = null;
		this.sendToClient("PLONK");
	}
	
	public boolean isAlive()
	{
		return (this.playerSocket != null && this.playerSocket.isConnected());
	}
	
	public boolean isReadyForPlaying()
	{
		return (this.isAlive() && this.loggedIn && ! this.isPlaying());
	}
	
	public boolean isPlaying()
	{
		return (this.isAlive() && this.loggedIn && this.currentStone != null);
	}
	
	public void step()
	{
		if(this.isAlive())
		{
			try
			{
				while(this.isAlive())
				{
					this.interpreteCommand(this.playerSocketIn.readLine());
				}
			}
			catch (Exception e)
			{
				System.err.println("5:"+e);
			}
		}
	}
}
