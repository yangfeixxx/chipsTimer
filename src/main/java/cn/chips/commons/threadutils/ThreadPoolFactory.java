package cn.chips.commons.threadutils;

import java.util.concurrent.*;

public class ThreadPoolFactory {
    /**
     * @param core 核心线程数
     * @return ScheduledThreadPoolExecutor 定时线程池(ScheduledThreadPoolExecutor使用的是默认队列长度为16，并且没有放开长度定义)
     */
    public static ScheduledThreadPoolExecutor buildScheduledThreadPool(int core, String threadPoolName) {
        return new ScheduledThreadPoolExecutor(core, new NameThreadFactory(false), new ChipsRejectedExecutionHandler(threadPoolName));
    }

    /**
     * @param core           核心线程数
     * @param max            最大线程数
     * @param queues         任务队列长度
     * @param threadPoolName 线程池名
     * @return
     */


    public static ThreadPoolExecutor buildThreadPoolExecutor(int core, int max, int queues, String threadPoolName) {
        return new ThreadPoolExecutor(core, max, 30, TimeUnit.SECONDS,
                queues == 0 ? new SynchronousQueue() :
                        queues < 0 ? new LinkedBlockingQueue() : new ArrayBlockingQueue(queues), new NameThreadFactory(false), new ChipsRejectedExecutionHandler(threadPoolName));
    }
}
