package fi.fmi.avi.data;

import java.util.List;

public interface AviationWeatherMessage {

    int getIssueDayOfMonth();

    int getIssueHour();

    int getIssueMinute();

    String getIssueTimeZone();

    List<String> getRemarks();

    void setIssueDayOfMonth(final int day);

    void setIssueHour(final int hour);

    void setIssueMinute(final int minute);

    void setIssueTimeZone(String timeZone);

    void setRemarks(List<String> remarks);


}
