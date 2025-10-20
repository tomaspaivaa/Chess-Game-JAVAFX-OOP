package pt.isec.pa.chess.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.isec.pa.chess.model.data.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {
    static Board getBoardForTesting() {
        Board board = new Board();
        board.addPiece(new Pawn('D', 4, PieceTeamEnum.BLACK, board));
        board.addPiece(new Queen('D', 5, PieceTeamEnum.WHITE, board));
        board.addPiece(new Pawn('C', 8, PieceTeamEnum.WHITE, board));
        board.addPiece(new Pawn('A', 8, PieceTeamEnum.BLACK, board));
        board.addPiece(new King('H', 5, PieceTeamEnum.BLACK, board, false));
        board.addPiece(new Queen('H', 1, PieceTeamEnum.WHITE, board));
        board.addPiece(new Queen('G', 1, PieceTeamEnum.WHITE, board));
        return board;
    }

    // ----

    static Stream<Arguments> provider_doesPositionHasAPiece_ReturnsExpectedValues() {
        return Stream.of(
                Arguments.arguments(getBoardForTesting(), 'D', 4, true),
                Arguments.arguments(getBoardForTesting(), 'A', 1, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_doesPositionHasAPiece_ReturnsExpectedValues")
    void doesPositionHasAPiece_ReturnsExpectedValues(Board board, char col, int row, boolean expectedValue) {
        // Arrange
        var chessGame = new ChessGame(board);

        // Act
        var hasAPiece = chessGame.doesPositionHasAPiece(col, row);

        // Assert
        assertEquals(expectedValue, hasAPiece);
    }

    // ----

    static Stream<Arguments> provider_executeMove_ReturnsExpectedValue() {
        return Stream.of(
                Arguments.arguments(getBoardForTesting(), 'D', 5, 'D', 4, true),
                Arguments.arguments(getBoardForTesting(), 'D', 2, 'D', 3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_executeMove_ReturnsExpectedValue")
    void executeMove_ReturnsExpectedValues(Board board, char colPiece, int rowPiece, char colToMove, int rowToMove, boolean expectedValue) {
        // Arrange
        var chessGame = new ChessGame(board);

        // Act
        var executeMove = chessGame.executeMove(colPiece, rowPiece, colToMove, rowToMove);

        // Assert
        assertEquals(expectedValue, executeMove);
    }

    // ----

    static Stream<Arguments> provider_isPawnPromotable_ReturnsExpectedValue() {
        return Stream.of(
                Arguments.arguments(getBoardForTesting(), 'D', 4, false),
                Arguments.arguments(getBoardForTesting(), 'C', 8, true),
                Arguments.arguments(getBoardForTesting(), 'A', 2, false),
                Arguments.arguments(getBoardForTesting(), 'A', 8, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_isPawnPromotable_ReturnsExpectedValue")
    void isPawnPromotable_ReturnsExpectedValues(Board board, char col, int row, boolean expectedValue) {
        // Arrange
        var chessGame = new ChessGame(board);

        // Act
        var isPawnPromotable = chessGame.isPawnPromotable(col, row);

        // Assert
        assertEquals(expectedValue, isPawnPromotable);
    }

    // ----

    static Stream<Arguments> provider_isCheckMate_ReturnsExpectedValue() {
        return Stream.of(
                Arguments.arguments(getBoardForTesting(), PieceTeamEnum.WHITE, false),
                Arguments.arguments(getBoardForTesting(), PieceTeamEnum.BLACK, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_isCheckMate_ReturnsExpectedValue")
    void isCheckMate_ReturnsExpectedValues(Board board, PieceTeamEnum team, boolean expectedValue) {
        // Arrange
        var chessGame = new ChessGame(board);

        // Act
        var isCheckMate = chessGame.isCheckMate(team);

        // Assert
        assertEquals(expectedValue, isCheckMate);
    }
}