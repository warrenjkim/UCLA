#include "cache.h"

Cache::Cache() {
  for (int i = 0; i < L1_CACHE_SETS; i++)
    l1[i].valid = false;

  for (int i = 0; i < L2_CACHE_SETS; i++)
    for (int j = 0; j < L2_CACHE_WAYS; j++)
      l2[i][j].valid = false;

  // Do the same for Victim Cache ...

  this->stat.l1_miss = 0;
  this->stat.l1_hit = 0;

  this->stat.l2_miss = 0;
  this->stat.l2_hit = 0;

  this->stat.vic_miss = 0;
  this->stat.vic_hit = 0;

  // Add stat for Victim cache ...
}

void Cache::controller(bool load, bool store, int *data, int addr,
                       int *memory) {
  // std::cout << "(load: " << load << ", store: " << store << ", data: " << *data
  //           << ", addr: " << addr << ")" << std::endl;
  if (load)
    lw(addr, memory);

  if (store)
    sw(data, addr, memory);
  // std::cout << std::endl;
}

void Cache::update_lru(CacheBlock *arr, CacheLevel level, int position) {
  size_t size = 0;

  if (level == VIC) {
    size = VICTIM_SIZE;
  } else if (level == L2)
    size = L2_CACHE_WAYS;

  for (size_t i = 0; i < size; i++) {
    if (arr[i].valid) {
      if (arr[i].lru_position < position)
        continue;

      if (arr[i].lru_position == 0)
        arr[i].valid = false;
      else
        arr[i].lru_position--;
    }
  }
}

CacheBlock *Cache::min_block(CacheLevel level, int index) {
  CacheBlock *min;
  switch (level) {
  case L1:
    return NULL;
  case VIC:
    min = this->victim;

    for (auto &entry : this->victim)
      if (!entry.valid)
        min = &entry;

    for (auto &entry : this->victim)
      if (entry.lru_position < min->lru_position && entry.valid)
        min = &entry;

    return min;
  case L2:
    min = this->l2[index];

    for (auto &entry : this->l2[index])
      if (!entry.valid)
        min = &entry;

    for (auto &entry : this->l2[index])
      if (entry.lru_position < min->lru_position && entry.valid)
        min = &entry;

    return min;
  default:
    return NULL;
  }
}

void Cache::evict(const CacheBlock &block, CacheLevel level) {
  if (level == L2) {
    CacheBlock *min = min_block(L2, block.index);
    *min = block;
    min->lru_position = L2_CACHE_WAYS;
    update_lru(this->l2[block.index], L2);
    min->tag = block.addr >> 6;
    return;
  }

  if (level == VIC) {
    CacheBlock *min = min_block(VIC);
    CacheBlock to_evict = *min;

    // update victim cache with l1
    *min = block;
    min->lru_position = VICTIM_SIZE;
    update_lru(this->victim, VIC);
    min->tag = block.addr;

    // evict
    if (to_evict.valid)
      evict(to_evict, L2);
  }
}

void Cache::lw(int addr, int *memory) {
  size_t block_offset = 0;
  int index = 0;
  int tag = 0;

  bool l1_hit = false;
  bool vic_hit = false;
  bool l2_hit = false;

  block_offset = 0x3 & addr;
  index = (addr >> 2) & 0xF;
  tag = addr >> 6;
  // std::cout << addr << ": L1: (offset: " << block_offset << ", index: " << index
  //           << ", tag: " << tag << ")" << std::endl;

  // std::cout << "l1:\n";
  // for (const auto &x : this->l1) {
  //   for (int i = 0; i < 4; i++)
  //     std::cout << (x.valid ? (int)x.byte[i] : -1) << " ";
  //   std::cout << std::endl;
  // }

  if (this->l1[index].index == index && this->l1[index].tag == tag &&
      this->l1[index].valid) {
     // std::cout << "HIT: found in l1" << std::endl;
    l1_hit = true;
  }

  if (!l1_hit) {
    tag = addr;
     // std::cout << "VIC: (offset: " << block_offset << ", index: " << index
     //           << ", tag: " << tag << ")" << std::endl;
     // std::cout << "vic: ";
    for (auto &entry : this->victim) {
       // std::cout << "(";
       // for (int i = 0; i < 4; i++) {
       //   std::cout << "[" << (entry.valid ? entry.byte[i] : -1) << ","
       //             << entry.lru_position << "," << entry.tag << "] ";
       // }
       // std::cout << ")\n";
      if (entry.tag == tag && entry.valid) {
         // std::cout << "HIT: found in victim" << std::endl;
        CacheBlock block = this->l1[index];

         // update l1
        this->l1[index] = entry;
        this->l1[index].tag = addr >> 6;

         // evict data from l1 into victim
        if (block.valid)
          evict(block, VIC);

        entry.valid = false;
        entry.lru_position = 0;

        vic_hit = true;
      }
    }
     // std::cout << std::endl;
  }

  if (!l1_hit && !vic_hit) {
    tag = addr >> 6;
//      std::cout << "L2: (offset: " << block_offset << ", index: " << index
//                << ", tag: " << tag << ")" << std::endl;
// 
     // std::cout << "l2:\n";
     // for (auto &x : this->l2) {
     //     for (auto &y : x) {
     //         std::cout << "(";
     //         for (int i = 0; i < 4; i++)
     //             std::cout << (y.valid ? y.byte[i] : -1) << " ";
     //         std::cout << ")";
     //     }
     //     std::cout << std::endl;
     // }
    for (auto &entry : this->l2[index]) {
      if (entry.index == index && entry.tag == tag && entry.valid) {
         // std::cout << "HIT: found in l2 " << std::endl;
        CacheBlock block = this->l1[index];

         // update l1
        this->l1[index] = entry;

         // evict data from l1 -> victim -> l2
        if (block.valid)
          evict(block, VIC);

         // remove entry
        entry.valid = false;

        l2_hit = true;
        break;
      }
    }
  }

  if (!l1_hit && !vic_hit && !l2_hit) {
     // std::cout << "MISS: going to memory" << std::endl;

    CacheBlock block = this->l1[index];

    for (size_t i = 0; i < BLOCK_SIZE; i++)
      this->l1[index].byte[i] = memory[addr - block_offset + i];

    this->l1[index].tag = tag;
    this->l1[index].valid = true;
    this->l1[index].index = index;
    this->l1[index].addr = addr;

    if (block.valid)
      evict(block, VIC);
  }

  stat.l1_miss += !l1_hit;
  stat.l1_hit += l1_hit;

  stat.vic_miss += !l1_hit && !vic_hit;
  stat.vic_hit += vic_hit;

  stat.l2_miss += !l1_hit && !vic_hit && !l2_hit;
  stat.l2_hit += l2_hit;
}

void Cache::sw(int *data, int addr, int *memory) {
  size_t block_offset = 0;
  size_t index = 0;
  int tag = 0;

  bool l1_hit = false;
  bool vic_hit = false;

  block_offset = 0x3 & addr;
  index = (addr >> 2) & 0xF;
  tag = addr >> 6;
   // std::cout << "(offset: " << block_offset <<
   //     ", index: " << index <<
   //     ", tag: " << tag << ")" << std::endl;
   // check l1
  if (this->l1[index].tag == tag && this->l1[index].valid) {
     // std::cout << "found in l1, updating" << std::endl;

    this->l1[index].byte[block_offset] = *data;
    this->l1[index].valid = true;

    l1_hit = true;
  }

  tag = addr;
   // check victim
  if (!l1_hit) {
    for (auto &entry : this->victim) {
      if (entry.tag == tag && entry.valid) {
         // std::cout << "found in victim, updating" << std::endl;

        entry.byte[block_offset] = *data;
        entry.valid = true;

        size_t position = entry.lru_position;
        entry.lru_position = VICTIM_SIZE;
        update_lru(this->victim, VIC, position);

        vic_hit = true;
        break;
      }
    }
  }

  tag = addr >> 6;
   // check l2
  if (!l1_hit && !vic_hit) {
    for (auto &entry : this->l2[index]) {
      if (entry.tag == tag && entry.valid) {
         // std::cout << "found in l2, updating" << std::endl;

        entry.byte[block_offset] = *data;
        entry.valid = true;

        size_t position = entry.lru_position;
        entry.lru_position = VICTIM_SIZE;
        update_lru(this->l2[index], L2, position);
        break;
      }
    }
  }

  memory[addr] = *data;
   // std::cout << "updated memory" << std::endl;
}

double Cache::l1_miss() {
    // std::cout << stat.l1_miss << ", " << stat.l1_hit << std::endl;
  if (stat.l1_miss == 0 && stat.l1_hit == 0)
    return (double)(0);
  return (double)(stat.l1_miss) / (stat.l1_miss + stat.l1_hit);
}

double Cache::vic_miss() {
    // std::cout << stat.vic_miss << ", " << stat.vic_hit << std::endl;
  if (stat.vic_miss == 0 && stat.vic_hit == 0)
    return (double)(0);
  return (double)(stat.vic_miss) / (stat.vic_miss + stat.vic_hit);
}

double Cache::l2_miss() {
    // std::cout << stat.l2_miss << ", " << stat.l2_hit << std::endl;
  if (stat.l2_miss == 0 && stat.l2_hit == 0)
    return (double)(0);

  return (double)(stat.l2_miss) / (stat.l2_miss + stat.l2_hit);
}
