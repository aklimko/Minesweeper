package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class VersionCheck {
    private static final String currentVersion = "1.0";
    private static final String urlString = "https://api.github.com/repos/exusar/Minesweeper/releases/latest";

    public static boolean checkForNewestVersion() throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "UTF-8");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(readData(inputStream));
        json = json.get("tag_name");
        return currentVersion.equals(json.asText());
    }

    private static String readData(InputStream inputStream) {
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
    }

    public static String getCurrentVersion() {
        return currentVersion;
    }

    public static void main(String[] args) throws IOException {
        checkForNewestVersion();
    }
}
