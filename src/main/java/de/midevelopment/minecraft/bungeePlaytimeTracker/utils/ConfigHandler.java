package de.midevelopment.minecraft.bungeePlaytimeTracker.utils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * The ConfigHandler class is responsible for managing configuration files.
 * It provides functionality for loading, saving, synchronizing, and managing configuration data.
 * This class ensures that configuration files are properly initialized, updated with default values,
 * and accessible from memory.
 */
public class ConfigHandler {

    /**
     * Represents the configuration file used for loading, saving,
     * and managing application settings.
     * This file is critical for persisting and synchronizing configuration data.
     */
    private final File configFile;

    /**
     * Represents the in-memory representation of the configuration file, used to
     * manage and interact with configuration data. This variable stores all configuration
     * values and is synchronized with the configuration file when loading or saving.
     * <p>
     * The configuration object is loaded using {@link #loadConfig()}, and any updates
     * to the configuration can be persisted back to the file using {@
     */
    private Configuration configuration;

    /**
     * The logger instance used to record messages and errors related to configuration handling.
     * It facilitates logging actions such as loading, saving, copying, and syncing configuration files,
     * as well as highlighting any issues encountered during these operations.
     */
    private final Logger logger;

    /**
     * Initializes a new instance of the ConfigHandler class, which manages a configuration file.
     *
     * @param resourceClass The class used to locate the default configuration resource.
     * @param pluginFolder  The directory where the configuration file will be stored.
     * @param configName    The name of the configuration file.
     * @param logger        The logger instance for logging messages and errors.
     */
    public ConfigHandler(Class<?> resourceClass, File pluginFolder, String configName, Logger logger) {
        this.logger = logger;

        // Create plugin directory if it does not exist
        if (!pluginFolder.exists() && !pluginFolder.mkdir()) {
            logger.severe("Could not create plugin directory: " + pluginFolder.getPath());
        }

        this.configFile = new File(pluginFolder, configName); // configName as file name

        try {
            // Default configuration file is loaded using the resource context of the class
            String resourcePath = configName; // File in the same path as the class
            if (!configFile.exists()) {
                copyDefaultConfig(resourceClass, resourcePath);
                logger.info("Default config copied from resources: " + configFile.getPath());
            }

            loadConfig();

            // Synchronize configuration with default file
            syncWithDefaultConfig(resourceClass, configName);

        } catch (IOException e) {
            logger.severe("Error initializing config file: " + e.getMessage());
        }
    }

    /**
     * Loads the configuration from the specified configuration file into memory.
     * If an error occurs during this process (e.g., the file cannot be read),
     * a log message is generated to indicate the issue.
     * <p>
     * This method retrieves the
     */
    public void loadConfig() {
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            logger.severe("Error loading config file: " + e.getMessage());
        }
    }

    /**
     * Saves the current configuration from memory to the configuration file.
     * <p>
     * This method uses a {@link ConfigurationProvider} to save the in-memory
     * configuration to the associated file. If an I/O error occurs during the
     * save operation, an error message will be logged.
     * <p>
     * Errors during save attempts
     */
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (IOException e) {
            logger.severe("Error saving config file: " + e.getMessage());
        }
    }

    /**
     * Copies the default configuration file from the specified resource path to the target file.
     * If the provided resourceClass is null or the resource cannot be found, appropriate exceptions will be thrown.
     *
     * @param resourceClass the class whose class loader will be used to locate the resource
     * @param resourcePath  the path to the default configuration file within the resource
     * @throws IOException if the resource cannot be found or
     */
    private void copyDefaultConfig(Class<?> resourceClass, String resourcePath) throws IOException {
        if (resourceClass == null) {
            throw new IllegalArgumentException("The specified class must not be null!");
        }

        InputStream resourceStream = resourceClass.getClassLoader().getResourceAsStream(resourcePath);

        if (resourceStream == null) {
            throw new IOException("Resource " + resourcePath + " not found! Class: " + resourceClass.getName());
        }

        try {
            Files.copy(resourceStream, configFile.toPath());
            logger.info("The default configuration was successfully copied to " + configFile.toPath());
        } catch (IOException e) {
            logger.severe("Error copying default config: " + e.getMessage());
            throw e;
        } finally {
            resourceStream.close();
        }
    }

    /**
     * Synchronizes the current configuration file with the default configuration defined in the specified resource.
     * Missing keys in the current configuration are added from the default configuration.
     *
     * @param resourceClass The class whose classloader is used to locate the default configuration resource.
     *                      Must not be null.
     * @param resourcePath  The path to the default configuration file
     */
    private void syncWithDefaultConfig(Class<?> resourceClass, String resourcePath) {
        try {
            if (resourceClass == null) {
                throw new IllegalArgumentException("The specified class must not be null!");
            }

            InputStream resourceStream = resourceClass.getClassLoader().getResourceAsStream(resourcePath);

            if (resourceStream == null) {
                throw new IOException("Resource " + resourcePath + " not found!");
            }

            Configuration defaultConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(resourceStream);

            // Synchronize default values with current configuration
            addMissingKeys(defaultConfig, configuration);

            // Save updated local configuration
            saveConfig();

        } catch (IOException e) {
            logger.severe("Error synchronizing with default config: " + e.getMessage());
        }
    }

    /**
     * Adds keys present in the default configuration to the local configuration
     * if they are missing.
     *
     * @param defaultConfig the configuration containing the default keys and values
     * @param localConfig
     **/
    private void addMissingKeys(Configuration defaultConfig, Configuration localConfig) {
        for (String key : defaultConfig.getKeys()) {
            // If key is missing in local config, add it
            if (!localConfig.contains(key)) {
                localConfig.set(key, defaultConfig.get(key));
                logger.info("Missing key added: " + key);
            }
        }
    }

    /**
     * Sets a configuration value for the specified path and saves the configuration file.
     *
     * @param path  the configuration path where the value will be set
     * @param value the value to set for the specified
     */
    public void set(String path, Object value) {
        configuration.set(path, value);
        saveConfig();
    }

    /**
     * Retrieves a value from the configuration associated with the provided path.
     * The returned value is cast to the specified generic type.
     *
     * @param <T>  The type to which the retrieved value should be cast.
     * @param path The path in the configuration from which the value will be retrieved.
     *             This path must be present in the configuration, otherwise an exception is thrown.
     * @return The value retrieved from the configuration at
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path) {
        if (configuration.contains(path)) {
            return (T) configuration.get(path);
        } else {
            throw new IllegalArgumentException("Path '" + path + "' does not exist in the config!");
        }
    }
}