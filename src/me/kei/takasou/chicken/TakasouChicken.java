package me.kei.takasou.chicken;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class TakasouChicken extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new TakasouChickenTask(this).runTaskTimer(this, 2, 2);
    }

    @Override
    public void onDisable() {
        TakasouChickenTask.disable();
    }
}
