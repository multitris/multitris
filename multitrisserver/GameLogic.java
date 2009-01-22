package multitrisserver;
import java.util.LinkedList;
import java.util.Date;

public class GameLogic
{
	private LinkedList<Stone> stones;
	private int width;
	private int height;
	private int [] [] fixedPixels;
	// manages all Tetris blocks which cannot be moved any more. Enumerated as described in server/gui-protocoll (first index: row, starting from top to bottom, second index: col, starting from left to right). Contains 0 if there is no permanent block at this position, otherwise it contains the corresponding color id.
	private FieldObserver fieldObserver; // tell him all changes of the field, and he will be your friend
	private GUIServer gui;
	private boolean goOnPlaying=true;
	private long nextGameStep = 0;
	private long nextGameSpeedIncrease = 0;
	private PlayerManager playerManager = null;
	private int currentSpeed;
	
	/* CONFIG *****************/
	private static final int DEFAULTWIDTH = 20;
	private static final int DEFAULTHEIGHT = 30;
	private static final int PLAYNUMBEROFGAMES = 10; // after this number of games played, the game will terminate. zero = play forever (yeeha!)
	private static final int SLEEPBETWEENGAMES = 15; // number of seconds between the end of one game and the beginning of the next one
	private static final int DEFAULTSPEED = 500; // milliseconds between two gamesteps (i.e. two moveAllDown()s); smaller number = faster game
	private static final int SLEEPINGTIME = 50; // milliseconds between two steps (i.e. processing user input etc.); smaller number = quicker reaction
	private static final int GUIPORT = 12345;
	private static final int CLIENTSPORT = 12346;
	private static final int INCREASESPEEDABOUT = 50; // this value will be substracted from the current speed if the players are too good ;) can be zero for no gamespeed regulation
	private static final int INCREASESPEEDAFTER = 45; // gamespeed regulation time rate: every X seconds the gamespeed is increased
	private static final int WINNINGSPEED = 200; // if gameOver is called, print winning message if the current gamespeed is at least as fast as this. may be zero to frustrate clients (no winning message will be printed).
	private static final double NEWSTONEPROPABILITY = 0.4; // maximum propability of inserting a new stone into the game in one gamestep (depends on available space / available players)
	/* END CONFIG *************/
	
	public GameLogic()
	{
		this.stones = new LinkedList<Stone>();
		this.width = DEFAULTWIDTH;
		this.height = DEFAULTHEIGHT;
		
		System.out.println("Connect your GUI to port "+GUIPORT+", please :)");
		this.gui = new GUIServer(GUIPORT);
		System.out.println("Thx. Game is started. Clients are welcome: Use port "+CLIENTSPORT+".");
		this.playerManager = new PlayerManager(this, CLIENTSPORT);
	}
	
	public void startGame()
	{
		this.currentSpeed = DEFAULTSPEED;
		this.goOnPlaying = true;
		this.nextGameSpeedIncrease = (new Date()).getTime() + 1000*INCREASESPEEDAFTER;
		this.fixedPixels = new int[this.height][];
		this.stones.clear();
		for(int row=0;row<this.height;row++)
		{
			this.fixedPixels[row] = new int[this.width];
			for(int col=0;col<this.width;col++)
				this.fixedPixels[row][col] = 0;
		}
		this.fieldObserver = new FieldObserver(this.height, this.width);
		
		this.gui.AUTH("foobar");
		this.gui.SIZE(this.width, this.height);
		this.gui.RESET(true, true, true, true, true);
		
		this.playerManager.cleanUp();
		this.playerManager.listGui(this.gui);
		
		this.gui.FLUSH();
		
		while(this.goOnPlaying)
		{
			this.step(); // here we go
			
			try
			{
				Thread.sleep(SLEEPINGTIME);
			}
			catch(Exception e)
			{
				System.err.println(e);
			}
		}
	}
	
	public void shutDown()
	{
		this.playerManager.goodbyeMyFriends();
		this.playerManager.shutDown();
		this.gui.close();
	}
	
	public void takeOverControl()
	{
		for(int currentGameNo=0;(PLAYNUMBEROFGAMES==0 || currentGameNo<PLAYNUMBEROFGAMES);currentGameNo++)
		{
			this.startGame();
			
			if(PLAYNUMBEROFGAMES == 0 || (currentGameNo+1)<PLAYNUMBEROFGAMES)
			{
				this.gui.MESSAGE("Prepare for the next game, it will start in " + SLEEPBETWEENGAMES + " seconds...");
				this.gui.FLUSH();
				for(int secondsWaited=0;secondsWaited<SLEEPBETWEENGAMES;secondsWaited++)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch(Exception e)
					{
						System.err.println(e);
					}
					this.playerManager.step();
				}
			}
		}
	}
	
	/*private void debug_printField() // pseudo-gui, for debugging purposes only
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
	} */
	
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
		
		this.playerManager.stoneHasPlonked(s);
		this.stones.remove(s);
	}
	
	public void step()
	{
		long now = (new Date()).getTime();
		
		if(this.nextGameStep <= now)
		{
			// time for increasing speed?
			if(INCREASESPEEDAFTER > 0 && this.nextGameSpeedIncrease <= now)
			{
				this.nextGameSpeedIncrease = now + 1000*INCREASESPEEDAFTER;
				if(this.currentSpeed > INCREASESPEEDABOUT)
					this.currentSpeed -= INCREASESPEEDABOUT;
			}
			
			this.nextGameStep = now + this.currentSpeed;
			this.gameStep();
		}
		
		this.playerManager.step();
	}
	
	private void gameStep()
	{
		this.moveAllDown();
		this.fieldObserver.flush(this.gui);
		this.removeCompletedRows();
		
		// check gameOver
		boolean gameOver = false;
		for(int col=0;col<this.width;col++)
		{
			if(this.fixedPixels[0][col] != 0)
			{
				gameOver = true;
				break;
			}
		}
		
		if(gameOver)
			this.gameOver();
		else
		{
			// new stone?
			
			if(Math.random() < NEWSTONEPROPABILITY)
			{
				Stone tryThisStone = Stone.randomStone();
				tryThisStone.setY(1-tryThisStone.getHeight());
				tryThisStone.setX((int)(((double)(this.width-tryThisStone.getWidth()+1))*Math.random()));
				
				if(this.validTransformation(tryThisStone, tryThisStone)) // call to see whether it fits
				{
					if(this.playerManager.activateNextPlayer(tryThisStone))
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
		
		int[][] beforeRemoval = this.fixedPixels;
		int[][] afterRemoval = new int[this.height][];
		boolean[] rowExploded = new boolean[this.height];
		
		for(int row=0;row<this.height;row++)
		{
			afterRemoval[row] = beforeRemoval[row];
			rowExploded[row] = false;
		}
		boolean anyRowsRemoved = false;
		
		for(int row=0;row<this.height;row++)
		{
			// check whether complete
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
				int[][] possibleNewField = new int[this.height][];
				for(int newrow=this.height-1;newrow>0;newrow--)
				{
					if(newrow > row) // copy old row
						possibleNewField[newrow] = afterRemoval[newrow];
					else // copy the one above
						possibleNewField[newrow] = afterRemoval[newrow-1];
				}
				// insert blank row at the top
				possibleNewField[0] = new int[this.width];
				for(int col=0;col<this.width;col++)
					possibleNewField[0][col] = 0;
				
				// now let's see whether we can remove it
				
				boolean canRemove = true;
				for(int i=0;i<this.stones.size();i++)
				{
					if(this.stones.get(i).collidesWith(possibleNewField))
					{
						canRemove = false;
						break;
					}
				}
				
				if(canRemove)
				{
					afterRemoval = possibleNewField;
					anyRowsRemoved = true;
					rowExploded[row] = true;
				}
				else
					break; // if this row cannot be removed, the next ones won't be safe to remove either
			}
		}
		
		// if rows have been removed, tell our fieldObserver
		if(anyRowsRemoved)
		{
			for(int row=0;row<this.height;row++)
			{
				for(int col=0;col<this.width;col++)
				{
					// notify fieldObserver if row exploded and/or pixel value changed
					if(rowExploded[row] || afterRemoval[row][col] != beforeRemoval[row][col])
						this.fieldObserver.fixedPixelChanged(row, col, afterRemoval[row][col], rowExploded[row]);
				}
			}
			
			this.fixedPixels = afterRemoval;
			this.fieldObserver.flush(this.gui);
		}
	}
	
	private void insertStone(Stone s)
	{
		this.stones.add(s);
		this.fieldObserver.stoneChanged(s, s);
		this.fieldObserver.flush(this.gui);
	}
	
/*	public void debug_insertStone(Stone s) // for testing purposes only
	{
		this.insertStone(s);
	} */
	
	private void gameOver()
	{
		if(this.currentSpeed <= WINNINGSPEED)
		{
			this.gui.MESSAGE("Wow, that was fast. Well done!");
			this.playerManager.allPlayersWon();
		}
		else
		{
			this.gui.MESSAGE("Sorry, you lost :(");
			this.playerManager.allPlayersLost();
		}
		this.goOnPlaying = false; // stop the game
	}
	
	public void playersChanged()
	{
		this.playerManager.listGui(this.gui);
	}
	
	public static void main(String[] args)
	{
		GameLogic GL = new GameLogic();
		GL.takeOverControl();
		GL.shutDown();
		System.out.println("Thanks for playing");
	}
}
