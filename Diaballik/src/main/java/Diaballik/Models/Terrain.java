package Diaballik.Models;

import java.util.*;

public class Terrain implements ITerrain {
    private Piece[][] _terrain;

    /**
     * Mettre tous les case = vide
     */
    private void init() {
        _terrain = new Piece[7][7];
        for (int l = 0; l < 7; l++) {
            for (int c = 0; c < 7; c++) {
                _terrain[l][c] = new Piece(PieceType.Empty, false, l, c,this);
            }
        }
        for (int i = 0; i < 7; i++) {
            _terrain[0][i].Type = PieceType.Black;
        }
        for (int i = 0; i < 7; i++) {
            _terrain[6][i].Type = PieceType.White;
        }

    }

    @Override
    public Piece[][] Create() {
        init();
        Setup();
        return _terrain;
    }

    @Override
    public void Setup() {
        /*** B ***/
        /*******/
        /*******/
        /*******/
        /*******/
        /*** B ***/
        _terrain[0][3].addBall();
        _terrain[6][3].addBall();
    }

    @Override
    public void Destroy() {
        System.gc();
    }

    @Override
    public void PrintTerrain() {
        for (int l = 0; l < 7; l++) {
            for (int c = 0; c < 7; c++) {
                if (_terrain[l][c].Type == PieceType.Empty) {
                    System.out.print(". ");
                } else {
                    if (_terrain[l][c].Type == PieceType.Black) {
                        if (_terrain[l][c].HasBall == true) {
                            System.out.print("⍟ ");
                        } else {
                            System.out.print("B ");
                        }
                    } else {
                        if (_terrain[l][c].HasBall == true) {
                            System.out.print("☆ ");
                        } else {
                            System.out.print("W ");
                        }
                    }
                }
            }
            System.out.println();
        }
    }

    @Override
    public Piece[][] getTerrain() {
        return _terrain;
    }

}