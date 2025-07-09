package io.github.some_example_name.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EyeBat extends Enemy {
    private float speed = 90f;
    private float shootCooldown = 0;
    private final float SHOOT_INTERVAL = 3f;
    private final float BULLET_SPEED = 400f;
    private final float BULLET_DAMAGE = 1f;

    private final List<Bullet> enemyBullets;

    private Animation<TextureRegion> animation;
    private TextureRegion deathFrame;
    private float stateTime = 0f;
    private boolean dead = false;

    public EyeBat(Vector2 spawnPosition) {
        super("Eyebat", 50, GameAssetManager.getManager().get("Images/Sprite/Monsters/T_EyeBat_0.png"), spawnPosition);

        this.damage = 1f;
        this.xpDrop = 15;
        this.enemyBullets = new ArrayList<>();

        TextureRegion[] frames = new TextureRegion[4];
        frames[0] = new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/Monsters/T_EyeBat_0.png", Texture.class));
        frames[1] = new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/Monsters/T_EyeBat_1.png", Texture.class));
        frames[2] = new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/Monsters/T_EyeBat_2.png", Texture.class));
        frames[3] = new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/Monsters/T_EyeBat_3.png", Texture.class));

        animation = new Animation<>(0.15f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        deathFrame = new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/Monsters/T_EyeBat_EM.png", Texture.class));
    }

    @Override
    public void update(float delta, Player player) {
        if (dead) return;
        stateTime += delta;
        rect.move(sprite.getX(), sprite.getY());

        Vector2 direction = new Vector2(player.getPosX() - getX(), player.getPosY() - getY()).nor();
        sprite.translate(direction.x * speed * delta, direction.y * speed * delta);

        shootCooldown -= delta;
        if (shootCooldown <= 0) {
            shoot(player);
            shootCooldown = SHOOT_INTERVAL;
        }

        // آپدیت گلوله‌ها
        Iterator<Bullet> iterator = enemyBullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update(delta);

            if (!bullet.isActive()) {
                iterator.remove();
            }
        }
    }

    private void shoot(Player player) {
        float startX = getX() + getSprite().getWidth() / 2;
        float startY = getY() + getSprite().getHeight() / 2;

        float targetX = player.getPosX() + player.getPlayerSprite().getWidth() / 2;
        float targetY = player.getPosY() + player.getPlayerSprite().getHeight() / 2;

        enemyBullets.add(new Bullet(startX, startY, targetX, targetY, BULLET_SPEED, BULLET_DAMAGE, Bullet.BulletOwner.ENEMY));
    }

    @Override
    public void draw(Batch batch, float offsetX, float offsetY) {
        if (dead) {
            batch.draw(deathFrame, getX() + offsetX, getY() + offsetY);
        } else {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, getX() + offsetX, getY() + offsetY, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }

        for (Bullet bullet : enemyBullets) {
            bullet.draw(batch, offsetX, offsetY);
        }
    }

    @Override
    public void die() {
        this.dead = true;
        this.stateTime = 0;
    }

    public List<Bullet> getBullets() {
        return enemyBullets;
    }
}
