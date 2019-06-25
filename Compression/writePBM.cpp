//
//  writePBM.cpp
//
//  Created by rongminjin on 2/19/19.
//  Copyright © 2019 rongminjin. All rights reserved.
//

#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include "Header.h"
using namespace std;

bool writePBM(const std::vector<uint16_t> &Data, const unsigned Width, const unsigned Height, const std::string FileName) {
    //Open file
    ofstream myof(FileName, std::ofstream::binary);
    if(!myof){
        std::cerr<<"Unable to open file";
        return false;
    }
    
    //Caculation for data array
    unsigned newWidth = (8 -Width % 8 + Width );
    unsigned bitBlockWidth = newWidth / 4;
    
    long size = Data.size();
    long length1 = size * 4;
    
    //decode 16bit data array
    uint16_t temp[length1];
    
    //get 4 bit data from 16 bit array
    for (long j = 0; j < size; ++j) {
        long x = j % bitBlockWidth;
        long y = j / bitBlockWidth;
        long index = 4 * bitBlockWidth * y + x;
        uint16_t tempInt = Data[j];
        uint16_t temp4bit=0;
        for (int i = 0 ; i <= 15 ; i++ ) {
            temp4bit<<=1;
            temp4bit ^= tempInt & 1;
            tempInt>>=1;
            if ( (i+1) % 4 == 0 ) {
                temp[index] = temp4bit;
                temp4bit =0;
                index= index +bitBlockWidth;
            }
        }
    }
    //PBM Header
    myof<<"P4"<<std::endl;
    myof<<Width<<" "<<Height<<std::endl;
    //merge and write data
    for (long m = 0 ; m < size * 2; ++m) {
        uint8_t temp1 = (temp[m*2] << 4) + temp[m*2+1];
        myof << temp1 ;
    }
    myof.close();
    
    return true;
}






