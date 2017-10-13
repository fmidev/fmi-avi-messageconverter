package fi.fmi.avi.model.metar.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HorizontalVisibilityImpl implements HorizontalVisibility {

    private NumericMeasure prevailingVisibility;
    private RelationalOperator prevailingVisibilityOperator;
    private NumericMeasure minimumVisibility;
    private NumericMeasure minimumVisibilityDirection;

    public HorizontalVisibilityImpl() {
    }

    public HorizontalVisibilityImpl(final HorizontalVisibility input) {
        if (input != null) {
            if (input.getPrevailingVisibility() != null) {
                this.prevailingVisibility = new NumericMeasureImpl(input.getPrevailingVisibility());
            }
            this.prevailingVisibilityOperator = input.getPrevailingVisibilityOperator();
            if (input.getMinimumVisibility() != null) {
                this.minimumVisibility = new NumericMeasureImpl(input.getMinimumVisibility());
            }
            if (input.getMinimumVisibilityDirection() != null) {
                this.minimumVisibilityDirection = new NumericMeasureImpl(input.getMinimumVisibilityDirection());
            }
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#getPrevailingVisibility()
     */
    @Override
    public NumericMeasure getPrevailingVisibility() {
        return prevailingVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#getPrevailingVisibilityOperator()
     */
    @Override
    public RelationalOperator getPrevailingVisibilityOperator() {
        return prevailingVisibilityOperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#getMinimumVisibility()
     */
    @Override
    public NumericMeasure getMinimumVisibility() {
        return minimumVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#getMinimumVisibilityDirection()
     */
    @Override
    public NumericMeasure getMinimumVisibilityDirection() {
        return minimumVisibilityDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#setPrevailingVisibility(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
        this.prevailingVisibility = prevailingVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#setPrevailingVisibilityOperator(fi.fmi.avi.model.AviationCodeListUser.RelationalOperator)
     */
    @Override
    public void setPrevailingVisibilityOperator(final RelationalOperator prevailingVisibilityOperator) {
        this.prevailingVisibilityOperator = prevailingVisibilityOperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#setMinimumVisibility(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMinimumVisibility(final NumericMeasure minimumVisibility) {
        this.minimumVisibility = minimumVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.HorizontalVisibility#setMinimumVisibilityDirection(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMinimumVisibilityDirection(final NumericMeasure minimumVisibilityDirection) {
        this.minimumVisibilityDirection = minimumVisibilityDirection;
    }
}
