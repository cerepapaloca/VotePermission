package ceres.vp;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public final class VotePermissionCore extends JavaPlugin implements Listener {

    private static LuckPerms lp;
    private final HashSet<String> playersNames = new HashSet<>();

    @Override
    public void onEnable() {
        // Obtiene la instancia de luckPerms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }else throw new RuntimeException("LuckPerms not found");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        playersNames.clear();
        // Plugin shutdown logic
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getEventName());
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
        lp.getUserManager().modifyUser(player.getUniqueId(), user -> user.data().add(InheritanceNode.builder("vote").build()));
    }

}
