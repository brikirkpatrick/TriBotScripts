package scripts.RoguesDen.Nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
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
    //All of these Areas will make up Part 1 of the map.
    private final RSArea ARoom = new RSArea(new RSTile(3056, 4992, 1), new RSTile(3050, 5005, 1));
    private final RSArea A2Room = new RSArea(new RSTile(3048, 4997, 1), new RSTile(3043, 4999, 1));
    private final RSArea BRoom = new RSArea(new RSTile(3039, 4995, 1), new RSTile(3024, 5003, 1));
    private final RSArea CRoom = new RSArea(new RSTile(3024, 5001, 1), new RSTile(3011, 5005, 1));
        //polygon of D-Room.
        private final RSTile[] otherD = new RSTile[]{new RSTile(2994, 5005, 1), new RSTile(2999, 4998, 1), new RSTile(3010, 5005, 1)};
    private final RSArea DRoom = new RSArea(otherD);
        //Polygon of E-Room
        private final RSTile[] otherE = new RSTile[]{new RSTile(2990, 5008, 1), new RSTile(2988, 4999, 1), new RSTile(2976, 5000, 1), new RSTile(2970, 5016, 1), new RSTile(2970, 5018, 1), new RSTile(2978, 5019, 1)};
    private final RSArea ERoom = new RSArea(otherE);
        private final RSTile[] otherF = new RSTile[]{new RSTile(2968, 5016, 1), new RSTile(2968, 5019, 1), new RSTile(2965, 5025, 1), new RSTile(2953, 5025, 1),  new RSTile(2967, 5016, 1)};
    private final RSArea FRoom = new RSArea(otherF);


    private State SCRIPT_STATE = getState();


    private enum State{CONTORTION_BARS_PENDULUM, AVOID_TRAP, OPEN_DOOR, CLIMB_LEDGE, CROSS_BLADE, CROSS_PENDULUM}
    private State getState(){
        RSTile myPos = Player.getPosition();
        if(ARoom.contains(myPos)){
            return State.CONTORTION_BARS_PENDULUM;
        }else if (BRoom.contains(myPos)){
            if(myPos.equals(new RSTile(3029, 5003, 1))){
                return State.OPEN_DOOR;
            }else
                return State.AVOID_TRAP;
        }else if (CRoom.contains(myPos)){
                return State.AVOID_TRAP;
        }else if(DRoom.contains(myPos)){
            return State.CLIMB_LEDGE;
        }else if(ERoom.contains(myPos)){
            return State.CROSS_BLADE;
        }else if(FRoom.contains(myPos)){
            return State.CROSS_PENDULUM;
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
                case OPEN_DOOR:
                    openDoor();
                    break;
                case AVOID_TRAP:
                    avoidTrap();
                    break;
                case CONTORTION_BARS_PENDULUM:
                    barAndPendulum();
                    break;
                case CLIMB_LEDGE:
                    climbLedge();
                    break;
                case CROSS_BLADE:
                    crossOtherTrap();
                    break;
                case CROSS_PENDULUM:
                    crossOtherTrap();
                    break;
                default:
                    break;
            }
        }
    }

    //Validate: if true, will run the execute method.
    //Will validate based on character being located in certain areas.
    @Override
    public boolean validate() {
        RSTile myPos = Player.getPosition();
        if(ARoom.contains(myPos) || BRoom.contains(myPos) || CRoom.contains(myPos) || DRoom.contains(myPos) || ERoom.contains(myPos) || FRoom.contains(myPos)){
            getState();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return SCRIPT_STATE != null ? SCRIPT_STATE.toString() : "";
    }

    private void barAndPendulum(){
        if (Player.getPosition() != new RSTile(3048, 4997, 1) && !(Player.getPosition().equals(A2Room))) {
            General.sleep(100);
            RSObject[] contortionBar = Objects.findNearest(10, 7251);
            DynamicClicking.clickRSObject(contortionBar[0], 1);
            waitUntilIdle();
        }

        //Turns camera and clicks tile after pendulum.
        while(Camera.getCameraRotation() != General.random(20, 25)) { //while loops might weight down cpu...
            Camera.setCameraAngle(100);
            Camera.setCameraRotation(General.random(20, 25)); //CRAZY CAMERA???
            General.sleep(100);
        }
        Walking.clickTileMS(new RSTile(3039, 4997, 1), 1);
        General.sleep(500, 1000);
    }

    private void avoidTrap(){
        RSTile myPos = Player.getPosition();
        RSTile tile1 = new RSTile(3029, 5003, 1);
        RSTile tile2 = new RSTile(3011, 5005, 1); //after door.
        RSTile tile3 = new RSTile(2995, 5004, 1);
        RSTile tile4 = new RSTile(3018, 5002, 1);
        RSTile tile5 = new RSTile(3000, 5001, 1); //random tile between ledge

        //Tile after pendulum
        if(myPos.equals(new RSTile(3039, 4997, 1))) {
            while (Camera.getCameraRotation() != General.random(112, 118)) {
                Camera.setCameraRotation(General.random(112, 118));
                General.sleep(100);
            }
             //2nd tile after pendulum. Infront of Floor trap.
            if(tile1 != null)
                Walking.clickTileMS(tile1, 1);
            waitUntilIdle();
            //tile outside door. Room C
        }else if(CRoom.contains(myPos)){ //(myPos.equals(new RSTile(3023, 5001, 1)
            if(tile4 != null && tile4.isOnScreen())
                Walking.clickTileMS(tile4, 1);
            waitUntilIdle();

            if(tile2 != null && tile2.isOnScreen())
                Walking.clickTileMS(tile2, 1); //trap tile
            waitUntilIdle();

            if (tile5 != null && tile5.isOnScreen())
                Walking.clickTileMM(tile5, 1); //area before Ledge.
            waitUntilIdle();
            Walking.clickTileMM(tile3, 1);
            waitUntilIdle();
            General.sleep(400, 800);
        }
    }

    //Simply clicks the door once player is on specific tile.
    private void openDoor(){
        if (Player.getPosition() != new RSTile(3022, 5001, 1)) {
            General.sleep(100);
            RSObject[] nextDoor = Objects.findNearest(10, 7255);
            DynamicClicking.clickRSObject(nextDoor[0], 1);
            waitUntilIdle();
        }
    }

    private void climbLedge(){
        RSObject[] ledge = Objects.findNearest(10, 7240);
        if(ledge.length > 0)
            DynamicClicking.clickRSObject(ledge[0], 1); //Climb across the ledge.
        waitUntilIdle();
    }

    private void crossOtherTrap(){
        RSTile myPos = Player.getPosition();

        //Cross Pendulum
        if(FRoom.contains(myPos)){
            RSArea pendulum = new RSArea(new RSTile(2964, 5028, 1), new RSTile(2953, 5028, 1));
            Walking.clickTileMM(FRoom.getRandomTile(), 1);
            waitUntilIdle();
            Walking.clickTileMS(pendulum.getRandomTile(), 1);
            waitUntilIdle();
            General.sleep(400, 800);
            Walking.clickTileMS(new RSTile(2958, 5029, 1), 1);  //TEST. Tile before Part 2 Ledge
        }
        //Cross Blade
        else if (ERoom.contains(myPos)) {
            RSArea blades = new RSArea(new RSTile(2969, 5016, 1), new RSTile(2969, 5019, 1));
            Walking.clickTileMM(new RSTile(2975, 5016, 1), 1);
            waitUntilIdle();
            Walking.clickTileMS(blades.getRandomTile(), 1);
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
