/* Author: Daniel Lorigan, Margot Maxwell, Jacob Peterson
 * Date: Spring 2017
 * Purpose: Player is an object to hold all player information and actions, and facilitate interaction from other classes.
 * Coupled with: Game and View
 */
import java.util.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import java.lang.*;
import java.awt.Toolkit;

public class Player extends Observable {
  private int rank;
  private int playerNumber;
  private int dollars;
  private int credits;
  private Role currentRole; //player's current role
  private int practiceChips;
  private Studio location;
  private String color;
  private ImageIcon playerToken;
  private JLabel playerlabel;
  private boolean employed; //true if the player is employed
  private int x;
  private int y;
  private int width;
  private int height;

  public Player(int playerNumber){
    this.rank = 1;
    this.playerNumber = playerNumber;
    this.dollars = 0;
    this.credits = 0;
    this.currentRole = null; //player's current role
    this.practiceChips = 0;
    this.employed = false;
    this.location = null;
    //call a function that determines player color based on number, and sets
    this.color = determineColor(playerNumber);
    // that player's image icon
    this.playerToken = initPlayerIcon(this.color, this.rank);
    this.width = playerToken.getIconWidth();
    this.height = playerToken.getIconHeight();
  }

  //getters:
  public JLabel getPlayerLabel(){
    return this.playerlabel;
  }
  
  public ImageIcon getImageIcon(){
    return this.playerToken;
  }

  public int getRank() {
    return this.rank;
  }
  
  public String getColor(){
    return this.color;
  }
  
  public int getPlayerNum() {
    return this.playerNumber;
  }
  
  public int getDollars(){
    return dollars;
  }  
  
  public int getCredits(){
    return credits;
  }
  
  public Role getCurrentRole() {
    return this.currentRole;
  }
  
  public int getPracticeChips(){
    return this.practiceChips;
  }
  
  public Studio getLocation(){
    return this.location;
  }
  
  public boolean getEmployed(){
    return this.employed;
  }

  public int getX(){
    return this.x;
  }

  public int getY(){
    return this.y;
  }

  public int getWidth(){
    return this.width;
  }

  public int getHeight(){
    return this.height;
  }
  
  //setters:

  //used when moving 
  public void setX(int newX){
    this.x = newX;
  }

  public void setY(int newY){
    this.y = newY;
  }
  
  public void setRank(int rank){
    this.rank = rank;
    setPlayerIcon(this.color, this.rank);
  }
  
  public void setDollars(int dollars){
    this.dollars = dollars;
    triggerObservers();
  }
  
  public void setCredits(int credits){
    this.credits = credits;
    triggerObservers();
  }
  
  public void setCurrentRole(Role newCurrentRole) {
    this.currentRole = newCurrentRole;
    triggerObservers();
  }
  
  public void setPracticeChips(int newPracticeChips) {
    this.practiceChips = newPracticeChips;
    triggerObservers();
  } 
  
  public void setEmployed(boolean newEmployed) { 
    this.employed = newEmployed; //true if the player is employedStatus
    triggerObservers();
  }
  
  public void setLocation(Studio location){
    this.location = location;
  }
  
	// This is called after player's values get updated
  private void triggerObservers() {
    setChanged();
    notifyObservers();
  }

  // choose a random number 1-6
  public int rollDie() {
    Random roll = new Random();
    int result = roll.nextInt(6) + 1;
    return result;
  } 
  
  /* Acting:
   *  - On card: 2 credits for success, nothing for fail.
   *  - Off card: 1 dollar and 1 credit for success, only 1 dollar for fail.
   */
  private void payAct(boolean success){
    
    if (success){
      if (this.currentRole.getStarRole()) {
        this.credits = this.credits + 2;
      } else {
        this.credits = this.credits + 1;
        this.dollars = this.dollars + 1;
      }
      triggerObservers();
    } else {
      if (!this.currentRole.getStarRole()) {
        this.dollars = this.dollars + 1;
        triggerObservers();
      }
    }
  }
  
  //If dice role + rehearsal chips is greater than or equal to the budget, return true and decrement shot counter. False otherwise.
  public boolean act() {
    int result = rollDie();
    boolean success = false;
    if (result + getPracticeChips() >= location.getActiveScene().getBudget()) {
      payAct(true);
	    success = true;
      location.decrementShotCounter();
    } else {
      payAct(false);
    }
	  return success;
  }
  
  public void rehearse() {
	  practiceChips = practiceChips + 1;
    triggerObservers();
  }
  
  /* Input: Name of adjacent room, list of all studios
   * Returns: True if player succesfully moves
   * Description: If the given room is adjacent, remove player from studio currentPlayer list and add them to the new studio's list.
   * Update players location.
   */
  public String move(String adjRoom, Studio[] studioList ) {
    String imgName = "";
	  ArrayList<String> adjacentRooms = location.getAdjacentRooms();
    //Check if the room is adjacent
    for(int i = 0; i < adjacentRooms.size(); i++){
      if(adjacentRooms.get(i).equalsIgnoreCase(adjRoom)){
        //Loop through studio list to find the one that matches the name
        for(int j = 0; j < studioList.length; j++){
          if(studioList[j].getName().equalsIgnoreCase(adjRoom)){ 
            //Removes player from old room arraylist then updates location to new room and adds player to new room arraylist
            location.removePlayer(playerNumber);
            location = studioList[j]; 
            location.addPlayer(playerNumber);
			      //Flip the card if it's a new scene
			      if((location.getFirst() == true) && (!location.getName().equals("Trailers")) && (!location.getName().equals("Casting Office"))){
              imgName = location.getActiveScene().getFile(); 
			      }
          }
        }
      }
    }
    return imgName;
  }

  //Returns true if attempted role is taken
  public boolean takeRole(String role) {
    if(currentRole == null){
      Scene currScene = location.getActiveScene();
      ArrayList<Role>  extraRoles = location.getExtraRoles();
      
      //TODO this will probably also be an array list after converting
      //everything to read the xml for the cards
      Role[] starRoles = currScene.getRoles();
      //Only get legnth once instead of every iteration of the for loop
      int extraRolesLength = extraRoles.size();
      int starRolesLength = starRoles.length;
      //Checks if the desired role exists in extra roles
      //If it does checks if it is occupied and the sets the players role to the desired role if not.
      for(int i = 0; i < extraRolesLength; i++){
        Role currRole = extraRoles.get(i);
        if(role.equalsIgnoreCase(currRole.getCharacterName())){
          if(!currRole.getOccupied()){
            if(currRole.getRequiredRank() <= rank){
              currentRole = extraRoles.get(i);
              employed = true;
	            x = currentRole.getX();
	            y = currentRole.getY();
              currRole.setOccupied(true);
              currRole.setCurrentPlayer(this);
	            triggerObservers();
              return true;
            }
		      }
        }
      }
      for(int i = 0; i < starRolesLength; i++){
        Role currRole = starRoles[i];
        if(role.equalsIgnoreCase(currRole.getCharacterName())){
          if(!currRole.getOccupied()){
            if(currRole.getRequiredRank() <= rank){
              currentRole = currRole;
              employed = true;
	            x = currentRole.getX();
	            y = currentRole.getY();
              currRole.setOccupied(true);
              currRole.setCurrentPlayer(this);
	            triggerObservers();
              return true;
            } 
          }
        }
      }
    }
    return false;
  }
  
  private String determineColor(int playerNumber){
    String color =  "";
    switch(playerNumber){
      case 0:
        color = "blue";
        break;
      case 1:
        color = "cyan";
        break;
      case 2:
        color = "green";
        break;
      case 3:
        color = "orange";
        break;
      case 4:
        color = "pink";
        break;
      case 5:
        color = "red";
        break;
      case 6:
        color = "violet";
        break;
      case 7:
        color = "yellow";
        break;
    }
  return color;
}

private ImageIcon initPlayerIcon(String color, int rank){
    String path = "resources/dice/x9.png";
    String[] patha = path.split("");
    StringBuilder pathSB;
    this.playerlabel = new JLabel();
    switch(color){
        case "blue":
          //update the appopriate index of the path so that x9 is relpaced,
          //then make this call:
          patha[15] = "b";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "cyan":
          patha[15] = "c";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "green":
          patha[15] = "g";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "orange":
          patha[15] = "o";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "pink":
          patha[15] = "p";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);           
          break;
        case "red":
          patha[15] = "r";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);      
          break;
        case "violet":
          patha[15] = "v";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);   
          break;
        case "yellow":
          patha[15] = "y";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);  
          break;
      }
      this.playerlabel.setIcon(playerToken);
      return playerToken;
  }

private ImageIcon setPlayerIcon(String color, int rank){
    this.playerToken = null;
    String path = "resources/dice/x9.png";
    String[] patha = path.split("");
    StringBuilder pathSB;
      switch(color){
        case "blue":
          //update the appopriate index of the path so that x9 is relpaced,
          patha[15] = "b";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "cyan":
          patha[15] = "c";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "green":
          patha[15] = "g";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "orange":
          patha[15] = "o";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "pink":
          patha[15] = "p";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "red":
          patha[15] = "r";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "violet":
          patha[15] = "v";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
        case "yellow":
          patha[15] = "y";
          patha[16] = Integer.toString(rank);
          path = Arrays.toString(patha).replaceAll(",","");
          path =  path.replaceAll(" ","");
          pathSB = new StringBuilder(path);
          pathSB.deleteCharAt(0);
          pathSB.deleteCharAt(21);
          path = pathSB.toString();
          this.playerToken = new ImageIcon(path);
          break;
      }
      this.playerlabel.setIcon(playerToken);
      return playerToken;
  }
  
  public void setInitialPosition(int playerNumber){
      if(playerNumber<3){
        this.x = (this.location.getX() + ((this.playerNumber + 1) * this.width));
        this.y = this.location.getY();
      }else if((playerNumber == 7) || (playerNumber == 6)){
        this.x = this.location.getX() + (((this.playerNumber % 3) + 1) * this.width);
        this.y = this.location.getY() + this.height *2;
      } else {
        this.x = (this.location.getX() + (((this.playerNumber % 3) + 1)  * this.width));
        this.y =this.location.getY()+this.height; 
      }
  }
}
