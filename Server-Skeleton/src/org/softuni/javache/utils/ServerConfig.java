package org.softuni.javache.utils;

import org.softuni.javache.RequestHandler;
import org.softuni.javache.WebConstants;
import org.softuni.javache.io.Reader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Todor Popov using Lenovo on 13.2.2018 г. at 21:59.
 */
public class ServerConfig {

    private static final String CONFIG_FILE = "config.ini";


    private LinkedList<String> configHandlers;


    public ServerConfig() {
        this.configHandlers = new LinkedList<>();
        this.loadConfig();

    }

    public void loadConfig() {
        File configFile = new File((WebConstants.SERVER_ROOT_PATH + CONFIG_FILE));

        if (!configFile.exists() || configFile.isDirectory()) {
            System.out.println("Congif.ini not found");
        }

        try {
            String configContent = Reader.
                    readAllLines(new ByteArrayInputStream(
                            Files.readAllBytes(
                                    Paths.get(configFile.getPath()))));

            String[] handlersOrder = configContent.split(":\\s+")[1].split(",\\s+");
            this.configHandlers.addAll(Arrays.asList(handlersOrder));

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    public Map<String, RequestHandler> getConfigHandlers(Map<String, RequestHandler> requestHandlerLoader) {
        LinkedHashMap<String, RequestHandler> orderedHandlers = new LinkedHashMap<>();

        for (String configHandler : this.configHandlers) {
            if (requestHandlerLoader.containsKey(configHandler)) {
                System.out.println(String.format("%s %s","Configed",configHandler));
                orderedHandlers.put(configHandler, requestHandlerLoader.get(configHandler));
            }
        }
        return Collections.unmodifiableMap(orderedHandlers);
    }

}
