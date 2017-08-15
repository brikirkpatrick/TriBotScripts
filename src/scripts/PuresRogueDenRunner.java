package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

/**
 * Created by Bri on 8/13/2017.
 */
@ScriptManifest(authors = {"PureImagination"}, category = "Mini games", name = "PuresRogueDenRunner",
        description = "Rogues Den Mini Game Runner.")

public class PuresRogueDenRunner extends Script {
    private final int RICHARD_ID = 3189, JEWEL_ID = 5561;

    @Override
    public void run() {
        //startMiniGame();
        partOne();
    }

    //Seems to work fine as long as Brian O'Richard is in sight.
    //Maybe walk to Brian before start?
    //IMPORTANT: MAKE SURE GEM IS ONLY ITEM WE HAVE
    private boolean startMiniGame(){
        RSNPC[] richard = NPCs.findNearest(50, RICHARD_ID);
        Camera.turnToTile(richard[0]);

        if (NPCChat.getSelectOptionInterface() == null){
            Walking.walkTo(richard[0]);
            waitUntilIdle();
            richard[0].click("Talk-to");
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100);
                    return NPCChat.clickContinue(true);
                }
            }, General.random(750, 1000));
        }

        //Continues until chat dialogue is over. Mini game should be started.
        while(NPCChat.getMessage() != null || NPCChat.getSelectOptionInterface() != null || NPCChat.getClickContinueInterface() != null){ //continue loop until interface is done.
            if(NPCChat.getSelectOptionInterface() != null)
                NPCChat.selectOption("I want to try the maze again!", true);
            else
                NPCChat.clickContinue(true);
        }

        //fail-safe. 100% Mini game has begun if we have the jewel. Otherwise, try again.
        if(Inventory.getCount(JEWEL_ID) == 1){
            println("Mini Game has begun.");
            return true;
        }else {
            println("Failed at starting mini game. Re-attempting.");
            startMiniGame();
        }
        return false;
    }

    private void partOne(){
        RSTile firstDoorTile = new RSTile(3056, 4990, 1);
        Walking.blindWalkTo(firstDoorTile, null, 0);
        waitUntilIdle();

        RSObject[] firstDoor = Objects.findNearest(10, 7256);
        DynamicClicking.clickRSObject(firstDoor[0], "Open");
        waitUntilIdle();

        RSObject[] contortionBar = Objects.findNearest(10, 7251);
        DynamicClicking.clickRSObject(contortionBar[0], 1);
        //GOOD POINT
        while(Camera.getCameraRotation() != General.random(15, 35)) { //while loops might weight down cpu...
            Camera.setCameraAngle(100);
            Camera.setCameraRotation(General.random(15, 35)); //CRAZY CAMERA???
        }
        waitUntilIdle();
        Walking.clickTileMS(new RSTile(3039, 4997, 1), 1); //Tile after Pendulum
        //SEEMS TO BE GOOD
        while(Camera.getCameraRotation() != General.random(112, 118)) {
            Camera.setCameraRotation(General.random(112, 118));
        }
        waitUntilIdle();

        RSTile tile1 = new RSTile(3029, 5003, 1); //2nd tile after pendulum. Infront of Floor trap.
        Walking.clickTileMS(tile1, 1);
        waitUntilIdle();

        RSObject[] door2 = Objects.findNearest(10, 7255);
        DynamicClicking.clickRSObject(door2[0], 1);
        waitUntilIdle();

        while(Camera.getCameraRotation() < 36 && Camera.getCameraRotation() > 14) {
            Camera.setCameraRotation(10);
        }
        RSTile tile2 = new RSTile(3011, 5005, 1);
        Walking.clickTileMS(tile2, 1);
        waitUntilIdle();
    }

    private void waitUntilIdle(){
        long t = System.currentTimeMillis();

        while(Timing.timeFromMark(t) < General.random(400, 800)){ //400, 800
            sleep(400, 800);

            if(Player.isMoving() || Player.getAnimation() != -1){
                t = System.currentTimeMillis();
                continue;
            }

            sleep(40, 80);

            if(Player.getAnimation() == -1)
                break;
        }
    }

}
