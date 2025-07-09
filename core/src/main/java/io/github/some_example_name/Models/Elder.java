package io.github.some_example_name.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Elder extends Enemy {
    private float speed = 60f;
    private float dashSpeed = 480f;
    private float dashCooldown = 0;
    private final float DASH_INTERVAL = 2f;
    private Vector2 dashTarget = null;
    private boolean isDashing = false;

    private TextureRegion normalFrame;
    private TextureRegion deadFrame;
    private boolean dead = false;

    public Elder(Vector2 spawnPosition) {
        super("Elder", 400, GameAssetManager.getManager().get("Images/Sprite/Monsters/ElderBrain.png", Texture.class), spawnPosition);
        this.damage = 2f;
        this.xpDrop = 100;

        Texture normalTexture = GameAssetManager.getManager().get("Images/Sprite/Monsters/ElderBrain.png", Texture.class);
        Texture deadTexture = GameAssetManager.getManager().get("Images/Sprite/Monsters/ElderBrain_Em.png", Texture.class);

        normalFrame = new TextureRegion(normalTexture);
        deadFrame = new TextureRegion(deadTexture);

        this.sprite.setSize(normalFrame.getRegionWidth(), normalFrame.getRegionHeight());
    }


    @Override
    public void update(float delta, Player player) {
        if (dead) return;
        rect.move(sprite.getX(), sprite.getY());

        dashCooldown -= delta;
        if (dashCooldown <= 0) {
            startDash(player);
            dashCooldown = DASH_INTERVAL;
        }

        if (isDashing) {
            dashTowardsTarget(delta);
        } else {
            moveToPlayer(player, speed * delta);
        }
    }

    private void startDash(Player player) {
        if (player == null || player.getPositionCenter() == null) return;
        isDashing = true;
        dashTarget = new Vector2(player.getPositionCenter());
    }

    private void dashTowardsTarget(float delta) {
        if (dashTarget == null || getPositionCenter().dst(dashTarget) < 15f) {
            isDashing = false;
            dashTarget = null;
            return;
        }
        Vector2 direction = dashTarget.cpy().sub(getPositionCenter()).nor();
        sprite.translate(direction.x * dashSpeed * delta, direction.y * dashSpeed * delta);
    }

    @Override
    public void die() {
        this.dead = true;
    }

    @Override
    public void draw(Batch batch, float offsetX, float offsetY) {
        float drawX = sprite.getX() + offsetX;
        float drawY = sprite.getY() + offsetY;
        if (dead) {
            batch.draw(deadFrame, drawX, drawY, deadFrame.getRegionWidth(), deadFrame.getRegionHeight());
        } else {
            batch.draw(normalFrame, drawX, drawY, normalFrame.getRegionWidth(), normalFrame.getRegionHeight());
        }
    }
}
