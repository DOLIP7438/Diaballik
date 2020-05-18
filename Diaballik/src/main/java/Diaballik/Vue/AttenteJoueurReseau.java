package Diaballik.Vue;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AttenteJoueurReseau extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel titre = new JLabel("Attente du 2ème joueur");
	JLabel codelabel = new JLabel("Code");
	JButton retour = new JButton("Retour");
	ImageIcon gif;
	JLabel gifContainer;
	ihm i;

	Graphics2D drawable;

	public String genAlea(String code){
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	    for(int x=0;x<5;x++)
	    {
	       int i = (int)Math.floor(Math.random() * 62); // Si tu supprimes des lettres tu diminues ce nb
	       code += chars.charAt(i);
		}	   
		return code;
	}

	public AttenteJoueurReseau(ihm ihm) {
		i = ihm;
		String code = genAlea("");
		final JTextArea c = new JTextArea(code);
		this.setLayout(null);
		try {
			gif = new ImageIcon(getClass().getResource("img/gifchargement.gif"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		gifContainer = new JLabel(gif);
		
		
		i.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
        		titre.setBounds((i.getWidth()/2) - 160, (i.getHeight()/4) - 100, 400, 100);
        		c.setBounds((i.getWidth()/2) - 90, (i.getHeight()/4) + 110, 250, 20);
            	codelabel.setBounds((i.getWidth()/2) - 170, (i.getHeight()/4) + 110, 100, 20);
         		retour.setBounds((i.getWidth()/2) - 70, (i.getHeight()/4) + 200, 120, 40);
         		gifContainer.setBounds((i.getWidth()/2) - 100, (i.getHeight()/4), 200, 100);
         		i.sound.setBounds(i.getWidth() - 80, 75, 40, 40);
            }
		});
		
		retour.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	i.fenetreCreerPartieReseau();
            } 
        } );

		this.add(codelabel);
		this.add(c);
		this.add(retour);
		this.add(titre);
		this.add(gifContainer);
		Font font = new Font("Arial",Font.BOLD,30);
		Font fontnomJoueur = new Font("Arial",Font.BOLD,15);
		c.setFont(fontnomJoueur);
		codelabel.setFont(fontnomJoueur);
		//Reseau.Client C = new Reseau.Client(code); //client hote
		
		titre.setFont(font);
		this.setVisible(true);
	}

}
