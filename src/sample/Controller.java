package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import java.util.*;

public class Controller {
    @FXML private Pane myPane;
    @FXML private Rectangle car1;
    @FXML private Rectangle floor1_doorLeft;
    @FXML private Rectangle floor1_doorRight;
    @FXML private Rectangle floor2_doorLeft;
    @FXML private Rectangle floor2_doorRight;
    @FXML private ComboBox comboFrom;
    @FXML private ComboBox comboTo;
    static ArrayList<LinkedList<Rider>> floorQueues;
    private ArrayList<Rectangle> doorSet = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    //@FXML private Text floor_read_out;
    static ArrayList<Integer> queueOffsets;
    //int standingRoom = 0;
    //public static LinkedList<Rider> waitingRiders = new LinkedList<Rider>();//holds all riders waiting for an elevator
    //private static ArrayList elevators;
     static ElevatorCar ec1;
    static ElevatorCar ec2;
    static ElevatorCar ec3;
     static int beginningFloor = 1;
     int endingFloor = 2;

    @FXML
    public void initialize(){
        //method runs after javafx scene is initialized
        floorQueues = new ArrayList<LinkedList<Rider>>();
        queueOffsets = new ArrayList<Integer>();
        for (int i = 0;i<2;i++){
            floorQueues.add(new LinkedList<Rider>());
            queueOffsets.add(0);
        }

        doorSet.addAll(Arrays.asList(floor1_doorLeft,floor1_doorRight,floor2_doorLeft,floor2_doorRight));
        ec1= new ElevatorCar(car1 , doorSet);
        ec2= new ElevatorCar(car1 , doorSet);
        ec3= new ElevatorCar(car1 , doorSet);

        comboFrom.getItems().addAll(
                1,
                2
        );
        comboTo.getItems().addAll(
                1,
                2
        );
    }
    @FXML protected void handleCombo1() {
        beginningFloor=(Integer)comboFrom.getValue();
        if (beginningFloor==1){
            endingFloor =2;
        } else {
            endingFloor =1;
        }
    }

    @FXML protected void handleAddRider() {
        //handles addRider button click, creates rider instance
        floorQueues.get(beginningFloor-1).add(new Rider(myPane,100,beginningFloor,endingFloor,0));
        if (ec1.currentFloor == floorQueues.get(beginningFloor-1).getLast().startFloor && (ec1.status.toString()=="OPEN")){
            floorQueues.get(beginningFloor-1).getLast().enterElevator(ec1);
            boarding(beginningFloor);
        } else {
            //System.out.println(ec1.status);
            floorQueues.get(beginningFloor-1).getLast().walkToQueue(floorQueues.get(beginningFloor-1));
            queueOffsets.set(beginningFloor-1,queueOffsets.get(beginningFloor-1)+50);//adjusts x distance to behind second to last person in line
        }

    }
    static void getElevator(int startFloor,String riderDir){
        //find current closest elevator
//        ElevatorCar closestCar = null;
//        ArrayList<ElevatorCar> carsByDistance = new ArrayList<ElevatorCar>();
//
//        int smallestDiff = 10;
//        int carDist1=Math.abs(startFloor-ec1.currentFloor);
//        int carDist2=Math.abs(startFloor-ec2.currentFloor);
//        int carDist3=Math.abs(startFloor-ec3.currentFloor);



        if(ec1.status==Status.WAITING){
            ec1.callElevator(startFloor);//if ec waiting, immediately go to floor

        }
            ec1.addPickupFloor(startFloor);//if busy, add to floors to pick up from


        //check if it's waiting

        //if not waiting, check if it's headed in direction passenger is headed, not at capacity

        //repeat for next closest elevator, repeat

        //once elevator is found for a floor, remove floor from buffer, unless people still waiting in queue
        //if no floor is found, keep in buffer until an elevator becomes empty, changes direction
    }

     static void boarding(int currentFloor){
        //handles boarding of a newly arrived elevator
        //ec.setCarWeight(wR.getFirst().weight);
        //int waiterAmt = waitingRiders.size();
            //System.out.println(waitingRiders);
         queueOffsets.set(currentFloor-1,0);
            for (Rider passenger: floorQueues.get(currentFloor-1)){
                if (ec1.getCarWeight()<400 && (ec1.currentFloor==passenger.startFloor)) {
                    queueOffsets.set(currentFloor-1,0);
                    passenger.enterElevator(ec1);
                    ec1.setCarWeight(passenger.weight);
                    ec1.setStandingRoom();
                    ec1.travelingRiders.add(passenger);
//                    if (waiterAmt>1){
//                        waiterAmt--;
//                        //System.out.println("should be running");
//                    }
                } else {

                    passenger.walkToQueue(floorQueues.get(passenger.startFloor-1));
                    queueOffsets.set(currentFloor-1,queueOffsets.get(currentFloor-1)+50);
                }

                if(!ec1.getDropOffFloor().contains(floorQueues.get(currentFloor-1).getFirst().endFloor)){
                    //System.out.println(waitingRiders.getFirst().endFloor);
                    ec1.addDropOffFloor(floorQueues.get(currentFloor-1).getLast().endFloor);
                }

            }
         //closeDoorButton(ec1);
         floorQueues.get(currentFloor-1).removeAll(ec1.travelingRiders);//can't remove boarded passengers from queue during for each loop, so after concludes remove travelingpassengers
         if (floorQueues.get(currentFloor-1).isEmpty()){
             ec1.getPickUpFloor().remove((Object)currentFloor);
         }
    }


    static void closeDoorButton(ElevatorCar ec){
        ec.closeDoors();
    }

     static LinkedList<Rider> getWaitingRiders(int startingFloor){
        return floorQueues.get(startingFloor-1);
    }
}
