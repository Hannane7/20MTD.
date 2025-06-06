package io.github.some_example_name.Controllers;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Models.*;
import io.github.some_example_name.Views.GameOverView;
import io.github.some_example_name.Views.GameView;
import io.github.some_example_name.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameController {
    private GameView view;

    private PlayerController playerController;
    private WeaponController weaponController;
    private WorldController worldController;

    private float enemySpawnTimer = 0f;
    private final float ENEMY_SPAWN_INTERVAL = 3f;
    private float playerHitCooldown = 0f;

    public GameController(Player player, PreGame preGame) {
        Weapon weapon = getWeaponByName(preGame.getWeapon());

        playerController = new PlayerController(player);
        weaponController = new WeaponController(player, weapon);
        worldController = new WorldController(playerController);

    }

    public void setView(GameView view) {
        this.view = view;
    }

    public void updateGame(float delta) {
        Player player = playerController.getPlayer();
        if (player.isDead()) {
            Main.instance.setScreen(new GameOverView(player, false));
            return;
        }
        playerController.update();
        weaponController.update(delta);
        worldController.update();


        for (Enemy enemy : worldController.getEnemies()) {
            enemy.update(delta, player);
        }
        handleBulletEnemyCollisions();

        handleEnemyPlayerCollisions(delta);

        for (Enemy enemy : worldController.getEnemies()) {
            if (enemy instanceof EyeBat) {
                EyeBat eyeBat = (EyeBat) enemy;
                for (Bullet bullet : eyeBat.getBullets()) {
                    if (bullet.isActive() && bullet.getBounds().overlaps(player.getBounds())) {
                        bullet.deactivate();
                        if (!player.isInvincible())
                            player.takeDamage(1);
                    }
                }
            }
        }

        enemySpawnTimer += delta;
        if (enemySpawnTimer >= ENEMY_SPAWN_INTERVAL) {
            spawnEnemy();
            enemySpawnTimer = 0;
        }

        player.addSurviveTime(delta);
    }

    private void spawnEnemy() {
        Vector2 playerPos = new Vector2(playerController.getPlayer().getPosX(), playerController.getPlayer().getPosY());
        float angle = (float) (Math.random() * Math.PI * 2);
        float radius = 400f;

        float x = playerPos.x + (float) Math.cos(angle) * radius;
        float y = playerPos.y + (float) Math.sin(angle) * radius;
        Vector2 spawnPos = new Vector2(x, y);

        int type = (int)(Math.random() * 4);
        Enemy enemy;
        switch (type) {
            case 0: enemy = new TentacleMonster(spawnPos); break;
            case 1: enemy = new EyeBat(spawnPos); break;
            case 2: enemy = new Elder(spawnPos); break;
            case 3: enemy = new Tree(spawnPos); break;
            default: enemy = new TentacleMonster(spawnPos);
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
                if (bullet.getSprite().getBoundingRectangle().overlaps(enemy.getSprite().getBoundingRectangle())) {
                    enemy.takeDamage(bullet.getDamage());
                    bullet.deactivate();

                    if (enemy.getHealth() <= 0) {
                        playerController.getPlayer().gainXP(enemy.getXpDrop());
                        enemy.die();
                        enemyIt.remove();
                        playerController.getPlayer().addKill();
                    }
                    break;
                }
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

            if (enemy.getRect() != null)
                enemy.getRect().move(enemy.getSprite().getX(), enemy.getSprite().getY());


            if (enemy.getRect() != null && player.getRect().collidesWith(enemy.getRect())) {
                if (playerHitCooldown <= 0f && !player.isInvincible()) {
                    player.takeDamage(1);
                    player.setInvincible(0.5f);
                    playerHitCooldown = 0.5f;
                }
            }
        }
    }


    public static List<Weapon> getAllWeapons() {
        List<Weapon> weapons = new ArrayList<>();
        weapons.add(new Weapon("Revolver", 20, 6, 1.0f ,"Images/Weapon/Revolver.png"));
        weapons.add(new Weapon("Shotgun", 40, 2, 1.0f, "Images/Weapon/Shotgun.png"));
        weapons.add(new Weapon("Dual SMG", 8, 24, 1.0f, "Images/Weapon/DualSMG.png"));
        return weapons;
    }



    public PlayerController getPlayerController() {
        return playerController;
    }

    public WeaponController getWeaponController() {
        return weaponController;
    }

    public WorldController getWorldController() {
        return worldController;
    }

    public List<Enemy> getEnemies() {
        return worldController.getEnemies();
    }

    public static Weapon getWeaponByName(String name) {
        for (Weapon w : getAllWeapons()) {
            if (w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }

        return new Weapon("Revolver", 20, 6, 1.0f, "Images/Weapon/Revolver.png");
    }



}
