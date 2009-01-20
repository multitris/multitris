package javagui;

import javax.swing.JFrame;
import java.util.Random;
public class MainWindow extends JFrame 
{
	public MainWindow(GamePanel gp)
	{
		this.setContentPane(gp);
		this.setSize(1024,768);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
	public static void main(String[] args)
	{
		GamePanel gp = new GamePanel();
		MainWindow MW = new MainWindow(gp);
		gp.SIZE(20,20);
		gp.COLOR(1, "0000ff");
		gp.COLOR(2, "ff0000");
		gp.COLOR(3, "00FF00");
		gp.FLUSH();
		delay(2000);
		gp.PLAYER(3, "Peter");
		gp.MESSAGE("Peter joined the Game");
		gp.FLUSH();
		delay(2000);
		gp.PLAYER(4, "Parker");
		gp.COLOR(4, "aa0037");
		gp.MESSAGE("Parker joined the Game");
		gp.FLUSH();
		delay(2000);
		gp.MESSAGE("Welcome all the Players, let's rock!");
		gp.SET(0,0,2);
		gp.SET(0, 1, 3);
		gp.FLUSH();
		gp.MESSAGE("Now we'll let it rand a little bit...");
		gp.FLUSH();
		delay(1000);
		for (int i=0;i<30;i++)
		{
			for (int x=0;x<20;x++)
				for(int y=0;y<20;y++)
					gp.SET(x, y, rnd(1,3));
			gp.FLUSH();
			delay(100);
		}
		gp.MESSAGE("Fertig");
		gp.FLUSH();
		//Server s = new Server(gp);
		//s.loop();
	}
}
