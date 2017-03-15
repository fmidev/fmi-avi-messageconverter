package fi.fmi.avi.data.metar.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.PossiblyMissingContentImpl;
import fi.fmi.avi.data.metar.RunwayVisualRange;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */

public class RunwayVisualRangeImpl extends PossiblyMissingContentImpl implements RunwayVisualRange {

    private String runwayDirectionDesignator;
    private NumericMeasure meanRVR;
    private NumericMeasure varyingMinRVR;
    private NumericMeasure varyingMaxRVR;
    private RelationalOperator meanRVROperator;
    private VisualRangeTendency pastTendency;

    public RunwayVisualRangeImpl() {
    }

    public RunwayVisualRangeImpl(final RunwayVisualRange input) {
        super(input.getMissingReason());
        if (MissingReason.NOT_MISSING.equals(this.getMissingReason())) {
            this.runwayDirectionDesignator = input.getRunwayDirectionDesignator();
            this.meanRVR = new NumericMeasureImpl(input.getMeanRVR());
            this.meanRVROperator = input.getMeanRVROperator();
            this.pastTendency = input.getPastTendency();
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getRunwayDirectionDesignator()
     */
    @Override
    public String getRunwayDirectionDesignator() {
        return runwayDirectionDesignator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getMeanRVR()
     */
    @Override
    public NumericMeasure getMeanRVR() {
        return meanRVR;
    }

    @Override
    public NumericMeasure getVaryingRVRMinimum() {
        return this.varyingMinRVR;
    }

    @Override
    public NumericMeasure getVaryingRVRMaximum() {
        return this.varyingMaxRVR;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getMeanRVROperator()
     */
    @Override
    public RelationalOperator getMeanRVROperator() {
        return meanRVROperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getPastTendency()
     */
    @Override
    public VisualRangeTendency getPastTendency() {
        return pastTendency;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setRunwayDirectionDesignator(java.lang.String)
     */
    @Override
    public void setRunwayDirectionDesignator(final String runwayDirectionDesignator) {
        this.runwayDirectionDesignator = runwayDirectionDesignator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setMeanRVR(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMeanRVR(final NumericMeasure meanRVR) {
        this.meanRVR = meanRVR;
    }

    @Override
    public void setVaryingRVRMinimum(final NumericMeasure minimum) {
        this.varyingMinRVR = minimum;
    }

    @Override
    public void setVaryingRVRMaximum(final NumericMeasure maximum) {
        this.varyingMaxRVR = maximum;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setMeanRVROperator(fi.fmi.avi.data.AviationCodeListUser.RelationalOperator)
     */
    @Override
    public void setMeanRVROperator(final RelationalOperator meanRVROperator) {
        this.meanRVROperator = meanRVROperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setPastTendency(fi.fmi.avi.data.AviationCodeListUser.VisualRangeTendency)
     */
    @Override
    public void setPastTendency(final VisualRangeTendency pastTendency) {
        this.pastTendency = pastTendency;
    }

}
