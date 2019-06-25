//
//  Event.h
//  SupSim
//
//  Created by Devin Sevy on 4/2/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef Event_h
#define Event_h

class Event {
private:
    double nextArr;
    double nextCheckout;
    double nextDept;
public:
    Event(double time1) {
        nextArr = 0;
        nextCheckout = time1 + 1;
        nextDept = time1 + 1;
    }
    
    double checkEvent(double time, const bool& isList, const bool& isQueue)
    {
        double min = time + 1;
        if(nextArr < min) min = nextArr;
        if(isList) {
            if(min > nextCheckout) min = nextCheckout;
        }
        if(isQueue) {
            if(min > nextDept) min = nextDept;
        }
        return min;
    }
    
    double getNextArr() {
        return nextArr;
    }
    
    double getNextCheckout() {
        return nextCheckout;
    }
    
    double getNextDept() {
        return nextDept;
    }
    
    void setNextArr(double nextTime, double r) {
        nextArr = (nextTime + r);
    }
    
    void setNextCheckout(double nextTime) {
        nextCheckout = nextTime;
    }
    
    void setNextDept(double nextTime, double r) {
        nextDept = (nextTime + r);
    }
};

#endif /* Event_h */
