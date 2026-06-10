package com.demoqa.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader — Reads values from config/config.properties.
 *
 * WHY THIS EXISTS:
 * Instead of writing "https://demoqa.com" directly in your test code,
 * you write ConfigReader.get("base.url") everywhere.
 * When the URL changes, you update ONE line in config.properties.
 * This is called "externalized configuration" — a senior pattern.
 *
 * HOW IT WORKS:
 * Java's built-in Properties class reads .properties files
 * as simple key=value pairs. We load the file once when the
 * class is first used (static block), then answer get() calls.
 */

public class ConfigReader {




        // Properties object holds all key=value pairs from the file
        private static final Properties properties = new Properties();

        // Static block runs ONCE when the class is first loaded
        // It opens the file and loads everything into memory
        static {
            try {
                FileInputStream file = new FileInputStream("config/config.properties");
                properties.load(file);
                file.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Could not load config.properties — make sure the file exists at config/config.properties",
                        e
                );
            }
        }

        /**
         * Get a value from config.properties by its key.
         */
        public static String get(String key) {
            String value = System.getProperty(key);
            if (value == null) {
                value = properties.getProperty(key);
            }
            if (value == null) {
                throw new RuntimeException(
                        "Key '" + key + "' not found in system properties or config.properties"
                );
            }
            return value.trim();
        }

        /**
         * Get a value as an integer.
         * Example: ConfigReader.getInt("implicit.wait") returns 10
         */
        public static int getInt(String key) {
            return Integer.parseInt(get(key));
        }

        /**
         * Get a value as a boolean.
         * Example: ConfigReader.getBoolean("headless") returns false
         */
        public static boolean getBoolean(String key) {
            return Boolean.parseBoolean(get(key));
        }
    }


