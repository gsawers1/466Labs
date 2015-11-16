

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
//import static lab2.Lab2.stemWords;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Ryan Daley & Greg Sawers
 */
public class Preprocessor {

    private ArrayList<Document> documents = new ArrayList<Document>();
    private ArrayList<String> keywords = new ArrayList<String>();
    
    private double avdl;
        
    /**
     * Map of each Document from the list of utterances by PID
     */
    private HashMap<Integer, Document> documentMap = new HashMap<Integer, Document>();
    
    /**
     * Map of each keyword to how many documents it appears in.
     */
    private HashMap<String, Double> wordToDocFrequency = new HashMap<String, Double>();
    
    /**
     * Map of words to its highest frequency in all documents.
     */
    private HashMap<String, Double> highestWordFrequency = new HashMap<String, Double>();
    
    private JSONArray jArray = new JSONArray();
    private String stopFile = "stopFile.txt";
    private boolean printed = false;
    
    /**
     * Object input / Object output streams for saving.
     */

    /*public static void main(String args[]){

        parse();
    }*/
    
     /**
     * 
     * @param s string representing an utterance
     * @return the utterance with all stopwords represented by file StopFile
     */
    private String removeStops(String s) {
        String result = "";
        try{
           Scanner sc = new Scanner(new File(stopFile));
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
               else {
                  if (tok.endsWith(",") || tok.endsWith(".") || tok.endsWith("-")) {
                      tok = tok.substring(0,tok.length() - 1);
                  }
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
    
    /** 
     * 
     * @param s string representing an utterance
     * Also adds new keywords to the keywords vector
     * @return the utterance, but all words have been stemmed if necessary
     */
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
    
    private double calcKeyWordWeight(String word, int document){
        Document doc = documentMap.get(document);
        return doc.normalizedTermFrequencies.get(word) * (Math.log(documentMap.size()/wordToDocFrequency.get(word)));
    }
        
    private double calcKeyWordWeight(String word, Document doc){
        return doc.normalizedTermFrequencies.get(word) * (Math.log(documentMap.size()/wordToDocFrequency.get(word)));
    }

    private double calcKeyWordWeight(String word, Query q){
        if (q.query.length() >= 5) {
           return q.rawTermFrequencies.get(word) * (Math.log(documentMap.size()/wordToDocFrequency.get(word)));
        }
        else {
            return (0.5 + 0.5*q.rawTermFrequencies.get(word)) * (Math.log(documentMap.size()/wordToDocFrequency.get(word)));
        }
        
    }

    
    public ArrayList<Document> calcQueryCosineSimilarity(Query query){

        /**
         * Use a text handler to parse out the query and generate word frequencies
         *
         * Need to add in stopword removal + stemming before the query is passed to the text handler.
         *
         */
        ArrayList<String> queryUniqueWords = query.keywords;
        HashMap<String, Double> queryWordFrequency = query.rawTermFrequencies;

        /**
         * TODO: Make documents comparable so we dont need two arrays
         */
        ArrayList<Document> highestDocuments = new ArrayList<Document>(10);
        ArrayList<Double> scores = new ArrayList<Double>(10);

        for(int i = 0; i < 10; i++){
            scores.add(-100.0);
        }

        for(Document doc : documents) {
            double similarity;
            double sumtop = 0;
            double sumwij = 0;
            double sumwiq = 0;
            for (String qword : queryUniqueWords) {
                if (keywords.contains(qword)) {
                    if (doc.uniqueWords.contains(qword)) {
                        sumtop += calcKeyWordWeight(qword, doc) * calcKeyWordWeight(qword, query);
                        sumwij += calcKeyWordWeight(qword, doc) * calcKeyWordWeight(qword, doc);
                        sumwiq += calcKeyWordWeight(qword, query) * calcKeyWordWeight(qword, query);
                    }
                    else{
                        sumwiq += calcKeyWordWeight(qword, query) * calcKeyWordWeight(qword, query);
                    }
                }
            }

            similarity = sumtop / (Math.sqrt(sumwij * sumwiq));
            doc.score = similarity;
            /**
             * Store top ten results.
             * Should probably just make Documents comparable so we don't need two arrays and
             */
            //if (similarity > 0) {
                if (similarity > scores.get(9)) {
                    scores.set(9, similarity);
                    if(highestDocuments.size() >= 10)
                        highestDocuments.set(9, doc);
                    else
                        highestDocuments.add(doc);

                    Collections.sort(scores);
                    Collections.sort(highestDocuments);
                }
            //}
        }

        return highestDocuments;

    }

    
    private double calcOneOkapi(double df, double dl, double k1, double b, int k2, double fij, double fiq){

        return Math.log((documents.size() - df + 0.5) / (df + 0.5)) *
                ( ((k1 + 1) * fij) / ( k1 * ( 1-b + b * dl/avdl))) *
                ( ((k2 + 1) * fiq) / (k2 + fiq));
    }
    
    /**
     * An alternate method from TF-IDF
     * @param query being handled
     * @return 10 most relevant documents
     */
    
    public ArrayList<Document> calcQueryOkapiScore(Query query){

        /**
         * Use a text handler to parse out the query and generate word frequencies
         *
         * Need to add in stopword removal + stemming before the query is passed to the text handler.
         *
         */
        ArrayList<String> queryUniqueWords = query.keywords;
        HashMap<String, Double> queryWordFrequency = query.rawTermFrequencies;

        /**
         * TODO: Make documents comparable so we dont need two arrays
         */
        ArrayList<Document> highestDocuments = new ArrayList<Document>(10);
        ArrayList<Double> scores = new ArrayList<Double>(10);

        for(int i = 0; i < 10; i++){
            scores.add(10.0);
        }

        for(Document doc : documents){
            double okapiScore = 0;
            for(String qword: queryUniqueWords) {
                if(keywords.contains(qword)) {
                    if (doc.uniqueWords.contains(qword)) {
                        okapiScore += calcOneOkapi(wordToDocFrequency.get(qword), doc.length, 1.5, 0.75, 100,
                                doc.rawTermFrequencies.get(qword), queryWordFrequency.get(qword));
                    }
                    else {
                        okapiScore += calcOneOkapi(wordToDocFrequency.get(qword), doc.length, 1.5, 0.75, 100,
                                0, queryWordFrequency.get(qword));
                    }
                }
            }
            doc.score = okapiScore;

            /**
             * Store top ten results.
             * Should probably just make Documents comparable so we don't need two arrays and
             */
            if (okapiScore > scores.get(9)) {
                scores.set(9, okapiScore);
                if(highestDocuments.size() >= 10)
                    highestDocuments.set(9, doc);
                else
                    highestDocuments.add(doc);

                Collections.sort(scores);
                Collections.sort(highestDocuments);
            }

        }
        return highestDocuments;
    }
    

    /**
     * parse the JSON File
     */
    public void parse(){
        int i = 0;
        Document next;
        ArrayList<String> nextUniqueWords;
        HashMap<String, Double> nextRawTermFrequencies;
        JSONParser parser = new JSONParser();
        String preprocess;

        try {
            jArray = (JSONArray) parser.parse(new FileReader("SB277Utter.json"));
        /**
         * Parsing method that generates Document objects.
         * Also tracks how many times a keyword appears in all Documents, and its highest frequency seen.
         */
            while(i < jArray.size()) {
                JSONObject j = (JSONObject) jArray.get(i);
                /**
                 * Put stopword removal / stemming here instead
                 */
                String u = (String) j.get("text");
                preprocess = stemWords(removeStops(u.toLowerCase()));
                next = new Document(preprocess, (String)j.get("text"));
                documentMap.put(i, next);
                documents.add(next);
                nextUniqueWords = next.uniqueWords;
                nextRawTermFrequencies = next.rawTermFrequencies;
                avdl += next.length;

                for(String word : nextUniqueWords){
                /**
                 * Add stemming + stop word removal here.
                 */
                    if(!keywords.contains(word)){
                        keywords.add(word);

                        wordToDocFrequency.put(word, 1.0);
                        highestWordFrequency.put(word, nextRawTermFrequencies.get(word));
                    } else {
                        wordToDocFrequency.put(word, wordToDocFrequency.get(word) + 1);
                        if(nextRawTermFrequencies.get(word) > highestWordFrequency.get(word)){
                            highestWordFrequency.put(word, nextRawTermFrequencies.get(word));
                        }
                    }
                }

                i++;
            }

            avdl /= documents.size();

        /**
         * Generate Normalized term frequencies for each keyword in each document
         */
            for(i = 0; i < documentMap.size(); i++){
                next = documentMap.get(i);
                for(String word : keywords)
                    next.generateNormalizedTermFrequency(highestWordFrequency.get(word), word);

            }
            
            /**
            * Generates keyword weights in each document
            */
            for (int j = 0; j < documents.size(); j++) {
                Document d = documents.get(j);
                for (String s : d.uniqueWords) {
                    d.keywordWeights.put(s, calcKeyWordWeight(s, j));
                }
            }
            //System.out.println("Size of docs: " + documents.size());
        }
        
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Please put SB277Utter.json within working directory");
        }
    }


}
