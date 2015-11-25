
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class AStar {
    int [][] map = new int[17][22];
    private TwoDimGrid maze;
    private List<Node> openList;//open list
    private List<Node> closeList;//close list
    private final int COST_STRAIGHT = 200;//
    private final int COST_CLOSEWALL = 1;
    private final int COST_TURN = 2000;
    private int row;
    private int column;
    
    public AStar(TwoDimGrid maze){
        this.maze = maze;
        for (int i = 0; i<17; i++){
        	for (int j = 0; j<22; j++){
        		if (maze.isBlocked(i, j)){
        			map[i][j]=0;
        		}
        		else{
        			map[i][j]=1;
        		}
        	}
        }
        this.row=15;
        this.column=20;
        openList=new ArrayList<Node>();
        closeList=new ArrayList<Node>();
    }
    
    //find the path
    public int searchResult(){
    	Node sNode=new Node(2,2,null);
        Node eNode=new Node(14,19,null);
        openList.add(sNode);
        List<Node> resultList=search(sNode, eNode);
        if(resultList.size()==0){
            return 0;
        }
        
        int previousDirection = 2;
        int previousX = 2;
        int previousY = 2;
        int currentDirection = -1;
        int shortestPathCount = 0;
        
        for(Node node:resultList){
        	if (node.getX()==3&&node.getY()==2){
        		previousDirection = 1;
        		UDP.send("r000/");   
        		System.out.println("r000/"); 
        		break;
        	}
        	
        }
		
//        UDP.send("done/");
//		try {
//			while (!UDP.receive2().equals("btstart")){
//				UDP.receive2();
//			}
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
        for(Node node:resultList){
        	if ((node.getX()!=2)||(node.getY()!=2)){
        		if (node.getX() == previousX+1){
        			currentDirection = 1;
        		}
        		else if (node.getX() == previousX-1){
        			currentDirection = 3;
        		}
        		else if (node.getY() == previousY+1){
        			currentDirection = 2;
        		}
        		else if (node.getY() == previousY-1){
        			currentDirection = 4;
        		}
        		
        		if (previousDirection == currentDirection){
            		shortestPathCount++;
            		if ((node.getX()==14)&&(node.getY()==19)){
            			if (shortestPathCount<10){
    	        			UDP.send("f0"+shortestPathCount+"1/");
    	        			System.out.println("f0"+shortestPathCount+"1/");
    	        		}
    	        		else {
    	        			UDP.send("f"+shortestPathCount+"1/");
    	        			System.out.println("f"+shortestPathCount+"1/");
    	        		}
            		}
            	}
        		else{
            		if (shortestPathCount!=0){
    	        		if (shortestPathCount<10){
    	        			UDP.send("f0"+shortestPathCount+"1/");
    	        			System.out.println("f0"+shortestPathCount+"1/");
    	        		}
    	        		else {
    	        			UDP.send("f"+shortestPathCount+"1/");
    	        			System.out.println("f"+shortestPathCount+"1/");
    	        		}
            		}
            		if (previousDirection % 4 + 1 == currentDirection ){
            			UDP.send("l001/");
            			System.out.println("l001/");
            		}
            		else {
            			UDP.send("r001/");
            			System.out.println("r001/");
            		}
            		
            		if ((node.getX()==14)&&(node.getY()==19)){
            			UDP.send("f011/");
            			System.out.println("f011/");
            			
            		}
            		
            		shortestPathCount = 1;
            		
            	}
        		
        		previousDirection = currentDirection;
        		previousX = node.getX();
        		previousY = node.getY();
        	
	
        	}
        	
           // map[node.getX()][node.getY()] = -1;//the result of the shortest path stored here
            System.out.println("Shortest Path: x , y"+ node.getX() +" "+ node.getY() );
            System.out.println("Previous direction"+ previousDirection +"CurrentDirection"+ currentDirection );
            System.out.println("Previous X,Y: "+ previousX +" "+ previousY );
            maze.tracer(node.getX(), node.getY());
            
            
        }
        if (currentDirection==1){
        	UDP.send("a001/");
        	System.out.println("a001/");
        }
        else{
        	UDP.send("r001/");
        	System.out.println("r001/");
        	UDP.send("a001/");
        	System.out.println("a001/");
        }
        //UDP.receive()
        
        return 1;
    }
    
    //Real SearchAlgorithm
    private List<Node> search(Node sNode,Node eNode){
        List<Node> resultList=new ArrayList<Node>();
        boolean isFind=false;
        Node node=null;
        while(openList.size()>0){
            //get the smallest f node
            node=openList.get(0);
          //if goal
            if(node.getX()==eNode.getX()&&node.getY()==eNode.getY()){
                isFind=true;
                break;
            }
            int costCloseWall = closeWall(node.getX(), node.getY()) * COST_CLOSEWALL;
            System.out.println("CLOSE WALL COST x,y, walls besides:"+node.getX()+" "+node.getY()+" "+costCloseWall);
            
            if (node.getX()==2&&node.getY()==2){//starting point, don't calculate the turn cost 
            	checkPath(node.getX(),node.getY()+1,node, eNode, COST_STRAIGHT+costCloseWall);
            	checkPath(node.getX()+1,node.getY(),node, eNode, COST_STRAIGHT+costCloseWall);
            }
            else{
	            //up
	            if((node.getY())>2){
	            	if (node.getParentNode().getY()==node.getY()+1)
	            		checkPath(node.getX(),node.getY()-1,node, eNode, COST_STRAIGHT+costCloseWall);
	            	else 
	            		checkPath(node.getX(),node.getY()-1,node, eNode, COST_TURN+costCloseWall);
	            }
	            //down
	            if((node.getY())<column-1){
	            	
	            	if (node.getParentNode().getY()==node.getY()-1)
	                		checkPath(node.getX(),node.getY()+1,node, eNode, COST_STRAIGHT+costCloseWall);
	            	else
	            		checkPath(node.getX(),node.getY()+1,node, eNode, COST_TURN+costCloseWall);
	            }
	            //left
	            if((node.getX())>2){
	            	if (node.getParentNode().getX()==node.getX()+1)
	            		checkPath(node.getX()-1,node.getY(),node, eNode, COST_STRAIGHT+costCloseWall);
	            	else 
	            		checkPath(node.getX()-1,node.getY(),node, eNode, COST_TURN+costCloseWall);
	            }
	            //right
	            if((node.getX())<row-1){
	            	if (node.getParentNode().getX()==node.getX()-1)
	            		checkPath(node.getX()+1,node.getY(),node, eNode, COST_STRAIGHT+costCloseWall);
	            	else
	            		checkPath(node.getX()+1,node.getY(),node, eNode, COST_TURN+costCloseWall);
	            }
            }
            //move from openlist to closed list
            closeList.add(openList.remove(0));
            //sort openlist
            Collections.sort(openList, new NodeFComparator());
        }
        if(isFind){
            getPath(resultList, node);
        }
        return resultList;
    }
    public int possiblePath(int x, int y) {
    	for (int i = -1;i < 2; i++){
    		for (int j = -1;j < 2; j++){
    			if (map[x+i][y+j] == 0){
    				return 0;
    			}
    		}
    		
    	}
    	return 1;
    }
    
    public int closeWall(int x, int y) {
    	int count = 0;
    	for (int i = -2;i < 2; i++){
    		if (map[x-2][y+i]==0){
    			count++;
    		}
    		if (map[x+2][y+i+1]==0){
    			count++;
    		}
    		if (map[x+i+1][y-2]==0){
    			count++;
    		}
    		if (map[x+i][y+2]==0){
    			count++;
    		}
    	}
    	return count;
    }
    
    private boolean checkPath(int x,int y,Node parentNode,Node eNode,int cost){
        Node node=new Node(x, y, parentNode);
        //if wall
        if(possiblePath(x,y) == 0){
            closeList.add(node);
            return false;
        }
        //if already in the closed path
        if(isListContains(closeList, x, y)!=-1){
            return false;
        }
        //if in the open list
        int index=-1;
        if((index=isListContains(openList, x, y))!=-1){
            //update the gf value
            if((parentNode.getG()+cost)<openList.get(index).getG()){
                node.setParentNode(parentNode);
                countG(node, eNode, cost);
                countF(node);
                openList.set(index, node);
            }
        }else{
            //if not in the openlist yet, new node to add
            node.setParentNode(parentNode);
            count(node, eNode, cost);
            openList.add(node);
        }
        return true;
    }
    
    //return index of the list element
    private int isListContains(List<Node> list,int x,int y){
        for(int i=0;i<list.size();i++){
            Node node=list.get(i);
            if(node.getX()==x&&node.getY()==y){
                return i;
            }
        }
        return -1;
    }
    
    //from the end to the start
    private void getPath(List<Node> resultList,Node node){
        if(node.getParentNode()!=null){
            getPath(resultList, node.getParentNode());
        }
        resultList.add(node);
    }
    
    //cal G H F
    private void count(Node node,Node eNode,int cost){
        countG(node, eNode, cost);
        countH(node, eNode);
        countF(node);
    }
    //G
    private void countG(Node node,Node eNode,int cost){
        if(node.getParentNode()==null){
            node.setG(cost);
        }else{
            node.setG(node.getParentNode().getG()+cost);
        }
    }
    //H
    private void countH(Node node,Node eNode){
        node.setH(Math.abs(node.getX()-eNode.getX())+Math.abs(node.getY()-eNode.getY()));
    }
    //F
    private void countF(Node node){
        node.setF(node.getG()+node.getH());
    }
    
}

//Structure
class Node {
    private int x;
    private int y;
    private Node parentNode;//parent node
    private int g;
    private int h;//mahatanD
    private int f;
    
    public Node(int x,int y,Node parentNode){
        this.x=x;
        this.y=y;
        this.parentNode=parentNode;
    }
    
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public Node getParentNode() {
        return parentNode;
    }
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }
    public int getG() {
        return g;
    }
    public void setG(int g) {
        this.g = g;
    }
    public int getH() {
        return h;
    }
    public void setH(int h) {
        this.h = h;
    }
    public int getF() {
        return f;
    }
    public void setF(int f) {
        this.f = f;
    }
}

class NodeFComparator implements Comparator<Node>{

    @Override
    public int compare(Node o1, Node o2) {
        return o1.getF()-o2.getF();
    }
    
}