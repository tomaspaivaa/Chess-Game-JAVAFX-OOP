package pt.isec.pa.chess.model.data;

import java.util.List;

public class Bishop extends Piece {

    public Bishop(char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board) {
        super('B', pieceColumn, pieceRow, pieceColor, board);
    }

    @Override
    public List<String> getPossibleMoves() {
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        return super.getPossibleMoves(directions, true);
    }
}
