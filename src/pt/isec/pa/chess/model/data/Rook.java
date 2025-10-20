package pt.isec.pa.chess.model.data;

import java.util.List;

public class Rook extends Piece {
    private boolean hasMoved;

    public Rook(char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board, boolean hasMoved) {
        super('R', pieceColumn, pieceRow, pieceColor, board);
        this.hasMoved = false;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHadMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public List<String> getPossibleMoves() {
        int[][] directions = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        return super.getPossibleMoves(directions, true);
    }

    @Override
    public String toString() {
        if (!hasMoved) {
            return super.toString() + "*";
        }
        return super.toString();
    }
}
