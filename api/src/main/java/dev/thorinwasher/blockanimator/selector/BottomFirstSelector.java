package dev.thorinwasher.blockanimator.selector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BottomFirstSelector implements BlockSelector {

    @Override
    public CompiledBlockSelector compile(List<Vector3D> blocks) {
        List<Vector3D> output = new ArrayList<>(blocks);
        output.sort(Comparator.comparingDouble(Vector3D::getY));
        return new CompiledBlockSelector(new LinkedList<>(output));
    }
}
