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
        System.out.println("start - " + ((args.length > 0 && !args[0].isEmpty()) ? args[0] : "none args"));
        SpyMemcachedManager manager = new SpyMemcachedManager();
        if(args.length > 0 && args[0].equals("ali")) {
            manager.testAli(args.length > 1 ? args[1] : null);
        }else{
            manager.testQcloud();
        }
    }

    public void testQcloud(){
        log("info", "start test qcloud");
        final String host = "10.66.108.24";
        final String port = "9101";

        MemcachedClient cache = null;
        try {

            cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(host + ":" + port));

            log("info", "connected");

            //向OCS中存一个key为"ocs"的数据，便于后面验证读取数据
            OperationFuture future = cache.set("ocs", 1000, " Open Cache Service,  from www.qcloud.com");

            //执行get操作，从缓存中读数据,读取key为"ocs"的数据
            log("get操作", cache.get("ocs").toString());

            future.get(); //  确保之前(mc.set())操作已经结束
            log("info", "future get finished");

            testNormal(cache, 20);
            testAsync(cache, 50);
            testAsyncGet(cache, 50);

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
            log("get操作", key +"=" + cache.get(key));
        }
    }

    /**
     * 测试并发set同一key
     * @param cache
     * @param count
     */
    public void testAsync(MemcachedClient cache, int count){
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
            log("get操作", key +"=" + cache.get(key));
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
            for (int i = 0; i < count; i++) {
                String key = "spymemcache-asyncGet-key-" + i;
                Future f = cache.asyncGet(key);
                try {
                    log("get操作",key + "=" + f.get(3, TimeUnit.SECONDS));
                }catch (Exception e){
                    e.printStackTrace();
                }
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
