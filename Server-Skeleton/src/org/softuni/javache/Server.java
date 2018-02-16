package org.softuni.javache;

import org.softuni.javache.http.HttpSessionStorage;
import org.softuni.javache.http.HttpSessionStorageImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.FutureTask;

public class Server {
    private static final String LISTENING_MESSAGE = "Listening on port: ";

    private static final String TIMEOUT_DETECTION_MESSAGE = "Timeout detected!";

    private static final Integer SOCKET_TIMEOUT_MILLISECONDS = 5000;


    private String rootPath;

    private int port;

    private Map<String,RequestHandler> requestHandlers;

    private int timeouts;

    private ServerSocket server;

    public Server(int port, Map<String,RequestHandler> requestHandlers) {
        this.port = port;
        this.timeouts = 0;
        this.requestHandlers = requestHandlers;
        this.getRootPath();
    }



    private void getRootPath() {
        try {
            this.rootPath = URLDecoder.decode(WebConstants.SERVER_ROOT_PATH, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("root path contains bad characters :" + WebConstants.SERVER_ROOT_PATH);
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        System.out.println(LISTENING_MESSAGE + this.port);

        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        HttpSessionStorage sessionStorage = new HttpSessionStorageImpl();

        while (true) {
            try (Socket clientSocket = this.server.accept()) {
                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

                ConnectionHandler connectionHandler
                        = new ConnectionHandler(clientSocket, this.requestHandlers);

                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException e) {
                System.out.println(String.format("%s  %s", TIMEOUT_DETECTION_MESSAGE, this.timeouts++));
            }
        }
    }
}