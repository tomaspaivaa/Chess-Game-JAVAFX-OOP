package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    boolean hasMoved;

    public King(char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board, boolean hasMoved) {
        super('K', pieceColumn, pieceRow, pieceColor, board);
        this.hasMoved = hasMoved;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHadMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public List<String> getBaseMovesOnly() {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, +1},
                {0, -1}, {0, +1},
                {+1, -1}, {+1, 0}, {+1, +1}
        };

        return super.getPossibleMoves(directions, false);
    }

    @Override
    public List<String> getPossibleMoves() {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, +1},
                {0, -1}, {0, +1},
                {+1, -1}, {+1, 0}, {+1, +1}
        };

        List<String> moves = super.getPossibleMoves(directions, false);

        if (hasMoved) {
            return moves;
        }

        Piece kingsideRook = board.getPiece('H', pieceRow);
        if (kingsideRook instanceof Rook rook && !rook.getHasMoved() && rook.getPieceColor() == pieceColor) {
            if (board.isEmpty('F', pieceRow) && board.isEmpty('G', pieceRow)) {
                if (!board.isSquareUnderAttack('E', pieceRow, pieceColor) &&
                        !board.isSquareUnderAttack('F', pieceRow, pieceColor) &&
                        !board.isSquareUnderAttack('G', pieceRow, pieceColor)) {
                    moves.add("G" + pieceRow);
                }
            }
        }

        Piece queensideRook = board.getPiece('A', pieceRow);
        if (queensideRook instanceof Rook rook && !rook.getHasMoved() && rook.getPieceColor() == pieceColor) {
            if (board.isEmpty('B', pieceRow) && board.isEmpty('C', pieceRow) && board.isEmpty('D', pieceRow)) {
                if (!board.isSquareUnderAttack('E', pieceRow, pieceColor) &&
                        !board.isSquareUnderAttack('D', pieceRow, pieceColor) &&
                        !board.isSquareUnderAttack('C', pieceRow, pieceColor)) {
                    moves.add("C" + pieceRow);
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        if (!hasMoved) {
            return super.toString() + "*";
        }
        return super.toString();
    }
}
