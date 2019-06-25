//
//  main.cpp
//  assignment8
//
//  Created by Devin Sevy on 2/13/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include "Header.h"

extern const std::string huff[];

bool compress(const std::vector<uint16_t> &Data, const unsigned Width, const unsigned Height, const std::string FileName) {
    char magic[4];
    magic[0] = 'P';
    magic[1] = 'B';
    magic[2] = 'M';
    magic[3] = 'C';
    uint32_t w = Width;
    uint32_t h = Height;
   
    std::ofstream out(FileName, std::ios::binary);
    if(!out.is_open()) {
        std::cout << "File failed" << std::endl;
        exit(0);
    }
    
    out << magic;
    out << w;
    out << h;
    
    std::map<uint16_t, std::string> map;
    for(int i=0;i < 65536;i++){
        map[i] = huff[i];
    }
    std::string block;
    int sum = 0;
    for(auto &x : Data) {
        std::string temp = map.at(x);
        sum += temp.size();
//        for(int i=0;i < temp.size();i++) {
//            block += std::bitset<8>(temp.substr(i,1)).to_string();
//        }
        //const char* c = temp.c_str();
        //char lo = temp & 0xFF;
        //block = std::bitset<16>(temp).to_string();
        //std::string str = block.substr(0,7);
        //if(block.substr(0,8) == "00000000") block.erase(0,8);
        //if(block != "00000001") out << block << std::endl;
        out << temp;
    }
    int rem = sum%8;
    for(int i=0;i < rem;i++) {
        out << "0";
    }
    /*
    for(auto &x : Data) {
        //char lo = x & 0xFF;
        //char hi = x >> 8;
        block = std::bitset<16>(x).to_string();
        block.erase(0,8);
        if(block != "00000000") out << block;
//        for(int i=7;i >= 0;i--) {
//            std::cout << ((lo >> i) & 0x01);
//        }
    }
    */
    return true;
}

