package dyss.shop.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author DYss东阳书生
 * @date 2024/7/18 10:31
 * @Description 描述
 */

public class Client {
//    public static void main(String[] args) throws Exception{
//        SocketChannel sc = SocketChannel.open();
//        sc.connect(new InetSocketAddress("localhost",8080));
//        System.out.println("the client:waiting server connection");
//    }

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        // 3. 接收数据
        int count = 0;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            count += sc.read(buffer);
            System.out.println(count);
            buffer.clear();
        }
    }
}
