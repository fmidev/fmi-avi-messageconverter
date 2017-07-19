package fi.fmi.avi.model;

public interface CloudLayer extends AviationCodeListUser {

    CloudAmount getAmount();

    NumericMeasure getBase();

    CloudType getCloudType();

    void setAmount(CloudAmount amount);

    void setBase(NumericMeasure base);

    void setCloudType(CloudType cloudType);

}
