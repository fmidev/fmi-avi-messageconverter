package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.RunwayDirection;

public interface RunwayState extends AviationCodeListUser {

    boolean isAllRunways();

    boolean isCleared();

    boolean isEstimatedSurfaceFrictionUnreliable();

    boolean isSnowClosure();
    
    boolean isRepetition();
    
    boolean isDepthNotMeasurable();
    
    boolean isRunwayNotOperational();

    RunwayDirection getRunwayDirection();

    RunwayDeposit getDeposit();

    RunwayContamination getContamination();

    NumericMeasure getDepthOfDeposit();
    
    RelationalOperator getDepthOperator();

    Double getEstimatedSurfaceFriction();
    
    BreakingAction getBreakingAction();
    

    void setAllRunways(boolean allRunways);

    void setCleared(boolean cleared);

    void setEstimatedSurfaceFrictionUnreliable(boolean estimatedSurfaceFrictionUnreliable);

    void setSnowClosure(boolean snowClosure);
    
    void setRepetition(boolean repetition);
    
    void setDepthNotMeasurable(boolean notMeasurable);
    
    void setRunwayNotOperational(boolean notOperational);

    void setRunwayDirection(RunwayDirection runwayDirection);

    void setDeposit(RunwayDeposit deposit);

    void setContamination(RunwayContamination contamination);

    void setDepthOfDeposit(NumericMeasure depthOfDeposit);
   
    void setDepthOperator(RelationalOperator operator); 

    void setEstimatedSurfaceFriction(Double estimatedSurfaceFriction);
    
    void setBreakingAction(BreakingAction action);

}
