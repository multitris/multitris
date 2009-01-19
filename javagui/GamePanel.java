package javagui;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.LinkedList;
import java.awt.Color;

public class GamePanel extends JPanel 
{
	private final int MAXMESSAGES = 5;
	private int wwidth= 1024;
	private int wheight= 768;
	private int nwidth = 100;//width of the names Panel;
	private int mheight = 100;//height of the Messages Panel;
	private int width = wwidth-nwidth;
	private int height = wheight-mheight;
	private int vwidth = 0;
	private int vheight = 0;
	private Color [][] pixmatrix;
	private String authstring;
	private LinkedList<Player> players;
	private String[] messages;
	private Color convertColor(String hexcolor)
	{
		int r = Integer.parseInt(hexcolor.substring(0, 2), 16);
		int g = Integer.parseInt(hexcolor.substring(2, 4), 16);
		int b = Integer.parseInt(hexcolor.substring(4, 6), 16);
		return new Color(r,g,b);
	}
	public void RESET(boolean field, boolean players, boolean colors, boolean points, boolean messages)
	{
		if (field)
		{
			vwidth=0;
			vheight=0;
		}
		if (points)
		{
			for (int i=0;i<this.players.size();i++)
				this.players.get(i).points=0;
		}
		if (players)
		{
			this.players.clear();
		}
		if (messages)
		{
			this.messages = new String[MAXMESSAGES];
		}
	}
	public void AUTH(String s)
	{
		authstring=s;
	}
	public void SIZE(int w, int h)
	{
		vwidth=w;
		vheight=h;
		pixmatrix = new Color[w][h];
		repaint();
	}
	public void COLOR(int player, String color)
	{
		for (int k=0;k<players.size();k++)
			if (players.get(k).ID==player)
			{
				players.get(k).color=convertColor(color);
				return;
			}
		Player p=new Player(player, "");
		p.color=convertColor(color);
		players.add(p);
	}
	public void PLAYER(int n, String name)
	{
		for (int i=0;i<players.size();i++)
		{
			if (players.get(i).ID==n)
			{
				players.get(i).name=name;
				return;
			}
		}
		players.add(new Player(n, name));
	}
	public void SET(int row, int column, int color)
	{
		for(int i=0;i<players.size();i++)
			if (players.get(i).ID==color)
			{
				pixmatrix[column][row]=players.get(i).color;
				break;
			}
	}
	public void MESSAGE(String txt)
	{
		for(int i=messages.length-1;i>0;i--)
			messages[i]=messages[i-1];
		messages[0]=txt;
	}
	public void POINTS(int n, int p)
	{
		for (int k=0;k<players.size();k++)
			if (players.get(k).ID==n)
			{
				players.get(k).points=p;
				break;
			}
	}
	public void FLUSH()
	{
		this.repaint();
	}
	public GamePanel()
	{
		this.setSize(width,height);
		pixmatrix = new Color [vwidth] [vheight];
		players=new LinkedList<Player>();
		messages=new String[MAXMESSAGES];
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
		
		int y=0;
		for (int i=0;i<players.size();i++)
			if(players.get(i).name!="")
			{
				g.setColor(players.get(i).color);
				g.drawString(players.get(i).name, width+5, 10+25*(y++));
			}
		
		y=0;
		g.setColor(new Color(0,0,0));
		for (int i=0;i<MAXMESSAGES;i++)
			if (messages[i]!=null)
				g.drawString(messages[i], 5, height+10+25*(y++));
	}
}
