package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Models.SettingsManager;

public class SettingsView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private Texture backgroundTexture;
    private final Player currentPlayer;

    public SettingsView(Skin skin, Player currentPlayer) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.currentPlayer = currentPlayer;

        Gdx.input.setInputProcessor(stage);
        SettingsManager.loadSettings();
        this.backgroundTexture = new Texture("Images/Backgrounds/Menus.png");
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        table.add(new Label("Settings", skin, "title")).colspan(2).padBottom(30).row();

        final Label volumeLabel = new Label("Music Volume: " + (int)(SettingsManager.getMusicVolume() * 100) + "%", skin);
        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(SettingsManager.getMusicVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float vol = volumeSlider.getValue();
                SettingsManager.setMusicVolume(vol);
                if (GameAssetManager.getCurrentMusic() != null) {
                    GameAssetManager.setMusicVolume(vol);
                }
                volumeLabel.setText("Music Volume: " + (int)(vol * 100) + "%");
            }
        });
        table.add(volumeLabel).left().padBottom(5);
        table.row();
        table.add(volumeSlider).width(300).colspan(2).padBottom(20).row();


        final SelectBox<String> musicSelectBox = new SelectBox<>(skin);
        musicSelectBox.setItems("Default", "Action Theme", "Calm Theme");
        musicSelectBox.setSelected(SettingsManager.getMusicTrack());
        musicSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = musicSelectBox.getSelected();
                SettingsManager.setMusicTrack(selected);
                GameAssetManager.setMusic(selected);
            }
        });
        table.add(new Label("Background Music:", skin)).left();
        table.add(musicSelectBox).width(200).padBottom(10).row();


        final CheckBox sfxCheckbox = new CheckBox(" Enable Sound Effects (SFX)", skin);
        sfxCheckbox.setChecked(SettingsManager.isSfxEnabled());
        sfxCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setSfxEnabled(sfxCheckbox.isChecked());
            }
        });
        table.add(sfxCheckbox).colspan(2).left().padBottom(10).row();

        final SelectBox<String> controlSelectBox = new SelectBox<>(skin);
        controlSelectBox.setItems("WASD", "Arrow Keys");
        controlSelectBox.setSelected(SettingsManager.getControlType());
        controlSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setControlType(controlSelectBox.getSelected());
            }
        });
        table.add(new Label("Controls:", skin)).left();
        table.add(controlSelectBox).width(200).padBottom(10).row();

        final CheckBox autoReloadCheckbox = new CheckBox(" Enable Auto-Reload", skin);
        autoReloadCheckbox.setChecked(SettingsManager.isAutoReload());
        autoReloadCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setAutoReload(autoReloadCheckbox.isChecked());
            }
        });
        table.add(autoReloadCheckbox).colspan(2).left().padBottom(10).row();


        final CheckBox grayscaleCheckbox = new CheckBox(" Enable Grayscale Mode", skin);
        grayscaleCheckbox.setChecked(SettingsManager.isGrayscale());
        grayscaleCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setGrayscale(grayscaleCheckbox.isChecked());
            }
        });
        table.add(grayscaleCheckbox).colspan(2).left().padBottom(20).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.instance.setScreen(new MainMenuView(new MainMenuController(currentPlayer), skin, currentPlayer));
            }
        });
        table.add(backButton).colspan(2).padTop(20);
    }

    @Override
    public void render(float delta) {
        com.badlogic.gdx.utils.ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);

        Main.getBatch().begin();
        Main.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Main.getBatch().end();

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    // متدهای دیگر Screen Interface
    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
