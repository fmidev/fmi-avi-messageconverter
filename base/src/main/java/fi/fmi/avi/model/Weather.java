package fi.fmi.avi.model;


public interface Weather {

    String getCode();

    String getDescription();

    void setCode(final String code);

    void setDescription(final String description);

}
