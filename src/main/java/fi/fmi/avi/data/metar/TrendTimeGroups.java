package fi.fmi.avi.data.metar;

/**
 * Created by rinne on 20/04/17.
 */
public interface TrendTimeGroups {
    int getFromHour();

    int getFromMinute();

    int getToHour();

    int getToMinute();

    boolean isSingleInstance();

    void setFromHour(final int hour);

    void setFromMinute(final int minute);

    void setToHour(final int hour);

    void setToMinute(final int minute);

    void setSingleInstance(final boolean isInstance);

}
