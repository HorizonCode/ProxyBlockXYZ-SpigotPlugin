package xyz.proxyblock.utils.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

/**
 * Created by HorizonCode at 30/08/2020
 *
 * @author HorizonCode
 * @since 30/08/2020
 **/
public class WebReader implements Callable<String> {

    private String target;

    public WebReader(String target) {
        this.target = target;
    }

    public String call() {
        try {
            URL website = new URL(target);
            URLConnection connection = website.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine = in.readLine();
            in.close();
            return inputLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}