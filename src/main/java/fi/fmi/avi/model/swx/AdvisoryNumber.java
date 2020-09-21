package fi.fmi.avi.model.swx;

public interface AdvisoryNumber {
    int getYear();

    int getSerialNumber();

    String asAdvisoryNumber();
}
