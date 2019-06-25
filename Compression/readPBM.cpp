//
// Created by Oliver Yu on 2019-02-18.
//

//#include "Header.h"
#include "readPBM.h"

bool **bitToByteArr(std::istream &input, const unsigned int &width, const unsigned int &arrWidth,
                    const unsigned int &arrHeight) {

    //make arr on heap b/c stack is not big enough
    bool **arr = new bool *[arrHeight];

    //initialize arr
    for (int i = 0; i < arrHeight; ++i) {
        arr[i] = new bool[arrWidth];
    }

    for (int i = 0; i < arrHeight; ++i) {
        for (int j = 0; j < arrWidth; ++j) {
            arr[i][j] = false;
        }
    }

    int rowIdx = 0, colIdx = 0;

    char byte;

    //read in all the bytes
    int counter = 0;
    while (input.read(&byte, 1)) {
        counter++;
        for (int i = 7; i >= 0; --i) {
            //add bit to current row if its within bounds
            if (colIdx == arrWidth) {
//                if (++rowIdx >= arrHeight) {
//                    break;
//                }
                rowIdx++;
                colIdx = 0;
            }
            arr[rowIdx][colIdx++] = (byte >> i) & 1;
        }
    }

//    std::cout << counter << std::endl;

//    for(int i = 0; i < arrHeight; ++i) {
//        for(int j = 0; j < arrWidth; ++j) {
//            std::cout << arr[i][j] << " ";
//        }
//        std::cout << "\n";
//    }
    return arr;
}

std::vector<uint16_t> *pbmToMatrix(std::istream &input, const unsigned int &width, const unsigned int &height) {
    unsigned int arrWidth = width % 8 == 0 ? width : width + 8 - (width % 8);
    unsigned int arrHeight = height % 4 == 0 ? height : height + 4 - (height % 4);

    bool **bitToByte = bitToByteArr(input, width, arrWidth, arrHeight);

    std::vector<uint16_t> *ret = new std::vector<uint16_t>;
    int i, j;
    for (i = 3; i < arrHeight; i += 4) {
        for (j = 3; j < arrWidth; j += 4) {
            uint16_t matrix = 0;

            //number of bits to shift for the 4x4 matrix
            int counter = 15;
            for (int y = i; y >= i - 3; --y) {
                for (int x = j; x >= j - 3; --x) {
                    matrix |= ((uint16_t) bitToByte[y][x] << counter--);
                }
            }
            ret->push_back(matrix);
        }
    }

    return ret;
}

std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>> readPBM(const std::string FileName) {
    std::ifstream input(FileName);
    std::string currLine;

    unsigned int width;
    unsigned int height;
    std::vector<uint16_t> *values;

    if (input.is_open()) {
        //from https://stackoverflow.com/questions/132358/how-to-read-file-content-into-istringstream
//        input.seekg(0,std::ios::end);
//        std::streampos len = input.tellg();
//        input.seekg(0,std::ios::beg);
//
//        std::vector<char> buffer(len);
//        input.read(&buffer[0],len);
//
//        std::stringstream localStream;
//        localStream.rdbuf()->pubsetbuf(&buffer[0],len);
//        input.close(); //close file stream
//
//        //skip first line

//        std::cout << currLine << std::endl;
        getline(input, currLine);
        assert(currLine == "P4");

        //skip all the comments if any
        getline(input, currLine);
        while (currLine[0] == '#') {
            getline(input, currLine);
        }

        std::regex delim("\\s+");
        std::vector<std::string> dimensions{
                std::sregex_token_iterator(currLine.begin(), currLine.end(), delim, -1), {}
        };

        width = std::stoi(dimensions[0]);
        height = std::stoi(dimensions[1]);

        assert(width > 0);
        assert(height > 0);

        std::stringstream newStream;
        newStream << input.rdbuf();
        input.close();

        values = pbmToMatrix(newStream, width, height);
        return std::make_tuple(width, height, values);

    }

    return {};
}
