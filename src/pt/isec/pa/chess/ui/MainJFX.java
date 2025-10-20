package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.Board;

public class MainJFX extends Application {
    ChessGameManager chessGameManager;
    ModelUi modelUi;

    public MainJFX() {
        chessGameManager = new ChessGameManager();
        modelUi = new ModelUi();
    }

    @Override
    public void start(Stage stage) throws Exception {
        createChessStage(stage);
        Stage stageModelLog = new Stage();
        createModelLog(stageModelLog, stage.getX()+stage.getWidth(),stage.getY());

        stage.setOnCloseRequest(windowEvent -> {
            stageModelLog.close();
        });
    }

    private void createChessStage(Stage stage) {
        RootPane root = new RootPane(chessGameManager, modelUi);
        Scene scene = new Scene(root, 800, 1000);
        stage.setScene(scene);
        stage.setTitle("PA Chess Game");
        stage.show();
    }

    private void createModelLog(Stage stage, double x, double y) {
        ModelLogPane mdLogPane = new ModelLogPane();
        Scene scene1 = new Scene(mdLogPane, 400, 400);
        stage.setScene(scene1);
        stage.setTitle("Model Log");
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }
}
