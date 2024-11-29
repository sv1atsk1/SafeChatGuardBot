package io.chatguard.chatguard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    private final MeterRegistry meterRegistry;

    private final OkHttpClient client = new OkHttpClient();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ThresholdService thresholdService;

    private final MessageService messageService;

    private static final String NSFW_REQUEST_URL = "http://your_ip:your_port/get_nsfw_decision/";

    private static final String IA_CLASSIFICATION_URL = "http://your_ip:your_port/get_ia_class/";

    public String sendPostRequest(String url, String jsonRequest) throws IOException {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            RequestBody body = RequestBody.create(
                    jsonRequest,
                    MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                meterRegistry.counter("api.requests.total", "url", url, "status", "success").increment();

                if (!response.isSuccessful()) {
                    log.error("POST request failed with code: {}", response.code());
                    throw new IOException("Unexpected code " + response);
                }

                if (response.body() == null) {
                    throw new IOException("Response body is null");
                }

                return response.body().string();
            }
        } catch (IOException e) {
            meterRegistry.counter("api.requests.total", "url", url, "status", "error").increment();
            throw e;
        } finally {
            timer.stop(meterRegistry.timer("api.requests.time", "url", url));
        }
    }

    public void sendNsfwDecisionRequest(String imagePath, Update update) {
        Timer.Sample timer = Timer.start(meterRegistry);

        Long chatId = (update.getMessage() != null)
                ? update.getMessage().getChatId()
                : update.getEditedMessage().getChatId();

        double threshold = thresholdService.getChatThreshold(chatId);
        String id = "-1";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost postRequest = new HttpPost(NSFW_REQUEST_URL);

            File file = new File(imagePath);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("threshold", new StringBody(String.valueOf(threshold), ContentType.TEXT_PLAIN));
            builder.addPart("id", new StringBody(id, ContentType.TEXT_PLAIN));
            builder.addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY));

            HttpEntity entity = builder.build();
            postRequest.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                meterRegistry.counter("api.nsfw.requests", "status", "success").increment();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);

                log.info("Ответ от сервера: " + responseBody);
                processNsfwResponse(responseBody, update, threshold);
            }

        } catch (IOException e) {
            meterRegistry.counter("api.nsfw.requests", "status", "error").increment();
            log.error("Ошибка при отправке запроса NSFW: ", e);
        } finally {
            timer.stop(meterRegistry.timer("api.nsfw.request.time"));
        }
    }

    private void processNsfwResponse(String responseBody, Update update, double chatThreshold) {
        try {
            JsonNode responseJson = objectMapper.readTree(responseBody);
            double nsfwType = responseJson.get("nsfw_type").asDouble();

            Message targetMessage =
                    (update.getMessage() != null) ? update.getMessage() : update.getEditedMessage();

            if (nsfwType > chatThreshold && messageService.deleteMessage(targetMessage)) {
                messageService.sendMessage(targetMessage.getChatId(), "Ваше изображение удалено, так как оно нарушает политику чата.");
            }
        } catch (IOException e) {
            log.error("Ошибка при обработке ответа NSFW: ", e);
        }
    }

    public void sendClassificationRequest(Long chatId, String messageText, double threshold, Update update) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String escapedMessageText = messageText.replace("\"", "\\\"");
        String jsonRequest = String.format(Locale.US, "{\"text\": \"%s\", \"threshold\": %.1f, \"id\": \"%s\"}", escapedMessageText, threshold, "-1");

        log.info("Sending JSON request: {}", jsonRequest);

        try {
            String response = sendPostRequest(IA_CLASSIFICATION_URL, jsonRequest);
            JsonNode jsonNode = objectMapper.readTree(response);
            meterRegistry.counter("api.classification.requests", "status", "success").increment();
            Message targetMessage =
                    (update.getMessage() != null) ? update.getMessage() : update.getEditedMessage();
            if (jsonNode.get("ia_type").asDouble() == 1) {
                messageService.deleteMessage(targetMessage);
                messageService.sendMessage(chatId, "Ваше сообщение удалено, так как оно нарушает правила.");
            }
            log.info(response);
        } catch (Exception e) {
            meterRegistry.counter("api.classification.requests", "status", "error").increment();
            log.error("Ошибка отправки POST-запроса для анализа текста: {}", e.getMessage());
            messageService.sendMessage(chatId, "Произошла ошибка при анализе текста.");
        } finally {
            timer.stop(meterRegistry.timer("api.classification.time"));
        }
    }
}
