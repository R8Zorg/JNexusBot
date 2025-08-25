package io.nexusbot.utils;

import io.nexusbot.componentsData.GlobalIds;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ModalUtil {
    public static Modal generateModal(String id, String title, String label,
            String placeholder, int minLength, int maxLength) {
        return Modal.create(id, title)
                .addActionRow(TextInput.create(GlobalIds.BASE_MODAL_TEXT_INPUT_ID.getValue(),
                        label, TextInputStyle.SHORT)
                        .setPlaceholder(placeholder)
                        .setMinLength(minLength)
                        .setMaxLength(maxLength)
                        .build())
                .build();
    }
}
