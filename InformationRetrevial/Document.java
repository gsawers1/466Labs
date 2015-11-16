

import java.util.Vector;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.json.simple.JSONObject;

/**
 * Created by Greg Sawers & Ryan Daley
 */
public class Document implements Comparable<Document>{

    int id;
    String utterance;
    String first;
    String last;
    String type;
    String house;
    String committee;
    String date;
    String origUtterance;
    double score;
    
    private double max;
    private Vector<String> keywords;

    public TextHandler textHandler;
    public HashMap<String, Double> rawTermFrequencies = new HashMap<String,Double>();
    HashMap<String, Double> normalizedTermFrequencies = new HashMap<String, Double>();
    HashMap<String, Double> keywordWeights = new HashMap<String, Double>();
    public ArrayList<String> uniqueWords = new ArrayList<String>();
    public double length = 0;

    public Document(String doc, String orig){
        origUtterance = orig;
        utterance = doc;
        byte[] bytes = null;
        try {
            bytes = doc.getBytes("UTF-8");
        }
        catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        length = bytes.length;

        textHandler = new TextHandler(doc);
        textHandler.parseFile();
        uniqueWords = textHandler.getUniqueWordList();
        generateRawFrequencies();
    }
    
    /**
     * 
     * @param o JSONObject to build a document from
     */
    public Document(JSONObject o) {
        utterance = (String)o.get("text");
        date = (String)o.get("date");
        type = (String)o.get("type");
        house = (String)o.get("house");
        committee = (String)o.get("committee");
        first = (String)o.get("first");
        last = (String)o.get("last");
        max = 0;
        keywords = new Vector<String>();
    }
    
    public void generateRawFrequencies(){
        HashMap<String, Double> wordMap = textHandler.getWordMap();

        for(String word: uniqueWords){
            rawTermFrequencies.put(word, wordMap.get(word).doubleValue());
        }

    }

    public void generateNormalizedTermFrequency(double maxFreq, String word){
        if(uniqueWords.contains(word))
            normalizedTermFrequencies.put(word, rawTermFrequencies.get(word)/maxFreq);
    }
    
    public double getCount(String word) {
        if (rawTermFrequencies.containsKey(word)) {
            return rawTermFrequencies.get(word);
        }
        else {
            return 0;
        }
    }

    public void setScore(double nextScore){score = nextScore;}

    public int compareTo(Document other){
        if(other.score > score)
            return 1;
        else
            return -1;
    }
    




}
