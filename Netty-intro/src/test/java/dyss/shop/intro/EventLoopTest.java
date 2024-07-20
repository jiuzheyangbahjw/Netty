package dyss.shop.intro;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author DYss东阳书生
 * @date 2024/7/20 10:45
 * @Description 描述
 */
@Slf4j
public class EventLoopTest {
    /**
     * 最简单的概念
     */
    @Test
    public void test_eventGroup1(){
        //处理 IO，事件，普通任务，定时任务
        EventLoopGroup group1=new NioEventLoopGroup(2);
        //处理 普通任务，定时任务
        EventLoopGroup group2=new DefaultEventLoop();
        log.info("当前电脑CPU核心数:{}",NettyRuntime.availableProcessors());
//        System.out.println(group1.next());
        group1.next().submit(()->{
            log.info("线程正在输出okkkkkkkkkk");
        });
        //execute效果相同
//        group1.next().execute(()->{
//            log.info("当前线程正在输出okkkkkkkkkk");
//        });
        log.info("主线程print....");
    }
    @Test
    public void test_eventGroup_schedule() throws IOException, InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup(2);
        group.next().scheduleAtFixedRate(()->{
            log.info("定时任务线程正在输出...");
        },0,1, TimeUnit.SECONDS);
        log.info("主线程正在输出");
        new CountDownLatch(1).await();
    }

}
