package dyss.shop.demo1;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author DYss东阳书生
 * @date 2024/7/18 10:26
 * @Description 描述
 */

public class NioServerTest {
    @Test
    public void test_SimpleServer() throws Exception{
        //不断的循环过程，非常浪费CPU资源


        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
//        ssc.configureBlocking(false); // 非阻塞模式🍒
         // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
         // 3. 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
            System.out.println("The server: waiting client request");
            SocketChannel sc = ssc.accept(); // 非阻塞，线程还会继续运行，如果没有连接建立，但sc是null
            if (sc != null) {
                System.out.println("connect complete!!!");
                System.out.println("the SC is"+sc);
//                sc.configureBlocking(false);  // 非阻塞模式🍒
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // 5. 接收客户端发送的数据
                System.out.println("before read"+channel);
                int read = channel.read(buffer);// 非阻塞，线程仍然会继续运行，如果没有读到数据，read 返回 0
                if (read > 0) {
                    System.out.println("read some data:"+read);
                    buffer.flip();
//                    debugRead(buffer);
                    buffer.clear();
                    System.out.println("after read"+channel);
                }
            }
        }
    }

    @Test
    public void test_Multiplexing() throws Exception{
        /// 1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open(); //🍒
        ServerSocketChannel ssc = ServerSocketChannel.open(); //🍅
//        ssc.configureBlocking(false); //🍅
        SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT, null); //🍒
        // interestOps设置关注事件，此处 key 只关注 accept 事件
        ssc.bind(new InetSocketAddress(8080)); //🍅
        while (true) {
            // 3. select 方法, 没有事件发生，线程阻塞（不占用CPU），有事件，线程才会恢复运行
            selector.select(); //🍒
            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read //🍒
            while (iter.hasNext()) {
                SelectionKey key = iter.next();//🍒
                // 处理key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                iter.remove();//🍒🍒🍒
                if (key.isAcceptable()) { // 如果是 accept（可连接事件）
                    //获取发生事件的 channel
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel(); //🍒
                    SocketChannel sc = channel.accept(); //🍅
//                    sc.configureBlocking(false); //🍅
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null); //🍒
                } else if (key.isReadable()) { // 如果是 read
                    key.cancel(); //暂时先不处理
                }
            }
        }

    }
}
