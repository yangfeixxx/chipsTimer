package cn.chips.commons.threadutils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {
    private AtomicInteger ai = new AtomicInteger(1);
    private ThreadGroup threadGroup;
    private boolean isDaemon = false;

    public NameThreadFactory(boolean isDaemon) {
        this.threadGroup = Thread.currentThread().getThreadGroup();
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable r) {

        return new Thread(this.threadGroup, r, "ChipsThread-" + ai.getAndIncrement());

    }
}
