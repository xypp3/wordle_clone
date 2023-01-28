package comp1721.cwk1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Guess {
  private int guessNumber;
  private String chosenWord;
  // Use this to get player input in readFromPlayer()
  private static final Scanner INPUT = new Scanner(System.in);

  public Guess(int num){
    // num validation
    if (1 <= num && num <= 6){
      this.guessNumber = num;
    }else {
      //todo find error msg
      throw new GameException("todo find proper: num not between 1 and 6");
    }
  }

  public Guess(int num, String word){
    // num validation
    if (1 <= num && num <= 6){
      this.guessNumber = num;
    }else {
      //todo find error msg
      throw new GameException("todo find proper: num not between 1 and 6");
    }
    // word validation
    if(word.length() != 5){
      //todo find proper msg
      throw new GameException("todo find proper: empty word");
    }else{
      // check if all letters are alphabetic
      for(int i = 0; i < word.length(); i++){
        if (!Character.isAlphabetic(word.charAt(i))){
          //todo: find proper msg
          throw new GameException("todo find proper: non alphabetic");
        }
      }
      // if it has passed all the checks set word
      this.chosenWord = word.toUpperCase();
    }
  }

  public int getGuessNumber() {
    return guessNumber;
  }

  public String getChosenWord() {
    return chosenWord;
  }

  public void readFromPlayer() {
    this.chosenWord = INPUT.nextLine().toUpperCase();
  }

  public String compareWith(String target){
    String[] output = {".", ".", ".", ".", "."};
    // by doing a separate for loop to fix rupee bug, bug is when chosenWord = eeeee, then yellow yellow grey grey grey
    for (int i = 0; i < this.chosenWord.length(); i++) {
      // if correct letter right position
      if(target.charAt(i) == this.chosenWord.charAt(i)) {
        // green
        output[i] = String.format("\033[30;102m %c \033[0m", this.chosenWord.charAt(i));
        // double letter bug: to get rid of error where a green letter is recognized later again and coloured yellow
        target = target.substring(0, i) + '-' + target.substring(i + 1);
      }
    }

    for (int i = 0; i < this.chosenWord.length(); i++){
        // go through to find if correct letter wrong position
        for(int j = 0; j < target.length(); j++){
          // iterate through all target letters on chosen word letter's position
          if(target.charAt(j) == this.chosenWord.charAt(i)){
            // yellow
            output[i] = String.format("\033[30;103m %c \033[0m", this.chosenWord.charAt(i));
            target = target.substring(0, j) + '-' + target.substring(j+1);
            break;
          }
        }
        //else wrong letter wrong position
        if(target.charAt(i) != '-') {
          //grey
          output[i] = String.format("\033[30;107m %c \033[0m", this.chosenWord.charAt(i));
        }
    }

    return String.join("", output);
  }

  public boolean matches(String target){
    return target.equals(this.chosenWord);
  }
}
