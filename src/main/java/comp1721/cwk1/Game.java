package comp1721.cwk1;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;

public class Game {
    private int gameNumber;
    private String target;
    private String[] gameHistory = new String[7];
    private boolean isAccessible = false;
    private int guessNumber;

    public Game(String filename) throws IOException {
        WordList wordList = new WordList(filename);
        // start date of wordle 19th of June 2021
        this.gameNumber = (int) DAYS.between(LocalDate.of(2021, 6, 19), LocalDate.now());
        this.target = wordList.getWord(this.gameNumber);
    }

    public Game(int num, String filename) throws IOException {
        WordList wordList = new WordList(filename);
        this.gameNumber = num;
        this.target = wordList.getWord(num);
    }

    public void play() {
        //setup
        this.guessNumber = 1;
        String colouredGuess, processedGuess;

        System.out.println("WORDLE " + this.gameNumber + "\n");
        // game loop
        while (this.guessNumber <= 6) {
            Guess guess = new Guess(this.guessNumber);
            // take player guess
            System.out.printf("Enter guess (%d/6): ", this.guessNumber);
            guess.readFromPlayer();
            // compare to today's word
            colouredGuess = guess.compareWith(this.target);
            processedGuess = (this.isAccessible)?parseColour(colouredGuess):colouredGuess;
            System.out.println(processedGuess);
            gameHistory[this.guessNumber - 1] = processedGuess; // add guess to history
            //win condition
            if (guess.matches(this.target)) {
                break;
            }
            this.guessNumber++;
        }
        // post game dialogue
        String endDialogue;
        if (this.guessNumber == 1) {
            endDialogue = "Superb - Got it in one!";
        } else if (this.guessNumber <= 5) {
            endDialogue = "Well done!";
        } else if (this.guessNumber == 6) {
            endDialogue = "That was a close call!";
        } else {
            endDialogue = "Nope - Better luck next time!\n"+ this.target;
        }
        System.out.println(endDialogue);
        // guessNumber starts at 1 so it cancles with the -1 for index
        gameHistory[this.guessNumber-1] = endDialogue;
    }

    public void save(String filename) throws IOException{
        Path path = Paths.get(filename);
        int counter = 0;
    // writing to file
        try(PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))){
            while (counter < 7 && this.gameHistory[counter] != null) { //short circuits so no err IndexOutOfBounds
                out.println(this.gameHistory[counter]); // every guess on a new line
                counter++;
            }
        }
    }

    public void setAccessible(){
        this.isAccessible = true;
    }

    // accessible mode convertor
    private String parseColour(String word){
        // constants
        int startColourConst = 5;
        int endColourConst = 8;
        int iterationConst = 16;
        // vars
        String colour, letter, output = "";
        int change;
        ArrayList<Integer> yellows = new ArrayList<>();
        ArrayList<Integer> greens = new ArrayList<>();
        boolean isAllGrey = true;

        for (int i = 0; i < 5; i++) {
            change = i*iterationConst;
            colour = word.substring(startColourConst+change,endColourConst+change);
            switch (colour){
                case("102"): //green
                    greens.add(i+1);
                    isAllGrey = false;
                    break;
                case("103"): // yellow
                    yellows.add(i+1);
                    isAllGrey = false;
                    break;
                default:
                    break;
            }
        }
        if (isAllGrey){
            return "All guessed letters cannot be found in the target word";
        }else{
            // describe the positions of yellow guesses
            if (!yellows.isEmpty()){
                output += positionStringBuilder(yellows);
                output += " correct but in wrong place";
            }
            // describe positions of green guesses
            if (!greens.isEmpty()){
                if (!yellows.isEmpty()) {
                    output += ", and ";
                }
                output += positionStringBuilder(greens);
                output += " perfect!";
            }
            return output;
        }
    }

    // convert accessible data into dialogue
    private String positionStringBuilder(ArrayList<Integer> positions){
        String output = "";
        String[] ends = {"", "st", "nd", "rd", "th", "th"};

        if (positions.size() > 1){
            for (int i = 0; i < positions.size()-1; i++) {
                output += positions.get(i)+ends[positions.get(i)]+", ";
            }
            int lastPos = positions.get(positions.size()-1);
            output += "and " + lastPos+ends[lastPos];
        }else{
            output += positions.get(0)+ends[positions.get(0)];
        }
        return output;
    }

    // inputs history.txt AND a regex pattern,,,,,, returns list of all found capture groups
    // make sure the pattern is using the group syntax ('(  )') in regex
    private ArrayList<String> readString(String input, String regStr){
        ArrayList<String> outputs = new ArrayList<>();

        Pattern pattern = Pattern.compile(regStr);
        Matcher matches = pattern.matcher(input);
        while(matches.find()){
            for (int i = 1; i <= matches.groupCount(); i++) {
                outputs.add(matches.group(i));
            }
        }
        return outputs;
    }

    // return newest version of stats, reads calculates and writes to file the wordle stats
    public String historicalData(String filename) throws IOException{


        // const and vars
        String totalGamesPatt = "Number of games played: (\\d+)";
        String totalWinsPatt = "Number of games won: (\\d+)";
        String winStreakPatt = "Length of the current winning streak: (\\d+)";
        String maxWinStreakPatt = "Longest winning streak: (\\d+)";
        String histogramPatt = "\u001B\\[30;102m(\\s*)\u001B\\[0m";
        String histogramDataPatt = "guessed (\\d+) times:";
        String output = "";
        int totalGames = 0, totalWins = 0, winStreak = 0, maxWinStreak = 0;
        double percentWins = 0.0;
        ArrayList<String> histogram = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));
        ArrayList<Integer> histogramData = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));

        // data ingesting
        Path path = Paths.get(filename);
        String file = Files.readString(path);

        if (!file.isEmpty()) {
            totalGames = Integer.parseInt(readString(file, totalGamesPatt).get(0));
            totalWins = Integer.parseInt(readString(file, totalWinsPatt).get(0));
            winStreak = Integer.parseInt(readString(file, winStreakPatt).get(0));
            maxWinStreak = Integer.parseInt(readString(file, maxWinStreakPatt).get(0));
            histogram =  readString(file, histogramPatt);
            for (int i = 0; i < 6; i++) {
                histogramData.set(i, Integer.parseInt(readString(file, histogramDataPatt).get(i)));
            }
        }
        // update data
        totalGames ++;
//        this.gameNumber = 5;
        if(this.gameNumber <= 6){
            winStreak ++;
            totalWins ++;
            histogramData.set(this.gameNumber-1, histogramData.get(this.gameNumber-1)+1);
            histogram.set(this.gameNumber-1, histogram.get(this.gameNumber-1) + "  ");
            if (maxWinStreak < winStreak){
                maxWinStreak = winStreak;
            }
        }else{
            winStreak = 0;
        }
        percentWins = ((double) totalWins/totalGames)*100;
        // prepare output string
        output += String.format("Number of games played: %d\n", totalGames);
        output += String.format("Number of games won: %d\n", totalWins);
        output += String.format("Percentage of games that were wins: %.2f%%\n", percentWins);
        output += String.format("Length of the current winning streak: %d\n", winStreak);
        output += String.format("Longest winning streak: %d\n", maxWinStreak);
        output += "Histogram of guesses (size of histogram bars are relative)\n";
        output += updateHistogram(histogram, histogramData);

        // write data to file
        try(PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))){
            out.println(output);
        }
        return output;
    }

    // return string of histogram data of all types of wins and histogram bars
    private String updateHistogram(ArrayList<String> bars, ArrayList<Integer> totalTimes){
        String output = "";
        bars = isHistogramScalable(bars)?scaleDownHistogram(bars):bars;
        for (int i = 0; i < 6; i++) {
            output += String.format("%d try, guessed %d times: \u001B[30;102m%s\u001B[0m\n", i+1, totalTimes.get(i), bars.get(i));
        }
        return output;
    }

    // return scaled down histogram bars
    private ArrayList<String> scaleDownHistogram(ArrayList<String> bars){
        for (int i = 0; i < 6; i++){
            int newLen = bars.get(i).length()/2 + bars.get(i).length() % 2; // rounds up intentionally
            bars.set(i, new String(new char[newLen]).replace("\0", " "));
        }
        return bars;
    }

    // check if bars in histogram can be scaled down
    private boolean isHistogramScalable(ArrayList<String> histogram){
        for (String bar: histogram) {
            // non reducable size
            if (bar.length() == 2){
                return false;
            }
            // too long
            if (bar.length() > 20){
                return true;
            }
        }
        return false;
    }
}
