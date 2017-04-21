package fi.fmi.avi.data.metar.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.data.metar.WindShear;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WindShearImpl implements WindShear {

    private boolean allRunways;
    private List<String> runwayDirectionDesignators;

    public WindShearImpl() {
    }

    public WindShearImpl(final WindShear input) {
        this.allRunways = input.isAllRunways();
        this.runwayDirectionDesignators = new ArrayList<String>(input.getRunwayDirectionDesignators());
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#isAllRunways()
     */
    @Override
    public boolean isAllRunways() {
        return allRunways;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#getRunwayDirectionDesignators()
     */
    @Override
    public List<String> getRunwayDirectionDesignators() {
        return runwayDirectionDesignators;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#setAllRunways(boolean)
     */
    @Override
    public void setAllRunways(final boolean allRunways) {
        this.allRunways = allRunways;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.WindShear#setRunwayDirectionDesignators(java.util.List)
     */
    @Override
    public void setRunwayDirectionDesignators(final List<String> runwayDirectionDesignators) {
        this.runwayDirectionDesignators = runwayDirectionDesignators;
    }

}
