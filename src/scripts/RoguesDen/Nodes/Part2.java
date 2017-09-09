package scripts.RoguesDen.Nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.RoguesDen.Const;
import scripts.api.Node;

/**
 * Created by Bri on 8/16/2017.
 */
public class Part2 extends Node {
    private final RSArea GRoom = new RSArea(new RSTile(2963, 5028, 1), new RSTile(2953, 5035, 1));
    private final RSArea HRoom = new RSArea(new RSTile(2956, 5068, 1), new RSTile(2966, 5046, 1));
    private final RSArea IRoom = new RSArea(new RSTile(2967, 5057, 1), new RSTile(2957, 5068, 1));
    private final RSArea JRoom = new RSArea(new RSTile(2958, 5072, 1), new RSTile(2954, 5094, 1));
    private final RSArea KRoom = new RSArea(new RSTile(2974, 5098, 1), new RSTile(2950, 5110, 1));
    private final RSArea LRoom = new RSArea(new RSTile(2970, 5095, 1), new RSTile(3055, 5085, 1));

    private State SCRIPT_STATE = getState();

    private enum State{CROSS_LEDGE, CROSS_FLOOR_TRAP, ENTER_PASSAGEWAY, WALK_TO_RUN_ROOM, RUN_TO_PASSAGEWAY}

    private State getState() {
        RSTile myPos = Player.getPosition();
        if(GRoom.contains(myPos)){
            return State.CROSS_LEDGE;
        }
        else if (HRoom.contains(myPos)){
            return State.CROSS_FLOOR_TRAP;
        }
        else if (IRoom.contains(myPos)){
            return State.ENTER_PASSAGEWAY;
            }
        else if (JRoom.contains(myPos)){
            return State.WALK_TO_RUN_ROOM;
        }
        else if(KRoom.contains(myPos)){
            return State.RUN_TO_PASSAGEWAY;
        }
        else if(LRoom.contains(myPos)){
            return State.CROSS_LEDGE;

        }
        return null;
    }

    @Override
    public void execute() {
        SCRIPT_STATE = getState();
        if(SCRIPT_STATE ==  null){
            General.println("Possible NPE. Script State is null.");
        }else {
            switch (SCRIPT_STATE) {
                case CROSS_LEDGE:
                    crossLedge();
                    break;
                case CROSS_FLOOR_TRAP:
                    crossFloorTrap();
                    break;
                case WALK_TO_RUN_ROOM:
                    walkThroughRoom();
                    break;
                case RUN_TO_PASSAGEWAY:
                    runThroughRoom();
                default:
                    General.println("State error, breaking");
                    //will put Const.teleportOut() here.
                    break;
            }
        }

    }

    @Override
    public boolean validate() {
        RSTile myPos = Player.getPosition();
        if(GRoom.contains(myPos) || HRoom.contains(myPos) || IRoom.contains(myPos) || JRoom.contains(myPos) || KRoom.contains(myPos) || LRoom.contains(myPos)){
            getState();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return SCRIPT_STATE != null ? SCRIPT_STATE.toString() : "";
    }

    public void crossLedge(){
        RSTile myPos = Player.getPosition();
        if(GRoom.contains(myPos)){
            RSObject[] ledge = Objects.findNearest(10, 7239);
            if(ledge.length > 0 && !myPos.equals(new RSTile(2958, 5035)) && !Player.isMoving());
            DynamicClicking.clickRSObject(ledge[0], 1); //Climb across the ledge.
            Const.waitUntilIdle();
            Walking.walkTo(new RSTile(2963, 5047, 1));
            Const.waitUntilIdle();
            Walking.walkTo(new RSTile(2963, 5050, 1)); //tile before floorTrap.
            Const.waitUntilIdle();
        }else if (LRoom.contains(myPos)){
            Walking.clickTileMM(new RSTile(2981, 5088), 1);
            Const.waitUntilIdle();
            RSObject[] ledge = Objects.findNearest(10, 7240);
            if(ledge.length > 0){
                DynamicClicking.clickRSObject(ledge[0], 1); //Climb across the ledge.
                Const.waitUntilIdle();
            }
        }

    }

    public void crossFloorTrap(){
        RSTile myPos = Player.getPosition();
        RSTile firstTile = new RSTile(2963, 5051, 1);
        RSTile secondTile = new RSTile(2963, 5052, 1);
        //RSObject[] passageWay = Objects.findNearest(5, 7219);
        General.println("method accessed.");

        if(firstTile.isOnScreen() && !myPos.equals(firstTile) && !myPos.equals(secondTile)) {
            firstTile.click("Search");
            General.println("First tile");
            Const.waitUntilIdle();
        }
        else if (myPos.equals(firstTile)) {
            secondTile.click("Search");
            General.println("2nd tile");
            Const.waitUntilIdle();
        }
        else if(myPos.equals(secondTile) || IRoom.contains(myPos)){
            Walking.clickTileMM(new RSTile(2957, 5067, 1), 1);
            General.println("going to passageway tile");
            Const.waitUntilIdle();
            RSObject[] passageWay = Objects.findNearest(5, 7219);
            if (passageWay.length > 0)
                DynamicClicking.clickRSObject(passageWay[0], 1);
        }
        Const.waitUntilIdle();
        General.println("what do...");
        //Walking.walkTo(new RSTile(2964, 5059, 1));
    }

    //Want to walk through room.
    public void walkThroughRoom(){
        RSTile myPos = Player.getPosition();
        RSTile tileAfterBlade = new RSTile(2957, 5076, 1);

        if (JRoom.contains(myPos) && !myPos.equals(tileAfterBlade)){
            Walking.clickTileMS(tileAfterBlade, 1);
            Const.waitUntilIdle();
        }else{
            Walking.blindWalkTo(new RSTile(2955, 5091));
            Const.waitUntilIdle();
            RSObject[] passageWay = Objects.findNearest(5, 7219);
            if (passageWay.length > 0)
                DynamicClicking.clickRSObject(passageWay[0], 1);
        }
    }

    //This room you MUST run.
    public void runThroughRoom(){

        if (Game.getRunEnergy() > 50){
            Walking.clickTileMM(new RSTile(2971, 5099, 1), 1);
            Const.waitUntilIdle();
            RSObject[] passageWay = Objects.findNearest(5, 7219);
            RSObject[] grill = Objects.findNearest(5, 7255);
            if (passageWay.length > 0) {
                DynamicClicking.clickRSObject(passageWay[0], 1);
                Const.waitUntilIdle();
                DynamicClicking.clickRSObject(grill[0], 1);

            }
        }else{ //wait.
            General.sleep(500, 1000);
            General.println("Waiting for run to regenerate over 50%");
        }
    }
}

/**
 * 1) After ledge, walks back over it.
 */