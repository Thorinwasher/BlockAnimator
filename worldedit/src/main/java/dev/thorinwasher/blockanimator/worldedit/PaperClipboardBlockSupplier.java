package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class PaperClipboardBlockSupplier implements BlockSupplier<BlockData> {

    private final Clipboard clipboard;
    private final Vector3D origin;

    public PaperClipboardBlockSupplier(Clipboard clipboard, Vector3D origin) {
        this.clipboard = clipboard;
        this.origin = origin;
    }

    @Override
    public BlockData getBlock(Vector3D targetPosition) {
        BlockVector3 relativePosition = WEVectorConverter.toBlockVector3(targetPosition.subtract(origin));
        return BukkitAdapter.adapt(clipboard.getBlock(relativePosition));
    }

    @Override
    public List<Vector3D> getPositions() {
        List<BlockVector3> output = new ArrayList<>();
        clipboard.getRegion().forEach(output::add);
        return new ArrayList<>(output.stream().map(WEVectorConverter::toVector3D).map(origin::add).toList());
    }
}
