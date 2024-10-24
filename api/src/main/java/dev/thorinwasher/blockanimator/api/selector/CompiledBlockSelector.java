package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CompiledBlockSelector {

    private final Queue<List<ImmutableVector3i>> queue;

    public CompiledBlockSelector(Queue<List<ImmutableVector3i>> queue) {
        this.queue = queue;
    }

    public @NotNull List<ImmutableVector3i> next() {
        List<ImmutableVector3i> toAdd = queue.poll();
        if (toAdd == null) {
            return new ArrayList<>();
        }
        return toAdd;
    }
}
