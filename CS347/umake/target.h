/* rnode_t
 * Linked list for makefile commands
 * stored inside a target node
 */
typedef
struct rule_node{
  char* rule;
  struct rule_node* next;
}rnode_t;

/* tnode_t
 * Linked list for makefile targets
 * store liked list of rules and char** of dependencies
 */
typedef
struct targ_node{
  char* target;
  rnode_t* ruleList;
  char** dependencies;
  struct targ_node* next;
}tnode_t;

void target_parse(char* line);
void command_parse(char* line);
rnode_t* findCommands(char* target);
void freeTarget();
