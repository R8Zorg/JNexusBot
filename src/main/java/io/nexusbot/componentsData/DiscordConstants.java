package io.nexusbot.componentsData;

public enum DiscordConstants {
    MAX_COMPONENTS(5),
    MAX_SELECT_MENU_ITEMS(25);

    private final int value;

    DiscordConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
