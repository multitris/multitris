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
	
	public static void addPlayer(Player p, String name)
	{
		String color = Misc.generateColor();
		if (name.equals(""))
			name="Player "+index;
		GUI.PLAYER(index, color, name);
		p.setColor(index);
		Players.add(p);
		for (int i=0;i<p.getPoints().size();i++)
		{
			GUI.SET(p.getPoints().get(i).x, p.getPoints().get(i).y,p.getColor());
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
		if (args.length>0)
		{
			try
			{
				int w = Integer.parseInt(args[0]);
				int h = Integer.parseInt(args[1]);
				GAMEWIDTH=w;
				GAMEHEIGHT=h;
			}
			catch (Exception e)
			{
				System.err.println("Syntax is: SnakeServer <Width> <Height>\n Using standard.");
			}
		}
		Players=new LinkedList<Player>();
		System.out.println("Please connect GUI to 12345");
		GUI = new GUIServer(12345);
		PlayerServer PS = new PlayerServer(12346, Players);
		System.out.println("Now Players can connect to 12346");
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
						if (counter==10)
						{
							pl.getPoints().addFirst(pl.removedPoint());
							
						}
						else
						{
							GUI.SET(rem.x, rem.y, 0);
						}
					}
				}
				GUI.FLUSH();
				if (counter==100)
					counter=0;
			}
			Misc.delay(200);
		}
	}

}
