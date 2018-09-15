package sample;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    Text timerLabel= new Text();
    String direction;
    Timer timer;

    public Rider(Pane pane, final int riderWeight, final int fromFloor, final int toFloor, int waitingTime){
        //rider constructor
        weight = riderWeight;
        startFloor = fromFloor;
        endFloor = toFloor;
        waitDuration = waitingTime;
        double initYVal;
        switch(fromFloor){
            case 1:
                initYVal = 550.0;
                break;
            case 2:
                initYVal = 433.0;
                break;
            case 3:
                initYVal = 311.5;
                break;
            case 4:
                initYVal = 190.0;
                break;
            case 5:
                initYVal = 76.0;
                break;
            default:
                initYVal = 550.0;
        }
        file1 = new File("src/sample/rider1.gif");
        image = new Image(file1.toURI().toString());
        riderAni = new ImageView(image);
        riderAni.setFitHeight(60.0);
        riderAni.setFitWidth(30.0);
        riderAni.setLayoutX(42.0);
        riderAni.setLayoutY(initYVal);
        riderAni.setPreserveRatio(true);
        floorLabel.setText(""+toFloor+"");
        floorLabel.setLayoutX(56.0);
        floorLabel.setLayoutY(initYVal);
        timerLabel.setText("0");
        timerLabel.setLayoutX(56.0);
        timerLabel.setLayoutY(initYVal-10);
        pane.getChildren().add(riderAni);
        pane.getChildren().add(floorLabel);
        pane.getChildren().add(timerLabel);
        if (endFloor-startFloor>0){
            direction = "UP";
        }   else {
            direction = "DOWN";
        }


    }

    private void startTimer(){
        //starts total wait time timer
        timer= new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                waitDuration++;
                //System.out.println("Has been waiting for " + waitDuration + " secs");
                //System.out.println("Current elevator 1 status is: " + Controller.ec1.status);
                //System.out.println("floors to drop off are: " + Controller.ec1.getDropOffFloor());
                //System.out.println("floor to pick up are: " + Controller.ec1.getPickUpFloor());
                timerLabel.setText(Integer.toString(waitDuration));
            }
        };
        timer.scheduleAtFixedRate(task,1000,1000);
    }

    public void stopTimer(){
        timer.cancel();
        //Controller.completeTrips++;
        Controller.totalWait+=waitDuration;
        Controller.averageWait=(Controller.totalWait/++Controller.completeTrips);
        Controller.getAverageWait(Controller.averageWait,waitDuration);
    }

     void walkToQueue(LinkedList<Rider> startFloorQueue){
        //uses the translate property to adjust object's x value
         riderAni.setImage(image);
        final int beginningQueueSize = startFloorQueue.size();
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(250-Controller.queueOffsets.get(startFloor-1));
        tt.setDuration(Duration.seconds(4));
        //tt.play();
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(250-Controller.queueOffsets.get(startFloor-1));
         fl.setDuration(Duration.seconds(4));
         TranslateTransition tl = new TranslateTransition();
         tl.setNode(timerLabel);
         tl.setToX(250-Controller.queueOffsets.get(startFloor-1));
         tl.setDuration(Duration.seconds(4));
        //fl.play();
         ParallelTransition pt = new ParallelTransition(tt,fl,tl);
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
         Double speed= Math.random()*3+1.5;
         riderAni.setImage(image);
         TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(315-ec.getStandingRoom());
        tt.setDuration(Duration.seconds(speed));
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(315-ec.getStandingRoom()-5);
         fl.setDuration(Duration.seconds(speed));
         TranslateTransition tl = new TranslateTransition();
         tl.setNode(timerLabel);
         tl.setToX(315-ec.getStandingRoom()-5);
         tl.setDuration(Duration.seconds(speed));
         ParallelTransition pt = new ParallelTransition(tt,fl,tl);
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
//     void leaveElevator(){
//         Double speed= Math.random()*2+1.5;
//         Double dist = Math.random()*50+350;
//         riderAni.setImage(image);
//         //cancel timer
//        TranslateTransition tt = new TranslateTransition();
//         riderAni.setOpacity(1);
//         //circle.setRadius(20);
//        tt.setNode(riderAni);
//        tt.setToX(dist);
//        tt.setDuration(Duration.seconds(speed));
//         TranslateTransition fl = new TranslateTransition();
//         fl.setNode(floorLabel);
//         fl.setToX(dist-20);
//         fl.setDuration(Duration.seconds(speed));
//         ParallelTransition pt = new ParallelTransition(tt,fl);
//        pt.play();
//        pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent event) {
//                walkFromElevator();
//            }
//        });
//    }

    public void walkFromElevator(){
        stopTimer();
        Double speed= Math.random()*3+7;
        riderAni.setImage(image);
        riderAni.setOpacity(1);
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(590);
        tt.setDuration(Duration.seconds(speed));
        TranslateTransition fl = new TranslateTransition();
        fl.setNode(floorLabel);
        fl.setToX(590-20);
        fl.setDuration(Duration.seconds(speed));
        TranslateTransition tl = new TranslateTransition();
        tl.setNode(timerLabel);
        tl.setToX(590-20);
        tl.setDuration(Duration.seconds(speed));
        ParallelTransition pt = new ParallelTransition(tt,fl,tl);
        pt.play();
        pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                riderAni.setVisible(false);
                floorLabel.setVisible(false);
                timerLabel.setVisible(false);
            }
        });
    }

}
