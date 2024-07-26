package dev.thorinwasher.blockanimator.paper.v1_19_4;

import dev.thorinwasher.blockanimator.api.algorithms.ManhatanNearest;
import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockPlaceDirectly1_19_4 implements BlockAnimator<BlockState> {
    private final World world;
    private final Map<Vector3D, BlockDisplay> blockDisplays = new HashMap<>();

    public BlockPlaceDirectly1_19_4(World world) {
        this.world = world;
    }

    @Override
    public void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<BlockState> blockSupplier) {
        BlockDisplay blockDisplay = getOrSpawnBlockDisplay(identifier, to, blockSupplier);
        Location current = blockDisplay.getLocation();
        Vector delta = VectorConverter.toLocation(to, world).subtract(current).toVector();
        Transformation transformation = blockDisplay.getTransformation();
        blockDisplay.setTransformation(new Transformation(delta.toVector3f(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
    }

    @Override
    public void blockPlace(Vector3D identifier, BlockSupplier<BlockState> blockSupplier) {
        BlockDisplay blockDisplay = blockDisplays.remove(identifier);
        if (blockDisplay != null) {
            blockDisplay.remove();
        }
        blockSupplier.getBlock(identifier).update(true, false);
    }

    @Override
    public void blockDestroy(Vector3D identifier) {
        VectorConverter.toLocation(identifier, world).getBlock().setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockState> blockSupplier) {
        // Blocks are placed dynamically, nothing needs to be done
    }

    private BlockDisplay getOrSpawnBlockDisplay(Vector3D identifier, Vector3D startingPos, BlockSupplier<BlockState> blockSupplier) {
        BlockDisplay blockDisplay = blockDisplays.get(identifier);
        if (blockDisplay == null) {
            Optional<Vector3D> middlePoint = ManhatanNearest.findClosestPosition(
                    identifier.scalarMultiply(0.5).add(startingPos.scalarMultiply(0.5)),
                    vector3D -> VectorConverter.toLocation(vector3D, world).getBlock().getType().isAir(), 5);
            blockDisplay = EntityUtils.spawnBLockDisplay(world, blockSupplier.getBlock(identifier), middlePoint.orElse(startingPos));
            blockDisplays.put(identifier, blockDisplay);
        }
        return blockDisplay;
    }
}
