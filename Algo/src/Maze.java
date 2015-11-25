//package src;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Maze {
	// The thread that the solver runs in
	public static Thread solvingThread;
	//public static Thread solvingThread1;
	private static ExecutorService ex;
	/**
	 * The main program to create the user interface
	 * @param args none expected
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle("Maze Solver");
		f.setSize(new Dimension(750, 600));
		
		// Create the panel that will display the maze
		final TwoDimGrid componentsPanel = new TwoDimGrid();
		
		 
		Container contentPane = f.getContentPane();
		contentPane.add(componentsPanel, BorderLayout.CENTER);
		
		// Solve button
		JPanel buttonPanel = new JPanel();
		JButton startButton = new JButton("Solve");
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Create a thread to animation the solution of the maze
				Exploration1 solver = new Exploration1(componentsPanel);
				//RealExploration solver = new RealExploration(componentsPanel);
				solvingThread = new Thread(solver);
				solvingThread.start();
				//solvingThread.stop();
				JFrame g = new JFrame();
				g.setSize(new Dimension(500, 400));
				g.setLocation(1000, 100);
				Container contentPane1 = g.getContentPane();
			
			}
			
		});
		buttonPanel.add(startButton);
		
		// Button to create a new maze
		JButton newMazeButton = new JButton ("New maze");
		newMazeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Stop the solving thread if there is one
				if (solvingThread != null && solvingThread.isAlive()) {
					solvingThread.interrupt();
				}
				
				// Create a new maze
				componentsPanel.newMaze();
				
			}
			
		});
		buttonPanel.add(newMazeButton);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton updatedMap = new JButton ("Timer Solve");
		updatedMap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//new Reminder(3);
				
				Exploration1 solver = new Exploration1(componentsPanel);
				solvingThread = new Thread(solver);
				solvingThread.start();
				JFrame g = new JFrame();
				g.setSize(new Dimension(500, 400));
				Container contentPane1 = g.getContentPane();
				
			}
			
		});
		buttonPanel.add(updatedMap);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton percentage = new JButton ("% Solve");
		percentage.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				Exploration1 solver = new Exploration1(componentsPanel);
				solvingThread = new Thread(solver);
				solvingThread.start();
				JFrame h = new JFrame();
				h.setSize(new Dimension(500, 400));
				
			}
			
		});
		buttonPanel.add(percentage);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		// Display the window.
		f.setVisible(true);
		
		

	}

}
