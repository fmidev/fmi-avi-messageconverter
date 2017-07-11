package fi.fmi.avi.data;

import java.time.ZonedDateTime;

public interface TimeReferenceAmendable {
	
	 void amendTimeReferences(final ZonedDateTime referenceTime);
	    
	 boolean areTimeReferencesResolved();
}