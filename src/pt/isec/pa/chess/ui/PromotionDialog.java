package pt.isec.pa.chess.ui;

import javafx.scene.control.ChoiceDialog;
import pt.isec.pa.chess.model.data.PieceTypeEnum;

import java.util.Arrays;
import java.util.Optional;

public class PromotionDialog {

    public static PieceTypeEnum show() {
        // Cria a lista de opções (removendo PAWN e KING)
        PieceTypeEnum[] validPieces = {
                PieceTypeEnum.QUEEN,
                PieceTypeEnum.ROOK,
                PieceTypeEnum.BISHOP,
                PieceTypeEnum.KNIGHT
        };

        ChoiceDialog<PieceTypeEnum> dialog = new ChoiceDialog<>(
                PieceTypeEnum.QUEEN,
                Arrays.asList(validPieces)
        );

        dialog.setTitle("Promoção de Peão");
        dialog.setHeaderText("Escolha a peça para promoção:");
        dialog.setContentText("Peça:");

        Optional<PieceTypeEnum> result = dialog.showAndWait();
        return result.orElse(PieceTypeEnum.QUEEN); // Default para Queen se cancelar
    }
}