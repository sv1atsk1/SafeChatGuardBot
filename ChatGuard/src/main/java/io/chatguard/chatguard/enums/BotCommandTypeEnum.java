package io.chatguard.chatguard.enums;

import lombok.Getter;

@Getter
public enum BotCommandTypeEnum {

    SET_THRESHOLD("/set_threshold", "Установить порог"),
    REMOVE_THRESHOLD("/remove_threshold", "Удалить порог"),
    ADD_TO_WHITELIST("/add_to_whitelist", "Добавить в белый список"),
    REMOVE_FROM_WHITELIST("/remove_from_whitelist", "Удалить из белого списка"),
    ADD_TO_BLACKLIST("/add_to_blacklist", "Добавить в черный список"),
    REMOVE_FROM_BLACKLIST("/remove_from_blacklist", "Удалить из черного списка"),
    HELP("/help", "Показать доступные команды");

    private final String command;

    private final String description;

    BotCommandTypeEnum(String command, String description) {
        this.command = command;
        this.description = description;
    }
}
