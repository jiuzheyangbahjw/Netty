package dyss.shop.intro.V2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * @author DYss东阳书生
 * @date 2024/7/20 9:57
 * @Description 描述
 */
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //最后记得把其关掉，否则进程继续运行。
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception{
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();
        log.info("channel:{}",channel);
        new Thread(()->{
            Scanner sc = new Scanner(System.in);
            while (true){
                String s = sc.nextLine();
                if ("q".equals(s)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(s);
            }
        },"input").start();

        /**********/  //  改进部分🍒🍒🍒🍒🍒【同步关闭】
  /*      ChannelFuture closeFuture = channel.closeFuture();
        System.out.println("等待关闭中.........");
        closeFuture.sync(); //阻塞住了，必须等到关闭
        log.info("处理关闭之后的操作");*/

        //方法2listener
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            log.info("处理关闭之后的操作");
            group.shutdownGracefully();
        });
    }


    @Test
    public void test_sync() throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception{
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        //A 使用sync方法
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.info("channel：{}",channel);
        channel.writeAndFlush("hello");

//        B 使用监听者
        /*channelFuture.addListener(new ChannelFutureListener() {
            @Override
            //如果NIO连接建立好之后，就会调用该方法。注意
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.info("channel：{}",channel);
                channel.writeAndFlush("hello");
            }
        });*/
        new CountDownLatch(1).await();//如果没有，数据可能没发出去就结束了。
    }

    @Test
    public void test_listener() throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception{
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            Channel channel = channelFuture1.channel();
            log.info("channel：{}",channel);
            channel.writeAndFlush("hello");
        });
        new CountDownLatch(1).await();
    }

    @Test
    public void test_Scanner() throws InterruptedException {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()//等待完成
                .channel();
        System.out.println("channel = " + channel);
        System.out.println("");

    }
}
