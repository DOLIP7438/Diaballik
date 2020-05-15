package Diaballik.Vue;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ihm extends JFrame {

	private static final long serialVersionUID = 1L;
	ObjectMapper objectMapper = new ObjectMapper();
	Menu menu = new Menu(this);
	Menu m = new Menu(this);
	NewGame ng = new NewGame(this);
	ChargerPartie cp = new ChargerPartie(this, false);
	JouerReseau jr = new JouerReseau(this);
	AttenteJoueurReseau ajr = new AttenteJoueurReseau(this);
	CreerPartieReseau crr = new CreerPartieReseau(this);
	Regles r = new Regles(this);
	RejoindrePartieReseau rpr = new RejoindrePartieReseau(this);
	Image icon = Toolkit.getDefaultToolkit().getImage("src/main/java/Diaballik/Vue/img/pionA_ballon.png");  
	JButton sound = new JButton();
	JButton drapeau = new JButton();
	JMenuBar mb = new JMenuBar();
	JMenu m1 = new JMenu("Thèmes");
	JMenu m2 = new JMenu("Options");
	JMenuItem mi1 = new JMenuItem("Daltonien");
	JMenuItem mi2 = new JMenuItem("mute");
	JMenuItem mi3 = new JMenuItem("son");
	Image son, mute, drapeauFr, drapeauGB;
	
	playSound ps = new playSound();
	boolean bmute = true;
	boolean blang = true;
	
	

	public ihm() {
		
		this.setIconImage(icon); 
		this.setTitle("Menu principal");
		this.setSize(600, 510);
		
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if(msgBox.msgYesNo("Voulez-vous quitter?", "Quitter") == 0){
						System.exit(0);
	            }
	            else{
	               return;
	               }
				}
	    });
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
		sound.setBounds(this.getWidth() - 80, 75, 40, 40);
		
		try {
    		son = ImageIO.read(this.getClass().getResourceAsStream("img/sound.png")).getScaledInstance(40, 40, Image.SCALE_DEFAULT);
    		mute = ImageIO.read(this.getClass().getResourceAsStream("img/mute.png")).getScaledInstance(40, 40, Image.SCALE_DEFAULT);
    		sound.setIcon(new ImageIcon(mute));
    		drapeau.setBounds(this.getWidth() - 80, 25, 40, 40);
    	}
    	catch (Exception e) {
    		System.out.println(e);
    	}
		
		try {
    		menu.drapeauFr = ImageIO.read(this.getClass().getResourceAsStream(("img/drapeaufr.png"))).getScaledInstance(40, 40, Image.SCALE_DEFAULT);; 
    		drapeau.setIcon(new ImageIcon(menu.drapeauFr));
    	}
    	catch (Exception e) {
    		System.out.println(e);
    	}
        
		try {
			
			
			words w = objectMapper.readValue(new File("src/main/java/Diaballik/Vue/languesEn.json"), words.class);
			
					
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		drapeau.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	try {
    				if (blang == true) {
    					menu.drapeauGB = ImageIO.read(this.getClass().getResourceAsStream("img/drapeauuk.jpg")).getScaledInstance(40, 40, Image.SCALE_DEFAULT); 
    		    		drapeau.setIcon(new ImageIcon(menu.drapeauGB));
    		    		words wEn = objectMapper.readValue(new File("src/main/java/Diaballik/Vue/languesEn.json"), words.class);
    		    		menu.nouvelle.setText(wEn.newgame);
    		    		menu.charger.setText(wEn.charger);
    		    		menu.reseau.setText(wEn.reseau);
    		    		menu.regles.setText(wEn.regles);
    		    		menu.quitter.setText(wEn.quit);
    		    		ng.titre.setText(wEn.newgame);
    		    		ng.humain.setText(wEn.humain);
    		    		ng.ordinateur.setText(wEn.ordinateur);
    		    		ng.jouerContre.setText(wEn.jouerContre);
    		    		ng.nomJoueur.setText(wEn.nomJoueur1);
    		    		ng.nomJoueur2.setText(wEn.nomJoueur2);
    		    		ng.joueur1.setText(wEn.joueur1);
    		    		ng.joueur2.setText(wEn.joueur2);
    		    		ng.varianteCheckbox.setText(wEn.variante);
    		    		ng.illimite.setText(wEn.illimite);
    		    		ng.priorite.setText(wEn.priorite);
    		    		ng.choixPersoJoueur1.setText(wEn.personnaliser);
    		    		ng.choixPersoJoueur2.setText(wEn.personnaliser);
    		    		ng.duree.setText(wEn.duree);
    		    		ng.retour.setText(wEn.retour);
    		    		ng.jouer.setText(wEn.jouer);
    		    		blang = false;
    				} else {
    					drapeauFr = ImageIO.read(this.getClass().getResourceAsStream(("img/drapeaufr.png"))).getScaledInstance(40, 40, Image.SCALE_DEFAULT); 
    		    		drapeau.setIcon(new ImageIcon(menu.drapeauFr));
    		    		words wFr = objectMapper.readValue(new File("src/main/java/Diaballik/Vue/languesFr.json"), words.class);
    		    		menu.nouvelle.setText(wFr.newgame);
    		    		menu.charger.setText(wFr.charger);
    		    		menu.reseau.setText(wFr.reseau);
    		    		ng.titre.setText(wFr.newgame);
    		    		menu.regles.setText(wFr.regles);
    		    		menu.quitter.setText(wFr.quit);
    		    		ng.humain.setText(wFr.humain);
    		    		ng.ordinateur.setText(wFr.ordinateur);
    		    		ng.jouerContre.setText(wFr.jouerContre);
    		    		ng.nomJoueur.setText(wFr.nomJoueur1);
    		    		ng.nomJoueur2.setText(wFr.nomJoueur2);
    		    		ng.joueur1.setText(wFr.joueur1);
    		    		ng.joueur2.setText(wFr.joueur2);
    		    		ng.varianteCheckbox.setText(wFr.variante);
    		    		ng.illimite.setText(wFr.illimite);
    		    		ng.priorite.setText(wFr.priorite);
    		    		ng.choixPersoJoueur1.setText(wFr.personnaliser);
    		    		ng.choixPersoJoueur2.setText(wFr.personnaliser);
    		    		ng.duree.setText(wFr.duree);
    		    		ng.retour.setText(wFr.retour);
    		    		ng.jouer.setText(wFr.jouer);
    		    		blang = true;
    				}
    			}
    			catch (Exception e1) {
    				System.out.println(e1);
    			}
            } 
        } );

		
		sound.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	try {
    				if (bmute == false) { 
    		    		sound.setIcon(new ImageIcon(mute));
    		    		bmute = true;
    				} else {
    		    		sound.setIcon(new ImageIcon(son));
    		    		bmute = false;
    				}
    					
    	    	}
    	    	catch (Exception e1) {
    	    		System.out.println(e1);
    	    	}
            } 
        } );
        
		menu.add(drapeau);
		menu.add(sound);
	    this.setLocationRelativeTo(null);
	    this.add(menu);
	    this.setVisible(true);
	}
	
	
	public void fenetreNouvellePartie() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(601, 550);
		ng.add(sound);
		ng.add(drapeau);		
		this.setContentPane(ng);
		this.setTitle("Nouvelle partie");
		this.repaint();
		this.revalidate();
	}

	
	public void fenetreChargerPartie() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(800, 540);
		this.setSize(800, 550);
		cp.add(sound);
		cp.add(drapeau);
		this.setContentPane(cp);
		this.setTitle("Charger partie");
		this.repaint();
		this.revalidate();
	}
	
	
	public void fenetreJouerEnReseau() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(600, 511);
		this.setSize(600, 510);
		JouerReseau jr = new JouerReseau(this);
		jr.add(sound);
		jr.add(drapeau);
		this.setContentPane(jr);
		this.setTitle("Jouer en réseau");
		this.repaint();
		this.revalidate();
	}
	
	
	public void retourMenuPrincipal() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(600, 511);
		this.setSize(600, 510);
		this.setLocationRelativeTo(null);
		menu.add(drapeau);
		menu.add(sound);
		this.setContentPane(menu);
		this.setTitle("Menu principal");
		this.repaint();
		this.revalidate();
	}
	
	
	public void fenetreCreerPartieReseau() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(600, 401);
		this.setSize(600, 400);
		crr.add(sound);
		this.setContentPane(crr);
		this.setTitle("Créer une partie en réseau");
		this.repaint();
		this.revalidate();
	}
	
	
	public void fenetreRejoindrePartieReseau() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(600, 401);
		this.setSize(600, 400);
		rpr.add(sound);
		this.setContentPane(rpr);
		this.setTitle("Rejoindre une partie");
		this.repaint();
		this.revalidate();
	}
	
	
	public void fenetreAttenteJoueurReseau() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(600, 401);
		this.setSize(600, 400);
		ajr.add(sound);
		this.setContentPane(ajr);
		this.setTitle("Attente du 2ème joueur");
		this.repaint();
		this.revalidate();
	}
	
	
	public void fenetreRegles() {
		ps.play("son/buttonClick.wav", bmute);
		this.setSize(800, 620);
		this.setLocationRelativeTo(null);
		this.setContentPane(r);
		this.setTitle("Règles du jeu");
		this.repaint();
		this.revalidate();
	}
	
	
	public void quit() {
		ps.play("son/buttonClick.wav", bmute);
		msgBox.MessageBox("Voulez-vous quitter le jeu ? ", "Quitter", this);
	}
		
}
