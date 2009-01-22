package snakeserver;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

public class Player implements Runnable
{
	private int GAMEWIDTH;
	private int GAMEHEIGHT;
	private LinkedList<Point> Points;
	private Point removedPoint;
	private Point newPoint;
	private BufferedReader sockIn;
	private boolean isAlive;
	private int direction;
	private int color;
	private OutputStreamWriter out;
	
	public void sendString(String s)
	{
		try
		{
			out.write(s+"\n");
			out.flush();
		}
		catch (Exception e)
		{
			System.out.println("Error sendingString in Player.sendString: "+e);
		}
	}
	public void die()
	{
		isAlive=false;
		sendString("FUCKYOU You sucked");
	}
	public int getColor()
	{
		return color;
	}
	public void setColor(int color)
	{
		this.color=color;
	}
	public LinkedList<Point> getPoints()
	{
		return Points;
	}
	public Point newPoint()
	{
		return newPoint;
	}
	public Point removedPoint()
	{
		return removedPoint;
	}
	public Player(BufferedReader in, OutputStreamWriter out, int x, int y)
	{
		this.out=out;
		GAMEWIDTH=SnakeServer.GAMEWIDTH;
		GAMEHEIGHT=SnakeServer.GAMEHEIGHT;
		sockIn = in;
		Points = new LinkedList<Point>();
		for (int k=0;k<=6;k++)
			Points.add(new Point(x+k, y+color));
		isAlive=true;
		new Thread(this).start();
		direction=0;
	}
	public void doStep()
	{
		removedPoint=Points.getFirst();
		Points.remove(removedPoint);
		Point last = Points.getLast();
		Point pnew=null;
		switch (direction)
		{
			case 0:
			{
				pnew = new Point(last.x+1, last.y);
				if (pnew.x>=GAMEWIDTH)
					pnew.x=0;
				break;
			}
			case 1:
			{
				pnew = new Point(last.x, last.y-1);
				if (pnew.y<0)
					pnew.y=GAMEHEIGHT-1;
				break;
			}
			case 2:
			{
				pnew = new Point(last.x-1, last.y);
				if (pnew.x<0)
					pnew.x=GAMEWIDTH-1;
				break;
			}
			case 3:
			{
				pnew = new Point(last.x, last.y+1);
				if (pnew.y>=GAMEHEIGHT)
					pnew.y=0;
				break;
			}
		}
		Points.addLast(pnew);
		newPoint=pnew;
	}
	public void run()
	{
		boolean WartePhase=true;
		while (isAlive)
		{
			try
			{
				String packet = sockIn.readLine();
				String command = packet.split(" ", 3)[0];
				if (WartePhase)
				{
					if (command.equals("IWANTFUN"))
					{
						SnakeServer.addPlayer(this, packet.split(" ", 3)[2]);
						WartePhase=false;
						sendString("ATTENTION ff0000 1");
						sendString("GOFORREST");
					}
				}
				else
				{
					int tmpdirection = direction;
					if (command.equals("STOIBER"))
						tmpdirection--;
					if (command.equals("LAFONTAINE"))
						tmpdirection++;
					if (tmpdirection==4)
						tmpdirection=0;
					if (tmpdirection==-1)
						tmpdirection=3;
					direction=tmpdirection;
				}
			}
			catch (Exception e)
			{
				isAlive=false;
				SnakeServer.removePlayer(this);
			}
		}
	}
}
