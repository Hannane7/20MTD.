package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Models.Ability;
import java.util.List;

public class PauseView implements Screen {
    private final Screen previousScreen;
    private final Player currentPlayer;
    private final Skin skin;
    private Stage stage;
    private Table table;

    public PauseView(Screen previousScreen, Player currentPlayer, Skin skin) {
        this.previousScreen = previousScreen;
        this.currentPlayer = currentPlayer;
        this.skin = skin;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("GAME PAUSED", skin);
        table.add(titleLabel).padBottom(30).row();


        TextButton resumeBtn = new TextButton("Resume (R)", skin);
        resumeBtn.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (previousScreen instanceof InputProcessor) {
                    Gdx.input.setInputProcessor((InputProcessor) previousScreen);
                }
                Main.instance.setScreen(previousScreen);
                return true;
            }
        });
        table.add(resumeBtn).padBottom(10).row();

        TextButton giveupBtn = new TextButton("Give Up (G)", skin);
        giveupBtn.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                MainMenuController controller = new MainMenuController(currentPlayer);
                Main.instance.setScreen(new io.github.some_example_name.Views.MainMenuView(controller, skin, currentPlayer));
                return true;
            }
        });
        table.add(giveupBtn).padBottom(20).row();

        TextButton saveBtn = new TextButton("Save Game", skin);
        saveBtn.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
          //      saveGame();
                return true;
            }
        });
        table.add(saveBtn).padBottom(20).row();


        Label abLabel = new Label("Abilities Unlocked:", skin);
        table.add(abLabel).padBottom(10).row();

        List<Ability> unlocked = currentPlayer.getUnlockedAbilities();
        if (unlocked != null && !unlocked.isEmpty()) {
            for (final Ability ab : unlocked) {
                TextButton abButton = new TextButton(ab.getName() + " (" + ab.getDescription() + ")", skin);
                abButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                    @Override
                    public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                        currentPlayer.setCurrentAbility(ab);
                        return true;
                    }
                });
                table.add(abButton).padBottom(5).row();
            }
        } else {
            Label noneAb = new Label("None", skin);
            table.add(noneAb).row();
        }

        Label currentAbLabel = new Label("Current Ability:", skin);
        table.add(currentAbLabel).padTop(20).row();

        Ability current = currentPlayer.getCurrentAbility();
        String currentAb = (current != null)
            ? current.getName() + " (" + current.getDescription() + ")"
            : "None";
        Label currAbName = new Label(currentAb, skin);
        table.add(currAbName).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        stage.act(delta);
        stage.draw();

        // میانبر کلیدها (اختیاری)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            if (previousScreen instanceof InputProcessor) {
                Gdx.input.setInputProcessor((InputProcessor) previousScreen);
            }
            Main.instance.setScreen(previousScreen);        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
            MainMenuController controller = new MainMenuController(currentPlayer);
            Main.instance.setScreen(new io.github.some_example_name.Views.MainMenuView(controller, skin, currentPlayer));
        }
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }

}
