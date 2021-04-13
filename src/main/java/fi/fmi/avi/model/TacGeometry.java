package fi.fmi.avi.model;

import java.io.Serializable;

public class TacGeometry implements Serializable {
    private static final long serialVersionUID = -8508395627249599116L;

    final String data;

    public TacGeometry(final String s) {
        this.data = s;
    }
}
