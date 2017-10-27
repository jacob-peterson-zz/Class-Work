

/* Author: Daniel Lorigan, Margot Maxwell, Jacob Peterson
 * Date: Spring 2017
 * Purpose: 
 * Coupled with: Room, Scene, and Role
 */
import java.util.*;
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class Studio extends Observable {
  private Scene activeScene;
  private int maxShotCounters;
  private int currShotCounters;
  private ArrayList<Role> extraRoles;
  private int numRoles;
  private String name;
  private ArrayList<String> adjRooms; 
  private ArrayList<Integer> currentPlayers; 
  private ArrayList<int[]> shotCounters;
  private int x;
  private int y;
  private int width;
  private int height;
  private Point[] freeSpaces;
  private boolean first;
  private ImageIcon shot;
  private JLabel[] shotLabels;
 
  public Studio(String name, int maxShotCounters, ArrayList<int[]> shotCounters, ArrayList<Role> roles, ArrayList<String> adjRooms, int numRoles, int x, int y, int height, int width, Point[] freeSpaces){
    this.activeScene = null;
    this.maxShotCounters = maxShotCounters;
    this.currShotCounters = maxShotCounters;
    this.extraRoles = roles;
    this.numRoles = numRoles;
    this.name = name; //added from Room 
    this.adjRooms = adjRooms; //added from Room 
    this.currentPlayers = new ArrayList<Integer>(); //holds player Number
    this.shotCounters = shotCounters;
    this.x = x - 5;
    this.y = y - 4;
    this.height = height;
    this.width = width;
    this.freeSpaces = freeSpaces;
	  this.first = true;
	  shot = new ImageIcon("resources/shot.png");
	  shotLabels = new JLabel[maxShotCounters];
	  for(int i = 0; i < maxShotCounters; i++){
	  shotLabels[i] = new JLabel();
	  shotLabels[i].setIcon(shot);
	  }
  }
  //used to decrease the number of shot counters
  public void decrementShotCounter() {
    currShotCounters--;
    triggerObservers();
  }
  
  
  // resets shot counter to maxShotCounters
  public void resetShotCounter() {
    this.currShotCounters = this.maxShotCounters;
    triggerObservers();
  }
  
  public void removePlayer(int playerNumber){
    this.currentPlayers.remove(Integer.valueOf(playerNumber));
  }
  
  public void addPlayer(int playerNumber){
    this.currentPlayers.add(Integer.valueOf(playerNumber));
  }

  private void triggerObservers() {
	  setChanged();
	  notifyObservers();
  }
  
  //Clears scene from studio, pays bonuses, clears roles of their players and vice versa
  public void endScene(){
    Role role = null;
    //Extra roles is working now.
    for (int i = 0; i < extraRoles.size(); i++) {
    //for (int i = 0; i < extraRoles.length; i++) {
      role = extraRoles.get(i);
      if (role.getOccupied()) {
        Player p = role.getCurrentPlayer();
        p.setDollars(p.getDollars() + role.getRequiredRank());
      }
    }
    //Give bonuses
    Role[] leadRoles = activeScene.getRoles();
    Player leadPlayer = null;
    ArrayList<Role> leads = new ArrayList<Role>();
    //finds a player to roll the dice
    for (int i = 0; i < leadRoles.length; i++) {
      if (leadRoles[i].getOccupied()) {
        leadPlayer = leadRoles[i].getCurrentPlayer();
      }
      leads.add(leadRoles[i]);
    }
    if(leadPlayer != null){
      ArrayList<Integer> rolls = new ArrayList<Integer>();
      //roll the dice *budget* times
      for (int i = 0; i < activeScene.getBudget(); i++) {
        rolls.add(leadPlayer.rollDie());
      }
      Collections.sort(rolls);
      Collections.reverse(rolls);
      Collections.reverse(leads);
    
      for (int i = 0; i < rolls.size(); i++){
        role = leads.get(i%leads.size());
        if (role.getOccupied()) {
          Player p = role.getCurrentPlayer();
          p.setDollars(p.getDollars() + rolls.get(i));
        }
      }
    }
    
    unemployCurrentPlayers();
    activeScene.setActive(false);
  }
  
  
  public Scene getActiveScene(){
    return activeScene;
  }


  //Resets the studio and all of the players in it after the scene is over.
  public void unemployCurrentPlayers(){
    first = true;
	  Role r = null;
    Player p = null;
    for (int i = 0; i < extraRoles.size(); i++) {
      r = extraRoles.get(i);
      p = r.getCurrentPlayer();
      if (r.getOccupied() && p.getEmployed()) {
        p.setEmployed(false);
        p.setPracticeChips(0);
        p.getCurrentRole().setCurrentPlayer(null);
        r.setCurrentPlayer(null);
        p.setCurrentRole(null);
        r.setOccupied(false);
      }
    }
    for (int i = 0; i < activeScene.getRoles().length; i++) {
      r = activeScene.getRoles()[i];
      p = r.getCurrentPlayer();
      if (r.getOccupied() && p.getEmployed()) {
        p.setEmployed(false);
        p.setPracticeChips(0);
        p.getCurrentRole().setCurrentPlayer(null);
        r.setCurrentPlayer(null);
        p.setCurrentRole(null);
      }
    }
  }
    
  public void displayStudioState() {
    
    //name
    System.out.println("Room name: " + name);
    //active scenes
    try{
      System.out.println("Scene name: " + activeScene.getMovieTitle());
      System.out.println("Scene Shots left: " + currShotCounters);
      System.out.println("Budget: " + activeScene.getBudget());
      //need to also show which players are in the room
      System.out.print("Players in room: ");
      for (Integer p : getCurrentPlayers()){
        System.out.print("Player " + p +"  ");
      }
      //roles
      System.out.println("\n\n--Lead roles--");
      Role [] roles = activeScene.getRoles();
      for(int i = 0; i < roles.length; i++){
        Role role = roles[i];
        System.out.println(role.getCharacterName() + " '" + role.getLine() + "' " + role.getRequiredRank());
        if(role.getOccupied()){
          
        }
      }
      
      System.out.println("\n--Extra roles--");
      for(Role r : this.extraRoles){
        System.out.println(r.getCharacterName() + " '" + r.getLine() + "' " + r.getRequiredRank());
      }
    
    } catch (NullPointerException e){
      //need to also show which players are in the room
    System.out.print("Players in room: ");
    for (Integer p : getCurrentPlayers()){
      System.out.print("Player " + p +"  ");
    }
    System.out.println();
    }
  }
  
  // check if studio has an active scene
  public boolean sceneActive() {
    return activeScene.getActive();
  }
  
  //setter 
  public void setX(int x){
    this.x = x;
  }

  public void setY(int y){
    this.y = y;
  }
  public void setFirst(boolean newFirst){
    first = newFirst;
  }
  
  //getters
  
  //gets x coordinate of a shot counter
  public int getShotCounterX(int shotNumber){
    int[] take = this.shotCounters.get(shotNumber);  
    return take[1];
  }

 public int getShotCounterY(int shotNumber){
    int[] take = this.shotCounters.get(shotNumber);  
    return take[2];
  }
  

  //gets info for a shot counter at a specific index
  public int[] getShotInfo(int shotNumber){
    return this.shotCounters.get(shotNumber);  
  }

  //gets entire shot counter array
  public ArrayList<int[]> getShotCounters(){
    return this.shotCounters;
  }

  public int getX(){
    return this.x;
  }
  public boolean getFirst(){
    return first;		  
  }

  public int getY(){
    return this.y;
  }
  
  public int getHeight(){
    return this.height;
  }

  public int getWidth(){
    return this.width;
  }

  public int getArea(){
    return (this.width * this.height);
  }

  public Point[] getFreeSpaces(){
    return freeSpaces;
  }
  
  public Scene getTheActiveScene(){
    return this.activeScene;
  }
  
  public int getMaxShotCounters(){
    return this.maxShotCounters;
  }
  
  public int getCurrShotCounters(){
    return this.currShotCounters;
  }
  
  public int getNumRoles(){
    return this.numRoles;
  }
  
  public ArrayList<Role> getExtraRoles() {
    return this.extraRoles;
  }
  
  public String getName(){
    return this.name;
  }
  
  public ArrayList<String> getAdjacentRooms() {
    return this.adjRooms;
  }
  
  //puts everyone in trailers
  public void setCurrentPlayers(int numPlayers, Player[] players){
    if(name.equalsIgnoreCase("Trailers")){
      for(int i = 0; i < numPlayers; i++)
        currentPlayers.add(players[i].getPlayerNum());
    }
  }
    
  public void setFreeSpaces(Point[] spaces) {
	  this.freeSpaces = spaces;
  }  

  public void setActiveScene(Scene scene){
    this.activeScene = scene;
  }
  
  public void setCurrShotCounters(int counters){
    this.currShotCounters = counters;
    triggerObservers();
  }
  
  
    //checks if a room is adjacent to this room
  public boolean checkAdjacency(Studio adjRoom){
    for (int i=0; i < adjRooms.size(); i++){
      if(adjRoom.getName() == adjRooms.get(i)){
        return true;
      }
    }
    return false;
  }
  
  public ArrayList<Integer> getCurrentPlayers(){
    return this.currentPlayers;
  }
  
  public void addCurrentPlayer(int playerNumber){
    this.currentPlayers.add(playerNumber);
  }
  
  public void clearCurrentPlayers(){
    this.currentPlayers.clear();
  }
  
  public JLabel[] getShotCountView(){
    for(int i = 0; i < maxShotCounters; i++){
      int[] currShotCounter = shotCounters.get(i);
	    shotLabels[i].setBounds(currShotCounter[1], currShotCounter[2], 
		  currShotCounter[3], currShotCounter[4]);
	  }
	return shotLabels;
  }
  
}
