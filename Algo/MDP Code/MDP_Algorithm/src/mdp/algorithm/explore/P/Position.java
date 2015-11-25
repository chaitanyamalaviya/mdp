package mdp.algorithm.explore.P;

public class Position {
	
	private int x;
	private int y;
	private int ori;
	
	public Position(int x, int y, int ori){
		this.x = x;
		this.y = y;
		this.ori = ori;
	}
	
	public void setX(int x){
		this.x = x;
	}
	public int getX(){
		return x;
	}
	public void setY(int y){
		this.y = y;
	}
	public int getY(){
		return y;
	}
	public void setOri(int ori){
		this.ori = ori;
	}
	public int getOri(){
		return ori;
	}

}
