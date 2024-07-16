package dyss.shop.demo1.Aio.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Future;

/**
 * @author DYss东阳书生
 * @date 2024/7/15 22:49
 * @Description 描述
 */

public class AioClient {
    public static void main(String[] args) throws Exception{
        //提供异步非阻塞通道实例，静态构造方法
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        //指定连接的服务器地址，返回future，void即为没有返回数据
        Future<Void> future = channel.connect(new InetSocketAddress("116.198.198.135", 80));
        System.out.println("尝试对远程服务器192.168.1.116");
        //获得连接结果，null则为连接成功，否则抛出异常
        future.get();
        //读取通道的数据，放在指定缓冲区里面，利用回调函数对数据进行处理
        channel.read(ByteBuffer.allocate(1024),null,new AioClientHandler(channel, Charset.forName("GBK")));
        Thread.sleep(100000);
    }
}
