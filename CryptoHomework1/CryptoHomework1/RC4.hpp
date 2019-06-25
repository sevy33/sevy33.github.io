//
//  RC4.hpp
//  CryptoHomework1
//
//  Created by Devin Sevy on 4/9/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef RC4_hpp
#define RC4_hpp

#include <stdio.h>
#include <iostream>

using namespace std;

class RC4 {
private:
    string key;
    unsigned char s[256];
public:
    RC4(string input);
    unsigned char getByte(int &i, int &j);
    string encryptDecrypt(string input);
};

#endif /* RC4_hpp */
