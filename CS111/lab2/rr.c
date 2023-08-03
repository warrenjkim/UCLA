#include <errno.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/queue.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>

typedef uint32_t u32;
typedef int32_t i32;

struct process
{
  u32 pid;
  u32 arrival_time;
  u32 burst_time;

  TAILQ_ENTRY(process) pointers;

  /* You probably need to add something here */

  u32 time_left;
  bool ready;
};

TAILQ_HEAD(process_list, process);

u32 next_int(const char **data, const char *data_end)
{
  u32 current = 0;
  bool started = false;
  while (*data != data_end)
  {
    char c = **data;

    if (c < 0x30 || c > 0x39)
    {
      if (started)
      {
        return current;
      }
    }
    else
    {
      if (!started)
      {
        current = (c - 0x30);
        started = true;
      }
      else
      {
        current *= 10;
        current += (c - 0x30);
      }
    }

    ++(*data);
  }

  printf("Reached end of file while looking for another integer\n");
  exit(EINVAL);
}

u32 next_int_from_c_str(const char *data)
{
  char c;
  u32 i = 0;
  u32 current = 0;
  bool started = false;
  while ((c = data[i++]))
  {
    if (c < 0x30 || c > 0x39)
    {
      exit(EINVAL);
    }
    if (!started)
    {
      current = (c - 0x30);
      started = true;
    }
    else
    {
      current *= 10;
      current += (c - 0x30);
    }
  }
  return current;
}

void init_processes(const char *path,
                    struct process **process_data,
                    u32 *process_size)
{
  int fd = open(path, O_RDONLY);
  if (fd == -1)
  {
    int err = errno;
    perror("open");
    exit(err);
  }

  struct stat st;
  if (fstat(fd, &st) == -1)
  {
    int err = errno;
    perror("stat");
    exit(err);
  }

  u32 size = st.st_size;
  const char *data_start = mmap(NULL, size, PROT_READ, MAP_PRIVATE, fd, 0);
  if (data_start == MAP_FAILED)
  {
    int err = errno;
    perror("mmap");
    exit(err);
  }

  const char *data_end = data_start + size;
  const char *data = data_start;

  *process_size = next_int(&data, data_end);

  *process_data = calloc(sizeof(struct process), *process_size);
  if (*process_data == NULL)
  {
    int err = errno;
    perror("calloc");
    exit(err);
  }

  for (u32 i = 0; i < *process_size; ++i)
  {
    (*process_data)[i].pid = next_int(&data, data_end);
    (*process_data)[i].arrival_time = next_int(&data, data_end);
    (*process_data)[i].burst_time = next_int(&data, data_end);

    (*process_data)[i].time_left = (*process_data)[i].burst_time; 
    (*process_data)[i].ready = true;
  }

  munmap((void *)data, size);
  close(fd);
}

int main(int argc, char *argv[])
{
  if (argc != 3)
  {
    return EINVAL;
  }
  struct process *data;
  u32 size;
  init_processes(argv[1], &data, &size);

  u32 quantum_length = next_int_from_c_str(argv[2]);

  struct process_list list;
  TAILQ_INIT(&list);

  u32 total_waiting_time = 0;
  u32 total_response_time = 0;

  /* You probably need to add something here */

  u32 clock = 0;
  u32 quantum = quantum_length;
  u32 list_size = 0;

  for (u32 i = 0; i < size; i++) {                                     // traverse the list for arrival time of 0
    if ((&data[i])->arrival_time == clock) {                           // process arrive
      TAILQ_INSERT_TAIL(&list, &data[i], pointers);                    // put process in queue
    }
  }

  while (true) {
    if (TAILQ_EMPTY(&list)) {                                            // if empty, just keep running the clock
      clock++;

      for (u32 i = 0; i < size; i++) {                                     // traverse the list and see which processes arrive
	if ((&data[i])->arrival_time == clock) {                           // process arrives
	  TAILQ_INSERT_TAIL(&list, &data[i], pointers);                    // put process in queue
	}
      }

      continue;
    }

    struct process *curr = TAILQ_FIRST(&list);                           // take the head of queue

    curr->time_left--;                                                   // decrement time left (simulates process running for one time unit)
    quantum--;                                                           // decrement quantum


    if (curr->ready) {                                                   // purpose: set to running and add response time only once
      curr->ready = false;                                               // add response time
      total_response_time += clock - curr->arrival_time;
    }

    clock++;
    
    for (u32 i = 0; i < size; i++) {                                     // traverse the list and see which processes arrive
      if ((&data[i])->arrival_time == clock) {                           // process arrives
	TAILQ_INSERT_TAIL(&list, &data[i], pointers);                    // put process in queue
      }
    }

    if (quantum == 0) {                                                  // end of process' quantum
      quantum = quantum_length;                                          // reset quantum

      TAILQ_REMOVE(&list, curr, pointers);                               // remove from queue
      TAILQ_INSERT_TAIL(&list, curr, pointers);                          // add to back of queue
    }


    if (curr->time_left == 0) {                                          // process is done
      quantum = quantum_length;
      u32 turnaround = clock - curr->arrival_time;                       // record turnaround time (total time - arrival time)
      total_waiting_time += turnaround - curr->burst_time;               // add process' wait time

      TAILQ_REMOVE(&list, curr, pointers);
      list_size++;
    }


    if (list_size == size)
      break;

  }



  printf("Average waiting time: %.2f\n", (float)total_waiting_time / (float)size);
  printf("Average response time: %.2f\n", (float)total_response_time / (float)size);

  free(data);
  return 0;
}
