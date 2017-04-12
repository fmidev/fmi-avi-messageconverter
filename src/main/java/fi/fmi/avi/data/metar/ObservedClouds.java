package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.CloudLayer;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.AviationCodeListUser.MissingReason;

public interface ObservedClouds extends AviationCodeListUser {

    boolean isAmountAndHeightUnobservableByAutoSystem();

    NumericMeasure getVerticalVisibility();

    List<CloudLayer> getLayers();


    void setAmountAndHeightUnobservableByAutoSystem(boolean amountAndHeightUnobservableByAutoSystem);

    void setVerticalVisibility(NumericMeasure verticalVisibility);

    void setLayers(List<CloudLayer> layers);

}
