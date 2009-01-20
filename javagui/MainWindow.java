package javagui;

import javax.swing.JFrame;
import java.util.Random;
import java.awt.Dimension;
import java.awt.event.*;

public class MainWindow extends JFrame
{
	private GamePanel gp;
	public MainWindow(GamePanel gp)
	{
		this.setContentPane(gp);
		this.setSize(1024,768);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.gp=gp;
	}
	public static int rnd(int min, int max)
	{
        Random rn = new Random();
        int n = max - min + 1;
        int i = rn.nextInt() % n;
        if (i < 0)
                i = -i;
        return min + i;
	}
	public static void delay(int ms)
	{
		try
		{	
			Thread.sleep(ms);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	public static void demo(GamePanel gp)
	{
		gp.SIZE(100,100);
		gp.COLOR(1, "ff0000");
		gp.COLOR(2, "00ff00");
		gp.COLOR(3, "0000ff");
		gp.FLUSH();
		delay(2000);
		gp.COLOR(4, "000000");
		gp.PLAYER(4, "Peter");
		gp.MESSAGE("Peter joined the Game");
		gp.FLUSH();
		delay(2000);
		gp.PLAYER(5, "Parker");
		gp.COLOR(5, "aa0037");
		gp.MESSAGE("Parker joined the Game");
		gp.FLUSH();
		delay(2000);
		gp.MESSAGE("Welcome all the Players, let's rock!");
		gp.SET(0,0,2, "");
		gp.SET(0, 1, 3, "");
		gp.FLUSH();
		gp.MESSAGE("Now we'll let it rand a little bit...");
		gp.FLUSH();
		delay(1000);
		for (int i=0;i<30;i++)
		{
			for (int x=0;x<100;x++)
				for(int y=0;y<100;y++)
					gp.SET(x, y, rnd(0,5), "");
			gp.FLUSH();
			delay(100);
		}
		gp.MESSAGE("Fertig");
		gp.FLUSH();
		gp.RESET(true, false, false, false, false);
		for (int x=0;x<100;x+=3)
		{
				int end = ((x%2)==0)?100:-1;
				int pm = ((x%2)==0)?1:-1;
				for(int y=((x%2)==0)?0:99;y!=end;y+=pm)
				{
					gp.SET(x, y, 1, "");
					gp.FLUSH();
					delay(10);
				}
		}
	}
	public static void main(String[] args)
	{
		GamePanel gp = new GamePanel();
		MainWindow MW = new MainWindow(gp);
		Client c = new Client(gp, "localhost", 12345);
		c.loop();
		demo(gp);
	}
}
