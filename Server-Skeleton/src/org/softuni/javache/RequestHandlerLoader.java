package org.softuni.javache;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Todor Popov using Lenovo on 11.2.2018 г. at 17:24.
 */
public class RequestHandlerLoader {
    private static final String LIB = "lib";
    private static final String CLASS = ".class";
    private static final String JAR = ".jar";

    private HashMap<String, RequestHandler> requestHandlers;
    private HashSet<File> requestHandlersClassPaths;
    private HashSet<File> requestHandlersJarPaths;
    private String path;

    public RequestHandlerLoader() {
        this.requestHandlers = new HashMap<>();
        this.requestHandlersClassPaths = new HashSet<>();
        this.requestHandlersJarPaths = new HashSet<>();
        this.path = "";
    }

    public Map<String, RequestHandler> loadRequestHandlers() {

        String path = "";

        try {
            this.path = URLDecoder.decode(WebConstants.SERVER_ROOT_PATH, "UTF-8");
            System.out.println(String.format("Root Folder at: %s", this.path));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Path contains bad charachters: " + WebConstants.SERVER_ROOT_PATH);
            e.printStackTrace();
        }


        this.scann(this.path + LIB, this.requestHandlersClassPaths, CLASS);
        this.scann(this.path + LIB, this.requestHandlersJarPaths, JAR);
        this.loadHandlers();


        return Collections.unmodifiableMap(this.requestHandlers);

    }

    private void loadHandlers() {
        File libDirectory = new File(this.path);
        Set<URL> urlsSet = new HashSet<>();
        try {
            urlsSet.add(new File(this.path + LIB).toURI().toURL());
            for (File requestHandlersPath : this.requestHandlersClassPaths) {
                urlsSet.add(requestHandlersPath.getParentFile().toURI().toURL());
            }
            for (File requestHandlersJarPath : this.requestHandlersJarPaths) {
                String jarPath = (requestHandlersJarPath.toURI().toURL().toString());
                urlsSet.add(new URL(jarPath));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        URL[] urls = new URL[urlsSet.size()];
        urls = urlsSet.toArray(urls);

        URLClassLoader urlClassLoader = new URLClassLoader(urls);

        for (File requestHandlersJarPath : this.requestHandlersJarPaths) {

            try {

                JarFile library = new JarFile(requestHandlersJarPath);
                Enumeration<JarEntry> jarEntries = library.entries();

                while (jarEntries.hasMoreElements()) {
                    JarEntry currentJar = jarEntries.nextElement();
                    if (currentJar.isDirectory() || !currentJar.getName().endsWith(CLASS)) {
                        continue;
                    }

                    String clazzPathName = currentJar.getName().replace(CLASS, "")
                            .replace("/", ".");

                    loadSpecificClass(urlClassLoader, clazzPathName);
                }

            } catch (IOException
                    | InstantiationException
                    | ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }

        }


        for (File requestHandlersPath : requestHandlersClassPaths) {
            try {

                String clazzPath = requestHandlersPath.toString()
                        .replace(libDirectory + File.separator + LIB + File.separator, "")
                        .replace(".class", "")
                        .replace(File.separator, ".");
                loadSpecificClass(urlClassLoader, clazzPath);
            } catch (IllegalAccessException
                    | InstantiationException
                    | NoSuchMethodException
                    | InvocationTargetException
                    | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    private void loadSpecificClass(URLClassLoader urlClassLoader, String clazzPath)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

        Class<?> clazz = urlClassLoader.loadClass(clazzPath);
        if (RequestHandler.class.isAssignableFrom(clazz)) {
            RequestHandler requestHandler = (RequestHandler) clazz.
                    getDeclaredConstructor(String.class).newInstance(this.path);
            String clazzName = requestHandler.getClass().getSimpleName();
            this.requestHandlers.put(clazzName, requestHandler);
            System.out.println("Laoded Handler: " + clazzName);
        }
    }

    private void scann(String path, HashSet<File> requestHandlersPaths, String type) {
        File libDirectory = new File(path);

        if (libDirectory.exists()) {
            for (File file : libDirectory.listFiles()) {
                if (file.isDirectory()) {
                    this.scann(file.getPath(), requestHandlersPaths, type);
                } else {
                    if (file.toString().endsWith(type)) {
                        requestHandlersPaths.add(file);
                    }
                }
            }
        } else {
            System.out.println("Bad path  " + this.path + "! Directory does not exist!");
        }


    }

}
