package dev.thorinwasher.blockanimator.paper.blockanimator;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.paper.ClassChecker;
import dev.thorinwasher.blockanimator.paper.v1_17_1.BlockPlaceDirectly1_17_1;
import dev.thorinwasher.blockanimator.paper.v1_19_4.BlockPlaceDirectly1_19_4;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public class PlaceBlocksDirectlyBlockAnimator implements BlockAnimator<BlockData> {

    private final BlockAnimator<BlockData> handle;

    public PlaceBlocksDirectlyBlockAnimator(World world) {
        if (ClassChecker.classExists("org.bukkit.entity.BlockDisplay")) {
            this.handle = new BlockPlaceDirectly1_19_4(world);
        } else {
            this.handle = new BlockPlaceDirectly1_17_1(world);
        }
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<BlockData> blockSupplier) {
        handle.blockMove(identifier, to, blockSupplier);
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<BlockData> blockSupplier) {
        handle.blockPlace(identifier, blockSupplier);
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        handle.blockDestroy(identifier);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        handle.finishAnimation(blockSupplier);
    }
}
