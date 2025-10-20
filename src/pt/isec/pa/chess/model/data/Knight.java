package pt.isec.pa.chess.model.data;

import java.util.List;

public class Knight extends Piece {

    public Knight(char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board) {
        super('N', pieceColumn, pieceRow, pieceColor, board);
    }

    @Override
    public List<String> getPossibleMoves() {
        int[][] directions = {
                {-2, 1}, {-1, 2}, {1, 2}, {2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        return super.getPossibleMoves(directions, false);
    }
}
