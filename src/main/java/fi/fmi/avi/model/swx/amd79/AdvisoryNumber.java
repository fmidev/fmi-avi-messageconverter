package fi.fmi.avi.model.swx.amd79;

public interface AdvisoryNumber {
    int getYear();

    int getSerialNumber();

    String asAdvisoryNumber();
}
