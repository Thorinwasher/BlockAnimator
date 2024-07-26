package dev.thorinwasher.blockanimator.paper.blockanimator;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.paper.ClassChecker;
import dev.thorinwasher.blockanimator.paper.v1_17_1.BlockPlaceAfter1_17_1;
import dev.thorinwasher.blockanimator.paper.v1_19_4.BlockPlaceAfter1_19_4;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.BlockState;

public class PlaceBlocksAfterBlockAnimator implements BlockAnimator<BlockState> {

    private final World world;
    private final int maxEntities;
    private final BlockAnimator<BlockState> handle;

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
    public void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<BlockState> blockSupplier) {
        handle.blockMove(identifier, to, blockSupplier);
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<BlockState> blockSupplier) {
        handle.blockPlace(identifier, blockSupplier);
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        handle.blockDestroy(identifier);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockState> blockSupplier) {
        handle.finishAnimation(blockSupplier);
    }
}
