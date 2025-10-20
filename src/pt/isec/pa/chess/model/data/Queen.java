package pt.isec.pa.chess.model.data;

import java.util.List;

public class Queen extends Piece {

    public Queen(char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board) {
        super('Q', pieceColumn, pieceRow, pieceColor, board);
    }

    @Override
    public List<String> getPossibleMoves() {
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        return super.getPossibleMoves(directions, true);
    }
}
