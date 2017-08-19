package scripts.RoguesDen.Nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.api.Node;

/**
 * Created by Bri on 8/16/2017.
 * Areas may need tweaking.
 */
public class Part1 extends Node {
    private final RSArea ARoom = new RSArea(new RSTile(3056, 4992, 1), new RSTile(3050, 5005, 1));
    private final RSArea A2Room = new RSArea(new RSTile(3048, 4997, 1), new RSTile(3043, 4999, 1));
    private final RSArea BRoom = new RSArea(new RSTile(3039, 4995, 1), new RSTile(3024, 5003, 1));
    private final RSArea CRoom = new RSArea(new RSTile(3024, 5001, 1), new RSTile(3011, 5005, 1));
        //polygon of D-Room.
        private final RSTile[] other = new RSTile[]{new RSTile(2994, 5005, 1), new RSTile(2999, 4998, 1), new RSTile(3011, 5005, 1)};
    private final RSArea DRoom = new RSArea(other);

    State state;

    private enum State{CONTORTION_BARS_PENDULUM, AVOID_TRAP, OPEN_DOOR, AVOID_TRAP2}
    public State getState(){
        RSTile myPos = Player.getPosition();
        if(ARoom.contains(myPos)){
            return State.CONTORTION_BARS_PENDULUM;
        }else if (BRoom.contains(myPos)){
            if(myPos.equals(new RSTile(3029, 5003, 1))){
                return State.OPEN_DOOR;
            }else
                return State.AVOID_TRAP;
        }else if (CRoom.contains(myPos)){
            if(myPos.equals(new RSTile(3023, 5001, 1))){
                return State.AVOID_TRAP;
            }
        }

        return null;
    }

    @Override
    public void execute() {
        this.state = getState();
        switch (this.state) {
            case OPEN_DOOR:
                openDoor();
                break;
            case AVOID_TRAP:
                avoidTrap();
                break;
            case CONTORTION_BARS_PENDULUM:
                barAndPendulum();
                break;
            default: break;

        }
    }

    @Override
    public boolean validate() {
        RSTile myPos = Player.getPosition();
        if(ARoom.contains(myPos) || BRoom.contains(myPos) || CRoom.contains(myPos) || DRoom.contains(myPos)){
            getState();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return state != null ? state.toString() : "";
    }

    //STOPPED HERE.----------------------
    public void barAndPendulum(){
        if (Player.getPosition() != new RSTile(3048, 4997, 1) && !(Player.getPosition().equals(BRoom))) {
            General.sleep(100);
            RSObject[] contortionBar = Objects.findNearest(10, 7251);
            DynamicClicking.clickRSObject(contortionBar[0], 1);
            waitUntilIdle();
        }

        //Turns camera and clicks tile after pendulum.
        while(Camera.getCameraRotation() != General.random(20, 25)) { //while loops might weight down cpu...
            Camera.setCameraAngle(100);
            Camera.setCameraRotation(General.random(20, 25)); //CRAZY CAMERA???
        }
        Walking.clickTileMS(new RSTile(3039, 4997, 1), 1);
        General.sleep(500, 1000);
    }

    public void avoidTrap(){
        RSTile myPos = Player.getPosition();
        //Tile after pendulum
        if(myPos.equals(new RSTile(3039, 4997, 1))) {
            while (Camera.getCameraRotation() != General.random(112, 118)) {
                Camera.setCameraRotation(General.random(112, 118));
            }
            RSTile tile1 = new RSTile(3029, 5003, 1); //2nd tile after pendulum. Infront of Floor trap.
            Walking.clickTileMS(tile1, 1);
            waitUntilIdle();
            //tile outside door. Room C
        }else if(myPos.equals(new RSTile(3023, 5001, 1))){
            while(Camera.getCameraRotation() != General.random(18, 25)) {
                Camera.setCameraRotation(General.random(18, 25));
            }
            RSTile tile2 = new RSTile(3011, 5005, 1);
            Walking.clickTileMS(tile2, 1); //trap tile
            General.sleep(400, 800);
            Walking.clickTileMM(new RSTile(2995, 5004, 1), 1); //area before Ledge.
            waitUntilIdle();
        }
    }

    //Simply clicks the door once player is on specific tile.
    public void openDoor(){
        if (Player.getPosition() != new RSTile(3022, 5001, 1)) {
            General.sleep(100);
            RSObject[] nextDoor = Objects.findNearest(10, 7255);
            DynamicClicking.clickRSObject(nextDoor[0], 1);
            waitUntilIdle();
        }
    }


    private void waitUntilIdle(){
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
}
