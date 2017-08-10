package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URL;

class VersionCheck {
    private static final String currentVersion = "1.0.2";
    private static final String urlString = "https://api.github.com/repos/exusar/Minesweeper/releases/latest";

    static boolean checkForNewestVersion() throws IOException {
        URL url = new URL(urlString);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(url);
        return currentVersion.equals(json.get("tag_name").asText());
    }

    static String getCurrentVersion() {
        return currentVersion;
    }
}