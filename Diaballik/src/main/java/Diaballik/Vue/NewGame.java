package Diaballik.Vue;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NewGame extends JPanel implements ActionListener{

	JLabel titre = new JLabel("Nouvelle partie");
	JLabel duree = new JLabel("Duree d'un tour :");
	JLabel priorite = new JLabel("Joue en premier : ");
	JButton retour = new JButton("Retour");
	JButton jouer = new JButton("Jouer");
	JButton humain = new JButton("Humain");
	JButton ordinateur = new JButton("Ordinateur");
	JButton illimite = new JButton("Illimité");
	JButton uneMin = new JButton("1 min");
	JButton deuxMin = new JButton("2 min");
	JButton troisMin = new JButton("3 min");
	JButton joueur1 = new JButton("Joueur 1");
	JButton joueur2 = new JButton("Joueur 2");
	
	
	public NewGame() {
		this.setLayout(null);
		titre.setBounds(240, 0, 300, 100);
		duree.setBounds(100, 120, 100, 100);
		priorite.setBounds(100, 210, 150, 120);
		jouer.setBounds(350, 390, 120, 40);
		retour.setBounds(210, 390, 120, 40);
		humain.setBounds(210, 100, 120, 40);
		ordinateur.setBounds(350, 100, 120, 40);
		illimite.setBounds(100, 200, 100, 40);
		uneMin.setBounds(220, 200, 100, 40);
		deuxMin.setBounds(340, 200, 100, 40);
		troisMin.setBounds(460, 200, 100, 40);
		joueur2.setBounds(350, 300, 120, 40);
		joueur1.setBounds(210, 300, 120, 40);
		

		Font font = new Font("Arial",Font.BOLD,30);
		titre.setFont(font);
		
		this.add(joueur1);
		this.add(joueur2);
		this.add(priorite);
		this.add(illimite);
		this.add(uneMin);
		this.add(deuxMin);
		this.add(troisMin);
		this.add(duree);
		this.add(ordinateur);
		this.add(humain);
		this.add(jouer);
		this.add(retour);
		this.add(titre);
		
		retour.addActionListener(this);
		//this.add(this);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == retour) {
			new Menu();
		}
		if(arg0.getSource() == jouer) {
			
		}
	}
}