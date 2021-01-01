package io.github.hirom320.gunswapcooldown;

import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PrepareShootListener implements Listener {

    GunSwapCooldown plugin;
    CSUtility cs;

    public PrepareShootListener(GunSwapCooldown plugin) {
        this.plugin = plugin;
        this.cs = plugin.cs;
    }

    @EventHandler
    public void onPrepareShoot(WeaponPrepareShootEvent event) {
        if(plugin.cooldowns.containsKey(event.getPlayer())) {
            // クールダウンが0より大きい(1以上)のとき
            if (plugin.cooldowns.get(event.getPlayer()) > 0) {

                event.getPlayer().getWorld().playSound(
                    event.getPlayer().getLocation(),
                    plugin.configManager.getSoundOnCd(),
                    plugin.configManager.getVolumeOnCd(),
                    plugin.configManager.getPitchOnCd()
                );

                event.setCancelled(true);
            }
        }
    }
}
