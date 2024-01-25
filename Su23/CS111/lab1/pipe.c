#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>


int main(int argc, char *argv[]) {
  if (argc < 2)                               // check for valid argc
    exit(EINVAL);
  
  
  if (argc == 2) {                            // only one command <==> no pipe needed => execute command
    execlp(argv[1], argv[1], (char *) NULL);
    exit(errno);
  }

  int   num_pipes = argc - 2;  // number of pipes needed:      argc - 2
  int   num_pids  = argc - 1;  // number of child pids needed: argc - 1

  int   parent    = 0;         // determine if we are the parent for clean up
  
  int   pipefds[num_pipes][2]; // [i][0]: read, [i][1]: write
  pid_t child_pids[num_pids];  // child pids for wait
  int   wstatuses[num_pids];   // wait statuses for child i

  for (int i = 0; i < num_pids; i++) {                      // loop for each process
    if (pipe(pipefds[i]) < 0)                               // check for pipe failure
      exit(errno);

    child_pids[i] = fork();                                 // fork process

    if (child_pids[i] < 0)                                  // check for fork failure
      exit(errno);                   

    if (child_pids[i] == 0) {                               // child process => route pipes and execute commands
      if (i == 0) {                                         // first command
	close(pipefds[i][0]);                               // close unused read end of pipe
	dup2(pipefds[i][1], STDOUT_FILENO);                 // reroute stdout to write end of pipe
	close(pipefds[0][1]);                               // close old write end of pipe
      }
      else {
	close(pipefds[i - 1][1]);                           // close write end of previous pipe
	dup2(pipefds[i - 1][0], STDIN_FILENO);              // reroute stdin to read end of previous pipe
	close(pipefds[i - 1][0]);                           // close old read end of previous pipe

	close(pipefds[i][0]);

	if (i == num_pipes) {
	  close(pipefds[i][1]);
	  execlp(argv[i + 1], argv[i + 1], (char *) NULL);  // execute command
	  exit(errno);                                      // execlp failed
	}

	dup2(pipefds[i][1], STDOUT_FILENO);                 // reroute stdout to write end of pipe
	close(pipefds[i][1]);                               // close old write end of pipe
      }

      execlp(argv[i + 1], argv[i + 1], (char *) NULL);      // execute command
      exit(errno);                                          // execlp failed
    }
    else {
      close(pipefds[i][1]);                                 // close write end of pipe
      parent = 1;                                           // we are the parent
    }
  }

  if (parent) {
    for (int i = 0; i < num_pids; i++) {
      close(pipefds[i][0]);
      pid_t w;

      w = waitpid(child_pids[i], &wstatuses[i], 0);                          // wait
      if (w < 0)
	exit(errno);
      if (WIFEXITED(wstatuses[i]) || WIFSIGNALED(wstatuses[i]))              // check for 0 wait status
	if (WEXITSTATUS(wstatuses[i]) != 0)                                  // error exit code
	  exit(WEXITSTATUS(wstatuses[i]));                                   // exit with error

    }
  }
    
  return 0;
}
