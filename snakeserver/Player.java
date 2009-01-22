package snakeserver;
import java.awt.Point;
import java.io.BufferedReader;
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
	
	public void die()
	{
		isAlive=false;
		//sendString("FUCKOUY You sucked");
	}
	public int getColor()
	{
		return color;
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
	public Player(BufferedReader in, int x, int y, int color)
	{
		this.color=color;
		GAMEWIDTH=SnakeServer.GAMEWIDTH;
		GAMEHEIGHT=SnakeServer.GAMEHEIGHT;
		sockIn = in;
		Points = new LinkedList<Point>();
		Points.add(new Point(x, y+color));
		Points.add(new Point(x+1, y+color));
		Points.add(new Point(x+2, y+color));
		Points.add(new Point(x+3, y+color));
		Points.add(new Point(x+4, y+color));
		Points.add(new Point(x+5, y+color));
		Points.add(new Point(x+6, y+color));
		isAlive=true;
		new Thread(this).start();
	}
	public void doStep()
	{
		removedPoint=Points.get(0);
		Points.remove(0);
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
		while (isAlive)
		{
			try
			{
				String command = sockIn.readLine();
				System.out.println("Player got:"+command);
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
			catch (Exception e)
			{
				isAlive=false;
			}
		}
	}
}
