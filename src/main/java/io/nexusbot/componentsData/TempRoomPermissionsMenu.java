package io.nexusbot.componentsData;

public enum TempRoomPermissionsMenu {
    LOCK("room-lock"),
    UNLOCK("room-unlock"),
    PERMIT("room-permit"),
    REJECT("room-reject"),
    KICK("room-kick"),
    GHOST("room-ghost"),
    UNGHOST("room-unghost"),
    ACCEPT("room-whitelist-add"),
    DENY("room-whitelist-remove");

    public static final String ID = "room-permissions";

    private final String value;

    TempRoomPermissionsMenu(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TempRoomPermissionsMenu fromValue(String value) {
        for (TempRoomPermissionsMenu menu : values()) {
            if (menu.value.equals(value)) {
                return menu;
            }
        }
        return null;
    }
}
