package kr.lucymc.here.lucy_fly;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Fly_TabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            if (sender instanceof Player player) {
                List<String> options = new ArrayList<>();
                if (player.hasPermission("lucyfly.staff")){
                    options.add("발급");
                    options.add("초기화");
                }
                options.add("남은시간");
                return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
            }
        }else if (args.length == 2 && args[0].equals("발급")){
            List<String> options = new ArrayList<>();
            options.add("갯수");
            return StringUtil.copyPartialMatches(args[1], options, new ArrayList<>());
        }else if (args.length == 3 && args[0].equals("발급")){
            List<String> options = new ArrayList<>();
            options.add("시");
            return StringUtil.copyPartialMatches(args[2], options, new ArrayList<>());
        }else if (args.length == 4 && args[0].equals("발급")){
            List<String> options = new ArrayList<>();
            options.add("분");
            return StringUtil.copyPartialMatches(args[3], options, new ArrayList<>());
        }else if (args.length == 5 && args[0].equals("발급")){
            List<String> options = new ArrayList<>();
            options.add("초");
            return StringUtil.copyPartialMatches(args[4], options, new ArrayList<>());
        }else if (args.length == 2 && args[0].equals("초기화")){
            List<String> options = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                options.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[1], options, new ArrayList<>());
        }
        return null;
    }
}
