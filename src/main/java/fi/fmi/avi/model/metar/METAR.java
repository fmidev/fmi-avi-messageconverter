package fi.fmi.avi.model.metar;

/**
 * Created by rinne on 13/04/2018.
 */
public interface METAR extends MeteorologicalTerminalAirReport {

    boolean isSpecial();

    boolean isRoutineDelayed();
}
