package solet;

import org.softuni.javache.http.HttpResponse;

import java.io.OutputStream;

/**
 * Created by Todor Popov using Lenovo on 13.2.2018 г. at 23:15.
 */
public interface HttpSoletResponse extends HttpResponse {
    OutputStream getResponseStream();
}
