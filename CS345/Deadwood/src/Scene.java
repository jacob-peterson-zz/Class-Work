/* Author: Daniel Lorigan, Margot Maxwell, Jacob Peterson
 * Date: Spring 2017
 * Purpose: Creates and object that holds the data for each scene. 
 * Coupled with: Studio and Role
 */

public class Scene {
  private int numRoles;
  private boolean active;
  private int sceneNum;
  private String sceneDescription;
  private String movieTitle;
  private int budget;
  private Role[] roles;
  //private Room parentStudio;
  private Studio parentStudio;
  private boolean used;
  String file;
  
  //constructor
  public Scene(int numRoles, int sceneNum, String sceneDescription, String movieTitle, int budget, Role[] roles, String file){
    this.numRoles = numRoles;
    this.active = false;
    this.sceneNum = sceneNum;
    this.sceneDescription = sceneDescription;
    this.movieTitle = movieTitle;
    this.budget = budget;
    this.roles = roles;
    this.parentStudio = null;
    this.used = false;
	  this.file = file;
  }
  
  //getters
  public int getNumRoles() {
    return this.numRoles;
  }
    
  public boolean getActive() {
    return this.active;
  }
  
  public String getFile(){
    return file;
  }
  public int getsceneNum() {
    return this.sceneNum;
  }
  
  public String getSceneDescription(){
    return this.sceneDescription;
  }
    
  public String getMovieTitle() {
    return this.movieTitle;
  }
     
  
  public int getBudget(){
    return budget;
  }
  
  public Role[] getRoles(){
    return this.roles;
  }

  public boolean getUsed(){
    return this.used;
  }
  
  public Studio getParentStudio(){
    return this.parentStudio;
  }
  
  //setters
  public void setUsed(boolean newUsed){
    this.used = newUsed;
  }
  
  public void setActive(boolean active){
    this.active = active;
  }
  
  public void setParentStudio(Studio pStudio){
    this.parentStudio = pStudio;
  }
}
