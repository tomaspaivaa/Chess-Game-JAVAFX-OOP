package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.MoveResult;
import pt.isec.pa.chess.model.WinnerEnum;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceTeamEnum;
import pt.isec.pa.chess.model.data.PieceTypeEnum;
import pt.isec.pa.chess.ui.res.images.ImageManager;
import pt.isec.pa.chess.ui.res.sounds.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends Canvas {
    private static final double MARGIN = 50;
    ChessGameManager data;
    ModelUi dataUi;
    private int[] selectedSource = null;

    public BoardView(ChessGameManager data, ModelUi dataUi) {
        super(550, 550);
        this.data = data;
        this.dataUi = dataUi;
        registerHandlers();
        update();
    }

    private Image getPieceImage(Character pieceCode) {
        char color = Character.isUpperCase(pieceCode) ? 'W' : 'B';
        char type = Character.toUpperCase(pieceCode);
        String imageName;
        switch (type) {
            case 'P':
                imageName = "pawn";
                break;
            case 'R':
                imageName = "rook";
                break;
            case 'N':
                imageName = "knight";
                break;
            case 'B':
                imageName = "bishop";
                break;
            case 'Q':
                imageName = "queen";
                break;
            case 'K':
                imageName = "king";
                break;
            default:
                return null;
        }
        imageName += color + ".png";
        return ImageManager.getImage(imageName);
    }

    private void drawPieces(GraphicsContext gc) {
        int boardSize = data.getBoardSize();
        double squareSize = (Math.min(getWidth(), getHeight()) - 2 * MARGIN) / boardSize;

        if (data.queryBoardState().isEmpty()) {
            return;
        }
        String[] pieces = data.queryBoardState().split(",");
        for (String p : pieces) {
            int row = boardSize - Character.getNumericValue(p.charAt(2));
            int col = p.charAt(1) - 'A';
            Image pieceImage = getPieceImage(p.charAt(0));
            double x = MARGIN + col * squareSize;
            double y = MARGIN + row * squareSize;
            gc.drawImage(pieceImage, x, y, squareSize, squareSize);
        }
    }


    private void createViews() {
    }

    private void drawBoard(GraphicsContext gc) {
        gc.clearRect(0, 0, getWidth(), getHeight());

        int boardSize = data.getBoardSize();
        double squareSize = (Math.min(getWidth(), getHeight()) - 2 * MARGIN) / boardSize;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                double x = MARGIN + col * squareSize;
                double y = MARGIN + row * squareSize;
                boolean light = (row + col) % 2 == 0;
                gc.setFill(light ? Color.rgb(240, 217, 181) : Color.rgb(181, 136, 99));
                gc.fillRect(x, y, squareSize, squareSize);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(0.5);
                gc.strokeRect(x, y, squareSize, squareSize);

                // Destacar casa selecionada
                if (selectedSource != null && 7 - selectedSource[0] == row && selectedSource[1] == col) {
                    gc.setStroke(Color.BROWN);
                    gc.setLineWidth(3);
                    gc.strokeRect(x + 1.5, y + 1.5, squareSize - 3, squareSize - 3);
                }

                if (selectedSource != null && dataUi.getShowPossibleMoves()) {
                    List<String> possibleMoves = data.getPossibleMoves((char) ('A' + selectedSource[1]), selectedSource[0] + 1);
                    if (possibleMoves == null) {
                        continue;
                    }
                    for (String move : possibleMoves) {
                        char moveCol = move.charAt(0);
                        int moveRow = Integer.parseInt(move.substring(1));

                        int moveColIdx = moveCol - 'A';
                        int moveRowIdx = boardSize - moveRow;

                        if (moveRowIdx == row && moveColIdx == col) {
                            gc.setStroke(Color.GREEN);
                            gc.setLineWidth(3);
                            gc.strokeRect(x + 1.5, y + 1.5, squareSize - 3, squareSize - 3);
                        }
                    }
                }
            }
        }
    }

    private void drawLabels(GraphicsContext gc) {
        int boardSize = data.getBoardSize();
        double squareSize = (Math.min(getWidth(), getHeight()) - 2 * MARGIN) / boardSize;

        gc.setFill(Color.BLACK);
        double fontSize = squareSize * 0.3;
        gc.setFont(new Font(fontSize));

        // COLUNAS (A-H)
        for (int col = 0; col < boardSize; col++) {
            String label = String.valueOf((char) ('A' + col));
            double x = MARGIN + col * squareSize + squareSize * 0.4;
            double y = MARGIN + boardSize * squareSize + fontSize + 5;
            gc.fillText(label, x, y);
        }

        // LINHAS (1-8)
        for (int row = 0; row < boardSize; row++) {
            String label = String.valueOf(boardSize - row);
            double x = MARGIN - fontSize - 5;
            double y = MARGIN + row * squareSize + squareSize * 0.6;
            gc.fillText(label, x, y);
        }
    }

    private void registerHandlers() {
        data.addPropertyChangeListener(ChessGameManager.PROP_VALUE_BOARD, evt -> {
            update();
        });
        dataUi.addPropertyChangeListener(ModelUi.PROP_VALUE_SOUND, evt -> {
            update();
        });
        dataUi.addPropertyChangeListener(ModelUi.PROP_VALUE_POSSIBLE_MOVES, evt -> {
            update();
        });
        this.widthProperty().addListener((observable, oldValue, newValue) -> update());
        this.heightProperty().addListener((observable, oldValue, newValue) -> update());
        this.setOnMouseClicked(event -> {
            if (data.getWinner() != null) {
                return;
            }

            double x = event.getX();
            double y = event.getY();
            int boardSize = data.getBoardSize();
            double squareSize = (Math.min(getWidth(), getHeight()) - 2 * MARGIN) / boardSize;

            int col = (int) ((x - MARGIN) / squareSize);
            int row = (int) ((y - MARGIN) / squareSize);

            if (col < 0 || col >= boardSize || row < 0 || row >= boardSize) return;

            row = boardSize - 1 - row;

            char clickedCol = (char) ('A' + col);
            int clickedRow = row + 1;
            Character clickedPiece = data.getCharPieceTypeAt(clickedCol, clickedRow);

            boolean isCurrentTeamPiece = clickedPiece != null &&
                    ((Character.isUpperCase(clickedPiece) && data.getTeamToPlay() == PieceTeamEnum.WHITE) ||
                            (Character.isLowerCase(clickedPiece) && data.getTeamToPlay() == PieceTeamEnum.BLACK));

            if (selectedSource == null) {
                if (isCurrentTeamPiece) {
                    selectedSource = new int[]{row, col};
                    update();
                }
            } else {
                char fromCol = (char) ('A' + selectedSource[1]);
                int fromRow = selectedSource[0] + 1;
                List<String> possibleMoves = data.getPossibleMoves(fromCol, fromRow);

                if (isCurrentTeamPiece && (selectedSource[0] != row || selectedSource[1] != col)) {
                    if (!dataUi.getIsNormalMode() || possibleMoves.isEmpty()) {
                        selectedSource = new int[]{row, col};
                    }
                    update();
                    return;
                }

                if (selectedSource[0] == row && selectedSource[1] == col) {
                    return;
                }

                int[] dest = new int[]{row, col};
                char toCol = (char) ('A' + dest[1]);
                int toRow = dest[0] + 1;

                String pieceFromType = data.getPieceTypeName(fromCol, fromRow);
                List<String> seq = new ArrayList<>();
                if (pieceFromType != null) {
                    seq.add(pieceFromType + ".mp3");
                }
                seq.add(Character.toLowerCase(fromCol) + ".mp3");
                seq.add(fromRow + ".mp3");
                seq.add(Character.toLowerCase(toCol) + ".mp3");
                seq.add(toRow + ".mp3");

                if (data.doesPositionHasAPiece(toCol, toRow)) {
                    String pieceTypeCaptured = data.getPieceTypeName(toCol, toRow);
                    seq.add("captured.mp3");
                    seq.add(pieceTypeCaptured + ".mp3");
                }

                MoveResult result = data.executeMove(fromCol, fromRow, toCol, toRow);

                if (result == MoveResult.VALID || result == MoveResult.VALID_PROMOTION) {
                    if (result == MoveResult.VALID_PROMOTION) {
                        showPromotionDialog(toCol, toRow);
                        if (data.getWinner() != null) {
                            checkWinner();
                        }
                    }
                    if (data.getIsAnyTeamInCheck()) {
                        seq.add("check.mp3");
                    }
                    if (dataUi.getSoundOn()) {
                        SoundManager.playSequence(seq);
                    }
                    selectedSource = null;
                } else if (data.getWinner() != null) {
                    checkWinner();
                    return;
                } else if (!dataUi.getIsNormalMode() && result == MoveResult.INVALID) {
                    selectedSource = null;
                }
            }
            update();
        });
    }

    private void checkWinner() {
        WinnerEnum winner = data.getWinner();
        if (winner == WinnerEnum.DRAW) {
            selectedSource = null;
            update();
            showAlert("Game Over", "Empate por afogamento (Stalemate)!", javafx.scene.control.Alert.AlertType.INFORMATION);
        } else {
            selectedSource = null;
            update();
            String winnerName = (winner == WinnerEnum.WHITE ? data.getPlayerWhite() : data.getPlayerBlack());
            showAlert("Game Over", "Vencedor: " + winnerName, javafx.scene.control.Alert.AlertType.INFORMATION);
        }
    }

    private void showPromotionDialog(char col, int row) {
        PieceTypeEnum chosenPiece = PromotionDialog.show();
        data.pawnPromotion(col, row, chosenPiece);
        update();
    }

    public void update() {
        GraphicsContext gc = this.getGraphicsContext2D();
        drawBoard(gc);
        drawLabels(gc);
        drawPieces(gc);
    }

private void showAlert(String title, String message, javafx.scene.control.Alert.AlertType type) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}