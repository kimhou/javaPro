package com.qcloud.memcache.test;
import net.spy.memcached.*;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tencent on 15/4/16.
 */
public class SpyMemcachedManager {

    public static void main(String[] args){
        String testMethod = (args.length > 1 && !args[0].isEmpty()) ? args[0] : "";
        String testUrl = (args.length > 1 && !args[1].isEmpty()) ? args[1] : "";
        int count = (args.length > 2 && !args[2].isEmpty()) ? Integer.parseInt(args[2].toString()) : 10;
        String connectionType = (args.length > 3 && !args[3].isEmpty()) ? args[3] : "";

        System.out.println("start test - method = " + testMethod + ", testUrl = " + testUrl + ", testCount = " + count);

        if(testUrl.length() >= 0) {
            SpyMemcachedManager manager = new SpyMemcachedManager();
            manager.testQcloud(testMethod, testUrl, count, connectionType);
        }else{
            System.out.println("缺少url");
        }
    }

    public void testQcloud(String type, String url, int count, String connectionType){
        log("info", "start test qcloud");
        MemcachedClient cache = null;
        try {
            DefaultConnectionFactory f;
            if(connectionType.equals("ascii")){
                f = new DefaultConnectionFactory();
            }else{
                f = new BinaryConnectionFactory();
            }

            cache = new MemcachedClient(f, AddrUtil.getAddresses(url));

            log("info", "connected");

            if(type.equals("whileSet")){
                whileSet(cache, count);
            }else if(type.equals("whileGet")){
                whileGet(cache, count);
            }else if(type.equals("normal")){
                testNormal(cache, count);
            }else if(type.equals("concurrency")){
                testConcurrency(cache, count);
            }else if(type.equals("asyncGet")){
                testAsyncGet(cache, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cache != null) {
            cache.shutdown();
        }
    }

    public void onlySet(MemcachedClient cache, int count){
        log("info", "-----------------start test only set----------------");
        for (int i = 0; i < count; i++) {
            long t = new Date().getTime();
            String key = "spymemcache-only-set-key-" + i;
            String value = "spymemcache-only-set-vlaue-" + i + "-" + t;

            //执行set操作，向缓存中存数据
            cache.set(key, 1000, value);
            log("set操作", key + "=" + value);
        }
    }

    /**
     * 测试正常情况
     * @param cache
     * @param count
     */
    public void testNormal(MemcachedClient cache, int count){
        log("info", "-----------------start test normal----------------");
        for (int i = 0; i < count; i++) {
            long t = new Date().getTime();
            String key = "spymemcache-normal-key-" + i;
            String value = "value-" + i + "-" + t;

            //执行set操作，向缓存中存数据
            cache.set(key, 1000, value);
            log("set操作", key + "=" + value);
        }

        log("info", "----------------------------------------------------------");
        for(int i = 0; i < count; i++) {
            String key = "spymemcache-normal-key-" + i;
            log("get操作", key + "=" + cache.get(key));
        }
    }

    /**
     * 测试并发set同一key
     * @param cache
     * @param count
     */
    public void testConcurrency(MemcachedClient cache, int count){
        log("info", "-----------------start test concurrency ----------------");
        for(int i = 0; i < count; i++){
            long t = new Date().getTime();
            String key = "spymemcache-concurrency-key-same";
            String value = "spymemcache-concurrency-value-" + t;
            cache.set(key, 1000, value);
            log("set操作", key + "=" + value);
        }

        log("info", "----------------------------------------------------------");
        for(int i = 0; i < count; i++){
            String key = "spymemcache-concurrency-key-same";
            log("get操作", key + "=" + cache.get(key));
        }
    }

    /**
     * 测试并发asyncGet接口
     * @param cache
     * @param count
     */
    public void testAsyncGet(MemcachedClient cache, int count){
        log("info", "-----------------start test async get----------------");

        for (int i = 0; i < count; i++) {
            long t = new Date().getTime();
            String key = "spymemcache-asyncGet-key-" + i;
            String value = "value-" + i + "-" + t;

            //执行set操作，向缓存中存数据
            cache.set(key, 1000, value);
            log("set操作", key + "=" + value);
        }
        log("info", "----------------------------------------------------------");
        Future[] futrues = new Future[count];
        String[] keys = new String[count];
            for (int i = 0; i < count; i++) {
                keys[i] = "spymemcache-asyncGet-key-" + i;
                Future f = cache.asyncGet(keys[i]);
                futrues[i] = f;
                log("future asyncGet操作", keys[i]);
            }
        try {
            log("future.get()实际操作", keys[count - 1] + "=" + futrues[count - 1].get(3, TimeUnit.SECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }
         try{
             Thread.sleep(3000);
         }catch (InterruptedException e){
             e.printStackTrace();
         }
    }

    /**
     * 循环写一个key
     */
    public void whileSet(MemcachedClient cache, int count){
        log("info", "-----------------start test while set----------------");
        int i = 0;
        while(i++ < count){
            String key = "key-" + i;
            String value = "value-" + i + "-" + new Date().getTime();
            cache.set(key, 1000, value);
            log("while setted", key + "=" + value);
        }
    }

    /**
     * 循环读一个key
     */
    public void whileGet(MemcachedClient cache, int count){
        log("info", "-----------------start test while get----------------");
        int i = 0;
        while(i++ < count){
            String key = "key-" + i;
            String value = cache.get(key).toString();
            log("while getted", key + "=" + value);
        }
    }
    public void log(String tag, String msg){
        System.out.println("["+new Date().getTime()+"] " + "[" + tag + "] " + msg);
    }
}
