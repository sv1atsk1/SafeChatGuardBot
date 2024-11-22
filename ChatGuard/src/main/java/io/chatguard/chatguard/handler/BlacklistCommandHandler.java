package io.chatguard.chatguard.handler;

import io.chatguard.chatguard.service.BlacklistService;
import io.chatguard.chatguard.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BlacklistCommandHandler {

    private final BlacklistService blacklistService;

    private final MessageService messageService;

    private static final String USERNAME = "Пользователь ";

    public void addToBlacklistCommand(Update update, String username) {
        username = username.replaceFirst("@", "");
        Long chatId = update.getMessage().getChatId();
        if (blacklistService.isUserBlacklisted(username)) {
            messageService.sendMessage(chatId, USERNAME + username + " уже в черном списке.");
        } else {
            blacklistService.addToBlacklist(username);
            messageService.sendMessage(chatId, USERNAME + username + " добавлен в черный список.");
        }
    }

    public void removeFromBlacklistCommand(Update update, String username) {
        username = username.replaceFirst("@", "");
        Long chatId = update.getMessage().getChatId();
        if (blacklistService.isUserBlacklisted(username)) {
            blacklistService.removeFromBlacklist(username);
            messageService.sendMessage(chatId, USERNAME + username + " удален из черного списка.");
        } else {
            messageService.sendMessage(chatId, USERNAME + username + " отсутствует в черном списке.");
        }
    }
}
