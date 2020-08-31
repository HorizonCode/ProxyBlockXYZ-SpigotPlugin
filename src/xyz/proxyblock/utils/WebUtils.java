package xyz.proxyblock.utils;

import xyz.proxyblock.utils.impl.WebReader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by HorizonCode at 30/08/2020
 *
 * @author HorizonCode
 * @since 30/08/2020
 **/
public class WebUtils {

    private ExecutorService pool;

    public WebUtils(int maxThreads) {
        pool = Executors.newFixedThreadPool(maxThreads);
    }

    /*
     * With this Method its possible to get a Response from
     * a URL without locking the current Main-Thread :)
     */
    public String getResponseFromURLAsync(String url) throws ExecutionException, InterruptedException {
        Future<String> factorialResult = pool.submit(new WebReader(url));
        return factorialResult.get();
    }

}
