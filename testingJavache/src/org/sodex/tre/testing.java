package org.sodex.tre;

import org.softuni.javache.RequestHandler;
import org.softuni.javache.http.*;
import org.softuni.javache.io.Reader;
import org.softuni.javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Todor Popov using Lenovo on 11.2.2018 г. at 22:11.
 */
public class testing implements RequestHandler {
    private String rootPath;
    private boolean intercepted = false;


    public testing(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        try {
            String content = Reader.readAllLines(inputStream);
            HttpRequest httpRequest = new HttpRequestImpl(content);
            if (!httpRequest.getRequestUrl().equals("/bratislava")) {
                this.intercepted = false;
                return;
            }
            HttpResponse response = new HttpResponseImpl();
            response.setStatusCode(HttpStatus.OK);
            response.addHeader("Content-Type", "text/html");
            response.setContent(("<h1>Hellow world from Bratislava</h1><h2>" + this.rootPath + "</h2>").getBytes());
            Writer.writeBytes(response.getBytes(), outputStream);
            this.intercepted = true;
        } catch (IOException var5) {
            this.intercepted = false;
            var5.printStackTrace();
        }

    }

    @Override
    public boolean hasIntercepted() {
        return this.intercepted;
    }
}
