package Diaballik.Models;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Scanner;
import Diaballik.Vue.*;

import Diaballik.Controllers.TerrainUtils;
import Diaballik.IA.IA;
//import Diaballik.Controllers.TerrainUtils;
import Diaballik.Models.*;
import Diaballik.Patterns.Observable;

public class Jeu extends Observable {
    public Terrain tr;
    //Joueur qui a la main
    public Joueur joueur;

    public Jeu(){
        tr = new Terrain();
        tr.Create();
        //white_to_move(tr);
    }

    public Jeu(Terrain terrain){
        tr = terrain;
        //white_to_move(tr);

    }

    public static Piece getPiece(Terrain tr, PieceType t) {
        Scanner sc = new Scanner(System.in);
        String ligne;
        String[] ligne_split;
        int pieceL;
        int pieceC;
        Piece res = tr.getTerrain()[0][0];
        boolean done = false;
        while (!done) {
            System.out.println("Entrez les coordonnées de la pièce " + t + " : l c ");
            ligne = sc.nextLine();
            ligne_split = ligne.split(" ");
            pieceL = Integer.parseInt(ligne_split[0]);
            pieceC = Integer.parseInt(ligne_split[1]);
            if ((pieceL > tr.taille()) || (pieceC > tr.taille())) {
                sc.close();
                throw new IllegalAccessError("Erreur dans getPiece : l ou c > taille");
            }
            res = tr.getTerrain()[pieceL][pieceC];
            if ((res.Type == t) && (!res.HasBall)) {
                done = true;
            } else {
                if (res.HasBall) {
                    System.out.println("Erreur : Vous ne pouvez pas bouger la piece qui a la balle");
                }
                if (res.Type != t) {
                    System.out.println("Erreur : Veuillez choisir une piece de type " + t);
                }
            }
        }

        // Je sais pas pourquoi, j'ai pas envie de savoir pourquoi, je ne veux même plus
        // chercher à savoir pourquoi,
        // mais pour une raison que j'ignore, si on ferme le scanner ici alors plus rien
        // ne marche
        // Du coup, juste laissez-le ouvert et osef du warning
        // sc.close();
        return res;
    }

    // Ici encore on utilise l'entrée standard. A modifier plus tard pour l'ihm et
    // l'ia
    public static void white_to_move(Terrain tr) {
        int nbMove = 2; // on a droit à deux mouvements max
        int passe_faite = 1; // une passe
        Scanner sc = new Scanner(System.in);
        char choix;
        Piece from;
        Piece to;
        // tant qu'il y a un truc à faire
        while (nbMove > 0 || passe_faite > 0) {
            tr.PrintTerrain();
            System.out.println("tour des blancs");
            System.out.println("Nombre de mouvements restants : " + nbMove);
            System.out.println("Nombre de passes restantes : " + passe_faite);
            System.out.println(
                    "entrez 'p' pour faire une passe, 'm' pour faire un mouvement, ou 'q' pour passer votre tour");
            choix = sc.nextLine().charAt(0);
            switch (choix) {
                case 'p':
                    // passe
                    if (passe_faite == 1) {
                        from = tr.getPieceWithBall(PieceType.White);
                        System.out.println("Les passes possibles sont : " + from.passesPossibles());
                        to = getPiece(tr, PieceType.White);
                        TerrainUtils.passeWrapper(from, to);
                        passe_faite = 0;
                        // victoire?
                        if (to.Position.l == 0) {
                            System.out.println("Les blancs ont gagnés !");
                            System.exit(0); // La j'ai fais un system.exit mais on changera plus tard si il le faut
                        }
                    } else {
                        System.out.println("Vous ne pourrez faire qu'une seule passe");
                    }
                    break;
                case 'm':
                    // mouvement
                    from = getPiece(tr, PieceType.White);
                    ArrayList<Position> ar = from.PossiblePositions();
                    System.out.println("Positions possibles : " + ar);
                    to = getPiece(tr, PieceType.Empty);
                    // On verifie si c est bien un mouvement legal

                    if (ar.contains(to.Position)) {
                        ArrayList<Position> diag = from.getDiagonals();
                        if (diag.contains(to.Position)) {
                            // Si c'est un mouvement en diagonale, on prend deux coups
                            nbMove -= 2;
                        } else {
                            // Sinon 1 seul
                            nbMove -= 1;
                        }
                        if (nbMove >= 0) {
                            from.move(to.Position.l, to.Position.c);
                        } else {
                            System.out.println("Vous n'avez plus de mouvements");
                            nbMove = 0;
                        }
                    } else {
                        throw new IllegalAccessError("Mouvement illegal");
                    }
                    break;
                case 'q':
                    // end turn
                    nbMove = 0;
                    passe_faite = 0;
                    black_to_move(tr);
                    break;
                default:
                    System.out.println("Choix invalide");
                    break;
            }
        }
        sc.close();
    }

    public static void black_to_move(Terrain tr) {
        int nbMove = 2; // on a droit à deux mouvements max
        int passe_faite = 1; // une passe
        Scanner sc = new Scanner(System.in);
        char choix;
        Piece from;
        Piece to;
        // tant qu'il y a un truc à faire
        while (nbMove > 0 || passe_faite > 0) {
            tr.PrintTerrain();
            System.out.println("tour des noirs");
            System.out.println("Nombre de mouvements restants : " + nbMove);
            System.out.println("Nombre de passes restantes : " + passe_faite);
            System.out.println(
                    "entrez 'p' pour faire une passe, 'm' pour faire un mouvement, ou 'q' pour passer votre tour");
            choix = sc.nextLine().charAt(0);
            switch (choix) {
                case 'p':
                    // passe
                    if (passe_faite == 1) {
                        from = tr.getPieceWithBall(PieceType.Black);
                        System.out.println("Les passes possibles sont : " + from.passesPossibles());
                        to = getPiece(tr, PieceType.Black);
                        TerrainUtils.passeWrapper(from, to);
                        passe_faite = 0;
                        // victoire?
                        if (to.Position.l == tr.taille() - 1) {
                            System.out.println("Les noirs ont gagnés !");
                            System.exit(0); // La j'ai fais un system.exit mais on changera plus tard si il le faut
                        }
                    } else {
                        System.out.println("Vous ne pourrez faire qu'une seule passe");
                    }
                    break;
                case 'm':
                    // mouvement
                    from = getPiece(tr, PieceType.Black);
                    ArrayList<Position> ar = from.PossiblePositions();
                    System.out.println("Positions possibles : " + ar);
                    to = getPiece(tr, PieceType.Empty);
                    // On verifie si c est bien un mouvement legal
                    if (ar.contains(to.Position)) {
                        ArrayList<Position> diag = from.getDiagonals();
                        if (diag.contains(to.Position)) {
                            // Si c'est un mouvement en diagonale, on prend deux coups
                            nbMove -= 2;
                        } else {
                            // Sinon 1 seul ou 2 selon si on a avancé d'une ou de deux cases
                            nbMove -= Math.abs((from.Position.l + from.Position.c)-(to.Position.l + to.Position.c));
                        }
                        if (nbMove >= 0) {
                            from.move(to.Position.l, to.Position.c);
                        } else {
                            System.out.println("Vous n'avez plus de mouvements");
                            nbMove = 0;
                        }
                    } else {
                        throw new IllegalAccessError("Mouvement illegal");
                    }
                    break;

                case 'q':
                    // end turn
                    nbMove = 0;
                    passe_faite = 0;
                    white_to_move(tr);
                    break;
                default:
                    System.out.println("Choix invalide");
                    break;
            }
        }
        sc.close();
    }
    
}