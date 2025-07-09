package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.Controllers.RegisterMenuController;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Views.RegisterMenuView;

public class Main extends Game {
    public static Main instance;
    private static SpriteBatch batch;

    public enum GameState { PLAYING, PAUSED }
    public static GameState gameState;

    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();
        GameAssetManager.loadAssets();
        gameState = GameState.PLAYING;
        setScreen(new RegisterMenuView(this, new RegisterMenuController(), GameAssetManager.getSkin()));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        if (batch != null) batch.dispose();
        GameAssetManager.dispose();
    }

    @Override
    public void setScreen(Screen screen) {
        setScreen(screen, true);
    }

    public void setScreen(Screen newScreen, boolean disposeOld) {
        if (disposeOld && this.screen != null) {
            this.screen.dispose();
        }
        super.setScreen(newScreen);
    }

    public static SpriteBatch getBatch() { return batch; }
    public static int getWidth() { return Gdx.graphics.getWidth(); }
    public static int getHeight() { return Gdx.graphics.getHeight(); }
}
