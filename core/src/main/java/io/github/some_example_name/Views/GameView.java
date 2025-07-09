package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.GameController;
import io.github.some_example_name.Controllers.PlayerController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.*;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class GameView implements Screen, InputProcessor {
    private Stage stage;
    private GameController controller;
    private BitmapFont font;
    private ShaderProgram grayscaleShader;
    private TextField cheatField;
    private boolean cheatMode = false;
    private float offsetX, offsetY;
    private Texture boundaryShieldTexture;


    public GameView(GameController controller) {
        this.controller = controller;
        init();
    }

    public GameView(Player player, PreGame preGame) {
        this.controller = new GameController(player, preGame);
        init();
    }

    private void init() {
        controller.setView(this);
        Gdx.input.setInputProcessor(this);
        font = GameAssetManager.getFont();
        stage = new Stage(new ScreenViewport());
        boundaryShieldTexture = GameAssetManager.getManager().get("Images/shield_effect.png");

        cheatField = new TextField("", GameAssetManager.getSkin());
        cheatField.setPosition(10, 10);
        cheatField.setSize(Gdx.graphics.getWidth() - 20, 30);
        cheatField.setVisible(false);
        stage.addActor(cheatField);

        grayscaleShader = new ShaderProgram(Gdx.files.internal("grayscale.vert"), Gdx.files.internal("grayscale.frag"));
        if (!grayscaleShader.isCompiled()) {
            Gdx.app.error("ShaderError", "Grayscale shader failed to compile: " + grayscaleShader.getLog());
        }
    }

    @Override
    public void render(float delta) {
        if (Main.gameState == Main.GameState.PLAYING) {
            controller.updateGame(delta);
        }

        ScreenUtils.clear(0, 0, 0, 1);
        Player player = controller.getPlayerController().getPlayer();
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        offsetX = centerX - player.getPosX();
        offsetY = centerY - player.getPosY();

        if (SettingsManager.isGrayscale()) {
            Main.getBatch().setShader(GameAssetManager.getGrayscaleShader());
        }

        Main.getBatch().begin();

        controller.getWorldController().render(offsetX, offsetY);
        player.render(centerX, centerY);
        controller.getWeaponController().render(Main.getBatch(), offsetX, offsetY);
        if (controller.isBoundaryShieldActive()) {
            Vector2 shieldCenter = controller.getBoundaryShieldCenter();
            float shieldRadius = controller.getBoundaryShieldRadius();
            float shieldDiameter = shieldRadius * 2;

            Main.getBatch().draw(boundaryShieldTexture,
                (shieldCenter.x - shieldRadius) + offsetX,
                (shieldCenter.y - shieldRadius) + offsetY,
                shieldDiameter,
                shieldDiameter
            );
        }
        String hudText = "HP: " + player.getPlayerHealth() + " | Kills: " + player.getKills() + " | Time: " + (int) player.getSurviveTime() + " | Ammo: " + controller.getWeaponController().getWeapon().getAmmo();
        font.draw(Main.getBatch(), hudText, 10, Gdx.graphics.getHeight() - 10);
        font.draw(Main.getBatch(), "Level: " + player.getLevel(), 10, Gdx.graphics.getHeight() - 55);
        font.draw(Main.getBatch(), "XP: " + player.getXP() + " / " + player.getXPToNextLevel(), 10, Gdx.graphics.getHeight() - 70);

        if (controller.isAutoAimActive()) {
            font.setColor(Color.CYAN);
            font.draw(Main.getBatch(), "Auto-Aim: ON", Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 10);
            font.setColor(Color.WHITE);
        }

        Main.getBatch().end();
        Main.getBatch().setShader(null);

        ShapeRenderer shapeRenderer = GameAssetManager.getShapeRenderer();
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float barX = 10, barY = Gdx.graphics.getHeight() - 50;
        float barWidth = 200, barHeight = 10;
        float progress = (float) player.getXP() / player.getXPToNextLevel();

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);

        shapeRenderer.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Main.gameState == Main.GameState.PAUSED) {
            return false;
        }

        PlayerController pc = controller.getPlayerController();
        String controls = SettingsManager.getControlType();
        if (controls.equals("WASD")) {
            if (keycode == Input.Keys.W) pc.setMoveUp(true);
            if (keycode == Input.Keys.S) pc.setMoveDown(true);
            if (keycode == Input.Keys.A) pc.setMoveLeft(true);
            if (keycode == Input.Keys.D) pc.setMoveRight(true);
        } else {
            if (keycode == Input.Keys.UP) pc.setMoveUp(true);
            if (keycode == Input.Keys.DOWN) pc.setMoveDown(true);
            if (keycode == Input.Keys.LEFT) pc.setMoveLeft(true);
            if (keycode == Input.Keys.RIGHT) pc.setMoveRight(true);
        }

        switch (keycode) {
            // کلیدهای اصلی بازی
            case Input.Keys.R:
                controller.getWeaponController().reload();
                break;
            case Input.Keys.SPACE:
                controller.toggleAutoAim();
                break;
            case Input.Keys.ESCAPE:
                Main.gameState = Main.GameState.PAUSED;
                Main.instance.setScreen(new PauseView(this, controller, GameAssetManager.getSkin()), false);
                break;
            // کلیدهای تقلب جدید
            case Input.Keys.G: // G برای GodMode
                controller.activateGodMode();
                break;
            case Input.Keys.L: // L برای LevelUp
                controller.activateLevelUp();
                break;
            case Input.Keys.V: // V برای Revive
                controller.activateRevive();
                break;
            case Input.Keys.T: // T برای Time
                controller.activateDecreaseTime();
                break;
            case Input.Keys.B: // B برای Boss
                controller.activateGoToBoss();
                break;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (cheatMode) return true;
        PlayerController pc = controller.getPlayerController();
        String controls = SettingsManager.getControlType();
        if (controls.equals("WASD")) {
            if (keycode == Input.Keys.W) pc.setMoveUp(false);
            if (keycode == Input.Keys.S) pc.setMoveDown(false);
            if (keycode == Input.Keys.A) pc.setMoveLeft(false);
            if (keycode == Input.Keys.D) pc.setMoveRight(false);
        } else {
            if (keycode == Input.Keys.UP) pc.setMoveUp(false);
            if (keycode == Input.Keys.DOWN) pc.setMoveUp(false);
            if (keycode == Input.Keys.LEFT) pc.setMoveLeft(false);
            if (keycode == Input.Keys.RIGHT) pc.setMoveRight(false);
        }
        return true;
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
    @Override public void show() {
        Gdx.input.setInputProcessor(this);
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (cheatMode || controller.isAutoAimActive()) return false;
        // ----------------------------------------------------
        float worldX = screenX - offsetX;
        float worldY = Gdx.graphics.getHeight() - screenY - offsetY;
        controller.getWeaponController().shoot(worldX, worldY);
        return true;
    }


    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    public Stage getStage() { return stage; }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public GameController getController() {
        return this.controller;
    }
}
