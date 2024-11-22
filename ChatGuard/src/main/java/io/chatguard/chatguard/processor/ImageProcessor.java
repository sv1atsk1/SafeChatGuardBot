package io.chatguard.chatguard.processor;

import io.chatguard.chatguard.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessor {

    private final BotApiService botApiService;

    private final WhitelistService whitelistService;

    private final FileDownloadService fileDownloadService;

    private final TextProcessor textProcessor;

    private final ApiService apiService;

    public void processImageMessage(Update update) {
        String username = update.getMessage().getFrom().getUserName();
        Long chatId = update.getMessage().getChatId();

        if (textProcessor.isUserBlacklisted(username, update, chatId)) {
            return;
        }

        if (whitelistService.isUserWhitelisted(username)) {
            return;
        }

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize largestPhoto = photos.get(photos.size() - 1);

            try {
                GetFile getFile = new GetFile(largestPhoto.getFileId());
                File file = botApiService.getFile(getFile);
                String fileUrl = "https://api.telegram.org/file/bot" + botApiService.getBotToken() + "/" + file.getFilePath();
                String localPath = fileDownloadService.saveFileFromURL(fileUrl);
                apiService.sendNsfwDecisionRequest(localPath, update);
            } catch (TelegramApiException | IOException | URISyntaxException e) {
                log.error("Ошибка при обработке фото: ", e);
            }
        }
    }
}
