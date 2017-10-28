/* CSCI 347 micro-make - target
 * Jacob Peterson
 * 10/18/2017
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <string.h>
#include "arg_parse.h"
#include "target.h"


/*PROTOTYPES*/
void target_parse(char* line);
void removeColon(char* line);

/*GLOBALS*/
tnode_t* tList;
rnode_t* firstRNode;
rnode_t* currRNode;


/* Target Parse
 * Parsing the target and depency line of a makefile
 * line - A target line that needs to be parsed
 * Puts Targets and dependencies into a linked list
 */
void target_parse(char* line){
  firstRNode = malloc(sizeof(rnode_t*));
  currRNode = malloc(sizeof(rnode_t*));
  removeColon(line);
  int numArgs;
  char** targLine = arg_parse(line, &numArgs);

  tnode_t* newNode = malloc(sizeof(tnode_t*));
  newNode -> target = (char*) malloc(strlen(targLine[0]));
  strcpy(newNode -> target, targLine[0]);

  newNode -> dependencies = (char**) malloc((numArgs - 1) * sizeof(char*));
  for(int i = 1; i < numArgs; i++){
    newNode -> dependencies[i-1] = (char*) malloc(sizeof(char*));
    strcpy(newNode -> dependencies[i-1], targLine[i]);
  }
  free(targLine);
  newNode -> next = tList;
  tList = newNode;
}

/* Command Parse
 * Current target is always at the front of the list
 */
void command_parse(char* line){
  rnode_t* newRNode = malloc(sizeof(rnode_t*));
  newRNode -> rule = (char*) malloc(100);
  strcpy(newRNode -> rule, line);
  currRNode -> next = newRNode;
  currRNode = newRNode;
  if(firstRNode -> rule == '\0'){
    firstRNode = newRNode;
    tList -> ruleList = newRNode;
  }
}

/* Find Commands
 * target - the target that is being searched for
 * Traverse through the linked list until you find the target
 * If the target is found, return its rules. If not return null
 */
rnode_t* findCommands(char* target){
  tnode_t* currTNode = tList;
  while(currTNode != '\0'){
    if(0 == strcmp(currTNode -> target, target)){
      return currTNode -> ruleList;
    }
    currTNode = currTNode -> next;
  }
  return NULL;
}

void removeColon(char* line){
  int i = 0;
  while(line[i] != '\0'){
    if(line[i] == ':')
      line[i] = ' ';
    i++;
  }
}

//I tried to free the linked lists but couldnt figure it out
/* Free Target
 * Free up the linked list and all of its contents
 */
void freeTarget(){
  /*tnode_t* currTNode;
  rnode_t* currRNode;
  rnode_t* firstRNode;
  while(tList != NULL){
    currTNode = tList;
    tList = tList -> next;
    free(currTNode -> target);
    currRNode = currTNode -> ruleList;
    firstRNode = currTNode -> ruleList;
    while(firstRNode != NULL){
      currRNode = firstRNode;
      firstRNode = firstRNode -> next;
      free(currRNode -> rule);
      free(currRNode);
    }
    free(currTNode -> dependencies);
  }*/
}
