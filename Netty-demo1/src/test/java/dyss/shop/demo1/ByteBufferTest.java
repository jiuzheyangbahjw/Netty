package dyss.shop.demo1;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author DYss东阳书生
 * @date 2024/7/17 10:02
 * @Description 描述
 */

public class ByteBufferTest {

    @Test
    public void test_ByteBuffer(){
        // FileChannel
        // 1. 输入输出流， 2. RandomAccessFile
        try (FileChannel channel = new FileInputStream("D:\\DYSS-techStack\\Netty\\Netty-demo1\\src\\main\\resources\\data.txt").getChannel()) {
            // 准备缓冲区，allocate 分配大小（单位字节）
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while(true) {
                // 从 channel 读取数据，向 buffer 写入
                int len = channel.read(buffer);
                System.out.println("字节数len = " + len);
                if(len == -1) { // 没有内容了
                    break;
                }
                // 打印 buffer 的内容
                buffer.flip(); // 切换至读模式
                while(buffer.hasRemaining()) { // 是否还有剩余未读数据
                    byte b = buffer.get();
                    System.out.printf("实际字节%c ",(char) b);
                    System.out.println();
                }
                buffer.clear(); // 切换为写模式
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test_BufferArea(){
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }

    @Test
    public void test_BufferToString(){
        // 1. 字符串转为 ByteBuffer(仍然是写模式)
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());

        // 2. Charset（自动切换到读模式）
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");

        // 3. wrap（nio提供的工具类，自动切换到读模式）
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());

        // 4. ByteBuffer转为字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);

        //对第一种方式，要先切换成读模式
        buffer1.flip();
        String str2 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str2);
    }

    @Test
    public void test_DiscreteRead(){
        try (RandomAccessFile file = new RandomAccessFile("helloword/3parts.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer a = ByteBuffer.allocate(3);
            ByteBuffer b = ByteBuffer.allocate(3);
            ByteBuffer c = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{a, b, c});
            a.flip(); //切换成读模式
            b.flip();
            c.flip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_gatherWriting(){
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好"); //6字节
        try (FileChannel channel = new RandomAccessFile("words2.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }
    }

    //粘包半包处理【每遇到\n,就进行处理，只是简单分配，这里只是个思路】
    @Test
    public void test_Split(){
        ByteBuffer source = ByteBuffer.allocate(32);
        //模拟网络传输
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }
    private static void split(ByteBuffer source) {
        source.flip(); //读模式
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i - source.position() + 1;
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
            }
        }
        source.compact(); //写模式
    }




}



