package sample;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.LinkedList;

public class Floor {
    Boolean upButtonActive=false;
    Boolean downButtonActive=false;
    LinkedList<Rider> ridersInQueue = new LinkedList<Rider>();
    int queueOffset=0;
    int floorJobsTotal=0;


    static void adjustButtonLights(int floorNum, String dir, int state){
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

    static Polyline getFloorTriangle(int floor, String dir, String elevatorName){
        ArrayList<Polyline> floorTriangles = new ArrayList<Polyline>();
        switch (floor){
            case 1:
                floorTriangles = Controller.floor1Triangles;
                break;
            case 2:
                floorTriangles = Controller.floor2Triangles;
                break;
            case 3:
                floorTriangles = Controller.floor3Triangles;
                break;
            case 4:
                floorTriangles = Controller.floor4Triangles;
                break;
            case 5:
                floorTriangles = Controller.floor5Triangles;
                break;
        }

        int index = 0;
        if (floor > 1 && floor < 5){
            if (elevatorName.equals("car1")){
                index = 0;
            } else if (elevatorName.equals("car2")){
                index = 2;
            } else if (elevatorName.equals("car3")){
                index = 4;
            }
            if (dir.equals("DOWN")){
                index++;
            }
            return floorTriangles.get(index);

        } else {
            if (elevatorName.equals("car1")){
                index = 0;
            } else if (elevatorName.equals("car2")){
                index = 1;
            } else if (elevatorName.equals("car3")){
                index = 2;
            }
            return floorTriangles.get(index);
        }

    }

    static void onEnterFloor(int floor, String dir, String elevatorName){
        Polyline pl = getFloorTriangle(floor, dir, elevatorName);
        pl.setFill(Color.PINK);
    }

    static void onLeaveFloor(int floor, String dir, String elevatorName){
        Polyline pl = getFloorTriangle(floor, dir, elevatorName);
        pl.setFill(Color.TRANSPARENT);
    }
}
