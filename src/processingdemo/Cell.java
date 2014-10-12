/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processingdemo;

import processing.core.PApplet;

/**
 *
 * @author guilherme
 */
public class Cell {

    PApplet parent;

    // A cell object knows about its location in the grid as well as its size with the variables x,y,w,h.
    float x, y;   // x,y location
    float w, h;   // width and height
    float angle; // angle for oscillating brightness

    // Cell Constructor
    Cell(PApplet p, float tempX, float tempY, float tempW, float tempH, float tempAngle) {
        parent = p;
        x = tempX;
        y = tempY;
        w = tempW;
        h = tempH;
        angle = tempAngle;
    }

    // Oscillation means increase angle
    void oscillate() {
        angle += 0.02;
    }

    void display() {
        parent.stroke(255);
        // Color calculated using sine wave
        parent.fill(127 + 127 * parent.sin(angle));
        parent.rect(x, y, w, h);
    }
}
