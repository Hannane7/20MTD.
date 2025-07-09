package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Controllers.ProfileController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;

public class ProfileView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final ProfileController controller;
    private final Player player;
    private Texture backgroundTexture;

    public ProfileView(Player player, Skin skin) {
        this.player = player;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.controller = new ProfileController();
        this.backgroundTexture = new Texture("Images/Backgrounds/Menus.png");
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        table.add(new Label("Player Profile", skin, "title")).colspan(2).padBottom(20).row();

        final Image avatarImg = new Image(new Texture(Gdx.files.internal(player.getAvatarPath())));
        table.add(avatarImg).size(64, 64).padBottom(10).colspan(2).row();

        final SelectBox<String> avatarBox = new SelectBox<>(skin);
        avatarBox.setItems("Images/avatar/avatar1.png", "Images/avatar/avatar2.png", "Images/avatar/avatar3.png", "Images/avatar/avatar4.png");
        avatarBox.setSelected(player.getAvatarPath());
        TextButton setAvatarFromListBtn = new TextButton("Set from List", skin);
        table.add(avatarBox).width(250).padRight(10);
        table.add(setAvatarFromListBtn).row();

        final TextField avatarPathField = new TextField("", skin);
        avatarPathField.setMessageText("Enter full path to image...");
        TextButton setAvatarFromPathBtn = new TextButton("Set from Path", skin);
        table.add(avatarPathField).width(250).padTop(5).padRight(10);
        table.add(setAvatarFromPathBtn).padTop(5).row();

        table.add(new Label("Username:", skin)).left().padTop(20);
        final TextField usernameField = new TextField(player.getUsername(), skin);
        table.add(usernameField).width(200).padTop(20).row();
        TextButton changeUsernameBtn = new TextButton("Change Username", skin);
        table.add(changeUsernameBtn).colspan(2).padBottom(10).row();

        table.add(new Label("New Password:", skin)).left();
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        table.add(passwordField).width(200).row();
        TextButton changePasswordBtn = new TextButton("Change Password", skin);
        table.add(changePasswordBtn).colspan(2).padBottom(20).row();

        TextButton deleteBtn = new TextButton("Delete Account", skin);
        deleteBtn.getLabel().setColor(Color.RED);
        TextButton backButton = new TextButton("Back", skin);
        table.add(deleteBtn).colspan(2).padBottom(20).row();
        table.add(backButton).colspan(2).row();

        final Label msgLabel = new Label("", skin);
        table.add(msgLabel).colspan(2).padTop(15).row();

        setAvatarFromListBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newAvatar = avatarBox.getSelected();
                controller.changeAvatar(player, newAvatar);
                avatarImg.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.internal(newAvatar))));
                msgLabel.setText("Avatar changed!");
                msgLabel.setColor(Color.GREEN);
            }
        });

        setAvatarFromPathBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String path = avatarPathField.getText();
                if (Gdx.files.absolute(path).exists()) {
                    controller.changeAvatar(player, path);
                    avatarImg.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.absolute(path))));
                    msgLabel.setText("Avatar changed successfully!");
                    msgLabel.setColor(Color.GREEN);
                } else {
                    msgLabel.setText("Error: File not found at that path.");
                    msgLabel.setColor(Color.RED);
                }
            }
        });

        changeUsernameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newName = usernameField.getText().trim();
                if (newName.isEmpty()) {
                    msgLabel.setText("Username cannot be empty.");
                    msgLabel.setColor(Color.RED);
                    return;
                }
                if (controller.isUsernameDuplicate(newName, player)) {
                    msgLabel.setText("This username is already taken.");
                    msgLabel.setColor(Color.RED);
                    return;
                }
                controller.changeUsername(player, newName);
                msgLabel.setText("Username changed successfully!");
                msgLabel.setColor(Color.GREEN);
            }
        });

        changePasswordBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String pass = passwordField.getText();
                if (!controller.isPasswordStrong(pass)) {
                    msgLabel.setText("Password is too weak!\n(8+ chars, 1 uppercase, 1 number, 1 symbol)");
                    msgLabel.setColor(Color.RED);
                    return;
                }
                controller.changePassword(player, pass);
                passwordField.setText("");
                msgLabel.setText("Password changed successfully!");
                msgLabel.setColor(Color.GREEN);
            }
        });

        deleteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.deleteAccount(player);
                msgLabel.setText("Account deleted! Returning to main menu...");
                msgLabel.setColor(Color.YELLOW);
                stage.addAction(
                    Actions.sequence(
                        Actions.delay(2.0f),
                        Actions.run(() -> Main.instance.setScreen(new MainMenuView(new MainMenuController(null), GameAssetManager.getSkin(), null)))
                    )
                );
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.instance.setScreen(new MainMenuView(new MainMenuController(player), GameAssetManager.getSkin(), player));
            }
        });
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

    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
