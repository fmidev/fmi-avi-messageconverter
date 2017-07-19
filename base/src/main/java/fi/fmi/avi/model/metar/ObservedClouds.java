package fi.fmi.avi.model.metar;

import java.util.List;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

public interface ObservedClouds extends AviationCodeListUser {

    boolean isAmountAndHeightUnobservableByAutoSystem();
    
    boolean isNoSignificantCloud();

    NumericMeasure getVerticalVisibility();

    List<CloudLayer> getLayers();


    void setAmountAndHeightUnobservableByAutoSystem(boolean amountAndHeightUnobservableByAutoSystem);
    
    void setNoSignificantCloud(boolean nsc);

    void setVerticalVisibility(NumericMeasure verticalVisibility);

    void setLayers(List<CloudLayer> layers);

}
