package tests.searches;

import util.WTFException;

import static training.TrainingDataGenerator.getColorEncoding;
import static training.TrainingDataGenerator.getSliceEncoding;

/**
 * Created by Chris on 4/9/2016.
 */
public class TestTrainingDataGenerator {

        public static boolean test(){
            boolean pass;
            pass = testGetColorEncoding();
            pass &= testGetSliceEncoding();
            return pass;
        }


        public static boolean testGetColorEncoding(){

            boolean pass;

            try {
                pass = getColorEncoding('R').equals("-1,-1,-1,-1,-1,1,");
                pass &= getColorEncoding('O').equals("-1,-1,-1,-1,1,-1,");
                pass &= getColorEncoding('Y').equals("-1,-1,-1,1,-1,-1,");
                pass &= getColorEncoding('G').equals("-1,-1,1,-1,-1,-1,");
                pass &= getColorEncoding('B').equals("-1,1,-1,-1,-1,-1,");
                pass &= getColorEncoding('W').equals("1,-1,-1,-1,-1,-1,");
            }
            catch( WTFException wtf){
                wtf.printStackTrace();
                pass = false;
            }


            String abcs = "abcdefghijklmnopqrstuvwxyzACDEFHIJKLMNPQSTUVXZ0123456789!@#$$%^&&*(),.;'[]\\/<>?:\"{}|+_-=";

            for (int i = 0; i < abcs.length(); i++) {
                try {
                    //This should throw an exception so it never gets to pass = false;
                    getColorEncoding(abcs.charAt(i));
                    pass = false;
                }
                catch( WTFException wtf){
                    //Should get here EVERY time in this loop

                    //wtf.printStackTrace();
                }
            }

            return pass;
        }



        public static boolean testGetSliceEncoding() {

            boolean pass;

            try {
                pass = getSliceEncoding("Row").equals("-1,-1,1,");
                pass &= getSliceEncoding("NS").equals("-1,1,-1,");
                pass &= getSliceEncoding("EW").equals("1,-1,-1,");
            }
            catch( WTFException wtf){
                wtf.printStackTrace();
                pass = false;
            }

            String abcs = "abcdefghijklmnopqrstuvwxyzACDEFHIJKLMNPQSTUVXZ0123456789!@#$$%^&&*(),.;'[]\\/<>?:\"{}|+_-=";

            for (int i = 0; i < abcs.length(); i++) {
                try {
                    //This should throw an exception so it never gets to pass = false;
                    getSliceEncoding(abcs.charAt(i)+"");
                    pass = false;
                }
                catch( WTFException wtf){
                    //wtf.printStackTrace();
                }
            }

            return pass;
        }

}
