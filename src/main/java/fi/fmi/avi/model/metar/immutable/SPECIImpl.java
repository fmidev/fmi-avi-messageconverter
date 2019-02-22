package fi.fmi.avi.model.metar.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilderHelper;
import fi.fmi.avi.model.metar.SPECI;

@FreeBuilder
public abstract class SPECIImpl implements MeteorologicalTerminalAirReport, Serializable {
    private static final long serialVersionUID = 1918131429312289735L;

    public static SPECIImpl immutableCopyOf(final SPECI speci) {
        requireNonNull(speci);
        if (speci instanceof SPECIImpl) {
            return (SPECIImpl) speci;
        } else {
            return Builder.from(speci).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SPECIImpl> immutableCopyOf(final Optional<SPECI> speci) {
        requireNonNull(speci);
        return speci.map(SPECIImpl::immutableCopyOf);
    }

    @Override
    public abstract Builder toBuilder();

    public static class Builder extends SPECIImpl_Builder implements MeteorologicalTerminalAirReport.Builder<SPECIImpl, Builder> {

        public Builder() {

        }

        public static Builder from(final SPECI value) {
            if (value instanceof SPECIImpl) {
                return ((SPECIImpl) value).toBuilder();
            }
            return new SPECIImpl.Builder().copyFrom(value);
        }

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
