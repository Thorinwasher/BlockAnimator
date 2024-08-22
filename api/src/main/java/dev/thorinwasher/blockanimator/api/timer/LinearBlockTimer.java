package dev.thorinwasher.blockanimator.api.timer;

import dev.thorinwasher.blockanimator.api.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

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
    public List<ImmutableVector3i> fetch(CompiledBlockSelector blockSelector, int totalBlockAmount) {
        double stepSize = (double) totalBlockAmount / ticks;
        int fetchApproximate = (int) (stepSize * ++currentTick - blocksPulled);
        List<ImmutableVector3i> blocks = new ArrayList<>();
        while (fetchApproximate >= 0 && totalBlockAmount > blocksPulled + blocks.size()) {
            List<ImmutableVector3i> next = blockSelector.next();
            blocks.addAll(next);
            fetchApproximate -= next.size();
        }
        blocksPulled += blocks.size();
        return blocks;
    }
}
