//
//  ClassifierTest.cpp
//  assignment3
//
//  Created by Devin Sevy on 1/26/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "ClassifierTest.hpp"
#include <iostream>
#include <vector>
#include <math.h>
#include <iomanip>
#include <exception>
#include <string>
#define PI 3.14159265
using namespace std;

vector<string> errorlog;

struct slopes slopeStruct(double xB, double yB, double xC, double yC, double xD, double yD) {
    struct slopes slope;
    
    slope.slopeAB = findSlope(0, 0, xB, yB);
    slope.slopeBC = findSlope(xB, yB, xC, yC);
    slope.slopeCD = findSlope(xC, yC, xD, yD);
    slope.slopeDA = findSlope(xD, yD, 0, 0);
    
    return slope;
}

struct dists distsStruct(double xB, double yB, double xC, double yC, double xD, double yD) {
    struct dists dist;
    
    dist.distAB = findDist(0, 0, xB, yB);
    dist.distBC = findDist(xB, yB, xC, yC);
    dist.distCD = findDist(xC, yC, xD, yD);
    dist.distDA = findDist(xD, yD, 0, 0);
    
    return dist;
}

double findSlope(double x1, double y1, double x2, double y2) {
    double slope = ((y2 - y1)/(x2 - x1));
    if(slope == +INFINITY || slope == -INFINITY) slope = -0;
    return slope;
}

double findDist(double x1, double y1, double x2, double y2) {
    double powX = pow((x2 - x1), 2);
    double powY = pow((y2 - y1), 2);
    double dist = sqrt((powX + powY));
    return dist;
}

bool isParallelograms(slopes &slope) {
    if(slope.slopeAB == slope.slopeCD && slope.slopeBC == slope.slopeDA) return true;
    return false;
}

bool isTrapezoid(slopes &slope) {
    if(slope.slopeAB == slope.slopeCD && slope.slopeBC != slope.slopeDA) return true;
    else if(slope.slopeAB != slope.slopeCD && slope.slopeBC == slope.slopeDA) return true;
    return false;
}

bool isKite(dists &dist) {
    if(dist.distAB == dist.distDA && dist.distBC == dist.distCD) return true;
    return false;
}

bool isSquareOrRhom(dists &dist) {
    double sideTotal = 4 * dist.distAB;
    double sum = dist.distAB + dist.distBC + dist.distCD + dist.distDA;
    if(sideTotal == sum) return true;
    return false;
}

bool isSquare(slopes &slope) {
    if(slope.slopeAB == -slope.slopeBC && slope.slopeCD == -slope.slopeDA) return true;
    return false;
}

string printShape(slopes &slope, dists &dist) {
    if(isParallelograms(slope)) {
        if(isSquareOrRhom(dist)) {
            if(isSquare(slope)) return "Square";
            else return "Rhombus";
        } else {
            return "Rectangle";
        }
    } else {
        if(isTrapezoid(slope)) return "Trapezoid";
        else if(isKite(dist)) return "Kite";
        else return "Scalene";
    }
}

void checkPoints(double xB, double yB, double xC, double yC, double xD, double yD) {
    vector<double> points;
    points.push_back(0);
    points.push_back(0);
    points.push_back(xB);
    points.push_back(yB);
    points.push_back(xC);
    points.push_back(yC);
    points.push_back(xD);
    points.push_back(yD);
    
    for(int i=0;i < points.size();i++) {
        //if(points[i] == NULL) throw "Invalid number of points";
        if(points[i] < 0 || points[i] > 100) errorlog.push_back("Values must be between 0 and 100");
    }
    
    int i = 0;
    while(i < points.size()) {
        double xCheck = points[i];
        double yCheck = points[i+1];
        
        int j = i + 2;
        while(j < points.size()) {
            if(xCheck == points[j] && yCheck == points[j+1]) errorlog.push_back("Coordinates cannot be the same");
            j+=2;
        }
        i+=2;
    }
    
    struct slopes slope = slopeStruct(xB, yB, xC, yC, xD, yD);
    
    if(slope.slopeAB == slope.slopeBC && slope.slopeBC == slope.slopeCD) {
        if(slope.slopeAB + slope.slopeBC + slope.slopeCD + slope.slopeDA != 0) errorlog.push_back("Points cannot be colinear");
    }
    
    if(xC > xB && yC < yB) errorlog.push_back("Lines cannot cross");
    if(xD > xC && yD < yC) errorlog.push_back("Lines cannot cross");
}

vector<string> getErrorLog() {
    return errorlog;
}
