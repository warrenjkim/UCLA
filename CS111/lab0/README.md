## UID: 305600694

(IMPORTANT: Only replace the above numbers with your true UID, do not modify spacing and newlines, otherwise your tarfile might not be created correctly)

# A Kernel Seedling

A kernel module to count the number of current processes.

## Building

To build the kernel module, in the lab0 directory, do:
``
make
``

## Running

To add the kernel module, do:
``
sudo insmod proc_count.ko
``
and to view the output, do:
``
cat /proc/count
``
We expect the kernel module to output the number of current processes.

## Cleaning Up

To remove the kernel module, do:
``
sudo rmmod proc_count
``
and to clean up the files, in the lab0 directory, do:
``
make clean
``

## Testing

Using `uname -r`, we get `5.14.8-arch1-1`. Tested with `python -m unittest` 
and by using the build/run/cleanup steps from above.
