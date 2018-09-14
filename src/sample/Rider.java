package sample;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Rider extends Pane
{
     int weight;
     int startFloor;
     int endFloor;
     private int waitDuration;
     ImageView riderAni;
     File file1;
     File file2= new File("src/sample/waiting2.png");
     Image image2 = new Image(file2.toURI().toString());
     Image image;
     Text floorLabel= new Text();
     String direction;


    public Rider(Pane pane, final int riderWeight, final int fromFloor, final int toFloor, int waitingTime){
        //rider constructor
        weight = riderWeight;
        startFloor = fromFloor;
        endFloor = toFloor;
        waitDuration = waitingTime;
        double initYVal;
        switch(fromFloor){
            case 1:
                initYVal = 470.0;
                break;
            case 2:
                initYVal = 183.0;
                break;
            default:
                initYVal = 0;
        }
        file1 = new File("src/sample/rider1.gif");
        image = new Image(file1.toURI().toString());
        riderAni = new ImageView(image);
        riderAni.setFitHeight(133.0);
        riderAni.setFitWidth(69.0);
        riderAni.setLayoutX(42.0);
        riderAni.setLayoutY(initYVal);
        riderAni.setPreserveRatio(true);
        floorLabel.setText(""+toFloor+"");
        floorLabel.setLayoutX(80.0);
        floorLabel.setLayoutY(initYVal+20);
        pane.getChildren().add(riderAni);
        pane.getChildren().add(floorLabel);
        if (endFloor-startFloor>0){
            direction = "UP";
        }   else {
            direction = "DOWN";
        }


    }

    private void startTimer(){
        //starts total wait time timer
        Timer timer= new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                waitDuration++;
                //System.out.println("Has been waiting for " + waitDuration + " secs");
                System.out.println("Current elevator 1 status is: " + Controller.ec1.status);
                System.out.println("floors to drop off are: " + Controller.ec1.getDropOffFloor());
                System.out.println("floor to pick up are: " + Controller.ec1.getPickUpFloor());
            }
        };
        timer.scheduleAtFixedRate(task,1000,1000);
    }

//    public int stopTimer(){
//        int totalWait = waitDuration;
//        timer.cancel();
//        System.out.print("Total wait time was " + totalWait);
//        return totalWait;
//    }

     void walkToQueue(LinkedList<Rider> startFloorQueue){
        //uses the translate property to adjust object's x value
         riderAni.setImage(image);
        final int beginningQueueSize = startFloorQueue.size();
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(250-Controller.queueOffsets.get(startFloor-1));
        tt.setDuration(Duration.seconds(2));
        //tt.play();
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(250-Controller.queueOffsets.get(startFloor-1)-20);
         fl.setDuration(Duration.seconds(1.9));
        //fl.play();
         ParallelTransition pt = new ParallelTransition(tt,fl);
         pt.play();
        pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //fires once person stops walking
                startTimer();
//                if (w.size()==1){
//
                   riderAni.setImage(image2);
                    if (beginningQueueSize==1){
                        //System.out.println("Rider has requested elevator");
                        Controller.getElevator(startFloor,direction);
                    }

//                    callElevator(w,startFloor,endFloor);
//                } else {
//                //    System.out.println(w.size() + " people in queue now");
//                }
            }
        });
    }

     void enterElevator(final ElevatorCar ec){
         riderAni.setImage(image);
         TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(400-ec.getStandingRoom());
        tt.setDuration(Duration.seconds(2));
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(400-ec.getStandingRoom()-20);
         fl.setDuration(Duration.seconds(1.8));
         ParallelTransition pt = new ParallelTransition(tt,fl);
        pt.play();
         final Boolean isLast = this==Controller.getWaitingRiders(startFloor).getLast();
         final int carWeight = ec.getCarWeight();
        pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                riderAni.setOpacity(.5);
                riderAni.setImage(image2);
                //riderAni.setRadius(15);
                if (isLast|| carWeight==300) {

                    Controller.closeDoorButton(ec);
                }
            }
        });

    }
     void leaveElevator(){
         riderAni.setImage(image);
         //cancel timer
        TranslateTransition tt = new TranslateTransition();
         riderAni.setOpacity(1);
         //circle.setRadius(20);
        tt.setNode(riderAni);
        tt.setToX(450);
        tt.setDuration(Duration.seconds(.8));
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(450-20);
         fl.setDuration(Duration.seconds(1));
         ParallelTransition pt = new ParallelTransition(tt,fl);
        pt.play();
        pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                walkFromElevator();
            }
        });
    }

    public void walkFromElevator(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(700);
        tt.setDuration(Duration.seconds(3));
        TranslateTransition fl = new TranslateTransition();
        fl.setNode(floorLabel);
        fl.setToX(700-20);
        fl.setDuration(Duration.seconds(2.8));
        ParallelTransition pt = new ParallelTransition(tt,fl);
        pt.play();
        pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                riderAni.setVisible(false);
                floorLabel.setVisible(false);
            }
        });
    }

}
