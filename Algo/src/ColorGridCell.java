//package src;
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


class ColorGridCell extends JPanel {
   
   private int row;
   private int col;

   public ColorGridCell(int row, int col) {
      this.row = row;
      this.col = col;
      setBackground(Color.LIGHT_GRAY);

      addMouseListener(new MouseAdapter() {
         
//    	 boolean isPressed = false;
//    	 int i = 0;
    	 @Override
         public void mousePressed(MouseEvent e) {
//        	 isPressed = true;
        	 setBackground(Color.black);
         }
       
//         @Override  
//         public void mouseEntered(MouseEvent e) {  
//             // TODO Auto-generated method stub  
//        	 if (isPressed)
//        		 System.out.println("mouse pressed and Entered "+i++); 
//        	 
//         }  
       
       
      });
   }

  
   public int getRow() {
      return row;
   }

   public int getCol() {
      return col;
   }
}
