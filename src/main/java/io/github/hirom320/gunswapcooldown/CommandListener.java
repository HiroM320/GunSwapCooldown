package io.github.hirom320.gunswapcooldown;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandListener implements CommandExecutor {

    GunSwapCooldown plugin;
    CSUtility cs;
    ConfigManager configManager;

    public CommandListener(GunSwapCooldown plugin) {
        this.plugin = plugin;
        this.cs = plugin.cs;
        this.configManager = plugin.configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {

        String permission = "GunSwapCooldown";

        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".help"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".help");
                return false;
            }

            sender.sendMessage("[GSC] /gsc help");
            sender.sendMessage("[GSC] コマンド一覧を表示");
            sender.sendMessage("[GSC] /gsc creategroup [group] [ticks]");
            sender.sendMessage("[GSC] [group]を作成し、クールダウンを[ticks]に設定します");
            sender.sendMessage("[GSC] /gsc removegroup [group] ");
            sender.sendMessage("[GSC] [group]を削除します");
            sender.sendMessage("[GSC] /gsc setcooldown [group] [ticks]");
            sender.sendMessage("[GSC] [group]のクールダウンを[ticks]に設定します");
            sender.sendMessage("[GSC] /gsc addweapon [group] ");
            sender.sendMessage("[GSC] 手に持っている武器を[group]に追加します");
            sender.sendMessage("[GSC] /gsc removeweapon [group] ");
            sender.sendMessage("[GSC] 手に持っている武器を[group]から削除します");
            sender.sendMessage("[GSC] /gsc get groups ");
            sender.sendMessage("[GSC] 全グループとそれぞれのクールダウンを表示します");
            sender.sendMessage("[GSC] /gsc get guns [group] ");
            sender.sendMessage("[GSC] [group]に登録されている武器を全て表示します");
            sender.sendMessage("[GSC] /gsc reload");
            sender.sendMessage("[GSC] コンフィグを再読込します");
            sender.sendMessage("[GSC] /gsc debug");
            sender.sendMessage("[GSC] デバッグモードのON/OFFをトグルします");

            return true;
        }

        if(args[0].equalsIgnoreCase("get")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".get"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".get");
                return false;
            }
            if(args.length<2) {
                sender.sendMessage("[GSC] groups, guns");
                return true;
            }

            if(args[1].equalsIgnoreCase("groups")) {
                sender.sendMessage("[GSC] Groups: ");
                for(Map.Entry<String, Integer> entry : plugin.groupCooldown.entrySet()) {
                    sender.sendMessage("[GSC]   "+entry.getKey()+": "+entry.getValue());
                }
                return true;
            }

            if(args[1].equalsIgnoreCase("guns")) {
                if(args.length<3) {
                    sender.sendMessage("[GSC] グループが指定されていません");
                    return true;
                }
                sender.sendMessage("[GSC] "+args[2]);
                for(String gunName : plugin.groups.get(args[2])) {
                    sender.sendMessage("[GSC]   - "+gunName);
                }
                return true;
            }
        }



        if(args[0].equalsIgnoreCase("creategroup")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".creategroup"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".creategroup");
                return false;
            }
            if(args.length<3) {
                sender.sendMessage("[GSC] クールダウンが入力されていません");
                return true;
            }

            try {
                configManager.createGroup(args[1], Integer.parseInt(args[2]));
                sender.sendMessage("[GSC] グループ"+args[1]+": "+args[2]+" を作成しました");
            } catch(NumberFormatException e) {
                sender.sendMessage("[GSC] クールダウンを指定してください");
                return true;
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("removegroup")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".removegroup"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".removegroup");
                return false;
            }

            configManager.removeGroup(args[1]);
            return true;
        }

        if(args[0].equalsIgnoreCase("setcooldown")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".setcooldown"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".setcooldown");
                return false;
            }
            try {
                configManager.setGroupCooldown(args[1], Integer.parseInt(args[2]));
            } catch(NumberFormatException e) {
                sender.sendMessage("[GSC] クールダウンを指定してください");
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("addweapon")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".add"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".add");
                return false;
            }
            if(!(sender instanceof Player)) {
                sender.sendMessage("[GSC] プレイヤーからのみ実行してください");
                return true;
            }

            Player player = (Player) sender;
            String weaponName = cs.getWeaponTitle(player.getEquipment().getItemInMainHand());

            if(weaponName==null) {
                sender.sendMessage("[GSC] CrackShotのアイテムを持って実行してください");
            }

            try {
                configManager.addWeapon(args[1], weaponName);
            } catch(InvalidConfigurationException e) {
                sender.sendMessage("[GSC] エラー: "+e.toString());
                return true;
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("removeweapon")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".remove"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".remove");
                return false;
            }
            if(!(sender instanceof Player)) {
                sender.sendMessage("[GSC] プレイヤーからのみ実行してください");
                return true;
            }

            Player player = (Player) sender;
            String weaponName = cs.getWeaponTitle(player.getEquipment().getItemInMainHand());

            if(weaponName==null) {
                sender.sendMessage("[GSC] CrackShotのアイテムを持って実行してください");
            }

            try {
                configManager.removeWeapon(args[1], weaponName);
            } catch(NumberFormatException | InvalidConfigurationException e) {
                sender.sendMessage("[GSC] エラー: "+e.toString());
                return true;
            }
            return true;
        }



        if(args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".reload"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".reload");
                return false;
            }

            configManager.reloadConfig();

            return true;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            if (sender instanceof Player && !(sender.hasPermission(permission + ".debug"))) {
                sender.sendMessage("[GSC] need permission:  " + permission + ".debug");
                return false;
            }

            sender.sendMessage("[GSC] debug: "+plugin.toggleDebug());

            return true;
        }

        return false;
    }
}
