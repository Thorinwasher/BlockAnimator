package dev.thorinwasher.blockanimator.selector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Deque;

public class CompiledBlockSelector {

    private final Deque<Vector3D> queue;

    public CompiledBlockSelector(Deque<Vector3D> queue){
        this.queue = queue;
    }

    public Vector3D next(){
        return queue.pollFirst();
    }
}
