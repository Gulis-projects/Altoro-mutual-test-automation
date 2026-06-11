package com.demoqa.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader — Resolves configuration values in priority order:
 *   1. JVM system property  (-Dkey=value on the command line)
 *   2. .env file            (project root, gitignored)
 *   3. config.properties    (committed defaults / non-sensitive settings)
 *
 * Credentials belong in .env; everything else belongs in config.properties.
 * Copy .env.example → .env and fill in your values before running tests.
 */
public class ConfigReader {

    private static final Properties properties = new Properties();
    private static final Dotenv dotenv;

    static {
        // Load .env silently if missing (no .env in CI is fine — env vars are injected directly)
        dotenv = Dotenv.configure().ignoreIfMissing().load();

        try (FileInputStream file = new FileInputStream("config/config.properties")) {
            properties.load(file);
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not load config/config.properties — make sure the file exists", e);
        }
    }

    public static String get(String key) {
        // 1. JVM system property (e.g. -Dbrowser=firefox)
        String value = System.getProperty(key);
        if (value != null) return value.trim();

        // 2. .env file — convert dotted key to UPPER_SNAKE env-var name
        //    e.g. "app.username" → "APP_USERNAME"
        String envKey = key.toUpperCase().replace('.', '_');
        value = dotenv.get(envKey, null);
        if (value != null) return value.trim();

        // 3. config.properties fallback
        value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                "Key '" + key + "' not found in system properties, .env (" + envKey + "), or config.properties");
        }
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
