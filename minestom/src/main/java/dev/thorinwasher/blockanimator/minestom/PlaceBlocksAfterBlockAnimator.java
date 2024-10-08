package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceBlocksAfterBlockAnimator implements BlockAnimator<Block> {

    private final int maxAmount;
    private final List<Entity> entitiesToRemove = new ArrayList<>();
    private final Map<ImmutableVector3i, Entity> blockEntityMap = new HashMap<>();
    private final Instance instance;

    public PlaceBlocksAfterBlockAnimator(int maxAmount, Instance instance) {
        this.maxAmount = maxAmount;
        this.instance = instance;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d position, BlockSupplier<Block> blockSupplier, Matrix4f transform) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, position, blockSupplier);
        Pos from = blockDisplay.getPosition();
        Vec delta = VectorConversion.toVec(position).sub(from).mul(20);
        blockDisplay.setVelocity(delta);
        setTransform(blockDisplay, transform);
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<Block> blockSupplier) {
        Entity blockDisplay = spawnOrGetBLockDisplay(identifier, identifier.asVector3d(), blockSupplier);
        blockDisplay.setVelocity(Vec.ZERO);
        blockDisplay.teleport(VectorConversion.toVec(identifier).asPosition());
        if (entitiesToRemove.size() > maxAmount) {
            finishAnimation(blockSupplier);
        }
        entitiesToRemove.add(blockDisplay);
        blockEntityMap.remove(identifier);
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), Block.AIR);
    }

    private Entity spawnOrGetBLockDisplay(ImmutableVector3i identifier, Vector3d startingPosition, BlockSupplier<Block> blockSupplier) {
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

    private void setTransform(Entity entity, Matrix4f transform) {
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) entity.getEntityMeta();
        Quaternionf rotation = new Quaternionf();
        transform.rotation(rotation);
        blockDisplayMeta.setRightRotation(new float[]{rotation.x, rotation.y, rotation.z, rotation.w});
        Vector3f scale = new Vector3f();
        transform.getScale(scale);
        blockDisplayMeta.setScale(VectorConversion.toVec(new Vector3d(scale)));
    }
}
