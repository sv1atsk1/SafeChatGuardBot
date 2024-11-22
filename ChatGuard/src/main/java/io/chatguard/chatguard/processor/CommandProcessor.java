package io.chatguard.chatguard.processor;

import io.chatguard.chatguard.service.BlacklistService;
import io.chatguard.chatguard.service.MessageService;
import io.chatguard.chatguard.service.ThresholdService;
import org.telegram.telegrambots.meta.api.objects.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandProcessor {

    private final ThresholdService thresholdService;

    private final BlacklistService blacklistService;

    private final MessageService messageService;

    private final ParameterProcessor parameterProcessor;

    public void processCommand(Update update, Long chatId) {
        String username = update.getMessage().getFrom().getUserName();

        if (blacklistService.isUserBlacklisted(username)) {
            messageService.sendMessage(chatId, "Вы не можете использовать команды, так как находитесь в черном списке.");
            return;
        }

        String command = update.getMessage().getText().split(" ")[0];
        command = command.split("@")[0];

        switch (command) {
            case "/set_threshold":
                messageService.sendMessage(chatId, "Пожалуйста, введите значение порога.");
                parameterProcessor.setWaitingForParams(chatId, command);
                break;
            case "/remove_threshold":
                thresholdService.removeChatThreshold(chatId);
                messageService.sendMessage(chatId, "Порог удален.");
                break;
            case "/add_to_whitelist":
                messageService.sendMessage(chatId, "Введите имя пользователя для добавления в белый список.");
                parameterProcessor.setWaitingForParams(chatId, command);
                break;
            case "/remove_from_whitelist":
                messageService.sendMessage(chatId, "Введите имя пользователя для удаления из белого списка.");
                parameterProcessor.setWaitingForParams(chatId, command);
                break;
            case "/add_to_blacklist":
                messageService.sendMessage(chatId, "Введите имя пользователя для добавления в черный список.");
                parameterProcessor.setWaitingForParams(chatId, command);
                break;
            case "/remove_from_blacklist":
                messageService.sendMessage(chatId, "Введите имя пользователя для удаления из черного списка.");
                parameterProcessor.setWaitingForParams(chatId, command);
                break;
            case "/help":
                messageService.sendHelpMessage(chatId);
                break;
            default:
                messageService.sendMessage(chatId, "Неизвестная команда.");
                break;
        }
    }
}
