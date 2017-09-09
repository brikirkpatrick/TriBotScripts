package scripts.RoguesDen;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;

/**
 * Created by Bri on 8/16/2017.
 * Script Constants
 */
public class Const{

    public static final RSArea ROGUES_DEN_START = new RSArea(new RSTile(3042, 4966, 1), new RSTile(3061, 4982, 1));

    //Important method. Waits until the Player is idle using my personally developed algorithm.
    public static void waitUntilIdle(){
        long t = System.currentTimeMillis();

        while(Timing.timeFromMark(t) < General.random(400, 800)){ //400, 800
            General.sleep(400, 800);

            if(Player.isMoving() || Player.getAnimation() != -1){
                t = System.currentTimeMillis();
                continue;
            }

            General.sleep(40, 80);

            if(Player.getAnimation() == -1)
                break;
        }
    }

    //Should Player ever be in an unmapped area, this method will act as the fail-safe
    //and teleport the player to the beginning of the game.
    public static void teleportOut(){

    }
}
