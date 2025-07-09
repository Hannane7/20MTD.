package io.github.some_example_name.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    protected String name;
    protected int health;
    protected Sprite sprite;
    protected int xpDrop = 10;
    protected CollisionRect rect;
    protected float damage = 1f;

    public Enemy(String name, int health, Texture texture, Vector2 spawnPosition){
        this.name = name;
        this.health = health;
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(spawnPosition.x, spawnPosition.y);
        this.rect = new CollisionRect(spawnPosition.x, spawnPosition.y, sprite.getWidth(), sprite.getHeight());
    }

    public abstract void update(float delta, Player player);
    public abstract void draw(Batch batch, float offsetX, float offsetY);

    public void moveToPlayer(Player player, float speed) {
        Vector2 playerPos = new Vector2(player.getPosX(), player.getPosY());
        Vector2 direction = playerPos.sub(sprite.getX(), sprite.getY()).nor();
        sprite.translate(direction.x * speed, direction.y * speed);
    }

    public Sprite getSprite() {

        return sprite;

    }

    public boolean isAlive() {

        return health > 0;

    }

    public boolean isDead() {

        return health <= 0;

    }

    public void setHealth(int health) {

        this.health = health;

    }

    public int getHealth() {

        return health;

    }

    public void takeDamage(float dmg) {

        this.health -= dmg;

    }

    public Rectangle getBounds() {

        return new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

    }

    public Vector2 getPosition() {

        return new Vector2(sprite.getX(), sprite.getY());

    }

    public Vector2 getPositionCenter() {

        return new Vector2(sprite.getX() + sprite.getWidth() / 2f,

            sprite.getY() + sprite.getHeight() / 2f);

    }

    public int getXpDrop() {

        return xpDrop;

    }

    public String getType() {

        return this.getClass().getSimpleName();

    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public CollisionRect getRect() {
        return rect;
    }

    public void die() {
    }


    public float getDamage() {
        return this.damage;
    }

    public void applyKnockback(Vector2 direction, float strength) {
        sprite.translate(direction.x * strength, direction.y * strength);
    }
}
