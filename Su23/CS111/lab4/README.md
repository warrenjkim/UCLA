## UID: 305600694

(IMPORTANT: Only replace the above numbers with your true UID, do not modify spacing and newlines, otherwise your tarfile might not be created correctly)

# Hey! I'm Filing Here

This lab creates a MiB ext2 file system with 2 directories, 1 regular file, and 1 symbolic link.

## Building

To build, in the lab4 directory, do
``
make
``

## Running

To compile, do
``
make
``

Create the img by running
``
./ext2-create
``

Create a `mnt` directory by doing `mkdir mnt`.

Then mount the img by doing
``
sudo mount -o loop cs111-base.img mnt
``

An example output of `ls -ain` looks like:
``
total 7
     2 drwxr-xr-x 3    0    0 1024 Aug 24 13:43 .
942190 drwxr-xr-x 3 1000 1000 4096 Aug 24 13:43 ..
    13 lrw-r--r-- 1 1000 1000   11 Aug 24 13:43 hello -> hello-world
    12 -rw-r--r-- 1 1000 1000   12 Aug 24 13:43 hello-world
    11 drwxr-xr-x 2    0    0 1024 Aug 24 13:43 lost+found
``
	
## Cleaning up

To unmount the filesystem, in `mnt`'s parent directory, do 
``
sudo umount mnt
``
then 
``
rmdir mnt
``
to remove the mount directory.

To clean up all binary files, do
``
make clean
``
