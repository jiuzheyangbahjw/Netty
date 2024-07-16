package dyss.shop.demo1.Aio.Server;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * @author DYss东阳书生
 * @date 2024/7/16 20:02
 * @Description 描述
 */

public class AioServer extends Thread{
    private AsynchronousServerSocketChannel serverSocketChannel;

    @Override
    public void run() {
        try {
            //静态方法创建新的异步通道组【创建一个可缓存的线程池，初始线程池大小】
            serverSocketChannel = AsynchronousServerSocketChannel.open
                    (AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10));
            //将服务器套接字通道绑定到指定端口
            serverSocketChannel.bind(new InetSocketAddress(7397));
            //接受客户端连接，并用后面的类处理客户端连接的建立
            CountDownLatch latch = new CountDownLatch(1);
            serverSocketChannel.accept(this,new AioServerChannelInitializer());
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public AsynchronousServerSocketChannel serverSocketChannel(){
        return serverSocketChannel;
    }

    public static void main(String[] args) {
        new AioServer().start();
    }
}
