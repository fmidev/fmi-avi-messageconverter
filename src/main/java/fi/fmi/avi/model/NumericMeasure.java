package fi.fmi.avi.model;

public interface NumericMeasure extends AviationCodeListUser {

    /**
     * Returns the value, or null if value is unknown. Note that the value for zero
     * may be either 0.0d or -0.0d, and the handling code may have to treat them differently.
     * For testing it may be useful to know that 1.0d/0.0d == Double.POSITIVE_INFINITY and
     * 1.0d/-0.0d == Double.NEGATIVE_INFINITY
     *
     * @return the provided measure value.
     */
    Double getValue();

    String getUom();
}
