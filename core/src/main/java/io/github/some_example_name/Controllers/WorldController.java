// WorldController.java
package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorldController {
    private PlayerController playerController;
    private Texture backgroundTexture;
    private float backgroundX = 0, backgroundY = 0;

    private List<Enemy> enemies;
    private float enemySpawnTimer = 0;
    private final float ENEMY_SPAWN_INTERVAL = 2f;
    private final Random random = new Random();
    private List<HitEffect> hitEffects;

    public WorldController(PlayerController playerController) {
        this.backgroundTexture = new Texture("Images/Backgrounds/GameBG.png");
        this.playerController = playerController;
        this.enemies = new ArrayList<>();
        this.hitEffects = new ArrayList<>();
        spawnInitialTrees();

    }


    public void addHitEffect(Vector2 position) {
        hitEffects.add(new HitEffect(position));
    }

    private void spawnInitialTrees() {
        int treeCount = random.nextInt(5) + 3;
        float mapWidth = Main.getWidth() * 2;
        float mapHeight = Main.getHeight() * 2;

        for (int i = 0; i < treeCount; i++) {
            float x = random.nextFloat() * mapWidth - (mapWidth / 4);
            float y = random.nextFloat() * mapHeight - (mapHeight / 4);
            enemies.add(new Tree(new Vector2(x, y)));
        }
    }


    public void update() {
        Player player = playerController.getPlayer();

        backgroundX = player.getPosX() - Main.getWidth() / 2f;
        backgroundY = player.getPosY() - Main.getHeight() / 2f;

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.update(Gdx.graphics.getDeltaTime(), player);
            if (!enemy.isAlive()) {
                iterator.remove();
            }
        }

        Iterator<HitEffect> effectIterator = hitEffects.iterator();
        while (effectIterator.hasNext()) {
            HitEffect effect = effectIterator.next();
            effect.update(Gdx.graphics.getDeltaTime());
            if (effect.isFinished()) {
                effectIterator.remove();
            }
        }
    }

    public void render(float offsetX, float offsetY) {
        Main.getBatch().draw(backgroundTexture, backgroundX + offsetX, backgroundY + offsetY);
        for (Enemy enemy : enemies) {
            enemy.draw(Main.getBatch(), offsetX, offsetY);
        }
        for (HitEffect effect : hitEffects) {
            effect.draw(Main.getBatch(), offsetX, offsetY);
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
