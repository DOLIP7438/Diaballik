package Reseau.Serveur;

import java.util.Scanner;

import Diaballik.Models.Jeu;



public class Partie implements Runnable{
    private Thread T;
    public Jeu j;
    public Partie(){
    	j = new Diaballik.Models.Jeu();
        T = new Thread(this);
		T.start();
    }

    

    public static void paraPartie(Diaballik.Models.ConfigJeu C){
        C.setMode(Diaballik.Models.ConfigJeu.Mode.humain);
        Menu menu = new Menu(C);
    }
    
    public void run() {
        Diaballik.Models.ConfigJeu C = new Diaballik.Models.ConfigJeu();
        paraPartie(C);
		
    }
    
    
}