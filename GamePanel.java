package multitris;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.LinkedList;
import java.awt.Color;

public class GamePanel extends JPanel 
{
	private LinkedList<Stone> stones;
	public GamePanel(LinkedList<Stone> stones)
	{
		this.setSize(800,600);
		this.stones = stones;
	}
	public void paint(Graphics g)
	{
		g.clearRect(0, 0, 800, 600);
		for (int i=0;i<stones.size();i++)
		{
			Stone s = stones.get(i);
			int r=255;//dummy here the Color String of the Stone Object will be converted to 3 Ints RGB.
			int gr=0;
			int b=0;
			g.setColor(new Color(r, gr, b));
			boolean [][] m = s.getPixelMatrix();
			for (int x=0;x<m.length;x++)
				for (int y=0;y<m[x].length;y++)
					if (m[x][y])
						switch (s.getRotation())
						{
							case 0:
							{
								g.drawRect(s.getX()*10+10*x, s.getY()*10+10*y, 10, 10);
								break;
							}
							case 1:
							{
								g.drawRect(s.getX()*10-10*x, s.getY()*10+10*y, 10, 10);
								break;
							}
							case 2:
							{
								g.drawRect(s.getX()*10-10*x, s.getY()*10-10*y, 10, 10);
								break;
							}
							case 3:
							{
								g.drawRect(s.getX()*10+10*x, s.getY()*10-10*y, 10, 10);
								break;
							}
						}
		}
	}
}
