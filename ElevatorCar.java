package sample;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.naming.PartialResultException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

enum Status {
    OPENING,OPEN,CLOSING,GOING_UP,GOING_DOWN,WAITING;
}

class ElevatorCar
{
    private Rectangle car1;
    private Rectangle leftDoor;
    private Rectangle rightDoor;
    int currentFloor = 1;
    int carWeight = 0;
    int standingRoom = 0;
    int xPos;

    //private ArrayList doorSet = new ArrayList(0);

    private ArrayList floorStops = new ArrayList(0);
    private ArrayList<Rectangle> doorSet2;
     private TranslateTransition leftDoorOpen = new TranslateTransition();
     private TranslateTransition rightDoorOpen = new TranslateTransition();
//   private TranslateTransition leftDoorClose = new TranslateTransition();
//   private TranslateTransition rightDoorClose = new TranslateTransition();
     private TranslateTransition elevatorMove = new TranslateTransition();
     LinkedList<Rider> travelingRiders = new LinkedList<Rider>();
     private ParallelTransition doorOpen;
     Status status=Status.WAITING;
     String currentDirection;
     String debugName;
     //private ParallelTransition doorClose = new ParallelTransition(leftDoorClose,rightDoorClose);
     ElevatorCar thisInstance = this;
     ElevatorCar(Rectangle car, ArrayList<Rectangle> doorSet, int xVal,String name){
        //constructor
        car1 = car;
        doorSet2 = doorSet;
        leftDoor = doorSet.get(0);
        rightDoor = doorSet.get(1);
        leftDoorOpen.setNode(leftDoor);
        rightDoorOpen.setNode(rightDoor);
        xPos = xVal;
        debugName=name;
    }


    void callElevator(int passCurFlr) {
         //should actually be in controller class, decides which elevator picks up which passengers
        if (passCurFlr == currentFloor) {
                openDoors();
            }
        else  {
            moveElevator(passCurFlr);
        }
    }

   private void openDoors(){

        // if (status==Status.WAITING) {
            if(Controller.stillNeedServicing.contains(currentFloor)){
                Controller.stillNeedServicing.remove((Object)currentFloor);
            }
             status = Status.OPENING;
             standingRoom=0;
             leftDoorOpen.setDuration(Duration.seconds(3));
             leftDoorOpen.setToX(-28);
             rightDoorOpen.setDuration(Duration.seconds(3));
             rightDoorOpen.setToX(28);
             doorOpen = new ParallelTransition(leftDoorOpen, rightDoorOpen);

             doorOpen.play();
             final LinkedList<Rider> leavingRiders = new LinkedList<Rider>();
             doorOpen.onFinishedProperty().set(new EventHandler<ActionEvent>() {

                 public void handle(ActionEvent event) {
                     status = Status.OPEN;
                     //left people off first
                     //check weight capacity, if room, let the next person waiting on

                     for (Rider passenger : travelingRiders) {//let people out
                         if (passenger.endFloor == currentFloor) {
                             passenger.walkFromElevator();
                             leavingRiders.add(passenger);
                             setCarWeight(-passenger.weight);
                         } else if(leavingRiders.size()>0) {
                             passenger.enterElevator(thisInstance);
                             standingRoom+=10;
                         } else{
                             standingRoom+=10;
                         }
                     }
                     travelingRiders.removeAll(leavingRiders);

                    Controller.updateTravellers(Controller.travellersCount);//for GUI
                     if (travelingRiders.size()==0 &&Controller.floorQueues.get(currentFloor-1).size()>0){
                         //set elevator direction to first person in line's direction
                         currentDirection=Controller.floorQueues.get(currentFloor-1).getFirst().direction;
                     }
                     if (Controller.floorQueues.get(currentFloor-1).isEmpty()){//if no one waiting on that floor, close
                         closeDoors();
                         currentDirection="NONE";
                     } else {
                         Controller.boarding(thisInstance,currentFloor,Controller.queueCountsCurrent,Controller.travellersCount);
                         new java.util.Timer().schedule(
                                 new java.util.TimerTask() {
                                     @Override
                                     public void run() {
                                         closeDoors();
                                     }
                                 },
                                 3000
                         );
                     }
                 }
             });
        // }
    }



    void closeDoors() {
        status=Status.CLOSING;
        //if no one currently walking to elevator, close and move
        leftDoor.setOpacity(.5);
        rightDoor.setOpacity(.5);
        if (floorStops.contains(currentFloor)){
                       floorStops.remove((Object)currentFloor);
        }
        if (!Controller.floorQueues.get(currentFloor-1).isEmpty()){
            Controller.getElevator(currentFloor, Controller.floorQueues.get(currentFloor-1).getFirst().direction);
        }
        leftDoorOpen.setDuration(Duration.seconds(3));
        leftDoorOpen.setToX(0);
        rightDoorOpen.setDuration(Duration.seconds(3));
        rightDoorOpen.setToX(0);
        doorOpen = new ParallelTransition(leftDoorOpen, rightDoorOpen);

        doorOpen.play();
        doorOpen.onFinishedProperty().set(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (travelingRiders.isEmpty()){
                        if (!Controller.stillNeedServicing.isEmpty()){
                            moveElevator(Controller.stillNeedServicing.get(0));
                            Controller.stillNeedServicing.remove(0);

                        } else {
                            status= Status.WAITING;
                        }
                } else if (!travelingRiders.isEmpty()){
                    //move to next floor destination
                    moveElevator((Integer)floorStops.get(0));

                }

            }
        });
    }
    void setCarWeight(int carW){
        carWeight += carW;
    }

    int getCarWeight(){
        return carWeight;
    }
    void setStandingRoom(){
        standingRoom+=10;
    }
    int getStandingRoom(){
        return standingRoom;
    }

    void addDropOffFloor(int floor){

    floorStops.add(floor);
}

    ArrayList<Integer> getDropOffFloor(){
        return floorStops;
    }

    private void setCurrentFloor(int floor){
        currentFloor = floor;

        leftDoor = doorSet2.get((floor-1)+(floor-1));
        rightDoor = doorSet2.get((floor)+(floor-1));
        leftDoorOpen.setNode(leftDoor);
        rightDoorOpen.setNode(rightDoor);

    }
//    int getCurrentFloor(){
//        return currentFloor;
//    }

    private void moveElevator(int moveToFloor){



        //travelling riders
        //which direction car going (UP,DOWN)
        //array of each floor stop
        //alert riders when it's their stop
        if (currentDirection=="UP"){
            Collections.sort(floorStops);

        } else if (currentDirection=="DOWN") {
            Collections.sort(floorStops);
            Collections.reverse(floorStops);
        }
        System.out.println(debugName+"'s next floors are: "+ floorStops);
        System.out.println(debugName+" GOING "+ currentDirection);
        final int passengerFloor;
        if (travelingRiders.size()==0){
            passengerFloor=moveToFloor;
        } else {
            passengerFloor=getDropOffFloor().get(0);//if there are passengers on board, move to next floor
        }
        if (passengerFloor>currentFloor){
            status=Status.GOING_UP;
            currentDirection="UP";
        } else {
            status=Status.GOING_DOWN;
            currentDirection="DOWN";

        }
        int floorYVal;
        switch(passengerFloor){
            case 1:
                floorYVal = 0;
                break;
            case 2:
                floorYVal = -120;
                break;
            case 3:
                floorYVal = -240;
                break;
            case 4:
                floorYVal = -360;
                break;
            case 5:
                floorYVal = -480;
                break;

            default:
                floorYVal = 0;
        }

        elevatorMove.setNode(car1);
        elevatorMove.setDuration(Duration.seconds(5));
        elevatorMove.setToY(floorYVal);
        ArrayList<TranslateTransition> ttArray = new ArrayList<TranslateTransition>();
        ArrayList<TranslateTransition> flArray = new ArrayList<TranslateTransition>();
        ArrayList<TranslateTransition> tlArray = new ArrayList<TranslateTransition>();


        int travellerCount=0;
        ParallelTransition ridersMove = new ParallelTransition();
        for (Rider riders: travelingRiders){
            int start = riders.startFloor;
            int disp = start-getDropOffFloor().get(0);
            //couldn't figure out how to set rider yVal to elevators, so moved 120px X  (rider's initial floor - next floor)
            ttArray.add(new TranslateTransition());
            ttArray.get(travellerCount).setNode(riders.riderAni);
            ttArray.get(travellerCount).setDuration(Duration.seconds(5));
            ttArray.get(travellerCount).setToY(ttArray.get(travellerCount).getByX()+120*disp);
            flArray.add(new TranslateTransition());
            flArray.get(travellerCount).setNode(riders.floorLabel);
            flArray.get(travellerCount).setDuration(Duration.seconds(5));
            flArray.get(travellerCount).setToY(120*disp);
            tlArray.add(new TranslateTransition());
            tlArray.get(travellerCount).setNode(riders.timerLabel);
            tlArray.get(travellerCount).setDuration(Duration.seconds(5));
            tlArray.get(travellerCount).setToY(120*disp);
            ridersMove.getChildren().add(ttArray.get(travellerCount));
            ridersMove.getChildren().add(flArray.get(travellerCount));
            ridersMove.getChildren().add(tlArray.get(travellerCount));

            travellerCount++;
        }
        ridersMove.getChildren().add(elevatorMove);

        ridersMove.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                setCurrentFloor(passengerFloor);

                Controller.updateTravellers(Controller.travellersCount);
                openDoors();
        }
        });

        ridersMove.play();

    }


}
