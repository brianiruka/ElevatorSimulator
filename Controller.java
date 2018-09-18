package sample;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.text.DecimalFormat;
import java.util.*;
import javafx.scene.control.ToggleButton;


public class Controller {
    @FXML private Pane myPane;
    @FXML private Rectangle car1,car2,car3;
    @FXML private Rectangle floor1_doorLeft1,floor1_doorRight1,floor1_doorLeft2,floor1_doorRight2,floor1_doorLeft3,floor1_doorRight3;
    @FXML private Rectangle floor2_doorLeft1,floor2_doorRight1,floor2_doorLeft2,floor2_doorRight2,floor2_doorLeft3,floor2_doorRight3;
    @FXML private Rectangle floor3_doorLeft1,floor3_doorRight1,floor3_doorLeft2,floor3_doorRight2,floor3_doorLeft3,floor3_doorRight3;
    @FXML private Rectangle floor4_doorLeft1,floor4_doorRight1,floor4_doorLeft2,floor4_doorRight2,floor4_doorLeft3,floor4_doorRight3;
    @FXML private Rectangle floor5_doorLeft1,floor5_doorRight1,floor5_doorLeft2,floor5_doorRight2,floor5_doorLeft3,floor5_doorRight3;
    @FXML private Text floor1Tot,floor2Tot,floor3Tot,floor4Tot,floor5Tot;
    @FXML private Text floor1Cur,floor2Cur,floor3Cur,floor4Cur,floor5Cur;
    @FXML private Text floor1Pct,floor2Pct,floor3Pct,floor4Pct,floor5Pct;
    @FXML private Text el1Riders,el1Flr,el1Wgt,el2Riders,el2Flr,el2Wgt,el3Riders,el3Flr,el3Wgt;
    @FXML  private Text totalRiders,averageWaitLabel,longestWaitLabel;
    @FXML private ToggleButton startSim;
    @FXML private Polyline triangle1LU;

    static ArrayList<LinkedList<Rider>> floorQueues;
    private ArrayList<Rectangle> shaft1 = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    private ArrayList<Rectangle> shaft2 = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    private ArrayList<Rectangle> shaft3 = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    static ArrayList<Integer> queueOffsets;
    static ArrayList<Text> queueCountsCurrent=new ArrayList<Text>(5);
    static ArrayList<Integer> queueCountsTotal=new ArrayList<Integer>(5);
    static ArrayList<String> floorDirectionCalled=new ArrayList<String>(5);
    static ArrayList<Text> travellersCount=new ArrayList<Text>(9);
    static Text awl;
    static Text lw;
    static ArrayList<Integer> stillNeedServicing=new ArrayList<Integer>(5);
    static Double totalRidersNum = 0.0;

    static ElevatorCar ec1,ec2,ec3;
    static int averageWait=0;
    static int completeTrips=0;
    static int totalWait=0;
    static int longestWait=0;




    @FXML
    public void initialize(){
        //method runs after javafx scene is initialized
        floorQueues = new ArrayList<LinkedList<Rider>>();
        queueOffsets = new ArrayList<Integer>();
        for (int i = 0;i<5;i++){
            floorQueues.add(new LinkedList<Rider>());
            queueOffsets.add(0);
        }
        shaft1.addAll(Arrays.asList(floor1_doorLeft1,floor1_doorRight1,floor2_doorLeft1,floor2_doorRight1,
                floor3_doorLeft1,floor3_doorRight1,floor4_doorLeft1,floor4_doorRight1,floor5_doorLeft1,floor5_doorRight1));
        shaft2.addAll(Arrays.asList(floor1_doorLeft2,floor1_doorRight2,floor2_doorLeft2,floor2_doorRight2,
                floor3_doorLeft2,floor3_doorRight2,floor4_doorLeft2,floor4_doorRight2,floor5_doorLeft2,floor5_doorRight2));
        shaft3.addAll(Arrays.asList(floor1_doorLeft3,floor1_doorRight3,floor2_doorLeft3,floor2_doorRight3,
                floor3_doorLeft3,floor3_doorRight3,floor4_doorLeft3,floor4_doorRight3,floor5_doorLeft3,floor5_doorRight3));
        queueCountsTotal.add(0);
        queueCountsTotal.add(0);
        queueCountsTotal.add(0);
        queueCountsTotal.add(0);
        queueCountsTotal.add(0);
        floorDirectionCalled.add("NONE");
        floorDirectionCalled.add("NONE");
        floorDirectionCalled.add("NONE");
        floorDirectionCalled.add("NONE");
        floorDirectionCalled.add("NONE");
        queueCountsCurrent.add(floor1Cur);
        queueCountsCurrent.add(floor2Cur);
        queueCountsCurrent.add(floor3Cur);
        queueCountsCurrent.add(floor4Cur);
        queueCountsCurrent.add(floor5Cur);
        travellersCount.add(el1Riders);
        travellersCount.add(el1Flr);
        travellersCount.add(el1Wgt);
        travellersCount.add(el2Riders);
        travellersCount.add(el2Flr);
        travellersCount.add(el2Wgt);
        travellersCount.add(el3Riders);
        travellersCount.add(el3Flr);
        travellersCount.add(el3Wgt);
        ec1= new ElevatorCar(car1 , shaft1,315,"car1");
        ec2= new ElevatorCar(car2 , shaft2,425,"car2");
        ec3= new ElevatorCar(car3 , shaft3,540,"car3");
        updateQueues(queueCountsCurrent);
        awl = averageWaitLabel;
        lw=longestWaitLabel;
    }

    @FXML protected void simTimer(MouseEvent actionEvent) {
        int fromFloor= (int)(Math.random()*5+1);
        addRider(fromFloor,27);
    }

    @FXML protected void onFloorButtonClick(MouseEvent actionEvent) {
        String id =actionEvent.getPickResult().getIntersectedNode().getId();
        int fromFloor = Character.getNumericValue(id.charAt(1));
        int toFloor = Character.getNumericValue(id.charAt(3));
        addRider(fromFloor,toFloor);
        }
 //   }

    void addRider(int ff,int tf){
        int fromFloor = ff;
        int toFloor = tf;
        while (toFloor ==27||toFloor==fromFloor){
            toFloor = (int)(Math.random()*5+1);
        }
        floorQueues.get(fromFloor-1).add(new Rider(myPane,100,fromFloor,toFloor,0));
        queueCountsTotal.set(fromFloor-1,queueCountsTotal.get(fromFloor-1)+1);
        totalRidersNum++;
        totalRiders.setText("Total Riders: "+totalRidersNum.intValue());
        DecimalFormat df2 = new DecimalFormat(".##");
        Double divisionResult = 0.0;
        floor1Tot.setText(queueCountsTotal.get(0).toString());
        floor2Tot.setText(queueCountsTotal.get(1).toString());
        floor3Tot.setText(queueCountsTotal.get(2).toString());
        floor4Tot.setText(queueCountsTotal.get(3).toString());
        floor5Tot.setText(queueCountsTotal.get(4).toString());
        updateQueues(queueCountsCurrent);

        divisionResult = (queueCountsTotal.get(0)/totalRidersNum)*100;
        floor1Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (queueCountsTotal.get(1)/totalRidersNum)*100;
        floor2Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (queueCountsTotal.get(2)/totalRidersNum)*100;
        floor3Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (queueCountsTotal.get(3)/totalRidersNum)*100;
        floor4Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (queueCountsTotal.get(4)/totalRidersNum)*100;
        floor5Pct.setText(df2.format(divisionResult)+"%");

        floorQueues.get(fromFloor-1).getLast().walkToQueue(floorQueues.get(fromFloor-1));
        queueOffsets.set(fromFloor-1,queueOffsets.get(fromFloor-1)+10);//adjusts x distance to behind second to last person in line
    }

    static void getElevator(int startFloor,String riderDir){
        //find current closest elevator
        ElevatorCar closestCar = null;
        ElevatorCar nextClosestCar = null;
        ElevatorCar furthestCar = null;

        int carDist1=Math.abs(startFloor-ec1.currentFloor);
        int carDist2=Math.abs(startFloor-ec2.currentFloor);
        int carDist3=Math.abs(startFloor-ec3.currentFloor);

        Map<ElevatorCar, Integer> map = new HashMap<ElevatorCar, Integer>();
        map.put(ec1, carDist1);
        map.put(ec2, carDist2);
        map.put(ec3, carDist3);

        Set<Map.Entry<ElevatorCar, Integer>> set = map.entrySet();
        List<Map.Entry<ElevatorCar, Integer>> list = new ArrayList<Map.Entry<ElevatorCar, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<ElevatorCar, Integer>>()
        {
            public int compare( Map.Entry<ElevatorCar, Integer> o1, Map.Entry<ElevatorCar, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );

        furthestCar=list.get(0).getKey();
        nextClosestCar=list.get(1).getKey();
        closestCar=list.get(2).getKey();



        if (floorDirectionCalled.get(startFloor-1).equals("DOWN")) {
            floorDirectionCalled.set(startFloor - 1, "BOTH");
        } else if (floorDirectionCalled.get(startFloor-1).equals("UP")){
            floorDirectionCalled.set(startFloor - 1, "BOTH");
        } else if (floorDirectionCalled.get(startFloor-1).equals("NONE")){
            floorDirectionCalled.set(startFloor - 1, riderDir);
        }
       // System.out.println(closestCar.currentDirection);
        if(closestCar.status==Status.WAITING){
            closestCar.callElevator(startFloor);//if ec waiting, immediately go to floor
            closestCar.currentDirection=riderDir;
        }
        else if(closestCar.currentDirection==riderDir && !closestCar.getDropOffFloor().contains(startFloor)
                &&closestCar.status!=Status.WAITING && isHeadedThatWay(closestCar.currentDirection, startFloor,
                closestCar.currentFloor)&&closestCar.getCarWeight()<500){

            closestCar.addDropOffFloor(startFloor);//if ec waiting, immediately go to floor
        }
        else if(nextClosestCar.status==Status.WAITING){
            nextClosestCar.callElevator(startFloor);//if ec waiting, immediately go to floor
            nextClosestCar.currentDirection=riderDir;

        }
        else if(nextClosestCar.currentDirection==riderDir&& !nextClosestCar.getDropOffFloor().contains(startFloor)
                &&nextClosestCar.status!=Status.WAITING && isHeadedThatWay(nextClosestCar.currentDirection, startFloor,
                nextClosestCar.currentFloor)&&nextClosestCar.getCarWeight()<500){
            nextClosestCar.addDropOffFloor(startFloor);//if ec waiting, immediately go to floor
        }
        else if(furthestCar.status==Status.WAITING){
            furthestCar.callElevator(startFloor);//if ec waiting, immediately go to floor
            furthestCar.currentDirection=riderDir;

        }
        else if(furthestCar.currentDirection==riderDir && !furthestCar.getDropOffFloor().contains(startFloor)
                && furthestCar.status!=Status.WAITING && isHeadedThatWay(furthestCar.currentDirection, startFloor, furthestCar.currentFloor)&&furthestCar.getCarWeight()<500){
            furthestCar.addDropOffFloor(startFloor);//if ec waiting, immediately go to floor
        } else {
            System.out.println(startFloor+ " STILL NEEDS SERVICING");
            stillNeedServicing.add(startFloor);
        }

        //check if it's waiting

        //if not waiting, check if it's headed in direction passenger is headed, not at capacity

        //repeat for next closest elevator, repeat

        //once elevator is found for a floor, remove floor from buffer, unless people still waiting in queue
        //if no floor is found, keep in buffer until an elevator becomes empty, changes direction
    }

     static void boarding(ElevatorCar ec,int currentFloor, ArrayList<Text> queuesCurr,ArrayList<Text> travellersCount){

         queueOffsets.set(currentFloor-1,0);
            for (Rider passenger: floorQueues.get(currentFloor-1)){
                if (passenger.direction.equals(ec.currentDirection)){
                if (ec.getCarWeight()<500 && (ec.currentFloor==passenger.startFloor)) {
                    passenger.enterElevator(ec);
                    ec.setCarWeight(passenger.weight);
                    ec.setStandingRoom();
                    ec.travelingRiders.add(passenger);

                    if(!ec.getDropOffFloor().contains(passenger.endFloor)){
                        ec.addDropOffFloor(passenger.endFloor);
                    }
                } else {//going in el. direction but already full
                    if(ec.travelingRiders.size()>0){
                        passenger.walkToQueue(floorQueues.get(passenger.startFloor-1));
                    }
                    queueOffsets.set(currentFloor-1,queueOffsets.get(currentFloor-1)+10);
                }

            } else {//not going in elevator direction
                    if(ec.travelingRiders.size()>0){
                        passenger.walkToQueue(floorQueues.get(passenger.startFloor-1));
                    }
                    queueOffsets.set(currentFloor-1,queueOffsets.get(currentFloor-1)+10);
                }
            }
            floorQueues.get(currentFloor-1).removeAll(ec.travelingRiders);//can't remove boarded passengers from queue during for each loop, so after concludes remove traveling passengers
         if (floorQueues.get(currentFloor-1).isEmpty()){//if no one in queue, remove from floors to pick up from
         }
         updateQueues(queuesCurr);
         updateTravellers(travellersCount);
     }

     static Boolean isHeadedThatWay(String direction, int startFloor, int eCurrentFloor){
         Boolean canStop=false;
            if (direction=="UP"){
                if (startFloor-eCurrentFloor>0){
                    canStop= true;
                } else {
                    canStop= false;
                }
            } else if (direction=="DOWN"){
                if (startFloor-eCurrentFloor>0){
                    canStop= false;
                } else {
                    canStop= true;
                }
            }
            return canStop;
     }
      static void updateQueues(ArrayList<Text> current){//updates the # of people in each queue on GUI
          current.get(0).setText(((Integer)(floorQueues.get(0).size())).toString());
          current.get(1).setText(((Integer)(floorQueues.get(1).size())).toString());
          current.get(2).setText(((Integer)(floorQueues.get(2).size())).toString());
          current.get(3).setText(((Integer)(floorQueues.get(3).size())).toString());
          current.get(4).setText(((Integer)(floorQueues.get(4).size())).toString());
    }
    static void updateTravellers(ArrayList<Text> travellers){
        travellers.get(0).setText("Elevator 1 ("+((Integer)(ec1.travelingRiders.size())).toString()+" Riders)");
        travellers.get(1).setText("Current Floor: "+((Integer)(ec1.currentFloor)).toString());
        travellers.get(2).setText("Current Weight: "+((Integer)(ec1.getCarWeight())).toString()+" lbs");
        travellers.get(3).setText("Elevator 1 ("+((Integer)(ec2.travelingRiders.size())).toString()+" Riders)");
        travellers.get(4).setText("Current Floor: "+((Integer)(ec2.currentFloor)).toString());
        travellers.get(5).setText("Current Weight: "+((Integer)(ec2.getCarWeight())).toString()+" lbs");
        travellers.get(6).setText("Elevator 1 ("+((Integer)(ec3.travelingRiders.size())).toString()+" Riders)");
        travellers.get(7).setText("Current Floor: "+((Integer)(ec3.currentFloor)).toString());
        travellers.get(8).setText("Current Weight: "+((Integer)(ec3.getCarWeight())).toString()+" lbs");
    }


     static LinkedList<Rider> getWaitingRiders(int startingFloor){
        return floorQueues.get(startingFloor-1);
    }
    static void getAverageWait(int wait,int indWait){

        awl.setText("Average Wait: "+wait+" seconds");
        if (indWait>longestWait){
            longestWait=indWait;
            lw.setText("Longest Wait: "+longestWait+" seconds");
        }

    }
}
