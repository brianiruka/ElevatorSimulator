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
    int nextFloor = 1;
    int carWeight = 0;
    int standingRoom = 0;

    //private ArrayList doorSet = new ArrayList(0);

    private ArrayList floorStops = new ArrayList(0);
    private ArrayList<Rectangle> doorSet2;
    ArrayList<Integer> controllerRequests=new ArrayList(0);;

     private TranslateTransition leftDoorOpen = new TranslateTransition();
     private TranslateTransition rightDoorOpen = new TranslateTransition();
//   private TranslateTransition leftDoorClose = new TranslateTransition();
//   private TranslateTransition rightDoorClose = new TranslateTransition();
     private TranslateTransition elevatorMove = new TranslateTransition();
     LinkedList<Rider> travelingRiders = new LinkedList<Rider>();
     private ParallelTransition doorOpen;
     Status status=Status.WAITING;
     String currentDirection;
     //private ParallelTransition doorClose = new ParallelTransition(leftDoorClose,rightDoorClose);

     ElevatorCar(Rectangle car, ArrayList<Rectangle> doorSet){
        //constructor
        car1 = car;
        doorSet2 = doorSet;
        leftDoor = doorSet.get(0);
        rightDoor = doorSet.get(1);
        leftDoorOpen.setNode(leftDoor);
        rightDoorOpen.setNode(rightDoor);
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
       System.out.println("how many times is this run");

       standingRoom=0;
        // if (status==Status.WAITING) {
             status = Status.OPENING;
// check weight capacity, don't open if overcapacity
//     System.out.println("left door open: " + leftDoorOpen);
//      System.out.println("door open " + doorOpen.getChildren());
//    System.out.println("left door node " + leftDoorOpen.getNode());
             leftDoorOpen.setDuration(Duration.seconds(3));
             leftDoorOpen.setToX(-60);
             rightDoorOpen.setDuration(Duration.seconds(3));
             rightDoorOpen.setToX(68);
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
                             passenger.leaveElevator();
                             leavingRiders.add(passenger);
                             setCarWeight(-passenger.weight);
                         }

                     }

                     travelingRiders.removeAll(leavingRiders);

                     if (Controller.floorQueues.get(currentFloor-1).isEmpty()){//if no one waiting on that floor, close
                         //System.out.println("there's no one waiting");
                         closeDoors();
                     } else {
                         //System.out.println("there's a line");

                         Controller.boarding(currentFloor);
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
        if (floorStops.contains(currentFloor)&&Controller.floorQueues.get(currentFloor-1).isEmpty()){
                       floorStops.remove((Object)currentFloor);
        }
        leftDoorOpen.setDuration(Duration.seconds(3));
        leftDoorOpen.setToX(0);
        rightDoorOpen.setDuration(Duration.seconds(3));
        rightDoorOpen.setToX(0);
        doorOpen = new ParallelTransition(leftDoorOpen, rightDoorOpen);

        doorOpen.play();
        doorOpen.onFinishedProperty().set(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (travelingRiders.isEmpty() && controllerRequests.isEmpty()){
                    status= Status.WAITING;
                } else if (!travelingRiders.isEmpty()){
                    //move to next floor destination
                    //System.out.println("Elevator called");

                    moveElevator((Integer)floorStops.get(0));
                }  else if (!controllerRequests.isEmpty()){
                //move to next floor destination
                //System.out.println("Elevator called");

                moveElevator(controllerRequests.get(0));
            }
//                System.out.println(travelingRiders);
//                System.out.println(getFloorStops());
            }
        });
    }
    void setCarWeight(int carW){

        carWeight += carW;
        //  System.out.println("method set car weight to "+carWeight);
    }

    int getCarWeight(){
        return carWeight;
    }
    void setStandingRoom(){
        standingRoom+=25;
    }
    int getStandingRoom(){
        return standingRoom;
    }

    void addDropOffFloor(int floor){
    floorStops.add(floor);
}
    void addPickupFloor(int floor){
        controllerRequests.add(floor);
    }
    ArrayList<Integer> getDropOffFloor(){
        return floorStops;
    }
    ArrayList<Integer> getPickUpFloor(){
        return controllerRequests;
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
        Collections.sort(floorStops);
        //System.out.println(floorStops);
        final int passengerFloor;
        if (travelingRiders.size()==0){
            passengerFloor=moveToFloor;
        } else {
            passengerFloor=getDropOffFloor().get(0);//if there are passengers on board, move to next floor
        }
        //nextFloor = 1;
        if (passengerFloor>currentFloor){
            status=Status.GOING_UP;
            currentDirection="UP";
        } else {
            status=Status.GOING_DOWN;
            currentDirection="DOWN";

        }
        int floorYVal;
        int rFloorYVal;
        switch(passengerFloor){
            case 1:
                floorYVal = 0;
                rFloorYVal = 297;
                break;
            case 2:
                floorYVal = -297;
                rFloorYVal = -297;
                break;
            default:
                floorYVal = 0;
                rFloorYVal=0;
        }
       // Collections.sort(floorStops,Collections.reverseOrder());
//        if (floorYVal == -297){
//            openAtFloor = 2;
//        } else {
//            openAtFloor = 1;
//        }
        elevatorMove.setNode(car1);
        elevatorMove.setDuration(Duration.seconds(5));
        elevatorMove.setToY(floorYVal);
        ArrayList<TranslateTransition> ttArray = new ArrayList<TranslateTransition>();
        ArrayList<TranslateTransition> flArray = new ArrayList<TranslateTransition>();

        int travellerCount=0;
        ParallelTransition ridersMove = new ParallelTransition();
        for (Rider riders: travelingRiders){
            ttArray.add(new TranslateTransition());
            ttArray.get(travellerCount).setNode(riders.riderAni);
            ttArray.get(travellerCount).setDuration(Duration.seconds(5));
            ttArray.get(travellerCount).setToY(rFloorYVal);
            flArray.add(new TranslateTransition());
            flArray.get(travellerCount).setNode(riders.floorLabel);
            flArray.get(travellerCount).setDuration(Duration.seconds(5));
            flArray.get(travellerCount).setToY(rFloorYVal);
            ridersMove.getChildren().add(ttArray.get(travellerCount));
            ridersMove.getChildren().add(flArray.get(travellerCount));

            travellerCount++;
        }
        ridersMove.getChildren().add(elevatorMove);

        ridersMove.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //currentFloor = openAtFloor;
                //floor_read_out.setText("current floor is " + currentFloor);
                setCurrentFloor(passengerFloor);

               // System.out.println(floorStops.contains(passengerFloor));
                //status=Status.WAITING;
            System.out.println("How many times");
                openDoors();
        }
        });

        ridersMove.play();

    }


}
