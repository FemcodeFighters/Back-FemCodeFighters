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
}
