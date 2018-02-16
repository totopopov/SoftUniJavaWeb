package org.softuni.javache;

import org.softuni.javache.io.Reader;

import java.io.*;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConnectionHandler implements Runnable {
    private Socket clientSocket;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    private Map<String, RequestHandler> requestHandlers;

    private String chashedInputStrem;


    public ConnectionHandler(Socket clientSocket, Map<String, RequestHandler> requestHandlers) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
    }

    private void initializeConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.clientSocketInputStream = new BufferedInputStream(this.clientSocket.getInputStream());
            this.chashedInputStrem = Reader.readAllLines(this.clientSocketInputStream);

            this.clientSocketOutputStream = this.clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            this.processRequest();
            this.clientSocketInputStream.close();
            this.clientSocketOutputStream.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest() {
        for (String handler : requestHandlers.keySet()) {

            InputStream clonedStream = new ByteArrayInputStream(this.chashedInputStrem.getBytes());
            RequestHandler requestHandler = this.requestHandlers.get(handler);
            requestHandler.handleRequest(clonedStream, this.clientSocketOutputStream);
            if (requestHandler.hasIntercepted()) {

                break;
            }
        }
    }
}






