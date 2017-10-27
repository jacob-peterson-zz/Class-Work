/* Author: Daniel Lorigan, Margot Maxwell, Jacob Peterson
 * Date: Spring 2017
 * Purpose: Creates an object that holds the data for each role
 * Coupled with: Scene, Studio, Player
 */

import java.util.ArrayList;

public class Role {
  private int requiredRank;
  private boolean starRole;
  private String line;
  private String characterName;
  private Player currentPlayer;
  private boolean occupied;
  private int x;
  private int y;
  private int height;
  private int width;
  
  //constructor
  public Role(int requiredRank, boolean starRole, String line, String character, int x, int y, int height, int width) {
    this.requiredRank = requiredRank;
    this.starRole = starRole;
    this.line = line;
    this.characterName = character;
    this.currentPlayer = null;
    this.occupied = false;
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
  }
  //Sets role's current player to player
  public boolean assignRole(Player player){
    //Checks if player is eligible to take the role. 
    if((player.getRank() >= requiredRank) && (player.getEmployed() == false) && (occupied == false)){
      occupied = true;
      currentPlayer = player;
      return true;
    }
    return false;
  }
  
  //Sets currentPlayer to null and occupied to false for off card roles
  //Should this be private or public?
  public void clearRole(){  
    this.currentPlayer = null;
    this.occupied = false;
  }
  
  // getters and setters:
  public int getX() {
	  return this.x;
  }

  public int getY() {
	  return this.y;
  }

  public Player getCurrentPlayer() {
    return this.currentPlayer;
  }
  
  public int getRequiredRank(){
    return this.requiredRank;
  }
  
  public boolean getStarRole(){
    return this.starRole;
  }

  public String getLine(){
    return this.line;
  }
  
  public boolean getOccupied(){
    return this.occupied;
  }
  
  public String getCharacterName(){
    return this.characterName;
  }
  
  public void setCurrentPlayer(Player p){
    this.currentPlayer = p;
  }

  public void setOccupied(boolean occupied){
    this.occupied = occupied;
  }
}
