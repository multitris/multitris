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
	private static boolean checkCollision(Player p)
	{
		for (int i=0;i<Players.size();i++)
		{
			Player pp = Players.get(i);
				LinkedList<Point> pa = p.getPoints();
				LinkedList<Point> pb = pp.getPoints();
				for (int ipa=0;ipa<pa.size();ipa++)
					for (int ipb=0;ipb<pb.size();ipb++)
						if (pa.get(ipa).equals(pb.get(ipb))&&pa.get(ipa)!=pb.get(ipb))
							return true;
		}
		return false;
	}
	private static GUIServer GUI;
	public static void addPlayer(BufferedReader in, int x, int y)
	{
		int color=index;
		String col = Integer.toHexString(1048576 + (int)(Math.random() * 9437184.0));
		GUI.PLAYER(index, col, "Player "+index);
		Players.add(new Player(in, x, y, index));
		System.out.println("Player added.");
		index++;
	}
	public static void main(String[] args) 
	{	
		Players=new LinkedList<Player>();
		System.out.println("Please connect GUI to 12347");
		GUI = new GUIServer(12347);
		PlayerServer PS = new PlayerServer(12346, Players);
		System.out.println("Now Players can connect to 12346");
		int counter=0;
		while(true)
		{
			counter++;
			for(int p=0;p<Players.size();p++)
			{
				Player pl = Players.get(p);
				pl.doStep();
				Point rem = pl.removedPoint();
				Point neu = pl.newPoint();
				GUI.SET(rem.x, rem.y, 0);
				GUI.SET(neu.x, neu.y, pl.getColor());
				if (checkCollision(pl))
				{
					LinkedList<Point> pis = pl.getPoints();
					for(int i=0;i<pis.size();i++)
					{
						GUI.SET(pis.get(i).x, pis.get(i).y, 0);
					}
					pl.die();
					Players.remove(p);
					p--;
				}
				if (counter==10)
				{
					pl.getPoints().addLast(pl.removedPoint());
				}
			}
			GUI.FLUSH();
			Misc.delay(200);
			if (counter==100)
				counter=0;
		}
	}

}
