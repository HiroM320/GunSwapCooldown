package io.github.hirom320.gunswapcooldown;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    GunSwapCooldown plugin;
    private FileConfiguration config;

    public ConfigManager(GunSwapCooldown plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public int getGroupCooldown(String group) {
        return config.getInt("Groups."+group);
    }

    public List<String> getWeapons(String group) {
        return config.getStringList(group);
    }

    public String getWeaponGroup(String weapon) {
        for( String group : config.getConfigurationSection("Groups").getKeys(false)) {
            // グループ内に武器が見つかったら
            if(getWeapons(group).contains(weapon)) {
                // グループ名を返す
                return group;
            }
        }
        // 見つからなかったらnull
        return null;
    }

    // グループ作成
    public void createGroup(String group, int cooldown) {
        if(config.isSet("Groups."+group)) {
            plugin.sendDebugLogInfo("group: exists");
            return;
        }
        // AR: 10
        config.set("Groups."+group, cooldown);
        // AR: -AK47
        config.createSection(group);

        plugin.groupCooldown.putIfAbsent(group, cooldown);
        plugin.groups.putIfAbsent(group, new ArrayList<>());

        saveConfig();

        plugin.sendDebugLogInfo("created group: "+group);
        plugin.sendDebugLogInfo("cooldown: "+cooldown);
    }

    // グループ削除
    public void removeGroup(String group) {
        config.set("Groups."+group, null);
        config.set(group, null);

        plugin.groups.remove(group);
        plugin.groupCooldown.remove(group);

        saveConfig();

        plugin.sendDebugLogInfo("removed group: "+group);


    }

    // グループにクールダウン設定
    public void setGroupCooldown(String group, int cooldown) {
        if(!config.isSet("Groups."+group)) {
            plugin.sendDebugLogInfo("group: not exists");
            return;
        }

        config.set("Groups."+group, cooldown);

        plugin.groupCooldown.put(group, cooldown);

        saveConfig();

        plugin.sendDebugLogInfo("set "+group+" cooldown: "+cooldown);

    }

    // グループに武器とそのクールダウンを設定
    public void addWeapon(String group, String weapon) throws InvalidConfigurationException {
        if(!config.isSet("Groups."+group)) {
            plugin.sendDebugLogInfo("addWeapon: no group");
            throw  new InvalidConfigurationException();
        }

        ArrayList<String> weaponList = plugin.groups.get(group);

        // すでにある場合
        if(!weaponList.isEmpty() && weaponList.contains(weapon)) {
            plugin.sendDebugLogInfo("addWeapon: exists");
            return;
        }

        weaponList.add(weapon);
        plugin.groups.put(group, weaponList);

        config.set(group, weaponList);
        saveConfig();

        plugin.sendDebugLogInfo("addWeapon: done");
    }

    // グループから武器を削除
    public void removeWeapon(String group, String weapon) throws InvalidConfigurationException {
        if(!config.isSet("Groups."+group)) {
            plugin.sendDebugLogInfo("removeWeapon: no group");
            throw new InvalidConfigurationException();
        }

        ArrayList<String> weaponList = plugin.groups.get(group);

        weaponList.remove(weapon);
        plugin.groups.put(group, weaponList);

        config.set(group, weaponList);
        saveConfig();

        plugin.sendDebugLogInfo("removeWeapon: done");
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        plugin.groups = getGroups();
        plugin.groupCooldown = getGroupCooldowns();

        plugin.sendDebugLogInfo("reloaded config");
    }

    public Map<String, Integer> getGroupCooldowns() {
        Map<String, Integer> groupCooldowns = new HashMap<>();

        // Groupsセクション内のkey: AR, value: 10として取得
        for(Map.Entry<String, Object> entry : config.getConfigurationSection("Groups").getValues(false).entrySet()) {
                groupCooldowns.put(entry.getKey(), (Integer)entry.getValue());
        }

        // Mapが入ったListを返す
        return groupCooldowns;
    }

    public Map<String, ArrayList<String>> getGroups() {
        Map<String, ArrayList<String>> groups = new HashMap<>();

        Map<String, Object> map = config.getConfigurationSection("Groups").getValues(false);

        if(map.isEmpty()) {
            return null;
        }

        // Groupsセクション内のkey: AR, value: 10として取得
        for(String key : config.getConfigurationSection("Groups").getValues(false).keySet()) {
            groups.put(key, (ArrayList<String>) config.getStringList(key));
        }

        return groups;
    }
}
