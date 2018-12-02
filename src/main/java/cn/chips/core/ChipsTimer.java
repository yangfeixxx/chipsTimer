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
    private WheelAnnularArray<LinkedList<Task>> waal;
    private static AtomicInteger ai = new AtomicInteger(0);

    private static AtomicInteger timerThreadPool = new AtomicInteger(1);
    private static AtomicInteger doWorkThreadPool = new AtomicInteger(1);
    private static final int DEFAULT_CORESIZE = 16;
    private static final TimerUnit DEFAULT_TIMERUNIT = TimerUnit.MILLISECONDS100;

    public ChipsTimer() {
        this(DEFAULT_CORESIZE, DEFAULT_TIMERUNIT);
    }

    public ChipsTimer(int coreSize, TimerUnit timeUnit) {
        this.coreSize = coreSize;
        this.es = ThreadPoolFactory.buildScheduledThreadPool(1, "timerThreadPool-" + ai.getAndIncrement());
        this.tpe = ThreadPoolFactory.buildThreadPoolExecutor(coreSize, 100, -1, "doWorkThreadPool-" + doWorkThreadPool.getAndIncrement());
        this.waal = new WheelAnnularArray();
        this.timeUnit = timeUnit.getTimeUnit();
        this.t = timeUnit.getT();
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

        LinkedList<Task> runnableTasks = waal.get(i);

        if (runnableTasks == null) {
            runnableTasks = new LinkedList();
            runnableTasks.addLast(task);
            waal.add(runnableTasks, i);
        } else runnableTasks.addLast(task);
    }

    public void start() {
        es.schedule(() -> {
            new Thread(() -> {
                LinkedList queue = waal.get(ai.getAndIncrement());
                for (int i = 0, j = queue.size(); i < j; i++) {
                    Task task = (Task) queue.poll();
                    int turnsCount = task.getTurnsCount();
                    if (turnsCount != 0)
                        --turnsCount;
                    else
                        tpe.execute(() -> task.getRt().run());
                }
            }).start();
        }, this.t, this.timeUnit);
    }
}
