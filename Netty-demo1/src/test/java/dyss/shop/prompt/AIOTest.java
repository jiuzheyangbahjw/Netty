package dyss.shop.prompt;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author DYss东阳书生
 * @date 2024/7/20 8:52
 * @Description 描述
 */
public class AIOTest {
    public static void main(String[] args) throws IOException {
        //读模式打开
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("D:\\DYSS-techStack\\Netty\\Netty-demo1\\src\\main\\resources\\data.txt"), StandardOpenOption.READ)) {
            // 参数1 ByteBuffer 用于接收结果
            // 参数2 读取的起始位置
            // 参数3 附件（一次读取不能读完时，用于读取剩下的数据）
            // 参数4 回调对象 CompletionHandler
            ByteBuffer buffer = ByteBuffer.allocate(16);
            System.out.println("read begin");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override // read 成功
                //para1 result 读到的实际字节数
                public void completed(Integer result, ByteBuffer attachment) { //在其他线程执行--是一个守护线程
                    System.out.println("read complete:"+result);
                    attachment.flip();
                }
                @Override // read 失败
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            System.out.println("read finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //其他线程都结束了，守护线程也会结束，无论其是否运行完
        System.in.read();
    }
}
