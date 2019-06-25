//
//  main.cpp
//  CryptoHomework1
//
//  Created by Devin Sevy on 2/25/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "EncryptUtil.hpp"
#include "RC4.hpp"

#include <iostream>
#include <string>
#include <array>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <vector>
#include <bitset>

using namespace std;

array<array<uint8_t, 256>, 8> sub8_256;

int main(int argc, const char * argv[]) {
    string pass = "password1";
    string msg = "Your salary is $1000";
    
    array<uint8_t, 8> bcypher = encryptKey(pass);
    
    array<uint8_t, 256> sub256;
    uint8_t j = 0;
    for(int i=0;i < 256;i++) {
        sub256[i] = j;
        j++;
    }

    array<uint8_t, 256> previous = sub256;
    for(int i=0;i < sub8_256.size();i++) {
        sub8_256[i] = fisherYates(previous);
        previous = sub8_256[i];
    }
    
    vector<array<uint8_t, 8>> msgVec;
    array<uint8_t, 8> msgData;
    for(int i=0;i < msg.size();i++) {
        if(i % 8 == 0) msgData = setInitialZero();
        msgData[i%8] = msg[i];
        if(i % 8 == 7) msgVec.push_back(msgData);
    }
    if(!(msg.size() % 8 == 0)) msgVec.push_back(msgData);
    
    array<array<uint8_t, 256>, 8> slvsub8_256 = reverse(sub8_256);
    for(int i=0;i < msgVec.size();i++) {
        msgVec[i] = encryption(msgVec.at(i), bcypher, sub8_256);
    }
    
    cout << "Encoded Message: " << decodedMessage(msgVec, msg.size()) << endl;
    
    //start decrypting
    string slvPass = "password1";
    array<uint8_t, 8> slvbcypher = encryptKey(slvPass);
    
    vector<array<uint8_t, 8>> slvmsgVec;
    array<uint8_t, 8> slvData = setInitialZero();
    for(int i=0;i < msgVec.size();i++) {
        slvData = decription(msgVec.at(i), slvbcypher, slvsub8_256);
        slvmsgVec.push_back(slvData);
     }
    
    string dmsg = decodedMessage(slvmsgVec, msg.size());
    
    if(testPassword(msg, dmsg)) cout << "Decoded Message: " << dmsg << endl;
    else cout << "Incorrect Password" << endl;
    
    
    //RC4
    RC4 rc4("pass1");
    msg = "Your salary is $1000";
    string encdata = rc4.encryptDecrypt(msg);
    cout << "RC4 Encrypted: " << encdata << endl;
    //Bad Key
    RC4 rc4Bad("pass2");
    string decdata = rc4Bad.encryptDecrypt(encdata);
    cout << "RC4 Bad Key: " << decdata << endl;
    //Good Key
    RC4 rc4Good("pass1");
    decdata = rc4Good.encryptDecrypt(encdata);
    cout << "RC4 Good Key: " << decdata << endl;
    
    //Encrypting 2 Messages
    RC4 rc4SameString("samePass");
    msg = "Begin same string";
    string same = rc4SameString.encryptDecrypt(msg);
    cout << "RC4 Same Encrypted: " << same << endl;
    msg = "End same string";
    string same2 = rc4SameString.encryptDecrypt(msg);
    cout << "RC4 Same Next Encrypted: " << same2 << endl;
    
    RC4 rc4SameString2("samePass");
    string samedec = rc4SameString2.encryptDecrypt(same + same2);
    cout << "RC4 Same Decryption: " << samedec << endl;
    
    //Bit Flipping
    RC4 rc4Flip("pass1");
    msg = "Your salary is $1000";
    encdata = rc4Flip.encryptDecrypt(msg);
    cout << "RC4 Encrypted: " << encdata << endl;
    //string flip = encdata.substr(encdata.size()-4, encdata.size()-1);
    
    //string bitflip;
    string flipmsg = "Your salary is $9999";
    for(int i=0;i < encdata.size();i++) {
        encdata[i] = encdata.at(i) ^ flipmsg.at(i) ^ msg[i];
    }
    
    //cout << "Bitflip " << bitflip << endl;
    
    RC4 rc4Flip2("pass1");
    decdata = rc4Flip2.encryptDecrypt(encdata);
    cout << "Bitflip: " << decdata << endl;
    
    return 0;
}
