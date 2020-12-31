package io.github.hirom320.gunswapcooldown;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class CooldownScheduler extends BukkitRunnable {

    GunSwapCooldown plugin;

    public CooldownScheduler(GunSwapCooldown plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Map.Entry<Player, Integer> entry : plugin.cooldowns.entrySet()) {
            if (entry.getValue() > 0) { // クールダウンが1以上なら
                entry.setValue(entry.getValue()-1); // クールダウンを-1する
            }
        }
    }
}
