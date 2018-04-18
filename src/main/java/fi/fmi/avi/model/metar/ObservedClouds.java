package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

public interface ObservedClouds extends AviationCodeListUser {

    boolean isAmountAndHeightUnobservableByAutoSystem();

    boolean isNoSignificantCloud();

    Optional<NumericMeasure> getVerticalVisibility();

    Optional<List<CloudLayer>> getLayers();

}
