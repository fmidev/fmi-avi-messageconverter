package fi.fmi.avi.data;

/**
 * Created by rinne on 30/01/15.
 */
public interface PossiblyMissingContent extends AviationCodeListUser {

    public MissingReason getMissingReason();

    public void setMissingReason(final MissingReason missingReason);

    public boolean notMissing();
}
