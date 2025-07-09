package io.github.some_example_name.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

public class Weapon {
    private String name;
    private float damage;
    private int magazineSize;
    private int ammo;
    private float reloadTime;
    private float fireRate; // زمان تاخیر بین هر شلیک (به ثانیه)
    private long lastShotTime;
    private boolean reloading;
    private long reloadStartTime;
    private String texturePath;

    private Sprite weaponSprite;
    private Animation<TextureRegion> reloadAnimation;
    private float reloadStateTime;
    private float angle = 0f;

    public Weapon(String name, float damage, int magazineSize, float fireRate, float reloadTime, String texturePath, String[] reloadTexturePaths) {
        this.name = name;
        this.damage = damage;
        this.magazineSize = magazineSize;
        this.fireRate = fireRate;
        this.reloadTime = reloadTime;
        this.texturePath = texturePath;
        this.ammo = magazineSize;
        this.lastShotTime = 0;
        this.reloading = false;

        try {
            this.weaponSprite = new Sprite(new Texture(Gdx.files.internal(texturePath)));

            // ساخت انیمیشن از روی آرایه مسیرها
            TextureRegion[] reloadFrames = new TextureRegion[reloadTexturePaths.length];
            for (int i = 0; i < reloadTexturePaths.length; i++) {
                reloadFrames[i] = new TextureRegion(new Texture(Gdx.files.internal(reloadTexturePaths[i])));
            }
            // زمان هر فریم انیمیشن برابر با کل زمان ریلود تقسیم بر تعداد فریم‌هاست
            this.reloadAnimation = new Animation<>(reloadTime / reloadFrames.length, reloadFrames);

            this.weaponSprite.setOrigin(this.weaponSprite.getWidth() * 0.2f, this.weaponSprite.getHeight() / 2f);
        } catch (Exception e) {
            System.err.println("Could not load weapon textures: " + e.getMessage());
            this.weaponSprite = new Sprite();
            this.reloadAnimation = new Animation<>(1f, new TextureRegion()); // انیمیشن خالی
        }
    }

    public void increaseMagazineSize(int amount) {
        this.magazineSize += amount;
    }

    public boolean canShoot() {
        if (reloading || ammo <= 0) {
            return false;
        }
        // محاسبه زمان گذشته از آخرین شلیک
        long timeSinceLastShot = TimeUtils.timeSinceNanos(lastShotTime);
        // تبدیل نانوثانیه به ثانیه و مقایسه با نرخ آتش
        return (timeSinceLastShot / 1_000_000_000.0f) >= fireRate;
    }

    public void shoot() {
        if (canShoot()) {
            ammo--;
            lastShotTime = TimeUtils.nanoTime();

            if (ammo <= 0) {
                if (SettingsManager.isAutoReload()) {
                    reload();
                }
            }
        }
    }

    public void reload() {
        if (reloading) return;
        reloading = true;
        reloadStartTime = TimeUtils.nanoTime();
        reloadStateTime = 0f; // ریست کردن زمان انیمیشن
    }

    public void update() {
        if (reloading) {
            reloadStateTime += Gdx.graphics.getDeltaTime(); // زمان انیمیشن را جلو ببر
            long timeSinceReloadStart = TimeUtils.timeSinceNanos(reloadStartTime);
            if ((timeSinceReloadStart / 1_000_000_000.0f) >= reloadTime) {
                ammo = magazineSize;
                reloading = false;
            }
        }
    }

    public boolean isReloading() {
        return reloading;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public float getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public float getFireRate() {
        return fireRate;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public Sprite getWeaponSprite() {
        return weaponSprite;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public TextureRegion getReloadKeyFrame() {
        return reloadAnimation.getKeyFrame(reloadStateTime, false);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
