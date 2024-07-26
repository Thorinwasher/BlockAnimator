package dev.thorinwasher.blockanimator.api.selector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class LayeredBottomFirst implements BlockSelector {
    @Override
    public CompiledBlockSelector compile(List<Vector3D> blocks) {
        List<Vector3D> toSort = new ArrayList<>(blocks);
        toSort.sort(Comparator.comparingDouble(Vector3D::getY));
        LinkedList<List<Vector3D>> output = new LinkedList<>();
        for (Vector3D block : toSort) {
            if (output.isEmpty()) {
                output.add(new ArrayList<>(List.of(block)));
                continue;
            }
            Vector3D toCompare = output.getLast().get(0);
            if (toCompare.getY() != block.getY()) {
                output.addLast(new ArrayList<>(List.of(block)));
            } else {
                output.getLast().add(block);
            }
        }
        return new CompiledBlockSelector(output);
    }
}
