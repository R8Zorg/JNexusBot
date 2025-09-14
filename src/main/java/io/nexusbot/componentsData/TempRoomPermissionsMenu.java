package io.nexusbot.componentsData;

public enum TempRoomPermissionsMenu {
    LOCK("room-lock"),
    UNLOCK("room-unlock"),
    REJECT_CONNECT("room-reject-connect"),
    CLEAR_CONNECT("room-permit-connect"),
    KICK("room-kick"),
    REJECT_STREAM("room-reject-stream"),
    CLEAR_STREAM("room-permit-stream"),
    GHOST("room-ghost"),
    UNGHOST("room-unghost"),
    PERMIT_VIEW_CHANNEL("room-whitelist-add"),
    CLEAR_VIEW_CHANNEL("room-whitelist-remove");

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
