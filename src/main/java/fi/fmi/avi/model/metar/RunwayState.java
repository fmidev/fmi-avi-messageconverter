package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;

public interface RunwayState extends AviationCodeListUser {

    boolean isAppliedToAllRunways();

    boolean isCleared();

    boolean isEstimatedSurfaceFrictionUnreliable();

    boolean isRepetition();

    boolean isDepthNotMeasurable();

    boolean isDepthInsignificant();

    boolean isRunwayNotOperational();

    Optional<RunwayDirection> getRunwayDirection();

    Optional<RunwayDeposit> getDeposit();

    Optional<RunwayContamination> getContamination();

    Optional<NumericMeasure> getDepthOfDeposit();

    Optional<RelationalOperator> getDepthOperator();

    /**
     * The estimated surface friction, if known.
     *
     * @return the surface friction between 0.00 and 0.98
     */
    Optional<Double> getEstimatedSurfaceFriction();

    Optional<BrakingAction> getBrakingAction();

}
