/* CSCI 347 arg_parse
 * Used by micro-make
 * Jacob Peterson
 * 10/17/2017
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>
#include "arg_parse.h"

/* PROTOTYPES */
static int counter(char* str);
static void addNullChar(char** currLine);

/* Arg Parse
 * line  The commancd line to parse.
 * Makes a pointer to the first char in each arg
 * Delimitted by null
 */
char** arg_parse(char* line, int* argcp){
  *argcp = counter(line);
  char** args = malloc ((*argcp + 1) * sizeof(char*));
  addNullChar(&line);
  int j = 0;
  int i = 0;
  bool prevNull = true;

  while(i < *argcp){
    if((line[j] != '\0') && (!isspace(line[j]))){
      if(prevNull){
        args[i] = line + j;
        i++;
      }
      prevNull = false;
    }else{
      prevNull = true;
    }
    j++;
  }
  args[i] = NULL;
  return args;
}

/* Counter
 * Counts number of arguments in the line
 * Skip spaces only count the first character after a space
 */
static int counter(char* str){
  int count = 0;
  int i = 0;
  bool afterSpace = true;
  while(str[i] != '\0'){
    if(!isspace(str[i])){
      if(afterSpace){
        count++;
      }
      afterSpace = false;
    }else{
      afterSpace = true;
    }
    i++;
  }
  return count;
}

//Delete spaces and add in null char between each arg
static void addNullChar(char** currLine){
  int j = 0;
  bool afterSpace = true;
  while(currLine[0][j] != '\0'){
    if(!isspace(currLine[0][j])){
      afterSpace = false;
    }else{
      if(!afterSpace){
        currLine[0][j] = '\0';
      }
      afterSpace = true;
    }
    j++;
  }
}
