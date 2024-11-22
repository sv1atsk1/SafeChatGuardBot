package io.chatguard.chatguard.manager;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class WaitingParametersManager {

    private final Map<Long, String> waitingForParams = new HashMap<>();

    public boolean hasPendingCommand(Long chatId) {
        return waitingForParams.containsKey(chatId);
    }

    public void addPendingCommand(Long chatId, String command) {
        waitingForParams.put(chatId, command);
    }

    public String removePendingCommand(Long chatId) {
        return waitingForParams.remove(chatId);
    }

    public String getPendingCommand(Long chatId) {
        return waitingForParams.get(chatId);
    }
}
