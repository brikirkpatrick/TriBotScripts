//https://www.youtube.com/watch?v=Q6SxhOAEcYs

package scripts;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import java.awt.*;

@ScriptManifest(authors = {"Brik94"}, category = "Mining", name = "TinMiner")

public class TinMiner extends Script implements Painting {

    Timer time = new Timer(3000); //3 seconds. Time it rests until another condiiton is T/F. Dynamic Sleeping.
    private final int ROCK_ID = 7484; //Copper ore. Private = only accesible within this class. Final= never change value.
    private final int[] DONTDROP = {1271, 1265}; //Multiple items we dont want it to drop.

    //Everything Script has to do before it starts.
    //GUI, Fishing Script with multiple locations, Mining script with multiple rocks and need
    //to choose a specific rock to mine
    private boolean onStart(){
        Mouse.setSpeed(150); //will speed up mouse. In this case speed up dropping.
        println("TinMiner has started.");
        return true;
    }

    @Override
    //Script begins, perform everything in this method once.
    public void run() {
        if (onStart()){
            while (true){
                sleep(loop()); //sleep in TriBot, sleeps for amount of time (42 milliseconds)
            }
        }
    }

    //Custom Method that only returns 1 item of the array.
    public RSObject findNearest(int distance, int...ids){
        RSObject[] objects = Objects.findNearest(distance, ids);

        for(RSObject object : objects){
            if (object != null){
                return object;
            }
        }
        return null;
    }

    //Most programming will happen in here.
    private int loop(){
        if(Inventory.isFull()){
            Inventory.dropAllExcept(DONTDROP);
        } else{
            //Can see Debug > Animation to get number.
            if(Player.getRSPlayer().getAnimation() == -1 ){ //-1 means doing nothing/standing
                RSObject rock = findNearest(15, ROCK_ID); //now returns 1 object, one nearest to us.

                //Check to make sure the object is there. Otherwise will throw NullPointer
                if(rock != null){
                    if(rock.isOnScreen()){
                        rock.click("Mine"); //This method chooses the click option available.


                        while (Player.getRSPlayer().getAnimation() == -1 && time.isRunning()){
                            sleep(10);
                        }
                    }
                }
            }
        }
        return 42; //Loop is completed, will wait after this amount of time.
    }

    @Override
    public void onPaint(Graphics g) {
        g.setColor(Color.GREEN);
        g.drawString("Script running: TinMiner", 244, 166);
    }
}
