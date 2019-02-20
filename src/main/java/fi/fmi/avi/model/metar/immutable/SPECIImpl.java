package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;

@FreeBuilder
public abstract class SPECIImpl implements MeteorologicalTerminalAirReport, Serializable {
    private static final long serialVersionUID = 1918131429312289735L;



    public static class Builder extends SPECIImpl_Builder implements MeteorologicalTerminalAirReport.Builder<SPECIImpl, Builder> {

    }

}
