#define CATCH_CONFIG_RUNNER

#include "Header.h"
#include "catch.hpp"

TEST_CASE("testbitToByteArrayEven") {
    int height = 4, width = 8;
    bool **expected = new bool *[height];

    //initialize expected
    for (int i = 0; i < height; ++i) {
        expected[i] = new bool[width];
    }

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            expected[i][j] = false;
        }
    }

    expected[0][1] = true;
    expected[1][2] = expected[1][3] = true;
    expected[2][1] = true;
    expected[3][2] = expected[3][3] = true;


    std::istringstream input("@0@0");
    bool **actual = bitToByteArr(input, width, width, height);

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            REQUIRE(expected[i][j] == actual[i][j]);
        }
    }
}

TEST_CASE("testbitToByteArrOdd") {
    int width = 8, height = 4;
    bool **expected = new bool *[height];

    //initialize expected
    for (int i = 0; i < height; ++i) {
        expected[i] = new bool[width];
    }

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            expected[i][j] = false;
        }
    }

    expected[0][1] = true;
    expected[1][2] = expected[1][3] = true;
    expected[2][1] = true;

    std::istringstream input("@0@");
    bool **actual = bitToByteArr(input, 7, width, height);

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            REQUIRE(expected[i][j] == actual[i][j]);
        }
    }

}

TEST_CASE("pbmToMatrix") {
    std::vector<uint16_t> expected;
    expected.push_back(49858);
    expected.push_back(0);

    std::istringstream input("@0@0");
    std::vector<uint16_t> *actual = pbmToMatrix(input, 8, 4);

    REQUIRE(expected.size() == (*actual).size());
    REQUIRE(expected == (*actual));
}

TEST_CASE("pbmToMatrix2") {
    std::vector<uint16_t> expected;
    expected.push_back(706);
    expected.push_back(0);

    std::istringstream input("@0@");
    std::vector<uint16_t> *actual = pbmToMatrix(input, 7, 3);

    REQUIRE(expected.size() == (*actual).size());
    REQUIRE(expected == (*actual));
}

TEST_CASE("testReadPBMFileNotFound") {
    std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>> tuple = readPBM(
            "testFiles/!QAZ@WSX#EDC.pbm");
    REQUIRE(!tuple.has_value());
}

TEST_CASE("testReadPBMDimensions") {
    auto tuple = readPBM("testFiles/small1.pbm").value();
    REQUIRE(std::get<0>(tuple) == 9);
    REQUIRE(std::get<1>(tuple) == 9);
}

TEST_CASE("testReadPBMDimensions2") {
    auto tuple = readPBM("testFiles/example.pbm").value();
    REQUIRE(std::get<0>(tuple) == 8);
    REQUIRE(std::get<1>(tuple) == 4);
}

TEST_CASE("testReadPBMDimensions3") {
    auto tuple = readPBM("testFiles/x-0007.pbm").value();
    REQUIRE(std::get<0>(tuple) == 3307);
    REQUIRE(std::get<1>(tuple) == 4677);
}

TEST_CASE("testREADPBMVector") {
    auto tuple = readPBM("testFiles/example.pbm").value();
    std::vector<uint16_t> expected;
    expected.push_back(49858);
    expected.push_back(0);

    std::istringstream input("@0@0");
    std::vector<uint16_t> *actual = std::get<2>(tuple);

    REQUIRE(std::get<0>(tuple) == 8);
    REQUIRE(std::get<1>(tuple) == 4);

    REQUIRE(expected.size() == (*actual).size());
    REQUIRE(expected == (*actual));
}

std::ifstream::pos_type filesize(std::string filename) {
    std::ifstream in(filename, std::ifstream::ate | std::ifstream::binary);
    return in.tellg();
}

TEST_CASE("Tuple is correctly computed") {
    
    std::tuple<unsigned, unsigned, std::vector<uint16_t> *> tuple2;
    auto test = readCompressed("testFiles/x-0000Output.pbm");
    tuple2 = test.value();
    unsigned width = std::get<0> (tuple2);
    unsigned height = std::get<1> (tuple2);
    std::vector<uint16_t> *uin = std::get<2> (tuple2);
    
    REQUIRE( width == 3307 );
    REQUIRE( height == 4677 );
    REQUIRE( uin->size() == 968760 );
    
}

TEST_CASE("Making the hash") {
    std::map<std::string, uint16_t> hash = makeHash();
    uint16_t firstIndex = 0;
    REQUIRE(hash["1"] == firstIndex);
    REQUIRE(hash["0110010"] == 1);
    REQUIRE(hash["00010100010"] == 2);
    REQUIRE(hash["00110011"] == 3);
    REQUIRE(hash["00010100011"] == 4);
    REQUIRE(hash["01111000000011100110000001001101111000"] == 5);
    REQUIRE(hash["0100001101"] == 6);
    REQUIRE(hash["0111111101"] == 7);
    REQUIRE(hash["00101"] == 65535);
}

int main(int argc, const char *argv[]) {
    int result = Catch::Session().run(argc, argv);
    return result;
//    if(argc == 2) {
//        if(strcmp(argv[1], "-t") == 0) {
//            std::cout <<"here" << std::endl;
//            int result = Catch::Session().run(argc, argv);
//            return result;
//        }else if(strcmp(argv[1], "-p") == 0) {
//            for(int i = 0; i < 24; ++i) {
//                std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>> preTup;
////        std::cout << "Reading file..." << std::endl;
//                std::string fileName = "testFiles/x-00" + (i < 10 ? "0" + std::to_string(i) : std::to_string(i)) + ".pbm";
//                preTup = readPBM(fileName);
//                auto tuple = preTup.value();
//
////        std::cout << "Compressing..." << std::endl;
//                if (compress(*std::get<2>(tuple), std::get<0>(tuple), std::get<1>(tuple), "pbmOutput")) {
//                    auto test = readCompressed("pbmOutput");
//                    std::tuple<unsigned, unsigned, std::vector<uint16_t> *> tuple2 = test.value();
//                    unsigned wid = std::get<0> (tuple2);
//                    unsigned height = std::get<1> (tuple2);
//                    std::vector<uint16_t> *uin = std::get<2> (tuple2);
////            std::cout << wid << std::endl;
////            std::cout << height << std::endl;
////            std::cout << uin->size() << std::endl;
////            std::cout << "Making output..." << std::endl;
//                    writePBM(*uin, wid, height, "finalOutput.pbm");
//                } else {
//                    std::cout << "compression failed" << std::endl;
//                }
//
//                double original = filesize(fileName)/(double)1000000;
//                double compressed = filesize("pbmOutput")/(double)1000000;
//                std::cout << "x-00" + (i < 10 ? "0" + std::to_string(i) : std::to_string(i)) + "\t\t\t" << std::setprecision(3) << original << "MB\t\t" << compressed << "KB\t\t" << original/compressed << "\n";
//            }
//        }else {
//            std::optional<std::tuple<unsigned, unsigned, std::vector<uint16_t> *>> preTup;
//            std::cout << "Reading file..." << std::endl;
//            preTup = readPBM(argv[1]);
//            auto tuple = preTup.value();
//
//            std::cout << "Compressing..." << std::endl;
//            if (compress(*std::get<2>(tuple), std::get<0>(tuple), std::get<1>(tuple), "pbmOutput")) {
//                auto test = readCompressed("pbmOutput");
//                std::tuple<unsigned, unsigned, std::vector<uint16_t> *> tuple2 = test.value();
//                unsigned wid = std::get<0>(tuple2);
//                unsigned height = std::get<1>(tuple2);
//                std::vector<uint16_t> *uin = std::get<2>(tuple2);
//                std::cout << wid << std::endl;
//                std::cout << height << std::endl;
//                std::cout << uin->size() << std::endl;
//                std::cout << "Making output..." << std::endl;
//                writePBM(*uin, wid, height, "finalOutput.pbm");
//            } else {
//                std::cout << "compression failed" << std::endl;
//            }
//        }
//    }else {
//        std::cout << "No file name given" << std::endl;
//    }
//    return 0;
}
