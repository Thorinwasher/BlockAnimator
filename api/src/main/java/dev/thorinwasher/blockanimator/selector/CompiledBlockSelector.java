package dev.thorinwasher.blockanimator.selector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CompiledBlockSelector {

    private final Queue<List<Vector3D>> queue;

    public CompiledBlockSelector(Queue<List<Vector3D>> queue) {
        this.queue = queue;
    }

    public List<Vector3D> next() {
        List<Vector3D> toAdd = queue.poll();
        if (toAdd == null) {
            return new ArrayList<>();
        }
        return toAdd;
    }
}
