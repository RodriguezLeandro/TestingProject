package Screen;

import com.badlogic.gdx.Preferences;
import com.mygdx.megamangame.MegamanMainClass;

/**
 * Created by Leandro on 13/01/2017.
 */

//Basicamente todo lo hace la clase padre asi que no sirve esta clase mas que solo para decorar?
public class Level1WinScreen extends WinScreen {

    public Level1WinScreen(MegamanMainClass game,Integer scoreDataInteger, LevelSelect levelSelect){
        super(game,scoreDataInteger,levelSelect);
    }
}
