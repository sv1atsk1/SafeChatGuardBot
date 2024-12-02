package io.chatguard.chatguard.telegram;

import io.chatguard.chatguard.enums.BotCommandTypeEnum;
import io.chatguard.chatguard.processor.CommandProcessor;
import io.chatguard.chatguard.processor.ImageProcessor;
import io.chatguard.chatguard.processor.ParameterProcessor;
import io.chatguard.chatguard.processor.TextProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.*;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SafeChatGuardBot extends TelegramLongPollingBot  {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final ImageProcessor imageProcessor;

    private final CommandProcessor commandProcessor;

    private final ParameterProcessor parameterProcessor;

    private final TextProcessor textProcessor;

    @PostConstruct
    public void init() {
        setBotCommands();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void setBotCommands() {
        List<BotCommand> commands = new ArrayList<>();

        for (BotCommandTypeEnum commandType : BotCommandTypeEnum.values()) {
            commands.add(new BotCommand(commandType.getCommand(), commandType.getDescription()));
        }

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            log.error("Ошибка при установке команд бота: ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            processMessage(update.getMessage(), update);
        } else if (update.hasEditedMessage()) {
            processMessage(update.getEditedMessage(), update);
        }
    }

    private void processMessage(Message message, Update update) {
        String username = message.getFrom().getUserName();
        Long chatId = message.getChatId();

        if (textProcessor.isUserBlacklisted(username, update, chatId)) {
            return;
        }

        if (parameterProcessor.isWaitingForParams(chatId)) {
            parameterProcessor.processCommandParameter(update, chatId);
            return;
        }

        if (message.isCommand()) {
            commandProcessor.processCommand(update, chatId);
            return;
        }

        if (message.hasText()) {
            textProcessor.processTextMessage(update);
        }

        if (message.hasPhoto()) {
            processPhotoMessage(message, update);
        }
    }

    private void processPhotoMessage(org.telegram.telegrambots.meta.api.objects.Message message, Update update) {
        String caption = message.getCaption();
        if (caption != null && !caption.isEmpty()) {
            textProcessor.processTextMessage(update);
        }
        imageProcessor.processImageMessage(update);
    }
}