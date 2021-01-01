package io.github.hirom320.gunswapcooldown;

import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    GunSwapCooldown plugin;
    private FileConfiguration config;


    /**
     * コンストラクタ
     * @param plugin {@link GunSwapCooldown}
     */
    public ConfigManager(GunSwapCooldown plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }


    /**
     * デフォルトのクールダウン時間を取得
     * @return
     */
    public int getDefaultCooldown() {
        return config.getInt("DefaultCooldown", 0);
    }


    /**
     * クールダウン中射撃時のサウンドを取得
     * @return
     */
    public Sound getSoundOnCd() {
        return Sound.valueOf(plugin.getConfig().getString("Sound"));
    }

    /**
     * クールダウン中射撃時のボリュームを取得
     * @return
     */
    public float getVolumeOnCd() {
        return (float) plugin.getConfig().getDouble("Volume");
    }

    /**
     * クールダウン中の射撃時のピッチを取得
     * @return
     */
    public float getPitchOnCd() {
        return (float) plugin.getConfig().getDouble("Pitch");
    }

    /**
     * グループのクールダウンを取得
     * @param group グループ名
     * @return クールダウン
     */
    public int getGroupCooldown(String group) {
        return config.getInt("Groups."+group);
    }


    /**
     * グループに属している武器の名前をStringのListで取得
     * @param group グループ名
     * @return 武器名のList
     */
    public List<String> getWeapons(String group) {
        return config.getStringList(group);
    }


    /**
     * 武器の属するグループを取得
     * @param weapon
     * @return 武器の属するグループ名
     */
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


    /**
     * グループ作成
     * @param group
     * @param cooldown
     */
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


    /**
     * グループを削除
     * @param group
     */
    public void removeGroup(String group) {
        config.set("Groups."+group, null);
        config.set(group, null);

        plugin.groups.remove(group);
        plugin.groupCooldown.remove(group);

        saveConfig();

        plugin.sendDebugLogInfo("removed group: "+group);
    }


    /**
     * グループにクールダウン設定
     * @param group
     * @param cooldown
     */
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


    /**
     * グループに武器とそのクールダウンを設定
     * @param group
     * @param weapon
     * @throws InvalidConfigurationException groupが正しくない
     */
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


    /**
     * グループから武器を削除
     * @param group
     * @param weapon
     * @throws InvalidConfigurationException
     */
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


    /**
     * コンフィグファイルをセーブ
     */
    public void saveConfig() {
        plugin.saveConfig();
    }

    /**
     * コンフィグファイルをリロード
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        plugin.groups = getGroups();
        plugin.groupCooldown = getGroupCooldowns();

        plugin.sendDebugLogInfo("reloaded config");
    }


    /** 
     * 全てのグループとそのクールダウンをMapで取得
     * @return
     */
    public Map<String, Integer> getGroupCooldowns() {
        Map<String, Integer> groupCooldowns = new HashMap<>();

        // Groupsセクション内のkey: AR, value: 10として取得
        for(Map.Entry<String, Object> entry : config.getConfigurationSection("Groups").getValues(false).entrySet()) {
                groupCooldowns.put(entry.getKey(), (Integer)entry.getValue());
        }

        // Mapが入ったListを返す
        return groupCooldowns;
    }


    /** 
     * 全グループとそれに属する武器一覧を取得
     * @return
     */
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
