package dyss.shop.demo1;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author DYss东阳书生
 * @date 2024/7/18 10:31
 * @Description 描述
 */

public class Client {
    public static void main(String[] args) throws Exception{
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        System.out.println("the client:waiting server connection");
    }
}
