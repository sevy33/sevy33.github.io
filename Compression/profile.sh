clang++ -std=c++17 -fprofile-instr-generate -fcoverage-mapping main.cpp readPBM.cpp write.cpp compress.cpp decompress.cpp huff-v1.cpp -o main
rm -f *.profdata
LLVM_PROFILE_FILE="main.profraw" ./main
xcrun llvm-profdata merge -sparse main.profraw -o main.profdata
xcrun llvm-cov show ./main -instr-profile=main.profdata > coverage.txt
