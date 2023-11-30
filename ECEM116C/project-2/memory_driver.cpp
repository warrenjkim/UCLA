#include "cache.h"
#include <cstring>
#include <fstream>
#include <iostream>
#include <sstream>
#include <vector>
#include <iomanip>

typedef struct Trace {
  bool load;
  bool store;
  int addr;
  int data;

  Trace() : load(false), store(false), addr(0), data(0) {}
} Trace;

/*
   Either implement your memory_controller here or use a separate .cpp/.c file
   for memory_controller and all the other functions inside it (e.g., LW, SW,
   Search, Evict, etc.)
   */

int main(int argc, char *argv[]) { // the program runs like this: ./program <filename>
                                   // <mode> input file (i.e., test.txt)
  std::string filename = argv[1];

  std::ifstream fin;

  // opening file
  fin.open(filename.c_str());
  if (!fin) { // making sure the file is correctly opened
    std::cout << "Error opening " << filename << std::endl;
    exit(1);
  }

  // reading the text file
  std::string line;
  std::vector<Trace> trace;
  int trace_size = 0;
  std::string s1, s2, s3, s4;

  while (getline(fin, line)) {
    std::stringstream ss(line);
    getline(ss, s1, ',');
    getline(ss, s2, ',');
    getline(ss, s3, ',');
    getline(ss, s4, ',');
    trace.push_back(Trace());
    trace[trace_size].load = stoi(s1);
    trace[trace_size].store = stoi(s2);
    trace[trace_size].addr = stoi(s3);
    trace[trace_size].data = stoi(s4);
    /* std::cout << "(" << trace[trace_size].load << ", " << trace[trace_size].store */
    /*           << ", " << trace[trace_size].addr << ", " */
    /*           << trace[trace_size].data << ")" << std::endl; */
    trace_size += 1;
  }

  // Defining cache and stat
  Cache cache;
  int memory[MEM_SIZE] = { 0 };

  int trace_counter = 0;
  bool load;
  bool store;
  int addr;
  int data;

  // this is the main loop of the code
  while (trace_counter < trace_size) {
    load = trace[trace_counter].load;
    store = trace[trace_counter].store;
    addr = trace[trace_counter].addr;
    data = trace[trace_counter].data;
    trace_counter += 1;
    cache.controller(load, store, &data, addr, memory); // in your memory controller you need to implement
                                                        // your FSM, LW, SW, and MM.
  }

  double l1_miss_rate, vic_miss_rate, l2_miss_rate, aat;
  // compute the stats here:
  
  l1_miss_rate = (double)(cache.l1_miss());
  vic_miss_rate = (double)(cache.vic_miss());
  l2_miss_rate = (double)(cache.l2_miss());

  aat = HT_L1 + (HT_VIC + (HT_L2 + (MP) * l2_miss_rate) * vic_miss_rate) * l1_miss_rate;

  std::cout << std::setprecision(10) << "(" << l1_miss_rate << "," << l2_miss_rate << "," << aat << ")"
            << std::endl;

  // closing the file
  fin.close();

  return 0;
}
