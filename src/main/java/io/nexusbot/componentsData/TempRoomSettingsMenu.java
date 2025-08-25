package io.nexusbot.componentsData;

public enum TempRoomSettingsMenu {
    STATUS("room-status"),
    LIMIT("room-limit"),
    BITRATE("room-bitrate"),
    NSFW("room-nsfw"),
    CLAIM("room-claim"),
    NAME("room-name");

    public static final String ID = "room-settings";

    private final String value;

    TempRoomSettingsMenu(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TempRoomSettingsMenu fromValue(String value) {
        for (TempRoomSettingsMenu menu : values()) {
            if (menu.value.equals(value)) {
                return menu;
            }
        }
        return null;
    }
}
