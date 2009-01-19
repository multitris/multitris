package multitris;
import java.util.LinkedList;
import java.util.Random;

public class GameLogic
{
	private LinkedList<Stone> stones;
	private GamePanel GP;
	public void add(Stone s)
	{
		stones.add(s);
	}
	public Stone add(int typ, String color, int rotation)
	{
		Stone s = new Stone(typ, color, rotation);
		this.add(s);
		return s;
	}
	public void repaint()
	{
		GP.repaint();
	}
	public GameLogic()
	{
		stones = new LinkedList<Stone>();
		stones.add(new Stone(0,"FF0000",0));
		Stone tmp = new Stone(1, "220033", 2);
		tmp.setX(10);
		stones.add(tmp);
		GP = new GamePanel(stones);
		MainWindow MW = new MainWindow(GP);
	}
	public void down()
	{
		for (int k=0;k<stones.size();k++)
		{
			Stone s = stones.get(k);
			if (!s.down)
			{
				s.setY(s.getY()+1);
				if (s.getY()==60)
					s.down=true;
			}
		}
	}
	
    public static int rand(int lo, int hi)
    {
    		Random rn = new Random();
            int n = hi - lo + 1;
            int i = rn.nextInt() % n;
            if (i < 0)
                    i = -i;
            return lo + i;
    }


	public static void main(String[] args)
	{
		GameLogic GL = new GameLogic();
		while (true)
		{
			for (int i=0;i<20;i++)
			{
				try
				{
					Thread.sleep(200);
				}
				catch (Exception e)
				{}
				GL.down();
				GL.repaint();
			}
			GL.add(rand(0,2), "", rand(0,3)).setX(rand(0, 70));
			GL.add(rand(0,2), "", rand(0,3)).setX(rand(0, 70));
			GL.add(rand(0,2), "", rand(0,3)).setX(rand(0, 70));
		}
	}
}
