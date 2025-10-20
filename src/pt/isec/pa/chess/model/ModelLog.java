package pt.isec.pa.chess.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton responsável por manter e divulgar logs de eventos do modelo de jogo de xadrez.
 * Utiliza o padrão Observer através de PropertyChangeSupport para notificar os listeners
 * sempre que um novo log é adicionado ou os logs são limpos.
 *
 * @author Nuno Tomás Paiva & Rui Santos
 * @version final
 *
 */
public class ModelLog {
    /**
     * Nome da propriedade utilizada para notificar listeners de alterações no log.
     */
    public static final String PROP_LOG = "log";

    private static ModelLog instance;
    private final List<String> logs;
    private final PropertyChangeSupport pcs;

    /**
     * Construtor privado. Utiliza o padrão Singleton.
     */
    private ModelLog() {
        logs = new ArrayList<>();
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Obtém a instância única de ModelLog.
     *
     * @return a instância singleton de ModelLog
     */
    public static ModelLog getInstance() {
        if (instance == null) {
            instance = new ModelLog();
        }
        return instance;
    }

    /**
     * Adiciona uma nova entrada de log e notifica os ouvintes da alteração.
     *
     * @param log a mensagem de log a adicionar
     */
    public void addLog(String log) {
        logs.add(log);
        pcs.firePropertyChange(PROP_LOG, null, log);
    }

    /**
     * Obtém uma lista imutável com todos os logs registados.
     *
     * @return uma List imutável contendo todas as mensagens de log
     */
    public List<String> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Remove todas as entradas de log e notifica os ouvintes da limpeza.
     */
    public void clearLogs() {
        logs.clear();
        pcs.firePropertyChange(PROP_LOG, null, null);
    }

    /**
     * Adiciona um listener que será notificado quando o log for alterado.
     *
     * @param propLog o nome da propriedade PROP_LOG
     * @param listener o listener a ser registado
     */
    public void addPropertyChangeListener(String propLog, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propLog, listener);
    }
}
