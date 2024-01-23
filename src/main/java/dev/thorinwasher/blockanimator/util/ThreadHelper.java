package dev.thorinwasher.blockanimator.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class ThreadHelper {

    public static void scheduleRegionTask(Location location, Runnable task, Plugin plugin, long delay) {
        if (OptionalMethod.FOLIA) {
            Bukkit.getServer().getRegionScheduler().runDelayed(plugin, location, ignored -> task.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    public static void scheduleEntityTask(Entity entity, Runnable task, Plugin plugin, long delay) {
        if (OptionalMethod.FOLIA) {
            entity.getScheduler().runDelayed(plugin, ignored -> task.run(), null, delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    public static void scheduleGlobalTask(Runnable task, Plugin plugin, long delay) {
        if (OptionalMethod.FOLIA) {
            Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, ignored -> task.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }
}
