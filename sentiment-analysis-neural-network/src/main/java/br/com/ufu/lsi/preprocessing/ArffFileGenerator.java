
package br.com.ufu.lsi.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import br.com.ufu.lsi.utils.StringUtils;

public class ArffFileGenerator {

    private static final String PREFIX_FILE_NAME_PREFIX = "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/reviews/review_polarity/txt_sentoken/";

    private static final String OUTPUT_FILE_NAME = "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/reviews/reviews.arff";

    public static void writeArff() {

        try {
            
            File file = new File( OUTPUT_FILE_NAME );
            BufferedWriter output = new BufferedWriter( new FileWriter( file, true ) );

            output.write( header() );
            output.write( readDirFiles( PREFIX_FILE_NAME_PREFIX + "neg/", "neg" ) );
            output.write( readDirFiles( PREFIX_FILE_NAME_PREFIX + "pos/", "pos" ) );
            output.close();

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static String header() {

        StringBuilder str = new StringBuilder();

        str.append( "@relation SMS_Spam_Collection\n\n" );
        str.append( "@attribute class {pos,neg}\n" );
        str.append( "@attribute text String\n\n" );
        str.append( "@data\n" );

        return str.toString();
    }

    public static String readDirFiles( String dirName, String clazz ) throws IOException {

        StringBuilder content = new StringBuilder();

        File dir = new File( dirName );
        File[] foundFiles = dir.listFiles();

        for ( File file : foundFiles ) {

            BufferedReader br = new BufferedReader( new FileReader( file ) );
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while ( line != null ) {
                    sb.append( line );
                    sb.append( " " );
                    line = br.readLine();
                }
                
                content.append( clazz + ", '" + StringUtils.escapeSingleQuotes( sb.toString() ) + "'\n" );

            } finally {
                br.close();
            }

        }

        return content.toString();
    }

    public static void main( String... args ) {
        writeArff();
    }

}
