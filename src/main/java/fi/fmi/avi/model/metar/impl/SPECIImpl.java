package fi.fmi.avi.model.metar.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.metar.SPECI;

/**
 * Created by rinne on 06/03/2018.
 */
@JsonDeserialize(as = SPECIImpl.class)
public class SPECIImpl extends METARImpl implements SPECI {

    public SPECIImpl() {
        super();
    }

    public SPECIImpl(final SPECI input) {
        super(input);
    }
}
