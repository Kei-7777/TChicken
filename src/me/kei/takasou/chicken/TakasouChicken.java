package me.kei.takasou.chicken;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class TakasouChicken extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        new TakasouChickenTask(this).runTaskTimer(this, 2, 10);
    }

    @Override
    public void onDisable() {
        TakasouChickenTask.disable();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        if(e.getEntity() instanceof Chicken){
            LivingEntity chicken = e.getEntity();
            if(chicken.getKiller() == null) return;
            if(this.getConfig().getStringList("users").contains(chicken.getKiller().getName())){
                e.getDrops().clear();
                e.setDroppedExp(0);
            }
        }
    }
}
