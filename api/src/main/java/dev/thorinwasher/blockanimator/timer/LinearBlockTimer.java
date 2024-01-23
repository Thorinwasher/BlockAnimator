package dev.thorinwasher.blockanimator.timer;

import java.util.ArrayList;
import java.util.List;

public class LinearBlockTimer implements BlockTimer {

    private final int ticks;

    public LinearBlockTimer(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public CompiledBlockTimer compile(int blocks) {
        double blocksPerTick = (double) blocks / ticks;
        double blocksLeft = blocks;
        List<Integer> frames = new ArrayList();
        while (blocksLeft > 0) {
            int previous = (int) blocksLeft;
            blocksLeft -= blocksPerTick;
            int next = Math.max((int) blocksLeft, 0);
            frames.add(previous-next);
        }
        return new CompiledBlockTimer(frames);
    }
}
