package dev.thorinwasher.blockanimator.api.supplier;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListBlockSupplier<B> implements BlockSupplier<B> {

    private final Map<Vector3D, B> states;

    public ListBlockSupplier(Map<Vector3D, B> states) {
        this.states = states;
    }

    @Override
    public B getBlock(Vector3D targetPosition) {
        return states.get(targetPosition);
    }

    @Override
    public List<Vector3D> getPositions() {
        return new ArrayList<>(states.keySet());
    }
}
