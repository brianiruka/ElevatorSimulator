package sample;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.LinkedList;

public class Floor {
    Boolean upButtonActive=false;
    Boolean downButtonActive=false;
    LinkedList<Rider> ridersInQueue = new LinkedList<Rider>();
    int queueOffset=0;
    int floorJobsTotal=0;


    static void setLights(int floorNum, String dir, int state){
        Circle crc = new Circle();
        if (floorNum == 1){
             crc = Controller.requestButtons.get(0);
                if (state == 0){
                    crc.setFill(Color.TRANSPARENT);
                } else if (state == 1){
                    crc.setFill(Color.PINK);
                }
        } else if (floorNum == 2){
            if (dir.equals("DOWN")){
                crc = Controller.requestButtons.get(1);
            } else if (dir.equals("UP")){
                crc = Controller.requestButtons.get(2);
            }
            if (state == 0){
                crc.setFill(Color.TRANSPARENT);
            } else if (state == 1){
                crc.setFill(Color.PINK);
            }
        } else if (floorNum == 3){
            if (dir.equals("DOWN")){
                crc = Controller.requestButtons.get(3);
            } else if (dir.equals("UP")){
                crc = Controller.requestButtons.get(4);
            }
            if (state == 0){
                crc.setFill(Color.TRANSPARENT);
            } else if (state == 1){
                crc.setFill(Color.PINK);
            }
        } else if (floorNum == 4){
            if (dir.equals("DOWN")){
                crc = Controller.requestButtons.get(5);
            } else if (dir.equals("UP")){
                crc = Controller.requestButtons.get(6);
            }
            if (state == 0){
                crc.setFill(Color.TRANSPARENT);
            } else if (state == 1){
                crc.setFill(Color.PINK);
            }
        }  else if (floorNum == 5) {
            crc = Controller.requestButtons.get(7);
            if (state == 0) {
                crc.setFill(Color.TRANSPARENT);
            } else if (state == 1) {
                crc.setFill(Color.PINK);
            }
        }
    }

}
