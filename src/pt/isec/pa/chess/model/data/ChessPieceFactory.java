package pt.isec.pa.chess.model.data;

public class ChessPieceFactory {
    public static Piece createPiece(PieceTypeEnum type, PieceTeamEnum color, Board board, String position) {
        if (type == null) {
            return null;
        }

        char col = position.charAt(0);
        int row = Character.getNumericValue(position.charAt(1));
        boolean hasMoved = !(position.length() == 3 && position.charAt(2) == '*');
        return switch (type) {
            case BISHOP -> new Bishop(col, row, color, board);
            case KING -> new King(col, row, color, board, hasMoved);
            case KNIGHT -> new Knight(col, row, color, board);
            case PAWN -> new Pawn(col, row, color, board);
            case QUEEN -> new Queen(col, row, color, board);
            case ROOK -> new Rook(col, row, color, board, hasMoved);
        };
    }

    public static Piece createPieceFromText(String textRepresentation, Board board) {
        char pieceType = textRepresentation.charAt(0);
        PieceTeamEnum pieceColor = Character.isUpperCase(pieceType) ? PieceTeamEnum.WHITE : PieceTeamEnum.BLACK;
        char col = Character.toUpperCase(textRepresentation.charAt(1));
        int row = Character.getNumericValue(textRepresentation.charAt(2));
        boolean hasMoved = !(textRepresentation.length() == 4 && textRepresentation.charAt(3) == '*');
        return switch (Character.toUpperCase(pieceType)) {
            case 'B' -> new Bishop(col, row, pieceColor, board);
            case 'K' -> new King(col, row, pieceColor, board, hasMoved);
            case 'N' -> new Knight(col, row, pieceColor, board);
            case 'P' -> new Pawn(col, row, pieceColor, board);
            case 'Q' -> new Queen(col, row, pieceColor, board);
            case 'R' -> new Rook(col, row, pieceColor, board, hasMoved);
            default -> null;
        };
    }
}
