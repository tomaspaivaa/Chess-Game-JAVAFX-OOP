package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

abstract public class Piece implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;
    protected char pieceColumn;
    protected int pieceRow;
    protected PieceTeamEnum pieceColor;
    protected PieceTypeEnum pieceTypeEnum;
    protected Board board;
    private char pieceType;


    public Piece(char pieceType, char pieceColumn, int pieceRow, PieceTeamEnum pieceColor, Board board) {
        this.pieceType = pieceType;
        this.pieceColumn = pieceColumn;
        this.pieceTypeEnum = makePieceTypeEnum(pieceType);
        this.pieceRow = pieceRow;
        this.pieceColor = pieceColor;
        this.board = board;
        if (pieceColor == PieceTeamEnum.WHITE) {
            this.pieceType = Character.toUpperCase(pieceType);
        } else if (pieceColor == PieceTeamEnum.BLACK) {
            this.pieceType = Character.toLowerCase(pieceType);
        }
    }

    private PieceTypeEnum makePieceTypeEnum(char pieceType) {
        return switch (Character.toUpperCase(pieceType)) {
            case 'B' -> PieceTypeEnum.BISHOP;
            case 'K' -> PieceTypeEnum.KING;
            case 'N' -> PieceTypeEnum.KNIGHT;
            case 'P' -> PieceTypeEnum.PAWN;
            case 'Q' -> PieceTypeEnum.QUEEN;
            case 'R' -> PieceTypeEnum.ROOK;
            default -> null;
        };
    }

    public PieceTypeEnum getPieceTypeEnum() {
        return this.pieceTypeEnum;
    }


    public abstract List<String> getPossibleMoves();

    public List<String> getPossibleMoves(int[][] directions, boolean keepsSearching) {
        List<String> possibleMoves = new ArrayList<>();
        int newRow;
        char newCol;

        for (int[] dir : directions) {
            newRow = pieceRow;
            newCol = pieceColumn;

            do {
                newRow += dir[0];
                newCol = (char) (newCol + dir[1]);

                if (!board.isValidPosition(newCol, newRow)) {
                    break;
                }

                if (!board.isEmpty(newCol, newRow)) {
                    if (board.getPiece(newCol, newRow).getPieceColor() != this.pieceColor) {
                        possibleMoves.add(String.format("%c%d", newCol, newRow));
                    }
                    break;
                }
                possibleMoves.add(String.format("%c%d", newCol, newRow));
            } while (keepsSearching);
        }
        return possibleMoves;
    }

    public PieceTeamEnum getPieceColor() {
        return pieceColor;
    }

    public void setPieceColor(PieceTeamEnum pieceColor) {
        this.pieceColor = pieceColor;
    }

    public char getPieceType() {
        return pieceType;
    }

    public void setPieceType(char pieceType) {
        this.pieceType = pieceType;
    }

    public char getPieceColumn() {
        return pieceColumn;
    }

    public void setPieceColumn(char pieceColumn) {
        this.pieceColumn = pieceColumn;
    }

    public int getPieceRow() {
        return pieceRow;
    }

    public void setPieceRow(int pieceRow) {
        this.pieceRow = pieceRow;
    }

    @Override
    public String toString() {
        return String.format("%c%c%d", this.pieceType, this.pieceColumn, this.pieceRow);
    }
}
