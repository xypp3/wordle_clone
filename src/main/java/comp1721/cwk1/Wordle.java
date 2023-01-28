// Main program for COMP1721 Coursework 1
// DO NOT CHANGE THIS!

package comp1721.cwk1;

import java.io.IOException;


public class Wordle {
  public static void main(String[] args) throws IOException {
    Game game;
    int argNum = 0;
    // if accessible flag is present
    if (args.length > 0 && "-a".equals(args[argNum])){
      argNum ++;
    }
    if (args.length > 0) {
      // Player wants to specify the game
      game = new Game(Integer.parseInt(args[argNum]), "data/words.txt");
    }
    else {
      // Play today's game
      game = new Game("data/words.txt");
    }

    // set accessible mode
    if (argNum == 1){
      game.setAccessible();
    }
    game.play();
    game.save("build/lastgame.txt");
    //additional
    game.historicalData("data/history.txt");
  }
}
