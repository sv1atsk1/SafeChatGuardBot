package io.chatguard.chatguard.processor;

import io.chatguard.chatguard.handler.BlacklistCommandHandler;
import io.chatguard.chatguard.handler.ThresholdCommandHandler;
import io.chatguard.chatguard.handler.WhitelistCommandHandler;
import io.chatguard.chatguard.manager.WaitingParametersManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import io.chatguard.chatguard.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterProcessor {

    private final ThresholdCommandHandler thresholdCommandHandler;

    private final WhitelistCommandHandler whitelistCommandHandler;

    private final BlacklistCommandHandler blacklistCommandHandler;

    private final MessageService messageService;

    private final WaitingParametersManager waitingParametersManager;

    public boolean isWaitingForParams(Long chatId) {
        return waitingParametersManager.hasPendingCommand(chatId);
    }

    public void setWaitingForParams(Long chatId, String command) {
        waitingParametersManager.addPendingCommand(chatId, command);
    }

    public void processCommandParameter(Update update, Long chatId) {
        String command = waitingParametersManager.removePendingCommand(chatId);
        String param = update.getMessage().getText();

        switch (command) {
            case "/set_threshold":
                thresholdCommandHandler.setThresholdCommand(update, param);
                break;
            case "/add_to_whitelist":
                whitelistCommandHandler.addToWhitelistCommand(update, param);
                break;
            case "/remove_from_whitelist":
                whitelistCommandHandler.removeFromWhitelistCommand(update, param);
                break;
            case "/add_to_blacklist":
                blacklistCommandHandler.addToBlacklistCommand(update, param);
                break;
            case "/remove_from_blacklist":
                blacklistCommandHandler.removeFromBlacklistCommand(update, param);
                break;
            default:
                messageService.sendMessage(chatId, "Неизвестная команда.");
                break;
        }
    }
}
