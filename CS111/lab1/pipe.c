#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>

static void error_and_exit(const char * type) {
  char reason[11];
  sprintf(reason, "%s failed", type);
  perror(reason);
  exit(EXIT_FAILURE);
}

int main(int argc, char *argv[]) {
  if (argc < 2) {                             // check for valid argc
    perror("invalid arguments");
    exit(EINVAL);
  }
  
  
  if (argc == 2) {                            // only one command <==> no pipe needed => execute command
    execlp(argv[1], argv[1], (char *) NULL);
    exit(EXIT_FAILURE);
  }

  int   num_pipes = argc - 2;   // number of pipes needed:      argc - 2
  int   num_pids  = argc - 1;   // number of child pids needed: argc - 1

  int   pipefds[num_pipes][2]; // [i][0]: read, [i][1]: write
  pid_t child_pids[num_pids];  // child pids for wait
  int   wstatuses[num_pids];   // wait statuses for child i
  

  for (int i = 0; i < num_pids; i++) {                      // loop for each process
    if (pipe(pipefds[i]) < 0)                               // check for pipe failure
      error_and_exit("pipe");

    child_pids[i] = fork();                                 // fork process

    if (child_pids[i] < 0)                                  // check for fork failure
      error_and_exit("fork");

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

	if (i == num_pipes) {
	  execlp(argv[i + 1], argv[i + 1], (char *) NULL);  // execute command
	  error_and_exit("execlp");                         // execlp failed
	}
	close(pipefds[i][0]);                               // close unused read end of pipe
	dup2(pipefds[i][1], STDOUT_FILENO);                 // reroute stdout to write end of pipe
	close(pipefds[i][1]);                               // close old write end of pipe
      }

      execlp(argv[i + 1], argv[i + 1], (char *) NULL);      // execute command
      error_and_exit("execlp");                             // check for execlp failure
    }
    else
      close(pipefds[i][1]);                                 // close write end of pipe 
  }
    
  for (int i = 0; i < num_pids; i++) {                                     // for each child pid
    waitpid(child_pids[i], &wstatuses[i], 0);                              // wait
    if (WIFEXITED(wstatuses[i]) || WIFSIGNALED(wstatuses[i])) {            // check for 0 wait status
      if (WEXITSTATUS(wstatuses[i]) != 0) {                                // error exit code
	char error_code[15];                                               // error code string
	sprintf(error_code, "error code: %d", WEXITSTATUS(wstatuses[i]));  // stringify exit message
	perror(error_code);                                                // error
	exit(WEXITSTATUS(wstatuses[i]));                                   // exit with error
      }
    }
  }

  return 0;
}
