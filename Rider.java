package sample;

import javafx.animation.Interpolator;
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
     File file2= new File("src/sample/waiting1.png");
     Image image2 = new Image(file2.toURI().toString());
     File file3= new File("src/sample/riding1.png");
     Image image3= new Image(file3.toURI().toString());
     Image image;
     Text floorLabel= new Text();
    Text timerLabel= new Text();
    String direction;
    Timer timer;
    TimerTask task;
    int elevatorX;


    public Rider(Pane pane, final int riderWeight, final int fromFloor, final int toFloor, int waitingTime){
        //rider constructor
        weight = riderWeight;
        startFloor = fromFloor;
        endFloor = toFloor;
        waitDuration = waitingTime;
        double initYVal;
        switch(fromFloor){
            case 1:
                initYVal = 544.0;
                break;
            case 2:
                initYVal = 425.0;
                break;
            case 3:
                initYVal = 305.0;
                break;
            case 4:
                initYVal = 185.0;
                break;
            case 5:
                initYVal = 65.0;
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
        floorLabel.setLayoutX(55.0);
        floorLabel.setLayoutY(initYVal);
        timerLabel.setText("0");
        timerLabel.setLayoutX(55.0);
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
        task = new TimerTask() {
            @Override
            public void run() {
                waitDuration++;

                timerLabel.setText(Integer.toString(waitDuration));
            }
        };
        timer.scheduleAtFixedRate(task,1000,1000);
    }

    public void stopTimer(){
        timer.cancel();
        task.cancel();
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
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(245-Controller.queueOffsets.get(startFloor-1));
         fl.setDuration(Duration.seconds(4));
         TranslateTransition tl = new TranslateTransition();
         tl.setNode(timerLabel);
         tl.setToX(245-Controller.queueOffsets.get(startFloor-1));
         tl.setDuration(Duration.seconds(4));
         ParallelTransition pt = new ParallelTransition(tt,fl,tl);
         pt.play();
         final int offset= Controller.queueOffsets.get(startFloor-1);

         pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //fires once person stops walking
                if (timer==null){
                    startTimer();
                }
                   riderAni.setImage(image2);
                    if (beginningQueueSize==1  || offset==0||(! Controller.floorDirectionCalled.get(startFloor-1).equals(direction)&&!Controller.floorDirectionCalled.get(startFloor-1).equals("BOTH"))){
                        Controller.getElevator(startFloor,direction);
                    }
            }
        });
    }

     void enterElevator(final ElevatorCar ec){
         //Bounds boundsInScene = floorLabel.localToScene(floorLabel.getBoundsInLocal());
        elevatorX=ec.xPos;
         Double speed= Math.random()*3+1.5;
         riderAni.setImage(image);
         TranslateTransition tt = new TranslateTransition();
         tt.setNode(riderAni);
         //tt.setFromX(boundsInScene.getMaxX());
         tt.setToX(ec.xPos-ec.getStandingRoom());
         tt.setDuration(Duration.seconds(speed));
         TranslateTransition fl = new TranslateTransition();
         fl.setNode(floorLabel);
         fl.setToX(ec.xPos-ec.getStandingRoom()-5);
         fl.setDuration(Duration.seconds(speed));
         TranslateTransition tl = new TranslateTransition();
         tl.setNode(timerLabel);
         tl.setToX(ec.xPos-ec.getStandingRoom()-5);
         tl.setDuration(Duration.seconds(speed));
         ParallelTransition pt = new ParallelTransition(tt,fl,tl);
         pt.play();
         pt.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                riderAni.setOpacity(.5);
                riderAni.setImage(image3);
            }
        });

    }


    public void walkFromElevator(){
        stopTimer();
        int speedConst = elevatorX/150;
        Double speed= Math.random()*3+7;
        riderAni.setImage(image);
        riderAni.setOpacity(1);
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(riderAni);
        tt.setToX(590);
        tt.setDuration(Duration.seconds(speed/speedConst));
        tt.setInterpolator(Interpolator.LINEAR);
        TranslateTransition fl = new TranslateTransition();
        fl.setNode(floorLabel);
        fl.setToX(585);
        fl.setDuration(Duration.seconds(speed/speedConst));
        fl.setInterpolator(Interpolator.LINEAR);
        TranslateTransition tl = new TranslateTransition();
        tl.setNode(timerLabel);
        tl.setToX(585);
        tl.setDuration(Duration.seconds(speed/speedConst));
        tl.setInterpolator(Interpolator.LINEAR);
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
