package io.github.some_example_name.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class CharacterAnimator {
    public enum State { IDLE, RUN, WALK }
    private Animation<TextureRegion> idleAnimation, runAnimation, walkAnimation;
    private State currentState = State.IDLE;
    private float stateTime = 0;
    private int width, height;

    public CharacterAnimator(String characterName) {
        switch (characterName.toUpperCase()) {
            case "SHANA":
                setupAnimations("T_Shana.png", 8, 32, 32);
                break;
            case "DIAMOND":
                setupAnimations("T_Diamond #7829.png", 8, 32, 32);
                break;
            case "SCARLET":
                setupAnimations("T_Scarlett.png", 8, 32, 32);
                break;
            case "LILITH":
                setupAnimations("T_Lilith.png", 8, 32, 32);
                break;
            case "DASHER":
                setupAnimations("T_Dasher.png", 12, 32, 32);
                break;
            default:
                setupAnimations("T_Shana.png", 8, 32, 32);
                break;
        }
    }

    private void setupAnimations(String fileName, int framesPerRow, int frameWidth, int frameHeight) {
        Texture sheet = GameAssetManager.getManager().get("Images/Texture2D/Character/" + fileName, Texture.class);
        TextureRegion[][] regions = TextureRegion.split(sheet, frameWidth, frameHeight);

        if (regions.length >= 3) {
            idleAnimation = new Animation<>(0.1f, new Array<>(regions[0]), Animation.PlayMode.LOOP);
            runAnimation  = new Animation<>(0.09f, new Array<>(regions[1]), Animation.PlayMode.LOOP);
            walkAnimation = new Animation<>(0.1f, new Array<>(regions[2]), Animation.PlayMode.LOOP);
        }

        width = frameWidth;
        height = frameHeight;
    }

    public void update(float delta, State state) {
        this.currentState = state;
        stateTime += delta;
    }

    public void render(Batch batch, float x, float y) {
        Animation<TextureRegion> anim = idleAnimation;
        if (anim == null) return;

        switch (currentState) {
            case RUN:  anim = runAnimation; break;
            case WALK: anim = walkAnimation; break;
            case IDLE:
            default:   anim = idleAnimation;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
