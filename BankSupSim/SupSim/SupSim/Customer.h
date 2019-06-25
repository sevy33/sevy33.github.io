//
//  Customer.h
//  SupSim
//
//  Created by Devin Sevy on 4/2/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef Customer_h
#define Customer_h

# include "Event.h"

# include <list>
# include <vector>
# include <stack>

using namespace std;

class Customer {
private:
    double arrTime;
    double pickTime;
    double waitTime;
public:
    Customer() {}
    Customer(Event& event, double time, double r) {
        arrTime = event.getNextArr();
        pickTime = (time + r);
        waitTime = 0;
    }
    
    double getPickTime() {
        return pickTime;
    }
    
    double getArrTime() {
        return waitTime;
    }
    
    double getWaitTime() {
        return arrTime;
    }
    
    void setWaitTime(double nextTime) {
        waitTime = nextTime;
    }
    
    bool sameInstance(Customer* c) {
        return this == c;
    }
    
};

#endif /* Customer_h */
