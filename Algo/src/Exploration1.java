
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.swing.JLabel;

public class Exploration1 implements Runnable {
	protected static final int SLEEP_TIME = 0;
	private static TwoDimGrid maze;
	private static Maze mazemaze;
	
	Stack<Integer> stackX = new Stack<Integer>();
	Stack<Integer> stackY = new Stack<Integer>();
	Stack<Integer> stackD = new Stack<Integer>();
	
	int[] offsetX = {0, 1, 0, -1, 0};
	int[] offsetY = {0, 0, 1, 0, -1};
	
	public Exploration1(TwoDimGrid m) {
		maze = m;
	}


	public static void front( TwoDimGrid maze,
				int direction, int x, int y, int frontLeft,  int frontMiddle, int frontRight) {
			
		int [] frontArray = {frontLeft, frontMiddle, frontRight};
	 
			try {
				if (direction == 1) {// south
						for (int j =0; j<3; j++){
							if (frontArray[j]==0){
								for (int i = 1; i < 4; i++)// left sensor
								{
									if (maze.isWall(x+1+i, y+1-j)) break;
									if (!maze.gettraversedPath(x + 1 + i, y + 1 - j)) {
										maze.setSensed(x + 1 + i, y + 1 - j);
									}
								}
							}
							else{
								if (!maze.isWall(x + 1 + frontArray[j], y + 1 - j)){
									maze.setObstacle(x + 1 + frontArray[j], y + 1-j);
								}

								for (int i = 1; i < frontArray[j]; i++)// left sensor
								{
									if (maze.isWall(x+1+i, y+1-j)) break;
									if (!maze.gettraversedPath(x + 1 + i, y + 1 - j)) {
										maze.setSensed(x + 1 + i, y + 1 - j);

									}
								}
							}
						}
					}
					
				if (direction == 3) {// north
					for (int j =0; j<3; j++){
						if (frontArray[j]==0){
							for (int i = 1; i < 4; i++)// left sensor
							{
								if (maze.isWall(x-1-i, y-1+j)) break;
								if (!maze.gettraversedPath(x - 1 - i, y - 1 + j)) {
									maze.setSensed(x - 1 - i, y - 1 + j);
								}
							}
						}
						else{

							if (!maze.isWall(x - 1 - frontArray[j], y - 1 + j)){
								maze.setObstacle(x - 1 - frontArray[j], y - 1 + j);
							}

							for (int i = 1; i < frontArray[j]; i++)// left sensor
							{
								if (maze.isWall(x - 1 - i, y - 1 + j)) break;
								if (!maze.gettraversedPath(x - 1 - i, y - 1 + j)) {
									maze.setSensed(x - 1 - i, y - 1 + j);
								}
							}
						}
					}

				}
				if (direction == 2) {// east
					for (int j =0; j<3; j++){
						if (frontArray[j]==0){
							for (int i = 1; i < 4; i++)// left sensor
							{
							    if (maze.isWall(x-1+j, y+1+i)) break;
								if (!maze.gettraversedPath(x - 1 + j, y + i + 1)) {
									maze.setSensed(x - 1 + j , y + 1 + i);
								}

							}
						}
						else{
							if (!maze.isWall(x - 1 + j, y + frontArray[j] + 1)){
								maze.setObstacle(x - 1 + j, y + frontArray[j] + 1);
							}
							for (int i = 1; i < frontArray[j]; i++)// left sensor
							{
								if (maze.isWall(x - 1 +j, y + 1 + i)) break;
								if (!maze.gettraversedPath(x - 1 + j, y + i + 1)) {
									maze.setSensed(x - 1 + j , y + 1 + i);
								}
	
							}
						}
					}

				}
				if (direction == 4) {//west
					for (int j =0; j<3; j++){
						if (frontArray[j]==0){
							for (int i = 1; i < 4; i++)// left sensor
							{
								if (maze.isWall(x+1-j, y-1-i)) break;
								if (!maze.gettraversedPath(x + 1 - j, y - 1 - i)) {
									maze.setSensed(x + 1 - j, y - 1 - i);
								}

							}
						}
						else
						{
							if (!maze.isWall(x + 1 - j, y - 1 - frontArray[j])){
								maze.setObstacle(x + 1 - j, y - 1 - frontArray[j]);
							
							}
							for (int i = 1; i < frontArray[j]; i++)// left sensor
							{
								if (maze.isWall(x+1-j, y-1-i)) break;
								if (!maze.gettraversedPath(x + 1 - j, y - 1 - i)) {
									maze.setSensed(x + 1 - j, y - 1 - i);
								}
	
							}
						}
					}
				}
			}
			catch (ArrayIndexOutOfBoundsException e) {
				return;
			}
		}

		public static void leftSensor(TwoDimGrid maze, int direction, int x, int y, int left){
			try{
				if(direction==1){
					if (left == 0){
						for (int i=1;i<5;i++)
						{
							if (maze.isWall(x+1, y+i+1)) break;
							if (!maze.gettraversedPath(x+1,y+1+i)){
								maze.setSensed(x+1,y+i+1);
							}

						}
						
					}
					else {
						if (!maze.isWall(x+1,y+left+1)){
							maze.setObstacle(x+1,y+left+1);
						}
						for (int i=1;i<left;i++)
						{
							if (maze.isWall(x+1, y+i+1)) break;
							if (!maze.gettraversedPath(x+1,y+1+i)){
								maze.setSensed(x+1,y+i+1);
							}
	
						}
					}
				}

				if(direction==3){//north
					if (left == 0){
						for (int i=1;i<5;i++)
						{
							if (maze.isWall(x-1, y-i-1)) break;
							
							if (!maze.gettraversedPath(x-1,y-i-1)){
								maze.setSensed(x-1,y-i-1);
								
							}
						}
						
					}
					else{
						
						if (!maze.isWall(x-1,y-left-1)){
							maze.setObstacle(x-1,y-left-1);
							
						}
						
						for (int i=1;i<left;i++)
						{
							if (maze.isWall(x-1, y-i-1)) break;
							if (!maze.gettraversedPath(x-1,y-i-1)){
								maze.setSensed(x-1,y-i-1);
								
							}
	
						}
					}
				}
				if (direction==2){//east
					if (left == 0){
						for (int i=1;i<5;i++){
							if (maze.isWall(x-1-i, y+1)) break;
							if (!maze.gettraversedPath(x-1-i,y+1)){
								maze.setSensed(x-1-i,y+1);
								
							}
	
						}
						
					}
					else{
						if (!maze.isWall(x-1-left,y+1)){
							maze.setObstacle(x-1-left,y+1);
							
						}
						
						for (int i=1;i<left;i++){
							if (maze.isWall(x-1-i, y+1)) break;

							if (!maze.gettraversedPath(x-1-i,y+1)){
								maze.setSensed(x-1-i,y+1);
								
							}
	
						}
					}
				}
				if (direction==4){//west
					if (left == 0){
						for (int i=1;i<5;i++){
							if (maze.isWall(x+1+i, y-1)) break;

							if (!maze.gettraversedPath(x+1+i,y-1)){
								maze.setSensed(x+1+i,y-1);
								
							}
	
						}
						
					}
					else{
						if (!maze.isWall(x+1+left,y-1)){
							maze.setObstacle(x+1+left,y-1);
							
						}
						for (int i=1;i<left;i++){
							if (maze.isWall(x+1+i, y-1)) break;

							if (!maze.gettraversedPath(x+1+i,y-1)){
								maze.setSensed(x+1+i,y-1);
								
							}
	
						}
					}
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				return;
			}

		}


		public static void rightSensor( TwoDimGrid maze,
				int direction, int x, int y, int right) {//need to change //strange out of bounds error

				if (direction == 1) {// south
					if (right == 0){
						for (int i=1;i<6;i++){
							if (maze.isWall(x+1, y-i-1)) break;

							if (!maze.gettraversedPath(x+1, y - 1 - i)) {
								maze.setSensed(x + 1, y - 1 - i);
							}	
	
						}
						
					}
					else{
						if (!maze.isWall(x + 1, y -1 - right)){
							maze.setObstacle(x + 1, y -1 - right);
							
						}
						for (int i = 1; i < right; i++) {// right sensor
							if (maze.isWall(x+1, y-i-1)) break;
							if (!maze.gettraversedPath(x+1, y -1 - i)) {
								maze.setSensed(x+1, y - 1 - i);
								
							}
	
						}
					}

				}
				if (direction == 3) {// north
					if (right == 0){
						for (int i=1;i<6;i++){
							if (maze.isWall(x-1, y+i+1)) break;
							if (!maze.gettraversedPath(x - 1, y + i + 1)) {
								maze.setSensed(x - 1, y + i + 1);
							}	
	
						}
						
					}
					else{
						if (!maze.isWall(x - 1, y + right + 1)){
							maze.setObstacle(x - 1, y + right + 1);
						}
						for (int i = 1; i < right; i++)// left sensor
						{
							if (maze.isWall(x-1, y+i+1)) break;
							if (!maze.gettraversedPath(x - 1, y + i + 1)) {
								maze.setSensed(x - 1, y + i + 1);
							}
	
						}
					}
				}
				if (direction == 2) {// east
					if (right == 0){
						for (int i=1;i<6;i++){
							if (maze.isWall(x+1+i, y+1)) break;
							if (!maze.gettraversedPath(x + i +1, y+1)) {
								maze.setSensed(x + i +1, y+1);
								
							}	
	
						}
						
					}
					else{
						if (!maze.isWall(x + right +1, y+1)){
							maze.setObstacle(x + right +1, y+1);
							
						}
						
						for (int i = 1; i < right; i++) {
							if (maze.isWall(x+1+i, y+1)) break;

							if (!maze.gettraversedPath(x + i +1, y+1)) {
								maze.setSensed(x + i +1, y+1);
								
							}
	
						}
					}
				}
				if (direction == 4) {// west
					if (right == 0){
						for (int i=1;i<6;i++){
							if (maze.isWall(x-i-1, y-1)) break;

							if (!maze.gettraversedPath(x - i -1, y-1)) {
								maze.setSensed(x - i -1, y-1);
								
							}	
	
						}
						
					}
					else{
						if (!maze.isWall(x - right -1, y-1)){
							maze.setObstacle(x - right -1, y-1);
							
						}
						
						for (int i = 1; i < right; i++) {
							if (maze.isWall(x-i-1, y-1)) break;

							if (!maze.gettraversedPath(x - i -1, y-1)) {
								maze.setSensed(x - i -1, y-1);
								
							}
						}
					}
				}
			
		}

	public void updateSensor(TwoDimGrid maze, int direction, int x, int y){
		System.out.println("Before update sensor x,y,direction:"+x+" "+y+" "+direction);
		int frontLeft, frontMiddle, frontRight, right, leftForward;
		frontLeft = frontMiddle = frontRight = 0;
		right = 0;
		leftForward= 0;
		System.out.println("before get real string");
		String IRSensor="";
		
		try {
			IRSensor = new String(UDP.receive());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
		
		
		System.out.println("after get real string");
		
		leftForward = Integer.parseInt(IRSensor.substring(1, 2));
		frontLeft = Integer.parseInt(IRSensor.substring(2, 3));
		frontMiddle = Integer.parseInt(IRSensor.substring(3, 4));
		frontRight = Integer.parseInt(IRSensor.substring(4, 5));	
		right = Integer.parseInt(IRSensor.substring(5, 6));
		
		System.out.println(leftForward+" "+frontLeft+" "+frontMiddle+" "+frontRight+" "+right);
		//System.out.println(IRSensor);

		//read from the arduino sensor signals
		front(maze, direction, x, y, frontLeft, frontMiddle, frontRight);
		leftSensor( maze, direction, x, y, leftForward);
		rightSensor( maze, direction, x, y, right);
		System.out.println("After update sensor x,y,direction:"+x+" "+y+" "+direction);
	}
	
	
	public boolean rightHandTraversed(TwoDimGrid maze, int direction, int x, int y){
		switch (direction){
		case 3:
			for  (int i = 0; i <= 14; i++){
				if (maze. isBlocked(x+i,y+2))  break;
				if (maze.gettraversedPath(x+i,y+2)) {
					System.out.println("get traversed 3 x y: "+(x+i) + " "+ (y+2));
					return true;
				}
			}
			break;
		case 2:
			for  (int i = 0; i <= 14; i++){
				if (maze. isBlocked(x+2,y-i))  break;
				if (maze.gettraversedPath(x+2,y-i)) {
					System.out.println("get traversed 2 x y: "+(x+2) + " "+ (y-i));
					return true;
				}
			}
			break;
		case 1:
			for  (int i = 0; i <= 14; i++){
				if (maze. isBlocked(x-i,y-2))  break;
				if (maze.gettraversedPath(x-i,y-2)) {
					System.out.println("get traversed 1 x y: "+(x-i) + " "+ (y-2));
					return true;
				}
			}
			break;
		case 4:
			for  (int i = 0; i <= 14; i++){
				if (maze. isBlocked(x-2,y+i))  break;
				if (maze.gettraversedPath(x-2,y+i)) {
					System.out.println("get traversed 4 x y: "+(x-2) + " "+ (y+i));
					return true;
				}
			}
			break;
			
		}	
		return false;
	}
	
	public void mazeTraverseTurn(TwoDimGrid maze, int direction,
			int x, int y) throws InterruptedException {
		if (stackD.size()!=0 ){
			System.out.println("PREVIOUS x, y, dirction: "+stackX.peek()+" "+stackY.peek()+" "+stackD.peek());
		}
		System.out.println("TURN TO x, y: "+x+" "+y+"  dirction: "+direction );
		

		
		if (stackD.peek() % 4 + 1 == direction ){
			System.out.println("Go to left");
			UDP.send("l000/");
		}
		else {
			System.out.println("Go to right");
			
				
			boolean canAdjust=false;
			switch (stackD.peek()){
			case 1:
				if (maze.isBlocked(x+2, y-1)&&maze.isBlocked(x+2, y+1)&&maze.isBlocked(x, y+2))
					canAdjust = true;
				break;
			case 2:
				if (maze.isBlocked(x+1, y+2)&&maze.isBlocked(x-1, y+2)&&maze.isBlocked(x-2, y))
					canAdjust = true;
				break;
			case 3:
				if (maze.isBlocked(x-2, y-1)&&maze.isBlocked(x-2, y+1)&&maze.isBlocked(x, y-2))
					canAdjust = true;
				break;
			case 4:
				if (maze.isBlocked(x+1, y-2)&&maze.isBlocked(x-1, y-2)&&maze.isBlocked(x+2, y))
					canAdjust = true;
				break;
			}
				
				
			if (canAdjust){
					UDP.send("a000/");
			}
			else
				UDP.send("r000/");
			}
		
		maze.updateCurrentPosition(direction, x, y);
		
		Thread.sleep(SLEEP_TIME);
		
		updateSensor(maze, direction, x, y);
		
		Thread.sleep(SLEEP_TIME);
        
		maze.traversedPath(x, y);
		
		mazeTraverse(maze, direction,x+offsetX[direction], y+offsetY[direction]);
		

}

	public void moveRobot(TwoDimGrid maze, int direction,
			int x, int y) throws InterruptedException {
		
		if (stackD.size()==0 ){
//			UDP.send("ready");
//			try {
//				while (!UDP.receive().equals("btstart")){
//					UDP.receive();
//				}
//			} catch (SocketException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			};
			UDP.send("gogo/");			
		}else {
			UDP.send("f010/");		
		}
		
		maze.updateCurrentPosition(direction, x, y);

		Thread.sleep(SLEEP_TIME);
		
		updateSensor(maze, direction, x, y);
		
		Thread.sleep(SLEEP_TIME); 

		maze.traversedPath(x, y);
	
	}
	
	
	public void reverseBack(TwoDimGrid maze, int direction,
			int x, int y, int previousDirection, int previousX, int previousY) throws InterruptedException{//update the maze map
		
		System.out.println("Reverse from x,y, direction: "+x+" "+y+" "+direction);
		System.out.println("To the x,y direction: "+previousX+" "+ previousY+" "+previousDirection);    
		
		//reverse the physical robot
		if (direction == previousDirection){
			UDP.send("b010");
			Thread.sleep(SLEEP_TIME);
			maze.traversedPath(x, y);
			Thread.sleep(SLEEP_TIME);
			maze.updateCurrentPosition(direction, previousX, previousY);
				//move the robot back
			updateSensor(maze, previousDirection,previousX,previousY);
		}
		else {
			
			UDP.send("b010");
			
			maze.traversedPath(x, y);
			Thread.sleep(SLEEP_TIME);
			maze.updateCurrentPosition(direction, previousX, previousY);
			Thread.sleep(SLEEP_TIME);
			updateSensor(maze, direction,previousX,previousY);
			Thread.sleep(SLEEP_TIME);
			//still not updating spotaneously
			if (direction == previousDirection % 4 + 1){
				UDP.send("r000/");
				maze.updateCurrentPosition(previousDirection, previousX, previousY);
				Thread.sleep(SLEEP_TIME);
				updateSensor(maze, previousDirection,previousX,previousY);
				Thread.sleep(SLEEP_TIME);
			}
			else{
				UDP.send("l000/");
				
				maze.updateCurrentPosition(previousDirection, previousX, previousY);
				Thread.sleep(SLEEP_TIME);
				updateSensor(maze, previousDirection,previousX,previousY);
				Thread.sleep(SLEEP_TIME);
			}
			
			//System.out.println();    
		}
	}
	

	
	public void mazeTraverse(TwoDimGrid maze, int direction,
			int x, int y) throws InterruptedException {
	
		
		if (stackD.size()!=0 ){
			System.out.println("PREVIOUS x, y, dirction: "+stackX.peek()+" "+stackY.peek()+" "+stackD.peek());
		}
		else {
			System.out.println("======START THE MAZE, GOGO=======");
		}
		System.out.println("MOVE TO x, y: "+x+" "+y+"  dirction:"+direction );
		
		moveRobot(maze, direction, x , y );
		
		stackX.push(x);
		stackY.push(y);
		stackD.push(direction);

		if (maze.gettraversedPath(1,1)&&(direction!=1)&&(direction!=2)) {
			System.out.println("Get to ending point direction: "+direction);
			if (direction == 4){
				UDP.send("r000/");
				maze.updateCurrentPosition(3,x,y);
				updateSensor(maze, 3, x, y);
				UDP.send("a000/");
				maze.updateCurrentPosition(2,x,y);
				updateSensor(maze, 2, x, y);
			}
			else if (direction==3){
				UDP.send("a000/");
				maze.updateCurrentPosition(2,x,y);
				updateSensor(maze, 2, x, y);
			}

			maze.clearSP();
			
			printField(maze);

			UDP.send("done/");
//			while (!UDP.receive().equals("btstart")){
//				UDP.receive();
//			}

			Thread.sleep(SLEEP_TIME);
			//shortest path

			AStar AS = new AStar(maze);

			int flag=AS.searchResult();

			if (flag == -1) {
				System.out.println("import map fail");

			}else if(flag==0){

				System.out.println("no path");
			}

			mazemaze.solvingThread.stop();
			//mazemaze.solvingThread1.stop();

			return;
		}

		maze.startPointColor(1, 1);
		
		//next step exploration   ERROR!!!!! x+-1 y+-1 turn
		
		if (maze.isWalkable(direction % 4 + 1, x, y))
			mazeTraverseTurn(maze, direction % 4 + 1, x, y);
		if (maze.isWalkable(direction , x, y))
			mazeTraverse(maze, direction, x+offsetX[direction], y+offsetY[direction]);
		if (maze.isWalkable((direction + 2) % 4 + 1, x, y))
			mazeTraverseTurn(maze, (direction + 2) % 4 + 1, x, y);
	
		
		//reverse
		stackX.pop();
		stackY.pop();
		stackD.pop();
		reverseBack(maze, direction, x, y, stackD.peek(), stackX.peek(), stackY.peek());

	}
	

	
								
	public void run() {
		UDP.buildSocket();
		
		try {
			mazeTraverse(maze, 2, 2, 2);
		} catch (InterruptedException e) {
			// Thread stops.
		}
	}

	
	public static void printField(TwoDimGrid maze) {
		System.out.println("Maze explored"); 

        String s = "11"; 
        System.out.println("11");
        for (int y = 1; y <21; y++) { 
            for (int x = 1; x < 16; x++) { 
            	if (maze.isUnexplored(x, y)){
            		s = s.concat("0");
            	    System.out.print("0");
            	}
            	else{ 
            		s = s.concat("1");
            	    System.out.print("1");
            	}
            } 
            System.out.println();
  
        } 
        s = s.concat("11"); 
        System.out.println("11");
        
        //System.out.println(s); 
        
        BigInteger b = new BigInteger(s, 2); 
       
        String toHex = b.toString(16); 
        
        System.out.println("The value in Hex is: " + toHex); 
      //second part
        
        
        
        String h = ""; 
        int count = 0;
        for (int y = 1; y <21; y++) { 
            for (int x = 1; x < 16; x++) { 
            	if (!maze.isUnexplored(x, y)){
            		if (maze.isObstacle(x, y)){
            			h = h.concat("1");
                	    System.out.print("1");
                	    count++;
            		}
            		else {
            			h = h.concat("0");
                	    System.out.print("0");
                	    count++;
            		}
            	}
            } 
            System.out.println();
        }
        for (int i=1;i<=((4 - count % 4) % 4);i++){
        	h = h.concat("0");
        }
        
        for (int i=0; i<=h.length()-4; i = i+4){
        	Integer sub = new Integer(Integer.parseInt(h.substring(i, i+4), 2));
        	//System.out.println(sub);
        	System.out.print(Integer.toHexString(sub));
        }
  
        System.out.println();
        //BigInteger toHex1 = new BigInteger(h, 2); 
        //String toHex2 = toHex1.toString(16); 
        
        //System.out.println("The value in Hex is: " + toHex2); 
       // System.out.println(String.format("%040x", new BigInteger(1, h.getBytes())));
        System.out.println("exploreEnds"); 
	}
	
}
