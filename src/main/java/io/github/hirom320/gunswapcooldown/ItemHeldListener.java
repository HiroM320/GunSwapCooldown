package io.github.hirom320.gunswapcooldown;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.Map;

public class ItemHeldListener implements Listener {

    GunSwapCooldown plugin;
    CSUtility cs;

    public ItemHeldListener(GunSwapCooldown plugin) {
        this.plugin = plugin;
        cs = plugin.cs;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        String gunName = cs.getWeaponTitle(event.getPlayer().getInventory().getItem(event.getNewSlot()));

        // アイテムがCrackshotの銃でないとき
        if(gunName==null) {
            return;
        }

        // クールダウンのデフォルトは10ticks(0.5sec)
        int cooldown = 10;

        // key: グループ名, value: 武器リスト
        for(Map.Entry<String, ArrayList<String>> entry : plugin.groups.entrySet()) {
            // 武器名がリストに入っている時
            if(entry.getValue().contains(gunName)) {
                cooldown = plugin.groupCooldown.get(entry.getKey());
            }
        }

        // クールダウンの登録
        plugin.cooldowns.put(event.getPlayer(), cooldown);

    }
}
