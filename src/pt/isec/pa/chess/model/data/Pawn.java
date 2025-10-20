package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board) {
        super('P', pieceColumn, pieceRow, pieceColor, board);
    }

    @Override
    public List<String> getPossibleMoves() {
        List<String> possibleMoves = new ArrayList<>();
        int direction = (this.pieceColor == PieceTeamEnum.WHITE) ? 1 : -1;
        int startRow = (this.pieceColor == PieceTeamEnum.WHITE) ? 2 : 7;

        int oneStepForward = pieceRow + direction;
        int twoStepsForward = pieceRow + 2 * direction;

        // Movimento normal de 1 casa
        if (board.isValidPosition(pieceColumn, oneStepForward) && board.isEmpty(pieceColumn, oneStepForward)) {
            possibleMoves.add("" + pieceColumn + oneStepForward);

            // Movimento inicial de 2 casas
            if (pieceRow == startRow && board.isEmpty(pieceColumn, twoStepsForward)) {
                possibleMoves.add("" + pieceColumn + twoStepsForward);
            }
        }

        // Captura à esquerda
        char left = (char) (pieceColumn - 1);
        if (board.isValidPosition(left, oneStepForward)) {
            Piece target = board.getPiece(left, oneStepForward);
            if (target != null && target.getPieceColor() != this.pieceColor) {
                possibleMoves.add("" + left + oneStepForward);
            }
        }

        // Captura à direita
        char right = (char) (pieceColumn + 1);
        if (board.isValidPosition(right, oneStepForward)) {
            Piece target = board.getPiece(right, oneStepForward);
            if (target != null && target.getPieceColor() != this.pieceColor) {
                possibleMoves.add("" + right + oneStepForward);
            }
        }

        return possibleMoves;
    }
}
