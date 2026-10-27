package com.code.fighters.model;

import com.code.fighters.config.GameBalanceConfig;
import com.code.fighters.dto.coop.CoopGameState;
import com.code.fighters.dto.coop.EnemyState;
import com.code.fighters.dto.coop.PlayerState;
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

    private static final int TICK_MS = 50;
    private static final int RANGED_DAMAGE = 10;
    private static final float RANGED_RANGE = 450f;
    private static final int SPAGHETTI_START_DELAY_MS = 500;
    private static final int CLONE_ARRIVAL_MS = 620;

    private final String roomId;
    private final LocalDateTime startedAt;
    private final int maxPlayers;
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();
    private final Map<String, UltimateSkill> ultimates = new ConcurrentHashMap<>();
    private final Map<String, String> ultimateOutcomes = new ConcurrentHashMap<>();
    private final Set<String> rangedThisTick = ConcurrentHashMap.newKeySet();
    @Getter(lombok.AccessLevel.NONE)
    private final List<TimedDamage> timedDamages = new CopyOnWriteArrayList<>();
    private EnemyState enemy;
    private GamePhase phase = GamePhase.WAITING;
    private long tick = 0;

    private final Queue<PlayerInput> inputQueue = new ConcurrentLinkedQueue<>();

    public GameRoom(int maxPlayers) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.startedAt = LocalDateTime.now();
        this.maxPlayers = maxPlayers;
        this.enemy = new EnemyState();
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
                });

        if (!players.isEmpty() && players.values().stream().noneMatch(PlayerState::alive)) {
            this.phase = GamePhase.LOSE;
        }
    }

    public void checkCollisions() {
        if (phase != GamePhase.PLAYING)
            return;

        for (String pid : players.keySet()) {
            PlayerState p = players.get(pid);
            if (!p.alive())
                continue;

            if (enemy.isAttackHitting(p) && enemy.lastAttackTick() == tick) {
                float pushX = (p.x() > enemy.x()) ? 60f : -60f;
                p = p.takeDamage(enemy.damage()).move(pushX, p.facingRight());
            }

            if (p.attacking()) {
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

        resolveTimedDamage();

        if (!enemy.alive())
            this.phase = GamePhase.WIN;
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

    private boolean isRangedHit(PlayerState p) {
        float dx = enemy.x() - p.x();
        boolean inFront = p.facingRight() ? dx > 0 : dx < 0;
        return inFront && Math.abs(dx) < RANGED_RANGE && Math.abs(p.y() - enemy.y()) < 50;
    }

    private double distanceTo(PlayerState p) {
        return Math.sqrt(Math.pow(enemy.x() - p.x(), 2) + Math.pow(enemy.y() - p.y(), 2));
    }

    public CoopGameState toGameState() {
    return new CoopGameState(
            roomId,
            Map.copyOf(players),
            enemy,
            tick,
            phase,
            maxPlayers,
            Map.copyOf(ultimateOutcomes));
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

private static final class TimedDamage {
    private final String casterId;
    private final String victimId;
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
}