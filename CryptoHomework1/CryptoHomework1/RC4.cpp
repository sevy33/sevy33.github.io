//
//  RC4.cpp
//  CryptoHomework1
//
//  Created by Devin Sevy on 4/9/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "RC4.hpp"

#include <iostream>
#include "RC4.hpp"

using namespace std;

RC4::RC4(string input) {
    key = input;
    for (int i = 0; i < 256; i ++) {
        s[i] = i;
    }
    
    int j = 0;
    for (int i = 0; i < 256; i ++) {
        j = (j + s[i] + key.at(i % key.length())) % 256;
        swap(s[i], s[j]);
    }
}

unsigned char RC4::getByte(int &i, int &j) {
    
    i = (i+1) % 256;
    j = (j+s[i]) % 256;
    swap(s[i], s[j]);
    unsigned char temp = s[ (s[i] + s[j]) % 256];
    
    return temp;
}

string RC4::encryptDecrypt(string input) {
    string result = "";
    int i = 0;
    int j = 0;
    for (int k = 0; k < input.length(); k++) {
        unsigned char temp = this->getByte(i, j);
        unsigned char results = input.at(k) ^ temp;
        result += results;
    }
    return result;
}
