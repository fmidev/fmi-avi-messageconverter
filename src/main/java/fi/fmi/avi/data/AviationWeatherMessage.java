package fi.fmi.avi.data;

public interface AviationWeatherMessage {

    int getIssueDayOfMonth();

    int getIssueHour();

    int getIssueMinute();

    String getIssueTimeZone();

    void setIssueDayOfMonth(final int day);

    void setIssueHour(final int hour);

    void setIssueMinute(final int minute);

    void setIssueTimeZone(String timeZone);
}
