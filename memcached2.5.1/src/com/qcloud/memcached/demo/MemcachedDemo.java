/**
 * qcloud cmem java-memcached2.5.1-client demo
 * 使用方法:
 * 1. memcached2.5.1源码地址: http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/memcached-java-2.5.1.zip
 * 2. 下载源码并引入到项目中
 * 3. 参照demo代码实现相应功能
 */

package com.qcloud.memcached.demo;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import java.util.Date;

public class MemcachedDemo {
    public static void main(String[] args){

        //管理中心，点击“NoSQL高速存储”，在NoSQL高速存储“管理视图”，可以看到系统分配的IP:Port
        //需要在内网IP上访问, 不需要账号密码
        final String ip = "10.66.115.111";
        final String port = "9101";

        System.out.println("start test qcloud cmem - " + ip + ":" + port);

        MemCachedClient memcachedClient = null;

        try{
            //设置缓存服务器列表，当使用分布式缓存的时，可以指定多个缓存服务器
            String[] servers = {ip + ":" + port};

            //创建Socked连接池实例
            SockIOPool pool = SockIOPool.getInstance();
            pool.setServers(servers);//设置连接池可用的cache服务器列表
            pool.setFailover(true);//设置容错开关
            pool.setInitConn(10);//设置开始时每个cache服务器的可用连接数
            pool.setMinConn(5);//设置每个服务器最少可用连接数
            pool.setMaxConn(250);//设置每个服务器最大可用连接数
            pool.setMaintSleep(30);//设置连接池维护线程的睡眠时间
            pool.setNagle(false);//设置是否使用Nagle算法，因为我们的通讯数据量通常都比较大（相对TCP控制数据）而且要求响应及时，因此该值需要设置为false（默认是true）
            pool.setSocketTO(3000);//设置socket的读取等待超时值
            pool.setAliveCheck(true);//设置连接心跳监测开关
            pool.initialize();

            memcachedClient = new MemCachedClient();

            //将数据存入缓存
            memcachedClient.set("cmem", "qcloud cmem service");

            //将数据存入缓存,并设置失效时间
            Date date = new Date(1000);
            memcachedClient.set("test_expire", "test_expire_value", date);

            //获取缓存数据
            System.out.println("get: cmem = " + memcachedClient.get("cmem"));
            System.out.println("get: test_expire = " + memcachedClient.get("test_expire"));

            //向cmem存入多个数据
            for(int i = 0; i < 100; i++){
                String key = "key-" + i;
                String value = "value-" + i;
                memcachedClient.set(key, value);
            }

            System.out.println("set 操作完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (memcachedClient != null) {
            memcachedClient = null;
        }
    }
}

