package comp1721.cwk1;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class WordList {
    private List<String> words = new ArrayList<>();

    public WordList(String filename) throws IOException {
        Scanner input = new Scanner((Paths.get(filename))); // open file
        // for every new line in file add to list
        while(input.hasNextLine()){
            String tmp = input.nextLine();
            words.add(tmp);
        }

        input.close();
    }

    public int size(){
        return words.size();
    }

    public String getWord(int n){
        // possible indexs 0..(words.size()-1)
        if(0<= n && n < words.size()) {
            return words.get(n);
        }else{
            //todo find proper msg
            throw new GameException("todo find proper: word outside of range");
        }

    }
}
