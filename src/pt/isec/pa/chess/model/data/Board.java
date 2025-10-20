package pt.isec.pa.chess.model.data;

import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {
    private static final long serialVersionUID = 100L;
    private static final int BOARD_SIZE = 8;
    private List<Piece> pieces;

    public Board() {
        this.pieces = new ArrayList<>();
    }

    public static int getBoardSize() {
        return BOARD_SIZE;
    }

    public void emptyBoard() {
        pieces.clear();
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean isValidPosition(char col, int row) {
        return row >= 1 && row <= BOARD_SIZE && col >= 'A' && col <= (char)('A' + BOARD_SIZE - 1);
    }

    public boolean isEmpty(char col, int row) {
        if (!isValidPosition(col, row)) {
            return false;
        }
        for (Piece piece : pieces) {
            if (piece.getPieceRow() == row && piece.getPieceColumn() == col) {
                return false;
            }
        }
        return true;
    }

    public boolean isSquareUnderAttack(char col, int row, PieceTeamEnum defenderTeam) {
        for (Piece piece : pieces) {
            if (piece.getPieceColor() == defenderTeam)
                continue;

            List<String> possibleMoves;
            if (piece instanceof King king) {
                possibleMoves = king.getBaseMovesOnly(); // evita recursão
            } else {
                possibleMoves = piece.getPossibleMoves();
            }

            if (possibleMoves.contains("" + col + row))
                return true;
        }
        return false;
    }



    public List<String> getEnemyTeamPossibleMoves(PieceTeamEnum team) {
        Set<String> possibleMoves = new HashSet<>();
        for (Piece p : pieces) {
            if (p.getPieceColor() != team) {
                possibleMoves.addAll(p.getPossibleMoves());
            }
        }
        return new ArrayList<>(possibleMoves);
    }

    public Piece getPiece(char col, int row) {
        for (Piece p : pieces) {
            if (p.getPieceRow() == row && p.getPieceColumn() == col) {
                return p;
            }
        }
        return null;
    }

    public Piece getKingPiece(PieceTeamEnum team) {
        for (Piece p : pieces) {
            if (p.getPieceColor() == team && Character.toUpperCase(p.getPieceType()) == 'K') {
                return p;
            }
        }
        return null;
    }

    public boolean removePiece(char col, int row) {
        Piece pieceToRemove = getPiece(col, row);
        if (pieceToRemove != null) {
            return pieces.remove(pieceToRemove);
        }
        return false;
    }

    public void addPiece(Piece pieceToAdd) {
        pieces.add(pieceToAdd);
    }

    public String getPiecePosition(char pieceType, PieceTeamEnum color) {
        for (Piece p : pieces) {
            if (p.getPieceType() == Character.toUpperCase(pieceType) && p.getPieceColor() == color) {
                return p.toString();
            }
        }
        return "A peça dada não existe";
    }

    public void movePiece(Piece piece, char col, int row) {
        piece.setPieceColumn(col);
        piece.setPieceRow(row);
    }

    public String getNormalizedText() {
        StringBuilder text = new StringBuilder();

        for (Piece p : pieces) {
            text.append(p.toString()).append(",");
        }
        if (!text.isEmpty()) {
            text.setLength(text.length() - 1);
        }

        return text.toString();
    }
}
