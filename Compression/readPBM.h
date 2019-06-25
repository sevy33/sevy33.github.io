//
// Created by Oliver Yu on 2019-02-18.
//

#include <iostream>
#include <optional>
#include <tuple>
#include <cstdint>
#include <fstream>
#include <sstream>
#include <vector>
#include <cmath>
#include <regex>


/**
 * reads the pbm file into 4x4 matrix represented by a 16bit unsigned int
 * @param FileName
 * @return optional tuple with width, height, and vector of unsigned 16 bit int
 */
std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>> readPBM(const std::string FileName);

/**
 * reads the pbm and converts it to a vector of uint16_t where each int represents a 4x4 bit matrix
 * @param input
 * @param width
 * @param height
 * @return
 */
std::vector<uint16_t>* pbmToMatrix(std::ifstream &input, const unsigned int &width, const unsigned int &height);

/**
 * Extracts each bit from the byte and create a actual 2d raster
 * @param input
 * @param width
 * @param arrWidth
 * @param arrHeight
 * @return
 */
bool **bitToByteArr(std::ifstream &input, const unsigned int &width, const unsigned int &arrWidth,
                    const unsigned int &arrHeight);

