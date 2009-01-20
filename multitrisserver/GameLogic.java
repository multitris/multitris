package multitrisserver;
import java.util.LinkedList;

public class GameLogic
{
	private LinkedList<Stone> stones;
	private int width;
	private int height;
	private int [] [] fixedPixels;
	// manages all Tetris blocks which cannot be moved any more. Enumerated as described in server/gui-protocoll (first index: row, starting from top to bottom, second index: col, starting from left to right). Contains 0 if there is no permanent block at this position, otherwise it contains the corresponding color id.
	private FieldObserver fieldObserver; // tell him all changes of the field, and he will be your friend
	private GUIServer gui;
	
	public GameLogic()
	{
		this.stones = new LinkedList<Stone>();
		this.width = 20; // debug value, should be bigger
		this.height = 10; // debug value, should be bigger
		this.fixedPixels = new int[this.height][];
		for(int row=0;row<this.height;row++)
		{
			this.fixedPixels[row] = new int[this.width];
			for(int col=0;col<this.width;col++)
				this.fixedPixels[row][col] = 0;
		}
		this.fieldObserver = new FieldObserver(this.height, this.width);
		this.gui = new GUIServer(2345);
	}
	
	public void shutDown()
	{
		this.gui.close();
	}
	
	private void debug_printField() // pseudo-gui, for debugging purposes only
	{
		char[][] pixels = new char[this.height][];
		for(int row=0;row<this.height;row++)
		{
			pixels[row] = new char[this.width];
			for(int col=0;col<this.width;col++)
			{
				pixels[row][col] = (this.fixedPixels[row][col] == 0 ? '.' : Integer.toString(this.fixedPixels[row][col]).charAt(0));
			}
		}
		
		char stoneName = 'A';
		
		for(int i=0;i<this.stones.size();i++)
		{
			int[][] absPixels = this.stones.get(i).getAbsolutePixels();
			for(int j=0;j<absPixels.length;j++)
			{
				if(-1 < absPixels[j][0] && absPixels[j][0] < this.height && -1 < absPixels[j][1] && absPixels[j][1] < this.width)
					pixels[absPixels[j][0]][absPixels[j][1]] = stoneName;
			}
			stoneName++;
		}
		
		for(int row=0;row<pixels.length;row++)
		{
			for(int col=0;col<pixels[row].length;col++)
			{
				System.out.print(pixels[row][col]);
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	public boolean tryToMoveLeft(Stone s)
	{
		return this.tryToMove(s, 0, -1);
	}
	
	public boolean tryToMoveRight(Stone s)
	{
		return this.tryToMove(s, 0, 1);
	}
	
	public boolean tryToMoveDown(Stone s)
	{
		return this.tryToMove(s, 1, 0);
	}
	
	private boolean tryToMove(Stone s, int deltaY, int deltaX)
	{
		Stone newStone = s.clone();
		newStone.move(deltaY, deltaX);
		
		if(this.validTransformation(newStone, s))
		{
			this.fieldObserver.stoneChanged(newStone, s);
			s.move(deltaY, deltaX);
			this.fieldObserver.flush(this.gui);
			return true;
		}
		return false;
	}
	
	public boolean tryToRotate(Stone s)
	{
		Stone newStone = s.clone();
		newStone.rotate();
		
		if(this.validTransformation(newStone, s))
		{
			this.fieldObserver.stoneChanged(newStone, s);
			s.rotate();
			this.fieldObserver.flush(this.gui);
			return true;
		}
		return false;
	}
	
	private boolean validTransformation(Stone newStone, Stone oldStone)
	{
		if(newStone.getX() < 0 || (newStone.getX() + newStone.getWidth()) > this.width || (newStone.getY() + newStone.getHeight()) > this.height)
			return false;
		
		if(newStone.collidesWith(this.fixedPixels))
			return false;
		
		for(int k=0;k<this.stones.size();k++)
		{
			Stone compareWith = stones.get(k);
			
			if(compareWith != oldStone)
			{
				if(newStone.collidesWith(compareWith))
					return false;
			}
		}
		
		return true;
	}
	
	private void moveAllDown()
	{
		// we have to do a little bit of tricky stuff here. imagine the following two blocks
		// AAB
		// ABB
		// AAB
		// the blocks cannot be moved one after another with tryToMove because they would overlap between the two tryToMove calls.
		// furthermore, if stone A hits the ground, you have to ensure stone B gets fixed too (and vice versa).
		
		// first, see which stones directly hit the ground, and transform them into fixed pixels.
		// if any stones are affected, repeat.
		
		boolean repeatCheck = true;
		while(repeatCheck)
		{
			repeatCheck = false;
			
			for(int i=0;i<this.stones.size();i++)
			{
				Stone s = stones.get(i);
				
				Stone tryMove = s.clone();
				tryMove.move(1, 0);
				
				if(tryMove.collidesWith(this.fixedPixels) || (tryMove.getY() + tryMove.getHeight()) > this.height)
				{
					repeatCheck = true;
					this.convertStoneToFixedPixels(s);
					// will be deleted out of this.stones
					i--;
				}
			}
		}
		
		// the remaining stones can definitely be moved down. now do so!
		
		for(int i=0;i<this.stones.size();i++)
		{
			Stone movedStone = this.stones.get(i).clone();
			movedStone.move(1, 0);
			this.fieldObserver.stoneChanged(movedStone, this.stones.get(i));
			this.stones.get(i).move(1, 0);
		}
	}
	
	public void convertStoneToFixedPixels(Stone s)
	{
		int[][] absoluteStonePixels = s.getAbsolutePixels();
		
		for(int i=0;i<absoluteStonePixels.length;i++)
		{
			int[] coords = absoluteStonePixels[i];
			if(coords[0] > -1 && coords[0] < this.height && coords[1] > -1 && coords[1] < this.width) // except for coords[0] > -1, this should always be the case!
			{
				this.fixedPixels[coords[0]][coords[1]] = s.getColor();
				this.fieldObserver.fixedPixelChanged(coords[0], coords[1], s.getColor(), false);
			}
		}
		
		// TODO: Tell Stone and player it does not exist any more
		this.stones.remove(s);
	}
	
	private void gameStep()
	{
		this.moveAllDown();
		this.removeCompletedRows();
		this.fieldObserver.flush(this.gui);
		
		// check gameOver
		boolean gameOver = false;
		for(int col=0;col<this.width;col++)
			gameOver = gameOver || (this.fixedPixels[0][col] != 0);
		
		if(gameOver)
			this.gameOver();
		else
		{
			// new stone?
			
			if(Math.random() < 0.25)
			{
				Stone tryThisStone = Stone.randomStone();
				tryThisStone.setY(1-tryThisStone.getHeight());
				tryThisStone.setX((int)(((double)(this.width-tryThisStone.getWidth()))*Math.random()));
				
				if(this.validTransformation(tryThisStone, tryThisStone)) // call to see whether it fits
				{
					this.insertStone(tryThisStone);
				}
			}
		}
	}
	
	private void removeCompletedRows()
	{
		// there are situations in which rows cannot be removed although they are completed. imagine the following situation:
		//
		//           *******
		//           *******
		// *          BBB  *
		// *  ** *  *      *
		// *AA**************
		// *AA**************
		//
		// when the A stone is turned into fixed pixels, stone A completes two rows. if they were removed directly, stone B would collide with the fixed pixels at the very top. therefore we have to check whether the rows can really be removed. don't be sad if not, it will probably happen the next time removeCompletedRows() is called.
		
		for(int row=this.height-1;row>=0;row--)
		{
			// complete?
			boolean complete = true;
			for(int col=0;col<this.width;col++)
			{
				if(this.fixedPixels[row][col] == 0)
				{
					complete = false;
					break;
				}
			}
			
			if(complete)
			{
				// remove row and insert new one
				int[][] newFixedPixels = new int[this.height][];
				newFixedPixels[0] = new int[this.width];
				for(int col=0;col<this.width;col++)
					newFixedPixels[0][col] = 0;
				
				for(int copyrow=1;copyrow<this.height;copyrow++)
				{
					newFixedPixels[copyrow] = this.fixedPixels[copyrow <= row ? (copyrow-1) : copyrow];
				}
				
				// now let's see whether we can remove it
				
				boolean canRemove = true;
				for(int i=0;i<this.stones.size();i++)
				{
					if(this.stones.get(i).collidesWith(newFixedPixels))
					{
						canRemove = false;
						break;
					}
				}
				
				if(canRemove)
				{
					this.fixedPixels = newFixedPixels;
					// let's do some ugly stuff :)
					for(int updateRow=row;updateRow<this.height;updateRow++)
					{
						for(int updateCol=0;updateCol<this.width;updateCol++)
							this.fieldObserver.fixedPixelChanged(updateRow, updateCol, newFixedPixels[updateRow][updateCol], (updateRow==row));
					}
					for(int i=0;i<this.stones.size();i++)
						this.fieldObserver.stoneChanged(this.stones.get(i), this.stones.get(i)); // yes he HAS changed! I'm totally sure! ;)
					
					row++; // re-check current row
				}
			}
		}
	}
	
	private void insertStone(Stone s)
	{
		this.stones.add(s);
		this.fieldObserver.stoneChanged(s, s);
	}
	
	public void debug_insertStone(Stone s) // for testing purposes only
	{
		this.insertStone(s);
	}
	
	private void gameOver()
	{
		this.gui.MESSAGE("Ohhhh you lost"); // TODO: stop the game ;)
	}
	
	public static void main(String[] args)
	{
		GameLogic GL = new GameLogic();
		
		GL.debug_printField();
		/*boolean[][] matrix = new boolean[][] {{true,true,true},{false,true,false}};
		Stone s = new Stone(matrix);
		s.setX(2);
		s.setY(1);
		GL.debug_insertStone(s);
		GL.debug_printField();
		
		GL.tryToRotate(s);
		GL.debug_printField();
		
		GL.tryToRotate(s);
		GL.debug_printField();
		
		GL.tryToRotate(s);
		GL.debug_printField();
		
		GL.tryToMoveDown(s);
		GL.debug_printField();
		
		boolean[][] matrix2 = new boolean[][] {{true,true,true},{true,false,true}};
		Stone second = new Stone(matrix2);
		
		second.setX(1);
		second.setY(2);
		second.rotate();
		GL.debug_insertStone(second);
		
		GL.debug_printField();
		
		boolean[][] matrix3 = new boolean[][] {{true},{true},{true}};
		Stone balken = new Stone(matrix3);
		
		balken.setX(0);
		balken.setY(-2);
		
		// GL.debug_insertStone(balken);
		
		boolean[][] pixelmatrix = new boolean[][] {{true}};
		
		for(int y=1;y>-14;y-=4)
		{
			Stone pixelStone = new Stone(pixelmatrix);
			pixelStone.setX(0);
			pixelStone.setY(y);
			GL.debug_insertStone(pixelStone);
		}
		
		
		for(int i=4;i<12;i++)
		{
			balken = new Stone(matrix3);
			balken.setX(i);
			balken.setY(3-(i/3));
			
			GL.debug_insertStone(balken);
		} */
		
		try {
			for(int i=0;i<60;i++)
			{
				GL.gameStep();
				GL.debug_printField();
				Thread.sleep(1000);
			}
		} catch(Exception e) {System.out.println(e);}
		
		GL.shutDown();
	}
}
