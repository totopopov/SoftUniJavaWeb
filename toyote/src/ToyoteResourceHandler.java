import org.softuni.javache.RequestHandler;
import org.softuni.javache.http.*;
import org.softuni.javache.io.Reader;
import org.softuni.javache.io.Writer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Todor Popov using Lenovo on 13.2.2018 г. at 20:40.
 */
public class ToyoteResourceHandler implements RequestHandler {

    private boolean intercepted;

    private final String SERVER_ROOT_PATH;

    public ToyoteResourceHandler(String server_root_path) {
        SERVER_ROOT_PATH = server_root_path;
        this.intercepted = false;
    }

    private boolean retrieveSource(HttpRequest httpRequest, HttpResponse response) throws IOException {
        String resourceURL = httpRequest.getRequestUrl();
        File file = new File(SERVER_ROOT_PATH + "static" + resourceURL);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        Path resourcePath = Paths.get(file.getPath());

        byte[] source = Files.readAllBytes(resourcePath);
        String contentType = Files.probeContentType(resourcePath);

        response.setStatusCode(HttpStatus.OK);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", source.length + "");
        response.addHeader("Content-Disposition", "inline");
        response.setContent(source);
        return true;
    }


    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {

        try {
            String requestContent = Reader.readAllLines(inputStream);
            HttpRequest httpRequest = new HttpRequestImpl(requestContent);
            HttpResponse response = new HttpResponseImpl();
            boolean foundFile = this.retrieveSource(httpRequest, response);
            if (!foundFile) {
                return;
            }
            Writer.writeBytes(response.getBytes(), outputStream);
            this.intercepted = true;
        } catch (IOException e) {
            this.intercepted = false;
            e.printStackTrace();
        }


    }


    @Override
    public boolean hasIntercepted() {
        return false;
    }
}
