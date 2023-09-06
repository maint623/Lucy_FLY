package kr.lucymc.here.lucy_fly;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kr.lucymc.here.lucy_fly.Fly_DB.*;
import static kr.lucymc.here.lucy_fly.Lucy_FLY.FlyTimeMessage;
import static kr.lucymc.here.lucy_fly.Lucy_FLY.map;
import static org.bukkit.Bukkit.getLogger;

public class Fly_Event implements Listener {
    private BukkitRunnable task;
    FileConfiguration config = Lucy_FLY.getInstance().getConfig();
    boolean ActionBar = config.getBoolean("ActionBar");
    public static int getTimeToString(String timeString, FileConfiguration config) {
        String[] timeArray = timeString.split(config.getString("H")+"|"+config.getString("M")+"|"+config.getString("S"));
        int hour = parseInteger(timeArray[0].toLowerCase().replace(List.of(config.getString("CouponPrefix").split(",")).get(0).toLowerCase(),""));
        int minute = parseInteger(timeArray[1]);
        if(!timeString.contains(config.getString("H")+"")){
            if(!timeString.contains(config.getString("M")+"")){
                return hour;
            }else{
                return hour * 60 + minute;
            }
        }else{
            int second = parseInteger(timeArray[2]);
            return hour * 3600 + minute * 60 + second;
        }

    }
    private static int parseInteger(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if(event.isFlying()) {
            String player = event.getPlayer().getUniqueId().toString();
            if(event.getPlayer().hasPermission("lucyfly.infinity")||event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            if(task == null) {
                task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!map.containsKey("" + event.getPlayer().getUniqueId()) || event.getPlayer().isDead()|| event.getPlayer().isSleeping() || event.getPlayer().isInsideVehicle() || !event.getPlayer().getAllowFlight()) {
                            task.cancel();
                            task = null;
                            event.getPlayer().setFlying(false);
                        } else {
                            if (map.get(player) > 0) {
                                map.put(player, map.get(player) - 1);
                                if(ActionBar){
                                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(FlyTimeMessage(config,"HaveFlyTime",map.get(""+event.getPlayer().getUniqueId()))));
                                }
                            } else {
                                task.cancel();
                                event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("NotHaveFlyTimeMessage")));
                                event.getPlayer().setFlying(false);
                                event.getPlayer().setAllowFlight(false);
                            }
                        }
                    }
                };
                task.runTaskTimer(Lucy_FLY.getInstance(),0L, 20L);
            }
        }else{
            if(task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    @EventHandler
    private void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getHand() == EquipmentSlot.HAND) {
                if(player.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                    if(player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                        if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toLowerCase().contains(List.of(config.getString("CouponPrefix").split(",")).get(0).toLowerCase())) {
                            int number = 0;
                            try {
                                number = getTimeToString(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName(), config);
                            } catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                            ItemStack cloned = player.getInventory().getItemInMainHand().clone();
                            cloned.setAmount(1);
                            player.getInventory().removeItem(cloned);

                            player.sendMessage(FlyTimeMessage(config, "AddFlyTime", number));
                            map.put("" + player.getUniqueId(), map.get("" + player.getUniqueId()) + number);
                            FlyUpdate(player.getUniqueId(), map.get("" + player.getUniqueId()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        String tableName = "fly";
        String columnName = "UserID";
        String value = ""+player.getUniqueId();
        boolean dataExists = isDataExists(tableName, columnName, value);
        if(!dataExists){
            FlyInsert(player.getUniqueId(),0);
            map.put(""+player.getUniqueId(),0);
        }else{
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Lucy_FLY.getInstance(),new Runnable() {public void run() {
                map.put(""+player.getUniqueId(),FlySelect(player.getUniqueId()));
            }}, config.getInt("DBLoadTick"));
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //FlyUpdate(player.getUniqueId(),map.get(""+player.getUniqueId()));
        if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        FlyUpdate(player.getUniqueId(),map.get(""+player.getUniqueId()));
        map.remove(""+player.getUniqueId());
    }
}

