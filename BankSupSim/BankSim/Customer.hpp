//
//  Customer.hpp
//  BankSupSim
//
//  Created by Devin Sevy on 4/1/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef Customer_hpp
#define Customer_hpp

#include <stdio.h>

class Customer {
private:
    double arrivalTime;
    double processTime;
public:
    Customer() {
        arrivalTime = 0;
        processTime = 0;
    }
    Customer (double at, double rand) {
        arrivalTime = at;
        processTime = (2 + rand);
    }
    
    bool done() {
        return --processTime < 0;
    }
    double getArrival() {
        return arrivalTime;
    }
};

#endif /* Customer_hpp */
