package pt.isec.pa.chess.model.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChessPieceFactoryTest {
    static Stream<Arguments> provider_createPiece_ReturnsExpectedType() {
        return Stream.of(
                Arguments.arguments(PieceTypeEnum.BISHOP, Bishop.class),
                Arguments.arguments(PieceTypeEnum.KING, King.class),
                Arguments.arguments(PieceTypeEnum.KNIGHT, Knight.class),
                Arguments.arguments(PieceTypeEnum.PAWN, Pawn.class),
                Arguments.arguments(PieceTypeEnum.QUEEN, Queen.class),
                Arguments.arguments(PieceTypeEnum.ROOK, Rook.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_createPiece_ReturnsExpectedType")
    void createPiece_ReturnsExpectedType(PieceTypeEnum type, Class expectedPieceClass) {
        // Act
        var piece = ChessPieceFactory.createPiece(type, PieceTeamEnum.WHITE, null, "A1");

        // Assert
        assertEquals(expectedPieceClass, piece.getClass());
    }

    // ---

    @Test
    void createPiece_ReturnsNull() {
        // Act
        var piece = ChessPieceFactory.createPiece(null, PieceTeamEnum.WHITE, null, "A1");

        // Assert
        assertNull(piece);
    }

    // ---

    static Stream<Arguments> provider_createPiece_CorrectlyPassesPieceTeam() {
        return Stream.of(
                Arguments.arguments(PieceTeamEnum.WHITE, Bishop.class),
                Arguments.arguments(PieceTeamEnum.BLACK, Bishop.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_createPiece_CorrectlyPassesPieceTeam")
    void createPiece_CorrectlyPassesPieceTeam(PieceTeamEnum color) {
        // Act
        var piece = ChessPieceFactory.createPiece(PieceTypeEnum.BISHOP, color, null, "A1");

        // Assert
        assertEquals(color, piece.getPieceColor());
    }

    // ---

    static Stream<Arguments> provider_createPiece_CorrectlyGuessesIfHasMoved() {
        return Stream.of(
                Arguments.arguments("A1", true),
                Arguments.arguments("A1*", false),
                Arguments.arguments("B6", true),
                Arguments.arguments("B6*", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_createPiece_CorrectlyGuessesIfHasMoved")
    void createPiece_CorrectlyGuessesIfHasMoved(String position, boolean expectedHasMoved){
        // Act
        var piece = (King) ChessPieceFactory.createPiece(PieceTypeEnum.KING, PieceTeamEnum.WHITE, null, position);

        // Assert
        assertEquals(piece.getHasMoved(), expectedHasMoved);
    }

    // ---

    static Stream<Arguments> provider_createPieceWithText_ReturnsExpectedType() {
        return Stream.of(
                Arguments.arguments("B23", Bishop.class, PieceTeamEnum.WHITE),
                Arguments.arguments("k12", King.class, PieceTeamEnum.BLACK),
                Arguments.arguments("p22", Pawn.class, PieceTeamEnum.BLACK)
        );
    }

    @ParameterizedTest
    @MethodSource("provider_createPieceWithText_ReturnsExpectedType")
    void provider_createPieceWithText_ReturnsExpectedType(String textRepresentation, Class expectedPieceClass, PieceTeamEnum expectedTeam) {
        // Act
        var piece = ChessPieceFactory.createPieceFromText(textRepresentation, null);

        // Assert
        assertEquals(expectedPieceClass, piece.getClass());
        assertEquals(expectedTeam, piece.getPieceColor());
    }
}