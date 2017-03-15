package fi.fmi.avi.data.impl;

import fi.fmi.avi.data.AviationCodeListUser;

/**
 * Created by rinne on 30/01/15.
 */
public abstract class PossiblyMissingContentImpl implements fi.fmi.avi.data.PossiblyMissingContent {
    private MissingReason missingReason;

    public PossiblyMissingContentImpl() {
        this.missingReason = MissingReason.NOT_MISSING;
    }

    public PossiblyMissingContentImpl(final MissingReason missingReason){
        this.missingReason = missingReason;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.NumericMeasure#getMissingReason()
     */
    @Override
    public MissingReason getMissingReason() {
        return missingReason;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.NumericMeasure#setMissingReason(fi.fmi.avi.data.AviationCodeListUser.MissingReason)
     */
    @Override
    public void setMissingReason(final MissingReason missingReason) {
        this.missingReason = missingReason;
    }

    @Override
    public boolean notMissing() {
        return this.missingReason != null && MissingReason.NOT_MISSING.equals(this.missingReason);
    }
}
