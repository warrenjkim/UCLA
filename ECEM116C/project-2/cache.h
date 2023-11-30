#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string>

#include <cstdint>

#define L1_CACHE_SETS 16
#define L2_CACHE_SETS 16
#define L2_CACHE_WAYS 8
#define VICTIM_SIZE 4
#define MEM_SIZE 4096
#define BLOCK_SIZE 4 // bytes per block
#define DM 0
#define SA 1

#define HT_L1 1
#define HT_VIC 1
#define HT_L2 8
#define MP 100

#define OFFSET_MASK 3
#define INDEX_MASK 29

#define OFFSET_SIZE 2
#define INDEX_SIZE 4
#define TAG_SIZE 26

#define INDEX_SHIFT 2
#define TAG_SHIFT 6

typedef enum {
    L1,
    VIC,
    L2
} CacheLevel;

typedef union byte_addr {
    int memory = 0;
    uint8_t byte[4];
} byte_addr;

typedef struct CacheBlock {
  int tag;
  int index;
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
  int l1_hit;

  int l2_miss;
  int l2_hit;

  int vic_miss;
  int vic_hit;
  // add more stat if needed. Don't forget to initialize!

  Stat()
      : l1_miss(0), l1_hit(0), l2_miss(0), l2_hit(0), vic_miss(0), vic_hit(0) {}
} Stat;

class Cache {
private:
  CacheBlock l1[L1_CACHE_SETS];                // 1 set per row.
  CacheBlock l2[L2_CACHE_SETS][L2_CACHE_WAYS]; // x ways per row
  CacheBlock victim[VICTIM_SIZE];

  Stat stat;
public:
  Cache();
  void controller(bool load, bool store, int *data, int addr, int *memory);
  void lw(int addr, int *memory);
  void sw(int *data, int addr, int *memory);
  void evict(const CacheBlock &block, CacheLevel level);
  void update_lru(CacheBlock *arr, CacheLevel level, int position = -1);
  CacheBlock *min_block(CacheLevel level, int index = -1);

  double l1_miss();
  double vic_miss();
  double l2_miss();
};
