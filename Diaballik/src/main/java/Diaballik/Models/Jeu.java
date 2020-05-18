package Diaballik.Models;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.SwingUtilities;

import Diaballik.Controllers.TerrainUtils;
import Diaballik.Models.IA.*;
import Diaballik.Models.ConfigJeu.Mode;
import Diaballik.Patterns.Observable;
import Diaballik.Vue.Plateau;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jeu extends Observable {
    public Terrain tr;
    public Joueur joueur1;
    public Joueur joueur2;
    public Joueur joueurCourant;
    public boolean gameOver = false;
    Piece from = null;
    Piece to = null;
    private ArrayList<Position> listeMarque = new ArrayList<Position>();
    private ArrayList<Position> listePositionsPossibles = new ArrayList<Position>();
    private boolean IA;
    Random_IA I;
    public ConfigJeu config;
    IaRandomIHM iaRandomIHM;
    IA_easy iaEasy;
    MiniMax minimax;
    public ArrayList<Couple> listeDeplacementJ1 = new ArrayList<Couple>();
    public ArrayList<Couple> listeDeplacementJ2 = new ArrayList<Couple>();
    public ArrayList<Couple> listePasseJ1 = new ArrayList<Couple>();
    public ArrayList<Couple> listePasseJ2 = new ArrayList<Couple>();
    InfCoups infc = new InfCoups(tr, joueurCourant, 2, 1); // j'en ai besoin pour le ctrl-z
    Stack<Couple> stackZ = new Stack<Couple>();
    Stack<Couple> stackY = new Stack<Couple>();
    public boolean antijeuBool;
    public boolean IaVSIa = false;
    public FromTo suggetionCoup = null;

    public Jeu() {
        tr = new Terrain();
        tr.Create();
    }

    public void init() {
        tr = new Terrain();
        tr.Create();
        listeMarque.clear();
        listePositionsPossibles.clear();
        clearListe();
        gameOver = false;
        from = to = null;
        antijeuBool = false;

    }

    public void configurer(ConfigJeu c) {
        config = c;
    }

    public void start() {
        antijeuBool = false;
        if (config.getVariante())
            tr.initVariante();
        if (config.getMode() == Mode.ordinateur) {
            IA = true;
            joueur1 = new Joueur(TypeJoueur.Joueur1, PieceType.White, 2, 1, config.getName1());
            joueur2 = new Joueur(TypeJoueur.IA, PieceType.Black, 2, 1, config.getName3());

            iaRandomIHM = new IaRandomIHM(this);
            iaEasy = new IA_easy(this);
            minimax = new MiniMax(this, PieceType.Black);

        } else {
            IA = false;
            joueur1 = new Joueur(TypeJoueur.Joueur1, PieceType.White, 2, 1, config.getName1());
            joueur2 = new Joueur(TypeJoueur.Joueur2, PieceType.Black, 2, 1, config.getName2());
        }

        if (config.getP1First() == true)
            joueurCourant = joueur1;
        else
            joueurCourant = joueur2;

        tr._jeuParent = this;
        metAJour();
        // IA joue en premier
        if (IA && joueurCourant == joueur2) {
            switch (config.getIALevel()) {
                case moyen:
                    iaEasy.joueTourIAEasy();
                    break;
                case facile:
                    iaRandomIHM.JoueTourIARand();
                    break;
                case difficile:
                    minimax.loadingScreen.Show();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                minimax.loadingScreen.Show();
                                Runnable r = new Runnable() {
                                    public void run() {
                                        minimax.VanillaMiniMax(new State(tr._jeuParent), 12, true);
                                        State bestState = minimax.bestMove;
                                        JoueTourIAMiniMax(bestState);
                                        minimax.loadingScreen.Hide();
                                    }
                                };
                                new Thread(r).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        } else if (IaVSIa) {
            iaRandomIHM.JoueTourIARand();
        }

    }

    public Jeu(Terrain terrain) {
        tr = terrain;

    }

    public void FinTour() {
        if (gameOver)
            return;
        joueurCourant.nbMove = 2;
        joueurCourant.passeDispo = 1;
        if (joueurCourant == joueur1) {
            joueurCourant = joueur2;
            listeDeplacementJ2.clear();
            listePasseJ2.clear();
            if (IA) {
                switch (config.getIALevel()) {
                    case moyen:
                        iaEasy.joueTourIAEasy();
                        break;
                    case facile:
                        iaRandomIHM.JoueTourIARand();
                        break;
                    case difficile:

                        minimax.loadingScreen.Show();
                        Runnable r = new Runnable() {
                            public void run() {
                                minimax.VanillaMiniMax(new State(tr._jeuParent), 12, true);
                                State bestState = minimax.bestMove;
                                JoueTourIAMiniMax(bestState);
                                minimax.loadingScreen.Hide();
                            }
                        };
                        new Thread(r).start();

                        // this.tr._terrain = bestState.Terrain.Copy(this.tr);
                        // this.tr.PrintTerrain();
                        //
                        break;
                    default:
                        break;
                }

            }
        } else {
            joueurCourant = joueur1;
            listeDeplacementJ1.clear();
            listePasseJ1.clear();
            if (IaVSIa) {
                iaRandomIHM.JoueTourIARand();
            }
        }
        if (config.getTimer() != ConfigJeu.Timer.illimite) {
            if (config.getTimer() == ConfigJeu.Timer.un)
                Plateau.setX(10000);
            else if (config.getTimer() == ConfigJeu.Timer.deux)
                Plateau.setX(30000);
            else if (config.getTimer() == ConfigJeu.Timer.trois)
                Plateau.setX(60000);
            else
                Plateau.setX(60000);
        }
        metAJour();
        RetirerMarque();
    }

    private boolean PieceAuJoueur(Piece select) {
        if (select.Type == PieceType.White && joueurCourant == joueur1)
            return true;
        else if (select.Type == PieceType.Black && joueurCourant == joueur2 && !IA)
            return true;
        else
            return false;
    }

    public void SelectionPiece(int l, int c) {
        suggetionCoup = null;
        if ((0 > l || l > 6 || 0 > c || c > 6) || gameOver)
            return;
        Piece select = tr._terrain[l][c];
        // première sélection d'une piece qui nous appartient si aucune sélection
        // précedemment
        if (from == null && PieceAuJoueur(select)) {
            from = select;
            if ((!select.HasBall))
                SelectionDeplacement(from);
            else {
                SelectionPasse(from);
            }
        } else if (from != null) {
            to = select;
            if ((from.HasBall) && PieceAuJoueur(to)) {
                if (from != to)
                    Passe();
                else {
                    RetirerMarque();
                    from = to = null;
                }
            } else {
                Deplacement();
            }
        }
        // mauvaise selection, reinitialisation de from et to
        else {
            RetirerMarque();
            from = to = null;
        }
        if (joueurCourant.nbMove == 0 && joueurCourant.passeDispo == 0)
            FinTour();
        metAJour();

    }

    public void SelectionPieceIA(Piece select) {

        if (gameOver)
            return;
        // Piece select = tr._terrain[l][c];
        if (from == null) {
            from = select;
            if ((!select.HasBall))
                SelectionDeplacement(from);
            else {
                SelectionPasse(from);
            }
        } else if (from != null) {
            to = select;
            if ((from.HasBall)) {
                if (from != to) {
                    Passe();
                } else {
                    RetirerMarque();
                    from = to = null;
                }
            } else {
                try {
                    Deplacement();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        // mauvaise selection, reinitialisation de from et to
        else {
            RetirerMarque();
            from = to = null;
        }
        metAJour();

    }

    public void SelectionDeplacement(Piece select) {
        // tr._terrain[l][c].SelectionDeplacement = true;
        int l = select.Position.l;
        int c = select.Position.c;
        getPiecePos(select).SelectionDeplacement = true;
        // Piece select = tr._terrain[l][c];
        // System.out.printf("Selection déplacement : Piece position (%d,%d)\n",
        // select.Position.l, select.Position.c);
        listeMarque.add(new Position(l, c));
        listePositionsPossibles = from.PossiblePositions(joueurCourant.nbMove);
        for (Position pos : listePositionsPossibles) {
            int li = pos.l;
            int co = pos.c;
            tr._terrain[li][co].PossibleDeplacement = true;
            listeMarque.add(pos);
        }
    }

    public void SelectionPasse(Piece select) {
        getPiecePos(select).SelectionPasse = true;
        listeMarque.add(select.Position);
        if (joueurCourant.passeDispo == 1) {
            ArrayList<Position> ar = from.passesPossibles();
            for (Position pos : ar) {
                int l = pos.l;
                int c = pos.c;
                tr._terrain[l][c].PossiblePasse = true;
                listeMarque.add(pos);
            }
        }
    }

    public void Passe() {
        if (joueurCourant.passeDispo == 1) {
            infc.passes = 1;
            this.tr.updateStack(joueurCourant.nbMove, 1);
            if (TerrainUtils.passeWrapper2(from, to) == true) {
                joueurCourant.passeDispo = 0;
                if (joueurCourant == joueur1)
                    listePasseJ1.add(new Couple(from.Position, to.Position));
                else
                    listePasseJ2.add(new Couple(from.Position, to.Position));
                gameOver = tr.victoire() != PieceType.Empty;
            }
        }
        RetirerMarque();
        from = to = null;
    }

    public void Deplacement() {
        if (listePositionsPossibles.contains(to.Position)) {
            ArrayList<Position> diag = from.getDiagonals();
            int temp = joueurCourant.nbMove;
            if (diag.contains(to.Position)) {
                // Si c'est un mouvement en diagonale, on prend deux coups
                joueurCourant.nbMove -= 2;
            } else {
                // Sinon 1 seul ou 2 selon si on a avancé d'une ou de deux cases
                joueurCourant.nbMove -= Math.abs((from.Position.l + from.Position.c) - (to.Position.l + to.Position.c));
            }
            if (joueurCourant.nbMove >= 0) {
                this.tr.updateStack(temp, joueurCourant.passeDispo);
                this.tr.ctrly.clear();
                from.move(to.Position.l, to.Position.c);
                infc.moves = temp;
                if (joueurCourant == joueur1)
                    listeDeplacementJ1.add(new Couple(from.Position, to.Position));
                else
                    listeDeplacementJ2.add(new Couple(from.Position, to.Position));
                gameOver = antijeuBool = antijeu();

            } else {
                joueurCourant.nbMove = temp;
            }
        }
        listePositionsPossibles.clear();
        RetirerMarque();
        from = to = null;
    }

    public void RetirerMarque() {

        Iterator<Position> itr = listeMarque.iterator();
        while (itr.hasNext()) {
            Position pos = (Position) itr.next();
            int l = pos.l;
            int c = pos.c;
            tr._terrain[l][c].PossiblePasse = false;
            tr._terrain[l][c].PossibleDeplacement = false;
            tr._terrain[l][c].SelectionPasse = false;
            tr._terrain[l][c].SelectionDeplacement = false;
            itr.remove();
        }
    }

    private Piece getPiecePos(Piece piece) {
        return tr._terrain[piece.Position.l][piece.Position.c];
    }

    // retourne vrai s'il y a antijeu
    public boolean antijeu() {
        int cpt = 0;
        int nbAdverseColonne = 0;
        PieceType actuel = joueurCourant.couleur;
        PieceType adverse = (actuel == PieceType.Black) ? PieceType.White : PieceType.Black;
        int l = -1, c = -1;

        for (int colonne = 0; colonne < tr.taille(); colonne++) {
            nbAdverseColonne = 0;
            // verifie s'il y a un pion adverse dans la premiere colonne
            if (colonne == 0) {
                for (int ligne = 0; ligne < tr.taille(); ligne++) {
                    if (tr.getTerrain()[ligne][colonne].Type == adverse) {
                        nbAdverseColonne++;
                        l = ligne;
                        c = colonne;
                        if ((l + 1 < tr.taille()) && tr.getTerrain()[l + 1][c].Type == actuel)
                            cpt++;
                        if ((l - 1 >= 0) && tr.getTerrain()[l - 1][c].Type == actuel) {
                            cpt++;
                        }
                    }
                    if (nbAdverseColonne > 1)
                        return false;
                }
                if (nbAdverseColonne == 0)
                    return false;

            } else {
                // verifie dans les cases adjacente à droite la presence de pions adverses
                int ligne = l;
                if (ligne - 1 >= 0 && tr.getTerrain()[ligne - 1][colonne].Type == adverse) {
                    nbAdverseColonne++;
                    l = ligne - 1;
                    c = colonne;
                    if (l + 1 < tr.taille() && tr.getTerrain()[l + 1][c].Type == actuel)
                        cpt++;
                    if ((l - 1 >= 0) && tr.getTerrain()[l - 1][c].Type == actuel) {
                        cpt++;
                    }
                } else if (tr.getTerrain()[ligne][colonne].Type == adverse) {
                    nbAdverseColonne++;
                    l = ligne;
                    c = colonne;
                    if (l + 1 < tr.taille() && tr.getTerrain()[l + 1][c].Type == actuel)
                        cpt++;
                    if ((l - 1 >= 0) && tr.getTerrain()[l - 1][c].Type == actuel) {
                        cpt++;
                    }
                } else if (ligne + 1 < 7 && tr.getTerrain()[ligne + 1][colonne].Type == adverse) {
                    nbAdverseColonne++;
                    l = ligne + 1;
                    c = colonne;
                    if (l + 1 < tr.taille() && tr.getTerrain()[l + 1][c].Type == actuel)
                        cpt++;
                    if ((l - 1 >= 0) && tr.getTerrain()[l - 1][c].Type == actuel) {
                        cpt++;
                    }
                }
                if (nbAdverseColonne != 1)
                    return false;
            }
        }
        if (cpt >= 3) {
            String nameAdv = (joueurCourant == joueur1) ? joueur2.name : joueur1.name;

            return true;
        } else
            return false;

    }

    private void clearListe() {
        listeDeplacementJ1.clear();
        listeDeplacementJ2.clear();
        listePasseJ1.clear();
        listePasseJ2.clear();
    }

    public void jctrl_z() {
        gameOver = false;
        tr.ctrl_z();
        clearListe();
        metAJour();
    }

    public void jctrl_y() {
        tr.ctrl_y();
        metAJour();
    }

    private void JoueTourIAMiniMax(State bState) {
        int l;
        int c;
        Piece p;
        switch (bState.GameMode) {
            case MMP:
                if (bState.firstMove != null) {
                    l = bState.firstMove.From.l;
                    c = bState.firstMove.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.firstMove.To.l;
                    c = bState.firstMove.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                if (bState.secondMove != null) {
                    l = bState.secondMove.From.l;
                    c = bState.secondMove.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.secondMove.To.l;
                    c = bState.secondMove.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                if (bState.pass != null) {
                    l = bState.pass.From.l;
                    c = bState.pass.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.pass.To.l;
                    c = bState.pass.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                break;
            case MPM:
                if (bState.firstMove != null) {
                    l = bState.firstMove.From.l;
                    c = bState.firstMove.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.firstMove.To.l;
                    c = bState.firstMove.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                if (bState.pass != null) {
                    l = bState.pass.From.l;
                    c = bState.pass.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.pass.To.l;
                    c = bState.pass.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }

                if (bState.secondMove != null) {
                    l = bState.secondMove.From.l;
                    c = bState.secondMove.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.secondMove.To.l;
                    c = bState.secondMove.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                break;
            case PMM:
                if (bState.pass != null) {
                    l = bState.pass.From.l;
                    c = bState.pass.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.pass.To.l;
                    c = bState.pass.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                if (bState.firstMove != null) {
                    l = bState.firstMove.From.l;
                    c = bState.firstMove.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.firstMove.To.l;
                    c = bState.firstMove.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }

                if (bState.secondMove != null) {
                    l = bState.secondMove.From.l;
                    c = bState.secondMove.From.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                    l = bState.secondMove.To.l;
                    c = bState.secondMove.To.c;
                    p = tr._terrain[l][c];
                    SelectionPieceIA(p);
                }
                break;
            default:
                break;
        }
        FinTour();
    }

    public void suggestion() {
        MiniMax mimax = new MiniMax(this, joueurCourant.couleur);
        State sugState = mimax.winningMove(this);
        switch (sugState.GameMode) {
            case MMP:
                if (sugState.firstMove != null) {
                    suggetionCoup = sugState.firstMove;
                }
                break;
            case MPM:
                if (sugState.firstMove != null) {
                    suggetionCoup = sugState.firstMove;
                }
                break;
            case PMM:
                if (sugState.pass != null) {
                    suggetionCoup = sugState.pass;
                }
                break;
            default:
                break;
        }
        metAJour();
    }

    public boolean DebutTour() {
        return (joueurCourant.nbMove == 2 && joueurCourant.passeDispo == 1);
    }

    // mode textuelle
    public Piece getPiece(PieceType t) {
        Scanner sc = new Scanner(System.in);
        String ligne;
        String[] ligne_split;
        int pieceL;
        int pieceC;
        Piece res = tr.getTerrain()[0][0];
        boolean done = false;
        while (!done) {

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

                }
                if (res.Type != t) {

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

    // fin de tour
    public void endTurn() {
        joueurCourant.nbMove = 2;
        joueurCourant.passeDispo = 1;
        if (joueurCourant.n == TypeJoueur.Joueur1) {
            joueurCourant = joueur2;
        } else {
            joueurCourant = joueur1; // a modifier pour joueur contre l'ia
        }
        move();
    }

    // Ici encore on utilise l'entrée standard. A modifier plus tard pour l'ihm et
    // l'ia
    public void move() {
        if (joueurCourant.n == TypeJoueur.IA) {
            I.IA();
        } else {
            // victoire
            if (gameOver) {
                System.exit(0); // la j'ai mis system.exit mais on changera si necessaire
            }
            Scanner sc = new Scanner(System.in);
            char choix;
            Piece from;
            Piece to;
            // tant qu'il y a un truc à faire
            while (!gameOver) {
                if ((joueurCourant.nbMove == 0 && joueurCourant.passeDispo == 0)) {
                    // end turn
                    endTurn();
                }
                tr.PrintTerrain();

                choix = sc.nextLine().charAt(0);
                switch (choix) {
                    case 'p':
                        // passe
                        if (joueurCourant.passeDispo == 1) {
                            from = tr.getPieceWithBall(joueurCourant.couleur);

                            to = getPiece(joueurCourant.couleur);
                            TerrainUtils.passeWrapper(from, to);
                            joueurCourant.passeDispo = 0;
                            gameOver = tr.victoire() != PieceType.Empty;
                        } else {

                        }
                        break;
                    case 'm':
                        // mouvement
                        from = getPiece(joueurCourant.couleur);
                        ArrayList<Position> ar = from.PossiblePositions(joueurCourant.nbMove);

                        to = getPiece(PieceType.Empty);
                        // On verifie si c est bien un mouvement legal

                        if (ar.contains(to.Position)) {
                            ArrayList<Position> diag = from.getDiagonals();
                            int temp = joueurCourant.nbMove;
                            if (diag.contains(to.Position)) {
                                // Si c'est un mouvement en diagonale, on prend deux coups
                                joueurCourant.nbMove -= 2;
                            } else {
                                // Sinon 1 seul ou 2 selon si on a avancé d'une ou de deux cases
                                joueurCourant.nbMove -= Math
                                        .abs((from.Position.l + from.Position.c) - (to.Position.l + to.Position.c));
                            }
                            if (joueurCourant.nbMove >= 0) {
                                from.move(to.Position.l, to.Position.c);
                            } else {

                                joueurCourant.nbMove = temp;
                            }
                        } else {
                            throw new IllegalAccessError("Mouvement illegal");
                        }
                        gameOver = antijeu();
                        break;
                    case 'q':
                        // end turn
                        endTurn();
                        break;
                    default:

                        break;
                }
            }
        }
    }

    private void test_Random_IA_P(Terrain tr) {
        Random_IA A = new Random_IA(PieceType.Black, tr);
        PieceType tour = PieceType.White;
        Boolean victoire = false;
        int nbMove = 2; // on a droit à deux mouvements max
        int passe_faite = 1; // une passe
        Scanner sc = new Scanner(System.in);
        char choix;
        Piece from;
        Piece to;
        while (!victoire) {

            if (tour == PieceType.White) {
                // tant qu'il y a un truc à faire
                while (nbMove > 0 || passe_faite > 0) {
                    tr.PrintTerrain();

                    String s = sc.nextLine();

                    if (s.length() == 0) {

                        continue;
                    }
                    choix = s.charAt(0);
                    switch (choix) {
                        case 'p':
                            // passe
                            if (passe_faite == 1) {
                                from = tr.getPieceWithBall(tour);

                                to = getPiece(tour);
                                TerrainUtils.passeWrapper(from, to);
                                passe_faite = 0;
                                // victoire?
                                if (to.Position.l == 0) {

                                    victoire = true;
                                    // System.exit(0); // La j'ai fais un system.exit mais on changera plus tard si
                                    // il le faut
                                }
                            } else {

                            }
                            break;
                        case 'm':
                            // mouvement

                            if (nbMove == 0) {

                                nbMove = 0;
                                continue;
                            }
                            from = getPiece(tour);
                            ArrayList<Position> ar = from.PossiblePositions(nbMove);

                            to = getPiece(PieceType.Empty);
                            // On verifie si c est bien un mouvement legal
                            if (ar.contains(to.Position)) {
                                ArrayList<Position> diag = from.getDiagonals();
                                if (diag.contains(to.Position)) {
                                    // Si c'est un mouvement en diagonale, on prend deux coups
                                    nbMove -= 2;
                                } else {
                                    // Sinon 1 seul ou deux selon si on a avancé d'une ou deux cases
                                    nbMove -= Math
                                            .abs((from.Position.l + from.Position.c) - (to.Position.l + to.Position.c));
                                }
                                if (nbMove >= 0) {
                                    from.move(to.Position.l, to.Position.c);
                                } else {

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
                            break;
                        default:

                            break;
                    }
                }
                tour = PieceType.Black;
            } else {
                A.IA();
                victoire = A.Victoire_IA;
                tour = PieceType.White;
                nbMove = 2;
                passe_faite = 1;
            }
        }
        sc.close();
    }

    private void test_Random_IA_IA(Terrain tr) {
        Random_IA A = new Random_IA(PieceType.Black, tr);
        Random_IA B = new Random_IA(PieceType.White, tr);
        PieceType tour = PieceType.White;
        Boolean victoire = false;
        while (!victoire) {
            tr.PrintTerrain();

            if (tour == PieceType.White) {
                B.IA();
                victoire = B.Victoire_IA;
                tour = PieceType.Black;
            } else {
                A.IA();
                victoire = A.Victoire_IA;
                tour = PieceType.White;
            }
        }
        if (A.Victoire_IA) {

        } else {

        }
    }

    /**
     * @author Thomas
     * @param p Pièce que possède la balle
     * @return null Si aucune position trouvé
     * @return res Liste des positions possible
     */
    public ArrayList<Position> winningMove(Piece p) {
        ArrayList<Position> list = p.passesPossibles();
        ArrayList<Position> res = new ArrayList<Position>();
        if (list.size() == 0) {
            return null;
        } else {
            if (p.Type == PieceType.Black) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).l == 6) {
                        res.add(list.get(i));
                    }
                }
            } else if (p.Type == PieceType.White) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).l == 0) {
                        res.add(list.get(i));
                    }
                }
            }
            if (res.size() == 0) {
                return null;
            }
            return res;
        }
    }

    public String JSONfromGame(Jeu j) {
        JeuJSON jte = new JeuJSON(j);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(jte);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;

    }

    public void ExportGameToJSON(Jeu j) {
        JeuJSON jte = new JeuJSON(j);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(jte);
            try {

                FileWriter fw = new FileWriter(this.getClass().getResource("../data/history.json").getFile(), true);
                fw.write(json + System.lineSeparator());
                fw.close();

            } catch (IOException ioe) {
                System.err.println("IOException: " + ioe.getMessage());
            } finally {

            }
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
