package pt.isec.pa.chess.model.memento;

import java.io.*;

public class Memento implements IMemento, Serializable {
    private final byte[] snapshot;

    public Memento(Serializable obj) {
        byte[] data;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            data = baos.toByteArray();
        } catch (Exception e) {
            data = null;
        }
        this.snapshot = data;
    }

    @Override
    public Object getSnapshot() {
        if (snapshot == null) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(snapshot);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
