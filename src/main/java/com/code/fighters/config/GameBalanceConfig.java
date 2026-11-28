package com.code.fighters.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GameBalanceConfig {

    public static final int FRIDAY_DEPLOY_HEAL = 30;

    public static final int SPAGHETTI_TICK_DAMAGE = 5;
    public static final int SPAGHETTI_DURATION_MS = 5000;
    public static final int SPAGHETTI_TICK_COUNT = 12;
    public static final int SPAGHETTI_TICK_INTERVAL_MS = 400;

    public static final int GIT_CLONE_DAMAGE = 35;
    public static final double CLONE_DAMAGE_RATIO = 0.2;
    public static final int CLONE_LIFESPAN_MS = 6000;

    public static final int GLOBAL_ULTIMATE_COOLDOWN_MS = 20000;
    public static final double FRIDAY_DEPLOY_FAIL_CHANCE = 0.5;
    public static final int FRIDAY_DEPLOY_FAIL_DAMAGE = 20;
    public static final int SPAGHETTI_ALLY_TICK_DAMAGE = 1;
    public static final double GIT_CLONE_FRIENDLY_FIRE_CHANCE = 0.1;

    public static final int COOP_ENEMY_COUNT = 6;
    public static final int COOP_ENEMY_BASE_HEALTH = 500; // para test ponerlo en 10
    public static final int COOP_ENEMY_HEALTH_PER_STAGE = 100; // para test ponerlo en 10
    public static final int COOP_ENEMY_BASE_DAMAGE = 5;
    public static final int COOP_ENEMY_DAMAGE_PER_STAGE = 1;
    public static final int ENEMY_KILL_HEAL = 30;

    public static final int ENEMY_RANGED_DAMAGE = 15;
    public static final double ENEMY_RANGED_CHANCE = 0.3;
    public static final int ENEMY_RANGED_COOLDOWN_MS = 1000;
    public static final int ENEMY_PROJECTILE_LIFE_MS = 1500;
    public static final int ENEMY_ULT_MAX_ENERGY = 100;
    public static final int ENEMY_ULT_MELEE_GAIN = 8;
    public static final int ENEMY_ULT_RANGED_GAIN = 12;
    public static final int ENEMY_ULT_RADIUS = 250;
    public static final int[] ENEMY_ULTIMATE_DAMAGE = { 25, 30, 40, 45, 55, 70 };
}
