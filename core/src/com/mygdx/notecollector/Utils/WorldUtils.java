package com.mygdx.notecollector.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by bill on 15/12/2015.
 */
public class WorldUtils {
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private static Touchpad.TouchpadStyle style;
    private static Skin skin;
    private static Drawable background;
    private static Drawable knob;

    public static World CreateWorld(){
        return new World(Constants.WORLD_GRAVITY,true);
    }

    public static Body CreateCollector(World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(Constants.CollectorStartX, Constants.CollectorStartY);
        /*if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            bodyDef.position.set(Constants.CollectorStartX720, Constants.CollectorStartY720);
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            bodyDef.position.set( Constants.CollectorStartX1080, Constants.CollectorStartY1080);
        }*/
        //bodyDef.position.set(Constants.CollectorStartX, Constants.CollectorStartY);
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((Constants.APP_WIDTH/ 2)+120, Constants.APP_HEIGHT /2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        body.createFixture(fixtureDef);
        body.resetMassData();
       // CollectorUserData userData = new CollectorUserData(Constants.CollectorWidth,Constants.CollectorHeight);
        //body.setUserData(userData);
        shape.dispose();
        return body;
    }

    public static Body CreateSquareNotes(World world){
       // SquareNoteType squareNoteType = SquareNoteType.SquarwNoteGrey;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(Constants.SquareNoteStartX, Constants.SquareNoteStartY);
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.SquareNoteWidth/ 2, Constants.SquareNoteHeight /2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        body.createFixture(fixtureDef);
        body.resetMassData();
       // SquareNotesUserData userData = new SquareNotesUserData(Constants.SquareNoteWidth, Constants.SquareNoteHeight);
       // body.setUserData(userData);
        shape.dispose();
        return body;
    }

    public static Body CreatePianoKeyboard(World world){
        // SquareNoteType squareNoteType = SquareNoteType.SquarwNoteGrey;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(36f*VIEWPORT_WIDTH/800, 56f*VIEWPORT_HEIGHT/480);//????
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5f, 5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        body.createFixture(fixtureDef);
        body.resetMassData();
       // PianoUserData userData = new PianoUserData(728f,56f);
        //body.setUserData(userData);
        shape.dispose();
        return body;
    }


}
