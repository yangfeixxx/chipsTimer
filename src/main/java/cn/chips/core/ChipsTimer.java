package cn.chips.core;

import cn.chips.commons.threadutils.ThreadPoolFactory;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChipsTimer {

    private boolean isRunner;
    private int coreSize;
    private TimeUnit timeUnit;
    private int t;
    private ScheduledThreadPoolExecutor es;
    private ThreadPoolExecutor tpe;
    private ThreadPoolExecutor wheeltpe;
    private WheelAnnularArray<ConcurrentLinkedQueue<Task>> waal;
    private static AtomicInteger ai = new AtomicInteger(0);

    private static AtomicInteger timerThreadPool = new AtomicInteger(1);
    private static AtomicInteger doWorkThreadPool = new AtomicInteger(1);
    private static AtomicInteger doWheelWorkThreadPool = new AtomicInteger(1);
    private static final int DEFAULT_CORESIZE = 16;
    private static final TimerUnit DEFAULT_TIMERUNIT = TimerUnit.MILLISECONDS100;

    public ChipsTimer() {
        this(DEFAULT_CORESIZE, DEFAULT_TIMERUNIT);
    }

    public ChipsTimer(int coreSize, TimerUnit timeUnit) {
        this.coreSize = coreSize;
        this.es = ThreadPoolFactory.buildScheduledThreadPool(1, "timerThreadPool-" + timerThreadPool.getAndIncrement());
        this.tpe = ThreadPoolFactory.buildThreadPoolExecutor(coreSize, 100, -1, "doWorkThreadPool-" + doWorkThreadPool.getAndIncrement());
        this.wheeltpe = ThreadPoolFactory.buildThreadPoolExecutor(10, Integer.MAX_VALUE, -1, "doWheelWorkThreadPool-" + doWheelWorkThreadPool.getAndIncrement());
        this.waal = new WheelAnnularArray(3600);
        this.timeUnit = timeUnit.getTimeUnit();
        this.t = timeUnit.getT();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            es.shutdown();
            tpe.shutdown();
            wheeltpe.shutdown();
        }));
    }

    /**
     * @param rt   任务类
     * @param time 多久之后运行
     */
    public void addTask(RunnableTask rt, int time) {
        int size = waal.getSize();
        int index = waal.getIndex();
        int i = (time + index) % size; //取得任务所在链表的下标

        Task task = new Task(time / size, rt);

        ConcurrentLinkedQueue<Task> runnableTasks = waal.get(i);

        if (runnableTasks == null) {
            runnableTasks = new ConcurrentLinkedQueue();
            runnableTasks.add(task);
            waal.add(runnableTasks, i);
        } else runnableTasks.add(task);
    }

    public void start() {

        es.schedule(() -> {
            wheeltpe.execute(() -> {
                ConcurrentLinkedQueue<Task> queue = waal.get(ai.getAndIncrement());
                queue.forEach(task -> {
                    int turnsCount = task.getTurnsCount();
                    if (turnsCount != 0)
                        --turnsCount;
                    else {
                        queue.remove(task);
                        tpe.execute(() -> task.getRt().run());
                    }
                });
            });
        }, this.t, this.timeUnit);

    }
}
