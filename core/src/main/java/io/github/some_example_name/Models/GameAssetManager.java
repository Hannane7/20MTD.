package io.github.some_example_name.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    private static final AssetManager assetManager = new AssetManager();
    private static Skin skin;
    private static BitmapFont font;
    private static Music defaultMusic;
    private static Music actionMusic;
    private static Music calmMusic;
    private static Music currentMusic;
    private static ShapeRenderer shapeRenderer;
    private static ShaderProgram grayscaleShader;
    private static Animation<TextureRegion> playerIdleAnimation;

    public static void loadAssets() {
        assetManager.load("skin/biological-attack-ui.json", Skin.class);

        // ## فونت‌ها (Fonts) ##
        font = new BitmapFont();

        // ## پس‌زمینه‌ها ##
        assetManager.load("Images/Backgrounds/Menus.png", Texture.class);
        assetManager.load("Images/Backgrounds/GameBG.png", Texture.class);


        // ## آواتارها ##
        assetManager.load("Images/avatar/avatar1.png", Texture.class);
        assetManager.load("Images/avatar/avatar2.png", Texture.class);
        assetManager.load("Images/avatar/avatar3.png", Texture.class);
        assetManager.load("Images/avatar/avatar4.png", Texture.class);
        assetManager.load("Images/avatar/avatar5.png", Texture.class);

        // ## انیمیشن‌های کاراکترها ##
        assetManager.load("Images/Texture2D/Character/T_Shana.png", Texture.class);
        assetManager.load("Images/Texture2D/Character/T_Diamond #7829.png", Texture.class);
        assetManager.load("Images/Texture2D/Character/T_Scarlett.png", Texture.class);
        assetManager.load("Images/Texture2D/Character/T_Lilith.png", Texture.class);
        assetManager.load("Images/Texture2D/Character/T_Dasher.png", Texture.class);

        // ## سلاح‌ها و ریلود ##
        assetManager.load("Images/Sprite/Weapons/RevolverStill.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/RevolverReload_1.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/RevolverReload_2.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/RevolverReload_3.png", Texture.class);

        assetManager.load("Images/Sprite/Weapons/T_Shotgun_SS_0.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/T_Shotgun_SS_1.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/T_Shotgun_SS_2.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/T_Shotgun_SS_3.png", Texture.class);

        assetManager.load("Images/Sprite/Weapons/T_DualSMGs_Icon.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/SMGReload_1.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/SMGReload_2.png", Texture.class);
        assetManager.load("Images/Sprite/Weapons/SMGReload_3.png", Texture.class);

// ## دشمنان ##
        assetManager.load("Images/Sprite/Monsters/ElderBrain.png", Texture.class);
        assetManager.load("Images/Sprite/Monsters/ElderBrain_Em.png", Texture.class);

        assetManager.load("Images/Sprite/Monsters/T_EyeBat_0.png", Texture.class);
        assetManager.load("Images/Sprite/Monsters/T_EyeBat_1.png", Texture.class);
        assetManager.load("Images/Sprite/Monsters/T_EyeBat_2.png", Texture.class);
        assetManager.load("Images/Sprite/Monsters/T_EyeBat_3.png", Texture.class);
        assetManager.load( "Images/Sprite/Monsters/T_EyeBat_EM.png", Texture.class);

        assetManager.load("Images/Sprite/Monsters/T_TentacleEnemy_0.png", Texture.class);
        // ## گلوله‌ها و افکت‌ها ##
        assetManager.load("Images/CharBullet.png", Texture.class);
        assetManager.load("Images/Bullet.png", Texture.class);
        assetManager.load("Images/shield_effect.png", Texture.class);
        assetManager.load("Images/Sprite/VFX/DeathFX_0.png", Texture.class);
        assetManager.load("Images/Sprite/VFX/DeathFX_1.png", Texture.class);
        assetManager.load("Images/Sprite/VFX/DeathFX_2.png", Texture.class);

        // ## موسیقی (Music) ##
//        assetManager.load("audio/background.mp3", Music.class); // موسیقی پیش‌فرض
//        assetManager.load("audio/action.mp3", Music.class);      // موسیقی اکشن
//        assetManager.load("audio/calm.mp3", Music.class);        // موسیقی آرام

        assetManager.finishLoading();

        // Prepare musics
//        defaultMusic = assetManager.get("audio/background.mp3", Music.class);
//        actionMusic = assetManager.get("audio/action.mp3", Music.class);
//        calmMusic = assetManager.get("audio/calm.mp3", Music.class);

      //  setMusic("Default");

        skin = assetManager.get("skin/biological-attack-ui.json", Skin.class);
        createIdleAnimation();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        grayscaleShader = new ShaderProgram(Gdx.files.internal("grayscale.vert"), Gdx.files.internal("grayscale.frag"));
        if (!grayscaleShader.isCompiled()) {
            Gdx.app.error("ShaderError", "Grayscale shader failed to compile: " + grayscaleShader.getLog());
        }
    }

    private static void createIdleAnimation() {
        TextureRegion[] frames = new TextureRegion[5];
        frames[0] = new TextureRegion(assetManager.get("Images/Texture2D/Character/T_Shana.png", Texture.class));
        frames[1] = new TextureRegion(assetManager.get("Images/Texture2D/Character/T_Diamond #7829.png", Texture.class));
        frames[2] = new TextureRegion(assetManager.get("Images/Texture2D/Character/T_Scarlett.png", Texture.class));
        frames[3] = new TextureRegion(assetManager.get("Images/Texture2D/Character/T_Lilith.png", Texture.class));
        frames[4] = new TextureRegion(assetManager.get("Images/Texture2D/Character/T_Dasher.png", Texture.class));

        playerIdleAnimation = new Animation<>(0.2f, frames);
        playerIdleAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public static Skin getSkin() {
        if (skin == null) {
            skin = new Skin(Gdx.files.internal("skin/biological-attack-ui.json"));
        }
        return skin;
    }

    public static BitmapFont getFont() {
        return font;
    }

    public static Music getCurrentMusic() {
        return currentMusic;
    }

    public static void setMusic(String name) {
        if (currentMusic != null) currentMusic.stop();

        switch (name) {
            case "Default":
                currentMusic = defaultMusic;
                break;
            case "Action Theme":
                currentMusic = actionMusic;
                break;
            case "Calm Theme":
                currentMusic = calmMusic;
                break;
            default:
                currentMusic = defaultMusic;
        }

        if (currentMusic != null) {
            currentMusic.setLooping(true);
            float volume = io.github.some_example_name.Models.SettingsManager.getMusicVolume();
            currentMusic.setVolume(volume);
            currentMusic.play();
        }
    }

    public static void setMusicVolume(float vol) {
        if (currentMusic != null) currentMusic.setVolume(vol);
    }

    public static Animation<TextureRegion> getPlayerIdleAnimation() {
        return playerIdleAnimation;
    }

    public static Texture getCharacterTexture() {
        return assetManager.get("Images/Texture2D/Character/T_Shana.png", Texture.class);
    }

    public static void dispose() {
        if (defaultMusic != null) defaultMusic.dispose();
        if (actionMusic != null) actionMusic.dispose();
        if (calmMusic != null) calmMusic.dispose();
        assetManager.dispose();
        if (skin != null) skin.dispose();

        if (skin != null) skin.dispose();


        if (shapeRenderer != null) shapeRenderer.dispose();
        if (grayscaleShader != null) grayscaleShader.dispose();
    }

    public static AssetManager getManager() {
        return assetManager;
    }

    public static ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public static ShaderProgram getGrayscaleShader() {
        return grayscaleShader;
    }
}
