package fi.fmi.avi.data.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.CloudLayer;
import fi.fmi.avi.data.NumericMeasure;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CloudLayerImpl implements CloudLayer {

    private CloudAmount amount;
    private NumericMeasure base;
    private CloudType cloudType;

    public CloudLayerImpl() {
    }

    public CloudLayerImpl(final CloudLayer input) {
        this.amount = input.getAmount();
        this.base = new NumericMeasureImpl(input.getBase());
        this.cloudType = input.getCloudType();
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.CloudLayer#getAmount()
     */
    @Override
    public CloudAmount getAmount() {
        return amount;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.CloudLayer#getBase()
     */
    @Override
    public NumericMeasure getBase() {
        return base;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.CloudLayer#getCloudType()
     */
    @Override
    public CloudType getCloudType() {
        return cloudType;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.CloudLayer#setAmount(fi.fmi.avi.data.AviationCodeListUser.CloudAmount)
     */
    @Override
    public void setAmount(final CloudAmount amount) {
        this.amount = amount;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.CloudLayer#setBase(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setBase(final NumericMeasure base) {
        this.base = base;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.CloudLayer#setCloudType(fi.fmi.avi.data.AviationCodeListUser.CloudType)
     */
    @Override
    public void setCloudType(final CloudType cloudType) {
        this.cloudType = cloudType;
    }
}
