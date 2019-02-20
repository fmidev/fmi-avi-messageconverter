package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilderHelper;

@FreeBuilder
public abstract class SPECIImpl implements MeteorologicalTerminalAirReport, Serializable {
    private static final long serialVersionUID = 1918131429312289735L;

    public abstract Builder toBuilder();

    public static class Builder extends SPECIImpl_Builder implements MeteorologicalTerminalAirReport.Builder<SPECIImpl, Builder> {

        @Override
        public Builder copyFrom(final MeteorologicalTerminalAirReport value) {
            if (value instanceof SPECIImpl) {
                return clear().mergeFrom((SPECIImpl) value);
            }
            MeteorologicalTerminalAirReportBuilderHelper.copyFrom(this, value);
            return this;
        }
    }

}
