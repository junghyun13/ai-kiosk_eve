package View;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MyPanel extends JPanel{
    private static final long serialVersionUID = 1L;
	Image i;
    ImageIcon ii;
    
    public MyPanel(Image p_i) {
    	this.i = p_i;
    }
    public MyPanel(ImageIcon p_ii) {
    	this.ii = p_ii;
    }
    
	public void paintComponent(Graphics g){
           // super.paintComponent(g);
  
            g.drawImage(i,0,0,getWidth(),getHeight(),this);
      }
    

}
