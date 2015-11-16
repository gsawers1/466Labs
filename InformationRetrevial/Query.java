

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.util.*;

/**
 *
 * @author Ryan Daley & Greg Sawers
 */
public class Query {
    
    public String query;
    public ArrayList<String> keywords = new ArrayList<String>();
    public HashMap<String, Double> rawTermFrequencies = new HashMap<String, Double>();
    private TextHandler textHandler;
    private boolean printed;
    
    public Query(String s) {
        query = s;
        printed = false;
        preprocessQuery();
    }
    
    private String removeStops(String s) {
        String result = "";
        try{
           Scanner sc = new Scanner(new File("stopFile.txt"));
           String stops = "";
           
           while (sc.hasNextLine()) {
               stops = stops.concat(sc.nextLine());
           }
            
           StringTokenizer tk = new StringTokenizer(s);
           while (tk.hasMoreTokens()) {
              String tok = tk.nextToken();
              if (tok.endsWith(".") || tok.endsWith(",") || tok.endsWith("-")) {
                 tok = tok.substring(0,tok.length() - 1);
              }
              if (stops.contains(tok)) {
                   tok = "";
              }
              result = result + " " + tok;
           }
           return result;
        }
        catch (Exception e) {
            if (!printed) {
               System.out.println("stopFile was not found, no words removed");
               printed = true;
            }
            return s;
        }
    }
    
    private String stemWords(String s) {
        Stemmer stem = new Stemmer();
        StringTokenizer tk = new StringTokenizer(s);
        String result = "";
        
        while (tk.hasMoreTokens()) {
            String tok = tk.nextToken();
            if (tok.endsWith(".") || tok.endsWith(",") || tok.endsWith("-")) {
               tok = tok.substring(0,tok.length() - 1);
            }
            char[] arr = tok.toCharArray();
            stem.add(arr, arr.length);
            stem.stem();
            result = result + " " + stem.toString();
        }
        return result;
    }
    
    private void makeKeyVector() {
        StringTokenizer tk = new StringTokenizer(query);
        String s;
        while (tk.hasMoreTokens()) {
            s = tk.nextToken();
            if (!keywords.contains(s)) {
                keywords.add(s);
            }
        }
    }
    
    public void preprocessQuery() {
        query = stemWords(removeStops(query.toLowerCase()));
        textHandler = new TextHandler(query);
        textHandler.parseFile();
        keywords = textHandler.getUniqueWordList();
        generateRawFrequencies();
    }

    private void generateRawFrequencies(){
        HashMap<String, Double> wordMap = textHandler.getWordMap();

        for(String word : keywords){
            rawTermFrequencies.put(word, wordMap.get(word).doubleValue());
        }

    }
    
}
