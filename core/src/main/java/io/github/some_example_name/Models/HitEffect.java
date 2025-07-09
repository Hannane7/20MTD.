package io.github.some_example_name.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HitEffect {
    private static Animation<TextureRegion> hitAnimation;
    private Vector2 position;
    private float stateTime = 0;
    private boolean finished = false;

    public HitEffect(Vector2 position) {
        this.position = new Vector2(position);


        if (hitAnimation == null) {
            TextureRegion[] frames = {
                new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/VFX/DeathFX_0.png", Texture.class)),
                new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/VFX/DeathFX_1.png", Texture.class)),
                new TextureRegion(GameAssetManager.getManager().get("Images/Sprite/VFX/DeathFX_2.png", Texture.class))
            };
            hitAnimation = new Animation<>(0.05f, frames);
        }
    }

    public void update(float delta) {
        stateTime += delta;
        if (hitAnimation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public void draw(Batch batch, float offsetX, float offsetY) {
        if (!finished) {
            TextureRegion currentFrame = hitAnimation.getKeyFrame(stateTime, false);
            batch.draw(currentFrame, position.x + offsetX, position.y + offsetY);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
