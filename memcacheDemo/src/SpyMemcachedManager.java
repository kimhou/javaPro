import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

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

        }catch (IOException e) {
            System.out.println("erro:" + e.getMessage());
        }catch (Exception e){
            System.out.println("erro:" + e.getMessage());
        }
    }

}
