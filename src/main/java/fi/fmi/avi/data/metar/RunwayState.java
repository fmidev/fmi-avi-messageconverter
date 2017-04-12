package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.AviationCodeListUser.MissingReason;
import fi.fmi.avi.data.AviationCodeListUser.RunwayContamination;
import fi.fmi.avi.data.AviationCodeListUser.RunwayDeposit;

public interface RunwayState extends AviationCodeListUser {

    boolean isAllRunways();

    boolean isCleared();

    boolean isEstimatedSurfaceFrictionUnreliable();

    boolean isSnowClosure();

    String getRunwayDirectionDesignator();

    RunwayDeposit getDeposit();

    RunwayContamination getContamination();

    NumericMeasure getDepthOfDeposit();

    Double getEstimatedSurfaceFriction();


    void setAllRunways(boolean allRunways);

    void setCleared(boolean cleared);

    void setEstimatedSurfaceFrictionUnreliable(boolean estimatedSurfaceFrictionUnreliable);

    void setSnowClosure(boolean snowClosure);

    void setRunwayDirectionDesignator(String runwayDirectionDesignator);

    void setDeposit(RunwayDeposit deposit);

    void setContamination(RunwayContamination contamination);

    void setDepthOfDeposit(NumericMeasure depthOfDeposit);

    void setEstimatedSurfaceFriction(Double estimatedSurfaceFriction);

}
