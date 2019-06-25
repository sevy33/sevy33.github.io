//
//  fairHotel.hpp
//  PetHotel
//
//  Created by Devin Sevy on 4/4/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef fairHotel_hpp
#define fairHotel_hpp

#include <stdio.h>

using namespace std;

class FairHotel {
    int birds;
    int cats;
    int dogs;
    int maxBirds;
    int maxDogs;
    mutable mutex m;
    condition_variable cvCat;
    condition_variable cvBirdDog;
public:
    FairHotel() {
        birds = 0;
        cats = 0;
        dogs = 0;
    }
    void setMax(int mBird, int mDog) {
        maxBirds = mBird;
        maxDogs = mDog;
    }
    void cat();
    void bird();
    void dog();
    void play() const;
};

#endif /* fairHotel_hpp */
