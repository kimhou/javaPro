package com.qcloud.memcache.test;
import net.spy.memcached.*;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tencent on 15/4/16.
 */
public class SpyMemcachedManager {

    public static void main(String[] args){
        String testTarget = (args.length > 0 && !args[0].isEmpty()) ? args[0] : "";
        String testMethod = (args.length > 1 && !args[1].isEmpty()) ? args[1] : "";
        String testUrl = (args.length > 2 && !args[2].isEmpty()) ? args[2] : "";
        System.out.println("start - " + (testTarget != null ? testTarget : "none args"));

        SpyMemcachedManager manager = new SpyMemcachedManager();
        if(testTarget.equals("ali")) {
            manager.testAli(args.length > 1 ? args[1] : null);
        }else{
            manager.testQcloud(testMethod, testUrl);
        }
    }

    public void testQcloud(String type, String url){
        log("info", "start test qcloud");
        final String host = "10.66.108.24";
        final String port = "9101";
        url = url.length() > 0 ? url : host + ":" + port;

        MemcachedClient cache = null;
        try {

            cache = new MemcachedClient(new DefaultConnectionFactory(), AddrUtil.getAddresses(url));

            log("info", "connected");

            //向OCS中存一个key为"ocs"的数据，便于后面验证读取数据
            OperationFuture future = cache.set("ocs", 1000, " Open Cache Service,  from www.qcloud.com");

            //执行get操作，从缓存中读数据,读取key为"ocs"的数据
            log("get操作", cache.get("ocs").toString());

            future.get(); //  确保之前(mc.set())操作已经结束
            log("info", "future get finished");

            if(type.equals("whileSet")){
                whileSet(cache);
            }else if(type.equals("whileGet")){
                whileGet(cache);
            }else if(type.equals("normal")){
                testNormal(cache, 20);
            }else if(type.equals("concurrency")){
                testConcurrency(cache, 50);
            }else if(type.equals("asyncGet")){
                testAsyncGet(cache, 50);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (cache != null) {
            cache.shutdown();
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
        log("info", "-----------------start test async set----------------");
        for(int i = 0; i < count; i++){
            long t = new Date().getTime();
            String key = "spymemcache-async-key-same";
            String value = "spymemcache-async-value-" + t;
            cache.set(key, 1000, value);
            log("set操作", key + "=" + value);
        }

        log("info", "----------------------------------------------------------");
        for(int i = 0; i < count; i++){
            String key = "spymemcache-async-key-same";
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
            for (int i = 0; i < count; i++) {
                String key = "spymemcache-asyncGet-key-" + i;
                Future f = cache.asyncGet(key);
                futrues[i] = f;
                log("futrue get操作", key + i + " asyncGet");
            }
        try {
            log("asyncGet实际操作", "spymemcache-asyncGet-key-" + (count - 1) + "=" + futrues[count - 1].get(3, TimeUnit.SECONDS));
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
     * @param tag
     * @param msg
     */
    public void whileSet(MemcachedClient cache){
        log("info", "-----------------start test while set----------------");
        while(true){
            String key = "spymemcache-whileSet-key";
            String value = "spymemcache-whileSet-value-" + new Date().getTime();
            cache.set(key, 1000, value);
            log("while setted", key + "=" + value);
        }
    }

    /**
     * 循环读一个key
     * @param tag
     * @param msg
     */

    public void whileGet(MemcachedClient cache){
        log("info", "-----------------start test while get----------------");
        while(true){
            String key = "spymemcache-whileSet-key";
            String value = cache.get(key).toString();
            log("while getted", key + "=" + value);
        }
    }
    public void log(String tag, String msg){
        System.out.println("["+new Date().getTime()+"] " + "[" + tag + "] " + msg);
    }

    public void testAli(String pwd){
        System.out.println("start test aliyun");
        final String host = "834103b87c6111e4.m.cnqdalicm9pub001.ocs.aliyuncs.com";
        final String port = "11211";
        final String uname = "834103b87c6111e4";

        if(pwd.isEmpty()){
            System.out.println("pwd is null");
            return;
        }

        MemcachedClient memcachedClient = null;
        try {
            AuthDescriptor authDescriptor = new AuthDescriptor(new String[]{"PLAIN"}, new PlainCallbackHandler(uname, pwd));
            ConnectionFactory connectionFactory = new ConnectionFactoryBuilder().setProtocol(ConnectionFactoryBuilder.Protocol.BINARY).setAuthDescriptor(authDescriptor).build();
            List<InetSocketAddress> list = AddrUtil.getAddresses(host + ":" + port);
            memcachedClient = new MemcachedClient(connectionFactory, list);

            System.out.println("OCS Sample Code");

            //向OCS中存一个key为"ocs"的数据，便于后面验证读取数据
            OperationFuture future = memcachedClient.set("ocs", 1000," Open Cache Service,  from www.Aliyun.com");

            //向OCS中存若干个数据，随后可以在OCS控制台监控上看到统计信息
            for(int i=0;i<10;i++){
                String key="key-"+i;
                String value="value-"+i + "-" + new Date();

                //执行set操作，向缓存中存数据
                memcachedClient.set(key, 1000, value);
            }


            System.out.println("Set操作完成!");

            future.get();  //  确保之前(mc.set())操作已经结束

            //执行get操作，从缓存中读数据,读取key为"ocs"的数据
            System.out.println("Get操作:"+memcachedClient.get("ocs"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (memcachedClient != null) {
            memcachedClient.shutdown();
        }
    }
}
