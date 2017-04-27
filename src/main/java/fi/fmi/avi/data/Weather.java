package fi.fmi.avi.data;


public interface Weather {

    String getCode();

    String getDescription();

    void setCode(final String code);

    void setDescription(final String description);

}
