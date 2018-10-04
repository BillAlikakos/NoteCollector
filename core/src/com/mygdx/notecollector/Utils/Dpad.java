package com.mygdx.notecollector.Utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.notecollector.GameStage.GameStage;

public class Dpad
{
    private GameStage stage;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public Dpad(GameStage stage)
    {
        this.stage = stage;
    }

    public void setupDpad()
    {
        this.stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        System.out.println("Up");
                        upPressed = true;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = true;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = false;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = false;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        break;
                }
                return true;
            }
        });
        //Gdx.input.setInputProcessor(this);
        Table table = new Table();
        table.left().bottom();

        Image upImg = new Image(new Texture(Constants.DpadUp));
        upImg.setSize(150, 150);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Up pressed !");
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Up released !");
                upPressed = false;
            }
        });

        Image downImg = new Image(new Texture(Constants.DpadDown));

        downImg.setSize(150, 150);
        downImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                System.out.println("Down pressed !");
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image rightImg = new Image(new Texture(Constants.DpadRight));
        rightImg.setSize(150, 150);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                rightPressed = true;
                System.out.println("Right pressed !");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image leftImg = new Image(new Texture(Constants.DpadLeft));
        leftImg.setSize(150, 150);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                System.out.println("Left pressed !");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });
        upImg.setDebug(true);
        downImg.setDebug(true);
        leftImg.setDebug(true);
        rightImg.setDebug(true);
        table.add();
        table.add(upImg).size(upImg.getWidth(), upImg.getHeight());
        table.add();
        table.row().pad(5, 5, 5, 5);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add();
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());
        table.row().padBottom(5);
        table.add();
        table.add(downImg).size(downImg.getWidth(), downImg.getHeight());
        table.add();
        table.setY(70f);
        stage.addActor(table);

    }

    public boolean isUpPressed()
    {
        return upPressed;
    }

    public boolean isDownPressed()
    {
        return downPressed;
    }

    public boolean isLeftPressed()
    {
        return leftPressed;
    }

    public boolean isRightPressed()
    {
        return rightPressed;
    }
}