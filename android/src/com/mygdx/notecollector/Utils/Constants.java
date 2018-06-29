package com.mygdx.notecollector.Utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by bill on 15/12/2015.
 */
public class Constants {

    public static final Vector2 WORLD_GRAVITY = new Vector2(0, -10f);

    public static final  int APP_WIDTH =Gdx.graphics.getWidth();//800
    public static final int APP_HEIGHT =Gdx.graphics.getHeight();//480

    public static final String WhiteKey = "data/Game Images/white_key.png";
    public static final String WhitePressedKey = "data/Game Images/white_pressed_key.png";
    public static final String BlackKey = "data/Game Images/black_key.png";
    public static final String BlackPressedKey = "data/Game Images/black_pressed_key.png";
    public static final String X="data/Game Images/x.png";



    public static  final String ButtonImage = "data/ui/buttons/appButton.png";
    public static  final String scoresImage = "data/ui/buttons/highScore.png";
    public static  final String helpImage = "data/ui/buttons/help.png";
    public static  final String settingsImage = "data/ui/buttons/settings.png";
    public static  final String exitImage = "data/ui/buttons/exit.png";
    public static  final String scoresImageP = "data/ui/buttons/highScorePressed.png";
    public static  final String helpImageP = "data/ui/buttons/helpPressed.png";
    public static  final String settingsImageP = "data/ui/buttons/settingsPressed.png";
    public static  final String exitImageP = "data/ui/buttons/exitPressed.png";

    public static String getBackgroundGame() {
        return BackgroundGame;
    }

    public static String getBackgroundMenu() {
        return BackgroundMenu;
    }

    public static  final String ButtonPressed = "data/ui/buttons/appButtonpressed.png";
    public static  final String logo = "data/ui/images/logo1000.png";
    public static  final String noConn = "data/ui/images/noWiFi.png";
    public static  final String IntroImage = "data/ui/images/IntroImage.jpg";
    public static  final String text = "data/ui/images/text.gif";

    public static  final String GameOver = "data/Game Images/failed.png";


    public static  final String spinner =   "data/ui/images/spinner.png";


    public static  final String SelectionColor = "data/ui/buttons/appButtonList.png";
    public static  final String Skin = "data/ui/skin/uiskin.json";
    public static  final String skinAtlas = "data/ui/skin/uiskin.atlas";



    public static final String root = Gdx.files.getExternalStoragePath();


    public static final String BackgroundGame= "data/Game Images/backgrounGame.jpg";
    public static final String BackgroundMenu="data/ui/images/new1080.png";



    public static final String Collector = "data/Game Images/squareGrey.png";
    public static final String BigCollector = "data/Game Images/BigCollector.png";
    public static final String VeryBigCollector = "data/Game Images/VeryBigCollector.png";
    public static final String square = "data/Game Images/squaremini.png";

    public static final float CollectorStartX480 = 400f;
    public static final float CollectorStartY480 = 240f;
    public static final float CollectorStartX720 = 540f;
    public static final float CollectorStartY720 = 360f;
    public static final float CollectorStartX1080 = 960f;
    public static final float CollectorStartY1080 = 540f;


    public static final float SquareNoteStartX = 37f;
    public static final float SquareNoteStartY = 56f;
    public static final float SquareNoteWidth  = 12f;
    public static final float SquareNoteHeight = 10f;

}
