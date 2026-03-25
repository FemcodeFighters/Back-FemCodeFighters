package com.code.fighters.dto.coop;

public record EnemyState(
        float x,
        float y,
        int health,
        int maxHealth,
        boolean alive,
        int damage,
        boolean attacking,
        boolean facingRight,
        long lastAttackTick) {
    private static final long ATTACK_COOLDOWN_TICKS = 40;
    private static final long ATTACK_DURATION_TICKS = 10;
    private static final int ATTACK_RANGE_X = 80;
    private static final int ATTACK_RANGE_Y = 60;

    public EnemyState() {
        this(600, 300, 500, 500, true, 5, false, false, 0);
    }

    public EnemyState takeDamage(int amount) {
        int newHealth = Math.max(0, this.health - amount);
        return new EnemyState(
                x, y, newHealth, maxHealth, newHealth > 0,
                damage, attacking, facingRight, lastAttackTick);
    }

    public EnemyState tryAttack(PlayerState target, long currentTick) {
        boolean isStillInAnimation = (currentTick - lastAttackTick) < ATTACK_DURATION_TICKS;
        if (isStillInAnimation)
            return this;
        boolean inCooldown = (currentTick - lastAttackTick) < ATTACK_COOLDOWN_TICKS;
        float dx = Math.abs(this.x - target.x());
        float dy = Math.abs(this.y - target.y());
        boolean inRange = dx < ATTACK_RANGE_X && dy < ATTACK_RANGE_Y;

        if (inRange && !inCooldown) {
            return new EnemyState(
                    x, y, health, maxHealth, alive,
                    damage, true, facingRight, currentTick);
        }
        return new EnemyState(
                x, y, health, maxHealth, alive,
                damage, false, facingRight, lastAttackTick);
    }

    public boolean isAttackHitting(PlayerState player) {
        if (!this.attacking)
            return false;
        float dx = Math.abs(this.x - player.x());
        float dy = Math.abs(this.y - player.y());
        return dx < ATTACK_RANGE_X && dy < ATTACK_RANGE_Y;
    }

    public EnemyState move(float newX, boolean facing) {
        if (this.attacking)
            return this;
        return new EnemyState(newX, y, health, maxHealth, alive,
                damage, attacking, facing, lastAttackTick);
    }
}