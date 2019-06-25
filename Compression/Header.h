//
//  Header.h
//  Compression
//
//  Created by Alex Moyle on 2/14/19.
//  Copyright © 2019 Alex Moyle. All rights reserved.
//

#ifndef Header_h
#define Header_h

#include <cstdint>
#include <optional>
#include <tuple>
#include <fstream>
#include <vector>
#include <cmath>
#include <regex>
#include <iostream>
#include <sstream>
#include <experimental/optional>
#include <map>
#include <bitset>

// NB this only compiles using -std=c++17 or later
// if anything goes wrong, these functions should print an explanation and then return either false or an empty option type

std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>>
readPBM(const std::string FileName);

bool **bitToByteArr(std::istream &input, const unsigned int &width, const unsigned int &arrWidth,
                    const unsigned int &arrHeight);
std::vector<uint16_t> *pbmToMatrix(std::istream &input, const unsigned int &width, const unsigned int &height);

bool compress(const std::vector<uint16_t> &Data, const unsigned Width,
              const unsigned Height, const std::string FileName);

std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>>
readCompressed(const std::string FileName);

bool writePBM(const std::vector<uint16_t> &Data, const unsigned Width,
              const unsigned Height, const std::string FileName);

std::vector<uint16_t> readBitstream(std::istringstream &input, std::map<std::string, uint16_t> hash);

std::map<std::string, uint16_t> makeHash();

void readFourBytes(std::istream &input, char* temp);
#endif /* Header_h */
