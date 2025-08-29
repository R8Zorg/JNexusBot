package io.nexusbot.componentsData;

public enum TempRoomPermissionsMenu {
    LOCK("room-lock"),
    UNLOCK("room-unlock"),
    REJECT_CONNECT("room-reject"),
    CLEAR_CONNECT("room-permit"),
    KICK("room-kick"),
    GHOST("room-ghost"),
    UNGHOST("room-unghost"),
    PERMIT_VIEW_CHANNEL("room-whitelist-add"),
    REJECT_VIEW_CHANNEL("room-whitelist-remove");

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
