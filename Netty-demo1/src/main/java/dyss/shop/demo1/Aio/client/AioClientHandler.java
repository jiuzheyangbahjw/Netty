package dyss.shop.demo1.Aio.client;

import dyss.shop.demo1.Aio.ChannelAdapter;
import dyss.shop.demo1.Aio.ChannelHandler;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author DYss东阳书生
 * @date 2024/7/15 22:49
 * @Description 描述
 */

public class AioClientHandler extends ChannelAdapter {
    public AioClientHandler(AsynchronousSocketChannel channel, Charset charset) {
        super(channel, charset);
    }

    @Override
    public void channelActive(ChannelHandler ctx) {
        try {
            System.out.println("链接报告信息"+ctx.channel().getRemoteAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void channelInactive(ChannelHandler ctx) {
        System.out.println("无效信道");
    }

    @Override
    public void channelRead(ChannelHandler ctx, Object msg) {
        System.out.println("客户端受到信息:"+new Date()+" "+msg+"\r\n");
        ctx.writeAndFlush("客户端信息处理success!!!\r\n");
    }
}
