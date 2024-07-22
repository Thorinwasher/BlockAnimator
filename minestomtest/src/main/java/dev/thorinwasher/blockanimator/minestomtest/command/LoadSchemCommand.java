package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.minestomtest.Registry;
import net.hollowcube.schem.Schematic;
import net.hollowcube.schem.SchematicReader;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoadSchemCommand extends Command {
    public LoadSchemCommand() {
        super("schemload");

        ArgumentString schemAddress = ArgumentType.String("address");
        addSyntax((sender, context) -> {
            if(!(sender instanceof Player player)){
                throw new IllegalArgumentException("Only players can issue this command");
            }
            File address = new File(context.get(schemAddress));
            try (InputStream inputStream = new FileInputStream(address)) {
                Schematic schematic = new SchematicReader().read(inputStream);
                Registry.playerSchematicRegistry.put(player.getUuid(), schematic);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }, schemAddress);
    }
}
