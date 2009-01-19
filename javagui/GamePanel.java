package javagui;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.LinkedList;
import java.awt.Color;

public class GamePanel extends JPanel 
{
	private Color [][] pixmatrix;
	private int width = 1024;
	private int height = 768;
	private int vwidth = 20;
	private int vheight = 60;
	public GamePanel()
	{
		this.setSize(width,height);
		pixmatrix = new Color [vwidth] [vheight];
	}
	public void paint(Graphics g)
	{
		g.clearRect(0, 0, width, height);
		for (int x=0;x<pixmatrix.length;x++)
			for (int y=0;y<pixmatrix.length;y++)
			{
				if (pixmatrix[x][y]!=null)
				{
					g.setColor(pixmatrix[x][y]);
					g.fillRect(x*(width/vwidth), y*(height/vheight), (width/vwidth), (width/vwidth));
				}
			}
	}
}
