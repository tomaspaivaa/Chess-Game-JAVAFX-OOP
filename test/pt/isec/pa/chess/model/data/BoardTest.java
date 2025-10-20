package pt.isec.pa.chess.model.data;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.isec.pa.chess.model.ChessGame;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    static Board getBoardForTesting() {
        Board board = new Board();
        board.addPiece(new Pawn('C', 8, PieceTeamEnum.WHITE, board));
        board.addPiece(new Queen('G', 1, PieceTeamEnum.WHITE, board));
        return board;
    }

    // ----

    static Stream<Arguments> provider_isValidPosition_ReturnsExpectedValue() {
        return Stream.of(
                Arguments.arguments('D', 4, true),
                Arguments.arguments('D', 12, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_isValidPosition_ReturnsExpectedValue")
    void isValidPosition_ReturnsExpectedValue(char col, int row, boolean expectedValue) {
        // Arrange
        var board = new Board();

        // Act
        var hasAPiece = board.isValidPosition(col, row);

        // Assert
        assertEquals(expectedValue, hasAPiece);
    }

    // ---

    static Stream<Arguments> provider_getNormalizedText_ReturnsExpectedString() {
        return Stream.of(
                Arguments.arguments(getBoardForTesting(), "PC8,QG1")
        );
    }

    @ParameterizedTest
    @MethodSource("provider_getNormalizedText_ReturnsExpectedString")
    void getNormalizedText_ReturnsExpectedValue(Board board, String expectedString) {
        // Act
        var normalizedText = board.getNormalizedText();

        // Assert
        assertEquals(expectedString, normalizedText);
    }
}