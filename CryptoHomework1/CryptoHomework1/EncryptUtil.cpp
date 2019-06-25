//
//  EncryptUtil.cpp
//  CryptoHomework1
//
//  Created by Devin Sevy on 2/26/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "EncryptUtil.hpp"

#include <iostream>
#include <string>
#include <array>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

using namespace std;

array<uint8_t, 8> encryptKey(string password) {
    array<uint8_t, 8> key;
    for(int i=0;i < 8;i++) {
        key[i] = 0;
    }
    for(int i=0;i < password.size();i++) {
        key[i%8] ^= password[i];
    }
    return key;
}

array<uint8_t, 256> fisherYates(array<uint8_t, 256> sub256) {
    srand ( time(NULL) );
    
    for (int i = 255; i >= 0; i--) {
        int j = rand() % (i+1);
        
        uint8_t temp = sub256[j];
        sub256[j] = sub256[i];
        sub256[i] = temp;
    }
    return sub256;
}

array<uint8_t, 8> setInitialZero() {
    array<uint8_t, 8> sub8;
    for(int i=0;i < 8;i++) {
        sub8[i] = 0;
    }
    return sub8;
}

array<uint8_t, 8> encryption(array<uint8_t, 8> sub8, array<uint8_t, 8> bcypher, array<array<uint8_t, 256>, 8> sub8_256) {
    for(int i=0;i < 16;i++) {
        for(int i=0;i < sub8.size();i++) {
            sub8[i] ^= bcypher[i];
        }
        for(int i=0;i < sub8.size();i++) {
            sub8[i] = sub8_256[i][sub8[i]];
        }
        uint8_t zeroByte = sub8[0];
        for(int i=0;i < 7;i++) {
            uint8_t nextByte = sub8[i+1] >> 7;
            sub8[i] = sub8[i] << 1 | nextByte;
        }
        sub8[7] = sub8[7] << 1 | zeroByte >> 7;
    }
    return sub8;
}


string decodedMessage(vector<array<uint8_t, 8>> message, uint8_t length) {
    std::string msg;
    uint8_t end = 0;
    for (int i=0;i < message.size(); i++){
        for (int j=0;j < message[i].size(); j++){
            if (end == length) break;
            msg += (char)(message[i][j]);
        }
    }
    return msg;
}

array<array<uint8_t, 256>, 8> reverse(array<array<uint8_t, 256>, 8> slvsub8_256) {
    array<array<uint8_t, 256>, 8> sub8_256;
    for(int i=0;i < 8;i++) {
        array<uint8_t, 256> sub256;
        for(int j=0;j < sub256.size();j++) {
            uint8_t temp = slvsub8_256[i][j];
            sub256[temp] = j;
        }
        sub8_256[i] = sub256;
    }
    return sub8_256;
}

array<uint8_t, 8> decription(array<uint8_t, 8> slvsub8, array<uint8_t, 8> slvbcypher, array<array<uint8_t, 256>, 8> slvsub8_256) {
    for(int i=0;i < 16;i++) {
        uint8_t lastByte = slvsub8[7];
        for(int j=slvsub8.size()-1;j >= 1;j--) {
            uint8_t revLastByte = slvsub8[j] >> 1;
            slvsub8[j] = slvsub8[j-1] << 7 | revLastByte;
        }
        slvsub8[0] = lastByte << 7 | slvsub8[0] >> 1;
        for(int j=0;j < slvsub8.size();j++) {
            slvsub8[j] = slvsub8_256[j][slvsub8[j]];
        }
        for(int j=0;j < slvsub8.size();j++) {
            slvsub8[j] ^= slvbcypher[j];
        }
    }
    return slvsub8;
}

bool testPassword(string bmsg, string emsg) {
    //cout << bmsg << endl;
    emsg.erase(std::remove(emsg.begin(), emsg.end(), '\0'), emsg.end());
    //cout << emsg << endl;
    if(bmsg == emsg) return true;
    return false;
}
