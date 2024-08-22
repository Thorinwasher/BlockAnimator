package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class LayeredBottomFirst implements BlockSelector {
    @Override
    public CompiledBlockSelector compile(List<ImmutableVector3i> blocks) {
        List<ImmutableVector3i> toSort = new ArrayList<>(blocks);
        toSort.sort(Comparator.comparingDouble(ImmutableVector3i::y));
        LinkedList<List<ImmutableVector3i>> output = new LinkedList<>();
        for (ImmutableVector3i block : toSort) {
            if (output.isEmpty()) {
                output.add(new ArrayList<>(List.of(block)));
                continue;
            }
            ImmutableVector3i toCompare = output.getLast().get(0);
            if (toCompare.y() != block.y()) {
                output.addLast(new ArrayList<>(List.of(block)));
            } else {
                output.getLast().add(block);
            }
        }
        return new CompiledBlockSelector(output);
    }
}
