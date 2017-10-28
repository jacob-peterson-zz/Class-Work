
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.*; //change this later
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;

public class Deadwood {
  private static class Closer extends WindowAdapter {
    public void windowCLosing(WindowEvent e) {
      System.exit(0);
    }
  }


  public static void main(String[] args) throws Exception {

    int numPlayers = 0;
    JFrame frame = new JFrame();
    View board = new View();

    //get screen size of player
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension screenSize = tk.getScreenSize();
    int screenHeight = screenSize.height;
    int screenWidth = screenSize.width;

    //Close program when the x is hit
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Initialize frame
    frame.setTitle("Deadwood");
    frame.setPreferredSize(new Dimension(1108, 795));
    frame.setResizable(false);
    frame.addWindowListener(new Closer());
    frame.add(board);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    //gets the number of players
    //Create the game and pass it to the controller
    numPlayers = welcome();
    Game game = new Game(numPlayers, board);
    Controller gameController = new Controller(frame, game, board);
    specialRules(numPlayers, game);
    game.startGame();
    board.setGame(game);
    board.initShotCounters();
    board.initScoreboard(numPlayers);
    board.initObserver();
    gameController.makeController();

    //call function in view that sets player tokens and displays them
    for(int i=0; i<numPlayers; i++){
      board.setPlayerTokens(i, game.getPlayer(i).getPlayerLabel(), game.getPlayer(i).getX(), game.getPlayer(i).getY(), game.getPlayer(i).getHeight(), game.getPlayer(i).getWidth());
    }
  }
  private static int welcome(){
    //pops up window that says "welcome to the game of deadwood and allows everyone to choose how many players
    JFrame welcomeFrame = new JFrame();
    Object[] possibilities = {"2", "3", "4", "5", "6", "7", "8"};
    String numPlayers = (String)JOptionPane.showInputDialog(
    welcomeFrame,
    "Welcome to Deadwood!\n"
    + "Please choose the number of players ",
    "",
    JOptionPane.PLAIN_MESSAGE,
    null,
    possibilities,
    "");

    welcomeFrame.addWindowListener(new Closer());
    return (Integer.parseInt(numPlayers));
  } 		//returns number of players

  //Displays and changes game rules based on number of players
  public static void specialRules(int numPlayers, Game game){
    Player[] players = game.getPlayers();
    String rules = "";
    switch(numPlayers) {
      case 2:
      case 3:
      rules = "With 2-3 players, you play through 3 days.";
      game.setMaxDays(3);
      break;
      case 4:
      rules = "With 4 players, you play through 4 days.";
      break;
      case 5:
      rules = "With 5 players, you start with 2 credits and play through 4 days.";
      for(int i = 0; i < numPlayers; i++){
        players[i].setCredits(2);
      }
      break;
      case 6:
      rules = "With 6 players, you start with 4 credits and play through 4 days.";
      for(int i = 0; i < numPlayers; i++){
        players[i].setCredits(4);
      }
      break;
      case 7:
      case 8:
      rules = "With 7-8 players you start at rank 2 and play through 4 days.";
      for(int i = 0; i < numPlayers; i++){
        players[i].setRank(2);
      }
      break;
    }
    JFrame rulesFrame = new JFrame();
    JOptionPane.showMessageDialog(rulesFrame, rules);
  }
}//end class
