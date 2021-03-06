package Diaballik.Models.IA;

import java.util.ArrayList;

import Diaballik.Models.*;

public class IA_easy {

    int nbMove = 2;
    int nbPasse = 1;
    PieceType type = PieceType.Black;
    Terrain tr;
    Jeu j;

    public IA_easy(Jeu jeu) {
        j = jeu;
        tr = jeu.tr;
    }

    public IA_easy(Terrain tr, PieceType type) {
        this.tr = tr;
        this.type = type;
    }

    /**
     * @author Wassim
     * Met a jour le nombre de coup restant pour l'IA selon le coup effectué
     */
    private void maj_nbMove(From_to bestMove) {
        ArrayList<Position> diag = tr.getTerrain()[bestMove.from.l][bestMove.from.c].getDiagonals();
        // mise a jour de nbMove
        int temp = nbMove;
        if (diag.contains(bestMove.to)) {
            // Si c'est un mouvement en diagonale, on prend deux coups
            nbMove -= 2;
        } else {
            // Sinon 1 seul ou 2 selon si on a avancé d'une ou de deux cases
            nbMove -= Math.abs((bestMove.from.l + bestMove.from.c) - (bestMove.to.l + bestMove.to.c));
        }
        if (nbMove < 0) {
            nbMove = temp;
        }
    }
    /**
     * @author Wassim
     * Retourne la meilleur passe selon l'IA avec une profondeur de 1
     */
    public Position getNextPasse(PieceType type, Terrain tr) {

        if (type == PieceType.White) {
            ArrayList<Position> passes;
            int max = -9999;
            Piece balle = null;
            Position bestPasse = null;
            for (int i = 0; i < tr.taille(); i++) {
                for (Piece p : tr.getTerrain()[i]) {
                    if (p.HasBall && p.Type == type) {
                        balle = p;
                        i = tr.taille();
                        break;
                    }
                }
            }
            passes = balle.passesPossibles();
            for (Position p : passes) {
                balle.HasBall = false;
                tr.getTerrain()[p.l][p.c].HasBall = true;
                int sc = Evaluator.scoreOfBoard(tr);
                if (max < sc) {
                    max = sc;
                    bestPasse = new Position(p.l, p.c);
                }
                tr.getTerrain()[p.l][p.c].HasBall = false;
                balle.HasBall = true;
            }
            
            return bestPasse;
        } else {
            ArrayList<Position> passes;
            int min = 9999;
            Piece balle = null;
            Position bestPasse = null;
            for (int i = 0; i < tr.taille(); i++) {
                for (Piece p : tr.getTerrain()[i]) {
                    if (p.HasBall && p.Type == type) {
                        balle = p;
                        i = tr.taille();
                        break;
                    }
                }
            }
            passes = balle.passesPossibles();
            for (Position p : passes) {
                balle.HasBall = false;
                tr.getTerrain()[p.l][p.c].HasBall = true;
                int sc = Evaluator.scoreOfBoard(tr);
                if (min > sc) {
                    min = sc;
                    bestPasse = new Position(p.l, p.c);
                }
                tr.getTerrain()[p.l][p.c].HasBall = false;
                balle.HasBall = true;
            }
            
            return bestPasse;
        }
    }

    /**
     * @author Wassim
     * Retourne le meilleur mouvement selon l'IA avec une profondeur de 1
     */
    public From_to getNextMove(PieceType type, Terrain tr) {

        if (type == PieceType.White) {
            ArrayList<Couple_piece_pos> possibleMoves = IA_utils.getAllPossibleMoves(type, tr, nbMove);
            int max = -9999;
            From_to bestMove = new From_to(possibleMoves.get(0).piece.Position, possibleMoves.get(0).pos.get(0));

            for (Couple_piece_pos pp : possibleMoves) {
                for (Position p : pp.pos) {
                    Position origin = new Position(pp.piece.Position.l, pp.piece.Position.c);
                    pp.piece.move(p.l, p.c);
                    maj_nbMove(bestMove);
                    int sc = Evaluator.scoreOfBoard(tr);
                    pp.piece.move(origin.l, origin.c);
                    if (max < sc) {
                        max = sc;
                        bestMove = new From_to(pp.piece.Position, p);
                    }
                }
            }

            
            return bestMove;
        }

        else {
            ArrayList<Couple_piece_pos> possibleMoves = IA_utils.getAllPossibleMoves(type, tr, nbMove);
            
            int min = 9999;
            From_to bestMove = new From_to(possibleMoves.get(0).piece.Position, possibleMoves.get(0).pos.get(0));
            for (Couple_piece_pos pp : possibleMoves) {
                for (Position p : pp.pos) {
                    Position origin = new Position(pp.piece.Position.l, pp.piece.Position.c);
                    pp.piece.move(p.l, p.c);
                    maj_nbMove(bestMove);
                    int sc = Evaluator.scoreOfBoard(tr);
                    pp.piece.move(origin.l, origin.c);
                    if (min > sc) {
                        min = sc;
                        bestMove = new From_to(pp.piece.Position, p);
                    }
                }
            }

            
            return bestMove;
        }
    }

    /**
     * @author Wassim
     * On demande à l'IA de jouer un tour
     */
    public void joueTourIAEasy() {
        nbMove = 2;
        nbPasse = 1;
        //mvm
        
        Terrain trTemp = tr.clone();
        
        From_to mouvement = getNextMove(PieceType.Black, trTemp); // là l'IA joue les noirs mais on changera
        
        j.SelectionPieceIA(tr.getTerrain()[mouvement.from.l][mouvement.from.c]);
        j.SelectionPieceIA(tr.getTerrain()[mouvement.to.l][mouvement.to.c]);
        
        //passe
        trTemp = tr.clone();
        Position passe = getNextPasse(PieceType.Black, trTemp);
        Piece balle = null;
        for (int i = 0; i < tr.taille(); i++) {
            for (Piece p : tr.getTerrain()[i]) {
                if (p.HasBall && p.Type == type) {
                    balle = p;
                    i = tr.taille();
                    break;
                }
            }
        }
        j.SelectionPieceIA(tr.getTerrain()[balle.Position.l][balle.Position.c]);
        j.SelectionPieceIA(tr.getTerrain()[passe.l][passe.c]);
        
        j.FinTour();
        
    }

}

/**
     * @author Wassim
     * Celle classe va nous permettre de modéliser un deplacement. Une position source et
     * une position destination
     */

class From_to {
    Position from;
    Position to;

    public From_to() {
        this.from = null;
        this.to = null;
    }

    public From_to(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return from.toString() + " -> " + to.toString();
    }
}
