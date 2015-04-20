package com.qcloud.memcached.test;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import java.util.Date;

/**
 * Created by tencent on 15/4/20.
 */
public class MemcachedTest {
    public static void main(String[] args){
        System.out.println("start - " + ((args.length > 0 && !args[0].isEmpty()) ? args[0] : "none args"));
        MemcachedTest test = new MemcachedTest();
        if(args.length > 0 && args[0].equals("ali")) {
            test.testAli(args.length > 1 ? args[1] : null);
        }else{
            test.testQcloud();
        }
    }
    public void testQcloud() {
        final String host = "10.66.108.24";
        final String port = "9101";
        String[] address = {host + ":" + port};

        System.out.println("start test qcloud - " + host + ":" + port);

        MemCachedClient memcachedClient = null;

        try{

            SockIOPool pool = SockIOPool.getInstance();
            pool.setServers(address);
            pool.setFailover(true);
            pool.setInitConn(10);
            pool.setMinConn(5);
            pool.setMaxConn(250);
            pool.setMaintSleep(30);
            pool.setNagle(false);
            pool.setSocketTO(3000);
            pool.setAliveCheck(true);
            pool.initialize();

            String key = "key-1";
            String value = "value-1" + "-" + new Date();

            memcachedClient = new MemCachedClient();

            System.out.println("get last key-1 isExist = " + memcachedClient.keyExists(key));
            System.out.println("get last key-1 = " + memcachedClient.get(key));

            memcachedClient.set(key, value);

            System.out.println("get just set key-1 = " + memcachedClient.get(key));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (memcachedClient != null) {
            memcachedClient = null;
        }
    }

    public void testAli(String pwd){
        System.out.println("start test aliyun");
        final String host = "834103b87c6111e4.m.cnqdalicm9pub001.ocs.aliyuncs.com";
        final String port = "11211";
        final String uname = "834103b87c6111e4";
    }
}

