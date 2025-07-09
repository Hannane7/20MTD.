package io.github.some_example_name.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;

public class Bullet {
    private Sprite sprite;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active = true;
    private float damage;
    private float maxDistance;
    private float traveledDistance;

    public enum BulletOwner {
        PLAYER, ENEMY
    }

    public Bullet(float startX, float startY, float targetX, float targetY, float speed, float damage, BulletOwner owner) {
        this.position = new Vector2(startX, startY);
        this.damage = damage;
        this.maxDistance = 2000f;
        this.traveledDistance = 0;

        Vector2 direction = new Vector2(targetX - startX, targetY - startY).nor();
        this.velocity = direction.scl(speed);

        Texture texture;
        if (owner == BulletOwner.PLAYER) {
            texture = GameAssetManager.getManager().get("Images/CharBullet.png", Texture.class);
        } else {
            texture = GameAssetManager.getManager().get("Images/Bullet.png", Texture.class);
        }
        this.sprite = new Sprite(texture);
        this.sprite.setSize(8, 8);
        this.sprite.setOriginCenter();
        this.sprite.setRotation(direction.angleDeg());
        this.sprite.setPosition(position.x, position.y);

        this.active = true;
    }

    public void update(float delta) {
        if (!active) return;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        sprite.setPosition(position.x, position.y);

        traveledDistance += velocity.len() * delta;
        if (traveledDistance >= maxDistance) {
            deactivate();
        }
    }

    public void draw(Batch batch, float offsetX, float offsetY) {
        if (active) {
            sprite.setPosition(position.x + offsetX, position.y + offsetY);
            sprite.draw(batch);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public float getDamage() {
        return damage;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
