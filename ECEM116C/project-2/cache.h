#include <bitset>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string>

#define L1_CACHE_SETS 16
#define L2_CACHE_SETS 16
#define VICTIM_SIZE 4
#define L2_CACHE_WAYS 8
#define MEM_SIZE 4096
#define BLOCK_SIZE 4 // bytes per block
#define DM 0
#define SA 1


#define OFFSET_MASK 3
#define INDEX_MASK 29

#define OFFSET_SIZE 2
#define INDEX_SIZE 4
#define TAG_SIZE 26

#define INDEX_SHIFT 2
#define TAG_SHIFT 6


typedef struct CacheBlock {
  int tag;
  int lru_position;
  union {
    int data;
    uint8_t byte[4];
  };
  bool valid;

  CacheBlock() : tag(0), lru_position(0), data(0), valid(false) {}
} CacheBlock;

typedef struct Stat {
  int l1_miss;
  int l1_access;

  int l2_miss;
  int l2_access;

  int vic_miss;
  int vic_access;
  // add more stat if needed. Don't forget to initialize!

  Stat()
      : l1_miss(0), l1_access(0), l2_miss(0), l2_access(0), vic_miss(0), vic_access(0) {}
} Stat;

class Cache {
private:
  CacheBlock l1[L1_CACHE_SETS];                // 1 set per row.
  CacheBlock l2[L2_CACHE_SETS][L2_CACHE_WAYS]; // x ways per row
  CacheBlock victim[VICTIM_SIZE];
  // Add your Victim cache here ...

  Stat stat;
  // add more things here
public:
  Cache();
  void controller(bool load, bool store, int *data, int addr, int *memory);
  // add more functions here ...
};
