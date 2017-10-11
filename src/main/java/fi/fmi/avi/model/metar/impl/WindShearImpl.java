package fi.fmi.avi.model.metar.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.metar.WindShear;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WindShearImpl implements WindShear {

    private boolean allRunways;
    private List<RunwayDirection> runwayDirections;

    public WindShearImpl() {
    }

    public WindShearImpl(final WindShear input) {
        if (input != null) {
            this.allRunways = input.isAllRunways();
            if (input.getRunwayDirections() != null) {
                this.runwayDirections = new ArrayList<>(input.getRunwayDirections());
            }
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#isAllRunways()
     */
    @Override
    public boolean isAllRunways() {
        return allRunways;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#getRunwayDirections()
     */
    @Override
    public List<RunwayDirection> getRunwayDirections() {
        return runwayDirections;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#setAllRunways(boolean)
     */
    @Override
    public void setAllRunways(final boolean allRunways) {
        this.allRunways = allRunways;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#setRunwayDirections(java.util.List)
     */
    @Override
    public void setRunwayDirections(final List<RunwayDirection> runwayDirections) {
        this.runwayDirections = runwayDirections;
    }

}
