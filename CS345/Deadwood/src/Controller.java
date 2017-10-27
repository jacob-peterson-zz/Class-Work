

import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.awt.Point;

public class Controller extends JPanel {
   JButton moveButton;
   JButton workButton;
   JButton actButton;
   JButton rehearseButton;
   JButton upgradeButton;
   JButton endButton;
   JFrame currFrame;
   boolean hasWorked;
   boolean hasMoved;
   Game game;
   View view;
   int turn;
   
   //for now the contructor has no arguments 
   public Controller(JFrame frame, Game game, View view){
	  moveButton = new JButton();
    workButton = new JButton();
    actButton = new JButton();
    rehearseButton = new JButton();
    upgradeButton = new JButton();
    endButton = new JButton();
	  currFrame = frame;
	  hasWorked = false;
	  hasMoved = false;
	  this.game = game;
  	this.view = view;  
  	turn = 0;
   }

   public void makeController(){
      //Set the coordinates of the buttons 
      moveButton.setBounds(60, 0, 118, 47);
      workButton.setBounds(180, 0, 122,  47);
      actButton.setBounds(305, 0, 81, 47);
      rehearseButton.setBounds(389, 0, 195, 47);
      upgradeButton.setBounds(587, 0, 181, 47);
      endButton.setBounds(771, 0, 79, 47);
      

	  //Give each button a mouse listener
      //MOVE
	  moveButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
	    	Player currPlayer = game.getPlayers()[turn];
		    //will call answerMove function in Deadwood
		    if(!hasMoved && !currPlayer.getEmployed()){
		    //Get adjacent rooms
			    Studio currStudio = currPlayer.getLocation();
			    ArrayList<String> adjRooms = currStudio.getAdjacentRooms();
			    Object[] moveOptions = new Object[adjRooms.size()];
		    	for(int i = 0; i < adjRooms.size(); i++){
          	moveOptions[i] = adjRooms.get(i);
			    }

		    //The current player is allowed to move.
          String roomMove = (String) JOptionPane.showInputDialog(currFrame, 
			   "Where do you want to move?",
			   "", JOptionPane.PLAIN_MESSAGE,
			   null, moveOptions, "");
		    //Get studio list from board

         Studio[] studios = game.getBoard().getStudioList();
         if(roomMove.length() != 1){ 
          String cardName = currPlayer.move(roomMove, studios);
			  	if(!cardName.equals("")){
					  int cardNum = Integer.parseInt(cardName.substring(0, cardName.length() - 4)); 
            view.flipCard(cardNum, roomMove);
			  	}
			  	view.movePlayer(currPlayer,0);
			  	hasMoved = true;
		    }
		  } else {
		    //The current player is not allowed to move
     		JOptionPane.showMessageDialog(currFrame, 
			   "You aren't allowed to move.");
		  }
	  }
	});
      //WORK
      workButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
          //call answerWork function in Deadwood
	      Player currPlayer = game.getPlayers()[turn];
	      Studio currStudio = currPlayer.getLocation();
	      if (currStudio.sceneActive() && !currPlayer.getEmployed() && !hasWorked){
		      ArrayList<Role> extras  = currStudio.getExtraRoles();
		      Role[] leads = currStudio.getActiveScene().getRoles();
		      ArrayList<Object> available = new ArrayList<Object>();
		      for (int i=0; i < leads.length; i++){
			      if(leads[i].getRequiredRank() <= currPlayer.getRank() && !leads[i].getOccupied()){
				      available.add(leads[i].getCharacterName());
			      }
		      }
		      for (int i=0; i < extras.size(); i++){
           if((extras.get(i).getRequiredRank() <= currPlayer.getRank()) && !extras.get(i).getOccupied()){
            available.add(extras.get(i).getCharacterName());
           }
          }
		      if (available.size() != 0) {
			      Object[] options = new Object[available.size()];
			      for (int i=0; i < options.length; i++){
				      options[i] = available.get(i);
			      }
			      String takeRole = (String) JOptionPane.showInputDialog(currFrame,
                           "Which role do you want to take?",
                           "", JOptionPane.PLAIN_MESSAGE,
                           null, options, "");
			        if (takeRole.length() != 1) {
                currPlayer.takeRole(takeRole);
				        hasWorked = true;
				        endTurn();	
              }
          } else {
          //The current player can't work.
            JOptionPane.showMessageDialog(currFrame, "There are no roles for you to take.");
           }
		    } else {
          JOptionPane.showMessageDialog(currFrame,
          "You aren't allowed to take a role.");
		      }
	      }
	    });
      
      //ACT
      actButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
        	Player currPlayer = game.getPlayers()[turn];
        	if(currPlayer.getEmployed() && !hasWorked){
          		boolean success = currPlayer.act();
			        hasWorked = true;
			        if(!success){
				        JOptionPane.showMessageDialog(currFrame, "You did not succed at acting!");
			        } else {
			    	    JOptionPane.showMessageDialog(currFrame,"You acted successfully!");
			         }
			      endTurn();
		       } else{
			        JOptionPane.showMessageDialog(currFrame,
       				"You aren't allowed to act.");
		        }
	        }
	      });
      //REHEARSE
	  rehearseButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		    Player currPlayer = game.getPlayers()[turn];
         if(currPlayer.getEmployed() && !hasWorked && (currPlayer.getPracticeChips() < currPlayer.getLocation().getActiveScene().getBudget() - 1)){
          currPlayer.rehearse();
			    JOptionPane.showMessageDialog(currFrame,
					"You have rehearsed!");
			    hasWorked = true;
			    endTurn();
		      } else{
            JOptionPane.showMessageDialog(currFrame,"You aren't allowed to rehearse.");
		      }   
	      }
	 });
      //UPGRADE
      upgradeButton.addActionListener(new ActionListener(){
	     public void actionPerformed(ActionEvent e){
        upgrade();  
		   }
	   });
     
      //END TURN
      endButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
			  endTurn();
		  }
	    });

	  //Make the buttons invisible
      moveButton.setOpaque(false);
      moveButton.setContentAreaFilled(false);
      moveButton.setBorderPainted(false);

      workButton.setOpaque(false);
      workButton.setContentAreaFilled(false);
      workButton.setBorderPainted(false);

      actButton.setOpaque(false);
      actButton.setContentAreaFilled(false);
      actButton.setBorderPainted(false);

      rehearseButton.setOpaque(false);
      rehearseButton.setContentAreaFilled(false);
      rehearseButton.setBorderPainted(false);

      upgradeButton.setOpaque(false);
      upgradeButton.setContentAreaFilled(false);
      upgradeButton.setBorderPainted(false);

      endButton.setOpaque(false);
      endButton.setContentAreaFilled(false);
      endButton.setBorderPainted(false);

	    //Add the buttons to the frame
	    currFrame.add(moveButton);
	    currFrame.add(workButton);
	    currFrame.add(actButton);
	    currFrame.add(rehearseButton);
	    currFrame.add(upgradeButton);
	    currFrame.add(endButton);
    }
   
	private void endTurn() {
   //ends the game
    turn = (turn + 1) % game.getNumPlayers();
    //Reset the booleans
    hasMoved = false;
    hasWorked = false;
		Point sbPoint = new Point(957, 49+(90*turn));
    view.getTurnIndicator().setLocation(sbPoint);
  }

   private void upgrade(){
		//get the player whose turn it is
		Player currPlayer = game.getPlayer(turn);
		//check their location
		Studio currLocation = currPlayer.getLocation();
		String studioName = currLocation.getName();
		if (studioName.equals("Casting Office")){
	    //player chose to upgrade
			Object[] options = {"Dollars",
	                    "Credits",};
			int choice = JOptionPane.showOptionDialog(currFrame,
			    "Would you like to use dollars or credits?",
			    "",
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[1]);
      if (choice ==  0) {
        upgradeDollars(currPlayer);
      } else{
        upgradeCredits(currPlayer);
      }
			//if statement that depends on the value of n and calls a the appopriate function	
  } else {
			//player must be in the casting office to upgrade
			JOptionPane.showMessageDialog(currFrame, "You must be in the Casting Office to upgrade.");
		}
   }


   private void upgradeDollars(Player currPlayer){
    int upgradeDollars[] = {0, 0, 4, 10, 18, 8, 40};
    //a new dialog box with a drop down menu that gets the desired rank from
    //the 
    Object[] levels = {2,3,4,5,6};
    int rank = (int) JOptionPane.showInputDialog(currFrame, "What rank would you like to increase to?", "", JOptionPane.PLAIN_MESSAGE, null, levels, "");
    int dollars = currPlayer.getDollars();
    if (dollars >= upgradeDollars[rank] && (rank > currPlayer.getRank())){
      currPlayer.setDollars(dollars-upgradeDollars[rank]);
      currPlayer.setRank(rank);
    } else {
      //they were unable to upgrade
      JOptionPane.showMessageDialog(currFrame, "You are unable to upgrade");
    }
   }

  private void upgradeCredits(Player currPlayer){
    int upgradeCredits[] =  {0, 0, 5, 10, 15, 20, 25};
    Object[] levels = {2,3,4,5,6};
    int rank = (int) JOptionPane.showInputDialog(currFrame, "What rank would you like to increase to?", "", JOptionPane.PLAIN_MESSAGE, null, levels, "");
    int credits = currPlayer.getCredits();
    if (credits >= upgradeCredits[rank] && (rank > currPlayer.getRank())){
      currPlayer.setCredits(credits-upgradeCredits[rank]);
      currPlayer.setRank(rank);
    } else {
      //they were unable to upgrade
      JOptionPane.showMessageDialog(currFrame, "You are unable to upgrade");
    }
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
}
