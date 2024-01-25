#include <linux/module.h>
#include <linux/printk.h>
#include <linux/proc_fs.h>
#include <linux/seq_file.h>
#include <linux/sched.h>

static struct proc_dir_entry *count;
static struct task_struct *task;

static int seq_show(struct seq_file *s, void *v)
{
  int curr_count = 0;                      // process count
  for_each_process(task) { curr_count++; } // increment count for each process
  seq_printf(s, "%d\n", curr_count);       // write to /proc/count
  return 0;
}

static int __init proc_count_init(void)
{
  pr_info("proc_count: init\n");                             // initialize module
  count = proc_create_single("count", 0644, NULL, seq_show); // create /proc/count
  return 0;
}

static void __exit proc_count_exit(void)
{
  proc_remove(count);            // remove /proc/count
  pr_info("proc_count: exit\n"); // clean up module
}

module_init(proc_count_init);
module_exit(proc_count_exit);

MODULE_AUTHOR("Warren Kim");
MODULE_DESCRIPTION("A kernel module to count the number of current processes.");
MODULE_LICENSE("GPL");
