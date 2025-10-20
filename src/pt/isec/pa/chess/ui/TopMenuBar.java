package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.ChessGameManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TopMenuBar extends VBox {
    ChessGameManager data;
    ModelUi dataUi;
    MenuBar menuBar;
    Menu mnGame, mnMode;
    MenuItem mnNew, mnOpen, mnSave, mnImport, mnExport, mnQuit;
    RadioMenuItem mnNormal, mnLearning, mnShowPossibleMoves;
    MenuItem mnUndo, mnRedo;
    AskName askName;

    public TopMenuBar(ChessGameManager data, ModelUi dataUi) {
        this.data = data;
        this.dataUi = dataUi;
        createViews();
        registerHandlers();
        update();
    }

    public void createViews() {
        askName = new AskName(data, dataUi);
        menuBar = new MenuBar();

        mnGame = new Menu("Game");
        mnNew = new MenuItem("_New");
        mnOpen = new MenuItem("_Open");
        mnSave = new MenuItem("_Save");
        mnImport = new MenuItem("_Import");
        mnExport = new MenuItem("_Export");
        mnQuit = new MenuItem("_Quit");

        mnGame.getItems().addAll(mnNew, new SeparatorMenuItem(), mnOpen, mnSave, new SeparatorMenuItem(),
                mnImport, mnExport, new SeparatorMenuItem(), mnQuit);

        mnMode = new Menu("Mode");
        mnNormal = new RadioMenuItem("Normal");
        mnLearning = new RadioMenuItem("Learning");
        ToggleGroup group = new ToggleGroup();
        mnNormal.setToggleGroup(group);
        mnLearning.setToggleGroup(group);
        mnNormal.setSelected(true);

        mnUndo = new MenuItem("Undo");
        mnRedo = new MenuItem("Redo");
        mnShowPossibleMoves = new RadioMenuItem("Possible Moves");
        mnUndo.setDisable(true);
        mnRedo.setDisable(true);
        mnShowPossibleMoves.setDisable(true);

        mnMode.getItems().addAll(mnNormal, mnLearning, new SeparatorMenuItem(), mnUndo, mnRedo, mnShowPossibleMoves);

        menuBar.getMenus().addAll(mnGame, mnMode);
        this.getChildren().add(menuBar);
    }

    public void registerHandlers() {
        data.addPropertyChangeListener(ChessGameManager.PROP_VALUE_BOARD, evt -> {
            update();
        });

        mnNew.setOnAction(e -> {
            askName.showAndWait();
            if(dataUi.getAreNamesConfirmed()) {
                data.start();
                dataUi.setAreNamesConfirmed(!dataUi.getAreNamesConfirmed());
            }
        });

        mnOpen.setOnAction(e -> {
            File file = chooseFile("Open Chess Game", "*.paf");
            if (file != null && data.importPartialGameFromFileBin(file.getAbsolutePath())) {
                showAlert("Success", "Game successfully loaded: " + file.getName(), Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Error on loading a game", Alert.AlertType.ERROR);
            }
        });

        mnSave.setOnAction(e -> {
            File file = chooseSaveFile("Save Game", "*.paf", ".paf");
            if (file != null && data.exportPartialGameBin(file.getAbsolutePath())) {
                showAlert("Success", "Game successfully saved", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Error on saving a game", Alert.AlertType.ERROR);
            }
        });

        mnImport.setOnAction(e -> {
            File file = chooseFile("Import Chess Partial Game", "*.txt", "*.csv");
            if (file != null) {
                try {
                    String content = Files.readString(file.toPath());

                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        content = content.replace("\n", "").replace("\r", "");
                    }

                    askName.showAndWait();
                    if(dataUi.getAreNamesConfirmed()) {
                        if (data.importPartialGameTxt(content)) {
                            showAlert("Success", "Game successfully loaded: " + file.getName(), Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Error", "Invalid file format", Alert.AlertType.ERROR);
                        }
                        dataUi.setAreNamesConfirmed(!dataUi.getAreNamesConfirmed());
                    }
                } catch (IOException ex) {
                    showAlert("Error", "Error reading file", Alert.AlertType.ERROR);
                }
            }
        });

        mnExport.setOnAction(e -> {
            File file = chooseSaveFile("Export Game", "*.txt", "*.csv");
            if (file != null) {
                try {
                    String content = data.exportPartialGameTxt();

                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        content = content.replace("\n", "").replace("\r", "");
                    }

                    Files.writeString(file.toPath(), content);
                    showAlert("Success", "Game successfully saved", Alert.AlertType.INFORMATION);
                } catch (IOException ex) {
                    showAlert("Error", "Error on saving a game", Alert.AlertType.ERROR);
                }
            }
        });

        mnQuit.setOnAction(e -> Platform.exit());

        mnNormal.setOnAction(e -> {
            mnShowPossibleMoves.setDisable(true);
            mnShowPossibleMoves.setSelected(false);
            mnUndo.setDisable(true);
            mnRedo.setDisable(true);
            dataUi.setShowPossibleMoves(false);
            dataUi.setNormalMode(true);
        });

        mnLearning.setOnAction(e -> {
            mnShowPossibleMoves.setDisable(false);
            mnUndo.setDisable(!data.hasUndo());
            mnRedo.setDisable(!data.hasRedo());
            dataUi.setNormalMode(false);
        });

        mnUndo.setOnAction(e -> {
            data.undo();
        });

        mnRedo.setOnAction(e -> {
            data.redo();
        });

        mnShowPossibleMoves.setOnAction(e -> {
            dataUi.setShowPossibleMoves(mnShowPossibleMoves.isSelected());
        });
    }

    public void update() {
        if(mnLearning.isSelected()) {
            mnUndo.setDisable(!data.hasUndo());
            mnRedo.setDisable(!data.hasRedo());
        }
    }

    private File chooseFile(String title, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("."));
        for (String ext : extensions)
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files (" + ext + ")", ext));
        return fileChooser.showOpenDialog(this.getScene().getWindow());
    }

    private File chooseSaveFile(String title, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("."));

        for (String ext : extensions) {
            String description = ext.toUpperCase().replace("*", "") + " Files";
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(description, ext)
            );
        }

        File file = fileChooser.showSaveDialog(this.getScene().getWindow());

        if (file != null) {
            boolean hasExtension = false;
            for (String ext : extensions) {
                String cleanExt = ext.replace("*", "");
                if (file.getName().toLowerCase().endsWith(cleanExt)) {
                    hasExtension = true;
                    break;
                }
            }

            if (!hasExtension) {
                return new File(file.getAbsolutePath() + extensions[0].replace("*", ""));
            }
        }

        return file;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
