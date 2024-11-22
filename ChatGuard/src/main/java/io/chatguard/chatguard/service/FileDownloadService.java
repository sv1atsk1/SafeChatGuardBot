package io.chatguard.chatguard.service;

import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileDownloadService {

    public String saveFileFromURL(String fileUrl) throws IOException, URISyntaxException {
        URL url = new URI(fileUrl).toURL();

        String resourcesDir = "src/main/resources";
        String picturesDirPath = resourcesDir + File.separator + "pictures";

        File picturesDir = new File(picturesDirPath);
        if (!picturesDir.exists() && !picturesDir.mkdirs()) {
            throw new IOException("Не удалось создать директорию: " + picturesDirPath);
        }

        String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1).replace(":", "_");
        String localPath = picturesDirPath + File.separator + fileName;

        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(localPath), StandardCopyOption.REPLACE_EXISTING);
        }

        return localPath;
    }
}
