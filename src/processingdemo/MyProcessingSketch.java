/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the temp late in the editor.
 */
package processingdemo;

import processing.core.*;

public class MyProcessingSketch extends PApplet {

    // 2D Array of objects
    Cell[][] grid;

// Number of columns and rows in the grid
    int cols = 10;
    int rows = 10;

    @Override
    public void setup() {
        size(200, 200);
        grid = new Cell[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                // Initialize each object
                grid[i][j] = new Cell(this, i * 20, j * 20, 20, 20, i + j);
            }
        }
    }

    @Override
    public void draw() {
        background(0);
  // The counter variables i and j are also the column and row numbers and 
        // are used as arguments to the constructor for each object in the grid.  
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                // Oscillate and display each object
                grid[i][j].oscillate();
                grid[i][j].display();
            }
        }
    }
}
