/* Author: Daniel Lorigan, Margot Maxwell, Jacob Peterson
 * Date:  Spring 2017
 * Purpose: A controller that handles looping through days
 * Coupled with: Player, Board, and View
 */


import java.util.*;
import java.io.*;
import java.lang.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Game extends Observable {
  private int numPlayers;
  private int dayCounter;
  private int maxDays;
  private Player[] players;
  private Scene[] deck;
  private int remainingScenes;
  private int numberOfActiveScenes;
  private boolean gameActive;
  private Board board;
  private int day;
  private View view;
  
  public Game(int numPlayers, View newView) {
    this.numPlayers = numPlayers;
    this.dayCounter = 0;
    this.maxDays = 4;
    this.players = initializePlayers(numPlayers);
    this.deck = initializeDeck();
    this.remainingScenes = 40;
    this.numberOfActiveScenes = 10;
    this.gameActive = true;
    this.board = new Board();
    this.day = 0;
	  view = newView;
  }
  
   // getters and setters
  public int getNumPlayers() {
    return numPlayers;
  }
  
  public int getDayCounter(){
    return dayCounter;
  }
  
  public int getMaxDays(){
    return maxDays;
  }
  
  public Player[] getPlayers(){
    return players;
  }
  
  public Player getPlayer(int i){
    return players[i];
  }
  
  public Scene[] getDeck(){
    return deck;
  }
  
  public int getDay(){
    return day;
  }
  
  public Board getBoard(){
    return board;
  }
  
  public int getRemainingScenes(){
    return remainingScenes;
  }
  
  public int getNumActiveScenes(){
    return numberOfActiveScenes;
  }
  
  public boolean getActive(){
    return gameActive;
  }
  
  public void endGame(){
    gameActive = false;
    day = maxDays + 1;
  }
  
  public void incrementDay(){
    day++;
  }
  
  public void setMaxDays(int newMax){
    maxDays = newMax;
  }
  
  public void setRemainingScenes(int remScenes){
    remainingScenes = remScenes;
    triggerObservers();
  }
  
  public void decrementActiveScenes(){
    numberOfActiveScenes--;
    triggerObservers();
  }
  
  private Player[] initializePlayers(int numberOfPlayers){
    Player[] playerArray = new Player[numberOfPlayers];
    for (int i = 0; i < numberOfPlayers; i++) {
      playerArray[i] = new Player(i);
    }
    return playerArray;
  }
	
	//This is called when the day is over	
  private void triggerObservers(){
	setChanged();
	notifyObservers();
  }
  
  //Build the deck of scenes by reading line by line from cards.txt
  private Scene[] initializeDeck(){
    Scene[] currDeck = new Scene[40];
    String name = null;
    String img = null;
    int number = 0;
    String desc = "";
    int budget = 0;
    int numRoles = 0;
    try {
	    int numerator = 4;
	    int denominator = 5;
    	File file = new File("resources/cards.xml");
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder();
    	Document doc = db.parse(file);
    	doc.getDocumentElement().normalize();
    
    	NodeList cardList = doc.getElementsByTagName("card");
    
    	for (int c=0; c < cardList.getLength(); c++) {
    	    Node card = cardList.item(c);
    	    if (card.getNodeType() == Node.ELEMENT_NODE) {
    		Element eCard = (Element) card;
    		Element scene = (Element) eCard.getElementsByTagName("scene").item(0);
    		NodeList parts = eCard.getElementsByTagName("part");
    		
    		name = eCard.getAttribute("name");
    		img = eCard.getAttribute("img");
    		budget = Integer.parseInt(eCard.getAttribute("budget"));
    		number = Integer.parseInt(scene.getAttribute("number"));
    		desc = eCard.getElementsByTagName("scene").item(0).getTextContent();
    		numRoles = parts.getLength();
    		Role[] roles = new Role[numRoles];
    		for (int p=0; p < numRoles; p++) {
    		    //System.out.println("Game inner for loop, p: " + p);
    		    Element part = (Element) parts.item(p);
    		    Element area = (Element) part.getElementsByTagName("area").item(0);
    		    
    		    int rank = Integer.parseInt(part.getAttribute("level"));
    		    String line = part.getElementsByTagName("line").item(0).getTextContent();
    		    String roleName = part.getAttribute("name");
    		    int x = (Integer.parseInt(area.getAttribute("x"))*numerator)/denominator;
    		    int y = (Integer.parseInt(area.getAttribute("y"))*numerator)/denominator;
    		    int h = (Integer.parseInt(area.getAttribute("h"))*numerator)/denominator;
    		    int w = (Integer.parseInt(area.getAttribute("w"))*numerator)/denominator;
    
    		    roles[p] = new Role(rank, true, line, roleName, x, y, h, w);
    		  }
    		currDeck[c] = new Scene(numRoles, number, desc, name, budget, roles, img);
    	  }
    	}
    } catch(Exception e){}  
    return currDeck;
  }
  
  // initialize the game objects
  public void startGame() {
    Studio[] studios = board.getStudioList();
    setupDay(studios);
  	view.setCards(); 
  }
  
  // sets shot counters to defaults, replaces scenes in studios, and puts players back in Trailers.
  private void setupDay(Studio[] rooms) {
    view.resetCardBacks();
	  board.assignScenes(deck);
    //Put players back in Trailers
    board.resetLocations(numPlayers);
    for (int i = 0; i < players.length; i++){
      //text version:
      this.getPlayer(i).setLocation(board.getStudioList()[0]);
      //GUI version:
      this.getPlayer(i).setInitialPosition(i);
    }
    //Reset all of the studios shot counters
    for(int j = 2; j < 12; j++){
      rooms[j].resetShotCounter();
    }
    numberOfActiveScenes = 10;
  }

  
  // returns players to trailers, clears all studios scenes.
  public void wrapDay() {
    this.day = this.day + 1;
    //System.out.println("The day is over! There are " + (maxDays-this.day) + " days left.");
    //System.out.println("All players have been returned to Trailers.");
    Player p = null;
    //If a player is unemployed at the end of a day, makes them unemployed for the new day.
    for (int i = 0; i < players.length; i++){
      p = players[i];
      view.movePlayer(p, 1); 
      if (p.getEmployed()){
        p.setEmployed(false);
        p.getCurrentRole().setCurrentPlayer(null);
        p.setCurrentRole(null);
      }
    }
    Studio[] studios = board.getStudioList();
    for (int s=2; s<12; s++){
      Scene scene = studios[s].getTheActiveScene();
        scene.setActive(false);
    }
    //Check if the game is over
    //Calc score and end game
    //triggerObservers();
    if(day == maxDays){
      System.out.println("The game has ended!");
      calculateScore();
      gameActive = false;
    } else if (day != maxDays){
      setupDay(board.getStudioList());
    }
  }
  
  // calculates players' scores at the end of the game
  public int[]  calculateScore() {
    //array to hold players points, index corresponds to player's number
    int[] points = new int[numPlayers];
    for (int i=0; i<numPlayers; i++) {
        points[i] = (players[i].getDollars() + players[i].getCredits() + (5 * players[i].getRank()));
    }
    int maxPoints=0;
    int winner;
    //prints points
    //System.out.println("Final Points");
    int i = 0;
    for (i=0; i<numPlayers; i++) {
      System.out.println("Player " + i + " scored " + points[i] + " points");
      //this doesn't account for ties
      if (points[i] > maxPoints) {
          maxPoints =  points[i];
          winner = i;
      }
    }
    return points;
  }  
}

