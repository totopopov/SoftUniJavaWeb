package org.softuni.javache;

public final class WebConstants {
    public static final Integer DEFAULT_SERVER_PORT = 8080;

    public static final String WEB_SERVER_HTTP_VERSION = "HTTP/1.1";

    public static final String SERVER_SESSION_TOKEN = "Javache";

    public static final String SERVER_ROOT_PATH = RequestHandlerLoader.class.getProtectionDomain()
            .getCodeSource().getLocation().getPath();


    private WebConstants() { }
}
