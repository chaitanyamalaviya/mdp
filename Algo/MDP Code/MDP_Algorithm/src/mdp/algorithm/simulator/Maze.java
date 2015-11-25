package mdp.algorithm.simulator;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Maze extends JPanel{
	private static final int ROWS = 22;
	private static final int COLUMNS = 17;
	
	private static final Color DEFAULT = new Color(198,217,241);
	private static final Color OBSTACLES = new Color(84,84,84);
	private static final Color OBSTACLESFOUND = new Color(165,165,165);
	private static final Color GOALSTART = Color.WHITE;
	private static final Color EXPLORED = new Color(255,255,102);
	private static final Color ROBOT = new Color(146,208,80);
	private static final Color ROBOTFRONT = Color.RED;
	
	private static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	private static int robotMidPointRow = 3;
	private static int robotMidPointCol = 3;
	private static int robotOrientation = NORTH;
	private static int robotFrontRow = robotMidPointRow - 1;
	private static int robotFrontCol = robotMidPointRow;
	private static final int ACTIONTIMER = 100;
	
	private String arduinoCommand = new String();
	
	int[][] exploredGridValue = new int[ROWS][COLUMNS]; // For map descriptor part 1, 0 for unknown, 1 for explored
	int[][] obstaclesGridValue = new int[ROWS][COLUMNS]; // For map descriptor part 2, -1 for unknown, 0 for safe, 1 for obstacles
	int[] mapDesc1 = new int[304];
	int[] mapDesc2 = new int[300];
	
	JPanel[][] grid = new JPanel[ROWS][COLUMNS];
	JLabel[][] cells = new JLabel[ROWS][COLUMNS];
	
	TimerQueue queue = new TimerQueue();
	
	Timer timer;
	
	public Maze(){
		resetMapDescriptorsValue();
		mazeUI();
		robot();
	}
	
	public void initializeArduinoCommand(String s){
		this.arduinoCommand = s;
	}
	
	public void resetMap(){
		int oldRobotMidPointRow = robotMidPointRow;
		int oldRobotMidPointCol = robotMidPointCol;
		resetMapDescriptorsValue();
		//stopTimer();
		robotMidPointRow = 2;
		robotMidPointCol = 2;
		robotOrientation = NORTH;
		robotFrontRow = robotMidPointRow - 1;
		robotFrontCol = robotMidPointRow;
		clearRobot(oldRobotMidPointRow, oldRobotMidPointCol);
		robot();
		for (int i = 0;i<ROWS;i++){
        	for (int j = 0;j<COLUMNS;j++){
        		if((i==0 || i==21 || j==0 || j==16)){
        			grid[i][j].setBackground(OBSTACLES);
        		}
        		else if(((i==1 || i==2 || i==3) && (j==13 || j==14 || j==15)) || ((i==18 || i==19 || i==20) && (j==1 || j==2 || j==3)))
    				grid[i][j].setBackground(GOALSTART);
        		else if(grid[i][j].getBackground() == OBSTACLES || grid[i][j].getBackground() == EXPLORED || grid[i][j].getBackground() == OBSTACLESFOUND){
        			grid[i][j].setBackground(DEFAULT);
        		}
        	}	
		}
		queue.resetQueue();
	}
	
	public void resetExplorationMap(){
		int oldRobotMidPointRow = robotMidPointRow;
		int oldRobotMidPointCol = robotMidPointCol;
		resetMapDescriptorsValue();
		clearRobot(oldRobotMidPointRow, oldRobotMidPointCol);
		robot();
		for (int i = 0;i<ROWS;i++){
        	for (int j = 0;j<COLUMNS;j++){
        		if(i==0 || i==21 || j==0 || j==16){
        			grid[i][j].setBackground(OBSTACLES);
        		}
        		else if(((i==1 || i==2 || i==3) && (j==13 || j==14 || j==15)) || ((i==18 || i==19 || i==20) && (j==1 || j==2 || j==3)))
    				grid[i][j].setBackground(GOALSTART);
        		else{
	        		if(grid[i][j].getBackground() == EXPLORED){
	        			grid[i][j].setBackground(DEFAULT);
	        		}
	        		else if(grid[i][j].getBackground() == OBSTACLESFOUND){
	        			grid[i][j].setBackground(OBSTACLES);
	        		}
        		}
        	}	
		}
		queue.resetQueue();
	}
	
	private void actionToBeTaken(){
		char item;
		if(queue.getQueueSize() > 0){
			item = queue.dequeueItem();
	  		switch(item){
	  		case 'f':
	  			//To Simulator
				moveForwardAction();
				//To Arduino
				arduinoCommand += "FT";
				break;
	  		case 'b':
	  			//To Simulator
				spinBackwardAction();
				//To Arduino
				arduinoCommand += "BT";
				break;
	  		case 'r':
	  			//To Simulator
				spinRightAction();
				//To Arduino
				arduinoCommand += "RT";
				break;
	  		case 'l':
	  			//To Simulator
				spinLeftAction();
				//To Arduino
				arduinoCommand += "LT";
				break;
	  		case 'e':
	  			setExplorationPathAction();
				break;
	  		case 's':
	  			completePathAction();
				break;
			}
		}
	}
	
	private void mazeUI(){
        setLayout(new GridLayout(ROWS, COLUMNS, 3, 3));
    	
        for (int i = 0;i<ROWS;i++){
        	for (int j = 0;j<COLUMNS;j++){
        		JPanel newCell = new JPanel();
        		//Colour cells
        		if(((i==1 || i==2 || i==3) && (j==13 || j==14 || j==15)) || ((i==18 || i==19 || i==20) && (j==1 || j==2 || j==3)))
        			newCell.setBackground(GOALSTART);
        		else if(i==0 || i==21 || j==0 || j==16)
        			newCell.setBackground(OBSTACLES);
        		else{
        			newCell.setBackground(DEFAULT);
        		
	        		//onClick
					newCell.addMouseListener(new MouseAdapter() {
		               	@Override
		               	public void mouseClicked(MouseEvent e) {
		               		JPanel clickedPanel = (JPanel) e.getSource();
		               		if(clickedPanel.getBackground() == DEFAULT)
		               			clickedPanel.setBackground(OBSTACLES);
		               		else if(clickedPanel.getBackground() == OBSTACLES)
		               			clickedPanel.setBackground(DEFAULT);
		                }
		             });
				}
        		grid[i][j] = newCell;
				cells[i][j] = new JLabel();
        		
				grid[i][j].add(cells[i][j]);
				add(newCell);
	        }
        }
	}
	
	private void robot(){
		grid[robotMidPointRow-1][robotMidPointCol-1].setBackground(ROBOT);
		grid[robotMidPointRow][robotMidPointCol-1].setBackground(ROBOT);
		grid[robotMidPointRow-1][robotMidPointCol].setBackground(ROBOT);
		grid[robotMidPointRow-1][robotMidPointCol+1].setBackground(ROBOT);
		grid[robotMidPointRow][robotMidPointCol].setBackground(ROBOT);
		grid[robotMidPointRow+1][robotMidPointCol+1].setBackground(ROBOT);
		grid[robotMidPointRow+1][robotMidPointCol].setBackground(ROBOT);
		grid[robotMidPointRow][robotMidPointCol+1].setBackground(ROBOT);
		grid[robotMidPointRow+1][robotMidPointCol-1].setBackground(ROBOT);
		
		setExploredGridValue(robotMidPointRow-1,robotMidPointCol-1,1);
		setExploredGridValue(robotMidPointRow,robotMidPointCol-1,1);
		setExploredGridValue(robotMidPointRow-1,robotMidPointCol,1);
		setExploredGridValue(robotMidPointRow-1,robotMidPointCol+1,1);
		setExploredGridValue(robotMidPointRow,robotMidPointCol,1);
		setExploredGridValue(robotMidPointRow+1,robotMidPointCol+1,1);
		setExploredGridValue(robotMidPointRow+1,robotMidPointCol,1);
		setExploredGridValue(robotMidPointRow,robotMidPointCol+1,1);
		setExploredGridValue(robotMidPointRow+1,robotMidPointCol-1,1);
		
		//Assume no obstacles at robot position
		setobstaclesGridValue(robotMidPointRow-1,robotMidPointCol-1,0);
		setobstaclesGridValue(robotMidPointRow,robotMidPointCol-1,0);
		setobstaclesGridValue(robotMidPointRow-1,robotMidPointCol,0);
		setobstaclesGridValue(robotMidPointRow-1,robotMidPointCol+1,0);
		setobstaclesGridValue(robotMidPointRow,robotMidPointCol,0);
		setobstaclesGridValue(robotMidPointRow+1,robotMidPointCol+1,0);
		setobstaclesGridValue(robotMidPointRow+1,robotMidPointCol,0);
		setobstaclesGridValue(robotMidPointRow,robotMidPointCol+1,0);
		setobstaclesGridValue(robotMidPointRow+1,robotMidPointCol-1,0);
		
		shadeFront();
	}
	
	//The robot moves one step forward according to its orientation
	public void moveForwardAction(){
		switch(robotOrientation){
		case 0: 
			if(robotMidPointRow > 2){
				robotMidPointRow--;
				robot();
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(EXPLORED);
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(EXPLORED);
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		case 1:
			if(robotMidPointCol < 14){
				robotMidPointCol++;
				robot();
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(EXPLORED);
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(EXPLORED);
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		case 2:
			if(robotMidPointRow < 19){
				robotMidPointRow++;
				robot();
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(EXPLORED);
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(EXPLORED);
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		case 3:
			if(robotMidPointCol > 2){
				robotMidPointCol--;
				robot();
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(EXPLORED);
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(EXPLORED);
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		}
	}
	
	//The robot moves one step backward according to its orientation
	private void moveBackwardAction(){
		switch(robotOrientation){
		case 0: 
			if(robotMidPointRow < 19){
				robotMidPointRow++;
				robot();
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(EXPLORED);
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(EXPLORED);
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		case 1:
			if(robotMidPointCol > 2){
				robotMidPointCol--;
				robot();
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(EXPLORED);
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(EXPLORED);
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		case 2:
			if(robotMidPointRow > 2){
				robotMidPointRow--;
				robot();
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(EXPLORED);
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(EXPLORED);
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		case 3:
			if(robotMidPointCol < 14){
				robotMidPointCol++;
				robot();
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(EXPLORED);
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(EXPLORED);
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(EXPLORED);
			}
			else
				System.out.println("WALL!");
			break;
		}
	}
	
	//The robot orientation changes to its right
	public void spinRightAction(){
		robotOrientation++;
	    if (robotOrientation == 4) {
	    	robotOrientation = 0;
	    }
		shadeFront();
	}
	
	//The robot orientation changes to its left
	public void spinLeftAction(){
		robotOrientation--;
        if (robotOrientation == -1) {
        	robotOrientation = 3;
        }
		shadeFront();
	}
	
	//The robot orientation changes to its back
	public void spinBackwardAction(){
		robotOrientation += 2;
		robotOrientation %= 4;
		shadeFront();
	}
	
	public void setExplorationPathAction(){
		switch(robotOrientation){
		case 0: //NORTH
			if(grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol-1, 0);
			} 
			else if (grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol-1, 1);
			}
			
			if(grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 0);
			} 
			else if (grid[robotMidPointRow-2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			}
			
			if(grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol+1, 0);
			}
			else if (grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol+1, 1);
			}

			setExploredGridValue(robotMidPointRow-2, robotMidPointCol-1, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol+1, 1);
			
			
			//For another side sensors
			if(grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLES  && grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			}
			
			setExploredGridValue(robotMidPointRow-1, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow-1, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			break;
		case 1: //EAST
			if(grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2, 1);
			}
			
			setExploredGridValue(robotMidPointRow-1, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow+1, robotMidPointCol+2, 1);
			
			//For side sensor
			if(grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 0);
			}
			else if (grid[robotMidPointRow-2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			}				
			
			if(grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol+1, 0);
			}
			else if (grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol+1, 1);
			}
			
			if(grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			}
			
			if(grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol+1, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol+1, 1);
			}
			
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol+1, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol+1, 1);
			break;
		case 2: //SOUTH
			if(grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol-1, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol-1, 1);
			}			
			
			if(grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			}			
			
			if(grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol+1, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol+1, 1);
			}
			
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol-1, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol+1, 1);
			
			//For side sensor
			if(grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2, 0);
			} 
			else if (grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2, 1);
			}
			
			setExploredGridValue(robotMidPointRow+1, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow+1, robotMidPointCol-2, 1);
			
			break;
		case 3: //WEST
			if(grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2, 1);
			}
			
			setExploredGridValue(robotMidPointRow-1, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow+1, robotMidPointCol-2, 1);
			
			//For side sensors
			if(grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol-1, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol-1, 1);
			}
			
			if(grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			}
			
			if(grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 0);
			}
			else if (grid[robotMidPointRow-2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			}
			
			if(grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol-1, 0);
			}
			else if (grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol-1, 1);
			}
			
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol-1, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol-1, 1);
			break;
		}
	}
	
	public void completePathAction(){
		Simulator s = new Simulator();
		s.stopAllTimerInSim();
	}
	
	private void shadeFront(){
		switch(robotOrientation){
			case 0: 
				if(grid[robotFrontRow][robotFrontCol].getBackground() == ROBOTFRONT)
					grid[robotFrontRow][robotFrontCol].setBackground(ROBOT);
				robotFrontRow = robotMidPointRow - 1;
				robotFrontCol = robotMidPointCol;
				grid[robotFrontRow][robotFrontCol].setBackground(ROBOTFRONT);
				break;
			case 1:
				if(grid[robotFrontRow][robotFrontCol].getBackground() == ROBOTFRONT)
					grid[robotFrontRow][robotFrontCol].setBackground(ROBOT);
				robotFrontRow = robotMidPointRow;
				robotFrontCol = robotMidPointCol + 1;
				grid[robotFrontRow][robotFrontCol].setBackground(ROBOTFRONT);
				break;
			case 2:
				if(grid[robotFrontRow][robotFrontCol].getBackground() == ROBOTFRONT)
					grid[robotFrontRow][robotFrontCol].setBackground(ROBOT);
				robotFrontRow = robotMidPointRow + 1;
				robotFrontCol = robotMidPointCol;
				grid[robotFrontRow][robotFrontCol].setBackground(ROBOTFRONT);
				break;
			case 3:
				if(grid[robotFrontRow][robotFrontCol].getBackground() == ROBOTFRONT)
					grid[robotFrontRow][robotFrontCol].setBackground(ROBOT);
				robotFrontRow = robotMidPointRow;
				robotFrontCol = robotMidPointCol - 1;
				grid[robotFrontRow][robotFrontCol].setBackground(ROBOTFRONT);
				break;
		}
	}
	
	public void moveForward(){
		queue.enqueueItem('f');
		//setExplorationPath();
	}
	
//	public void moveBackward(){
//		queue.enqueueItem('b');
//		//setExplorationPath();
//	}
	
	public void spinLeft(){
		queue.enqueueItem('l');
		//setExplorationPath();
	}
	
	public void spinRight(){
		queue.enqueueItem('r');
		//setExplorationPath();
	}
	
	public void spinBackward(){
		queue.enqueueItem('b');
	}

	public void setExplorationPath(){
		queue.enqueueItem('e');
	}
	
	public void completePath(){
		queue.enqueueItem('s');
	}
	//primarily used to preset the obstacles (so that we don't have to always click when exploring).
	public void setObstacles(int row, int col) {
		if(grid[row][col].getBackground() == DEFAULT)
			grid[row][col].setBackground(OBSTACLES);
	}
	
	//return true if cell is obstacle
	public boolean checkObstacle(int row, int col){
		if((grid[row][col].getBackground() == OBSTACLES) || (grid[row][col].getBackground() == OBSTACLESFOUND))
			return true;
		return false;
	}
	
	//return true if cell is obstacle
	public boolean checkUnexplored(int row, int col){
		if(obstaclesGridValue[row][col] == -1)
			return true;
		return false;
	}
	
	public boolean checkExplored(int row, int col){
		if(exploredGridValue[row][col] == 1)
			return true;
		return false;
	}
	
	//cell display black; 
	public void addObstacles(int row, int col){
		if(grid[row][col].getBackground() == OBSTACLES)
			grid[row][col].setBackground(OBSTACLESFOUND);
		//if(grid[row][col].getBackground() == DEFAULT)
			//grid[row][col].setBackground(OBSTACLES);
	}
	
	//cell display default color
	public void removeObstacles(int row, int col){
		if(grid[row][col].getBackground() == OBSTACLES)
			grid[row][col].setBackground(DEFAULT);
	}
	
	private void clearRobot(int oldRow, int oldCol){
		grid[oldRow-1][oldCol-1].setBackground(DEFAULT);
		grid[oldRow][oldCol-1].setBackground(DEFAULT);
		grid[oldRow-1][oldCol].setBackground(DEFAULT);
		grid[oldRow-1][oldCol+1].setBackground(DEFAULT);
		grid[oldRow][oldCol].setBackground(DEFAULT);
		grid[oldRow+1][oldCol+1].setBackground(DEFAULT);
		grid[oldRow+1][oldCol].setBackground(DEFAULT);
		grid[oldRow][oldCol+1].setBackground(DEFAULT);
		grid[oldRow+1][oldCol-1].setBackground(DEFAULT);
		resetMapDescriptorsValue();
	}
	
	//re-position the robot
	public void setRobotMidPoint(int row, int col){
		clearRobot(robotMidPointRow, robotMidPointCol);
		robotMidPointRow = row;
		robotMidPointCol = col;
		robot();
	}
	
	//return robot mid Point - Row
	public int getRobotMidPointRow(){
		return robotMidPointRow;
	}
	
	//return robot mid Point - Col
	public int getRobotMidPointCol(){
		return robotMidPointCol;
	}
	
	//return robot direction facing
	public int getRobotOrientation(){
		return robotOrientation;
	}
	
	public int getMapRow(){
		return ROWS;
	}

	public int getMapCol(){
		return COLUMNS;
	}
	
	//set robot orientation
	public void setRobotOrientation(int orientation){
		if(orientation < 4){
			robotOrientation = orientation;
			shadeFront();
		}
		else
			System.out.print("ERROR");
	}
	
	public void startTimer(){
		timer = new Timer(ACTIONTIMER, new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  actionToBeTaken();
			}
		});
		arduinoCommand += "ST";
		timer.start(); 
	}
	
	public void stopTimer(){
		if(timer.isRunning())
			timer.stop(); 
		else
			System.out.println("Timer Not Started");
		queue.resetQueue();
	}
	
	public int actionTimerValue(){
		return ACTIONTIMER;
	}
	
	private void setExploredGridValue(int row, int col, int value){
		exploredGridValue[row][col] = value;
	}
	
	private void resetMapDescriptorsValue(){
		for (int i = 0;i<ROWS;i++){
        	for (int j = 0;j<COLUMNS;j++){
        		exploredGridValue[i][j] = 0;
        		obstaclesGridValue[i][j] = -1;        		
        	}
		}
		mapDesc1[0]=1;
		mapDesc1[1]=1;
		mapDesc1[302]=1;
		mapDesc1[303]=1;
		for(int z=2; z<302; z++){
			mapDesc1[z]=0;
		}
		for(int z=0; z<300; z++){
			mapDesc2[z]=-1;
		}
	}
	
	private void getMapDescriptor1(){
		int z = 2;
		for(int i=ROWS-2; i>0; i--){
			for(int j=1; j<COLUMNS-1; j++){
				mapDesc1[z]=exploredGridValue[i][j];
				z++;
			}
		}
	}
	
	public void printMapDescriptor1(){
		String md1Hex = "";
//		int count = 1;
		System.out.println("print Map Descriptor1");
		getMapDescriptor1();
		for(int z=0; z<304; z++){
			System.out.print(mapDesc1[z]);
			md1Hex += mapDesc1[z];
/*			if(count==4){
				System.out.println();
				count = 0;
			}
			count ++;*/
		}
		System.out.println();
		System.out.print("MD1 in Hexidecimal: " + toHex(md1Hex));
		System.out.println();
	}

	public String toHex(String binary){
		BigInteger b = new BigInteger(binary, 2);
		String hexNum = b.toString(16);
		
		int binaryLength = binary.length() * 2;
		int hexLength = hexNum.length() * 8;
		if(binaryLength - hexLength > 0) {
			for(int i = 0; i < ((binaryLength - hexLength)/8); i++) {
				hexNum = "0"+hexNum;
			}
		}
		return hexNum;
	}
	
	public void printMapDescriptor1OnMaze(){
		for(int i=1; i<ROWS-1; i++){
			for(int j=1; j<COLUMNS-1; j++){
				cells[i][j].setText(Integer.toString(exploredGridValue[i][j]));
			}
		}
	}
	
	public void removeMapDescriptorOnMaze(){
		for(int i=1; i<ROWS-1; i++){
			for(int j=1; j<COLUMNS-1; j++){
				cells[i][j].setText("");
			}
		}
	}
	
	private void setobstaclesGridValue(int row, int col, int value){
		obstaclesGridValue[row][col] = value;
	}
	
	public void printMapDescriptor2OnMaze(){
		for(int i=1; i<ROWS-1; i++){
			for(int j=1; j<COLUMNS-1; j++){
				if(obstaclesGridValue[i][j]>-1)
					cells[i][j].setText(Integer.toString(obstaclesGridValue[i][j]));
			}
		}
	}
	
	public int[][] getMap() {
		return exploredGridValue;
	}
	
	private void getMapDescriptor2(){
		int z = 0;
		for(int i=ROWS-2; i>0; i--){
			for(int j=1; j<COLUMNS-2; j++){
				mapDesc2[z]=obstaclesGridValue[i][j];
				z++;
			}
		}
	}
	
	public void printMapDescriptor2(){
		String md2Hex = "";
		System.out.println("print Map Descriptor2");
		getMapDescriptor2();
		for(int z=0; z<300; z++){
			System.out.print(mapDesc2[z]);
			if(mapDesc2[z]>-1)
				md2Hex += mapDesc2[z];
		}
		
		while(md2Hex.length()%8 != 0){
			md2Hex += 0;
		}
		
		System.out.println();
		System.out.print("MD2 in Hexidecimal: " + toHex(md2Hex));
		System.out.println();
		System.out.print("Android:");
		androidMapDesc2();
	}
	
	public float percentageExplored(){
		int countExplored = 0;
		getMapDescriptor1();
		for(int z=2; z<302; z++){
			if(mapDesc1[z] == 1)
				countExplored++;
		}
		return (float)(countExplored/300.0)*100;
	}
	
	public float countExplored(){
		int countExplored = 0;
		getMapDescriptor1();
		for(int z=2; z<302; z++){
			if(mapDesc1[z] == 1)
				countExplored++;
		}
		return countExplored;
	}
	
	public void androidMapDesc2(){
		String androidMD2 = "grid ";
		androidMD2 += (ROWS-2) +" ";
		androidMD2 += (COLUMNS-2) +" ";
		androidMD2 += robotFrontRow +" ";
		androidMD2 += robotFrontCol +" ";
		androidMD2 += robotMidPointRow +" ";
		androidMD2 += robotMidPointCol;
		for(int j=1; j<COLUMNS-2; j++){
			for(int i=ROWS-2; i>0; i--){
				androidMD2+=" " + obstaclesGridValue[i][j];
			}
		}
		System.out.println(androidMD2);
	}
	

	////////////////////////////////////////////////////////
	//FOR EXPLORATIONSIDE
	
	public boolean esCheckLeftClear(int[][] visited){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol-2) && obstaclesGridValue[robotMidPointRow][robotMidPointCol-2]==0 && obstaclesGridValue[robotMidPointRow+1][robotMidPointCol-2]==0)
				return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol+1) && obstaclesGridValue[robotMidPointRow-2][robotMidPointCol]==0 && obstaclesGridValue[robotMidPointRow-2][robotMidPointCol-1]==0)
				return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow+1, robotMidPointCol+2) && obstaclesGridValue[robotMidPointRow][robotMidPointCol+2]==0 && obstaclesGridValue[robotMidPointRow-1][robotMidPointCol+2]==0)
				return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol-1) && obstaclesGridValue[robotMidPointRow+2][robotMidPointCol]==0 && obstaclesGridValue[robotMidPointRow+2][robotMidPointCol+1]==0)
				return true;
			break;
		}
		return false;
	}
	
	public boolean esCheckRightClear(int[][] visited){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol+2) && obstaclesGridValue[robotMidPointRow][robotMidPointCol+2]==0 && obstaclesGridValue[robotMidPointRow+1][robotMidPointCol+2]==0)
				return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol+1) && obstaclesGridValue[robotMidPointRow+2][robotMidPointCol]==0 && obstaclesGridValue[robotMidPointRow+2][robotMidPointCol-1]==0)
				return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow+1, robotMidPointCol-2) && obstaclesGridValue[robotMidPointRow][robotMidPointCol-2]==0 && obstaclesGridValue[robotMidPointRow-1][robotMidPointCol-2]==0)
				return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol-1) && obstaclesGridValue[robotMidPointRow-2][robotMidPointCol]==0 && obstaclesGridValue[robotMidPointRow-2][robotMidPointCol+1]==0)
				return true;
			break;
		}
		return false;
	}

	public boolean esCheckFrontClear(){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow-2, robotMidPointCol) && !checkObstacle(robotMidPointRow-2, robotMidPointCol+1))
				return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol+2) && !checkObstacle(robotMidPointRow, robotMidPointCol+2) && !checkObstacle(robotMidPointRow+1, robotMidPointCol+2))
				return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow+2, robotMidPointCol) && !checkObstacle(robotMidPointRow+2, robotMidPointCol+1))
				return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow+1, robotMidPointCol-2) && !checkObstacle(robotMidPointRow, robotMidPointCol-2) && !checkObstacle(robotMidPointRow-1, robotMidPointCol-2))
				return true;
			break;
		}
		return false;
	}
	
	public void esSetExplorationPathAction(){
		int i = 0;
		switch(robotOrientation){
		case 0: //NORTH
			if(grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol-1, 0);
			} 
			else if (grid[robotMidPointRow-2][robotMidPointCol-1].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol-1, 1);
			}
			
			if(grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 0);
			} 
			else if (grid[robotMidPointRow-2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			}
			
			if(grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() != OBSTACLES && grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol+1, 0);
			}
			else if (grid[robotMidPointRow-2][robotMidPointCol+1].getBackground() == OBSTACLES){
				grid[robotMidPointRow-2][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-2, robotMidPointCol+1, 1);
			}

			setExploredGridValue(robotMidPointRow-2, robotMidPointCol-1, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow-2, robotMidPointCol+1, 1);
			
			//For left side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow-1, robotMidPointCol-2-i, 1);
				if(grid[robotMidPointRow-1][robotMidPointCol-2-i].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow-1][robotMidPointCol-2-i].getBackground() == OBSTACLES){
					grid[robotMidPointRow-1][robotMidPointCol-2-i].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2-i, 1);
					break;
				}
				grid[robotMidPointRow-1][robotMidPointCol-2-i].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2-i, 0);
				i++;
			}
			//For right side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow-1, robotMidPointCol+2+i, 1);
				if(grid[robotMidPointRow-1][robotMidPointCol+2+i].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow-1][robotMidPointCol+2+i].getBackground() == OBSTACLES){
					grid[robotMidPointRow-1][robotMidPointCol+2+i].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2+i, 1);
					break;
				}
				grid[robotMidPointRow-1][robotMidPointCol+2+i].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2+i, 0);
				i++;
			}
			break;
		case 1: //EAST
			if(grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow-1][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow-1][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			}
			
			if(grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() != OBSTACLES && grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2, 0);
			}
			else if (grid[robotMidPointRow+1][robotMidPointCol+2].getBackground() == OBSTACLES){
				grid[robotMidPointRow+1][robotMidPointCol+2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2, 1);
			}
			
			setExploredGridValue(robotMidPointRow-1, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol+2, 1);
			setExploredGridValue(robotMidPointRow+1, robotMidPointCol+2, 1);
			
			//For left side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow-2-i, robotMidPointCol+1, 1);
				if(grid[robotMidPointRow-2-i][robotMidPointCol+1].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow-2-i][robotMidPointCol+1].getBackground() == OBSTACLES){
					grid[robotMidPointRow-2-i][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow-2-i, robotMidPointCol+1, 1);
					break;
				}
				grid[robotMidPointRow-2-i][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2-i, robotMidPointCol+1, 0);
				i++;
			}
			//For right side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow+2+i, robotMidPointCol+1, 1);
				if(grid[robotMidPointRow+2+i][robotMidPointCol+1].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow+2+i][robotMidPointCol+1].getBackground() == OBSTACLES){
					grid[robotMidPointRow+2+i][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow+2+i, robotMidPointCol+1, 1);
					break;
				}
				grid[robotMidPointRow+2+i][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2+i, robotMidPointCol+1, 0);
				i++;
			}
			break;
		case 2: //SOUTH
			if(grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol-1, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol-1].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol-1, 1);
			}			
			
			if(grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			}			
			
			if(grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() != OBSTACLES && grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol+1, 0);
			}
			else if (grid[robotMidPointRow+2][robotMidPointCol+1].getBackground() == OBSTACLES){
				grid[robotMidPointRow+2][robotMidPointCol+1].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+2, robotMidPointCol+1, 1);
			}
			
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol-1, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol, 1);
			setExploredGridValue(robotMidPointRow+2, robotMidPointCol+1, 1);
			
			//For left side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow+1, robotMidPointCol-2-i, 1);
				if(grid[robotMidPointRow+1][robotMidPointCol-2-i].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow+1][robotMidPointCol-2-i].getBackground() == OBSTACLES){
					grid[robotMidPointRow+1][robotMidPointCol-2-i].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2-i, 1);
					break;
				}
				grid[robotMidPointRow+1][robotMidPointCol-2-i].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2-i, 0);
				i++;
			}
			//For right side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow+1, robotMidPointCol+2+i, 1);
				if(grid[robotMidPointRow+1][robotMidPointCol+2+i].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow+1][robotMidPointCol+2+i].getBackground() == OBSTACLES){
					grid[robotMidPointRow+1][robotMidPointCol+2+i].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2+i, 1);
					break;
				}
				grid[robotMidPointRow+1][robotMidPointCol+2+i].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol+2+i, 0);
				i++;
			}
			
			break;
		case 3: //WEST
			if(grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow-1][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow-1][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow-1, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			}
			
			if(grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() != OBSTACLES && grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() != OBSTACLESFOUND){
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2, 0);
			}
			else if (grid[robotMidPointRow+1][robotMidPointCol-2].getBackground() == OBSTACLES){
				grid[robotMidPointRow+1][robotMidPointCol-2].setBackground(OBSTACLESFOUND);
				setobstaclesGridValue(robotMidPointRow+1, robotMidPointCol-2, 1);
			}
			
			setExploredGridValue(robotMidPointRow-1, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow, robotMidPointCol-2, 1);
			setExploredGridValue(robotMidPointRow+1, robotMidPointCol-2, 1);
			
			//For right side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow-2-i, robotMidPointCol-1, 1);
				if(grid[robotMidPointRow-2-i][robotMidPointCol-1].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow-2-i][robotMidPointCol-1].getBackground() == OBSTACLES){
					grid[robotMidPointRow-2-i][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow-2-i, robotMidPointCol-1, 1);
					break;
				}
				grid[robotMidPointRow-2-i][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow-2-i, robotMidPointCol-1, 0);
				i++;
			}
			//For left side sensors
			i=0;
			while(i<5){
				setExploredGridValue(robotMidPointRow+2+i, robotMidPointCol-1, 1);
				if(grid[robotMidPointRow+2+i][robotMidPointCol-1].getBackground() == OBSTACLESFOUND)
					break;
				if(grid[robotMidPointRow+2+i][robotMidPointCol-1].getBackground() == OBSTACLES){
					grid[robotMidPointRow+2+i][robotMidPointCol-1].setBackground(OBSTACLESFOUND);
					setobstaclesGridValue(robotMidPointRow+2+i, robotMidPointCol-1, 1);
					break;
				}
				grid[robotMidPointRow+2+i][robotMidPointCol-1].setBackground(EXPLORED);
				setobstaclesGridValue(robotMidPointRow+2+i, robotMidPointCol-1, 0);
				i++;
			}
			break;
		}
	}
	
	public boolean checkFrontClear(int[][] visited){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow-2, robotMidPointCol) && !checkObstacle(robotMidPointRow-2, robotMidPointCol+1))
				if(visited[robotMidPointRow-2][robotMidPointCol-1]==0 || visited[robotMidPointRow-2][robotMidPointCol]==0 || visited[robotMidPointRow-2][robotMidPointCol+1]==0)
					return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol+2) && !checkObstacle(robotMidPointRow, robotMidPointCol+2) && !checkObstacle(robotMidPointRow+1, robotMidPointCol+2))
				if(visited[robotMidPointRow-1][robotMidPointCol+2]==0 || visited[robotMidPointRow][robotMidPointCol+2]==0 || visited[robotMidPointRow+1][robotMidPointCol+2]==0)
					return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow+2, robotMidPointCol) && !checkObstacle(robotMidPointRow+2, robotMidPointCol+1))
				if(visited[robotMidPointRow+2][robotMidPointCol-1]==0 || visited[robotMidPointRow+2][robotMidPointCol]==0 || visited[robotMidPointRow+2][robotMidPointCol+1]==0)
					return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow+1, robotMidPointCol-2) && !checkObstacle(robotMidPointRow, robotMidPointCol-2) && !checkObstacle(robotMidPointRow-1, robotMidPointCol-2))
				if(visited[robotMidPointRow+1][robotMidPointCol-2]==0 || visited[robotMidPointRow][robotMidPointCol-2]==0 || visited[robotMidPointRow-1][robotMidPointCol-2]==0)
					return true;
			break;
		}
		return false;
	}
	
	public boolean checkFrontObstaclesClear(){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow-2, robotMidPointCol) && !checkObstacle(robotMidPointRow-2, robotMidPointCol+1))
				return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol+2) && !checkObstacle(robotMidPointRow, robotMidPointCol+2) && !checkObstacle(robotMidPointRow+1, robotMidPointCol+2))
				return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow+2, robotMidPointCol) && !checkObstacle(robotMidPointRow+2, robotMidPointCol+1))
				return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow+1, robotMidPointCol-2) && !checkObstacle(robotMidPointRow, robotMidPointCol-2) && !checkObstacle(robotMidPointRow-1, robotMidPointCol-2))
				return true;
			break;
		}
		return false;
	}
	public boolean checkThreeObstacles(){
		switch(robotOrientation){
		case 0: //NORTH
			if(checkObstacle(robotMidPointRow-2, robotMidPointCol-1) && checkObstacle(robotMidPointRow-2, robotMidPointCol) && checkObstacle(robotMidPointRow-2, robotMidPointCol+1))
				return true;
			break;
		case 1: //EAST
			if(checkObstacle(robotMidPointRow-1, robotMidPointCol+2) && checkObstacle(robotMidPointRow, robotMidPointCol+2) && checkObstacle(robotMidPointRow+1, robotMidPointCol+2))
				return true;
			break;
		case 2: //SOUTH
			if(checkObstacle(robotMidPointRow+2, robotMidPointCol-1) && checkObstacle(robotMidPointRow+2, robotMidPointCol) && checkObstacle(robotMidPointRow+2, robotMidPointCol+1))
				return true;
			break;
		case 3: //WEST
			if(checkObstacle(robotMidPointRow+1, robotMidPointCol-2) && checkObstacle(robotMidPointRow, robotMidPointCol-2) && checkObstacle(robotMidPointRow-1, robotMidPointCol-2))
				return true;
			break;
		}
		return false;
	}
	
	public boolean checkLeftClear(int[][] visited){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol-2) && !checkObstacle(robotMidPointRow, robotMidPointCol-2))
				if(visited[robotMidPointRow-1][robotMidPointCol-2]==0 || visited[robotMidPointRow][robotMidPointCol-2]==0)
					return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol+1) && !checkObstacle(robotMidPointRow-2, robotMidPointCol))
				if(visited[robotMidPointRow-2][robotMidPointCol+1]==0 || visited[robotMidPointRow-2][robotMidPointCol]==0)
					return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow, robotMidPointCol+2) && !checkObstacle(robotMidPointRow+1, robotMidPointCol+2))
				if(visited[robotMidPointRow][robotMidPointCol+2]==0 || visited[robotMidPointRow+1][robotMidPointCol+2]==0)
					return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow+2, robotMidPointCol))
				if(visited[robotMidPointRow+2][robotMidPointCol-1]==0 || visited[robotMidPointRow+2][robotMidPointCol]==0)
					return true;
			break;
		}
		return false;
	}
	
	public boolean checkRightClear(int[][] visited){
		switch(robotOrientation){
		case 0: //NORTH
			if(!checkObstacle(robotMidPointRow-1, robotMidPointCol+2) && !checkObstacle(robotMidPointRow, robotMidPointCol+2))
				if(visited[robotMidPointRow-1][robotMidPointCol+2]==0 || visited[robotMidPointRow][robotMidPointCol+2]==0)
					return true;
			break;
		case 1: //EAST
			if(!checkObstacle(robotMidPointRow+2, robotMidPointCol+1) && !checkObstacle(robotMidPointRow+2, robotMidPointCol))
				if(visited[robotMidPointRow+2][robotMidPointCol+1]==0 || visited[robotMidPointRow+2][robotMidPointCol]==0)
					return true;
			break;
		case 2: //SOUTH
			if(!checkObstacle(robotMidPointRow, robotMidPointCol-2) && !checkObstacle(robotMidPointRow+1, robotMidPointCol-2))
				if(visited[robotMidPointRow][robotMidPointCol-2]==0 || visited[robotMidPointRow+1][robotMidPointCol-2]==0)
					return true;
			break;
		case 3: //WEST
			if(!checkObstacle(robotMidPointRow-2, robotMidPointCol-1) && !checkObstacle(robotMidPointRow-2, robotMidPointCol))
				if(visited[robotMidPointRow-2][robotMidPointCol-2]==0 || visited[robotMidPointRow-2][robotMidPointCol]==0)
					return true;
			break;
		}
		return false;
	}

}