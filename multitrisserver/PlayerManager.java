package multitrisserver;
import java.net.*;
import java.nio.channels.*;
import java.util.LinkedList;

public class PlayerManager implements Runnable
{
	private LinkedList<Player> players;
	private LinkedList<Player> nextActivatedPlayers;
	private String[] colors;
	private GameLogic parent;
	private ServerSocket sSock = null;
	private boolean keepOnRunning = true;
	private int cleanUpTo = 1; // when calling listGui(), this many records will be transmitted (remember this to make sure players are deleted properly)
	
	public PlayerManager(GameLogic parent, int port)
	{
		this.parent = parent;
		this.players = new LinkedList<Player>();
		this.nextActivatedPlayers = new LinkedList<Player>();
		this.colors = new String[] {"kiss me i'm a special value"};
		
		try
		{
			this.sSock = new ServerSocket(port);
			new Thread(this).start();
		}
		catch(Exception e)
		{
			System.err.println(e); // Good error handling is one of the most important aspects of programming ;)
		}
	}
	
	public boolean activateNextPlayer(Stone s)
	{
		while(this.nextActivatedPlayers.size() > 0)
		{
			Player luckyOne = this.nextActivatedPlayers.poll();
			
			if(luckyOne.isReadyForPlaying())
			{
				luckyOne.activate(s);
				return true;
			}
		}
		return false;
	}
	
	public boolean tryToMoveLeft(Stone s)
	{
		return this.parent.tryToMoveLeft(s);
	}
	
	public boolean tryToMoveRight(Stone s)
	{
		return this.parent.tryToMoveRight(s);
	}
	
	public boolean tryToMoveDown(Stone s)
	{
		return this.parent.tryToMoveDown(s);
	}
	
	public boolean tryToRotate(Stone s)
	{
		return this.parent.tryToRotate(s);
	}
	
	public void deactivatePlayer(int col)
	{
		for(int i=0;i<this.players.size();i++)
		{
			if(this.players.get(i).getColor() == col)
			{
				this.players.get(i).deactivate();
				if(this.players.get(i).isAlive())
					this.nextActivatedPlayers.offer(this.players.get(i)); // enqueue him for the next time
				return;
			}
		}
	}
	
	public void stoneHasPlonked(Stone s)
	{
		this.deactivatePlayer(s.getColor());
	}
	
	public void allPlayersWon()
	{
		for(int i=0;i<this.players.size();i++)
		{
			if(this.players.get(i).isPlaying())
				this.players.get(i).deactivate();
			
			this.players.get(i).sendWin();
		}
	}
	
	public void allPlayersLost()
	{
		for(int i=0;i<this.players.size();i++)
		{
			if(this.players.get(i).isPlaying())
				this.players.get(i).deactivate();
			
			this.players.get(i).sendLose();
		}
	}
	
	private void sendToAllClients(String msg)
	{
		for(int i=0;i<this.players.size();i++)
		{
			if(this.players.get(i).isAlive())
				this.players.get(i).sendToClient(msg);
		}
	}
	
	public void playerDied(Player deadPlayer)
	{
		// Hey, let's do a funky bit of nothing here... who cares about dead players
	}
	
	public void listGui(GUIServer gui)
	{
		this.cleanUpTo = (this.colors.length > this.cleanUpTo ? this.colors.length : this.cleanUpTo);
		
		for(int i=1;i<this.cleanUpTo;i++)
		{
			if(i >= this.colors.length)
			{
				// player is definitely dead
				gui.COLOR(i, "000000");
				gui.PLAYER(i);
				gui.POINTS(i, 0);
				continue;
			}
			
			gui.COLOR(i, this.colors[i]);
			// does he exist?
			boolean exists = false;
			for(int player=0;player<this.players.size();player++)
			{
				if(this.players.get(player).getColor() == i)
				{
					if(this.players.get(player).isAlive())
					{
						exists = true;
						gui.PLAYER(i, this.players.get(player).getName());
						gui.POINTS(i, this.players.get(player).getPoints());
					}
					break;
				}
			}
			
			if(! exists)
			{
				gui.PLAYER(i);
				gui.POINTS(i, 0);
			}
		}
	}
	
	private int generateColor()
	{
		String newCol = "";
		
		int newColor = this.colors.length;
		
		switch(newColor)
		{
			case 1:	newCol = "ff0000"; break;
			case 2:	newCol = "00ff00"; break;
			case 3:	newCol = "0000ff"; break;
			case 4:	newCol = "ffff00"; break;
			case 5:	newCol = "ff00ff"; break;
			case 6:	newCol = "00ffff"; break;
			default: newCol = Integer.toHexString(1048576 + (int)(Math.random() * 9437184.0)); break;
		}
		
		String[] newColors = new String[newColor+1];
		for(int i=0;i<newColor;i++)
			newColors[i] = this.colors[i];
		
		newColors[newColor] = newCol;
		
		this.colors = newColors;
		return newColor;
	}
				
	public void cleanUp()
	{
		this.nextActivatedPlayers.clear();
		this.cleanUpTo = (this.colors.length > this.cleanUpTo ? this.colors.length : this.cleanUpTo);
		this.colors = new String[] {"kiss me i'm a special value"};
				
		for(int i=0;i<this.players.size();i++)
		{
			if(! this.players.get(i).isAlive())
				this.players.remove(i--);
			else
			{
				int generatedColor = this.generateColor();
				this.players.get(i).setColor(generatedColor);
				this.players.get(i).sendToClient("ATTENTION " + this.colors[generatedColor] + " " + generatedColor);
				if(! this.players.get(i).isPlaying())
					this.nextActivatedPlayers.offer(this.players.get(i));
			}
		}
	}
	
	public void playerReady(Player player)
	{
		int hisColor = this.generateColor();
		player.setColor(hisColor);
		player.sendToClient("ATTENTION " + this.colors[hisColor] + " " + hisColor);
		this.nextActivatedPlayers.offer(player);
		
		this.parent.playersChanged();
	}
	
	public void goodbyeMyFriends()
	{
		for(int i=0;i<this.players.size();i++)
		{
			Player player = this.players.get(i);
			if(player.isAlive())
			{
				if(player.isPlaying())
					player.deactivate();
				
				player.fuckYou("Have a nice day");
			}
		}
		
		this.nextActivatedPlayers.clear();
	}
	
	public void shutDown()
	{
		this.keepOnRunning = false;
	}
	
	public void run() 
	{
		try
		{
			this.sSock.setSoTimeout(1000);
		}
		catch(Exception e) { System.err.println(e); }
		
		while(this.keepOnRunning)
		{
			try
			{
				Socket clientSocket = this.sSock.accept();
				this.players.offer(new Player(clientSocket, this));
			}
			catch (SocketTimeoutException e)
			{
				// do nothing
			}
			catch (Exception e)
			{
				System.err.println(e);
				this.keepOnRunning = false;
			}
		}
		
		try
		{
			this.goodbyeMyFriends();
			this.sSock.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	
	public void step()
	{
		for(int i=0;i<this.players.size();i++)
		{
			this.players.get(i).step();
		}
	}
}
