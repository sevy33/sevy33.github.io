//
//  RandDouble.h
//  SupSim
//
//  Created by Devin Sevy on 4/2/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef RandDouble_h
#define RandDouble_h

#include <stdio.h>
#include <random>
#include <cstdlib>
#include <ctime>


class RandDouble {
private:
    
public:
    double fRand(double fMin, double fMax, unsigned int seed) {
        srand(seed);
        double f = (double) rand() / RAND_MAX;
        return fMin + f * (fMax - fMin);
    }
};


#endif /* RandDouble_h */
