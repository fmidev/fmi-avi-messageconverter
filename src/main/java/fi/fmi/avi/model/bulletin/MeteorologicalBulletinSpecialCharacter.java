package fi.fmi.avi.model.bulletin;

public enum MeteorologicalBulletinSpecialCharacter {

        START_OF_HEADING('\u0001'),
        START_OF_TEXT('\u0002'),
        END_OF_TEXT('\u0003'),
        END_OF_TRANSMISSION('\u0004'),
        ACKNOWLEDGE('\u0006'),
        HORIZONTAL_TAB('\u0009'),
        LINE_FEED('\n'),
        VERTICAL_TAB('\u000B'),
        FORM_FEED('\u000C'),
        CARRIAGE_RETURN('\r'),
        DATA_LINK_ESCAPE('\u0010'),
        DEVICE_LINK_CONTROL_1('\u0011'),
        DEVICE_LINK_CONTROL_2('\u0012'),
        NEGATIVE_ACKNOWLEDGE('\u0015'),
        SYNCHRONOUS_IDLE('\u0016'),
        END_OF_TRANSMISSION_BLOCK('\u0017'),
        ESCAPE('\u001B'),
        FILE_SEPARATOR('\u001C'),
        GROUP_SEPARATOR('\u001D'),
        RECORD_SEPARATOR('\u001E'),
        DELETE('\u007F'),
        SPACE('\u0020');

        private final char content;

        MeteorologicalBulletinSpecialCharacter(final char content) {
            this.content = content;
        }

        public static MeteorologicalBulletinSpecialCharacter fromChar(final char c) {
            for (final MeteorologicalBulletinSpecialCharacter m : MeteorologicalBulletinSpecialCharacter.values()) {
                if (m.getContent().equals(Character.toString(c))) {
                    return m;
                }
            }
            return null;
        }

        public String getContent() {
            return String.valueOf(this.content);
        }

}
