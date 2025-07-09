package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.SecurityQuestionController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;

public class SecurityQuestionView implements Screen {
    private final Skin skin;
    private final Stage stage;

    private final TextField usernameField;
    private final TextField answerField;
    private final TextField newPasswordField;
    private final Label messageLabel;
    private final TextButton submitButton;
    private final TextButton backButton;
    private final Label securityQuestionLabel;
    private final TextButton findUserButton;
    private final Texture backgroundTexture;

    public SecurityQuestionView(SecurityQuestionController controller, Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.backgroundTexture = GameAssetManager.getManager().get("Images/Backgrounds/Menus.png", Texture.class);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter your username");

        findUserButton = new TextButton("Find User", skin);

        securityQuestionLabel = new Label("Your security question will appear here", skin);
        securityQuestionLabel.setVisible(false);

        answerField = new TextField("", skin);
        answerField.setMessageText("Your Answer");
        answerField.setVisible(false);

        newPasswordField = new TextField("", skin);
        newPasswordField.setPasswordCharacter('*');
        newPasswordField.setPasswordMode(true);
        newPasswordField.setMessageText("Enter new password");
        newPasswordField.setVisible(false);

        submitButton = new TextButton("Set New Password", skin);
        submitButton.setVisible(false);

        backButton = new TextButton("Back", skin);
        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);

        table.add(new Label("Forgot Password", skin, "title")).colspan(2).padBottom(20).row();
        table.add(new Label("Username:", skin)).left();
        table.add(usernameField).width(300).row();
        table.add(findUserButton).colspan(2).padTop(10).row();
        table.add(securityQuestionLabel).colspan(2).padTop(20).row();
        table.add(answerField).width(300).colspan(2).padTop(10).row();
        table.add(newPasswordField).width(300).colspan(2).padTop(10).row();
        table.add(submitButton).colspan(2).padTop(10).row();
        table.add(backButton).colspan(2).padTop(20).row();
        table.add(messageLabel).colspan(2).padTop(10);

        findUserButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = SecurityQuestionView.this.getUsernameField().getText().trim();
                if (username.isEmpty()) {
                    SecurityQuestionView.this.showError("Please enter a username.");
                    return;
                }
                Player player = controller.findPlayerByUsername(username);
                if (player == null) {
                    SecurityQuestionView.this.showError("User not found.");
                    return;
                }
                SecurityQuestionView.this.getSecurityQuestionLabel().setText(player.getSecurityQuestion());
                SecurityQuestionView.this.getSecurityQuestionLabel().setVisible(true);
                SecurityQuestionView.this.getAnswerField().setVisible(true);
                SecurityQuestionView.this.getNewPasswordField().setVisible(true);
                SecurityQuestionView.this.getSubmitButton().setVisible(true);
                SecurityQuestionView.this.showSuccess("User found. Please answer the question.");
            }
        });

        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.handlePasswordReset(
                    SecurityQuestionView.this.getUsernameField().getText().trim(),
                    SecurityQuestionView.this.getAnswerField().getText().trim(),
                    SecurityQuestionView.this.getNewPasswordField().getText().trim()
                );
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.backToLogin();
            }
        });
    }
    public Stage getStage() {
        return this.stage;
    }

    public void showError(String message) {
        messageLabel.setColor(Color.RED);
        messageLabel.setText(message);
    }

    public void showSuccess(String message) {
        messageLabel.setColor(Color.GREEN);
        messageLabel.setText(message);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Main.getBatch().begin();
        Main.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Main.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    public Skin getSkin() {
        return skin;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getAnswerField() {
        return answerField;
    }

    public TextField getNewPasswordField() {
        return newPasswordField;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public TextButton getSubmitButton() {
        return submitButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Label getSecurityQuestionLabel() {
        return securityQuestionLabel;
    }

    public TextButton getFindUserButton() {
        return findUserButton;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }
}
