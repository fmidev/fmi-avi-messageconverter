package fi.fmi.avi.model.metar.impl;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirectionImpl;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.metar.RunwayVisualRange;

/**
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RunwayVisualRangeImpl implements RunwayVisualRange, Serializable {

    private static final long serialVersionUID = -5828225096013766498L;

    private RunwayDirectionImpl runwayDirection;
    private NumericMeasure meanRVR;
    private NumericMeasure varyingMinRVR;
    private NumericMeasure varyingMaxRVR;
    private RelationalOperator meanRVROperator;
    private RelationalOperator minRVROperator;
    private RelationalOperator maxRVROperator;
    private VisualRangeTendency pastTendency;

    public RunwayVisualRangeImpl() {
    }

    public RunwayVisualRangeImpl(final RunwayVisualRange input) {
        if (input != null) {
            if (input.getRunwayDirection() != null) {
                this.runwayDirection = new RunwayDirectionImpl(input.getRunwayDirection());
            }
            if (input.getMeanRVR() != null) {
                this.meanRVR = new NumericMeasureImpl(input.getMeanRVR());
            }
            this.meanRVROperator = input.getMeanRVROperator();
            this.minRVROperator = input.getVaryingRVRMinimumOperator();
            this.maxRVROperator = input.getVaryingRVRMaximumOperator();
            this.pastTendency = input.getPastTendency();
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getRunwayDirection()
     */
    @Override
    public RunwayDirectionImpl getRunwayDirection() {
        return runwayDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setRunwayDirection()
     */
    @Override
    public void setRunwayDirection(final RunwayDirectionImpl runwayDirection) {
        this.runwayDirection = runwayDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getMeanRVR()
     */
    @Override
    public NumericMeasure getMeanRVR() {
        return meanRVR;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setMeanRVR(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMeanRVR(final NumericMeasure meanRVR) {
        this.meanRVR = meanRVR;
    }

    @Override
    public NumericMeasure getVaryingRVRMinimum() {
        return this.varyingMinRVR;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setVaryingRVRMinimum(final NumericMeasure minimum) {
        this.varyingMinRVR = minimum;
    }

    @Override
    public NumericMeasure getVaryingRVRMaximum() {
        return this.varyingMaxRVR;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setVaryingRVRMaximum(final NumericMeasure maximum) {
        this.varyingMaxRVR = maximum;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getMeanRVROperator()
     */
    @Override
    public RelationalOperator getMeanRVROperator() {
        return meanRVROperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setMeanRVROperator(fi.fmi.avi.model.AviationCodeListUser.RelationalOperator)
     */
    @Override
    public void setMeanRVROperator(final RelationalOperator meanRVROperator) {
        this.meanRVROperator = meanRVROperator;
    }

    @Override
    public RelationalOperator getVaryingRVRMinimumOperator() {
        return minRVROperator;
    }

    @Override
    public void setVaryingRVRMinimumOperator(final RelationalOperator minRVROperator) {
        this.minRVROperator = minRVROperator;
    }

    @Override
    public RelationalOperator getVaryingRVRMaximumOperator() {
        return maxRVROperator;
    }

    @Override
    public void setVaryingRVRMaximumOperator(final RelationalOperator maxRVROperator) {
        this.maxRVROperator = maxRVROperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#getPastTendency()
     */
    @Override
    public VisualRangeTendency getPastTendency() {
        return pastTendency;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.RunwayVisualRange#setPastTendency(fi.fmi.avi.model.AviationCodeListUser.VisualRangeTendency)
     */
    @Override
    public void setPastTendency(final VisualRangeTendency pastTendency) {
        this.pastTendency = pastTendency;
    }

}
