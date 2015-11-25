//package ;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class TwoDimGrid extends JPanel {
	// Size of the maze
	private static final int NUM_ROWS = 17;
	private static final int NUM_COLS = 22;
	
	// Indicates a wall
	private static final Color WALL = Color.BLACK;
	private static final Color SENSED = Color.GREEN;
	// Indicates a cell that was searched but did not lead to a path
	private static final Color FAILED = new Color (182, 27, 32);
	
	// Indicates an unexplored area
	private static final Color UNEXPLORED = Color.LIGHT_GRAY;
	
	// Indicates a cell known to be on the path out
	private static final Color PATH = new Color(13, 156, 252);
	
	// Indicates a cell on a path that is being explored.
	private static final Color TENTATIVE = Color.YELLOW;
	
	private static final Color TRAVERSED = Color.BLUE;
	private static final Color OBSTACLE = Color.DARK_GRAY;
	
	// Indicates a cell that has been found but not yet explored
	//private static final Color FOUND = new Color(183, 252, 166);
	
	// The cells that make up the maze
	JPanel[][] grid = new JPanel[NUM_ROWS][NUM_COLS];
	
	protected JLabel[][] labels = new JLabel[NUM_ROWS][NUM_COLS];
	
	// Random number generator to help decide where to put the walls.
	// setWall()
	private Random colorGen = new Random();
	
	private boolean showLabels = false;
	
	/**
	 * Creates and displays a new maze.
	 */
	public TwoDimGrid() {
		initGrid();
		newMaze();
	}
	
	public TwoDimGrid(TwoDimGrid original) {
		initGrid();
		copyMaze(original);
	}

	private void initGrid() {
		setLayout (new GridLayout (NUM_ROWS, NUM_COLS));
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				ColorGridCell newCell = new ColorGridCell(i,j);
				newCell.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
				grid[i][j] = newCell;
				labels[i][j] = new JLabel();
				grid[i][j].add(labels[i][j]);
				add(newCell);
				
			}
		}
	}

	/**
	 * Generates a new maze
	 */
	
	public void newMaze() {
		 for (int i = 0; i < NUM_COLS; i++) {
	            grid[0][i].setBackground(WALL);
	        }
	        for (int i = 0; i < NUM_COLS; i++) {
	            grid[16][i].setBackground(WALL);
	        }
	        for (int i = 0; i < NUM_ROWS; i++) {
	            grid[i][0].setBackground(WALL);
	        }
	        for (int i = 0; i < NUM_ROWS; i++) {
	            grid[i][21].setBackground(WALL);
	        }
	}
	public void randomMaze() {
		 int mazecounter = 9;
	        clearmap();
	         
	        for (int i = 1; i < NUM_ROWS-1; i++) {
	            for (int j = 1; j < NUM_COLS-1; j++) {
	                if ( mazecounter != 0)
	                {
	                    if( getRandomColor(i,j) == WALL )
	                    {
	                        mazecounter --;
	                    }
	                     
	                    else if  (grid[i][j].getBackground() != WALL)
	                    {
	                        grid[i][j].setBackground(UNEXPLORED);
	                    }
	                }
	                else
	                {
	                    grid[i][j].setBackground(UNEXPLORED);
	                }
	                 
	                 
	            }
	        }
	       
	        //grid[15][4].setBackground(WALL);
	        //grid[14][11].setBackground(WALL);
	        //grid[15][11].setBackground(WALL);
	        //grid[13][10].setBackground(WALL);
	        //grid[14][4].setBackground(WALL);
	        //grid[14][3].setBackground(WALL);
	        //grid[14][1].setBackground(WALL);
	        //grid[13][1].setBackground(WALL);
	        ///grid[14][2].setBackground(WALL);
	        //grid[13][4].setBackground(WALL);
	        //grid[13][8].setBackground(WALL);
	        //grid[13][7].setBackground(WALL);
	        //grid[13][6].setBackground(WALL);
	        //grid[12][8].setBackground(WALL);
	        //grid[11][8].setBackground(WALL);
	        //grid[10][8].setBackground(WALL);
	        //grid[10][7].setBackground(WALL);
	        //grid[10][6].setBackground(WALL);
//	        grid[7][20].setBackground(WALL);
	 
	        //grid[7][17].setBackground(WALL);//grid[13][16].setBackground(WALL);
	        //grid[11][16].setBackground(WALL);
	        /*grid[7][20].setBackground(WALL);
	        grid[7][19].setBackground(WALL);
	        grid[7][18].setBackground(WALL);
	        grid[7][16].setBackground(WALL);
	        grid[7][15].setBackground(WALL);*/
	        //grid[6][15].setBackground(WALL);
	        //grid[5][15].setBackground(WALL);
	        //grid[4][15].setBackground(WALL);
	        //grid[4][16].setBackground(WALL);
	        //grid[4][17].setBackground(WALL);
	        //grid[5][].setBackground(WALL);
//	        grid[11][18].setBackground(WALL);
	        //grid[11][14].setBackground(WALL);
//	       grid[1][5].setBackground(WALL);
//	        grid[15][5].setBackground(WALL);
	         
	        // Make sure starting and ending cells are visitable
	        grid[1][2].setBackground(UNEXPLORED);
	        grid[1][1].setBackground(UNEXPLORED);
	        grid[1][3].setBackground(UNEXPLORED);
	        grid[2][2].setBackground(UNEXPLORED);
	        grid[2][1].setBackground(UNEXPLORED);
	        grid[2][3].setBackground(UNEXPLORED);
	        grid[3][2].setBackground(UNEXPLORED);
	        grid[3][1].setBackground(UNEXPLORED);
	        grid[3][3].setBackground(UNEXPLORED);
	        grid[15][20].setBackground(UNEXPLORED);
	        grid[15][19].setBackground(UNEXPLORED);
	        grid[15][18].setBackground(UNEXPLORED);
	        grid[14][20].setBackground(UNEXPLORED);
	        grid[14][19].setBackground(UNEXPLORED);
	        grid[14][18].setBackground(UNEXPLORED);
	        grid[13][20].setBackground(UNEXPLORED);
	        grid[13][19].setBackground(UNEXPLORED);
	        grid[13][18].setBackground(UNEXPLORED);
	 
	         
	    }
	 
	    public void clearmap()
	    {
	        for (int i = 1; i < NUM_ROWS-1; i++) {
	            for (int j = 1; j < NUM_COLS-1; j++) {
	             
	                grid[i][j].setBackground(UNEXPLORED);
	                 
	            }
	        }
	    }
	    
	    public void clearSP()
	    {
	        for (int i = 1; i < 4; i++) {
	            for (int j = 1; j < 4; j++) {
	                grid[i][j].setBackground(TRAVERSED);
	                grid[16-i][21-j].setBackground(TRAVERSED);
	            }
	        }
	    }
	     
	     
	    public void copyMaze(TwoDimGrid original) {
	        for (int i = 0; i < NUM_ROWS; i++) {
	            for (int j = 0; j < NUM_COLS; j++) {
	                grid[i][j].setBackground(original.grid[i][j].getBackground());
	                labels[i][j].setText("");
	            }
	        }
	     
	        // Make sure starting and ending cells are visitable
	        grid[1][1].setBackground(UNEXPLORED);
	        grid[NUM_ROWS-1][NUM_COLS-1].setBackground(UNEXPLORED);
	         
	    }
	 
	    /**
	     * Randomly select whether to put a wall or hallway
	     * @return
	     */
	    private Color getRandomColor(int row,int col) {
	        // 70% of the time we select a hallway
	        int next = colorGen.nextInt(30);
	        if (next <=28) {
	            return UNEXPLORED;
	        }
	         
	        int mazetype = colorGen.nextInt(5);
	        int mazedirect = colorGen.nextInt(99);
	        int border =  colorGen.nextInt(1);
	        if(mazedirect % 2 == 1)
	        {   
	             
	            for(int i = 0; i<= mazetype;i++)
	            {
	                try
	            {
	                grid[row+i][col + border].setBackground(WALL);
	            }
	            catch(Exception e)
	            {
	                 
	            }
	            }
	         
	        }
	        else
	        {
	             
	            for(int i = 0; i<= mazetype;i++)
	            {
	                try
	                {
	                grid[row+ border][col+i].setBackground(WALL);
	                }
	                catch(Exception e)
	                {
	                    continue;
	                }
	            }
	         
	            }
	         
	        return WALL;
	    }
	 

    

	

	/**
	 * 
	 * @return number of rows in the maze
	 */
	public int getNumRows() {
		return NUM_ROWS;
	}

	/**
	 * 
	 * @return number of columns in the maze
	 */
	public int getNumCols() {
		return NUM_COLS;
	}

	/**
	 * 
	 * @param x row of maze
	 * @param y column of maze
	 * @return true if there is a wall at (row, column)
	 */

	public void setObstacle(int x, int y) {
		grid[x][y].setBackground(OBSTACLE);
	}
	
	public boolean getSensed(int x, int y) {
		return grid[x][y].getBackground()==SENSED;
	}
	public boolean isWall(int x, int y) {
		if ((x>15)||(x<1)||(y>20)||(y<1)){
			return true;
		}
		return grid[x][y].getBackground() == WALL;
	}
	
	public boolean isObstacle(int x, int y) {
		return grid[x][y].getBackground() == OBSTACLE;
	}
	
	
	public boolean isBlocked(int x, int y) {
		return isWall(x,y)||isObstacle(x,y);
	}
	
	
	
	public boolean isWalkable(int direction, int x, int y) {
		switch (direction){
		case 1:
			try{
				return !isBlocked(x+2,y-1)&&!isBlocked(x+2,y+1)&&!isBlocked(x+2,y);
			} 
			catch (ArrayIndexOutOfBoundsException e) {
				
			}
		case 2:
			try{
				return !isBlocked(x-1,y+2)&&!isBlocked(x,y+2)&&!isBlocked(x+1,y+2);
			} 
			catch (ArrayIndexOutOfBoundsException e) {
				
			}
		case 3:
			try{
				return !isBlocked(x-2,y-1)&&!isBlocked(x-2,y)&&!isBlocked(x-2,y+1);
			} 
			catch (ArrayIndexOutOfBoundsException e) {
				
			}
		case 4:
			try{
				return !isBlocked(x-1,y-2)&&!isBlocked(x,y-2)&&!isBlocked(x+1,y-2);
			} 
			catch (ArrayIndexOutOfBoundsException e) {
				
			}
		}
		return true;
	}
	
	public boolean isUnexplored(int x, int y) {
		return grid[x][y].getBackground() == UNEXPLORED;
	}
	/**
	 * 
	 * @param x row of maze
	 * @param y column of maze
	 * @return true if the cell at (x, y) has already been explored
	 */
	public boolean alreadyVisited(int x, int y) {
		return grid[x][y].getBackground() == PATH || grid[x][y].getBackground() == TENTATIVE
			|| grid[x][y].getBackground() == FAILED /*|| grid[x][y].getBackground() == FOUND*/;
	}

	/**
	 * Mark the cell at (x, y) as being on the solution path 
	 * @param x row of maze
	 * @param y column of maze
	 */
	public void onPath(int x, int y) {
		grid[x][y].setBackground(PATH);		
	}

	/**
	 * Mark the cell at (x, y) as being under exploration
	 * @param x row of maze
	 * @param y column of maze
	 */
	
	public void setExplored(int x, int y) {
		grid[x][y].setBackground(TENTATIVE);
	}
	public void setSensed(int x, int y) {
		grid[x][y].setBackground(SENSED);
	}
	
	public void directionExplore(int x, int y) {
		grid[x][y].setBackground(Color.red);
	}
	
	public void updateCurrentPosition(int direction, int x, int y) {
		setExplored(x-1, y-1);
		setExplored(x, y-1);
		setExplored(x+1, y-1);
		setExplored(x-1, y);
		setExplored(x, y);
		setExplored(x+1, y);
		setExplored(x-1, y+1);
		setExplored(x, y+1);
		setExplored(x+1, y+1);
		switch (direction){
		case 1:
			directionExplore(x+1,y);
			break;
		case 2:
			directionExplore(x,y+1);
			break;
		case 3:
			directionExplore(x-1,y);
			break;
		case 4:
			directionExplore(x,y-1);
			break;
		}
	}
	
	public void traversed(int x, int y){
		grid[x][y].setBackground(TRAVERSED);
	}
	
	public void startPointColor(int x, int y){
		grid[x][y].setBackground(Color.magenta);
	}
	
	public void traversedPath(int x, int y) {
		traversed(x-1, y-1);
		traversed(x, y-1);
		traversed(x+1, y-1);
		traversed(x-1, y);
		traversed(x, y);
		traversed(x+1, y);
		traversed(x-1, y+1);
		traversed(x, y+1);
		traversed(x+1, y+1);
			
	}
	
	public boolean gettraversedPath(int x, int y) {
		return grid[x][y].getBackground()==TRAVERSED;		
	}
	
	public boolean isExplored(int x, int y) {
		return grid[x][y].getBackground()==TENTATIVE;		
	}
	
	

	/**
	 * Mark the cell at (x, y) as being a dead end
	 * @param x row of maze
	 * @param y column of maze
	 */
	
	public void deadEnd(int x, int y) {
		grid[x][y].setBackground(FAILED);		
	}
	
	/**
	 * Mark the cell at (x, y) as being found but not yet explored
	 * @param x row of the maze
	 * @param y column of the maze
	 */

	public void setLabel(int x, int y, String s) {
		if (showLabels) {
			labels[x][y].setText(s);
		}
	}

	public void showLabels(boolean show) {
		showLabels = show;
	}

	public boolean Sensed(int x, int y) {
		return grid[x][y].getBackground()==SENSED;
	}

	
	public void tracer(int x, int y)//, int x2,int y2) 	// trace back route
	{
			for(int i = -1; i<2;i++)
			{
				for(int j = -1; j<2;j++)
				{
					grid[x+i][y+j].setBackground(Color.cyan);
				}
			}
	}
	
	public void speeding(int x, int y)//, int x2,int y2) 	// trace back route
	{
		try{
			
			
			for(int i = -1; i<2;i++)
			{
				for(int j = -1; j<2;j++)
				{
					grid[x+i][y+j].setBackground(Color.orange);
				}
			}
		
	//	grid[x2][y2].setBackground(Color.orange);
		Thread.sleep(Exploration1.SLEEP_TIME);
		}
		catch(Exception e)
		{
			
		}
	}
	
}
