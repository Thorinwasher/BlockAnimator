package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class PaperClipboardBlockSupplier implements BlockSupplier<BlockData> {

    private final Clipboard clipboard;
    private final Vector3D origin;
    private final com.sk89q.worldedit.world.World world;

    public PaperClipboardBlockSupplier(Clipboard clipboard, Vector3D origin, World world) {
        this.clipboard = clipboard;
        this.origin = new Vector3D(Math.round(origin.getX()), Math.round(origin.getY()), Math.round(origin.getZ()));
        this.world = BukkitAdapter.adapt(world);
    }

    @Override
    public BlockData getBlock(Vector3D targetPosition) {
        BlockVector3 relativePosition = WEVectorConverter.toBlockVector3(targetPosition.subtract(origin)).add(clipboard.getOrigin());
        return BukkitAdapter.adapt(clipboard.getBlock(relativePosition));
    }

    @Override
    public List<Vector3D> getPositions() {
        List<BlockVector3> output = new ArrayList<>();
        clipboard.getRegion().forEach(output::add);
        return new ArrayList<>(output.stream().map(blockVector3 -> blockVector3.subtract(clipboard.getOrigin()))
                .map(WEVectorConverter::toVector3D)
                .map(origin::add).toList());
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        BlockVector3 relativePosition = WEVectorConverter.toBlockVector3(identifier.subtract(origin)).add(clipboard.getOrigin());
        BaseBlock baseBlock = clipboard.getFullBlock(relativePosition);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            editSession.setSideEffectApplier(SideEffectSet.none().with(SideEffect.LIGHTING, SideEffect.State.ON));
            editSession.setBlock(WEVectorConverter.toBlockVector3(identifier), baseBlock);
            Operations.complete(editSession.commit());
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }
}
