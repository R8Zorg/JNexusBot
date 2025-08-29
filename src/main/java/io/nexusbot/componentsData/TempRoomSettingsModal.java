package io.nexusbot.componentsData;

public enum TempRoomSettingsModal {
    STATUS(TempRoomSettingsMenu.STATUS.getValue() + GlobalIds.BASE_MODAL_ID.getValue()),
    LIMIT(TempRoomSettingsMenu.LIMIT.getValue() + GlobalIds.BASE_MODAL_ID.getValue()),
    BITRATE(TempRoomSettingsMenu.BITRATE.getValue() + GlobalIds.BASE_MODAL_ID.getValue()),
    NAME(TempRoomSettingsMenu.NAME.getValue() + GlobalIds.BASE_MODAL_ID.getValue());

    private final String value;

    TempRoomSettingsModal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
