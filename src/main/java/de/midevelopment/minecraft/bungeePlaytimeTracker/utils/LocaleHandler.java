package de.midevelopment.minecraft.bungeePlaytimeTracker.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LocaleHandler {

    /**
     * Represents the base locale used for fallback translations.
     * When a requested locale does not provide a specific translation for a resource key,
     * translations from this base locale are used as a default.
     * In this implementation, the base locale is set to English ("en").
     */
    private static final String BASE_LOCALE = "en";
    /**
     * The constant directory path where locale files are stored.
     * This path is used to construct the full resource path for loading locale files.
     */
    private static final String LOCALE_DIR = "/locale/";
    /**
     * The file suffix used to identify locale resource files.
     * In this implementation, the suffix is fixed to ".json",
     * indicating that the locale files are stored in JSON format.
     */
    private static final String FILE_SUFFIX = ".json";
    /**
     * A static instance of the Gson library used for JSON serialization and deserialization.
     * It facilitates converting Java objects to JSON representation and vice versa.
     */
    private static final Gson GSON = new Gson();
    /**
     * Represents the specific {@code Type} for a {@code Map<String, String>} used in JSON deserialization.
     * This type is utilized within the class to parse locale files into a map of key-value pairs where both
     * the keys and values are strings, typically representing translation keys and their corresponding translations.
     */
    private static final Type LOCALE_MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    /**
     * A thread-safe map used to store locale-based translations.
     * The keys represent translation keys, while the values represent their corresponding
     * translations in the currently loaded locale.
     *
     * This map is populated and updated dynamically when a locale is loaded using the
     * {@link LocaleHandler#loadLocale(String)} method. It contains fallback translations from the
     * base locale and, if applicable, localized translations from the selected locale file.
     *
     * The {@link LocaleHandler#get(String)} method allows retrieval of translations
     * from this map. In the case where a translation key is not found in the map,
     * the key itself will be returned as a fallback.
     */
    private static final Map<String, String> translations = new ConcurrentHashMap<>();

    /**
     * Loads the specified locale into the application's translation map.
     * The method first loads the base locale and then applies translations
     * from the specified locale if it differs from the base locale.
     *
     * @param localeKey the key representing the desired locale. It must not be null
     *                  and is processed as a lowercase, trimmed string.
     * @throws NullPointerException if the localeKey is null.
     * @throws RuntimeException if a failure occurs while loading or merging the locale file.
     */
    public static synchronized void loadLocale(String localeKey) {
        Objects.requireNonNull(localeKey, "localeKey must not be null");
        final String normalizedLocale = localeKey.trim().toLowerCase(Locale.ROOT);

        try {
            translations.clear();
            mergeLocaleFromResource(resourcePathFor(BASE_LOCALE), false, BASE_LOCALE);

            if (!normalizedLocale.equals(BASE_LOCALE)) {
                mergeLocaleFromResource(resourcePathFor(normalizedLocale), true, normalizedLocale);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locale: " + localeKey, e);
        }
    }

    /**
     * Retrieves the translation associated with the specified key. If the key does not have a translation,
     * the key itself is returned as the default value.
     *
     * @param key the translation key, must not be null
     * @return the translated value if the key exists, otherwise the key itself
     * @throws NullPointerException if the provided key is null
     */
    public static String get(String key) {
        Objects.requireNonNull(key, "translation key must not be null");
        return translations.getOrDefault(key, key);
    }

    // --- Helper ---

    /**
     * Merges locale-specific translations from a specified resource file into the existing translations map.
     *
     * @param resourcePath the path of the resource file containing locale translations
     * @param required specifies whether the resource file is mandatory; if true and the resource is missing,
     *                 a RuntimeException will be thrown
     * @param localeForMessage the name of the locale being processed, used in error messaging
     */
    private static void mergeLocaleFromResource(String resourcePath, boolean required, String localeForMessage) {
        try (InputStream inputStream = openResourceStream(resourcePath)) {
            if (inputStream == null) {
                if (required) {
                    throw new RuntimeException("Locale file not found: " + localeForMessage);
                }
                return;
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Map<String, String> loaded = GSON.fromJson(reader, LOCALE_MAP_TYPE);
                if (loaded != null) {
                    translations.putAll(loaded);
                }
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Error reading locale file for: " + localeForMessage, e);
        }
    }

    /**
     * Opens a resource as an InputStream given the resource path.
     *
     * @param resourcePath the path of the resource to be opened, relative to the classpath
     * @return an InputStream to read the resource, or null if the resource cannot be found
     */
    private static InputStream openResourceStream(String resourcePath) {
        return LocaleHandler.class.getResourceAsStream(resourcePath);
    }

    /**
     * Constructs the resource path for a given locale.
     *
     * @param locale the locale identifier (e.g., "en", "fr") used to determine the resource path
     * @return the complete path to the locale resource file
     */
    private static String resourcePathFor(String locale) {
        return LOCALE_DIR + locale + FILE_SUFFIX;
    }
}