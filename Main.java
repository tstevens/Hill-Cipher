package hill_cipher;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

/**
 *
 * @author trevorstevens
 */

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DoubleMatrix Key;
        DoubleMatrix InverseKey;
        Key = new DoubleMatrix(new double[][]{
                    {5,8},
                    {17,3}
        });

        ArrayList<String> dictionary = loadDictionary("/Users/trevorstevens/Desktop/dictionary4.txt");

        String plainText = "GOAT";
        System.out.println(plainText);

        String cipherText = Main.encrypt(plainText, Key);
        System.out.println(cipherText);

        if(Key.getRows() == 2 ){
            InverseKey = invertMatrix2x2Prep(Key);
            InverseKey = modMatrixInverse(InverseKey.mmul(-1 * inverse(Math.abs((int)determinantOfMatrix(InverseKey)), 26)));
        } else {
            DoubleMatrix TransKey = Key.transpose();
            InverseKey = modMatrixInverse(TransKey.mmul(-1 * inverse(Math.abs((int)determinantOfMatrix(TransKey)), 26)));
        }


        String DecryptedPlainText = Main.decrypt(cipherText, InverseKey);
        System.out.println(DecryptedPlainText);

        System.out.println(bruteForce(cipherText, dictionary));

    }

    public static String encrypt(String plainText, DoubleMatrix key) {
        ArrayList<DoubleMatrix> cipherChunks = splitToMatrix(plainText, key.getRows());

        StringBuilder resultBuilder = new StringBuilder();
        for (DoubleMatrix chunk : cipherChunks) {
            DoubleMatrix result = key.mmul(chunk);
            resultBuilder.append(matrixToString(modChunkMatrix(result)));
        }

        return resultBuilder.toString();
    }

    public static String decrypt(String cipherText, DoubleMatrix key) {
        ArrayList<DoubleMatrix> encodedChunks = splitToMatrix(cipherText, key.getRows());

        StringBuilder resultBuilder = new StringBuilder();
        for (DoubleMatrix chunk : encodedChunks) {
            DoubleMatrix result = key.mmul(chunk);
            resultBuilder.append(matrixToString(modChunkMatrix(result)));
        }
        return resultBuilder.toString();
    }

    private static ArrayList<DoubleMatrix> splitToMatrix(String plainText, int splitValue) {
        if (splitValue == 2 || splitValue == 3) {
            ArrayList<DoubleMatrix> chunks = new ArrayList<DoubleMatrix>();
            for (int i = 0, sub = splitValue; sub <= plainText.length(); i += splitValue, sub += splitValue) {
                char[] chunk = plainText.substring(i, sub).toCharArray();
                DoubleMatrix temp = stringToMatrix(chunk);
                chunks.add(temp);
//             TODO Parse non even length strings
//                if(i+splitValue > plainText.length()){
//                    if (splitValue == 3){
//                        if((plainText.length() - sub) == 2){
//                            char[] fill = {plainText.substring(sub, sub+1).toCharArray()[0], 'A', 'A'};
//                            temp = stringToMatrix(fill);
//                        } else {
//                            char[] fill = {plainText.substring(i, sub+2).toCharArray()[0], 'A'};
//                            temp = stringToMatrix(fill);
//                        }
//                        chunks.add(temp);
//                    } else {
//                        char[] fill = {plainText.substring(i, sub).toCharArray()[0], 'A'};
//                        temp = stringToMatrix(fill);
//                        chunks.add(temp);
//                    }
//                }
            }
            return chunks;
        } else {
            return null;
        }
    }

    private static DoubleMatrix modChunkMatrix(DoubleMatrix matrix) {
        DoubleMatrix modMatrix = null;
        if (matrix.getRows() == 3) {
            modMatrix = new DoubleMatrix(new double[]{matrix.get(0) % 26, matrix.get(1) % 26, matrix.get(2) % 26});
        } else {
            modMatrix = new DoubleMatrix(new double[]{matrix.get(0) % 26, matrix.get(1) % 26});
        }
        return modMatrix;
    }

    private static DoubleMatrix modMatrixInverse(DoubleMatrix matrix) {
        DoubleMatrix modMatrix = null;
        if (matrix.getRows() == 3) {
            modMatrix = new DoubleMatrix(new double[][]{{matrix.get(0) % 26, matrix.get(1) % 26, matrix.get(2) % 26},
                                                      {matrix.get(3) % 26, matrix.get(4) % 26, matrix.get(5) % 26},
                                                      {matrix.get(6) % 26, matrix.get(7) % 26, matrix.get(8) % 26}
            });
            //Ugly.. but works to get around java implementation of mod
            double a = modMatrix.get(0);
            double b = modMatrix.get(1);
            double c = modMatrix.get(2);
            double d = modMatrix.get(3);
            double e = modMatrix.get(4);
            double f = modMatrix.get(5);
            double g = modMatrix.get(6);
            double h = modMatrix.get(7);
            double i = modMatrix.get(8);

            if(modMatrix.get(0) < 0){
                a = (modMatrix.get(0) + 26);
            }
            if(modMatrix.get(1) < 0){
                b = (modMatrix.get(1) + 26);
            }
            if(modMatrix.get(2) < 0){
                c = (modMatrix.get(2) + 26);
            }
            if(modMatrix.get(3) < 0){
                d = (modMatrix.get(3) + 26);
            }
            if(modMatrix.get(4) < 0){
                e = (modMatrix.get(4) + 26);
            }
            if(modMatrix.get(5) < 0){
                f = (modMatrix.get(5) + 26);
            }
            if(modMatrix.get(6) < 0){
                g = (modMatrix.get(6) + 26);
            }
            if(modMatrix.get(7) < 0){
                h = (modMatrix.get(7) + 26);
            }
            if(modMatrix.get(8) < 0){
                i = (modMatrix.get(8) + 26);
            }

            modMatrix = new DoubleMatrix(new double[][]{{a, b, c},
                                                        {d, e, f},
                                                        {g, h, i}
            });
        } else {
            modMatrix = new DoubleMatrix(new double[][]{{matrix.get(0) % 26, matrix.get(1) % 26},
                                                      {matrix.get(2) % 26, matrix.get(3) % 26}
            });
            //Ugly.. but works to get around java implementation of mod
            double a = modMatrix.get(0);
            double b = modMatrix.get(1);
            double c = modMatrix.get(2);
            double d = modMatrix.get(3);
            if(modMatrix.get(0) < 0){
                a = (modMatrix.get(0) + 26);
            }
            if(modMatrix.get(1) < 0){
                b = (modMatrix.get(1) + 26);
            }
            if(modMatrix.get(2) < 0){
                c = (modMatrix.get(2) + 26);
            }
            if(modMatrix.get(3) < 0){
                d = (modMatrix.get(3) + 26);
            }
            modMatrix = new DoubleMatrix(new double[][]{{a, b},
                                                        {c, d}
            });
        }


        return modMatrix;
    }

    private static DoubleMatrix stringToMatrix(char[] chunk) {
        DoubleMatrix temp = null;
        if (chunk.length == 3) {
            temp = new DoubleMatrix(new double[]{chunk[0] - 'A', chunk[1] - 'A', chunk[2] - 'A'});
        } else {
            temp = new DoubleMatrix(new double[]{chunk[0] - 'A', chunk[1] - 'A'});
        }
        return temp;
    }

    private static String matrixToString(DoubleMatrix matrix) {
        char[] a = null;
        if(matrix.getRows() == 3){
            a = new char[] {(char) (matrix.get(0) + 'A'), (char) (matrix.get(1) + 'A'), (char) (matrix.get(2) + 'A')};
        } else {
            a = new char[] {(char) (matrix.get(0) + 'A'), (char) (matrix.get(1) + 'A')};
        }

        return new String(a);
    }

    private static DoubleMatrix keyInverse(DoubleMatrix matrix) {
        DoubleMatrix result = null;
        if (matrix.isSquare()) {
            DoubleMatrix ident = DoubleMatrix.eye(matrix.getRows());
            result = Solve.solve(matrix, ident);
        }
        return result;
    }

    private static double determinantOfMatrix(DoubleMatrix matrix){
        if(matrix.getRows() == 3){
            return (matrix.get(0,0)* matrix.get(1,1)* matrix.get(2,2)) +
            (matrix.get(1,0)* matrix.get(2,1)* matrix.get(0,2)) +
            (matrix.get(2,0)* matrix.get(0,1)* matrix.get(1,2)) -
            (matrix.get(0,0)* matrix.get(2,1)* matrix.get(1,2)) -
            (matrix.get(1,0)* matrix.get(0,1)* matrix.get(2,2)) -
            (matrix.get(2,0)* matrix.get(1,1)* matrix.get(0,2));
        } else {
            return (matrix.get(0,0)*matrix.get(1,1)) - (matrix.get(0,1)* matrix.get(1,0));
        }
    }

    private static int[] gcd(int p, int q) {
        if (q == 0) {
            return new int[]{p, 1, 0};
        }
        int[] vals = gcd(q, p % q);
        int d = vals[0];
        int a = vals[2];
        int b = vals[1] - (p / q) * vals[2];
        return new int[]{d, a, b};
    }

    private static int inverse(int k, int n) {
        int[] vals = gcd(k, n);
        int d = vals[0];
        int a = vals[1];
        int b = vals[2];
        if (d > 1) {
            System.out.println("Inverse does not exist.");
            return 0;
        }
        if (a > 0) {
            return a;
        }
        return n + a;
    }

    private static DoubleMatrix invertMatrix2x2Prep(DoubleMatrix matrix){
        DoubleMatrix tmp = null;
        if(matrix.getRows() == 2 ){
               tmp =  new DoubleMatrix(new double[][]{
                                {matrix.get(1,1), -1*matrix.get(0,1)},
                                {-1*matrix.get(1,0), matrix.get(0,0)}
               });
        }
        return tmp;
    }

    public static String bruteForce(String eText, ArrayList<String> dictionary){
        if(eText.length() == 3)
            return bruteForceDim3(eText, dictionary);
        else
            return bruteForceDim2(eText, dictionary);
    }

    private static String bruteForceDim3(String eText, ArrayList<String> dictionary){

        double[] keyArray = new double[9];
        boolean flag = true;
        double det = 0;
        DoubleMatrix keyMatrix;
        String dText;

        while(flag){

            keyMatrix = new DoubleMatrix(3, 3, keyArray); //create our matrix

            if((det = determinantOfMatrix(keyMatrix)) != 0.0 && (det % 2 != 0) && (det % 13 != 0) &&
                                                (dictionary.contains(dText = decrypt(eText, keyMatrix))))
                return dText; //we found a word, return it

            if ((keyArray = incrementArray(keyArray)) == null) //if we have tested all combos to no avail
                flag = false; //end loop, return null
        }

        return null; //fail condition (no word found)
    }

    private static String bruteForceDim2(String eText, ArrayList<String> dictionary){

        double[] keyArray = new double[4];
        boolean flag = true;
        double det = 0;
        DoubleMatrix keyMatrix;
        String dText;

        while(flag){

            keyMatrix = new DoubleMatrix(2, 2, keyArray); //create our matrix

            if((det = determinantOfMatrix(keyMatrix)) != 0 && (det % 2 != 0) && (det % 13 != 0) &&
                                                (dictionary.contains(dText = decrypt(eText, keyMatrix))))
                return dText; //we found a word, return it

            if ((keyArray = incrementArray(keyArray)) == null) //if we have tested all combos to no avail
                flag = false; //end loop, return null
        }

        return null; //fail condition (no word found)
    }

    private static double[] incrementArray(double[] keyArray){

        boolean flag = true;
    	int i = 0;

    	while(flag){
    		if(keyArray[i] == 25){
                    keyArray[i] = 0;
    			i++;
    		}
    		else{
    			keyArray[i] += 1;
    			flag = false;
    		}
                if(i == keyArray.length)
                    return null;
    	}
        return keyArray;
    }

    private static ArrayList<String> loadDictionary(String filename){
        ArrayList<String>  dictionary = new ArrayList<String>();

        try{
        Scanner inReader = new Scanner(new File(filename));

        while(inReader.hasNext()){
            //System.out.println(inReader.next());
            dictionary.add(inReader.next());
        }
        inReader.close();
        }
        catch(Exception e){
            System.out.println("Error: cannot find dictionary!");
        }
        return dictionary;


    }
}
