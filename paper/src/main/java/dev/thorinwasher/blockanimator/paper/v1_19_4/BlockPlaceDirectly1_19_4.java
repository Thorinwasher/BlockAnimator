package dev.thorinwasher.blockanimator.paper.v1_19_4;

import dev.thorinwasher.blockanimator.api.algorithms.ManhatanNearest;
import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockPlaceDirectly1_19_4 implements BlockAnimator<BlockData> {
    private final World world;
    private final Map<ImmutableVector3i, BlockDisplay> blockDisplays = new HashMap<>();

    public BlockPlaceDirectly1_19_4(World world) {
        this.world = world;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, to, blockSupplier);
        Location current = blockDisplay.getLocation();
        Vector delta = VectorConverter.toLocation(to, world).subtract(current).toVector();
        Transformation transformation = blockDisplay.getTransformation();
        blockDisplay.setTransformation(new Transformation(delta.toVector3f(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = blockDisplays.remove(identifier);
        if (blockDisplay != null) {
            blockDisplay.remove();
        }
        blockSupplier.placeBlock(identifier);
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        VectorConverter.toLocation(identifier, world).getBlock().setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        // Blocks are placed dynamically, nothing needs to be done
    }

    @Override
    public void setTransform(ImmutableVector3i identifier, Matrix4f transform) {
        BlockDisplay blockDisplay = blockDisplays.get(identifier);
        Transformation transformation = blockDisplay.getTransformation();
        Quaternionf rotation = new Quaternionf();
        transform.getNormalizedRotation(rotation);
        Vector3f translation = new Vector3f();
        transform.getTranslation(translation);
        Vector3f scale = new Vector3f();
        transform.getScale(scale);
        blockDisplay.setTransformation(new Transformation(translation, transformation.getLeftRotation(), scale, rotation));
    }

    private BlockDisplay getOrSpawnBlockDisplay(ImmutableVector3i identifier, Vector3d startingPos, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplay blockDisplay = blockDisplays.get(identifier);
        if (blockDisplay == null) {
            Optional<Vector3d> middlePoint = ManhatanNearest.findClosestPosition(
                    identifier.asVector3d().mul(0.5).add(new Vector3d(startingPos).mul(0.5)),
                    Vector3d -> VectorConverter.toLocation(Vector3d, world).getBlock().getType().isAir(), 5);
            blockDisplay = EntityUtils.spawnBLockDisplay(world, blockSupplier.getBlock(identifier), middlePoint.orElse(startingPos));
            blockDisplays.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }
}
