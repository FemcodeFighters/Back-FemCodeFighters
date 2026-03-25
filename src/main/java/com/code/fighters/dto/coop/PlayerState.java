package com.code.fighters.dto.coop;

public record PlayerState(
        String playerId, float x, float y, float velocityY,
        int health, int maxHealth, boolean alive,
        boolean attacking, boolean usingUltimate, boolean jumping,
        boolean facingRight, int damageDealt, int damageTaken, int ultimatesUsed) {

private static final float GROUND_Y = 1000f;
    public PlayerState(String playerId) {
this(playerId, 200, 1000f, 0, 100, 100, true, false, false, false, true, 0, 0, 0);    }

    public PlayerState updatePhysics() {
        if (!alive) return this;

        float gravity = 0.8f;
        float newVelocityY = velocityY + gravity;
        float newY = y + newVelocityY;
        boolean stillJumping = jumping;

        if (newY >= GROUND_Y) {
            newY = GROUND_Y;
            newVelocityY = 0;
            stillJumping = false;
        }
        return new PlayerState(playerId, x, newY, newVelocityY, health, maxHealth, alive,
                attacking, usingUltimate, stillJumping, facingRight, damageDealt, damageTaken, ultimatesUsed);
    }

    public PlayerState takeDamage(int amount) {
        int newHealth = Math.max(0, this.health - amount);
        return new PlayerState(playerId, x, y, velocityY, newHealth, maxHealth, newHealth > 0,
                attacking, usingUltimate, jumping, facingRight, damageDealt, damageTaken + amount, ultimatesUsed);
    }

    public PlayerState move(float dx, boolean facing) {
        return new PlayerState(playerId, x + dx, y, velocityY, health, maxHealth, alive, attacking, usingUltimate,
                jumping, facing, damageDealt, damageTaken, ultimatesUsed);
    }

    public PlayerState jump(float power) {
        return new PlayerState(playerId, x, y, power, health, maxHealth, alive, attacking, usingUltimate, true,
                facingRight, damageDealt, damageTaken, ultimatesUsed);
    }

    public PlayerState setCombatState(boolean atk, boolean ult) {
        return new PlayerState(playerId, x, y, velocityY, health, maxHealth, alive, atk, ult, jumping, facingRight,
                damageDealt, damageTaken, ultimatesUsed + (ult ? 1 : 0));
    }

    public PlayerState stopAttacking() {
        return new PlayerState(playerId, x, y, velocityY, health, maxHealth, alive, false, false, jumping, facingRight,
                damageDealt, damageTaken, ultimatesUsed);
    }

    public PlayerState addDamageDealt(int amt) {
        return new PlayerState(playerId, x, y, velocityY, health, maxHealth, alive, attacking, usingUltimate, jumping,
                facingRight, damageDealt + amt, damageTaken, ultimatesUsed);
    }

    public boolean isHitting(EnemyState enemy) {
        if (!attacking && !usingUltimate) return false;
        return Math.abs(x - enemy.x()) < 70 && Math.abs(y - enemy.y()) < 50;
    }

    public int getAttackDamage() {
        return 10;
    }

    public int getUltimateDamage() {
        return 40;
    }
}
