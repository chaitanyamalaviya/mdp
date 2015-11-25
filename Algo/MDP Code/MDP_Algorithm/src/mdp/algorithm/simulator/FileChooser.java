package mdp.algorithm.simulator;

import java.io.*;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FileChooser extends JPanel implements ActionListener {
    static private final String newline = "\n";
    JButton openButton;
    JButton loadMapButton;
    JTextArea log;
    JFileChooser fc;
    Simulator sim = new Simulator();
    String fileName = "";
    
    public FileChooser() {
        super(new BorderLayout());
        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        fc = new JFileChooser();

        openButton = new JButton("Open a Text File");
        openButton.addActionListener(this);

        loadMapButton = new JButton("Load Map");
        loadMapButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(loadMapButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(FileChooser.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                log.append("Opening: " + file.getName() + "." + newline);
            } else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());

        //Handle loadMap button action.
        } else if (e.getSource() == loadMapButton) {
        	sim.terminateAndReset();
        	File file = fc.getSelectedFile();
        	fileName = file.getPath();
        	System.out.print(fileName);
        	getFileContents();
        }
    }
    
    public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("FileChooserDemo");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new FileChooser());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void getFileContents(){
    	try{
    		System.out.println(fileName);
    		File inFile = new File(fileName);
            Scanner input = new Scanner(inFile);
            String fileContents = "";
            while(input.hasNext())
            	fileContents += input.next();
            System.out.println(fileContents);
            sim.loadMapPlotObstacles(fileContents);
		}
		catch(Exception msg){
			System.out.print(msg);
		}
    }
}