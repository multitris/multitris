package javagui;

import javax.swing.JFrame;
import java.util.Random;
import java.awt.Dimension;
import java.awt.event.*;
import common.*;

public class MainWindow extends JFrame implements Runnable
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
	private static void drawChar(int x, int y, char a, int color, GamePanel gp)
	{
		switch (a)
		{
			case 'M':
			{
				gp.SET(x+1, y+1, color, "");
				gp.SET(x+2, y+2, color, "");
				gp.SET(x+3, y+1, color, "");
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'U':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'L':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				break;
			}
			case 'I':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+1, y+k, color, "");
				}
				break;
			}
			case 'Y':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, k>=2?y+4-k:y+k, color, "");
				}
				for (int k=3;k<5;k++)
				{
					gp.SET(x+2, y+k, color, "");
				}
				break;
			}
			case 'T':
			{
				for (int k=0;k<5;k++)
					gp.SET(x+k, y, color, "");
				for (int k=0;k<5;k++)
				{
					gp.SET(x+2, y+k, color, "");
				}
				break;
			}
			case 'A':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+2, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'H':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y+2, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'E':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+2, color, "");
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				break;
			}
			case 'S':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+2, color, "");
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(k<3?x:x+4, y+k, color, "");
				}
				break;
			}
			case 'N':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y+k, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'D':
			{
				for (int k=0;k<4;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				for (int k=1;k<4;k++)
				{
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'O':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case ',':
			{
				gp.SET(x, y+4, color, "");
				gp.SET(x+1, y+3, color, "");
				break;
			}
			case 'K':
			{
				for(int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				gp.SET(x+1, y+2, color, "");
				gp.SET(x+2, y+3, color, "");
				gp.SET(x+2, y+1, color, "");
				gp.SET(x+3, y+4, color, "");
				gp.SET(x+3, y+0, color, "");
				break;
			}
			case 'J':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					if (k>2)
						gp.SET(x, y+k, color, "");
					gp.SET(x+4, y+k, color, "");
				}
				break;
			}
			case 'P':
			{
				for (int k=0;k<5;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+2, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				gp.SET(x+4, y+1, color, "");
				break;
			}
			case 'R':
			{
				for (int k=0;k<4;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+2, color, "");
					gp.SET(x+k, y+k+1, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				gp.SET(x+3, y+1, color, "");
				break;
			}
			case 'B':
			{
				for (int k=0;k<4;k++)
				{
					gp.SET(x+k, y, color, "");
					gp.SET(x+k, y+2, color, "");
					gp.SET(x+k, y+4, color, "");
				}
				for (int k=0;k<5;k++)
				{
					gp.SET(x, y+k, color, "");
				}
				gp.SET(x+3, y+1, color, "");
				gp.SET(x+3, y+3, color, "");
				break;
			}
		}
	}
	private static void drawString(int x, int y, String s, int color, GamePanel gp)
	{
		char[] ca = s.toCharArray();
		for (int c=0;c<ca.length;c++)//c++ ha ha ha.... man kann also in Java C++ schreiben,....
			drawChar(x+6*c, y, ca[c], color, gp);
	}
	public void run()
	{
		gp.RESET(true, true, true, true, true);
		gp.SIZE(100,100);
		gp.COLOR(1, "ff0000");
		gp.COLOR(2, "00ff00");
		gp.COLOR(3, "0000ff");
		gp.COLOR(4, "abcdef");
		for (int x = 100;x>-180;x--)
		{
			gp.RESET(true, false, false, false, false);
			drawString(x, 10, "MULTITRIS", 4, gp);
			drawString(x+30, 30, "BY ANDREAS K,", 1, gp);
			drawString(x+60, 50, "JOHANNES K,", 2, gp);
			drawString(x+90, 70, "AND THOMAS O", 3, gp);
			
			if(x==100)
				gp.MESSAGE("Well, this is the Story of Multitris...");
			if(x==80)
				gp.MESSAGE("First, there was the idea....");
			if(x==60)
			{
				gp.MESSAGE("Then there was Andreas K");
				gp.PLAYER(1, "Andreas_K");
			}
			if(x==40)
			{
				gp.MESSAGE("and Johannes K joined the game...");
				gp.PLAYER(2, "Johannes_K");
			}
			if(x==20)
			{
				gp.MESSAGE("and Thomas O joined the game...");
				gp.PLAYER(3, "Thomas_O");
			}
			gp.FLUSH();
			delay(100);
		}
		
		gp.MESSAGE("Welcome all the Players, let's rock!");
		gp.FLUSH();
		delay(1000);
		for (int i=0;i<100;i++)
		{
			for (int x=0;x<100;x++)
				for(int y=0;y<100;y++)
					gp.SET(x, y, Misc.random(0,3), "");
			gp.FLUSH();
			delay(100);
		}
		gp.RESET(true, false, false, false, false);
		gp.FLUSH();
	}
	public static void main(String[] args)
	{
		GamePanel gp = new GamePanel();
		MainWindow MW = new MainWindow(gp);
		gp.SIZE(20,10);//dummy
		gp.COLOR(1, "ff0000");
		if (args.length==0)
		{
			MultiRequester MR = new MultiRequester(new String[]{"URL", "Port"}, 
												new String[]{"localhost", "12345"},
												"Bitte Pfad zum Gameserver angeben.");
			args = MR.getResult();
		}
		String URL="";
		int Port=0;
		try
		{
			URL = args[0];
			Port = Integer.parseInt(args[1]);
		}
		catch(Exception e)
		{
			if (args!=null)
				System.out.println("Syntax is javagui.MainWindow[ <URL> <Port>]");
			System.exit(0);
		}
		Thread DemoThread=new Thread(MW);
		DemoThread.start();
		while (true)
		{
			if(DemoThread.getState()==Thread.State.TERMINATED)
				(DemoThread=new Thread(MW)).start();
			Client c = new Client(gp, URL, Port);
			c.loop(DemoThread);
			Misc.delay(3000);
		}
	}
}
