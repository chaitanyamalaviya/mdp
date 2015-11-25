package mdp.algorithm.simulator;

import java.util.ArrayList;
import java.util.List;

public class TimerQueue {
	private final List<Character> queue = new ArrayList<Character>();
	
	public TimerQueue(){
		
	}
	
	public List<Character> enqueueItem(char item){
		queue.add(item);
		return queue;
	}
	
	public char dequeueItem(){
		char firstItem = queue.get(0);
		for(int i=0; i<queue.size()-1; i++){
			setItem(i, getItem(i+1));
		}
		queue.remove(queue.size()-1);
		return firstItem;
	}
	
	public int getQueueSize(){
		return queue.size();
	}
	
	public char getItem(int listNumber){
		return queue.get(listNumber);
	}
	
	public void setItem(int listNumber, char item){
		queue.set(listNumber, item);
	}
	
	public void resetQueue(){
		queue.clear();
	}
}
