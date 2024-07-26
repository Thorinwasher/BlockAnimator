package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceBlocksAfterBlockAnimator implements BlockAnimator<Block> {

    private final int maxAmount;
    private final List<Entity> entitiesToRemove = new ArrayList<>();
    private final Map<Vector3D, Entity> blockEntityMap = new HashMap<>();
    private final Instance instance;

    public PlaceBlocksAfterBlockAnimator(int maxAmount, Instance instance) {
        this.maxAmount = maxAmount;
        this.instance = instance;
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D position, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, position, blockSupplier);
        Pos from = blockDisplay.getPosition();
        Vec delta = VectorConversion.toVec(position).sub(from).mul(20);
        blockDisplay.setVelocity(delta);
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, identifier, blockSupplier);
        blockDisplay.setVelocity(Vec.ZERO);
        blockDisplay.teleport(VectorConversion.toVec(identifier).asPosition());
        if (entitiesToRemove.size() > maxAmount) {
            finishAnimation(blockSupplier);
        }
        entitiesToRemove.add(blockDisplay);
        blockEntityMap.remove(identifier);
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), Block.AIR);
    }

    private Entity spawnOrGetBLockDisplay(Vector3D identifier, Vector3D startingPosition, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = blockEntityMap.get(identifier);
        if (blockDisplay == null) {
            Block block = blockSupplier.getBlock(identifier);
            blockDisplay = BlockAnimationUtils.spawnBlockDisplay(startingPosition, block, instance);
            blockEntityMap.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }

    @Override
    public void finishAnimation(BlockSupplier<Block> blockSupplier) {
        entitiesToRemove.forEach(entity -> {
            BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) entity.getEntityMeta();
            entity.getInstance().setBlock(entity.getPosition(), blockDisplayMeta.getBlockStateId());
            entity.remove();
        });
        entitiesToRemove.clear();
    }
}
