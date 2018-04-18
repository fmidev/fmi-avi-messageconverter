package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;

public interface RunwayState extends AviationCodeListUser {

    boolean isAppliedToAllRunways();

    boolean isCleared();

    boolean isEstimatedSurfaceFrictionUnreliable();

    boolean isSnowClosure();

    boolean isRepetition();

    boolean isDepthNotMeasurable();

    boolean isRunwayNotOperational();

    Optional<RunwayDirection> getRunwayDirection();

    Optional<RunwayDeposit> getDeposit();

    Optional<RunwayContamination> getContamination();

    Optional<NumericMeasure> getDepthOfDeposit();

    Optional<RelationalOperator> getDepthOperator();

    Optional<Double> getEstimatedSurfaceFriction();

    Optional<BreakingAction> getBreakingAction();

}
