package sample;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.text.DecimalFormat;
import java.util.*;
import javafx.scene.control.ToggleButton;

import static java.util.Arrays.asList;

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
//    @FXML private Polyline triangle1LU;
    @FXML  private Text ec1NextStops,ec2NextStops,ec3NextStops,needUP,needDOWN;
    @FXML private Circle firstFlrBtn,secondFlrBtnUp,secondFlrBtnDown,thirdFlrBtnUp,
            thirdFlrBtnDown,fourthFlrBtnUp,fourthFlrBtnDown,fifthFlrBtn;
    @FXML
    private Polyline triangle1LU,triangle1MU,triangle1RU,
    triangle2LD,triangle2LU,triangle2MD,triangle2MU,triangle2RD,triangle2RU,
    triangle3LD,triangle3LU,triangle3MD,triangle3MU,triangle3RD,triangle3RU,
    triangle4LD,triangle4LU,triangle4MD,triangle4MU,triangle4RD,triangle4RU, triangle5LD,triangle5MD,triangle5RD;
    private ArrayList<Rectangle> shaft1 = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    private ArrayList<Rectangle> shaft2 = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    private ArrayList<Rectangle> shaft3 = new ArrayList<Rectangle>();//holds all elevator doors of a shaft
    static ArrayList<Circle> requestButtons = new ArrayList<Circle>();//holds all elevator doors of a shaft
    static ArrayList<Polyline> floor1Triangles = new ArrayList<Polyline>();
    static ArrayList<Polyline> floor2Triangles = new ArrayList<Polyline>();
    static ArrayList<Polyline> floor3Triangles = new ArrayList<Polyline>();
    static ArrayList<Polyline> floor4Triangles = new ArrayList<Polyline>();
    static ArrayList<Polyline> floor5Triangles = new ArrayList<Polyline>();

    static ArrayList<Text> queueCountsCurrent=new ArrayList<Text>(5);
    static ArrayList<Text> travellersCount=new ArrayList<Text>(9);
    static Text awl;
    static Text lw;
    static ArrayList<Integer> upRequests=new ArrayList<Integer>(5);
    static ArrayList<Integer> downRequests=new ArrayList<Integer>(5);
    static ArrayList<Text> floorStopLabels = new ArrayList<Text>();
    static Double allFloorJobsTotal = 0.0;
    static ElevatorCar ec1,ec2,ec3;
    static ArrayList<ElevatorCar> carArray = new ArrayList<ElevatorCar>();

    static int averageWait=0;
    static int completeTrips=0;
    static int totalWait=0;
    static int longestWait=0;

    static Floor floor1 = new Floor();
    static Floor floor2 = new Floor();
    static Floor floor3 = new Floor();
    static Floor floor4 = new Floor();
    static Floor floor5 = new Floor();
    static List<Floor> floorsList = asList(floor1,floor2,floor3,floor4,floor5);
    //ArrayList<Floor> floorsArray = new ArrayList<Floor>(floorsList);

    @FXML
    public void initialize(){
        //method runs after javafx scene is initialized
        shaft1.addAll(Arrays.asList(floor1_doorLeft1,floor1_doorRight1,floor2_doorLeft1,floor2_doorRight1,
                floor3_doorLeft1,floor3_doorRight1,floor4_doorLeft1,floor4_doorRight1,floor5_doorLeft1,floor5_doorRight1));
        shaft2.addAll(Arrays.asList(floor1_doorLeft2,floor1_doorRight2,floor2_doorLeft2,floor2_doorRight2,
                floor3_doorLeft2,floor3_doorRight2,floor4_doorLeft2,floor4_doorRight2,floor5_doorLeft2,floor5_doorRight2));
        shaft3.addAll(Arrays.asList(floor1_doorLeft3,floor1_doorRight3,floor2_doorLeft3,floor2_doorRight3,
                floor3_doorLeft3,floor3_doorRight3,floor4_doorLeft3,floor4_doorRight3,floor5_doorLeft3,floor5_doorRight3));

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
        ec1= new ElevatorCar(car1 , shaft1,315,"car1",1,0);
        ec2= new ElevatorCar(car2 , shaft2,425,"car2",3,4);
        ec3= new ElevatorCar(car3 , shaft3,540,"car3",5,8);
        carArray.addAll(Arrays.asList(ec1,ec2,ec3));

        updateQueues(queueCountsCurrent);
        awl = averageWaitLabel;
        lw=longestWaitLabel;
        floorStopLabels.add(ec1NextStops); floorStopLabels.add(ec2NextStops);
        floorStopLabels.add(ec3NextStops);
        floorStopLabels.add(needUP);
        floorStopLabels.add(needDOWN);
        requestButtons.add(firstFlrBtn);
        requestButtons.add(secondFlrBtnDown);
        requestButtons.add(secondFlrBtnUp);
        requestButtons.add(thirdFlrBtnDown);
        requestButtons.add(thirdFlrBtnUp);
        requestButtons.add(fourthFlrBtnDown);
        requestButtons.add(fourthFlrBtnUp);
        requestButtons.add(fifthFlrBtn);
        floor1Triangles.addAll(Arrays.asList(triangle1LU,triangle1MU,triangle1RU));
        floor2Triangles.addAll(Arrays.asList(triangle2LU,triangle2LD,triangle2MU,triangle2MD,triangle2RU,triangle2RD));
        floor3Triangles.addAll(Arrays.asList(triangle3LU,triangle3LD,triangle3MU,triangle3MD,triangle3RU,triangle3RD));
        floor4Triangles.addAll(Arrays.asList(triangle4LU,triangle4LD,triangle4MU,triangle4MD,triangle4RU,triangle4RD));
        floor5Triangles.addAll(Arrays.asList(triangle5LD,triangle5MD,triangle5RD));
    }

    @FXML protected void simTimer(MouseEvent actionEvent) {
        int fromFloor= (int)(Math.random()*5+1);
        addRider(fromFloor,27);
    }

    @FXML
    void onHoverOff(MouseEvent actionEvent) {
        Circle circle = (Circle) actionEvent.getTarget();
        circle.setFill(Color.TRANSPARENT);

    }

    @FXML
    void onHoverOn(MouseEvent actionEvent) {
        String id =actionEvent.getPickResult().getIntersectedNode().getId();
        Circle circle = (Circle) myPane.lookup('#'+id);

        int toFloor = Character.getNumericValue(id.charAt(3));

        switch(toFloor){
            case 1:
                circle.setFill(Color.web("#ff00007c"));
                break;
            case 2:
                circle.setFill(Color.web("#00ff007c"));
                break;
            case 3:
                circle.setFill(Color.web("#fffb007c"));
                break;
            case 4:
                circle.setFill(Color.web("#0000ff7c"));
                break;
            case 5:
                circle.setFill(Color.web("#ffa6007c"));
                break;
            default:
                circle.setFill(Color.web("#00000000"));
        }
    }


    @FXML protected void onFloorButtonClick(MouseEvent actionEvent) {
        String id =actionEvent.getPickResult().getIntersectedNode().getId();
        Circle circle = (Circle) myPane.lookup('#'+id);


        circle.setFill(Color.web("#ff00007c"));
        int fromFloor = Character.getNumericValue(id.charAt(1));
        int toFloor = Character.getNumericValue(id.charAt(3));


        switch(toFloor){
            case 1:
                circle.setFill(Color.web("#ff00007c"));
                break;
            case 2:
                circle.setFill(Color.web("#00ff007c"));
                break;
            case 3:
                circle.setFill(Color.web("#fffb007c"));
                break;
            case 4:
                circle.setFill(Color.web("#0000ff7c"));
                break;
            case 5:
                circle.setFill(Color.web("#ffa6007c"));
                break;
            default:
                circle.setFill(Color.web("#00000000"));
        }
        addRider(fromFloor,toFloor);
        }

    void addRider(int ff,int tf){
        int fromFloor = ff;
        int toFloor = tf;
        while (toFloor ==27||toFloor==fromFloor){
            toFloor = (int)(Math.random()*5+1);
        }
        floorsList.get(fromFloor-1).ridersInQueue.add(new Rider(myPane,100,fromFloor,toFloor,0));
        floorsList.get(fromFloor-1).floorJobsTotal++;
        allFloorJobsTotal++;
        totalRiders.setText("Total Riders: "+allFloorJobsTotal.intValue());
        DecimalFormat df2 = new DecimalFormat(".##");
        Double divisionResult;
        floor1Tot.setText(((Integer) floorsList.get(0).floorJobsTotal).toString());
        floor2Tot.setText(((Integer) floorsList.get(1).floorJobsTotal).toString());
        floor3Tot.setText(((Integer) floorsList.get(2).floorJobsTotal).toString());
        floor4Tot.setText(((Integer) floorsList.get(3).floorJobsTotal).toString());
        floor5Tot.setText(((Integer) floorsList.get(4).floorJobsTotal).toString());
        updateQueues(queueCountsCurrent);

        divisionResult = (((Integer) floorsList.get(0).floorJobsTotal)/allFloorJobsTotal)*100;
        floor1Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (((Integer) floorsList.get(1).floorJobsTotal)/allFloorJobsTotal)*100;
        floor2Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (((Integer) floorsList.get(2).floorJobsTotal)/allFloorJobsTotal)*100;
        floor3Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (((Integer) floorsList.get(3).floorJobsTotal)/allFloorJobsTotal)*100;
        floor4Pct.setText(df2.format(divisionResult)+"%");
        divisionResult = (((Integer) floorsList.get(4).floorJobsTotal)/allFloorJobsTotal)*100;
        floor5Pct.setText(df2.format(divisionResult)+"%");

        floorsList.get(fromFloor-1).ridersInQueue.getLast().walkToQueue(floorsList.get(fromFloor-1).ridersInQueue);
        floorsList.get(fromFloor-1).queueOffset += 10;//adjusts x distance to behind second to last person in line
    }

     void getElevator(int startFloor,String riderDir){
        //has already checked to see if button needs to be pressed
         if (riderDir.equals("UP") && !upRequests.contains(startFloor)){
             floorsList.get(startFloor-1).upButtonActive = true;
             Floor.adjustButtonLights(startFloor,riderDir , 1);
         } else if (riderDir.equals("DOWN") && !downRequests.contains(startFloor)){
             floorsList.get(startFloor-1).downButtonActive = true;
             Floor.adjustButtonLights(startFloor,riderDir , 1);
         }

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


        //System.out.println(riderDir + " REQUESTED!!!");
        //an elevator is already prepared to stop at startfloor

         if(closestCar.status==Status.WAITING){
             closestCar.currentDirection=riderDir;//set direction equal to first rider it's picking up
             closestCar.destinationDirection = riderDir;
             closestCar.callElevator(startFloor);//if ec waiting, immediately go to floor
         } //either check if it will stop at your floor anyway, or make it stop there
         else
         if(nextClosestCar.status==Status.WAITING){
             nextClosestCar.currentDirection=riderDir;
             nextClosestCar.destinationDirection = riderDir;
             nextClosestCar.callElevator(startFloor);//if ec waiting, immediately go to floor
         }
         else
         if(furthestCar.status==Status.WAITING){
             furthestCar.currentDirection=riderDir;
             furthestCar.destinationDirection = riderDir;
             furthestCar.callElevator(startFloor);//if ec waiting, immediately go to floor
         }

         else {
                if (riderDir.equals("UP") && !upRequests.contains(startFloor)){
                    upRequests.add(startFloor);
                } else if (riderDir.equals("DOWN") && !downRequests.contains(startFloor)){
                    downRequests.add(startFloor);
                }
             floorStopLabels.get(3).setText("up requests: "+ Controller.upRequests.toString());
             floorStopLabels.get(4).setText("down requests: "+ Controller.downRequests.toString());
            }


         //check if it's waiting

        //if not waiting, check if it's headed in direction passenger is headed, not at capacity

        //repeat for next closest elevator, repeat

        //once elevator is found for a floor, remove floor from buffer, unless people still waiting in queue
        //if no floor is found, keep in buffer until an elevator becomes empty, changes direction
    }

      void boarding(ElevatorCar ec,int currentFloor, ArrayList<Text> queuesCurr,ArrayList<Text> travellersCount){
        LinkedList<Rider> thisQueue = floorsList.get(currentFloor-1).ridersInQueue;
        int thisOffset = floorsList.get(currentFloor-1).queueOffset;
         floorsList.get(currentFloor-1).queueOffset=0;
            for (Rider passenger: thisQueue){
                if (passenger.direction.equals(ec.currentDirection)){
                if (ec.getCarWeight()<500 && (ec.currentFloor==passenger.startFloor)) {
                    passenger.enterElevator(ec);
                    ec.setCarWeight(passenger.weight);
                    ec.setStandingRoom();
                    ec.travelingRiders.add(passenger);

                    if(!ec.getDropOffFloor().contains(passenger.endFloor)){
                        ec.addDropOffFloor(passenger.endFloor);
                    }
                    floorStopLabels.get(0).setText(ec1.getDropOffFloor().toString());
                    floorStopLabels.get(1).setText(ec2.getDropOffFloor().toString());
                    floorStopLabels.get(2).setText(ec3.getDropOffFloor().toString());

                } else {//going in el. direction but already full
                    if(ec.travelingRiders.size()>0 && !passenger.foundElevator){
                        passenger.walkToQueue(floorsList.get(passenger.startFloor-1).ridersInQueue);
                    }
                    floorsList.get(currentFloor-1).queueOffset += 10;
                }

            }
            else {//not going in elevator direction
                    if(ec.travelingRiders.size()>0 && !passenger.foundElevator){
                        passenger.walkToQueue(floorsList.get(passenger.startFloor-1).ridersInQueue);
                    }
                    floorsList.get(currentFloor-1).queueOffset += 10;
                }
            }
            floorsList.get(currentFloor-1).ridersInQueue.removeAll(ec.travelingRiders);//can't remove boarded passengers from queue during for each loop, so after concludes remove traveling passengers
         updateQueues(queuesCurr);
         updateTravellers(travellersCount);
          if (floorsList.get(currentFloor-1).ridersInQueue.isEmpty()){
              if (upRequests.contains(currentFloor)){
                  upRequests.remove((Integer)currentFloor);
              } else if (downRequests.contains(currentFloor)){
                  downRequests.remove((Integer)currentFloor);
              }
          }
     }


       void updateQueues(ArrayList<Text> current){//updates the # of people in each queue on GUI
    }
     void updateTravellers(ArrayList<Text> travellers){
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

      LinkedList<Rider> getWaitingRiders(int startingFloor){
        return floorsList.get(startingFloor-1).ridersInQueue;
    }
     void getAverageWait(int wait,int indWait){

        awl.setText("Average Wait: "+wait+" seconds");
        if (indWait>longestWait){
            longestWait=indWait;
            lw.setText("Longest Wait: "+longestWait+" seconds");
        }
    }

    static int setWaitingCar(){//determines, based on traffic, where a car that has entered wait state should go
        int busiestFloorReq = 0; //how many cars the busiest floor should have
        int nextBusiestFloorReq = 0; //how many cars the busiest floor should have

        int floor1 =  floorsList.get(0).floorJobsTotal;
        int floor2 =  floorsList.get(1).floorJobsTotal;
        int floor3 =  floorsList.get(2).floorJobsTotal;
        int floor4 =  floorsList.get(3).floorJobsTotal;
        int floor5 =  floorsList.get(4).floorJobsTotal;
        List<Integer> floorTraffic = new ArrayList<Integer>();
        floorTraffic.addAll(Arrays.asList(floor1,floor2,floor3,floor4,floor5));
        List<Integer> floorTrafficSorted = floorTraffic;
        Collections.sort(floorTrafficSorted);
        int largestInt = floorTrafficSorted.get(0);
        int nextLargestInt = floorTrafficSorted.get(1);

        if (largestInt >= nextLargestInt * 3){
            busiestFloorReq = 3;
        } else if (largestInt >= nextLargestInt * 2){
            busiestFloorReq = 2;
            nextBusiestFloorReq = 1;

        } else {
            busiestFloorReq = 1;
            nextBusiestFloorReq = 1;
        }
        int carsAtBusiestFloor = 0;
        int carsAtNextBusiestFloor = 0;

        for (ElevatorCar ec: carArray) {
            if (ec.status == Status.WAITING && ec.currentFloor == busiestFloorReq){
                carsAtBusiestFloor++;
            } else if (ec.status == Status.WAITING && ec.currentFloor == nextBusiestFloorReq){
                carsAtNextBusiestFloor++;
            }
        }

        if (busiestFloorReq - carsAtBusiestFloor > 0 && nextLargestInt > 0){//rest car at busiest floor
            return floorTraffic.indexOf(largestInt)+1;
        } else if (nextBusiestFloorReq - carsAtNextBusiestFloor > 0  && nextLargestInt > 0){//rest car at next busiest floor
            return floorTraffic.indexOf(nextLargestInt)+1;
        } else {//take no action
            return 0;
        }

    }

    void addInstances(){
        for (int i = 0;i<5;i++){
        }

    }
}
