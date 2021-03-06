package Diaballik.Vue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Regles extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton retour = new JButton("<");
	ihm i;
	Image regles1, regles2;
	Graphics2D drawable;
	public Regles(ihm ihm) {
		i = ihm;
		this.setLayout(null);
		
		retour.setBounds(10, 10, 50, 50);
		
		retour.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.retourMenuPrincipal();
            } 
        } );
		
		this.add(retour);
		this.setVisible(true);
	}
	
	
	public void paintComponent(Graphics g){
        drawable = (Graphics2D) g;
        afficherLogo();
    }
    
    public void afficherLogo() {
    	try {
    		regles1 = ImageIO.read(this.getClass().getResourceAsStream("/reglesDuJeu1.png"));
    		regles2 = ImageIO.read(this.getClass().getResourceAsStream("/reglesDuJeu2.png"));
    		drawable.drawImage(regles1, 70, 0, 326, 544, null);
    		drawable.drawImage(regles2, 400, 45, 319, 361, null);
    	}
    	catch (Exception e) {
    		
    	}
    }
}
