package dyss.shop.intro.V3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author DYss东阳书生
 * @date 2024/7/22 10:52
 * @Description 描述
 */
@Slf4j
public class HandlerPipelineTest {
    public static void main(String[] args) {
        EventLoopGroup group=new DefaultEventLoopGroup();
        new ServerBootstrap()
                //细分
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ch.alloc().buffer().writeBytes("12213".getBytes());  alloc是分配一个ByteBuf对象。
                                ByteBuf buf = (ByteBuf) msg;
                                String name = buf.toString(Charset.defaultCharset());
                                log.info("1，msg:{}",msg);
//                                ctx.fireChannelRead(msg);
//                                super方法也会调用   往下传递
                                super.channelRead(ctx,name);
                            }
                        }).addLast("handler2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("2，结果MSG:{}",msg);
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("heihei".getBytes()));
                            }
                        }).addLast("handler3", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("3");
                            }
                        }).addLast("handler4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("4");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
