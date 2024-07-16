package dyss.shop.demo1.Aio;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author DYss东阳书生
 * @date 2024/7/16 17:42
 * @Description 描述
 */

public abstract class ChannelAdapter implements CompletionHandler {

    private AsynchronousSocketChannel channel;
    private Charset charset;

    public ChannelAdapter(AsynchronousSocketChannel channel, Charset charset) {
        this.channel = channel;
        this.charset = charset;
        //确定通道是否可用【一般在读取或者写入操作前可以检查】
        if (channel.isOpen()) {
            //否则进行普通通道处理对象的创建
            channelActive(new ChannelHandler(channel,charset));
        }
    }

    @Override
    public void completed(Object result, Object attachment) {
        try {
           final ByteBuffer buffer = ByteBuffer.allocate(1024);
           final long timeout = 60 * 60L;
           channel.read(buffer, timeout, TimeUnit.SECONDS, null, new CompletionHandler<Integer, Object>() {
               @Override
               public void completed(Integer result, Object attachment) {
                   if (result == -1) {
                       try {
                           channelInactive(new ChannelHandler(channel, charset));
                           channel.close();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       return;
                   }
                   buffer.flip();
                   //将字节解码为字符。
                   channelRead(new ChannelHandler(channel, charset), charset.decode(buffer));
                   //清空缓冲区，准备下一次的读取
                   buffer.clear();
                   channel.read(buffer, timeout, TimeUnit.SECONDS, null, this);
               }
               @Override
               public void failed(Throwable exc, Object attachment) {
                   exc.printStackTrace();
               }
           });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        exc.getStackTrace();
    }

    public abstract void channelActive(ChannelHandler ctx);
    //通道无效/惰性类
    public abstract void channelInactive(ChannelHandler ctx);
    //读取消息抽象类
    public abstract void channelRead(ChannelHandler ctx,Object msg);
}
