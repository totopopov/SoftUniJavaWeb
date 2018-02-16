package solet;

import org.softuni.javache.http.HttpRequest;

import java.io.InputStream;

/**
 * Created by Todor Popov using Lenovo on 13.2.2018 г. at 23:14.
 */
public interface HttpSoletRequest extends HttpRequest {
    InputStream getRequestStream();

}
