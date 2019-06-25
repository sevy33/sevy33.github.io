//
//  Teller.hpp
//  BankSupSim
//
//  Created by Devin Sevy on 4/1/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef Teller_hpp
#define Teller_hpp

#include "Customer.hpp"
#include <stdio.h>

class Teller {
private:
    bool free;
    double time;
    Customer customer;
public:
    Teller() {
        free = true;
    }
    
    bool isFree() {
        if (free) return true;
        if (customer.done())
            free = true;
        return free;
    }
    
    void addCustomer(Customer & c) {
        customer = c;
        free = false;
    }
    
    void addTime(double timeat) {
        time += timeat;
    }
    double getTime() {
        return time;
    }
};

#endif /* Teller_hpp */
