package com.code.fighters.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GameBalanceConfig {

   // Friday Deploy: Curación moderada
    public static final int FRIDAY_DEPLOY_HEAL = 30;
    
    // Spaghetti Code: Daño por "tick" (cada vez que el código toca al enemigo)
    public static final int SPAGHETTI_TICK_DAMAGE = 5;
    public static final int SPAGHETTI_DURATION_MS = 5000;

    // Git Clone: Multiplicador de daño bajo para el clon
    public static final double CLONE_DAMAGE_RATIO = 0.2; 
    public static final int CLONE_LIFESPAN_MS = 6000;
    
    // Cooldown general para todas las Ultimates
    public static final int GLOBAL_ULTIMATE_COOLDOWN_MS = 20000; // 20 segundos
}
