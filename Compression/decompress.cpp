//
//  Part3.cpp
//  Compression
//
//  Created by Alex Moyle on 2/19/19.
//  Copyright © 2019 Alex Moyle. All rights reserved.
//

#include "Header.h"

extern const std::string huff[];
//starts with a 32-bit magic number containing the bytes PBMC
//next contains a 4-byte little endian integer storing the image width
//next contains a 4-byte little endian integer storing the image height
//next contains the compressed bitstream

std::map<std::string, uint16_t> makeHash() {
    std::map<std::string, uint16_t> hash;
    for(int i = 0; i < 65536; i++) {
        hash[huff[i]] = (uint16_t) i;
    }
    return hash;
}


std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>>
readCompressed(const std::string FileName) {
    
    std::ifstream input (FileName, std::ios::in | std::ios::binary);
    std::vector<uint16_t>* byteData = new std::vector<uint16_t>;
    
    if (!input) {
        std::cout << "Reading file failed";
        exit(1);
    }
    
    char magic [4];
    char charWidth [4];
    char charHeight [4];
    
    if (!input.read(magic,4)) {
        std::cout << "Magic read failed";
        exit(1);
    }
    //Assert that the first four magic letters are correct according to pbm compression
    assert(magic[0]=='P');
    assert(magic[1]=='B');
    assert(magic[2]=='M');
    assert(magic[3]=='C');
    
    if (!input.read(charWidth, 4)) {
        std::cout<<"Width read failed";
    }
    if (!input.read(charHeight,4)) {
        std::cout<<"Height read failed";
    }
    
    uint32_t width = *reinterpret_cast<int*>(charWidth);
    uint32_t height = *reinterpret_cast<int*>(charHeight);
    
    
    //Take the huffman table and reverse it into a hashmap
    std::map<std::string, uint16_t> hash = makeHash();
    //    std::map<std::string, uint16_t> hash;
    //    for(int i = 0; i < 65536; i++) {
    //        hash[huff[i]] = (uint16_t) i;
    //    }
    
    //Now read in the bitstream
    std::string concatBit= "";
    while(true) {
        if (input.peek() != EOF) {
            char c [1];
            input.read(c, 1);
            for (int i = 7; i >= 0; i--) {
                std::string temp = std::bitset<1>(c[0] >> i).to_string();
                concatBit += temp;
                if(hash.find(concatBit) != hash.end()) {
                    byteData->push_back(hash.at(concatBit));
                    //Reset the string
                    concatBit = "";
                }
            }
        } else {
            break;
        }
    }
    
    //Cast width and height to unsigned
    std::tuple tuple= std::make_tuple((unsigned)width, (unsigned)height, byteData);
    
    return tuple;
}

std::vector<uint16_t> readBitstream(std::istringstream &input, std::map<std::string, uint16_t> hash) {
    std::string concatBit= "";
    std::vector<uint16_t> byteData;
    while(true) {
        if (!input) {
            char c [1];
            input.read(c, 1);
            for (int i = 7; i >= 0; i--) {
                std::string temp = std::bitset<1>(c[0] >> i).to_string();
                concatBit += temp;
                if(hash.find(concatBit) != hash.end()) {
                    byteData.push_back(hash.at(concatBit));
                    //Reset the string
                    concatBit = "";
                }
            }
        } else {
            break;
        }
    }
    return byteData;
}

void readFourBytes(std::istream &input, char* temp) {
    if (!input.read(temp, 4)) {
        std::cout<<"readFourBytes read failed";
    }
}
