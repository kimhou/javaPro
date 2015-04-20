package com.qcloud.memcached.test;

import com.danga.MemCached.MemCachedClient;

import java.io.IOException;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.ExecutionException;

/**
 * Created by tencent on 15/4/20.
 */
public class MemcachedTest {
    public static void main(String[] args){

    }
    public void testQcloud() {
        System.out.println("start test qcloud");
        final String host = "10.66.108.24";
        final String port = "11211";
        MemCachedClient memcachedClient = null;

        try{
            memcachedClient = new MemCachedClient(host + ":" + port);

        }catch (IOException e) {
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

    public void testAli(){
        System.out.println("start test aliyun");
        final String host = "834103b87c6111e4.m.cnqdalicm9pub001.ocs.aliyuncs.com";
        final String port = "11211";
        final String uname = "834103b87c6111e4";
        final String pwd = "hkmYXL123";
    }
}

