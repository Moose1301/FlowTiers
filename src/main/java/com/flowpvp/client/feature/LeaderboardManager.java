package com.flowpvp.client.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeaderboardManager {

    public static class Entry {
        public String name;
        public int elo;
        public int position;

        public Entry(String name, int elo, int position) {
            this.name = name;
            this.elo = elo;
            this.position = position;
        }
    }

    // Raw API response structure
    private static class ApiEntry {
        int position;
        String uuid;
        String name;
        int elo;
    }

    private static final Gson GSON = new Gson();

    private static List<Entry> cached = new ArrayList<>();

    public static List<Entry> getCached() {
        return cached;
    }

    public static CompletableFuture<List<Entry>> fetch(String mode) {

    final String finalMode = mode.toUpperCase(); // ✅ FIX

    return CompletableFuture.supplyAsync(() -> {
        try {
            String urlStr = "https://flowpvp.gg/api/leaderboard/" + finalMode + "?page=1";
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());

            Type listType = new TypeToken<List<ApiEntry>>(){}.getType();
            List<ApiEntry> apiList = GSON.fromJson(reader, listType);

            List<Entry> result = new ArrayList<>();

            for (ApiEntry e : apiList) {
                result.add(new Entry(e.name, e.elo, e.position));
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    });
}

    public static void load(String mode) {
    MinecraftClient mc = MinecraftClient.getInstance();

    System.out.println("Loading leaderboard for: " + mode); // ✅ DEBUG

    fetch(mode).thenAcceptAsync(result -> {
        mc.execute(() -> {
            System.out.println("Received entries: " + result.size()); // ✅ DEBUG
            cached = result;
        });
    });
}
}