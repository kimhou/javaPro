import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by tencent on 15/4/16.
 */
public class SpyMemcachedManager {

    public static void main(String[] args){
        System.out.println("helloa");
        final String host = "834103b87c6111e4.m.cnqdalicm9pub001.ocs.aliyuncs.com";
        final String port = "11211";
        final String uname = "834103b87c6111e4";
        final String pwd = "hkmYXL123";

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
                String value="value-"+i;

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
