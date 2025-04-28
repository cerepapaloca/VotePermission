package ceres.vp;

import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public final class VotePermissionCore extends JavaPlugin implements Listener {

    private final HashSet<String> playersNames = new HashSet<>();
    @Getter
    private static VotePermissionCore instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Obtiene la instancia de luckPerms
        PluginCommand pluginCommand = getCommand("vote");
        if (pluginCommand != null) pluginCommand.setExecutor(new VoteCommand());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        playersNames.clear();
        // Plugin shutdown logic
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getVote().getUsername());
        if (player != null) {
            reward(player);
        }else {
            playersNames.add(event.getVote().getUsername());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        if (playersNames.contains(name)) {
            for (String playerName : playersNames) {
                if (name.equals(playerName)){
                    reward(event.getPlayer());
                    return;
                }
            }
        }
    }

    private void reward(Player player) {
        playersNames.remove(player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("lp user %s parent add vote", player.getName()));
    }

}
