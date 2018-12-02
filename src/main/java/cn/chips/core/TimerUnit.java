package cn.chips.core;

import java.util.concurrent.TimeUnit;

public enum TimerUnit {
    MILLISECONDS1(TimeUnit.MILLISECONDS, 1), MILLISECONDS10(TimeUnit.MILLISECONDS, 10), MILLISECONDS100(TimeUnit.MILLISECONDS, 100), SECONDS1(TimeUnit.SECONDS, 1);

    private TimeUnit timeUnit;
    private int t;

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    TimerUnit(TimeUnit milliseconds, int i) {
    }
}
