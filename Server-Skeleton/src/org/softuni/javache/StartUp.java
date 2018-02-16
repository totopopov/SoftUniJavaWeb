package org.softuni.javache;

import org.softuni.javache.utils.ServerConfig;

import java.io.IOException;
import java.util.*;

public class StartUp {

    //TODO:  Read ME { : If the program is unzipped in a project where the path has spaces " ", there could be a problem
    // (or other special charachters )so please movi it in a good looking path;}
    //Tried my best to make it work on different platforms.


    public static void main(String[] args) {
        start(args);
    }

    private static void start(String[] args) {
        int port = WebConstants.DEFAULT_SERVER_PORT;

        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }


        RequestHandlerLoader requestHandlerLoader=new RequestHandlerLoader();
        Map<String,RequestHandler> requestHandlers = requestHandlerLoader.loadRequestHandlers();
        ServerConfig serverConfig=new ServerConfig();
        Map<String, RequestHandler> configHandlers = serverConfig.getConfigHandlers(requestHandlers);
        serverConfig.loadConfig();

        Server server = new Server(port,configHandlers);

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
