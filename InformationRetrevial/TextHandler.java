import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Greg on 9/24/15.
 */
public class TextHandler {


    String document;
    Scanner docScan = null;
    //Contains the word as a key and the number of times seen the value
    HashMap<String, Double> wordMap = new HashMap<String, Double>();

    //The list of individual words seen
    ArrayList<String> uniqueWords = new ArrayList<String>();

    //Contains the number of times seen as the key and the list of words seen that many times as the value
    TreeMap<Double, ArrayList<String>> frequencyToWord = new TreeMap<Double, ArrayList<String>>();

    int numwords = 0;
    int numsentences = 0;
    int numparagraphs = 1;

    public TextHandler(String doc){
        document = doc;
        docScan = new Scanner(document);
        
    }

    public void parseFile() {

        String next;
        while(docScan.hasNextLine()){
            String nextLine = docScan.nextLine();
            Scanner lineScan = new Scanner(nextLine);
            int wordCount = 0;
            while(lineScan.hasNext()) {

                next = lineScan.next();
                if ((next.contains(".") || next.contains("!") || next.contains("?") || next.contains("...."))
                        && !next.equals("...") && !next.contains("...")) {
                    numsentences++;
                    next = next.substring(0, next.length() - 1);
                }
                /**
                 * Various punctuation handling statements
                 */
                if (next.contains(",") || next.contains("-") || next.contains(":") || next.contains(";") || next.contains(")"))
                    next = next.substring(0, next.length() - 1);
                if(next.contains("("))
                    next = next.substring(1, next.length());
                if(next.contains("\"")){
                    if(next.charAt(0) == '\"')
                        next = next.substring(1,next.length());
                    else
                        next = next.substring(0, next.length() -1);
                }
                if(next.contains("..."))
                    next = next.substring(0, next.length()-3);

                if (wordMap.containsKey(next)) {
                    wordMap.put(next, wordMap.get(next) + 1);
                } else {
                    wordMap.put(next, 1.0);
                    uniqueWords.add(next);
                }
                numwords++;
                wordCount++;
            }
            if(wordCount == 0)
                numparagraphs++;
        }

        generateWordFrequencies();
    }

    private void generateWordFrequencies(){
        for(String word : uniqueWords){
            double count = wordMap.get(word);
            ArrayList<String> updateList;

            if(frequencyToWord.containsKey(count))
                updateList = frequencyToWord.get(count);
            else
                updateList = new ArrayList<String>();

            updateList.add(word);
            frequencyToWord.put(count, updateList);
        }
    }

    public int getUniqueWords(){
        return wordMap.size();
    }

    public int getNumsentences(){return numsentences;}

    public int getNumparagraphs(){return numparagraphs;}

    public int getNumwords(){
        return numwords;
    }

    public boolean isInDocument(String word){
        return uniqueWords.contains(word);
    }

    public double getWordFrequency(String word){
        if(uniqueWords.contains(word))
            return wordMap.get(word);
        else
            return -1;
    }

    public ArrayList<String> getMostFrequentWords(){
        return frequencyToWord.get(frequencyToWord.lastKey());
    }

    public ArrayList<String> getWordsByFrequency(int freq){
        return frequencyToWord.get(freq);
    }

    public ArrayList<String> getWordsAboveFrequency(double frequency){
        ArrayList<String> words = new ArrayList<String>();
        NavigableMap<Double, ArrayList<String>> sortedFrequencies = frequencyToWord.subMap(frequency, true,
                frequencyToWord.lastKey(), true);

        for(Map.Entry<Double, ArrayList<String>> entry : sortedFrequencies.entrySet()){
            words.addAll(entry.getValue());
        }
        return words;
    }

    public ArrayList<String> getUniqueWordList(){
        return uniqueWords;
    }

    public HashMap<String, Double> getWordMap(){return wordMap;}

}
