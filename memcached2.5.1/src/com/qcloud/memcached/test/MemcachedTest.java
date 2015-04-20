package com.qcloud.memcached.test;

import com.danga.MemCached.MemCachedClient;

import java.io.IOException;
import java.lang.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by tencent on 15/4/20.
 */
public class MemcachedTest {
    public static void main(String[] args){

    }
    public void testQcloud() {
        final String host = "10.66.108.24";
        final String port = "9101";

        System.out.println("start test qcloud - " + host + ":" + port);

        MemCachedClient memcachedClient = null;

        try{
            memcachedClient = new MemCachedClient(host + ":" + port);

            String key = "key-1";
            String value = "value-1" + "-" + new Date();

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

