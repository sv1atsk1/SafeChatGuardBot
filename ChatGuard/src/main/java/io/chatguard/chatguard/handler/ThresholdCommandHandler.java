package io.chatguard.chatguard.handler;

import io.chatguard.chatguard.service.MessageService;
import io.chatguard.chatguard.service.ThresholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ThresholdCommandHandler {

    private final ThresholdService thresholdService;

    private final MessageService messageService;

    public void setThresholdCommand(Update update, String param) {
        Long chatId = update.getMessage().getChatId();
        try {
            double threshold = Double.parseDouble(param);
            thresholdService.setChatThreshold(chatId, threshold);
            messageService.sendMessage(chatId, "Порог установлен на: " + threshold);
        } catch (NumberFormatException e) {
            messageService.sendMessage(chatId, "Некорректное значение порога. Попробуйте снова.");
        }
    }
}
