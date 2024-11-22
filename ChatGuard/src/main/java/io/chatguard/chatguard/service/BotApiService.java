package io.chatguard.chatguard.service;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.File;

public interface BotApiService {

    void sendMessage(SendMessage message) throws TelegramApiException;

    boolean deleteMessage(DeleteMessage deleteMessage) throws TelegramApiException;

    File getFile(GetFile getFile) throws TelegramApiException;

    String getBotToken();
}
