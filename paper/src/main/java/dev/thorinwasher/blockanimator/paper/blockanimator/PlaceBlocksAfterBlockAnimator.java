package dev.thorinwasher.blockanimator.paper.blockanimator;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.paper.ClassChecker;
import dev.thorinwasher.blockanimator.paper.v1_17_1.BlockPlaceAfter1_17_1;
import dev.thorinwasher.blockanimator.paper.v1_19_4.BlockPlaceAfter1_19_4;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public class PlaceBlocksAfterBlockAnimator implements BlockAnimator<BlockData> {

    private final World world;
    private final int maxEntities;
    private final BlockAnimator<BlockData> handle;

    public PlaceBlocksAfterBlockAnimator(World world, int maxEntities) {
        this.world = world;
        this.maxEntities = maxEntities;
        if (ClassChecker.classExists("org.bukkit.entity.BlockDisplay")) {
            handle = new BlockPlaceAfter1_19_4(world, maxEntities);
        } else {
            handle = new BlockPlaceAfter1_17_1(world, maxEntities);
        }
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<BlockData> blockSupplier, Matrix4f transform) {
        handle.blockMove(identifier, to, blockSupplier, transform);
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<BlockData> blockSupplier) {
        handle.blockPlace(identifier, blockSupplier);
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        handle.blockDestroy(identifier);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        handle.finishAnimation(blockSupplier);
    }
}
