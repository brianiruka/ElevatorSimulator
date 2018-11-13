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
                     if (currentDirection.equals("UP")){
                         floorsList.get(currentFloor-1).upButtonActive = false;
                         Floor.setLights(currentFloor,"UP" ,0 );
                     } else if (currentDirection.equals("DOWN")){
                         floorsList.get(currentFloor-1).downButtonActive = false;
                         Floor.setLights(currentFloor,"DOWN" ,0 );

                     }
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
                     //updateLabels();

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
//        else {
//            Boolean passengerGoingUp = Controller.floorQueues.get(currentFloor-1).getFirst().direction.equals("UP");
//            if (passengerGoingUp && !Controller.upRequests.contains(currentFloor)) {
//                Controller.upRequests.add(currentFloor);
//            } else if (!Controller.downRequests.contains(currentFloor)) {
//                Controller.downRequests.add(currentFloor);
//            }
//        }
        leftDoorOpen.setDuration(Duration.seconds(3));
        leftDoorOpen.setToX(0);
        rightDoorOpen.setDuration(Duration.seconds(3));
        rightDoorOpen.setToX(0);
        doorOpen = new ParallelTransition(leftDoorOpen, rightDoorOpen);

        doorOpen.play();
        doorOpen.onFinishedProperty().set(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (travelingRiders.isEmpty()){
                    if (!upRequests.isEmpty() && !downRequests.isEmpty()){
                        for (int u: Controller.upRequests){
                            if (u > currentFloor){
                                floorStops.add(u);
                            }
                        }
                        sortDropOffFloor();
                        moveElevator(floorStops.get(0));
                        upRequests.removeAll(floorStops);

                        if (floorStops.isEmpty()){//if no suitable uprequests added
                            for (int d: Controller.downRequests){
                                if (d < currentFloor){
                                    floorStops.add(d);
                                }
                            }
                            sortDropOffFloor();
                            moveElevator(floorStops.get(0));
                            downRequests.removeAll(floorStops);
                        }
                    } else {
                        status= Status.WAITING;
                        }
                } else {
                    //move to next floor destination
                    sortDropOffFloor();
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
        //travelling riders
        //which direction car going (UP,DOWN)
        //array of each floor stop
        //alert riders when it's their stop

        if (currentDirection.equals("UP")){//check next floor to add]
            //System.out.println("current floor is "+currentFloor);
            //System.out.println("up requests size is "+Controller.upRequests.size());
            if (!Controller.upRequests.isEmpty()){
                int removeFloor = 0;
                Collections.sort(Controller.upRequests);
                for (int c: Controller.upRequests){
                    if (c>currentFloor && getCarWeight()<500){
                        if (!Controller.floorsList.get(c-1).ridersInQueue.isEmpty() && !floorStops.contains(c)){//floor has riders & you're not already stopping there
                            removeFloor=c;
                            //System.out.println("floor = "+c);
                            floorStops.add(c);
                            break;
                        }
                    }
                }
                Controller.upRequests.remove((Integer)(removeFloor));
                //System.out.println("removed floor "+removeFloor+" before elevator moved");
                sortDropOffFloor();
                //updateLabels();
            }
            if (floorStops!=null){//if any floors not in direction, remove
                ArrayList<Integer> removeFloors = new ArrayList<Integer>();
                for (int fs: floorStops){
                    if (fs<currentFloor){
                            removeFloors.add(fs);
                        }
                    }
                    floorStops.removeAll(removeFloors);
                }

        } else if (currentDirection.equals("DOWN")) {
            if (!Controller.downRequests.isEmpty()){
                int removeFloor = 0;
                Collections.sort(Controller.downRequests);
                Collections.reverse(Controller.downRequests);
                for (int c: Controller.downRequests){
                    if (c<currentFloor  && getCarWeight()<500){
                        if (!Controller.floorsList.get(c-1).ridersInQueue.isEmpty() && !floorStops.contains(c)){//floor has riders & you're not already stopping there
                            removeFloor=c;
                            floorStops.add(c);
                            break;
                        }
                    }
                }
                Controller.downRequests.remove((Integer)(removeFloor));
                sortDropOffFloor();
            }
            if (floorStops!=null){//if any floors not in direction, remove
                ArrayList<Integer> removeFloors = new ArrayList<Integer>();
                for (int fs: floorStops){
                    if (fs>currentFloor ){
                        removeFloors.add(fs);
                    }

                }
                floorStops.removeAll(removeFloors);
            }
        }
        //System.out.println(debugName+"'s next floors are: "+ floorStops);
        //System.out.println(debugName+"'s GOING "+ currentDirection);
        int pFloor=moveToFloor;
        if (!getDropOffFloor().isEmpty()) {
                for (int i : getDropOffFloor()) {
                    if (!Controller.floorsList.get(i - 1).ridersInQueue.isEmpty()) {
                        pFloor = i;//only go to floor if there are passengers in line
                        break;
                    } else{
                        pFloor = getDropOffFloor().get(0);
                    }
                }
            }
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
                openDoors();
        }
        });
        updateLabels();

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
