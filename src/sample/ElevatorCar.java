package sample;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

import static sample.Controller.downRequests;
import static sample.Controller.floorsList;
import static sample.Controller.upRequests;

enum Status {
    OPENING,OPEN,CLOSING,GOING_UP,GOING_DOWN,WAITING;
}
class ElevatorCar
{
    private Rectangle car1;
    private Rectangle leftDoor;
    private Rectangle rightDoor;
    int currentFloor;
    int beginFloor;
    int carWeight = 0;
    int standingRoom = 0;
    int xPos;

    //private ArrayList doorSet = new ArrayList(0);

    private ArrayList<Integer> floorStops = new ArrayList<Integer>(0);
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
     String destinationDirection;
     String debugName;
     Controller ctrlr = new Controller();
     Timer timer;
     TimerTask task;
    //private ParallelTransition doorClose = new ParallelTransition(leftDoorClose,rightDoorClose);
     ElevatorCar thisInstance = this;
     ElevatorCar(Rectangle car, ArrayList<Rectangle> doorSet, int xVal,String name,int startFloor,int doorsetIndex){
        //constructor
        car1 = car;
        doorSet2 = doorSet;
        leftDoor = doorSet.get(doorsetIndex);
        rightDoor = doorSet.get(doorsetIndex+1);
        leftDoorOpen.setNode(leftDoor);
        rightDoorOpen.setNode(rightDoor);
        xPos = xVal;
        debugName=name;
        currentFloor = startFloor;
        beginFloor = startFloor;
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
//            if(Controller.stillNeedServicing.contains(currentFloor)){
//                Controller.stillNeedServicing.remove((Object)currentFloor);
//            }

             status = Status.OPENING;
             standingRoom=0;
             leftDoorOpen.setDuration(Duration.seconds(3));
             leftDoorOpen.setToX(-28);
             rightDoorOpen.setDuration(Duration.seconds(3));
             rightDoorOpen.setToX(28);
             doorOpen = new ParallelTransition(leftDoorOpen, rightDoorOpen);

             doorOpen.play();
             final LinkedList<Rider> leavingRiders = new LinkedList<Rider>();
       //updateLabels();

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
                     LinkedList<Rider> rl = Controller.floorsList.get(currentFloor-1).ridersInQueue;

                     ctrlr.updateTravellers(Controller.travellersCount);//for GUI
                     if (travelingRiders.size()==0 &&rl.size()>0){
                         //set elevator direction to first person in line's direction
                         currentDirection=rl.getFirst().direction;
                     }
                     if (rl.isEmpty()){//if no one waiting on that floor, close
                         closeDoors();
                         currentDirection="NONE";
                     } else {
                         if (currentDirection.equals("UP")){
                             floorsList.get(currentFloor-1).upButtonActive = false;
                             Floor.adjustButtonLights(currentFloor,"UP" ,0 );
                         } else if (currentDirection.equals("DOWN")){
                             floorsList.get(currentFloor-1).downButtonActive = false;
                             Floor.adjustButtonLights( currentFloor,"DOWN" ,0 );
                         }
                         ctrlr.boarding(thisInstance,currentFloor,Controller.queueCountsCurrent,Controller.travellersCount);
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
        if (floorStops.contains(currentFloor)){//remove current floor from elevator destinations
            floorStops.remove((Integer) currentFloor);
        }
        if (Controller.floorsList.get(currentFloor-1).ridersInQueue.isEmpty()){//if floor is empty when elevator leaves
            if(Controller.upRequests.contains(currentFloor)){
                Controller.upRequests.remove((Integer)currentFloor);
            }
            if(Controller.downRequests.contains(currentFloor)){
                Controller.downRequests.remove((Integer)currentFloor);
            }
        }

        leftDoorOpen.setDuration(Duration.seconds(3));
        leftDoorOpen.setToX(0);
        rightDoorOpen.setDuration(Duration.seconds(3));
        rightDoorOpen.setToX(0);
        doorOpen = new ParallelTransition(leftDoorOpen, rightDoorOpen);
        Floor.onLeaveFloor(currentFloor, destinationDirection,debugName );//change corresponding triangle
        doorOpen.play();
        doorOpen.onFinishedProperty().set(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (travelingRiders.isEmpty()){
                    if (!upRequests.isEmpty() || !downRequests.isEmpty()){
                        for (int u: Controller.upRequests){
                            if (u > currentFloor){
                                floorStops.add(u);
                            }
                        }
                        if (!floorStops.isEmpty()){
                            sortDropOffFloor();
                            moveElevator(floorStops.get(0));
                            upRequests.removeAll(floorStops);
                        } else if (floorStops.isEmpty()){//if no suitable uprequests added
                            for (int d: Controller.downRequests){
                                if (d < currentFloor){
                                    floorStops.add(d);
                                }
                            }
                            if (!floorStops.isEmpty()){
                                sortDropOffFloor();
                                moveElevator(floorStops.get(0));
                                downRequests.removeAll(floorStops);
                            } else {//should never be waiting if there are still floors to service
                                ArrayList<Integer> unservicedFloors = new ArrayList<Integer>();
                                unservicedFloors.addAll(downRequests);
                                unservicedFloors.addAll(upRequests);
                                Set<Integer> uFNoDupes = new LinkedHashSet<Integer>(unservicedFloors);//get rid of duplicates
                                unservicedFloors.clear();
                                unservicedFloors.addAll(uFNoDupes);
                                int longestWait = 0;
                                int floorWithLongestWait = 1;
                                for (int f: unservicedFloors){
                                    int thisWait = Controller.floorsList.get(f-1).ridersInQueue.getFirst().waitDuration;
                                    if ( thisWait > longestWait){
                                        longestWait = thisWait;
                                        floorWithLongestWait = f;
                                    }
                                }
                                moveElevator(floorWithLongestWait);


                                }
                        }
                    } else {
                        status= Status.WAITING;
                        int waitAtFloor = Controller.setWaitingCar();//find floor that most needs elevator
                        if (waitAtFloor > 0){
                            moveElevator(waitAtFloor);
                        }
                        }
                } else {
                    //if not empty, car has a direction, check which floors you can pick up going in that direction
                    if (currentDirection.equals("UP")){
                        if (!upRequests.isEmpty()) {
                            for (int u : Controller.upRequests) {
                                if (u > currentFloor && !floorStops.contains(u)) {
                                    floorStops.add(u);
                                }
                            }
                        }
                        upRequests.removeAll(floorStops);
                    } else if (currentDirection.equals("DOWN")){
                        if (!downRequests.isEmpty()) {
                            for (int d : Controller.downRequests) {
                                if (d < currentFloor && !floorStops.contains(d)) {
                                    floorStops.add(d);
                                }
                            }
                        }
                        downRequests.removeAll(floorStops);
                    }


                    sortDropOffFloor();
                    if (debugName.equals("car1")){
                        Controller.floorStopLabels.get(0).setText(floorStops.toString());
                    } else if (debugName.equals("car2")){
                        Controller.floorStopLabels.get(1).setText(floorStops.toString());
                    } else if (debugName.equals("car3")){
                        Controller.floorStopLabels.get(2).setText(floorStops.toString());

                    }
                    moveElevator(floorStops.get(0));

                }
                //updateLabels();

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
    void sortDropOffFloor(){
        //System.out.println(currentDirection);
        if (currentDirection==null || currentDirection.equals("UP")){
            Collections.sort(floorStops);
        } else if (currentDirection.equals("DOWN")){
            Collections.sort(floorStops);
            Collections.reverse(floorStops);
        }
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

       int pFloor=moveToFloor;
         destinationDirection = currentDirection;

        if (pFloor>currentFloor){
            status=Status.GOING_UP;
            currentDirection="UP";
        } else {
            status=Status.GOING_DOWN;
            currentDirection="DOWN";

        }
        final int fpFloor = pFloor;

        elevatorMove.setNode(car1);
        elevatorMove.setDuration(Duration.seconds(5));
        elevatorMove.setToY((beginFloor-pFloor)*120);
        ArrayList<TranslateTransition> ttArray = new ArrayList<TranslateTransition>();
        ArrayList<TranslateTransition> flArray = new ArrayList<TranslateTransition>();
        ArrayList<TranslateTransition> tlArray = new ArrayList<TranslateTransition>();


        int travellerCount=0;
        ParallelTransition ridersMove = new ParallelTransition();
        for (Rider riders: travelingRiders){
            int start = riders.startFloor;
            int disp = start-pFloor;
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
        //System.out.println("elevator direction: " + currentDirection);


        ridersMove.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                timer.cancel();
                task.cancel();
                setCurrentFloor(fpFloor);

                ctrlr.updateTravellers(Controller.travellersCount);
                if (!Controller.floorsList.get(fpFloor-1).ridersInQueue.isEmpty() || !travelingRiders.isEmpty()){
                    openDoors();
                } else {
                    status = Status.WAITING;
                }
        }
        });
        updateLabels();
        if (debugName.equals("car1")){
            Controller.floorStopLabels.get(0).setText(floorStops.toString());
        } else if (debugName.equals("car2")){
            Controller.floorStopLabels.get(1).setText(floorStops.toString());
        } else if (debugName.equals("car3")){
            Controller.floorStopLabels.get(2).setText(floorStops.toString());
        }
        if (!Controller.floorsList.get(fpFloor-1).ridersInQueue.isEmpty() || !travelingRiders.isEmpty()){
            Floor.onEnterFloor(fpFloor, destinationDirection,debugName );//change corresponding triangle
        }
        ridersMove.play();

    }

 void updateLabels(){

     timer= new Timer();
     task = new TimerTask() {
         @Override
         public void run() {
             Double currentY = 480+34-car1.getLayoutY()-car1.getTranslateY();
             //System.out.println(currentY);
             if (currentY == 0 && currentDirection.equals("DOWN")){
                 setCurrentFloor(1);
             }
             if (currentY < 119){
                 if (currentDirection.equals("DOWN")){
                     setCurrentFloor(2);

                 } else if (currentDirection.equals("UP")){
                     setCurrentFloor(1);
                 }
             } else if (currentY < 239){
                 if (currentDirection.equals("DOWN")){
                     setCurrentFloor(3);

                 } else if (currentDirection.equals("UP")){
                     setCurrentFloor(2);
                 }
             } else if (currentY < 360){
                 if (currentDirection.equals("DOWN")){
                     setCurrentFloor(4);

                 } else if (currentDirection.equals("UP")){
                     setCurrentFloor(3);
                 }
             } else if (currentY < 480){
                 if (currentDirection.equals("DOWN")){
                     setCurrentFloor(5);

                 } else if (currentDirection.equals("UP")){
                     setCurrentFloor(4);
                 }
             } else {
                 setCurrentFloor(5);
             }

             if (debugName.equals("car1")){
                 Controller.travellersCount.get(1).setText("Current Floor: "+currentFloor );
                 Controller.floorStopLabels.get(0).setText(floorStops.toString());

             } else if (debugName.equals("car2")){
                 Controller.travellersCount.get(4).setText("Current Floor: "+currentFloor );
                 Controller.floorStopLabels.get(1).setText(floorStops.toString());
             } else if (debugName.equals("car3")){
                 Controller.travellersCount.get(7).setText("Current Floor: "+currentFloor );
                 Controller.floorStopLabels.get(2).setText(floorStops.toString());

             }
         }

     };
     timer.scheduleAtFixedRate(task,100,100);





     Controller.floorStopLabels.get(3).setText("up requests: "+ Controller.upRequests.toString());
     Controller.floorStopLabels.get(4).setText("down requests: "+ Controller.downRequests.toString());
 }



}
