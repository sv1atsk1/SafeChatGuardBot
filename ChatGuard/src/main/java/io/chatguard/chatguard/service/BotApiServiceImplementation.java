package io.chatguard.chatguard.service;

import io.chatguard.chatguard.telegram.SafeChatGuardBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class BotApiServiceImplementation implements BotApiService {

    private final SafeChatGuardBot safeChatGuardBot;

    public BotApiServiceImplementation(@Lazy SafeChatGuardBot safeChatGuardBot) {
        this.safeChatGuardBot = safeChatGuardBot;
    }

    @Override
    public void sendMessage(SendMessage message) throws TelegramApiException {
        safeChatGuardBot.execute(message);
    }

    @Override
    public boolean deleteMessage(DeleteMessage deleteMessage) throws TelegramApiException {
        safeChatGuardBot.execute(deleteMessage);
        return true;
    }

    @Override
    public File getFile(GetFile getFile) throws TelegramApiException {
        return safeChatGuardBot.execute(getFile);
    }

    @Override
    public String getBotToken() {
        return safeChatGuardBot.getBotToken();
    }
}