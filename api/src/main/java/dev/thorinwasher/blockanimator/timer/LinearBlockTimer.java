package dev.thorinwasher.blockanimator.timer;

import dev.thorinwasher.blockanimator.selector.CompiledBlockSelector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class LinearBlockTimer implements BlockTimer {

    private final int ticks;
    private int currentTick = 0;
    private int blocksPulled;

    public LinearBlockTimer(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public boolean hasNext(int totalBlockAmount) {
        return totalBlockAmount > blocksPulled;
    }

    @Override
    public List<Vector3D> fetch(CompiledBlockSelector blockSelector, int totalBlockAmount) {
        double stepSize = (double) totalBlockAmount / ticks;
        int fetchApproximate = (int) (stepSize * ++currentTick - blocksPulled);
        List<Vector3D> blocks = new ArrayList<>();
        while (fetchApproximate >= 0 && totalBlockAmount > blocksPulled + blocks.size()) {
            List<Vector3D> next = blockSelector.next();
            blocks.addAll(next);
            fetchApproximate -= next.size();
        }
        blocksPulled += blocks.size();
        return blocks;
    }
}
