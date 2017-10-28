
import java.util.*;
import java.awt.*;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;

public class View extends JLayeredPane implements Observer {
  private JLabel boardLabel;
  private ImageIcon boardJPG; //actual board picture
	private JLabel dayCounter;
	private JLabel[] scoreBoard;
	private JLabel turnIndicator;
	private JLabel boardBG;
	private JLabel cardBackLabel;
	private ImageIcon cardBack;
	private JLabel[] cardBacks;
	private JLabel[] cards;
	private ImageIcon cardFront;
	private Game game;

	public View() throws Exception{
	  boardLabel = new JLabel();
	  //Add the Board
    Class<?> cls = getClass();
    boardJPG = new ImageIcon(ImageIO.read(cls.getResourceAsStream("resources/finalBoard.jpg")));
	  boardLabel.setIcon(boardJPG);
    add(boardLabel, new Integer(0));
	  boardLabel.setBounds(0, 47, boardJPG.getIconWidth(), boardJPG.getIconHeight());
	  scoreBoard = null;
	  boardBG = new JLabel();
	  boardBG.setBackground(new Color(178, 114, 68));
	  boardBG.setBounds(960, 47, 144, 720);
	  boardBG.setOpaque(true);
	  add(boardBG, new Integer(0));
	  
	  //Add the menu image
	  JLabel menuLabel = new JLabel();
	  ImageIcon menuPNG = new ImageIcon(ImageIO.read(cls.getResourceAsStream("resources/menu.png")));
	  menuLabel.setIcon(menuPNG);
    add(menuLabel, new Integer(0));
	  menuLabel.setBounds(0, 0, menuPNG.getIconWidth(), menuPNG.getIconHeight());

    //Add the Title image
	  JLabel titleLabel = new JLabel();
	  ImageIcon titlePNG = new ImageIcon(ImageIO.read(cls.getResourceAsStream("resources/NTrp42v.png")));
	  titleLabel.setIcon(titlePNG);
    add(titleLabel, new Integer(0));
	  titleLabel.setBounds(904, 0, titlePNG.getIconWidth(), titlePNG.getIconHeight());
      
	  //Create the card back image
    cardBacks = new JLabel[10];
	  for(int i = 0; i < 10; i++)
		cardBacks[i] = new JLabel();

	  cardBack = new ImageIcon(ImageIO.read(cls.getResourceAsStream("resources/cardBack.png")));
	  for(int i = 0; i < 10; i++)
		  cardBacks[i].setIcon(cardBack);

	  //Create cards array
    cards = new JLabel[41];
	  for(int i = 1; i < 41; i++){
      cards[i] = new JLabel();
	    //Read each image in
		  cardFront = new ImageIcon(ImageIO.read(cls.getResourceAsStream("resources/cards/" + i + ".png"))); 
		  cards[i].setIcon(cardFront);
	  }
	}

	public void initObserver() {
		Player[] player = game.getPlayers();
		Studio[] studio = game.getBoard().getStudioList();
		game.addObserver(this);
		for (int i=0; i < player.length; i++) {
			player[i].addObserver(this);
		}

		for (int i=0; i < studio.length; i++) {
			studio[i].addObserver(this);
		}
	}


	@Override
	public void update(Observable obs, Object data) {
		if (obs instanceof Player) {
			Player p = (Player) obs;
			if (p.getEmployed()){
				int x = p.getCurrentRole().getX();
				int y = p.getCurrentRole().getY();
				if (p.getCurrentRole().getStarRole()){
					x += 3+p.getLocation().getX();
					y += 4+p.getLocation().getY();
				}
				Point rolePos = new Point(x, y);
				p.getPlayerLabel().setLocation(rolePos);
				updateScoreboard(p.getPlayerNum());
			} else {
				updateScoreboard(p.getPlayerNum());
			}	
		} else if (obs instanceof Studio) {
		  	Studio s = (Studio) obs;
		  	updateShotCounters(s);
		  	if (s.getCurrShotCounters() == 0){
          endScene(s);
          game.decrementActiveScenes();
			}
		} else if (obs instanceof Game) {
			  Game g = (Game) obs;
			  if (g.getDay() == g.getMaxDays()) {
				  g.endGame();
				  int[] scores = g.calculateScore();
				  String scoreTxt = "";
				  for (int i=0; i < scores.length; i++) {
					  scoreTxt = scoreTxt + "<br>" + g.getPlayer(i).getColor() + ": " + scores[i];
				  }
				  scoreTxt = "<html>The game is over!<br>The scores are:<br>" + scoreTxt + "</html>";
				  JOptionPane.showMessageDialog(null, scoreTxt);
				  System.out.println("Goodbye!");
				  System.exit(0);
			  } 	
			  if ((g.getNumActiveScenes() == 1) && g.getActive()) {
				  g.wrapDay();
			  }
		  }
	 }


  public void initScoreboard(int numPlayers) {
	  scoreBoard = new JLabel[numPlayers];
	  Color[] colors = {new Color(0, 128, 255), Color.cyan, new Color(51, 255, 51), new Color(255, 128, 0), new Color(255, 204, 204), Color.red, new Color(204, 0, 204), Color.yellow};
	  int x = 960;//960
	  int y = 52;//47
	  int h = 85;//90
	  int w = 139;//144
	  for (int i = 0; i < numPlayers; i++) {
		  Player p = game.getPlayers()[i];
		  scoreBoard[i] = new JLabel("<html><body><p style=\"font-size:18\">" + p.getColor().toUpperCase() +
					"<p style=\"padding:3; font-size:12\">"+  p.getCredits() + " Credits" + "</p>" +
					"<p style=\"padding:3; font-size:12\">" + "$" + p.getDollars() + "</p>" + 
					"<p style=\"padding:3; font-size:12\">" + p.getPracticeChips() + " Rehearsals<p/></body></html>",
			SwingConstants.CENTER);
	    JLabel score = scoreBoard[i];
	  	y = 52 + (i * 90);
		  score.setBounds(x, y, w, h);
		  score.setBackground(colors[i]);
		  score.setFont(new Font("Serif", Font.BOLD, 32));
		  score.setOpaque(true);
		  add(score, new Integer(3));
	  }
	  turnIndicator = new JLabel();
	  turnIndicator.setBounds(957, 49, 145, 91);
	  turnIndicator.setBackground(new Color(255, 254, 248));
	  turnIndicator.setOpaque(true);
	  add(turnIndicator, new Integer(2));
  }

  private void updateScoreboard(int playerNum) {
	  Player p = game.getPlayers()[playerNum];
	  scoreBoard[playerNum].setText("<html><body><p style=\"font-size:18\">" + p.getColor().toUpperCase() +
                                        "<p style=\"padding:3; font-size:12\">"+  p.getCredits() + " Credits" + "</p>" +
                                        "<p style=\"padding:3; font-size:12\">" + "$" + p.getDollars() + "</p>" + 
                                        "<p style=\"padding:3; font-size:12\">" + p.getPracticeChips() + " Rehearsals<p/></body></html>");
  }

  // function to add the player tokens
  public void setPlayerTokens(int playerNum, JLabel playerLabel, int x, int y, int height, int width){
     //set bounds
      playerLabel.setBounds(x, y, height, width);
     //add to board
      add(playerLabel, new Integer(6));
  } 

  public int getBoardHeight(){
    return boardJPG.getIconHeight();
  }

  //Sets the backs of the cards to their spots.
  public void setCards(){
    //Create a new Jlabel for each room and add the card back on the scene
    //Train Station
	  add(cardBacks[0], new Integer(1));
	  cardBacks[0].setBounds(13, 98, cardBack.getIconWidth(), cardBack.getIconHeight());

    //Jail
	  add(cardBacks[1], new Integer(1));
   	cardBacks[1].setBounds(221, 64, cardBack.getIconWidth(), cardBack.getIconHeight());

    //Main Street
	  add(cardBacks[2], new Integer(1));
  	cardBacks[2].setBounds(770, 64, cardBack.getIconWidth(), cardBack.getIconHeight());
    
    //General Store
	  add(cardBacks[3], new Integer(1));
   	cardBacks[3].setBounds(291, 268, cardBack.getIconWidth(), cardBack.getIconHeight());
  
    //Saloon
	  add(cardBacks[4], new Integer(1));
   	cardBacks[4].setBounds(501, 267, cardBack.getIconWidth(), cardBack.getIconHeight());
  
    //Ranch
	  add(cardBacks[5], new Integer(1));
   	cardBacks[5].setBounds(197, 425, cardBack.getIconWidth(), cardBack.getIconHeight());

    //Bank
	  add(cardBacks[6], new Integer(1));
   	cardBacks[6].setBounds(494, 423, cardBack.getIconWidth(), cardBack.getIconHeight());

    //Hotel
	  add(cardBacks[7], new Integer(1));
   	cardBacks[7].setBounds(770, 635, cardBack.getIconWidth(), cardBack.getIconHeight());

    //Secret Hiedout
	  add(cardBacks[8], new Integer(1));
   	cardBacks[8].setBounds(17, 628, cardBack.getIconWidth(), cardBack.getIconHeight());
    
    //Church
	  add(cardBacks[9], new Integer(1));
   	cardBacks[9].setBounds(494, 630, cardBack.getIconWidth(), cardBack.getIconHeight());
  }

  public JLabel getTurnIndicator(){
  	return turnIndicator;
  }

  public void setGame(Game newGame){
    game = newGame;
  }

  public void resetCardBacks(){
    for(int i = 0; i < 10; i++){
      cardBacks[i].setVisible(true);
	  }
  }

  public void initShotCounters(){
    Studio[] stds = game.getBoard().getStudioList();
	  for(int i = 0; i < 12; i++){
      JLabel[] curr = stds[i].getShotCountView();
	    for(int j = 0; j < curr.length; j++){
        add(curr[j], new Integer(2));
	  	}
	  }
  }

  public void flipCard(int C, String roomName){
	  int room = getRoomNum(roomName);
	  cardBacks[room].setVisible(false);
	  add(cards[C], new Integer(2));
	  cards[C].setBounds(cardBacks[room].getX(), cardBacks[room].getY(), cardFront.getIconWidth(), cardFront.getIconHeight());
  }

  private int getRoomNum(String name){
    int num = 0;
	  switch(name){
	    case "Train Station":
	      break;
	    case "Jail":
		    num = 1;
		    break;
	    case "Main Street":
		    num = 2;
		    break;
	    case "General Store":
		    num = 3;
		    break;
	    case "Saloon":
		    num = 4;
		    break;
	    case "Ranch":
		    num = 5;
		    break;
	    case "Bank":
		    num = 6;
		    break;
	    case "Hotel":
	    	num = 7;
	    	break;
	    case "Secret Hideout":
		    num = 8;
		    break;
	    case "Church":
		    num = 9;
		    break;
	  }
	return num;
  }

  public void movePlayer(Player player, int flag){
    if (flag == 1){
      Board b = game.getBoard();
      Studio[] studios = b.getStudioList();
      Studio currStudio = studios[0];
	    Point[] spots = currStudio.getFreeSpaces();
	    int mySpot = player.getPlayerNum();
	    Point myPoint = spots[mySpot];
      player.getPlayerLabel().setLocation(myPoint); 
    } else { 
        Studio currStudio = player.getLocation();
	      Point[] spots = currStudio.getFreeSpaces();
	      int mySpot = currStudio.getCurrentPlayers().size();
	      Point myPoint = spots[mySpot - 1];
        player.getPlayerLabel().setLocation(myPoint);
      }
  
  } 

  private void updateShotCounters(Studio studio){
    int diffShots = studio.getMaxShotCounters() - studio.getCurrShotCounters();
    JLabel[] curr = studio.getShotCountView();
    for(int i = 0; i < diffShots; i++){
	    curr[i].setVisible(false);
	  }
  }

  private void endScene(Studio studio){
    studio.endScene();
    int roomNum = getRoomNum(studio.getName());
	//Move current players to room
    Player[] p = game.getPlayers();
	  for(int i = 0; i < p.length; i++){
      Studio currLocation = p[i].getLocation();
	    if(studio.getName().equals(currLocation.getName())){
        String c = studio.getActiveScene().getFile();	    
	      int cnum = Integer.parseInt(c.substring(0,c.length() - 4));
	      cards[cnum].setVisible(false);
		    mPlayer(p[i]);
	     }
	  }
  }

  public void mPlayer(Player player){
    Studio currStudio = player.getLocation();
	  Point[] spots = currStudio.getFreeSpaces();
	  ArrayList<Integer> players = currStudio.getCurrentPlayers();
	  int numPlayers = players.size();
	  Point myPoint = null;
	  for (int i=0; i < numPlayers; i++){
		  if (players.get(i) == player.getPlayerNum()){
			  myPoint = spots[i];
			  break;
		  }
	  }
    player.getPlayerLabel().setLocation(myPoint);
  }
}
