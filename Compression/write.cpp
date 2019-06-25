//
//  write.cpp
//  PBMCompress
//
//  Created by Alexander Sloan on 2/13/19.
//  Copyright © 2019 Alexander Sloan. All rights reserved.
//

//This is group 6's part 4
#include "Header.h"
//Natalie

//function to write a byte to the file
void writeBytes (const std::vector<uint16_t>& vec, std::ofstream &stream, int counter, int maxWidth, int shiftValue){
    unsigned firstFour = 0;
    unsigned secondFour = 0;
    for(int i=counter; i<maxWidth+counter; i+=2){
        firstFour = vec[i]>>shiftValue &0xF;;
        if(i==maxWidth-1){
            secondFour = 0;
        }else{
            secondFour = vec[i+1]>>shiftValue &0xF;
        }
        unsigned char result = ((firstFour << 4) | secondFour);
        stream << result;
        
    }
}

//function to reverse a uint16
uint16_t reverseInt16(uint16_t nonReversed){
    uint16_t reversed = 0;
    
    for ( uint16_t i = 0; i < 16; i++ )
    {
        reversed |= ( nonReversed >> ( 16 - i - 1 ) & 1 ) << i;
    }
    
    return reversed;
}

bool writePBM(const std::vector<uint16_t> &Data, const unsigned Width,
              const unsigned Height, const std::string FileName){
    std::ofstream os (FileName);
    
    if(!os){
        std::cerr<<"Unable to open file";
        return false;
    }
    
    os<<"P4"<<std::endl;
    os<<Width<<" "<<Height<<std::endl;
    

    std::vector<uint16_t> reversedInts;
    for(uint16_t u: Data){
        uint16_t temp = reverseInt16(u);
        reversedInts.push_back(temp);
    }

    int chunksInRow = Width/4;
    int leftoverInRow = Width%4;

    int chunksInCol = Height/4;
    int leftoverInCol = Height%4;

    int maxWidth = 0;
    int maxHeight = 0;

    if (leftoverInRow == 0){
        maxWidth = chunksInRow;
    }else{
        maxWidth = chunksInRow+1;
    }

    maxHeight = chunksInCol;

    int counter = 0;

    for(int j=0; j<maxHeight; j++){
        
        writeBytes(reversedInts, os, counter, maxWidth, 12);

        writeBytes(reversedInts, os, counter, maxWidth, 8);

        writeBytes(reversedInts, os, counter, maxWidth, 4);
    
        writeBytes(reversedInts, os, counter, maxWidth, 0);
        
        if(maxWidth%2==0){
        counter+=maxWidth;
        }else{
            counter+=maxWidth+1;
        }

    }
    
    //accounting for the height not being an even amount of 4x4 blocks
    if(leftoverInCol != 0){
        if(leftoverInCol == 1){
            writeBytes(reversedInts, os, counter, maxWidth, 12);
        }

        if(leftoverInCol == 2){
            writeBytes(reversedInts, os, counter, maxWidth, 12);
            writeBytes(reversedInts, os, counter, maxWidth, 8);
        }

        if(leftoverInCol == 3){
            writeBytes(reversedInts, os, counter, maxWidth, 12);
            writeBytes(reversedInts, os, counter, maxWidth, 8);
            writeBytes(reversedInts, os, counter, maxWidth, 4);

        }
    }
    return true;
}
