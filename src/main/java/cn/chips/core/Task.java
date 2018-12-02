package cn.chips.core;

import java.util.Date;

public class Task {
    private int turnsCount; //圈数
    private RunnableTask rt;

    public Task(int turnsCount, RunnableTask rt) {
        this.turnsCount = turnsCount;
        this.rt = rt;
    }



    public int getTurnsCount() {
        return turnsCount;
    }

    public void setTurnsCount(int turnsCount) {
        this.turnsCount = turnsCount;
    }


    public RunnableTask getRt() {
        return rt;
    }

    public void setRt(RunnableTask rt) {
        this.rt = rt;
    }
}
