package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.WinnerEnum;
import pt.isec.pa.chess.model.data.PieceTeamEnum;

public class RootPane extends BorderPane {
    ChessGameManager data;
    ModelUi dataUi;
    BoardView boardView;
    Label lblTitle, lblPlayerWhite, lblPlayerBlack, lblCurrentPlayer;
    Button btnSound;
    TopMenuBar topMenuBar;
    VBox infoBox;

    public RootPane(ChessGameManager data, ModelUi dataUi) {
        this.data = data;
        this.dataUi = dataUi;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        boardView = new BoardView(data, dataUi);
        topMenuBar = new TopMenuBar(data, dataUi);
        setTop(topMenuBar);

        infoBox = createInfo();
        VBox centerBox = new VBox(infoBox, boardView);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setSpacing(5);

        setCenter(centerBox);
    }

    private VBox createInfo() {
        lblTitle = new Label("CHESS GAME");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 1px;");
        lblPlayerWhite = new Label();
        lblPlayerWhite.setStyle("-fx-font-size: 15px; -fx-font-weight: normal; -fx-padding: 1px;");
        lblPlayerBlack = new Label();
        lblPlayerBlack.setStyle("-fx-font-size: 15px; -fx-font-weight: normal; -fx-padding: 1px;");
        lblCurrentPlayer = new Label();
        lblCurrentPlayer.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 5px;");
        btnSound = new Button(dataUi.getSoundOn() ? "Desativar Som" : "Ativar Som");
        VBox centerBox = new VBox(lblTitle, lblPlayerWhite, lblPlayerBlack, lblCurrentPlayer, btnSound);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setSpacing(0);
        return centerBox;
    }

    private void registerHandlers() {
        data.addPropertyChangeListener(ChessGameManager.PROP_VALUE_PLAYER, evt -> {
            update();
        });
        dataUi.addPropertyChangeListener(ModelUi.PROP_VALUE_SOUND, evt -> {
            update();
        });
        btnSound.setOnAction(e -> {
            dataUi.setSoundOn(!dataUi.getSoundOn());
        });
        boardView.widthProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.min(getWidth(), getHeight() - topMenuBar.getHeight() - infoBox.prefHeight(-1) - 20),
                widthProperty(), heightProperty(), topProperty()));
        boardView.heightProperty().bind(boardView.widthProperty());
    }

    private void update() {
        String playerWhite = data.getPlayerWhite() != null ? data.getPlayerWhite() : "Not defined";
        String playerBlack = data.getPlayerBlack() != null ? data.getPlayerBlack() : "Not defined";
        lblPlayerWhite.setText("White Player: " + playerWhite);
        lblPlayerBlack.setText("Black Player: " + playerBlack);
        lblCurrentPlayer.setText("Current Player: " + (data.getTeamToPlay() == PieceTeamEnum.WHITE ? playerWhite : playerBlack));
        btnSound.setText(dataUi.getSoundOn() ? "Desativar Som" : "Ativar Som");
    }


    private void showAlert(String title, String message, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}