package javagui;

import javax.swing.JFrame;

public class MainWindow extends JFrame 
{
	public MainWindow(GamePanel gp)
	{
		this.setContentPane(gp);
		this.setSize(1024,768);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public static void main(String[] args)
	{
		GamePanel gp = new GamePanel();
		gp.SIZE(100,100);
		gp.COLOR(2, "ff0000");
		gp.COLOR(3, "00FF00");
		gp.PLAYER(3, "Peter");
		gp.MESSAGE("Hello World");
		gp.SET(0,0,2);
		gp.SET(0, 1, 3);
		MainWindow MW = new MainWindow(gp);
		gp.FLUSH();
		//Server s = new Server(gp);
		//s.loop();
	}
}
