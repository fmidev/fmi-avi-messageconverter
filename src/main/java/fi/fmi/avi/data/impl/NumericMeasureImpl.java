package fi.fmi.avi.data.impl;

import fi.fmi.avi.data.NumericMeasure;


public class NumericMeasureImpl implements NumericMeasure {

    private Double value;
    private String uom;

    public NumericMeasureImpl() {
    }

    public NumericMeasureImpl(final Double value, final String uom) {
        this.value = value;
        this.uom = uom;
    }
    
    public NumericMeasureImpl(final Integer value, final String uom) {
        this(new Double(value), uom);
    }

    public NumericMeasureImpl(final NumericMeasure measure) {
        this(measure.getValue(), measure.getUom());
    }


    /* (non-Javadoc)
     * @see fi.fmi.avi.data.NumericMeasure#getValue()
     */
    @Override
    public Double getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.NumericMeasure#setValue(java.lang.Double)
     */
    @Override
    public void setValue(final Double value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.NumericMeasure#getUom()
     */
    @Override
    public String getUom() {
        return uom;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.NumericMeasure#setUom(java.lang.String)
     */
    @Override
    public void setUom(final String uom) {
        this.uom = uom;
    }
}
