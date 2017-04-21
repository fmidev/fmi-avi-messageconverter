package fi.fmi.avi.data.metar.impl;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.data.metar.TrendTimeGroups;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TrendTimeGroupsImpl implements TrendTimeGroups {
    private int startHour = -1;
    private int startMinute = -1;
    private int endHour = -1;
    private int endMinute = -1;
    private boolean isSingular = false;

    public TrendTimeGroupsImpl() {
    }

    public TrendTimeGroupsImpl(final TrendTimeGroups input) {
        this.startHour = input.getFromHour();
        this.startMinute = input.getFromMinute();
        this.endHour = input.getToHour();
        this.endMinute = input.getToMinute();
        this.isSingular = input.isSingleInstance();
    }

    //TODO: move these to TAC token parser
    /*
    public TrendTimeGroupsImpl(final List<String> codes) throws IllegalArgumentException {
        if (codes != null) {
            String[] arr = new String[codes.size()];
            arr = codes.toArray(arr);
            this.init(arr);
        }
    }

    public TrendTimeGroupsImpl(final String[] codes) throws IllegalArgumentException {
        init(codes);
    }

    private void init(final String[] codes) throws IllegalArgumentException {
        if (codes.length > 0) {
            for (String code : codes) {
                if (code.length() != 6) {
                    throw new IllegalArgumentException("Unregognized (too long) time code in TREND: " + code);
                }
                if (code.startsWith("FM")) {
                    startHour = Integer.parseInt(code.substring(2, 4));
                    startMinute = Integer.parseInt(code.substring(4, 6));
                } else if (code.startsWith("TL")) {
                    endHour = Integer.parseInt(code.substring(2, 4));
                    endMinute = Integer.parseInt(code.substring(4, 6));
                } else if (code.startsWith("AT")) {
                    startHour = Integer.parseInt(code.substring(2, 4));
                    startMinute = Integer.parseInt(code.substring(4, 6));
                    isSingular = true;
                } else {
                    throw new IllegalArgumentException("Unregognized time code in TREND: " + code);
                }
            }
        }
    }
    */

    public int getFromHour() {
        return startHour;
    }

    public int getFromMinute() {
        return startMinute;
    }

    public int getToHour() {
        return endHour;
    }

    public int getToMinute() {
        return endMinute;
    }

    public boolean isSingleInstance() {
        return isSingular;
    }

    public void setFromHour(final int hour) {
        this.startHour = hour;
    }

    public void setFromMinute(final int minute) {
        this.startMinute = minute;
    }

    public void setToHour(final int hour) {
        this.endHour = hour;
    }

    public void setToMinute(final int minute) {
        this.endMinute = minute;
    }

    public void setSingleInstance(final boolean singular) {
        this.isSingular = singular;
    }

    public boolean hasStart() {
        return this.startHour >= 0 && this.startMinute >= 0;
    }

    public boolean hasEnd() {
        return this.endHour >= 0 && this.endMinute >= 0;
    }

}
