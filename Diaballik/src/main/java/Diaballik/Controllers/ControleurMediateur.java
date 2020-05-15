package Diaballik.Controllers;

import Diaballik.Models.Jeu;
import Diaballik.Vue.CollecteurEvenements;
import Diaballik.Vue.Plateau;
import Diaballik.IA.*;

public class ControleurMediateur implements CollecteurEvenements {

	Jeu jeu;
	Plateau vue;

	public ControleurMediateur(Jeu j) {
		jeu = j;
	}

	@Override
	public void clicSouris(int l, int c) {
		System.out.printf("Mouse position : (%d,%d)\n", l, c);
		jeu.SelectionPiece(l, c);
		System.out.println("Score de plateau : " + Evaluator.scoreOfBoard(jeu.tr));

	}

	public void annule() {
	
		jeu.jctrl_z();
		System.out.println("Annulation");
	}

	public void refait() {
		jeu.tr.PrintTerrain();
		jeu.tr.ctrl_y();
		System.out.println("Refait");
		jeu.tr.PrintTerrain();
	}

	public void finTour() {
		jeu.FinTour();
	}

	public void save() {
		jeu.ExportGameToJSON(jeu);
		System.out.println("Sauvegarde de la partie");
	}

	public void replay() {
		jeu.init();
		jeu.start();
	}

	@Override
	public void toucheClavier(String touche) {
		switch (touche) {
			case "Undo":
				annule();
				break;
			case "FinTour":
				finTour();
				break;
			case "Redo":
				refait();
				break;
			case "Quit":
				System.exit(0);
				break;
			case "Full":
				vue.toggleFullscreen();
				break;
			case "Save":
				save();
				break;
			case "Replay":
				replay();
				break;
			default:
				System.out.println("Touche inconnue : " + touche);
		}
	}

	@Override
	public void ajouteInterfaceUtilisateur(Plateau v) {
		vue = v;
	}

	@Override
	public void tictac() {
		// TODO Auto-generated method stub

	}

}
