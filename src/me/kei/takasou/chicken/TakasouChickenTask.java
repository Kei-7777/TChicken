package me.kei.takasou.chicken;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakasouChickenTask extends BukkitRunnable {
    TakasouChicken plugin;
    Map<String, Integer> killed;
    public static Map<String, BossBar> bar;
    Map<String, Integer> stats;


    public TakasouChickenTask(TakasouChicken takasouChicken) {
        this.plugin = takasouChicken;
        killed = new HashMap<>();
        bar = new HashMap<>();
        stats = new HashMap<>();
    }

    static void disable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Map.Entry<String, BossBar> entry : bar.entrySet()) {
                entry.getValue().removePlayer(p);
            }
        }
    }

    @Override
    public void run() {
        for (String s : plugin.getConfig().getStringList("users")) {
            if (Bukkit.getPlayer(s) != null) {
                if (Bukkit.getPlayer(s).isOnline()) {
                    double range = plugin.getConfig().getDouble("range");


                    // Nishito Particle 一人しかいないのにParticle 5000行くので却下
                    /*
                    List<Location> circle = getCircle(Bukkit.getPlayer(s).getLocation(), range, 12);
                        List<List<Location>> doubleLocation = new ArrayList<>();
                        doubleLocation.add(getCircle(circle.get(0), 3, 40));
                        doubleLocation.add(getCircle(circle.get(2), 3, 40));
                        doubleLocation.add(getCircle(circle.get(4), 3, 40));
                        doubleLocation.add(getCircle(circle.get(6), 3, 40));
                        doubleLocation.add(getCircle(circle.get(8), 3, 40));
                        doubleLocation.add(getCircle(circle.get(10), 3, 40));

                        for(List<Location> locs : doubleLocation){
                            for(Location l2 : locs){
                                l2.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l2, 1);
                            }
                        }

                        for(Location locs : getCircle(Bukkit.getPlayer(s).getLocation(), 1.5, 32)){
                            locs.getWorld().spawnParticle(Particle.FLAME, locs.add(0,0.2,0), 0, 1, 0, 0, 0);
                        }*/

                    for (LivingEntity le : Bukkit.getPlayer(s).getLocation().getNearbyLivingEntities(range)) {
                        if (le instanceof Chicken) {
                            if (!le.isDead()) {
                                Player p = Bukkit.getPlayer(s);
                                final Location loc = le.getLocation();
                                List<Location> locations = calc(loc, p.getEyeLocation().clone().add(0, -1, 0), 0.2);
                                new BukkitRunnable() {
                                    Location loc2 = loc.clone();

                                    @Override
                                    public void run() {
                                        List<Location> locations = calc(loc2, p.getEyeLocation().clone().add(0, -1, 0), 0.2);
                                        Location l = locations.get(1);
                                        l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l, 1);
                                        if (locations.size() > 1) {
                                            l = locations.get(2);
                                            l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l, 1);
                                        }
                                        loc2 = l;
                                        if (l.getBlockX() == p.getLocation().getBlockX() && l.getBlockZ() == p.getLocation().getBlockZ() && l.getBlockY() == p.getLocation().getBlockY()) {
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(this.plugin, 1, 1);

                                List<Location> leLocations = calc(p.getLocation(), le.getLocation(), 1);
                                leLocations.remove(0);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Location l = leLocations.get(0);
                                        //l.getWorld().spawnEntity(l, EntityType.EVOKER_FANGS).setSilent(true);
                                        leLocations.remove(0);
                                        if (leLocations.size() < 1) {
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(this.plugin, 1, 1);


                                int power = 0;
                                if (p.getPotionEffect(PotionEffectType.HEALTH_BOOST) != null) {
                                    power = p.getPotionEffect(PotionEffectType.HEALTH_BOOST).getAmplifier() + 1;
                                    p.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                }

                                if (power > 127) {
                                    power = 127;
                                }

                                p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, power, true));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, power, true));

                                if (killed.containsKey(s)) {
                                    killed.replace(s, killed.get(s) + 1);
                                } else {
                                    killed.put(s, 1);
                                }

                                le.damage(10000, Bukkit.getPlayer(s));
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : killed.entrySet()) {
            String name = entry.getKey();
            int kill = entry.getValue();

            BossBar boss;
            if (bar.containsKey(name)) {
                boss = bar.get(name);
            } else {
                boss = Bukkit.createBossBar(name + "が鶏を吸収した数: " + kill, BarColor.WHITE, BarStyle.SEGMENTED_10);
                bar.put(name, boss);
            }

            BarColor color = BarColor.WHITE;
            if (kill <= 100) {
                color = BarColor.WHITE;
            } else if (kill <= 200) {
                color = BarColor.PURPLE;
            } else if (kill <= 300) {
                color = BarColor.BLUE;
            } else if (kill <= 400) {
                color = BarColor.GREEN;
            } else if (kill <= 500) {
                color = BarColor.YELLOW;
            } else if (kill <= 600) {
                color = BarColor.PINK;
            } else if (kill <= 700) {
                color = BarColor.RED;
            } else {
                color = BarColor.RED;
            }

            boss.setTitle(name + "が鳥を吸収した数: " + kill);
            boss.setColor(color);

            double count100 = (double) Math.floor(kill / 100);
            boss.setProgress((kill - (100 * count100)) / 100);

            for (Player p : Bukkit.getOnlinePlayers()) {
                boss.addPlayer(p);
            }
        }
    }

    public static List<Location> calc(Location loc1, Location loc2, double distanceBetween) throws IllegalArgumentException {
        if (loc1.getWorld() != loc2.getWorld() || distanceBetween <= 0) {
            throw new IllegalArgumentException();
        }

        List<Location> locations = new ArrayList<>();
        loc1 = loc1.clone();
        loc2 = loc2.clone();

        Vector v = adjustExactDistance(loc2.clone().subtract(loc1.clone()).toVector().normalize(), distanceBetween);
        Location firstLocation1 = loc1.clone();
        while (firstLocation1.distance(loc2) >= firstLocation1.distance(loc1)) {
            locations.add(loc1.clone());
            loc1.add(v);
        }

        return locations;
    }

    private static Vector adjustExactDistance(Vector v, double length) {
        return v.multiply(length / v.length());
    }

    public ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
}
