//
//  main.cpp
//  BankSupSim
//
//  Created by Devin Sevy on 4/1/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "Teller.hpp"
#include "Customer.hpp"
#include "RandDouble.hpp"
#include <iostream>
# include <list>
# include <vector>
# include <stack>
# include <string>
# include <queue>

using namespace std;

RandDouble randomizer;

int main(int argc, const char * argv[]) {
    double avgArrivalRt = atof(argv[1]);
    double maxServTime = atof(argv[2]);
    unsigned int seed = atoi(argv[3]);
    int numTellers = 6;
    int numMinutes = 720;
    double totalWait = 0;
    int numCustomer = 0;
    double totalServeTime = 0;
    vector<Teller> teller(numTellers);
    queue<Customer> line;
    
    
    for(double time = 1;time <= numMinutes;time++) {
        Customer newCustomer(time, randomizer.fRand(.1, maxServTime, seed));
        line.push(newCustomer);

        for (int i = 0; i < numTellers; i++) {
            time++;
            if (teller[i].isFree() & !line.empty()) {
                double frontRand = randomizer.fRand(.1, maxServTime, seed);
                Customer frontCustomer = line.front();
                numCustomer++;
                totalWait += (time - frontCustomer.getArrival());
                teller[i].addCustomer(frontCustomer);
                teller[i].addTime(frontRand);
                line.pop();
            }
        }
    }
    for(int i=0;i < teller.size();i++) {
        totalServeTime += teller[i].getTime();
    }
    cout << "average:" << totalWait/numCustomer << endl;
    cout << "total Cust:" << numCustomer << endl;
    
    return 0;
    
    
}
