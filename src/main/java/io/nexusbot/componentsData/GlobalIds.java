package io.nexusbot.componentsData;

public enum GlobalIds {
    NOTHING("nothing"),
    BASE_MODAL_ID("-modal"),
    BASE_MODAL_TEXT_INPUT_ID("text-input");

    private final String value;

    GlobalIds(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
