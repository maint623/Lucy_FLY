package kr.lucymc.here.lucy_fly;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static kr.lucymc.here.lucy_fly.Fly_DB.FlyDel;
import static kr.lucymc.here.lucy_fly.Fly_DB.FlyInsert;


public final class Lucy_FLY extends JavaPlugin {
    public static Connection connection;
    public static HashMap<String, Integer> map = new HashMap<>();
    private static Lucy_FLY INSTANCE;
    public static Lucy_FLY getInstance() {
        return INSTANCE;
    }
    FileConfiguration config = this.getConfig();
    public static String TimeFormat(FileConfiguration config, String path, int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int Seconds = seconds % 60;
        List<String> CFT = List.of(Objects.requireNonNull(Objects.requireNonNull(config.getString(path)).split(",")));
        if(hours>0){
            return (CFT.get(0)+CFT.get(1)+CFT.get(2)).replace("%h%",""+hours).replace("%m%",""+minutes).replace("%s%",""+Seconds);
        }else{
            if(minutes>0){
                return (CFT.get(1)+CFT.get(2)).replace("%m%",""+minutes).replace("%s%",""+Seconds);
            }else{
                return (CFT.get(2)).replace("%s%",""+Seconds);
            }
        }
    }
    public static String FlyTimeMessage(FileConfiguration config, String path, int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int Seconds = seconds % 60;
        List<String> CFT = List.of(Objects.requireNonNull(Objects.requireNonNull(config.getString(path)).split(",")));
        if(hours>0){
            return (CFT.get(0)+CFT.get(1)+CFT.get(2)+CFT.get(3)+CFT.get(4)).replace("%h%",""+hours).replace("%m%",""+minutes).replace("%s%",""+Seconds);
        }else{
            if(minutes>0){
                return (CFT.get(0)+CFT.get(2)+CFT.get(3)+CFT.get(4)).replace("%m%",""+minutes).replace("%s%",""+Seconds);
            }else{
                return (CFT.get(0)+CFT.get(3)+CFT.get(4)).replace("%s%",""+Seconds);
            }
        }
    }

    //------------------------------------------------------------------------------------------
    @Override
    public void onEnable() {
        INSTANCE = this;
        File ConfigFile = new File(getDataFolder(), "config.yml");
        if(!ConfigFile.isFile()){
            config.addDefault("DB_ID", "root");
            config.addDefault("DB_PW", "INTY");
            config.addDefault("DB_URL", "jdbc:mysql://127.0.0.1:3307/lucy?autoReconnect=true");
            config.addDefault("NotHaveFlyTimeMessage", "[ 플라이 ] 플라이 시간이 부족합니다.");
            config.addDefault("HaveFlyTime", "[ 플라이 ] ,%h%시 ,%m%분 ,%s%초, 남음");
            config.addDefault("CouponPrefix", "[ 플라이 ] 플라이 쿠폰 ( ,%h%시 ,%m%분 ,%s%초, )");
            config.addDefault("CouponLore", "§f플라이 쿠폰,§f좌클릭 사용");
            config.addDefault("AddFlyTime", "[ 플라이 ] ,%h%시 ,%m%분 ,%s%초, 증가");
            config.addDefault("JoinFlyingMessage", "[ 플라이 ] 플라이가 켜져있습니다");
            config.addDefault("Fly_ON", "[ 플라이 ] 플라이를 켰습니다");
            config.addDefault("Fly_OFF", "[ 플라이 ] 플라이를 껐습니다");
            config.addDefault("doServerReloadKick", true);
            config.addDefault("ServerReloadKick", "서버 리로드중");
            config.addDefault("CheckFlyTime", "[ 플라이 ] ,%h%시 ,%m%분 ,%s%초, 남음");
            config.addDefault("TimeFormat", "%h%시 ,%m%분 ,%s%초");
            config.addDefault("H", "시");
            config.addDefault("M", "분");
            config.addDefault("S", "초");
            config.addDefault("DBLoadTick", 3);
            config.addDefault("ActionBar", false);
            config.addDefault("DeleteFlyTimeMessage", "[ 플라이 ] %a%님이 %p%님의 플라이 시간을 초기화 하였습니다.");
            config.addDefault("NotFindPlayerMessage", "[ 플라이 ] %p% 유저는 존재하지 않습니다.");
            config.addDefault("DeleteMyFlyTimeMessage", "[ 플라이 ] 플라이 시간을 초기화 하였습니다.");
            config.addDefault("CouponCountNotSet", "[ 플라이 ] 쿠폰 갯수를 입력해 주세요");
            config.addDefault("HNotSet", "[ 플라이 ] 시간를 입력해 주세요");
            config.addDefault("MNotSet", "[ 플라이 ] 분를 입력해 주세요");
            config.addDefault("SNotSet", "[ 플라이 ] 초를 입력해 주세요");
            config.options().copyDefaults(true);
            saveConfig();
        }
        getCommand("플라이").setExecutor(this);
        getCommand("플라이").setTabCompleter(new Fly_TabCompleter());
        getServer().getPluginManager().registerEvents(new Fly_Event(), this);
            try {
                connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("DB_URL")), config.getString("DB_ID"), config.getString("DB_PW"));
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Fly_PAPI(this).register();
        } else {
            getLogger().severe("PlaceholderAPI 플러그인을 찾을 수 없습니다.");
        }
    }

    @Override
    public void onDisable() {
        try { // using a try catch to catch connection errors (like wrong sql password...)
            if (connection != null && !connection.isClosed()) { // checking if connection isn't null to
                connection.close(); // closing the connection field variable.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if(player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.sendMessage(Objects.requireNonNull(config.getString("Fly_OFF")));
                }else{
                    if(map.get(player.getUniqueId().toString()) > 0|| player.hasPermission("lucyfly.infinity")) {
                        player.setAllowFlight(true);
                        player.sendMessage(Objects.requireNonNull(config.getString("Fly_ON")));
                    }else{
                        player.sendMessage(Objects.requireNonNull(config.getString("NotHaveFlyTimeMessage")));
                    }
                }
            }else if (player.hasPermission("lucyfly.staff")&&args[0].equalsIgnoreCase("발급")) {
                if(args.length == 1){
                    player.sendMessage(Objects.requireNonNull(config.getString("CouponCountNotSet")));
                }else if(args.length == 2){
                    player.sendMessage(Objects.requireNonNull(config.getString("HNotSet")));
                }else if(args.length == 3){
                    player.sendMessage(Objects.requireNonNull(config.getString("MNotSet")));
                }else if(args.length == 4){
                    player.sendMessage(Objects.requireNonNull(config.getString("SNotSet")));
                }else{
                    ItemStack Coupon = new ItemStack(Material.PAPER, Integer.parseInt(args[1]));
                    ItemMeta CouponMeta = Coupon.getItemMeta();
                    Objects.requireNonNull(CouponMeta).setDisplayName(FlyTimeMessage(config,"CouponPrefix",Integer.parseInt(args[2]) * 3600 + Integer.parseInt(args[3]) * 60 + Integer.parseInt(args[4])));
                    CouponMeta.setLore(List.of(Objects.requireNonNull(config.getString("CouponLore")).split(",")));
                    Coupon.setItemMeta(CouponMeta);
                    player.getInventory().addItem(Coupon);
                }
            }else if (player.hasPermission("lucyfly.staff")&&args[0].equalsIgnoreCase("초기화")) {
                if(args.length == 1){
                    FlyDel(player.getUniqueId());
                    map.remove(player.getUniqueId().toString());
                    player.sendMessage(Objects.requireNonNull(config.getString("DeleteMyFlyTimeMessage")));
                    FlyInsert(player.getUniqueId(),0);
                    map.put(""+player.getUniqueId(),0);
                }else{
                    Player inplayer = Bukkit.getPlayerExact(args[1]);
                    if(inplayer != null){
                        if(player!=inplayer){
                            FlyDel(inplayer.getUniqueId());
                            map.remove(inplayer.getUniqueId().toString());
                            player.sendMessage(Objects.requireNonNull(config.getString("DeleteFlyTimeMessage")).replace("%a%",player.getDisplayName()).replace("%p%",inplayer.getDisplayName()));
                            inplayer.sendMessage(Objects.requireNonNull(config.getString("DeleteFlyTimeMessage")).replace("%a%",player.getDisplayName()).replace("%p%",inplayer.getDisplayName()));
                            FlyInsert(inplayer.getUniqueId(),0);
                            map.put(""+inplayer.getUniqueId(),0);
                        }else{
                            FlyDel(player.getUniqueId());
                            map.remove(player.getUniqueId().toString());
                            player.sendMessage(Objects.requireNonNull(config.getString("DeleteMyFlyTimeMessage")));
                            FlyInsert(player.getUniqueId(),0);
                            map.put(""+player.getUniqueId(),0);
                        }
                    }else{
                        player.sendMessage(Objects.requireNonNull(config.getString("NotFindPlayerMessage")).replace("%p%",inplayer.getDisplayName()));
                    }
                }
            }else if(args[0].equalsIgnoreCase("남은시간")) {
                //FlyUpdate(player.getUniqueId(),map.get(""+player.getUniqueId()));
                player.sendMessage(FlyTimeMessage(config,"CheckFlyTime",map.get(""+player.getUniqueId())));
            }
        }
        return true;
    }
}
