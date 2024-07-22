package dyss.shop.intro.V3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author DYss东阳书生
 * @date 2024/7/22 10:19
 * @Description 描述
 */
@Slf4j
public class FuturePromiseTest {
    @Test
    public void test_jdkFuture() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        //JDK future   执行任务的线程回填结果
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });
        log.info("等待结果");
        log.info("结果是:{}",future.get());
    }

    @Test
    public void test_nettyFuture() throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        io.netty.util.concurrent.Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("正在计算");
                Thread.sleep(2000);
                return 100;
            }
        });
//        log.info("正在等待");
//        log.info("结果为:{}",future.get());

        future.addListener(future1 -> {
           log.info("结果为:{}",future1.getNow());
        });
        new CountDownLatch(1).await();
    }

    @Test
    public void test_promise() throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(() -> {
            log.info("开始计算");
            try {
                Thread.sleep(1000);
                promise.setSuccess(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();
        log.info("等待结果");
        log.info("结果是:{}", promise.get());
        new CountDownLatch(1).await();
    }
}
