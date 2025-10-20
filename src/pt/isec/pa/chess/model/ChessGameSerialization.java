package pt.isec.pa.chess.model;

import java.io.*;

public class ChessGameSerialization {
    private ChessGameSerialization(){
     //construtor privado que impede alguem de criar uma instacia desta classe
    }

    public static boolean save (String filename, ChessGame game) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(game);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static ChessGame load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (ChessGame) in.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
