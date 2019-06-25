//
//  ClassifierTest.hpp
//  assignment3
//
//  Created by Devin Sevy on 1/26/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef ClassifierTest_hpp
#define ClassifierTest_hpp

#include <stdio.h>
#include <iostream>
#include <vector>
#include <math.h>
#include <iomanip>
#include <string>
#define PI 3.14159265
using namespace std;

struct slopes {
    double slopeAB;
    double slopeBC;
    double slopeCD;
    double slopeDA;
};
struct dists {
    double distAB;
    double distBC;
    double distCD;
    double distDA;
};
double findSlope(double x1, double y1, double x2, double y2);
double findDist(double x1, double y1, double x2, double y2);
struct slopes slopeStruct(double xB, double yB, double xC, double yC, double xD, double yD);
struct dists distsStruct(double xB, double yB, double xC, double yC, double xD, double yD);
bool isParallelograms(struct slopes& slope);
bool isTrapezoid(slopes &slope);
bool isSquareOrRhom(dists &dist);
bool isSquare(dists &dist);
string printShape(slopes &slope, dists &dist);
void checkPoints(double xB, double yB, double xC, double yC, double xD, double yD);
vector<string> getErrorLog();
bool isKite(dists &dist);

#endif /* ClassifierTest_hpp */
