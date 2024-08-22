package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.List;

public interface BlockSelector {

    CompiledBlockSelector compile(List<ImmutableVector3i> blocks);
}
