package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Models.*;
    import io.github.some_example_name.Views.GameOverView;
import io.github.some_example_name.Views.GameView;
import io.github.some_example_name.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameController {
    private GameView view;

    private PlayerController playerController;
    private WeaponController weaponController;
    private WorldController worldController;
    private float playerHitCooldown = 0f;
    private boolean isAutoAimActive = false;
    private final Random random = new Random();

    private final float gameDurationSeconds;
    private boolean isBossSpawned = false;

    private float enemySpawnTimer = 0f;
    private float enemySpawnInterval = 2.0f;
    private final float MIN_SPAWN_INTERVAL = 0.2f;
    private float spawnRateIncreaseTimer = 0f;
    private final float SPAWN_RATE_INCREASE_INTERVAL = 15f;

    private boolean isBoundaryShieldActive = false;
    private Vector2 boundaryShieldCenter;
    private float boundaryShieldRadius;
    private final float BOUNDARY_SHIELD_SHRINK_RATE = 10f;
    private final float BOUNDARY_SHIELD_DAMAGE = 2f;

    public GameController(Player player, PreGame preGame) {
        Weapon weapon = getWeaponByName(preGame.getWeapon());
        playerController = new PlayerController(player);
        weaponController = new WeaponController(player, weapon);
        worldController = new WorldController(playerController);
        this.gameDurationSeconds = preGame.getDuration() * 60;
        player.setWeapon(weapon);

    }

    public void setView(GameView view) { this.view = view; }

    public void updateGame(float delta) {
        Player player = playerController.getPlayer();
        if (player.isDead()) {
            Main.instance.setScreen(new GameOverView(player, false));
            return;
        }

        if (player.getSurviveTime() >= gameDurationSeconds) {
            Main.instance.setScreen(new GameOverView(player, true));
            return;
        }

        playerController.update();

        Vector2 aimTarget = new Vector2();
        if (isAutoAimActive()) {
            Enemy nearest = findNearestEnemy();
            if (nearest != null) {
                aimTarget.set(nearest.getPositionCenter());
                weaponController.shoot(aimTarget.x, aimTarget.y);
            }
        } else {
            float worldX = Gdx.input.getX() - view.getOffsetX();
            float worldY = Gdx.graphics.getHeight() - Gdx.input.getY() - view.getOffsetY();
            aimTarget.set(worldX, worldY);
        }

        weaponController.update(delta, aimTarget);


        worldController.update();

        if (!isBossSpawned && player.getSurviveTime() >= (gameDurationSeconds / 2)) {
            spawnBoss();
            activateBoundaryShield();
            isBossSpawned = true;
        }

        if (isBoundaryShieldActive) {
            updateBoundaryShield(delta, player);
        }

        for (Enemy enemy : worldController.getEnemies()) { enemy.update(delta, player); }

        handleBulletEnemyCollisions();
        handleEnemyPlayerCollisions(delta);

        handleEnemyBulletCollisions(delta);

        updateSpawnRate(delta);
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnInterval) {
            spawnEnemy();
            enemySpawnTimer = 0;
        }

        player.addSurviveTime(delta);
    }


    private Enemy findNearestEnemy() {
        Player player = playerController.getPlayer();
        Enemy nearestEnemy = null;
        float minDistance = Float.MAX_VALUE;
        Vector2 playerCenter = player.getPositionCenter();

        for (Enemy enemy : worldController.getEnemies()) {
            if (enemy.isAlive()) {
                float distance = playerCenter.dst(enemy.getPositionCenter());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = enemy;
                }
            }
        }
        return nearestEnemy;
    }

   public void activateBoundaryShield() {
        this.isBoundaryShieldActive = true;
        this.boundaryShieldCenter = new Vector2(playerController.getPlayer().getPosition());
        this.boundaryShieldRadius = Gdx.graphics.getWidth();
    }

    private void updateBoundaryShield(float delta, Player player) {
        boundaryShieldRadius -= BOUNDARY_SHIELD_SHRINK_RATE * delta;
        if (boundaryShieldRadius < 50) {
            boundaryShieldRadius = 50;
        }


        float playerDistance = player.getPositionCenter().dst(this.boundaryShieldCenter);
        if (playerDistance > this.boundaryShieldRadius) {
            if (playerHitCooldown <= 0 && !player.isInvincible()) {
                player.takeDamage(BOUNDARY_SHIELD_DAMAGE);
                player.setInvincible(0.5f);
                playerHitCooldown = 0.5f;
            }
        }
    }

    private void spawnBoss() {
        Player player = playerController.getPlayer();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float spawnX = player.getPosX() - (screenWidth / 2) + (random.nextFloat() * screenWidth);
        float spawnY = player.getPosY() + (screenHeight / 2) - 150;

        Vector2 spawnPos = new Vector2(spawnX, spawnY);
        worldController.getEnemies().add(new Elder(spawnPos));
    }

    private void updateSpawnRate(float delta) {
        spawnRateIncreaseTimer += delta;
        if (spawnRateIncreaseTimer >= SPAWN_RATE_INCREASE_INTERVAL) {
            if (enemySpawnInterval > MIN_SPAWN_INTERVAL) {
                enemySpawnInterval *= 0.9f;
                if (enemySpawnInterval < MIN_SPAWN_INTERVAL) {
                    enemySpawnInterval = MIN_SPAWN_INTERVAL;
                }
            }
            spawnRateIncreaseTimer = 0f;
        }
    }

    private void spawnEnemy() {
        Vector2 playerPos = new Vector2(playerController.getPlayer().getPosX(), playerController.getPlayer().getPosY());
        float angle = (float) (Math.random() * Math.PI * 2);
        float radius = 800f;
        float x = playerPos.x + (float) Math.cos(angle) * radius;
        float y = playerPos.y + (float) Math.sin(angle) * radius;
        Vector2 spawnPos = new Vector2(x, y);

        Enemy enemy;
        int chance = random.nextInt(20);

        if (chance < 2) {
            enemy = new Tree(spawnPos);
        } else if (chance < 8) {
            enemy = new EyeBat(spawnPos);
        } else {
            enemy = new TentacleMonster(spawnPos);
        }

        worldController.getEnemies().add(enemy);
    }

    private void handleBulletEnemyCollisions() {
        Iterator<Bullet> bulletIt = weaponController.getBullets().iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            Iterator<Enemy> enemyIt = worldController.getEnemies().iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();


                if (!enemy.isAlive()) continue;

                if (bullet.getSprite().getBoundingRectangle().overlaps(enemy.getSprite().getBoundingRectangle())) {
                    worldController.addHitEffect(bullet.getPosition());
                    enemy.takeDamage(bullet.getDamage());


                    Vector2 knockbackDirection = bullet.getVelocity().nor();
                    float knockbackStrength = 15f;

                    enemy.applyKnockback(knockbackDirection, knockbackStrength);

                    bullet.deactivate();

                    if (enemy.isDead()) {
                        if (enemy instanceof Elder) {
                            isBoundaryShieldActive = false;
                        }
                        playerController.getPlayer().gainXP(enemy.getXpDrop());
                        enemy.die();
                        playerController.getPlayer().addKill();
                    }


                    break;
                }
            }

            if (!bullet.isActive()) {
                bulletIt.remove();
            }
        }
    }
    private void handleEnemyPlayerCollisions(float delta) {
        Player player = playerController.getPlayer();

        if (playerHitCooldown > 0f)
            playerHitCooldown -= delta;

        player.getRect().move(player.getPosX(), player.getPosY());

        for (Enemy enemy : getEnemies()) {
            if (!enemy.isAlive()) continue;

            if (enemy.getRect() != null && player.getRect().collidesWith(enemy.getRect())) {
                if (playerHitCooldown <= 0f && !player.isInvincible()) {
                    player.takeDamage(enemy.getDamage());
                    player.setInvincible(0.5f);
                    playerHitCooldown = 0.5f;
                }
            }
        }
    }

    public static List<Weapon> getAllWeapons() {
        List<Weapon> weapons = new ArrayList<>();

        weapons.add(new Weapon("Revolver", 20, 6, 0.5f, 1.0f,
            "Images/Sprite/Weapons/RevolverStill.png",new String[] {
            "Images/Sprite/Weapons/RevolverReload_1.png",
            "Images/Sprite/Weapons/RevolverReload_2.png",
            "Images/Sprite/Weapons/RevolverReload_3.png" }
        ));

        weapons.add(new Weapon("Shotgun", 10, 2, 1.0f, 1.0f,
            "Images/Sprite/Weapons/T_Shotgun_SS_0.png", new String[] {
            "Images/Sprite/Weapons/T_Shotgun_SS_1.png",
            "Images/Sprite/Weapons/T_Shotgun_SS_2.png",
            "Images/Sprite/Weapons/T_Shotgun_SS_3.png" }
        ));

        weapons.add(new Weapon("Dual SMG", 8, 24, 0.1f, 2.0f,
            "Images/Sprite/Weapons/T_DualSMGs_Icon.png", new String[] {
            "Images/Sprite/Weapons/SMGReload_1.png",
            "Images/Sprite/Weapons/SMGReload_2.png",
            "Images/Sprite/Weapons/SMGReload_3.png" }
        ));

        return weapons;
    }

    public PlayerController getPlayerController() { return playerController; }
    public WeaponController getWeaponController() { return weaponController; }
    public WorldController getWorldController() { return worldController; }
    public List<Enemy> getEnemies() { return worldController.getEnemies(); }

    public static Weapon getWeaponByName(String name) {
        for (Weapon w : getAllWeapons()) {
            if (w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }
        return new Weapon("Revolver", 20, 6, 0.5f, 1.0f, "Images/Sprite/Weapons/RevolverStill.png", new String[] {
            "Images/Sprite/Weapons/RevolverReload_1.png",
            "Images/Sprite/Weapons/RevolverReload_2.png",
            "Images/Sprite/Weapons/RevolverReload_3.png" }
        );
    }

    public void toggleAutoAim() { this.isAutoAimActive = !this.isAutoAimActive; }
    public boolean isAutoAimActive() { return this.isAutoAimActive; }

    public void activateLevelUp() {
        Player player = playerController.getPlayer();
        player.gainXP(player.getXPToNextLevel() - player.getXP());
        System.out.println("Cheat: LEVEL UP!");
    }

    public void activateRevive() {
        Player player = playerController.getPlayer();
        if (player.getPlayerHealth() <= 0) {
            player.setPlayerHealth(50);
            System.out.println("Cheat: REVIVED!");
        }
    }

    public void activateDecreaseTime() {
        Player player = playerController.getPlayer();
        player.addSurviveTime(60);
        System.out.println("Cheat: Time advanced by 60 seconds!");
    }

    public void activateGoToBoss() {
        Player player = playerController.getPlayer();
        float bossTime = gameDurationSeconds / 2;
        if (player.getSurviveTime() < bossTime) {
            player.addSurviveTime(bossTime - player.getSurviveTime());
            System.out.println("Cheat: Warping to boss fight!");
        }
    }

    public void activateGodMode() {
        Player player = playerController.getPlayer();
        player.setPlayerHealth(99999);
        System.out.println("Cheat: GODMODE ON!");
    }
    public float getBoundaryShieldRadius() {
        return boundaryShieldRadius;
    }

    public Vector2 getBoundaryShieldCenter() {
        return boundaryShieldCenter;
    }

    public boolean isBoundaryShieldActive() {
        return isBoundaryShieldActive;
    }

    private void handleEnemyBulletCollisions(float delta) {
        Player player = playerController.getPlayer();
        if (player.isDead() || player.isInvincible()) return;

        for (Enemy enemy : worldController.getEnemies()) {
            if (enemy instanceof EyeBat) {
                EyeBat eyeBat = (EyeBat) enemy;
                Iterator<Bullet> bulletIt = eyeBat.getBullets().iterator();
                while (bulletIt.hasNext()) {
                    Bullet bullet = bulletIt.next();
                    if (bullet.getSprite().getBoundingRectangle().overlaps(player.getBounds())) {
                        player.takeDamage(bullet.getDamage());

                        worldController.addHitEffect(player.getPosition());

                        bullet.deactivate();
                        bulletIt.remove();
                    }
                }
            }
        }
    }

    public float getEnemySpawnInterval() {
        return enemySpawnInterval;
    }

    public float getGameDurationSeconds() {
        return gameDurationSeconds;
    }

    public void setBossSpawned(boolean bossSpawned) {
        isBossSpawned = bossSpawned;
    }

    public boolean isBossSpawned() {
        return this.isBossSpawned;
    }

    public void setEnemySpawnInterval(float interval) {
        this.enemySpawnInterval = interval;
    }
}
