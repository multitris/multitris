package multitrisserver;
import java.util.LinkedList;

public class FieldObserver
{
	private LinkedList<FieldObserverChange> changes;
	private int width;
	private int height;
	
	public FieldObserver(int height, int width)
	{
		this.changes = new LinkedList<FieldObserverChange>();
		this.width = width;
		this.height = height;
	}
	
	public void stoneChanged(Stone newStone, Stone oldStone)
	{
		int[][] oldPixels = oldStone.getAbsolutePixels();
		int[][] newPixels = newStone.getAbsolutePixels();
		
		for(int i=0;i<oldPixels.length;i++)
		{
			if(oldPixels[i][0] > -1 && oldPixels[i][0] < this.height && oldPixels[i][1] > -1 && oldPixels[i][1] < this.width)
				this.addChange(new FieldObserverChange(oldPixels[i][0], oldPixels[i][1], 0, false));
		}
		
		for(int i=0;i<newPixels.length;i++)
		{
			if(newPixels[i][0] > -1 && newPixels[i][0] < this.height && newPixels[i][1] > -1 && newPixels[i][1] < this.width)
				this.addChange(new FieldObserverChange(newPixels[i][0], oldPixels[i][1], newStone.getColor(), false));
		}
	}
	
	public void fixedPixelChanged(int y, int x, int col, boolean exploding)
	{
		this.addChange(new FieldObserverChange(y, x, col, exploding));
	}

	private void addChange(FieldObserverChange change)
	{
		for(int i=0;i<this.changes.size();i++)
		{
			if(this.changes.get(i).getX() == change.getX() && this.changes.get(i).getY() == change.getY())
			{
				// override unless new color is zero
				if(change.getColor() != 0)
					this.changes.get(i).setColor(change.getColor());
				this.changes.get(i).setExploding(this.changes.get(i).getExploding() || change.getExploding());
				return;
			}
		}
		
		this.changes.add(change);
	}
					
	public void flush(GUIServer gui)
	{
		while(this.changes.size() != 0)
		{
			FieldObserverChange change = this.changes.get(0);
			gui.SET(change.getY(), change.getX(), change.getColor(), change.getExploding());
			System.out.println("SET " + change.getY() + " " + change.getX() + " " + change.getColor() + (change.getExploding() ? " explode" : "")); // debug
			this.changes.remove(0);
		}
		gui.FLUSH();
	}
}
