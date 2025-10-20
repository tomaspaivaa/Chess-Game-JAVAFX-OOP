package pt.isec.pa.chess.ui;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

public class AskName extends Stage {
    ChessGameManager data;
    ModelUi dataUi;
    TextField tfPlayerOne, tfPlayerTwo;
    Button btnConfirm, btnCancel;

    public AskName(ChessGameManager data, ModelUi dataUi) {
        this.data = data;
        this.dataUi = dataUi;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        // white
        Label lbPlayerOne = new Label("White Player:");
        lbPlayerOne.setMinWidth(80);
        tfPlayerOne = new TextField();
        tfPlayerOne.setPrefWidth(200);
        HBox playerOneBox = new HBox(lbPlayerOne, tfPlayerOne);
        playerOneBox.setAlignment(Pos.BASELINE_LEFT);
        playerOneBox.setSpacing(10);

        // black
        Label lbPlayerTwo = new Label("Black Player:");
        lbPlayerTwo.setMinWidth(80);
        tfPlayerTwo = new TextField();
        tfPlayerTwo.setPrefWidth(200);
        HBox playerTwoBox = new HBox(lbPlayerTwo, tfPlayerTwo);
        playerTwoBox.setAlignment(Pos.BASELINE_LEFT);
        playerTwoBox.setSpacing(10);

        btnConfirm = new Button("Confirm");
        btnConfirm.setPrefWidth(9999);
        btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(9999);
        HBox btns = new HBox(btnCancel, btnConfirm);
        btns.setSpacing(20);

        VBox root = new VBox(playerOneBox, playerTwoBox, btns);
        root.setSpacing(15);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 300, 150);
        this.setScene(scene);
        this.setTitle("Player Names");
        this.setResizable(false);
    }

    private void registerHandlers() {
        btnCancel.setOnAction(actionEvent -> this.close());

        btnConfirm.setOnAction(actionEvent -> {
            data.setPlayerWhite(tfPlayerOne.getText().trim());
            data.setPlayerBlack(tfPlayerTwo.getText().trim());
            dataUi.setAreNamesConfirmed(true);
            this.close();
        });
    }

    private void update() {
    }
}