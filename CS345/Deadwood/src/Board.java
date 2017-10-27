/* Author: Daniel Lorigan, Margot Maxwell, Jacob Peterson
 * Date: Spring 2017
 * Purpose: A bigger picture of the layout of all of the rooms. Initializes the layout of the board.
 * Coupled with: Room and Game
 */

import java.util.*;
import java.util.ArrayList;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.awt.Point;
public class Board {
  
  //Holds all 12 rooms, each room object has an adjacency list
  private Studio[] studioList;
  private String fileName;
  
  //Constructor
  public Board() {
    this.studioList = initializeBoard();
    this.fileName = fileName;
  }
  
  public void resetLocations(int numPlayers){
    for (int i = 0; i < studioList.length; i++){
      Studio currStudio = studioList[i];
        if (currStudio.getName().equalsIgnoreCase("Trailers")){
          for (int j = 0; j < numPlayers; j++){
            currStudio.addCurrentPlayer(j);
          }
        } else {
          currStudio.clearCurrentPlayers();
        }
    }
  }
  
  
  public Studio getRoom(int i){
    return studioList[i];
  }
  

  public Studio getRoom(String roomName){
    for (int i=0; i < studioList.length; i++){
      if (studioList[i].getName().equalsIgnoreCase(roomName)){
        return studioList[i];
      }
    }
    return null;
  }  
  
  //initializes all room objects with their shot counters, adjacent rooms and roles from board.xml
  private Studio[] initializeBoard(){
    Studio[] currStudioList = new Studio[12];
    String roomName = null;
    int numShots = 0;
    int numRoles = 0;
    int numAdj = 0;
    int x = 0;
    int y = 0;
    int h = 0;
    int w = 0;

    currStudioList[0]=initTrailer();
    currStudioList[1]=initOffice();
    currStudioList[0].setFreeSpaces(makeFreeSpaces("Trailers", 792, 245));
    currStudioList[1].setFreeSpaces(makeFreeSpaces("Casting Office", 7, 414));
    try{
    	int numerator = 4;
      int denominator = 5;
      File file = new File("resources/board.xml");
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder();
    	Document doc = db.parse(file);
    	doc.getDocumentElement().normalize();
    
    	// split file by set
    	NodeList setList = doc.getElementsByTagName("set");
    	
    	for (int s=0; s < setList.getLength(); s++) {
    	    Node set = setList.item(s);
    	    if (set.getNodeType() == Node.ELEMENT_NODE) {
    		    Element setElement = (Element) set;
        		roomName = setElement.getAttribute("name");
        		ArrayList<String> adjacentRooms = new ArrayList<String>();
        		Element neighbors = (Element) setElement.getElementsByTagName("neighbors").item(0);
    		    NodeList neighbor = neighbors.getElementsByTagName("neighbor");
    		    for (int n=0; n < neighbor.getLength(); n++) {
    		      Element room = (Element) neighbor.item(n);
    		      adjacentRooms.add(room.getAttribute("name"));
    		    }
    		    Element area = (Element) setElement.getElementsByTagName("area").item(0);
    		    x = (Integer.parseInt(area.getAttribute("x"))*numerator)/denominator;
    		    y = ((Integer.parseInt(area.getAttribute("y"))*numerator)/denominator)+47;
    		    h = (Integer.parseInt(area.getAttribute("h"))*numerator)/denominator;
    		    w = (Integer.parseInt(area.getAttribute("w"))*numerator)/denominator;
    		    ArrayList<int[]> shotCounters = new ArrayList<int[]>();
    		    Element takes = (Element) setElement.getElementsByTagName("takes").item(0);
    		    NodeList take = takes.getElementsByTagName("take");
    		    for (int t=0; t < take.getLength(); t++) {
    		      Element shot = (Element) take.item(t);
    		      Element a = (Element) shot.getElementsByTagName("area").item(0);
    		      int[] shotInfo = new int [5];
    		      shotInfo[0] = Integer.parseInt(shot.getAttribute("number"));
    		      shotInfo[1] = (Integer.parseInt(a.getAttribute("x"))*numerator)/denominator;
    		      shotInfo[2] = ((Integer.parseInt(a.getAttribute("y"))*numerator)/denominator)+47;
    		      shotInfo[3] = (Integer.parseInt(a.getAttribute("h"))*numerator)/denominator;
    		      shotInfo[4] = (Integer.parseInt(a.getAttribute("w"))*numerator)/denominator;
    		      shotCounters.add(shotInfo);
    		    }
    		    Collections.reverse(shotCounters);
    		    numShots = shotCounters.size();
    		    ArrayList<Role> roles = new ArrayList<Role>();
    		    Element parts = (Element) setElement.getElementsByTagName("parts").item(0);
    		    NodeList part = parts.getElementsByTagName("part");
    		    for (int p=0; p < part.getLength(); p++) {
    		       Element role = (Element) part.item(p);
    		       Element a = (Element) role.getElementsByTagName("area").item(0);
    		        String name      = role.getAttribute("name");
    		        int level        = Integer.parseInt(role.getAttribute("level"));
    		        String line      = role.getElementsByTagName("line").item(0).getTextContent();		    
    		        int roleX        = (Integer.parseInt(a.getAttribute("x"))*numerator)/denominator;
    		        int roleY        = ((Integer.parseInt(a.getAttribute("y"))*numerator)/denominator)+47;
    		        int roleHeight   = (Integer.parseInt(a.getAttribute("h"))*numerator)/denominator;
    		        int roleWidth    = (Integer.parseInt(a.getAttribute("w"))*numerator)/denominator;
    		    
    		        Role currRole = new Role(level, false, line, name, roleX, roleY, roleHeight, roleWidth);
    		        roles.add(currRole);
    	      	}
    		      numRoles = roles.size();
        //calls function, passes roomname, freeSpaces has switch statement, returns
        //array of points, gives it to constructor below
              Point[] freeSpaces = makeFreeSpaces(roomName, x, y);
    		      currStudioList[s+2] = new Studio(roomName, numShots, shotCounters, roles, adjacentRooms, numRoles, x, y, h, w, freeSpaces);
    	    }
    	}
    } catch(Exception e){
	    System.out.println("file Rooms.txt not found");
    }
    return currStudioList;
  }
  
  public void assignScenes(Scene[] deck){
    List<Integer> rand = new ArrayList<Integer>();
    for (int r=0; r<40; r++){
      rand.add(r);
    }
    Collections.shuffle(rand);
    for(int i = 0; i < studioList.length; i++){
      //Choose a random scene from deck
      int randomScene = rand.get(i);
      //Assign scenes to studios
      String currRoom = studioList[i].getName();
      if(!currRoom.equalsIgnoreCase("Casting Office") && !currRoom.equalsIgnoreCase("Trailers")){
        studioList[i].setActiveScene(deck[randomScene]);
        deck[randomScene].setUsed(true);
        deck[randomScene].setActive(true);
      }
    }
  }

  public Studio[] getStudioList(){
    return studioList;
  }
  
  public void displayBoardState() {
    for (Studio s : studioList){
      s.displayStudioState();
    }
  }

  private Studio initTrailer(){
    ArrayList<String> adjRooms = new ArrayList<String>();
    adjRooms.add("Main Street");
    adjRooms.add("Saloon");
    adjRooms.add("Hotel");
    Studio trailer = new Studio("Trailers", 0, null, null, adjRooms, 0,792, 245, 155, 160, null);
    return trailer;
  }

  private Studio initOffice(){
    ArrayList<String> adjRooms = new ArrayList<String>();
    adjRooms.add("Train Station");
    adjRooms.add("Ranch");
    adjRooms.add("Secret Hideout");
    Studio office = new Studio("Casting Office", 0, null, null, adjRooms, 0, 7, 414, 166, 167, null);
    return office;
  }
  
  public Point[] makeFreeSpaces(String roomName, int x, int y){
    int iW = 37;//icon width
    int iH = 37;//icon height
    int originalX;
    int originalY;
    y += 100;
    Point[] freeSpaces = new Point[8];
    Point a = null;
    Point b = null;
    Point c = null;
    Point d = null;
    Point e = null;
    Point f = null;
    Point g = null;
    Point h = null;
    switch(roomName){
      case "Trailers":
        y-=37;
        a = new Point(x,y); 
        b = new Point(x+iW,y); 
        c = new Point(x+(iW*2),y); 
        d = new Point(x+(iW*3),y);
        e = new Point(x,y+iH); 
        f = new Point(x+(iW*2),y+iH); 
        g = new Point(x+(iW*3),y+iH);
        h = new Point(x+(iW*4),y+iH);
        break;
      case "Casting Office":
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        y+=iH;
        e = new Point(x,y);
        f = new Point(x+iW,y);
        g = new Point(x+(iW*2),y);
        h = new Point(x+(iW*3),y);
        break;
      case "Train Station":
        x-=9;
        y-=3;
        a = new Point(x,y);
        b = new Point(x+iW,y);
        y+=iH;
        c = new Point(x,y);
        y+=iH;
        d = new Point(x,y);
        y+=iH;
        e = new Point(x,y);
        y+=iH;
        f = new Point(x,y);
        g = new Point(x+iW,y+17);
        h = new Point(x+(iW*2),y+17);
        break;
      case "Secret Hideout":
        x-=8;
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        f = new Point(x+(iW*5),y);
        g = new Point(x+(iW*6),y);
        h = new Point(x+(iW*7),y);
        break;
      case "Church":
        originalX = x;
        x-=5;
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        f = new Point(x+(iW*5),y);
        g = new Point(x+(iW*6),y);
        x = originalX + (4 * iW);
        h = new Point(x,y - 105);
        break;
      case "Hotel":
        x-=16;
        y-=3;
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        originalX = x;
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        y-=188;
        f = new Point(x+(iW*3),y);
        g = new Point(x+(iW*4),y); 
        x = originalX + (2*iW);
        y -= (3 * iH);
        y -= 19;
        h = new Point(x,y);
        break;
      case "Main Street":
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        f = new Point(x+(iW*2),y+iH);
        g = new Point(x+(iW*3),y+iH);
        h = new Point(x+(iW*4),y+iH);
        break;
      case "Jail":
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        y+=5;
        d = new Point(x+(iW*2),y+iH);
        e = new Point(x+(iW*3)+16,y+iH);
        f = new Point(x+(iW*4)+16,y+iH);
        g = new Point(x+(iW*5)+16,y+iH);
        h = new Point(x+(iW*6)+16,y+iH);
        break;
      case "General Store":
        x-=(iW * 3);
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        f = new Point(x+(iW*5),y);
        g = new Point(x+(iW*6),y);
        h = new Point(x+(iW*7),y);
        break;
      case "Ranch":
        x+= 16;
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        y+=iH;
        d = new Point(x,y);
        e = new Point(x+(iW*1),y);
        f = new Point(x+(iW*2),y);
        y+=5;
        g = new Point(x+(iW*3),y);
        h = new Point(x+(iW*4),y);
        break;
      case "Bank":
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        f = new Point(x+(iW*5),y);
        g = new Point(x+(iW*6),y);
        h = new Point(x+(iW*7),y);
        break;
      case "Saloon":
        x-=10;
        a = new Point(x,y);
        b = new Point(x+iW,y);
        c = new Point(x+(iW*2),y);
        d = new Point(x+(iW*3),y);
        e = new Point(x+(iW*4),y);
        f = new Point(x+(iW*5),y);
        g = new Point(x+(iW*6),y);
        h = new Point(x+(iW*7),y);
        break;
    }
    freeSpaces[0] = a;
    freeSpaces[1] = b;
    freeSpaces[2] = c;
    freeSpaces[3] = d;
    freeSpaces[4] = e;
    freeSpaces[5] = f;
    freeSpaces[6] = g;
    freeSpaces[7] = h;
  return freeSpaces;
  }
}
