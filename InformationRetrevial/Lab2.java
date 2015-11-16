/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.json.simple.*;
import java.io.*;
import java.util.*;
import org.json.simple.parser.JSONParser;

import javax.xml.bind.SchemaOutputResolver;

/**
 *
 * @author Ryan Daley & Greg Sawers
 */
public class Lab2 {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Preprocessor p = new Preprocessor();
        p.parse();        
        Query q;
        boolean cont = true;
        Scanner input = new Scanner(System.in);
        String yesOrNo;
        String oOrC;
        ArrayList<Document> scores;

        System.out.println("Would you like to use cosine similarity or okapi to score queries? (C/O)");
        oOrC = input.nextLine();
        while (cont) {
            System.out.println("Enter a query: ");
            /**
             * We don't need the query class, we can just stem it and stopword it then make a document out of it.
             */
           q = new Query(input.nextLine());

            if(oOrC.equals("O") || oOrC.equals("o")){
                scores = p.calcQueryOkapiScore(q);
            }
            else {
                scores = p.calcQueryCosineSimilarity(q);
            }


            System.out.println("Documents returned from cosine similarity analysis on query are: ");
            for(Document doc : scores){
                System.out.println("Text :"+doc.origUtterance);
            }
            
            System.out.println("Would you like to enter another? Y/N");
            yesOrNo = input.nextLine();
            if (yesOrNo.equals("N") || yesOrNo.equals("n")) {
                cont = false;
            }
        }
    }



    
}
