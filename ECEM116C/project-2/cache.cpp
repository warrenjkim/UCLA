#include "cache.h"

Cache::Cache() {
  for (int i = 0; i < L1_CACHE_SETS; i++)
    l1[i].valid = false;

  for (int i = 0; i < L2_CACHE_SETS; i++)
    for (int j = 0; j < L2_CACHE_WAYS; j++)
      l2[i][j].valid = false;

  // Do the same for Victim Cache ...

  this->stat.l1_miss = 0;
  this->stat.l2_miss = 0;

  this->stat.l1_access = 0;
  this->stat.l2_access = 0;

  this->stat.vic_miss = 0;
  this->stat.vic_access = 0;

  // Add stat for Victim cache ...
}

void Cache::controller(bool load, bool store, int *data, int addr, int *memory) {
  std::cout << "(load: " << load <<
    ", store: " << store << 
    ", data: " << *data << 
    ", addr: " << addr << ")" << std::endl;

  size_t block_offset = std::bitset<OFFSET_SIZE>((OFFSET_MASK & addr)).to_ulong();
  size_t index = std::bitset<INDEX_SIZE>((INDEX_MASK & addr) >> INDEX_SHIFT).to_ulong();
  int tag = std::bitset<TAG_SIZE>(addr >> TAG_SHIFT).to_ulong();


  if (load) {
    std::cout << "load: "
      "addr (int): " << addr << 
      ", (block offset): " << block_offset <<
      ", (index): " << index <<
      ", (tag): " << tag << std::endl;

    if (this->l1[index].tag == tag && this->l1[index].valid)
      std::cout << "found in L1 (" << index << "): " << (int) this->l1[index].byte[block_offset] << std::endl;
    else
      std::cout << "need to go to mem" << std::endl;
  }

  if (store) {
    std::cout << "store: " << 
      "addr (int): " << addr << 
      ", (block offset): " << block_offset <<
      ", (index): " << index <<
      ", (tag): " << tag << std::endl;

    this->l1[index].tag = tag;
    this->l1[index].byte[block_offset] = *data;
    this->l1[index].valid = 1;
    this->l1[index].lru_position = 1;
  }

  std::cout << std::endl;
}
