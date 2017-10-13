package fi.fmi.avi.model;

/**
 * Created by rinne on 13/10/17.
 */
public interface AerodromeUpdateListener {

    void aerodromeInfoAdded(final AerodromeUpdateEvent e);
    void aerodromeInfoRemoved(final AerodromeUpdateEvent e);
    void aerodromeInfoChanged(final AerodromeUpdateEvent e);
}
