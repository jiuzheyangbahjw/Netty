package dyss.shop.intro;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;



/**
 * @author DYss东阳书生
 * @date 2024/7/20 9:40
 * @Description 描述
 */
public class HelloServer {
    public static void main(String[] args) {
        //启动器，组装Netty，启动服务器
        new ServerBootstrap()
                //组
                .group(new NioEventLoopGroup())
                //选择服务器的socketChannel的具体实现
                .channel(NioServerSocketChannel.class)
                //worker的具体实现逻辑
                .childHandler(
                        //通道的初始化，添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //在链的最后添加将ByteBuf转化为字符串的处理器
                        ch.pipeline().addLast(new StringDecoder());
                        //在链的最后添加读事件处理器【自定义的】
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("msg = " + msg);
                            }
                        });
                    }
                })
                //监听端口
                .bind(8080);
    }
}
