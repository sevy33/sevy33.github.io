//
//  hotelTests.hpp
//  PetHotel
//
//  Created by Devin Sevy on 4/4/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef petHotel_hpp
#define petHotel_hpp

#include <stdio.h>

using namespace std;

class PetHotel {
    int birds;
    int cats;
    int dogs;
    mutable mutex m;
    condition_variable cvCat;
    condition_variable cvBirdDog;
public:
    PetHotel() {
        birds = 0;
        cats = 0;
        dogs = 0;
    }
    void cat();
    void bird();
    void dog();
    void play() const;
};

#endif /* petHotel_hpp */
