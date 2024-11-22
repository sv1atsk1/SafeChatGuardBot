package io.chatguard.chatguard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final BotApiService botApiService;

    private final Set<String> deletedMessageIds = new HashSet<>();

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            botApiService.sendMessage(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: ", e);
        }
    }

     public  boolean deleteMessage(Message message) {
        String chatId = message.getChatId().toString();
        Integer messageId = message.getMessageId();
        String messageKey = chatId + "_" + messageId;

        if (deletedMessageIds.contains(messageKey)) {
            log.warn("Сообщение уже удалено: " + messageKey);
            return false;
        }

        try {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
            botApiService.deleteMessage(deleteMessage);
            deletedMessageIds.add(messageKey);
            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения: ", e);
            return false;
        }
    }

    public void sendHelpMessage(Long chatId) {
        String helpMessage = """
                Доступные команды:
                /set_threshold <значение> - Установить порог.
                /add_to_whitelist <username> - Добавить пользователя в белый список.
                /remove_from_whitelist <username> - Удалить пользователя из белого списка.
                /add_to_blacklist <username> - Добавить пользователя в черный список.
                /remove_from_blacklist <username> - Удалить пользователя из черного списка.
                /help - Показать это сообщение.
                """;

        sendMessage(chatId, helpMessage);
    }
}