package pt.isec.pa.chess.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelUi {
    private final PropertyChangeSupport pcs;
    public static final String PROP_VALUE_POSSIBLE_MOVES = "prop_possible_moves";
    public static final String PROP_VALUE_SOUND = "prop_possible_sound";

    private boolean showPossibleMoves;
    private boolean soundOn;
    private boolean isNormalMode;
    private boolean areNamesConfirmed;

    public ModelUi() {
        this.pcs = new PropertyChangeSupport(this);
        this.showPossibleMoves = false;
        this.soundOn = true;
        this.isNormalMode = true;
        this.areNamesConfirmed = false;
    }

    public boolean getAreNamesConfirmed() {
        return areNamesConfirmed;
    }

    public void setAreNamesConfirmed(boolean areNamesConfirmed) {
        this.areNamesConfirmed = areNamesConfirmed;
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    public boolean getShowPossibleMoves() {
        return this.showPossibleMoves;
    }

    public void setShowPossibleMoves(boolean show) {
        boolean old = this.showPossibleMoves;
        this.showPossibleMoves = show;
        pcs.firePropertyChange(PROP_VALUE_POSSIBLE_MOVES, old, show);
    }

    public boolean getSoundOn() {
        return this.soundOn;
    }

    public void setSoundOn(boolean show) {
        boolean old = this.soundOn;
        this.soundOn = show;
        pcs.firePropertyChange(PROP_VALUE_SOUND, old, show);
    }

    public boolean getIsNormalMode() {
        return this.isNormalMode;
    }

    public void setNormalMode(boolean normalMode) {
        this.isNormalMode = normalMode;
    }
}
