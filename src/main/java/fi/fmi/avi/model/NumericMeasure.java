package fi.fmi.avi.model;

public interface NumericMeasure extends AviationCodeListUser {

    Double getValue();

    void setValue(Double value);

    String getUom();

    void setUom(String uom);

}
