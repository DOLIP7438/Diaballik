package Diaballik.IA;

import java.util.ArrayList;

import Diaballik.Controllers.TerrainUtils;
import Diaballik.Models.*;

public class IA_medium {

    int nbMove = 2;
    int nbPasse = 1;
    PieceType type = PieceType.Black;
    Terrain tr;

    public IA_medium(Terrain tr, PieceType type){
        this.tr = tr;
        this.type = type;
    }

    //Still working on it
    public void getNextMove(){
        /*
        ArrayList<Couple_piece_pos> possibleMoves = IA_utils.getAllPossibleMoves(type, tr, nbMove);
        for(Couple_piece_pos pp : possibleMoves){
            Terrain trTemp = tr.clone();
            int max = 0;


        }
        
        */
    }
    /**
     * PPE = Player Player Exchange
     */
    private void Strategy_PPE(){

    }
    /**
     * PEP = Player Exchance Player
     */
    private void Strategy_PEP(){

    }
    /**
     * EPP = Exchange Player Player
     */
    private void Strategy_EPP(){

    }

    public static void main(String args[]){
        Terrain tr = new Terrain();
        tr.Create();
        System.out.println(IA_utils.getAllPossibleMoves(PieceType.Black, tr, 2));
    }
}

class From_to{
    Position from;
    Position to;
    public From_to(Position from, Position to){
        this.from = from;
        this.to = to;
    }
    @Override
    public String toString(){
        return from.toString() + " -> " + to.toString();
    }
}
