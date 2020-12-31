package io.github.hirom320.gunswapcooldown;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class GunSwapCooldown extends JavaPlugin {

    private boolean debug = false;

    public CSUtility cs = new CSUtility();
    ConfigManager configManager;

    public Map<Player, Integer> cooldowns = new HashMap<>();

    // <グループ名, 武器一覧>
    public Map<String, ArrayList<String>> groups = new HashMap<>();
    // <グループ名, クールダウン>
    public Map<String, Integer> groupCooldown = new HashMap<>();

    @Override
    public void onEnable() {

        // 時間計測用
        long start = System.currentTimeMillis();

        // config.ymlが存在しない場合はjarの中身をファイルに出力
        saveDefaultConfig();

        configManager = new ConfigManager(this);

        groups = configManager.getGroups();
        groupCooldown = configManager.getGroupCooldowns();

        getLogger().info("Groups: ");
        for(Map.Entry<String, Integer> entry : groupCooldown.entrySet()) {
            getLogger().info("  "+entry.getKey()+": "+entry.getValue());
        }

        for(Map.Entry<String, ArrayList<String>> entry : groups.entrySet()) {
            getLogger().info(entry.getKey()+": ");
            for(String gunName : entry.getValue()) {
                getLogger().info("  - "+gunName);
            }
        }


        getServer().getPluginManager().registerEvents(new ItemHeldListener(this), this);
        getServer().getPluginManager().registerEvents(new PrepareShootListener(this), this);

        getCommand("gsc").setExecutor(new CommandListener(this));

        // 1tickごとに動作するスケジューラーの登録
        new CooldownScheduler(this).runTaskTimer(this, 1, 1);

        // 読み込みにかかった時間を表示
        getLogger().info("loading time: "+(System.currentTimeMillis()-start)+"[ms]");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean toggleDebug() {
        debug = !debug;
        return debug;
    }

    public void sendDebugLogInfo(String msg) {
        if(debug) {
            getLogger().info(msg);
        }
    }
}
