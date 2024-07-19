package dyss.shop.prompt;

import javafx.concurrent.Worker;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author DYss东阳书生
 * @date 2024/7/19 13:19
 * @Description 描述
 */

public class workerTest {
    public static void main(String[] args) throws Exception{
        Thread.currentThread().setName("boss"); //设置线程名字
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open(); //专门用于处理accept event
        SelectionKey bossKey = ssc.register(boss,0,null);  //将channel与selector关联起来
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        Worker worker = new Worker("worker01");
        // 1 创建固定数量的worker并初始化
        while(true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    System.out.println("与远程"+sc.getRemoteAddress()+"建立连接");
                    System.out.println("事件建立连接前");
                    worker.register(sc);
               //     sc.register(worker.selector,SelectionKey.OP_READ,null);  //线程顺序问题
                    System.out.println("事件建立连接后 ");
                }
            }
        }
    }
    // 只有内部类能够定义为static
    // 为了在 main 函数中使用，定义为static类
    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name; //work对应的线程名字
        private volatile boolean start = false;//所有内存可见，防止多线程问题

        private ConcurrentLinkedQueue<Runnable> queue=new ConcurrentLinkedQueue<>();

        public Worker(String name){this.name = name;}
        // 初始化线程和selector
        public void register(SocketChannel sc) throws IOException {
            if(!start){   // 利用 start 保证这段代码只会被执行一次。
                selector = Selector.open();   // open返回：SelectorProvider.provider().openSelector()
                thread = new Thread( this,name);//传入实现runnable的类，第二个是标记线程的名字
                thread.start();
                start = true;
            }
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();
            System.out.println("主动唤醒selector");
        }
        @Override
        public void run() {
            while(true){
                try{
                    //这个select方法一旦阻塞，就没法注册，   🍒🍒🍒🍒
                    //在这里一定要保证注册的时候 不能保持阻塞状态的  💣💣💣
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                        System.out.println("注册任务执行完毕");
                    }

                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while(iter.hasNext()){
                        SelectionKey key = iter.next();
                        /*这里实际读写需要考虑消息边界，写的数据规模过大的问题，以及连接的正常/异常关闭问题详见单线程版本设计*/
                        if(key.isReadable()){
                            try {
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                SocketChannel channel = (SocketChannel) key.channel();
                                System.out.println("worker读到数据from："+channel.getRemoteAddress());
                                int read = channel.read(buffer);
                                if (read==-1) {
                                    key.cancel();
                                    channel.close();
                                }else {
                                    buffer.flip();
                                    printBytebuffer(buffer);
                                }
                            }catch (IOException e){
                                //这里表现为异常中止客户端情况，必须要清除该selectionKey。
                                key.cancel();
                                e.printStackTrace();
                            }
                        }
                        iter.remove();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    static void printBytebuffer(ByteBuffer tmp){      // 注意：传入的bytebuffer必须时写模式
        System.out.println(StandardCharsets.UTF_8.decode(tmp).toString());
    }
}
