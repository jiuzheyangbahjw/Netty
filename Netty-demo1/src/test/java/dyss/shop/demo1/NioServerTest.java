package dyss.shop.demo1;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
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

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                };
            }
        }
        //如果 source 中没有获取到 `/n`，则说明没有执行读取数据，
        //此时，position=16，limit=16
        source.compact(); //🍒 让position变成剩余未读字节数
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
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, buffer); //🍒
                } else if (key.isReadable()) { // 如果是 read
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
//                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read==-1) {
                            key.cancel();
                        }else{
                            //分离特殊字符\n
                            split(buffer);
                            //判断是否满
                            if (buffer.position()==buffer.limit()){
                                //关联新Buffer，扩容后改变关联
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                newBuffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
//                            System.out.println();
                        }
                    } catch (Exception e) {
                        key.cancel();
                       e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * 向客户端写入数据
     * @throws IOException
     */
    @Test
    public void test_writing() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式
        ssc.register(selector,SelectionKey.OP_ACCEPT,null);
        ssc.bind(new InetSocketAddress(8080));
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);//非阻塞
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null);
                    //构建内容
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 10000000; i++) {
                        stringBuilder.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(stringBuilder.toString());
                    //先写
                    int write = sc.write(buffer);
                    System.out.println("第一次写入的数量是"+write);

                    if (buffer.hasRemaining()){
                        scKey.interestOps(SelectionKey.OP_READ+SelectionKey.OP_WRITE);
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int write = channel.write(buffer);
                    System.out.println("继续写入"+write);
                    if (!buffer.hasRemaining()){
                        //写完就释放
                        key.attach(null);
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                    }
                }
//                    while (buffer.hasRemaining()){
//                        int write = sc.write(buffer);//实际写入的字符数
//                        System.out.println("write = " + write);
//                    }


            }
        }
    }
}
