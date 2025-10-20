package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pt.isec.pa.chess.model.ModelLog;

public class ModelLogPane extends VBox {
    Button btnClear;
    ListView<String> logList;

    public ModelLogPane() {
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        btnClear = new Button("Clear");
        btnClear.setPrefWidth(200);
        btnClear.setPrefHeight(100);

        logList = new ListView<>();

        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

        this.getChildren().addAll(logList, btnClear);
    }

    private void registerHandlers() {
        btnClear.setOnAction(e -> {ModelLog.getInstance().clearLogs();});
        ModelLog.getInstance().addPropertyChangeListener(ModelLog.PROP_LOG, evt -> {
            update();
        });
    }

    private void update() {
        logList.getItems().setAll(ModelLog.getInstance().getLogs());
    }
}
