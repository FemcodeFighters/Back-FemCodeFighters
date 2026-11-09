package com.code.fighters.model;

import com.code.fighters.config.GameBalanceConfig;
import com.code.fighters.dto.coop.CoopGameState;
import com.code.fighters.dto.coop.EnemyState;
import com.code.fighters.dto.coop.PlayerState;
import com.code.fighters.dto.coop.ProjectileState;
import com.code.fighters.entity.enums.UltimateSkill;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Slf4j
public class GameRoom {

    // Debe coincidir con @Scheduled(fixedRate = 50) de GameLoopServiceImpl
    private static final int TICK_MS = 50;
    private static final int RANGED_DAMAGE = 10;
    private static final float RANGED_RANGE = 450f;
    // Tiempos que coinciden con los efectos visuales de Player.js
    private static final int SPAGHETTI_START_DELAY_MS = 500;
    private static final int CLONE_ARRIVAL_MS = 620;
    private static final int INTERMISSION_MS = 3500;
    // Ataques del enemigo
    private static final float ENEMY_RANGED_MIN_DIST = 100f;
    private static final float ENEMY_RANGED_MAX_DIST = 400f;
    private static final float PROJECTILE_SPEED = 25f; // por tick
    private static final float PROJECTILE_HIT_X = 45f;
    private static final float PROJECTILE_HIT_Y = 70f;
    private static final float ENEMY_ULT_TRIGGER_DIST = 150f;
    private static final int ENEMY_ULT_WINDUP_MS = 600;

    private final String roomId;
    private final LocalDateTime startedAt;
    private final int maxPlayers;
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();
    private final Map<String, UltimateSkill> ultimates = new ConcurrentHashMap<>();
    private final Map<String, String> ultimateOutcomes = new ConcurrentHashMap<>();
    private final Set<String> rangedThisTick = ConcurrentHashMap.newKeySet();
    @Getter(lombok.AccessLevel.NONE)
    private final List<TimedDamage> timedDamages = new CopyOnWriteArrayList<>();
    @Getter(lombok.AccessLevel.NONE)
    private final List<EnemyProjectile> projectiles = new CopyOnWriteArrayList<>();
    private EnemyState enemy;
    private GamePhase phase = GamePhase.WAITING;
    private long tick = 0;
    private int stage = 0;
    private int intermissionTicks = 0;
    private int nextProjectileId = 0;
    private int rangedCooldownTicks = 0;
    private int enemyEnergy = 0;
    private int enemyUltCount = 0;
    private int ultWindupTicks = 0;

    private final Queue<PlayerInput> inputQueue = new ConcurrentLinkedQueue<>();

    public GameRoom(int maxPlayers) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.startedAt = LocalDateTime.now();
        this.maxPlayers = maxPlayers;
        this.enemy = enemyForStage(0);
    }

    public boolean addPlayer(String playerId) {
        if (players.size() >= maxPlayers)
            return false;
        if (phase != GamePhase.WAITING)
            return false;
        players.put(playerId, new PlayerState(playerId));
        return true;
    }

    public void setUltimate(String playerId, UltimateSkill skill) {
        ultimates.put(playerId, skill);
    }

    public boolean removePlayer(String playerId) {
        ultimates.remove(playerId);
        ultimateOutcomes.remove(playerId);
        return players.remove(playerId) != null;
    }

    public void startGame() {
        if (phase == GamePhase.WAITING) {
            this.phase = GamePhase.PLAYING;
        }
    }

    public void receiveInput(PlayerInput input) {
        if (input != null) {
            this.inputQueue.offer(input);
        }
    }

    public void processInputs() {
        if (phase != GamePhase.PLAYING)
            return;

        PlayerInput input;
        while ((input = inputQueue.poll()) != null) {
            PlayerState player = players.get(input.playerId());
            if (player != null && player.alive()) {
                players.put(input.playerId(), applyInput(player, input));
            }
        }
        for (String id : players.keySet()) {
            players.put(id, players.get(id).updatePhysics());
        }
        tick++;
    }

    private PlayerState applyInput(PlayerState player, PlayerInput input) {
        return switch (input.action()) {
            case "MOVE_LEFT" -> player.move(-5, false);
            case "MOVE_RIGHT" -> player.move(5, true);
            case "JUMP" -> !player.jumping() ? player.jump(-15) : player;
            case "ATTACK" -> player.setCombatState(true, false);
            case "RANGED" -> {
                rangedThisTick.add(player.playerId());
                yield player.setCombatState(true, false);
            }
            case "ULTIMATE" -> applyUltimate(player);
            case "IDLE" -> player;
            default -> player;
        };
    }

    private PlayerState applyUltimate(PlayerState caster) {
        UltimateSkill skill = ultimates.getOrDefault(caster.playerId(), UltimateSkill.FRIDAY_DEPLOY);
        switch (skill) {
            case FRIDAY_DEPLOY -> {
                boolean fails = ThreadLocalRandom.current().nextDouble()
                        < GameBalanceConfig.FRIDAY_DEPLOY_FAIL_CHANCE;
                ultimateOutcomes.put(caster.playerId(), fails ? "DEPLOY_FAIL" : "DEPLOY_OK");
                for (String id : players.keySet()) {
                    PlayerState mate = players.get(id);
                    if (!id.equals(caster.playerId()) && mate.alive()) {
                        players.put(id, fails
                                ? mate.takeDamage(GameBalanceConfig.FRIDAY_DEPLOY_FAIL_DAMAGE)
                                : mate.heal(GameBalanceConfig.FRIDAY_DEPLOY_HEAL));
                    }
                }
                caster = fails
                        ? caster.takeDamage(GameBalanceConfig.FRIDAY_DEPLOY_FAIL_DAMAGE)
                        : caster.heal(GameBalanceConfig.FRIDAY_DEPLOY_HEAL);
            }
            case SPAGHETTI_CODE -> timedDamages.add(new TimedDamage(
                    caster.playerId(),
                    null,
                    GameBalanceConfig.SPAGHETTI_TICK_DAMAGE,
                    GameBalanceConfig.SPAGHETTI_ALLY_TICK_DAMAGE,
                    // +1 porque el efecto visual repite tickCount veces después del primer golpe
                    GameBalanceConfig.SPAGHETTI_TICK_COUNT + 1,
                    (SPAGHETTI_START_DELAY_MS + GameBalanceConfig.SPAGHETTI_TICK_INTERVAL_MS) / TICK_MS,
                    GameBalanceConfig.SPAGHETTI_TICK_INTERVAL_MS / TICK_MS));
            case GIT_CLONE -> castGitClone(caster.playerId());
        }
        return caster.setCombatState(false, true);
    }

    private void castGitClone(String casterId) {
        List<String> allies = players.entrySet().stream()
                .filter(e -> !e.getKey().equals(casterId) && e.getValue().alive())
                .map(Map.Entry::getKey)
                .toList();
        boolean friendlyFire = !allies.isEmpty()
                && ThreadLocalRandom.current().nextDouble()
                        < GameBalanceConfig.GIT_CLONE_FRIENDLY_FIRE_CHANCE;
        String victim = friendlyFire
                ? allies.get(ThreadLocalRandom.current().nextInt(allies.size()))
                : null;
        ultimateOutcomes.put(casterId, friendlyFire ? "CLONE_ALLY:" + victim : "CLONE_ENEMY");
        timedDamages.add(new TimedDamage(
                casterId,
                victim,
                GameBalanceConfig.GIT_CLONE_DAMAGE,
                0,
                1,
                CLONE_ARRIVAL_MS / TICK_MS,
                0));
    }

    public void updateEnemy() {
        if (phase != GamePhase.PLAYING || !enemy.alive())
            return;

        // Mientras carga su ultimate el enemigo se queda quieto (el área ya está avisada)
        if (ultWindupTicks > 0) {
            updateEnemyUltimate();
            return;
        }

        players.values().stream()
                .filter(PlayerState::alive)
                .min((a, b) -> Double.compare(distanceTo(a), distanceTo(b)))
                .ifPresent(target -> {
                    float dx = target.x() - enemy.x();
                    if (Math.abs(dx) > 10) {
                        float moveX = enemy.x() + (dx > 0 ? 3 : -3);
                        this.enemy = enemy.move(moveX, dx > 0);
                    }
                    this.enemy = enemy.tryAttack(target, tick);
                    enemyRangedAttack(target);
                });

        updateEnemyUltimate();

        if (!players.isEmpty() && players.values().stream().noneMatch(PlayerState::alive)) {
            this.phase = GamePhase.LOSE;
        }
    }

    private void enemyRangedAttack(PlayerState target) {
        if (rangedCooldownTicks > 0) {
            rangedCooldownTicks--;
            return;
        }
        float dx = target.x() - enemy.x();
        float dist = Math.abs(dx);
        // Cada ~0,4 s decide si dispara, solo si la jugadora no está al alcance del golpe
        if (tick % 8 != 0 || dist < ENEMY_RANGED_MIN_DIST || dist > ENEMY_RANGED_MAX_DIST)
            return;
        if (ThreadLocalRandom.current().nextDouble() >= GameBalanceConfig.ENEMY_RANGED_CHANCE)
            return;
        projectiles.add(new EnemyProjectile(nextProjectileId++, enemy.x(), enemy.y(), dx > 0));
        rangedCooldownTicks = GameBalanceConfig.ENEMY_RANGED_COOLDOWN_MS / TICK_MS;
    }

    private void updateEnemyUltimate() {
        if (ultWindupTicks > 0) {
            if (--ultWindupTicks == 0) {
                detonateEnemyUltimate();
            }
            return;
        }
        if (enemyEnergy < GameBalanceConfig.ENEMY_ULT_MAX_ENERGY)
            return;
        boolean someoneClose = players.values().stream()
                .anyMatch(p -> p.alive() && Math.abs(p.x() - enemy.x()) < ENEMY_ULT_TRIGGER_DIST);
        if (!someoneClose)
            return;
        enemyEnergy = 0;
        enemyUltCount++;
        ultWindupTicks = ENEMY_ULT_WINDUP_MS / TICK_MS;
    }

    private void detonateEnemyUltimate() {
        int[] damages = GameBalanceConfig.ENEMY_ULTIMATE_DAMAGE;
        int damage = damages[Math.min(stage, damages.length - 1)];
        for (String id : players.keySet()) {
            PlayerState p = players.get(id);
            if (p.alive() && Math.abs(p.x() - enemy.x()) < GameBalanceConfig.ENEMY_ULT_RADIUS) {
                players.put(id, p.takeDamage(damage));
            }
        }
    }

    private void gainEnemyEnergy(int amount) {
        enemyEnergy = Math.min(GameBalanceConfig.ENEMY_ULT_MAX_ENERGY, enemyEnergy + amount);
    }

    public void checkCollisions() {
        if (phase != GamePhase.PLAYING)
            return;

        for (String pid : players.keySet()) {
            PlayerState p = players.get(pid);
            if (!p.alive())
                continue;

            if (enemy.alive() && enemy.isAttackHitting(p) && enemy.lastAttackTick() == tick) {
                float pushX = (p.x() > enemy.x()) ? 60f : -60f;
                p = p.takeDamage(enemy.damage()).move(pushX, p.facingRight());
                gainEnemyEnergy(GameBalanceConfig.ENEMY_ULT_MELEE_GAIN);
            }

            if (p.attacking() && enemy.alive()) {
                boolean ranged = rangedThisTick.contains(pid);
                boolean hit = ranged ? isRangedHit(p) : p.isHitting(enemy);
                int dmg = ranged ? RANGED_DAMAGE : p.getAttackDamage();
                if (hit) {
                    this.enemy = enemy.takeDamage(dmg);
                    p = p.addDamageDealt(dmg);
                }
            }
            players.put(pid, p);
        }

        moveProjectiles();
        resolveTimedDamage();
        advanceStage();
    }

    private void moveProjectiles() {
        projectiles.removeIf(pr -> {
            pr.x += pr.facingRight ? PROJECTILE_SPEED : -PROJECTILE_SPEED;
            if (--pr.ticksLeft <= 0)
                return true;
            for (String id : players.keySet()) {
                PlayerState p = players.get(id);
                if (p.alive()
                        && Math.abs(p.x() - pr.x) < PROJECTILE_HIT_X
                        && Math.abs(p.y() - pr.y) < PROJECTILE_HIT_Y) {
                    players.put(id, p.takeDamage(GameBalanceConfig.ENEMY_RANGED_DAMAGE));
                    gainEnemyEnergy(GameBalanceConfig.ENEMY_ULT_RANGED_GAIN);
                    return true;
                }
            }
            return false;
        });
    }

    private void resolveTimedDamage() {
        timedDamages.removeIf(t -> {
            if (!enemy.alive())
                return true;
            if (--t.ticksUntilNext > 0)
                return false;
            if (t.victimId != null) {
                // Fuego amigo: el golpe va a una aliada en vez de al enemigo
                PlayerState victim = players.get(t.victimId);
                if (victim != null && victim.alive()) {
                    players.put(t.victimId, victim.takeDamage(t.damage));
                }
            } else {
                this.enemy = enemy.takeDamage(t.damage);
                PlayerState caster = players.get(t.casterId);
                if (caster != null) {
                    players.put(t.casterId, caster.addDamageDealt(t.damage));
                }
            }
            if (t.allyDamage > 0) {
                for (String id : players.keySet()) {
                    PlayerState ally = players.get(id);
                    if (!id.equals(t.casterId) && ally.alive()) {
                        players.put(id, ally.takeDamage(t.allyDamage));
                    }
                }
            }
            t.ticksUntilNext = t.intervalTicks;
            return --t.hitsLeft <= 0;
        });
    }

    private EnemyState enemyForStage(int index) {
        int health = GameBalanceConfig.COOP_ENEMY_BASE_HEALTH
                + GameBalanceConfig.COOP_ENEMY_HEALTH_PER_STAGE * index;
        int damage = GameBalanceConfig.COOP_ENEMY_BASE_DAMAGE
                + GameBalanceConfig.COOP_ENEMY_DAMAGE_PER_STAGE * index;
        return new EnemyState(600, 1000, health, health, true, damage, false, false, 0);
    }

    private void advanceStage() {
        if (enemy.alive())
            return;
        // Un enemigo muerto no dispara ni explota
        projectiles.clear();
        ultWindupTicks = 0;
        if (stage >= GameBalanceConfig.COOP_ENEMY_COUNT - 1) {
            this.phase = GamePhase.WIN; // era el último enemigo
            return;
        }
        if (intermissionTicks == 0) {
            // Primer tick con el enemigo muerto: se cura a las vivas y empieza la pausa
            intermissionTicks = INTERMISSION_MS / TICK_MS;
            for (String id : players.keySet()) {
                PlayerState p = players.get(id);
                if (p.alive()) {
                    players.put(id, p.heal(GameBalanceConfig.ENEMY_KILL_HEAL));
                }
            }
            return;
        }
        if (--intermissionTicks == 0) {
            stage++;
            this.enemy = enemyForStage(stage);
            enemyEnergy = 0;
            rangedCooldownTicks = 0;
        }
    }

    private boolean isRangedHit(PlayerState p) {
        float dx = enemy.x() - p.x();
        boolean inFront = p.facingRight() ? dx > 0 : dx < 0;
        return inFront && Math.abs(dx) < RANGED_RANGE && Math.abs(p.y() - enemy.y()) < 50;
    }

    private double distanceTo(PlayerState p) {
        return Math.sqrt(Math.pow(enemy.x() - p.x(), 2) + Math.pow(enemy.y() - p.y(), 2));
    }

    public CoopGameState toGameState() {
        List<ProjectileState> shots = projectiles.stream()
                .map(pr -> new ProjectileState(pr.id, pr.x, pr.y, pr.facingRight))
                .toList();
        return new CoopGameState(
                roomId,
                Map.copyOf(players),
                enemy,
                tick,
                phase,
                maxPlayers,
                Map.copyOf(ultimateOutcomes),
                stage,
                shots,
                enemyEnergy * 100 / GameBalanceConfig.ENEMY_ULT_MAX_ENERGY,
                enemyUltCount);
    }

    public void resetAllAttacks() {
        for (String id : players.keySet()) {
            PlayerState p = players.get(id);
            if (p.attacking() || p.usingUltimate()) {
                players.put(id, p.stopAttacking());
            }
        }
        rangedThisTick.clear();
    }

    /** Daño programado: un golpe (Git Clone) o varios espaciados (Spaghetti Code). */
    private static final class TimedDamage {
        private final String casterId;
        private final String victimId; // null = el golpe va al enemigo
        private final int damage;
        private final int allyDamage;
        private final int intervalTicks;
        private int hitsLeft;
        private int ticksUntilNext;

        private TimedDamage(String casterId, String victimId, int damage, int allyDamage,
                int hits, int firstDelayTicks, int intervalTicks) {
            this.casterId = casterId;
            this.victimId = victimId;
            this.damage = damage;
            this.allyDamage = allyDamage;
            this.hitsLeft = hits;
            this.ticksUntilNext = firstDelayTicks;
            this.intervalTicks = intervalTicks;
        }
    }

    /** Bola que dispara el enemigo: avanza en horizontal hasta que golpea o se agota. */
    private static final class EnemyProjectile {
        private final int id;
        private float x;
        private final float y;
        private final boolean facingRight;
        private int ticksLeft = GameBalanceConfig.ENEMY_PROJECTILE_LIFE_MS / TICK_MS;

        private EnemyProjectile(int id, float x, float y, boolean facingRight) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
        }
    }
}