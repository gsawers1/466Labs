import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by Greg on 9/22/15.
 */
public class CSVHandler {
    File f;
    List<Vector<Double>> vectorList;
    Scanner fileScan = null;
    Scanner lineScan;

    public CSVHandler(File file){
        f = file;
        vectorList = new ArrayList<Vector<Double>>();
        try {
            fileScan = new Scanner(f);
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public void parseFile() {
        if(f == null){
            System.out.println("File not instantiated");
            return;
        }

        while(fileScan.hasNextLine()){
            String nextLn = fileScan.nextLine();
            lineScan = new Scanner(nextLn);
            lineScan.useDelimiter(",");

            Vector nextV = new Vector<Integer>(1,1);
            while(lineScan.hasNext()){
                String next = lineScan.next();
                Double nextNum;
                if(next.equals(""))
                    nextNum = 0.0;
                else{
                    nextNum = Double.parseDouble(next);
                }
                nextV.add(nextNum);
            }

            vectorList.add(nextV);
        }
    }

    public double computeLength(){
        return 0;
    }

    public double computDotProduct(){
        return 0;
    }

    public double computeEucledianDistance(){
        return 0;
    }

    public double computeManhattanDistance(){
        return 0;
    }

    public double computePearsonCorrelation(){
        return 0;
    }

    public Vector<Double> computeLargestVectorColumn(int columnNum){
        return null;
    }

    public Vector<Double> computeSmallestVectorColumn(int columnNum){
        return null;
    }

    public Vector<Double> computeMeanVectorColumn(int columnNum){
        return null;
    }

    public Vector<Double> computeMedianVectorColumn(int columnNum){
        return null;
    }

    public double computeStandardDevVector(){
        return 0;
    }

    public double computeStandardDevColumn(int columnNum){
        return 0;
    }




}
