package fi.fmi.avi.data;

public interface NumericMeasure extends AviationCodeListUser, PossiblyMissingContent {

    Double getValue();

    void setValue(Double value);

    String getUom();

    void setUom(String uom);

}
