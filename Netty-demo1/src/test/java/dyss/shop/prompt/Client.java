package dyss.shop.prompt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author DYss东阳书生
 * @date 2024/7/19 13:50
 * @Description 描述
 */

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        sc.write(Charset.defaultCharset().encode("123456789abcdefghijkl"));
        System.in.read();
    }
}
