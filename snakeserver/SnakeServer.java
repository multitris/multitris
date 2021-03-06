package snakeserver;
import java.awt.Point;
import java.io.BufferedReader;
import java.util.LinkedList;
import common.*;
public class SnakeServer 
{
	public static int index=1;
	public static int GAMEWIDTH = 40;
	public static int GAMEHEIGHT = 40;
	public static int GameServerPort = 12346;
	public static int GUIServerPort = 12345;
	private static LinkedList<Player> Players;
	private static boolean checkCollision(Point p)
	{
		for (int i=0;i<Players.size();i++)
		{
			Player pp = Players.get(i);
			LinkedList<Point> pb = pp.getPoints();
			for (int ipb=0;ipb<pb.size();ipb++)
				if (pb.get(ipb).equals(p)&&pb.get(ipb)!=p)
				{
					return true;
				}
		}
		return false;
	}
	private static GUIServer GUI;
	public static void sendError(String s)
	{
		System.err.println("Error will be send to everybody connected:"+s);
		for (int i=0;i<Players.size();i++)
		{
			if (Players.get(i).connected())
				Players.get(i).sendString("FUCKYOU "+s);
		}
		if (GUI.connected())
		{
			GUI.MESSAGE("Error: "+s);
			GUI.FLUSH();
		}
		System.exit(0);
	}
	public static void addPlayer(Player p, String name)
	{
		String color = Misc.generateColor();
		if (name.equals(""))
			name="Player "+index;
		GUI.PLAYER(index, color, name);
		p.setColor(index);
		Players.add(p);
		//Find some nice place for the new Snake
		int x=0;
		int y=0;
		nothingFound:
		{//This block repeats checking the whole game for 6 horizontal free blocks
			//to place the snake in.. if nothing found, it waits 2 seconds and retries.
			while (true)
			{
				for (x=0;x<GAMEWIDTH-6;x++)
					for (y=0;y<GAMEHEIGHT;y++)
						if (checkCollision(new Point(x,y))==false
						&& checkCollision(new Point(x+1, y))==false
						&& checkCollision(new Point(x+2, y))==false
						&& checkCollision(new Point(x+3, y))==false
						&& checkCollision(new Point(x+4, y))==false
						&& checkCollision(new Point(x+5, y))==false
						&& checkCollision(new Point(x+6, y))==false
						) break nothingFound;
				Misc.delay(2000);
			}
		}
		for (int k=0;k<=6;k++)
		{
			p.getPoints().add(new Point(x+k, y));
			GUI.SET(x+k, y, p.getColor());
		}
		p.sendString("ATTENTION "+color+" "+index);
		p.sendString("GOFORREST");
		index++;
	}
	public static void removePlayer(Player p)
	{
		LinkedList<Point> pis = p.getPoints();
		for(int i=0;i<pis.size();i++)
		{
			GUI.SET(pis.get(i).x, pis.get(i).y, 0);
		}
		GUI.removePLAYER(p.getColor());
		p.die();
		Players.remove(p);
		GUI.FLUSH();
	}
	public static void main(String[] args) 
	{	
		try
		{
			for(int i=0;i<args.length;i++)
			{
				if (args[i].equals("-s"))
				{
					GAMEWIDTH = Integer.parseInt(args[++i]);
					GAMEHEIGHT = Integer.parseInt(args[i]);
					continue;
				}
				if (args[i].equals("-p"))
				{
					GameServerPort = Integer.parseInt(args[++i]);
					continue;
				}
				if (args[i].equals("-P"))
				{
					GUIServerPort = Integer.parseInt(args[++i]);
					continue;
				}
				if (args[i].equals("-h"))
				{
					System.err.println("Syntax is: SnakeServer [-s <width> <height>] [-p <GameServerPort>] [-P <GUIServerPort>");
					System.exit(0);
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Syntax is: SnakeServer [-s <width> <height>] [-p <GameServerPort>] [-P <GUIServerPort>");
			System.exit(0);
		}
		Players=new LinkedList<Player>();
		System.out.println("Please connect GUI to "+GUIServerPort);
		GUI = new GUIServer(GUIServerPort);
		PlayerServer PS = new PlayerServer(GameServerPort, Players);
		System.out.println("Now Players can connect to "+GameServerPort);
		GUI.MESSAGE("Welcome to Multi-Snake!!");
		GUI.MESSAGE("Now Players can connect to port: "+GameServerPort);
		int counter=0;
		while(true)
		{
			if (Players.size()!=0)
			{
				counter++;
				for(int p=0;p<Players.size();p++)
				{
					Player pl = Players.get(p);
					pl.doStep();
					Point rem = pl.removedPoint();
					Point neu = pl.newPoint();
					if (checkCollision(neu))
					{
						GUI.SET(rem.x, rem.y, 0);
						//Player collision so kill the player...
						LinkedList<Point> pis = pl.getPoints();
						for(int i=0;i<pis.size();i++)
						{
							GUI.SET(pis.get(i).x, pis.get(i).y, 0);
						}
						GUI.removePLAYER(pl.getColor());
						pl.die();
						Players.remove(p);
						p--;
					}
					else
					{
						GUI.SET(neu.x, neu.y, pl.getColor());
						if (counter==50)
						{
							pl.getPoints().addFirst(pl.removedPoint());
							pl.setPunkte(pl.getPunkte()+1);
							GUI.POINTS(pl.getColor(), pl.getPunkte());
						}
						else
						{
							GUI.SET(rem.x, rem.y, 0);
						}
					}
				}
				if (counter==50)
					counter=0;
			}
			GUI.FLUSH();//so we can detect GUI disconnection even if no Player connected...
			Misc.delay(200);
		}
	}

}
