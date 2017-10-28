/* CSCI 347 micro-make
 * Jacob Peterson
 * 10/18/2017
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>
#include <stdbool.h>
#include <assert.h>
#include "arg_parse.h"
#include "target.h"
/* PROTOTYPES */

/* Process Line
 * line   The command line to execute.
 * This function interprets line as a command line.  It creates a new child
 * process to execute the line and waits for that process to complete.
 */
void processline(char* line);
void execute_commands(char* target);

/* Main entry point.
 * argc    A count of command-line arguments
 * argv    The command-line argument valu
 *
 * Micro-make (umake) reads from the uMakefile in the current working
 * directory.  The file is read one line at a time.  Lines with a leading tab
 * character ('\t') are interpreted as a command and passed to processline minus
 * the leading tab.
 */
int main(int argc, char* argv[]) {
  FILE* makefile = fopen("./uMakefile", "r");

  size_t  bufsize = 0;
  char*   line    = NULL;
  ssize_t linelen = getline(&line, &bufsize, makefile);

  while(-1 != linelen) {
    if(line[linelen-1]=='\n') {
      linelen -= 1;
      line[linelen] = '\0';
    }
    if(line[0] == '\t'){
      command_parse(&line[1]);

    }else if(line[0] != '\0'){
      target_parse(&line[0]);
    }
    linelen = getline(&line, &bufsize, makefile);
  }

  for(int i = 1; i < argc; i++){
    execute_commands(argv[i]);
  }
  freeTarget();
  free(line);
  return EXIT_SUCCESS;
}


/* Execute commands
 * target - the target to run commands for
 * When a target is found in the linked list, run the commands through processline
 */
void execute_commands(char* target){
  rnode_t* currRNode = findCommands(target);
  if(currRNode == NULL){
    printf("Couldn't find target: %s\n", target);
  }else{
    while(currRNode != '\0'){
      processline(currRNode -> rule);
      currRNode = currRNode -> next;
    }
  }
}


/* Process Line */
void processline (char* line) {
  int argcp;
  char** args = arg_parse(line, &argcp);
  if(argcp > 0){
    const pid_t cpid = fork();

    switch(cpid) {

      case -1: {
        perror("fork");
        free(args);
        break;
      }

      case 0: {
        execvp(args[0], args);
        perror("execvp");
        exit(EXIT_FAILURE);
        free(args);
        break;
      }

      default: {
        int   status;
        const pid_t pid = wait(&status);
        if(-1 == pid) {
          perror("wait");
        } else if (pid != cpid) {
          fprintf(stderr, "wait: expected process %d, but waited for process %d",
              cpid, pid);
        }
        free(args);
        break;
      }
    }
  }
}
