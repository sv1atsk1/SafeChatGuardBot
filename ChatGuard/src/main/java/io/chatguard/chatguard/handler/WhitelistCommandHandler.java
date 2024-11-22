package io.chatguard.chatguard.handler;

import io.chatguard.chatguard.service.MessageService;
import io.chatguard.chatguard.service.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class WhitelistCommandHandler {

    private final WhitelistService whitelistService;

    private final MessageService messageService;

    private static final String USERNAME = "Пользователь ";

    public void addToWhitelistCommand(Update update, String username) {
        username = username.replaceFirst("@", "");
        Long chatId = update.getMessage().getChatId();
        if (whitelistService.isUserWhitelisted(username)) {
            messageService.sendMessage(chatId, USERNAME + username + " уже в белом списке.");
        } else {
            whitelistService.addToWhitelist(username);
            messageService.sendMessage(chatId, USERNAME + username + " добавлен в белый список.");
        }
    }

    public void removeFromWhitelistCommand(Update update, String username) {
        username = username.replaceFirst("@", "");
        Long chatId = update.getMessage().getChatId();
        if (whitelistService.isUserWhitelisted(username)) {
            whitelistService.removeFromWhitelist(username);
            messageService.sendMessage(chatId, USERNAME + username + " удален из белого списка.");
        } else {
            messageService.sendMessage(chatId, USERNAME + username + " отсутствует в белом списке.");
        }
    }
}
