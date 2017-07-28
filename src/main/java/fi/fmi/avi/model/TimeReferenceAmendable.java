package fi.fmi.avi.model;

import java.time.ZonedDateTime;

public interface TimeReferenceAmendable {
	
	 void amendTimeReferences(final ZonedDateTime referenceTime);
	    
	 boolean areTimeReferencesResolved();
}