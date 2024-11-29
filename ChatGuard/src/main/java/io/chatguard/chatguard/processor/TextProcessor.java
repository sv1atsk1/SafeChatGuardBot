package io.chatguard.chatguard.processor;

import io.chatguard.chatguard.enums.BotCommandTypeEnum;
import io.chatguard.chatguard.manager.WaitingParametersManager;
import io.chatguard.chatguard.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextProcessor {

    private final WaitingParametersManager waitingParametersManager;

    private final ThresholdService thresholdService;

    private final WhitelistService whitelistService;

    private final BlacklistService blacklistService;

    private final MessageService messageService;

    private final ApiService apiService;

    private static final String USERNAME = "Пользователь @";

    private static final String UNKNOWN_COMMAND_MSG = "Неизвестная команда.";

    public void processTextMessage(Update update) {
        Message message = messageService.getMessageFromUpdate(update);
        String messageText = message.getText();
        Long chatId = message.getChatId();
        String username = message.getFrom().getUserName();

        if (isUserBlacklisted(username, update, chatId) || whitelistService.isUserWhitelisted(username)) {
            return;
        }

        double threshold = thresholdService.getChatThreshold(chatId);
        log.info("Threshold for chat {}: {}", chatId, threshold);

        if (waitingParametersManager.hasPendingCommand(chatId)) {
            handleCommandParameter(chatId, messageText);
        } else {
            handleStandardMessage(chatId, messageText, threshold, update);
        }
    }

    private void handleCommandParameter(Long chatId, String messageText) {
        String pendingCommand = waitingParametersManager.getPendingCommand(chatId);

        switch (BotCommandTypeEnum.valueOf(pendingCommand)) {
            case SET_THRESHOLD:
                try {
                    double threshold = Double.parseDouble(messageText);
                    thresholdService.setChatThreshold(chatId, threshold);
                    messageService.sendMessage(chatId, "Порог установлен на: " + threshold);
                } catch (NumberFormatException e) {
                    messageService.sendMessage(chatId, "Некорректное значение порога. Пожалуйста, введите числовое значение.");
                }
                break;
            case ADD_TO_WHITELIST:
                String usernameForWhitelist = messageText.replaceFirst("@", "");
                whitelistService.addToWhitelist(usernameForWhitelist);
                messageService.sendMessage(chatId, USERNAME + usernameForWhitelist + " добавлен в белый список.");
                break;
            case REMOVE_FROM_WHITELIST:
                String usernameForRemoveWhitelist = messageText.replaceFirst("@", "");
                whitelistService.removeFromWhitelist(usernameForRemoveWhitelist);
                messageService.sendMessage(chatId, USERNAME + usernameForRemoveWhitelist + " удален из белого списка.");
                break;
            case ADD_TO_BLACKLIST:
                String usernameForBlacklist = messageText.replaceFirst("@", "");
                blacklistService.addToBlacklist(usernameForBlacklist);
                messageService.sendMessage(chatId, USERNAME + usernameForBlacklist + " добавлен в черный список.");
                break;
            case REMOVE_FROM_BLACKLIST:
                String usernameForRemoveBlacklist = messageText.replaceFirst("@", "");
                blacklistService.removeFromBlacklist(usernameForRemoveBlacklist);
                messageService.sendMessage(chatId, USERNAME + usernameForRemoveBlacklist + " удален из черного списка.");
                break;
            default:
                messageService.sendMessage(chatId, UNKNOWN_COMMAND_MSG);
                break;
        }
        waitingParametersManager.removePendingCommand(chatId);
    }

    private void handleStandardMessage(Long chatId, String messageText, double threshold, Update update) {
        if (messageText.startsWith("/")) {
            messageService.sendMessage(chatId, "Неизвестная команда. Используйте /help для получения списка команд.");
        } else {
            apiService.sendClassificationRequest(chatId, messageText, threshold, update);
        }
    }

    public boolean isUserBlacklisted(String username, Update update, Long chatId) {
        if (blacklistService.isUserBlacklisted(username)) {
            messageService.deleteMessage(update.getMessage());
            messageService.sendMessage(chatId, "Ваше сообщение удалено, так как вы находитесь в черном списке.");
            return true;
        }
        return false;
    }
}