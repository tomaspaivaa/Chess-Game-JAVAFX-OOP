package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Pawn;
import pt.isec.pa.chess.model.data.PieceTeamEnum;
import pt.isec.pa.chess.model.data.PieceTypeEnum;
import pt.isec.pa.chess.model.memento.CareTaker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * A classe ChessGameManager atua como uma facade para o modelo do jogo de xadrez,
 * Implementa o padrão Memento para funcionalidades de undo/redo e usa
 * PropertyChangeSupport para notificações de listeners.
 *
 * @author Nuno Tomás Paiva & Rui Santos
 * @version final
 *
 * @see ChessGame
 */
public class ChessGameManager {
    /** Nome da propriedade para notificação de mudanças no tabuleiro */
    public static final String PROP_VALUE_BOARD = "prop_board";
    /** Nome da propriedade para notificação de mudanças no jogador atual */
    public static final String PROP_VALUE_PLAYER = "prop_player";

    private final PropertyChangeSupport pcs;
    private ChessGame chessGame;
    private final CareTaker mementoManager;

    /**
     * Constrói um ChessGameManager e cria uma instância ChessGame.
     *
     */
    public ChessGameManager() {
        this.chessGame = new ChessGame(new Board());
        this.mementoManager = new CareTaker(chessGame);
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Adiciona um listener com uma propriedade para notificações específicas.
     *
     * @param property O nome da propriedade a ser ouvida (PROP_VALUE_BOARD ou PROP_VALUE_PLAYER)
     * @param listener O listener a ser adicionado
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    /**
     * Verifica se uma posição no tabuleiro contém uma peça.
     *
     * @param col A coluna (A-H)
     * @param row A linha (1-8)
     * @return true se a posição contém uma peça, false caso contrário
     */
    public boolean doesPositionHasAPiece(char col, int row) {
        return chessGame.doesPositionHasAPiece(col, row);
    }

    /**
     * Verifica se alguma das equipas está em xeque.
     *
     * @return true se a equipa branca ou preta está em xeque, false caso contrário
     */
    public boolean getIsAnyTeamInCheck(){
        return chessGame.isTeamInCheck(PieceTeamEnum.WHITE) || chessGame.isTeamInCheck(PieceTeamEnum.BLACK);
    }

    /**
     * Obtém o tamanho do tabuleiro de xadrez.
     *
     * @return O tamanho do tabuleiro (8 para xadrez padrão)
     */
    public int getBoardSize() {
        return chessGame.getBoardSize();
    }

    /**
     * Exporta o estado atual do jogo para um arquivo binário (Serialization).
     *
     * @param fileName O nome do arquivo para salvar
     * @return true se a exportação foi bem-sucedida, false caso contrário
     */
    public boolean exportPartialGameBin(String fileName) {
        return ChessGameSerialization.save(fileName, this.chessGame);
    }

    /**
     * Importa um estado de jogo de um arquivo binário (Serialization).
     *
     * @param fileName O nome do arquivo para carregar
     * @return true se a importação foi bem-sucedida, false caso contrário
     */
    public boolean importPartialGameFromFileBin(String fileName) {
        this.chessGame = ChessGameSerialization.load(fileName);
        mementoManager.reset();
        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, null, getTeamToPlay());
        return this.chessGame != null;
    }

    /**
     * Obtém a equipa atual cuja vez é de jogar.
     *
     * @return A equipa atual (BRANCO ou PRETO)
     */
    public PieceTeamEnum getTeamToPlay() {
        return chessGame.getTeamToPlay();
    }

    /**
     * Executa um movimento no tabuleiro de xadrez.
     *
     * @param colPiece A coluna da peça a mover (A-H)
     * @param rowPiece A linha da peça a mover (1-8)
     * @param colToMove A coluna de destino (A-H)
     * @param rowToMove A linha de destino (1-8)
     * @return true se o movimento foi válido e executado, false caso contrário
     */
    public MoveResult executeMove(char colPiece, int rowPiece, char colToMove, int rowToMove) {
        PieceTeamEnum oldTeamEnum = getTeamToPlay();
        mementoManager.save();
        MoveResult result = chessGame.executeMove(colPiece, rowPiece, colToMove, rowToMove);
        if (result == MoveResult.INVALID) {
            pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
            mementoManager.discardLastSave();
            return result;
        }

        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, oldTeamEnum, getTeamToPlay());
        return result;
    }

    /**
     * Verifica se uma operação de undo está disponível.
     *
     * @return true se undo é possível, false caso contrário
     */
    public boolean hasUndo() {
        return mementoManager.hasUndo();
    }

    /**
     * Desfaz o último movimento, se possível.
     */
    public void undo() {
        if (!mementoManager.hasUndo()) {
            return;
        }

        mementoManager.undo();
        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, null, getTeamToPlay());
    }

    /**
     * Verifica se uma operação de redo está disponível.
     *
     * @return true se redo é possível, false caso contrário
     */
    public boolean hasRedo() {
        return mementoManager.hasRedo();
    }

    /**
     * Refaz o último movimento desfeito, se possível.
     */
    public void redo() {
        if (!mementoManager.hasRedo()) {
            return;
        }
        mementoManager.redo();
        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, null, getTeamToPlay());
    }

    /**
     * Obtém o nome do jogador branco.
     *
     * @return O nome do jogador branco
     */
    public String getPlayerWhite() {
        return this.chessGame.getPlayerWhite();
    }

    /**
     * Define o nome do jogador branco.
     *
     * @param playerWhite O nome a ser definido para o jogador branco
     */
    public void setPlayerWhite(String playerWhite) {
        this.chessGame.setPlayerWhite(playerWhite);
    }

    /**
     * Obtém o nome do jogador preto.
     *
     * @return O nome do jogador preto
     */
    public String getPlayerBlack() {
        return this.chessGame.getPlayerBlack();
    }

    /**
     * Define o nome do jogador preto.
     *
     * @param playerBlack O nome a ser definido para o jogador preto
     */
    public void setPlayerBlack(String playerBlack) {
        this.chessGame.setPlayerBlack(playerBlack);
    }

    /**
     * Obtém o estado atual do tabuleiro como uma string de texto normalizada.
     *
     * @return Uma string representando o estado do tabuleiro
     */
    public String queryBoardState() {
        return chessGame.queryBoardState();
    }

    /**
     * Obtém todos os movimentos possíveis para uma peça na posição especificada.
     *
     * @param col A coluna da peça (A-H)
     * @param row A linha da peça (1-8)
     * @return Uma lista de movimentos possíveis em notação algébrica
     */
    public List<String> getPossibleMoves(char col, int row) {
        return chessGame.getPossibleMovesWithColRow(col, row);
    }

    /**
     * Determina o vencedor do jogo, se houver.
     *
     * @return A equipa vencedora (BRANCA ou PRETA), EMPATE se for empate, ou null se o jogo estiver em andamento
     */
    public WinnerEnum getWinner() {
        if (chessGame.isDraw(PieceTeamEnum.WHITE) || chessGame.isDraw(PieceTeamEnum.BLACK)) {
            return WinnerEnum.DRAW;
        }
        if (chessGame.isCheckMate(PieceTeamEnum.WHITE)) {
            return WinnerEnum.BLACK;
        }
        if (chessGame.isCheckMate(PieceTeamEnum.BLACK)) {
            return WinnerEnum.WHITE;
        }

        return null;
    }

    /**
     * Obtém o nome do tipo da peça na posição especificada.
     *
     * @param col A coluna (A-H)
     * @param row A linha (1-8)
     * @return O nome do tipo da peça em minúsculas, ou null se não existir peça
     */
    public String getPieceTypeName(char col, int row) {
        return chessGame.getPieceTypeName(col, row);
    }

    /**
     * Obtém a representação em char da peça na posição especificada.
     *
     * @param col A coluna (A-H)
     * @param row A linha (1-8)
     * @return O char representando a peça, ou null se não existir peça
     */
    public Character getCharPieceTypeAt(char col, int row) {
        String clickedPosition = "" + col + row;
        String boardState = queryBoardState();
        if (boardState == null || boardState.isEmpty())
            return null;

        String[] pieces = boardState.split(",");
        for (String p : pieces) {
            if (p.length() >= 3 && p.substring(1, 3).equalsIgnoreCase(clickedPosition)) {
                return p.charAt(0);
            }
        }
        return null;
    }

    /**
     * Verifica se um peão na posição especificada pode ser promovido.
     *
     * @param col A coluna (A-H)
     * @param row A linha (1-8)
     * @return true se o peão pode ser promovido, false caso contrário
     */
    public boolean isPawnPromotable(char col, int row){
        return chessGame.isPawnPromotable(col, row);
    }

    /**
     * Inicia um novo jogo completo com a configuração padrão de peças.
     */
    public void start() {
        chessGame.startCompleteGame();
        mementoManager.reset();
        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, null, getTeamToPlay());
    }

    /**
     * Importa um estado de jogo através de um ficheiro de texto.
     *
     * @param gameData Os dados do jogo em formato texto
     * @return true se a importação foi bem-sucedida, false caso contrário
     */
    public boolean importPartialGameTxt(String gameData) {
        boolean result = chessGame.importPartialGameTxt(gameData);
        mementoManager.reset();
        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, null, getTeamToPlay());
        return result;
    }

    /**
     * Verifica se um peão específico pode ser promovido.
     *
     * @param pawn O peão a verificar
     * @return true se o peão pode ser promovido, false caso contrário
     */
    public boolean isPawnPromotable(Pawn pawn) {
        return chessGame.isPawnPromotable(pawn);
    }

    /**
     * Promove um peão para o tipo de peça especificado.
     *
     * @param col A coluna do peão (A-H)
     * @param row A linha do peão (1-8)
     * @param newPieceType O tipo para promover (RAINHA, TORRE, BISPO ou CAVALO)
     */
    public void pawnPromotion(char col, int row, PieceTypeEnum newPieceType) {
        chessGame.pawnPromotion(col, row, newPieceType);
        pcs.firePropertyChange(PROP_VALUE_BOARD, null, null);
        pcs.firePropertyChange(PROP_VALUE_PLAYER, null, null);
    }

    /**
     * Exporta o estado atual do jogo para um ficheiro de texto.
     *
     * @return Uma string representando o estado do jogo
     */
    public String exportPartialGameTxt() {
        return chessGame.exportPartialGameTxt();
    }
}