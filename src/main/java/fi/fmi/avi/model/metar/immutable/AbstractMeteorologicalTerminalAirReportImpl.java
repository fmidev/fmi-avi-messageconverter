package fi.fmi.avi.model.metar.immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.TrendForecast;

public abstract class AbstractMeteorologicalTerminalAirReportImpl implements MeteorologicalTerminalAirReport {

    /**
     * Returns true if issue time, valid time and all other time references contained in this
     * message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (!this.getIssueTime().isPresent() || !this.getIssueTime().get().getCompleteTime().isPresent()) {
            return false;
        }
        if (this.getTrends().isPresent()) {
            for (final TrendForecast trend : this.getTrends().get()) {
                if (trend.getPeriodOfChange().isPresent()) {
                    if (!trend.getPeriodOfChange().get().isComplete()) {
                        return false;
                    }
                } else if (trend.getInstantOfChange().isPresent()) {
                    if (!trend.getInstantOfChange().get().getCompleteTime().isPresent()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    @JsonIgnore
    public boolean allAerodromeReferencesContainPosition() {
        Aerodrome ad = this.getAerodrome();
        if (!ad.getFieldElevationValue().isPresent()) {
            return false;
        }
        if (this.getRunwayStates().isPresent()) {
            for (final RunwayState state : this.getRunwayStates().get()) {
                if (state.getRunwayDirection().isPresent()) {
                    if (state.getRunwayDirection().get().getAssociatedAirportHeliport().isPresent()) {
                        ad = state.getRunwayDirection().get().getAssociatedAirportHeliport().get();
                        if (!ad.getReferencePoint().isPresent()) {
                            return false;
                        }
                    }
                }
            }
        }

        if (this.getRunwayVisualRanges().isPresent()) {
            for (final RunwayVisualRange range : this.getRunwayVisualRanges().get()) {
                if (range.getRunwayDirection().getAssociatedAirportHeliport().isPresent()) {
                    ad = range.getRunwayDirection().getAssociatedAirportHeliport().get();
                    if (!ad.getReferencePoint().isPresent()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
