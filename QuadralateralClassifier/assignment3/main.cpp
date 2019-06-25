//
//  main.cpp
//  assignment3
//
//  Created by Devin Sevy on 1/16/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "ClassifierTest.hpp"
#include <iostream>
#include <vector>
#include <math.h>
#include <iomanip>
#include <stdlib.h>
#include <time.h>
#include <exception>
#define PI 3.14159265
using namespace std;

double xB, yB, xC, yC, xD, yD;

int main(int argc, const char * argv[]) {
    vector<string> shapes;
    // Square
    xB = 6; yB = 0; xC = 6; yC = 6; xD = 0; yD = 6;
    checkPoints(xB, yB, xC, yC, xD, yD);
    struct slopes slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    struct dists dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Rectangle
    xB = 6; yB = 0; xC = 6; yC = 10; xD = 0; yD = 10;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Rhombus
    xB = 5; yB = 0; xC = 8; yC = 4; xD = 3; yD = 4;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Trapezoid
    xB = 6; yB = 0; xC = 4; yC = 5; xD = 2; yD = 5;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Kite
    xB = 6; yB = 0; xC = 8; yC = 4; xD = 2; yD = 6;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Invalid number between 0 and 100
    xB = -1; yB = 0; xC = 8; yC = 4; xD = 2; yD = 6;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Two Points the same
    xB = 6; yB = 0; xC = 5; yC = 3; xD = 5; yD = 3;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Linear Cordinates
    xB = 2; yB = 2; xC = 4; yC = 4; xD = 6; yD = 6;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Point C line cross
    xB = 4; yB = 4; xC = 6; yC = 2; xD = 2; yD = 4;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    // Point D line cross
    xB = 0; yB = 6; xC = 6; yC = 6; xD = 8; yD = 4;
    checkPoints(xB, yB, xC, yC, xD, yD);
    slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    dist = distsStruct(xB, yB, xC, yC, xD, yD);
    shapes.push_back(printShape(slope, dist));
    
    for(int i=0;i < shapes.size();i++) {
        cout << shapes[i] << endl;
    }
    
    vector<string> logs = getErrorLog();
    for(int i=0;i < logs.size();i++) {
        cout << i+1 << ": " << logs[i] << endl;
    }

    /*
    vector<string> logs;
    vector<string> shapes;
    for(int i=0;i < 1000;i++) {
        xB = rand()%101;
        yB = rand()%101;
        xC = rand()%101;
        yC = rand()%101;
        xD = rand()%101;
        yD = rand()%101;
        
        checkPoints(xB, yB, xC, yC, xD, yD);
        struct slopes slope = slopeStruct(xB, yB, xC, yC, xD, yD);
        struct dists dist = distsStruct(xB, yB, xC, yC, xD, yD);
        logs = getErrorLog();
        if(logs.size() == 0) {
            string shape = printShape(slope, dist);
            string str = "xA,yA = 0,0 xB,yB = " + to_string(xB) + "," + to_string(yB) + " xC,yC = " + to_string(xC) + "," + to_string(yC) + " xD,y d= " + to_string(xD) + "," + to_string(yD) + "\n";
            shapes.push_back(str + shape + " OK");
        }
        else {
            string str = "xA,yA = 0,0 xB,yB = " + to_string(xB) + "," + to_string(yB) + " xC,yC = " + to_string(xC) + "," + to_string(yC) + " xD,y d= " + to_string(xD) + "," + to_string(yD) + "\n";
            shapes.push_back(str + "ERROR");
        }
    }
     */
    
    return 0;
}

