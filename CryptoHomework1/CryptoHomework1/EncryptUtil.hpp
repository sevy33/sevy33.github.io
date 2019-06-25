//
//  EncryptUtil.hpp
//  CryptoHomework1
//
//  Created by Devin Sevy on 2/26/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#ifndef EncryptUtil_hpp
#define EncryptUtil_hpp

#include <iostream>
#include <string>
#include <array>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <vector>

using namespace std;

array<uint8_t, 8> encryptKey(string password);
array<uint8_t, 256> fisherYates(array<uint8_t, 256> sub256);
void swap (uint8_t *a, uint8_t *b);
array<uint8_t, 8> setInitialZero();
array<uint8_t, 8> encryption(array<uint8_t, 8> sub8, array<uint8_t, 8> bcypher, array<array<uint8_t, 256>, 8> sub8_256);

string decodedMessage(vector<array<uint8_t, 8>> message, uint8_t length);

array<array<uint8_t, 256>, 8> reverse(array<array<uint8_t, 256>, 8> slvsub8_256);
array<uint8_t, 8> decription(array<uint8_t, 8> slvsub8, array<uint8_t, 8> slvbcypher, array<array<uint8_t, 256>, 8> slvsub8_256);
bool testPassword(string bmsg, string emsg);

#endif /* EncryptUtil_hpp */
