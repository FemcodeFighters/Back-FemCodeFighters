package com.code.fighters.model;

import com.code.fighters.dto.coop.CoopGameState;
import com.code.fighters.dto.coop.EnemyState;
import com.code.fighters.dto.coop.PlayerState;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class GameRoom {

    private final String roomId;
    private final LocalDateTime startedAt;
    private final int maxPlayers;
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();
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
        case "ULTIMATE" -> player.setCombatState(false, true);
        case "IDLE" -> player; 
        default -> player;
    };
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

            if (p.isHitting(enemy)) {
                int dmg = p.usingUltimate() ? p.getUltimateDamage() : p.getAttackDamage();
                this.enemy = enemy.takeDamage(dmg);
                //p = p.addDamageDealt(dmg).stopAttacking();
                p = p.addDamageDealt(dmg);
            }
            players.put(pid, p);
        }

        if (!enemy.alive())
            this.phase = GamePhase.WIN;
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
                maxPlayers);
    }

    public void resetAllAttacks() {
    for (String id : players.keySet()) {
        PlayerState p = players.get(id);
        if (p.attacking() || p.usingUltimate()) {
            players.put(id, p.stopAttacking());
        }
    }
}
}