package Diaballik.Vue;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Menu extends JPanel {

	private static final long serialVersionUID = 1L;
	JButton nouvelle = new JButton("Nouvelle partie");
	JButton charger = new JButton("Charger partie");
	JButton reseau = new JButton("Jouer en réseau");
	JButton regles = new JButton("Règles");
	JButton quitter = new JButton("Quitter");
	JButton drapeau = new JButton();
	JMenuBar mb = new JMenuBar();
	JMenu m1 = new JMenu("Thèmes");
	JMenu m2 = new JMenu("Options");
	JMenuItem mi1 = new JMenuItem("Daltonien");
	JMenuItem mi2 = new JMenuItem("mute");
	JMenuItem mi3 = new JMenuItem("son");
	Image logo, drapeauFr, drapeauGB;
	playSound ps = new playSound();
	ihm i;
    Graphics2D drawable;
    boolean bson;
    boolean blang = true;
  
     	
	
	public Menu(ihm ihm) {
		i = ihm;
		bson = i.bmute;
		m1.add(mi1);
		m2.add(mi2);
		mb.add(m1);
		mb.add(m2);
		mb.setBounds(0, 0, 600, 20);
		this.add(mb);
		this.setSize(600, 510);

        this.setLayout(null);
        
        i.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent evt) {
                    nouvelle.setBounds((i.getWidth()/2) - 80 , (i.getHeight()/4) + 20,150 , 50 + (i.getHeight()/150));
                    charger.setBounds((i.getWidth()/2) - 80, (i.getHeight()/4) + 80, 150, 50 + (i.getHeight()/150));
                    reseau.setBounds((i.getWidth()/2) - 80, (i.getHeight()/4) + 140, 150, 50 + (i.getHeight()/150));
                    regles.setBounds((i.getWidth()/2) - 80, (i.getHeight()/4) + 200, 150, 50 + (i.getHeight()/150));
                    quitter.setBounds((i.getWidth()/2) - 80, (i.getHeight()/4) + 260, 150, 50 + (i.getHeight()/150));
                    drapeau.setBounds(i.getWidth() - 80, 25, 40, 40);
                    i.sound.setBounds(i.getWidth() - 80, 75, 40, 40);
                }
        });
        
        try {
    		drapeauFr = ImageIO.read(this.getClass().getResourceAsStream(("img/drapeaufr.png"))).getScaledInstance(40, 40, Image.SCALE_DEFAULT);; 
    		drapeau.setIcon(new ImageIcon(drapeauFr));
    	}
    	catch (Exception e) {
    		System.out.println(e);
    	}
        
        this.add(nouvelle);
        this.add(charger);
        this.add(reseau);
        this.add(regles);
        this.add(quitter);
        this.add(drapeau);
        

        nouvelle.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.fenetreNouvellePartie();
            } 
        } );
        
        charger.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.fenetreChargerPartie();
            } 
        } );
        
        reseau.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.fenetreJouerEnReseau();
            } 
        } );
        
        regles.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.fenetreRegles();
            } 
        } );
        
        quitter.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.quit();
            } 
        } );
        
        drapeau.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	try {
    				if (blang == true) {
    					drapeauGB = ImageIO.read(this.getClass().getResourceAsStream("img/drapeauuk.jpg")).getScaledInstance(40, 40, Image.SCALE_DEFAULT); 
    		    		drapeau.setIcon(new ImageIcon(drapeauGB));
    		    		blang = false;
    				} else {
    					drapeauFr = ImageIO.read(this.getClass().getResourceAsStream(("img/drapeaufr.png"))).getScaledInstance(40, 40, Image.SCALE_DEFAULT); 
    		    		drapeau.setIcon(new ImageIcon(drapeauFr));
    		    		blang = true;
    				}
    			}
    			catch (Exception e1) {
    				System.out.println(e1);
    			}
            } 
        } );
  
        mi2.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	try {
    				if (bson == false) { 
    		    		i.sound.setIcon(new ImageIcon(i.mute));
    		    		m2.remove(mi2);
    		    		m2.add(mi3);
    		    		bson = true;
    		    		i.bmute = bson;
    				} else {
    		    		i.sound.setIcon(new ImageIcon(i.son));
    		    		m2.remove(mi3);
    		    		m2.add(mi2);
    		    		bson = false;
    		    		i.bmute = bson;
    				}
    					
    	    	}
    	    	catch (Exception e1) {
    	    		System.out.println(e1);
    	    	}
            } 
        } );
        
        mi3.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	try {
    				if (bson == false) { 
    		    		i.sound.setIcon(new ImageIcon(i.mute));
    		    		m2.remove(mi2);
    		    		m2.add(mi3);
    		    		bson = true;
    		    		i.bmute = bson;
    				} else {
    		    		i.sound.setIcon(new ImageIcon(i.son));
    		    		m2.remove(mi3);
    		    		m2.add(mi2);
    		    		bson = false;
    		    		i.bmute = bson;
    				}
    					
    	    	}
    	    	catch (Exception e1) {
    	    		System.out.println(e1);
    	    	}
            } 
        } );
       
        this.setVisible(true);
	
	}
	
	public void paintComponent(Graphics g){
        drawable = (Graphics2D) g;
        afficherLogo();
    }
    
    public void afficherLogo() {
    	try {
    		logo = ImageIO.read(this.getClass().getResourceAsStream("img/logo.png"));
    		drawable.drawImage(logo, (i.getWidth()/2) - 130, (i.getHeight()/25) + 20, 250, (i.getHeight()/5), null);
    	}
    	catch (Exception e) {
    		System.out.println(e);
    	}
    }
    	
}
