package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;

public class GameOverView implements Screen {
    private final Stage stage;
    private final Player player;

    public GameOverView(Player player, boolean isVictory) {
        this.player = player;
        this.stage = new Stage(new ScreenViewport());
        Skin skin = GameAssetManager.getSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        String titleText = isVictory ? "VICTORY!" : "GAME OVER";
        Label titleLabel = new Label(titleText, skin, "title");
        if (isVictory) {
            titleLabel.setColor(com.badlogic.gdx.graphics.Color.GOLD);
        } else {
            titleLabel.setColor(com.badlogic.gdx.graphics.Color.RED);
        }
        table.add(titleLabel).padBottom(30).row();

        if (player != null) {
            String detailsText = String.format(
                "Character: %s\nKills: %d\nTime: %d seconds\nScore: %d",
                player.getUsername(),
                player.getKills(),
                (int) player.getSurviveTime(),
                player.getScore()
            );
            Label detailsLabel = new Label(detailsText, skin);
            detailsLabel.setAlignment(Align.center);
            table.add(detailsLabel).padBottom(30).row();
        }

        Label promptLabel = new Label("Press ESC to return to main menu.", skin);
        table.add(promptLabel).padTop(20).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // پاک کردن صفحه با یک رنگ ثابت

        // چک کردن کلید ESC برای بازگشت
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Main.instance.setScreen(new MainMenuView(new MainMenuController(player), GameAssetManager.getSkin(), player));
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
