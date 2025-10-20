package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.*;
import pt.isec.pa.chess.model.memento.IMemento;
import pt.isec.pa.chess.model.memento.IOriginator;
import pt.isec.pa.chess.model.memento.Memento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um jogo de xadrez, controlando todas as regras e moves das peças.
 * Implementa Serializable para permitir serialização e IOriginator para suportar o padrão Memento.
 *
 * @author Nuno Tomás Paiva & Rui Santos
 * @version final
 *
 */

public class ChessGame implements Serializable, IOriginator {
    private Board board;
    private PieceTeamEnum teamToPlay;
    private String playerWhite;
    private String playerBlack;
    private PieceTeamEnum lastTeamInCheck;
    private Pawn lastDoubleStepPawn;

    /**
     * Construtor que inicializa o jogo de xadrez com um tabuleiro.
     * @param board O tabuleiro a ser utilizado no jogo
     */
    public ChessGame(Board board) {
        this.board = board;
        this.lastTeamInCheck = null;
        this.teamToPlay = PieceTeamEnum.WHITE;
        this.lastDoubleStepPawn = null;
    }

    /**
     * Verifica se existe uma peça na posição especificada.
     * @param col Coluna da posição (A-H)
     * @param row Linha da posição (1-8)
     * @return true se existir uma peça na posição, false caso contrário
     */
    public boolean doesPositionHasAPiece (char col, int row) {
        Piece piece = getPieceAt(col, row);
        return piece != null;
    }

    /**
     * Obtém o nome do tipo de peça na posição especificada.
     * @param col Coluna da posição (A-H)
     * @param row Linha da posição (1-8)
     * @return Nome do tipo de peça em minúsculas ou null se não houver peça
     */
    public String getPieceTypeName(char col, int row) {
        Piece piece = getPieceAt(col, row);
        if(piece == null) {
            return null;
        }
        return piece.getPieceTypeEnum().toString().toLowerCase();
    }

    /**
     * Obtém os movimentos possíveis para a peça na posição especificada.
     * @param col Coluna da posição (A-H)
     * @param row Linha da posição (1-8)
     * @return Lista de movimentos possíveis no formato "A1" ou null se não houver peça
     */
    public List<String> getPossibleMovesWithColRow(char col, int row) {
        Piece piece = board.getPiece(col, row);
        if (piece == null)
            return null;

        List<String> rawMoves;

        if (piece instanceof Pawn p) {
            rawMoves = getPossibleMovesWithEnPassant(p);
        } else {
            rawMoves = piece.getPossibleMoves();
        }

        return getPossibleMovesWithCheck(piece, rawMoves);
    }

    /**
     * Filtra os movimentos possíveis removendo aqueles que deixariam o rei em xeque.
     * @param piece Peça a ser movimentada
     * @param possibleMoves Lista de movimentos possíveis
     * @return Lista filtrada de movimentos válidos
     */
    private List<String> getPossibleMovesWithCheck(Piece piece, List<String> possibleMoves) {
        List<String> moves = new ArrayList<>();
        char colPiece = piece.getPieceColumn();
        int rowPiece = piece.getPieceRow();
        for(String s : possibleMoves) {
            char col = s.charAt(0);
            int row = Character.getNumericValue(s.charAt(1));
            Piece pieceToRemove = board.getPiece(col, row);
            if (pieceToRemove != null) {
                board.removePiece(col, row);
            }

            board.movePiece(piece, col, row);
            boolean isInCheck = isTeamInCheck(piece.getPieceColor());

            board.movePiece(piece, colPiece, rowPiece);
            if (pieceToRemove != null) {
                board.addPiece(pieceToRemove);
            }

            if (isInCheck) {
                continue;
            }
            moves.add(String.format("%c%d", col, row));
        }
        return moves;
    }

    /**
     * Obtém o tamanho do tabuleiro.
     * @return Tamanho do tabuleiro (8 para xadrez padrão)
     */
    public int getBoardSize() {
        return Board.getBoardSize();
    }

    /**
     * Reinicia o jogo de xadrez com um tabuleiro vazio.
     */
    public void resetChessGame() {
        this.board = new Board();
        this.teamToPlay = PieceTeamEnum.WHITE;
    }

    /**
     * Inicia um jogo completo com todas as peças em suas posições iniciais.
     */
    public void startCompleteGame() {
        resetChessGame();
        for (char c = 'A'; c <= 'H'; c++) {
            board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.PAWN, PieceTeamEnum.WHITE, board, String.format("%c%d", c, 2)));
            board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.PAWN, PieceTeamEnum.BLACK, board, String.format("%c%d", c, 7)));
            if (c == 'A' || c == 'H') {
                board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.ROOK, PieceTeamEnum.WHITE, board, String.format("%c%d*", c, 1)));
                board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.ROOK, PieceTeamEnum.BLACK, board, String.format("%c%d*", c, 8)));
            }
            if (c == 'B' || c == 'G') {
                board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.KNIGHT, PieceTeamEnum.WHITE, board, String.format("%c%d", c, 1)));
                board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.KNIGHT, PieceTeamEnum.BLACK, board, String.format("%c%d", c, 8)));
            }
            if (c == 'C' || c == 'F') {
                board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.BISHOP, PieceTeamEnum.WHITE, board, String.format("%c%d", c, 1)));
                board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.BISHOP, PieceTeamEnum.BLACK, board, String.format("%c%d", c, 8)));
            }
        }
        board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.KING, PieceTeamEnum.WHITE, board, String.format("%c%d*", 'E', 1)));
        board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.KING, PieceTeamEnum.BLACK, board, String.format("%c%d*", 'E', 8)));
        board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.QUEEN, PieceTeamEnum.WHITE, board, String.format("%c%d", 'D', 1)));
        board.addPiece(ChessPieceFactory.createPiece(PieceTypeEnum.QUEEN, PieceTeamEnum.BLACK, board, String.format("%c%d", 'D', 8)));
        ModelLog.getInstance().addLog("Novo jogo iniciado.");
    }

    /**
     * Alterna a vez para o próximo jogador.
     */
    public void retrivePlayerTeam() {
        if (this.teamToPlay == PieceTeamEnum.WHITE) {
            this.teamToPlay = PieceTeamEnum.BLACK;
        } else if (this.teamToPlay == PieceTeamEnum.BLACK) {
            this.teamToPlay = PieceTeamEnum.WHITE;
        }
    }

    /**
     * Obtém o estado atual do tabuleiro em formato de texto.
     * @return String a representar o estado do tabuleiro
     */
    public String queryBoardState() {
        return board.getNormalizedText();
    }

    /**
     * Obtém a equipa que tem a vez de jogar.
     * @return Enum representando a equipa que deve jogar
     */
    public PieceTeamEnum getTeamToPlay() {
        return this.teamToPlay;
    }

    /**
     * Define a equipa que tem a vez de jogar.
     * @param teamToPlay Enum representando a equipa que deve jogar
     */
    public void setTeamToPlay(PieceTeamEnum teamToPlay) {
        this.teamToPlay = teamToPlay;
    }

    /**
     * Obtém a peça na posição especificada, caso exista, se não devolve null.
     * @param col Coluna da posição (A-H)
     * @param row Linha da posição (1-8)
     * @return Peça na posição ou null se não houver peça
     */
    public Piece getPieceAt(char col, int row) {
        return board.getPiece(col, row);
    }

    /**
     * Adiciona uma peça ao tabuleiro a partir de uma string de dados.
     * @param data String contendo os dados da peça
     */
    public void addPieceWithString(String data) {
        board.addPiece(ChessPieceFactory.createPieceFromText(data, board));
    }

    /**
     * Verifica se a equipe especificada está em xeque.
     * @param team Equipe a verificar
     * @return true se a equipe está em xeque, false caso contrário
     */
    public boolean isTeamInCheck(PieceTeamEnum team) {
        Piece teamKing = board.getKingPiece(team);
        if (teamKing == null) {
            return false;
        }
        String teamKingPosition = String.format("%c%d", teamKing.getPieceColumn(), teamKing.getPieceRow());

        for (String enemyPossibleMove : board.getEnemyTeamPossibleMoves(team)) {
            if (teamKingPosition.equals(enemyPossibleMove)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o jogo está empatado para a equipe especificada.
     * @param team Equipe a verificar
     * @return true se a equipe está em empate, false caso contrário
     */
    public boolean isDraw(PieceTeamEnum team) {
        // se o jogo ainda não começo, não pode haver empate
        if (board.getKingPiece(PieceTeamEnum.WHITE) == null || board.getKingPiece(PieceTeamEnum.BLACK) == null) {
            return false;
        }

        if (isTeamInCheck(team)) {
            return false;
        }

        var x = new ArrayList<>(board.getPieces());
        for (Piece piece : x) {
            if (piece.getPieceColor() != team)
                continue;

            for (String move : piece.getPossibleMoves()) {
                char colToMove = move.charAt(0);
                int rowToMove = Integer.parseInt(move.substring(1));
                //dá save do estado atual
                char originalCol = piece.getPieceColumn();
                int originalRow = piece.getPieceRow();
                Piece captured = board.getPiece(colToMove, rowToMove);

                // simula movimento
                if (captured != null)
                    board.removePiece(colToMove, rowToMove);

                board.movePiece(piece, colToMove, rowToMove);
                boolean stillNotInCheck = !isTeamInCheck(team);

                // desfaz movimento
                board.movePiece(piece, originalCol, originalRow);

                if (captured != null)
                    board.addPiece(captured);

                if (stillNotInCheck) {
                    return false;
                }
            }
        }

        ModelLog.getInstance().addLog("EMPATE!");
        return true;
    }

    /**
     * Verifica se a equipa especificada está em xeque-mate.
     * @param team Equipe a verificar
     * @return true se a equipe está em xeque-mate, false caso contrário
     */
    public boolean isCheckMate(PieceTeamEnum team) {
        Piece king = board.getKingPiece(team);
        if (king == null || !isTeamInCheck(team)) {
            return false;
        }

        for (Piece piece : new ArrayList<>(board.getPieces())) {
            if (piece.getPieceColor() != team)
                continue;

            char originalCol = piece.getPieceColumn();
            int originalRow = piece.getPieceRow();

            for (String move : piece.getPossibleMoves()) {
                char colToMove = move.charAt(0);
                int rowToMove = Integer.parseInt(move.substring(1));

                Piece capturedPiece = board.getPiece(colToMove, rowToMove);

                //simulal o movimento
                if (capturedPiece != null)
                    board.removePiece(colToMove, rowToMove);
                board.movePiece(piece, colToMove, rowToMove);

                boolean stillInCheck = isTeamInCheck(team);

                //desfaz o movimento
                board.movePiece(piece, originalCol, originalRow);
                if (capturedPiece != null)
                    board.addPiece(capturedPiece);

                if (!stillInCheck) {
                    return false; // Encontrou um movimento que evita o check
                }
            }
        }
        ModelLog.getInstance().addLog("Checkmate! Equipa perdedora: " + team);
        return true;
    }

    /**
     * Obtém os movimentos possíveis para um peão com o en passant.
     * @param pawn Peão a verificar
     * @return Lista de movimentos possíveis
     */
    private List<String> getPossibleMovesWithEnPassant(Pawn pawn) {
        List<String> moves = new ArrayList<>(pawn.getPossibleMoves());
        char col = pawn.getPieceColumn();
        int row = pawn.getPieceRow();
        int direction = pawn.getPieceColor() == PieceTeamEnum.WHITE ? 1 : -1;

        // linhas correta -  5 para brancas e 4 para pretas
        boolean isOnEnPassantRow = (pawn.getPieceColor() == PieceTeamEnum.WHITE && row == 5) || (pawn.getPieceColor() == PieceTeamEnum.BLACK && row == 4);

        if (lastDoubleStepPawn != null && isOnEnPassantRow) {
            char leftCol = (char)(col - 1);
            if (lastDoubleStepPawn.getPieceColumn() == leftCol && lastDoubleStepPawn.getPieceRow() == row) {
                moves.add(String.format("%c%d", leftCol, row + direction));
            }

            char rightCol = (char)(col + 1);
            if (lastDoubleStepPawn.getPieceColumn() == rightCol && lastDoubleStepPawn.getPieceRow() == row) {
                moves.add(String.format("%c%d", rightCol, row + direction));
            }
        }
        return moves;
    }

    /**
     * Executa um movimento válido no jogo, verificando todas as regras.
     * @param colPiece Coluna da peça a mover
     * @param rowPiece Linha da peça a mover
     * @param colToMove Coluna de destino
     * @param rowToMove Linha de destino
     * @return true se o movimento foi executado com sucesso, false caso contrário
     */
    public MoveResult executeMove(char colPiece, int rowPiece, char colToMove, int rowToMove) {
        Piece pieceToMove = board.getPiece(colPiece, rowPiece);

        if (pieceToMove == null || pieceToMove.getPieceColor() != this.teamToPlay) {
            ModelLog.getInstance().addLog("Movimento inválido: peça inexistente ou da equipa errada.");
            return MoveResult.INVALID;
        }

        List<String> possibleMoves;
        if((pieceToMove instanceof Pawn p)) {
            possibleMoves = getPossibleMovesWithEnPassant(p);
        } else {
            possibleMoves = pieceToMove.getPossibleMoves();
        }

        if (!(possibleMoves.contains(String.format("%c%d", colToMove, rowToMove)))) {
            ModelLog.getInstance().addLog("Movimento inválido: destino inválido para a peça.");
            return MoveResult.INVALID;
        }

        // ----- EN PASSANT -----
        Piece capturedEnPassant = null;
        if (pieceToMove instanceof Pawn) {
            int colDiff = Math.abs(colToMove - colPiece);
            int rowDiff = Math.abs(rowToMove - rowPiece);

            if (colDiff == 1 && rowDiff == 1 && !doesPositionHasAPiece(colToMove, rowToMove)) {
                capturedEnPassant = board.getPiece(colToMove, rowPiece);
            }
        }
        // ----------------------

        Piece pieceToRemove = board.getPiece(colToMove, rowToMove);
        if (pieceToRemove != null) {
            board.removePiece(colToMove, rowToMove);
        }
        if(capturedEnPassant != null) { // EN PASSANT
            board.removePiece(capturedEnPassant.getPieceColumn(), capturedEnPassant.getPieceRow());
        }

        board.movePiece(pieceToMove, colToMove, rowToMove);
        boolean isInCheck = isTeamInCheck(pieceToMove.getPieceColor());

        board.movePiece(pieceToMove, colPiece, rowPiece);
        if (pieceToRemove != null) {
            board.addPiece(pieceToRemove);
        }
        if(capturedEnPassant != null) { // EN PASSANT
            board.addPiece(capturedEnPassant);
        }
        if (isInCheck) {
            ModelLog.getInstance().addLog("Movimento inválido: deixa o rei em check.");
            return MoveResult.INVALID;
        }

        if (pieceToRemove != null) {
            board.removePiece(colToMove, rowToMove);
        }
        if(capturedEnPassant != null) { // EN PASSANT
            board.removePiece(capturedEnPassant.getPieceColumn(), capturedEnPassant.getPieceRow());
        }
        board.movePiece(pieceToMove, colToMove, rowToMove);

        // ------- CASTLING -------
        if (pieceToMove instanceof King) {
            // 0-0 (kingside castling)
            if (colToMove == 'G') {
                Piece rook = board.getPiece('H', rowToMove);
                if (rook instanceof Rook) {
                    board.movePiece(rook, 'F', rowToMove);
                }
            }
            // 0-0-0 (queenside castling)
            else if (colToMove == 'C') {
                Piece rook = board.getPiece('A', rowToMove);
                if (rook instanceof Rook) {
                    board.movePiece(rook, 'D', rowToMove);
                }
            }
        }
        // --------------------------

        // ------- EN PASSANT -------
        if (pieceToMove instanceof Pawn) {
            int diff = Math.abs(rowToMove - rowPiece);
            if (diff == 2) {
                lastDoubleStepPawn = (Pawn) pieceToMove;
            } else {
                lastDoubleStepPawn = null;
            }
        } else {
            lastDoubleStepPawn = null;
        }
        // --------------------------

        ModelLog.getInstance().addLog("Movimento executado: " + pieceToMove.getPieceType() + " de " + colPiece + rowPiece + " para " + colToMove + rowToMove);
        this.retrivePlayerTeam();

        // ----- MODEL LOG -----
        boolean currentTeamInCheck = isTeamInCheck(teamToPlay);
        if (currentTeamInCheck && lastTeamInCheck != teamToPlay) {
            ModelLog.getInstance().addLog("Equipa " + teamToPlay + " está em check.");
            lastTeamInCheck = teamToPlay;
        } else if (!currentTeamInCheck && lastTeamInCheck == teamToPlay) {
            lastTeamInCheck = null;
        }
        // --------------------

        if((pieceToMove instanceof Pawn p) && isPawnPromotable(p)) {
            return MoveResult.VALID_PROMOTION;
        }

        if (isDraw(PieceTeamEnum.WHITE) || isDraw(PieceTeamEnum.BLACK)) {
            return MoveResult.DRAW;
        }

        if (isCheckMate(PieceTeamEnum.WHITE)) {
            return MoveResult.CHECKMATE_BLACK;
        }

        if (isCheckMate(PieceTeamEnum.BLACK)) {
            return MoveResult.CHECKMATE_WHITE;
        }

        return MoveResult.VALID;
    }

    /**
     * Exporta o estado atual do jogo para uma string.
     * @return String contendo o estado do jogo
     */
    public String exportPartialGameTxt() {
        ModelLog.getInstance().addLog("Jogo exportado.");
        StringBuilder sb = new StringBuilder();

        sb.append(teamToPlay.toString());
        sb.append(",");
        sb.append(board.getNormalizedText());

        return sb.toString();
    }

    /**
     * Importa um jogo a partir de uma string de dados.
     * @param gameData String contendo os dados do jogo
     * @return true se a importação foi bem sucedida, false caso contrário
     */
    public boolean importPartialGameTxt(String gameData) {
        board.getPieces().clear();

        String[] parts = gameData.trim().split(",");
        if (parts.length < 1) {
            return false;
        }

        teamToPlay = PieceTeamEnum.valueOf(parts[0].trim().toUpperCase());

        for (int i = 1; i < parts.length; i++) {
            board.addPiece(ChessPieceFactory.createPieceFromText(parts[i].trim(), this.board));
        }
        ModelLog.getInstance().addLog("Jogo importado com sucesso.");
        return true;
    }

    /**
     * Promove um peão para outro tipo de peça.
     * @param col Coluna do peão
     * @param row Linha do peão
     * @param newPieceType Novo tipo de peça para promoção
     */
    public void pawnPromotion(char col, int row, PieceTypeEnum newPieceType) {
        Piece pawn = board.getPiece(col, row);
        if (pawn instanceof Pawn p && isPawnPromotable(p)) {
            board.removePiece(col, row);
            board.addPiece(ChessPieceFactory.createPiece(newPieceType, pawn.getPieceColor(), this.board, String.format("%c%d", col, row)));
            ModelLog.getInstance().addLog("Peão promovido para " + newPieceType);
        }
    }

    /**
     * Verifica se um peão pode ser promovido.
     * @param pawn Peão a verificar
     * @return true se o peão pode ser promovido, false caso contrário
     */
    public boolean isPawnPromotable(Pawn pawn) {
        if (pawn == null) {
            return false;
        }

        if (pawn.getPieceColor() == PieceTeamEnum.BLACK && pawn.getPieceRow() == 1) {
            return true;
        }
        if (pawn.getPieceColor() == PieceTeamEnum.WHITE && pawn.getPieceRow() == 8) {
            return true;
        }
        return false;
    }

    /**
     * Verifica se a peça na posição especificada é um peão que pode ser promovido.
     * @param col Coluna da posição
     * @param row Linha da posição
     * @return true se for um peão promovível, false caso contrário
     */
    public boolean isPawnPromotable(char col, int row) {
        Piece pawn = board.getPiece(col, row);
        if (pawn instanceof Pawn p && isPawnPromotable(p)) {
            return true;
        }
        return false;
    }

    /**
     * Obtém o nome do jogador das peças brancas.
     * @return Nome do jogador
     */
    public String getPlayerWhite() {
        return playerWhite;
    }

    /**
     * Define o nome do jogador das peças brancas.
     * @param playerWhite Nome do jogador
     */
    public void setPlayerWhite(String playerWhite) {
        this.playerWhite = playerWhite;
    }

    /**
     * Obtém o nome do jogador das peças pretas.
     * @return Nome do jogador
     */
    public String getPlayerBlack() {
        return playerBlack;
    }

    /**
     * Define o nome do jogador das peças pretas.
     * @param playerBlack Nome do jogador
     */
    public void setPlayerBlack(String playerBlack) {
        this.playerBlack = playerBlack;
    }

    /**
     * Salva o estado atual do jogo em um memento.
     * @return Memento contendo o estado do jogo
     */
    @Override
    public IMemento save() {
        return new Memento(this);
    }

    /**
     * Restaura o estado do jogo a partir de um memento.
     * @param memento Memento contendo o estado a ser restaurado
     */
    @Override
    public void restore(IMemento memento) {
        Object obj = memento.getSnapshot();
        if (obj instanceof ChessGame saved) {
            this.board = saved.board;
            this.teamToPlay = saved.teamToPlay;
            this.lastDoubleStepPawn = saved.lastDoubleStepPawn;
            this.lastTeamInCheck = saved.lastTeamInCheck;
        }
    }
}