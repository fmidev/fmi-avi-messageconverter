package fi.fmi.avi.model.sigmet;

import java.util.Optional;

import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.VolcanoDescription;

public interface VAInfo {
    Optional<VolcanoDescription> getVolcano();
    Optional<UnitPropertyGroup> getVolcanicAshMovedToFIR();
}
