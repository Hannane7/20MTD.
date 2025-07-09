package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.Bullet;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Models.Weapon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import static io.github.some_example_name.Main.batch;

public class WeaponController {
    private final Weapon weapon;
    private final Player player;
    private final List<Bullet> bullets;

    public WeaponController(Player player, Weapon weapon) {
        this.player = player;
        this.weapon = weapon;
        this.bullets = new ArrayList<>();
    }

    public void update(float delta, Vector2 aimTarget) {
        weapon.update();
        updateWeaponAngle(aimTarget);

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(delta);
            if (!b.isActive()) {
                it.remove();
            }
        }
    }

    public void render(Batch batch, float offsetX, float offsetY) {
        for (Bullet b : bullets) {
            b.draw(batch, offsetX, offsetY);
        }

        Sprite weaponSprite = weapon.getWeaponSprite();
        if (weaponSprite == null) return;

        float playerScreenX = Gdx.graphics.getWidth() / 2f;
        float playerScreenY = Gdx.graphics.getHeight() / 2f;

        if (weapon.isReloading()) {
            TextureRegion reloadFrame = weapon.getReloadKeyFrame();
            if (reloadFrame != null && player.getPlayerSprite() != null) {
                batch.draw(reloadFrame,
                    playerScreenX - reloadFrame.getRegionWidth() / 2,
                    playerScreenY + player.getPlayerSprite().getHeight() / 2);
            }
        } else {
            float weaponX = playerScreenX;
            float weaponY = playerScreenY - weaponSprite.getHeight() / 2f;
            weaponSprite.setPosition(weaponX, weaponY);
            weaponSprite.setRotation(weapon.getAngle());
            weaponSprite.draw(batch);
        }
    }

    public void shoot(float targetX, float targetY) {
        if (!weapon.canShoot()) return;

        float playerCenterX = player.getPosX() + player.getPlayerSprite().getWidth() / 2;
        float playerCenterY = player.getPosY() + player.getPlayerSprite().getHeight() / 2;
        Vector2 weaponOffset = new Vector2(weapon.getWeaponSprite().getWidth() * 0.8f, -10).rotateDeg(weapon.getAngle());
        float startX = playerCenterX + weaponOffset.x;
        float startY = playerCenterY + weaponOffset.y;

        Vector2 target = new Vector2(targetX, targetY);
        float currentDamage = weapon.getDamage();
        if (player.isDamagerActive()) {
            currentDamage *= 1.25f;
        }

        String name = weapon.getName().toLowerCase();
        if (name.contains("shotgun")) {
            shootShotgun(startX ,startY, target, currentDamage);
        } else if (name.contains("smg")) {
            shootSMG(startX, startY, target, currentDamage);
        } else {
            shootSingleBullet(startX, startY, target, currentDamage);
        }

        for (int i = 0; i < player.getProjectileBonus(); i++) {
            shootSingleBullet(startX, startY, target, weapon.getDamage() * 0.2f);
        }

        weapon.shoot();
    }

    private void shootSingleBullet(float x, float y, Vector2 target, float damage) {
        bullets.add(new Bullet(x, y, target.x, target.y, 800, damage, Bullet.BulletOwner.PLAYER));
    }

    private void shootShotgun(float x, float y, Vector2 target, float damage) {
        int pelletCount = 4;
        float spreadAngle = 15f;

        Vector2 direction = target.cpy().sub(x, y).nor();
        float baseAngle = direction.angleDeg();

        for (int i = 0; i < pelletCount; i++) {
            float angleOffset = (i - (pelletCount - 1) / 2f) * spreadAngle;
            float finalAngle = baseAngle + angleOffset;

            Vector2 rotated = new Vector2(1, 0).setAngleDeg(finalAngle);
            Vector2 bulletTarget = new Vector2(x, y).add(rotated.scl(1000));

            bullets.add(new Bullet(x, y, bulletTarget.x, bulletTarget.y, 800, weapon.getDamage(), Bullet.BulletOwner.PLAYER));
        }
    }

    private void shootSMG(float x, float y, Vector2 target, float damage) {
        for (int i = 0; i < 2; i++) {
            Vector2 spread = target.cpy().sub(x, y).nor();
            spread.rotateDeg((float) (Math.random() * 10 - 5));

            Vector2 bulletTarget = new Vector2(x, y).add(spread.scl(1000));
            bullets.add(new Bullet(x, y, bulletTarget.x, bulletTarget.y, 900, weapon.getDamage(), Bullet.BulletOwner.PLAYER));
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public Weapon getWeapon() {
        return weapon;
    }
    public void setAmmo(int ammo) {
        weapon.setAmmo(ammo);
    }

    public void reload() {
        weapon.reload();
    }

    private void updateWeaponAngle(Vector2 aimTarget) {
        if (player == null || weapon.getWeaponSprite() == null) return;

        float playerCenterX = player.getPosX() + player.getPlayerSprite().getWidth() / 2;
        float playerCenterY = player.getPosY() + player.getPlayerSprite().getHeight() / 2;

        float dx = aimTarget.x - playerCenterX;
        float dy = aimTarget.y - playerCenterY;

        float angle = com.badlogic.gdx.math.MathUtils.atan2(dy, dx) * com.badlogic.gdx.math.MathUtils.radiansToDegrees;
        weapon.setAngle(angle);
    }



}
