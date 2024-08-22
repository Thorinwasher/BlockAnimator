package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BottomFirstSelector implements BlockSelector {

    @Override
    public CompiledBlockSelector compile(List<ImmutableVector3i> blocks) {
        List<ImmutableVector3i> output = new ArrayList<>(blocks);
        output.sort(Comparator.comparingDouble(ImmutableVector3i::y));
        return new CompiledBlockSelector(new LinkedList<>(output.stream().map(List::of).toList()));
    }
}
