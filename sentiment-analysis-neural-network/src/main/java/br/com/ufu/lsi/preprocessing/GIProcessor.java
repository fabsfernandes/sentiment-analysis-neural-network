
package br.com.ufu.lsi.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.ClassAssigner;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import br.com.ufu.lsi.utils.DocumentFrequencyAttributeEval;
import br.com.ufu.lsi.utils.LexiconFilter;
import br.com.ufu.lsi.utils.SnowballStemmerWrapper;

public class GIProcessor {

    private static final String ORIGINAL_FILE = "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/GI/inquireraugmented.csv";

    private static final String PROCESSED_FILE = "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/GI/inquireraugmented_processed.csv";
    
    private static final String PREFIX = "PREFIX-";
    
    private static final int FEATURES_NUMBER = 500;

    public Instances preprocess() throws Exception {
        DataSource source = new DataSource( "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/reviews/reviews.arff" );
        Instances data = source.getDataSet();

        Filter[] filters = new Filter[ 5 ];
        filters[ 0 ] = stringToWordVector();
        filters[ 1 ] = classAssigner();
        filters[2] = attributeSelection();
        filters[3] = lexiconFilter();
        filters[4] = removeFilter( FEATURES_NUMBER );
        
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters( filters );
        multiFilter.setInputFormat( data );

        Instances filteredData = Filter.useFilter( data, multiFilter );
        
        System.out.println( filteredData.attribute( "class" ) );
        System.out.println( "# attributes: " + filteredData.numAttributes() );
        
        for( int i = 0; i < filteredData.numAttributes(); i++ ) {
            System.out.println( i + " " + filteredData.attribute( i ).name() );
        }
        
        return filteredData;
    }
    
    public Filter lexiconFilter(){
        LexiconFilter lexicon = new LexiconFilter( PROCESSED_FILE, PREFIX );
        return lexicon;
    }
    
    

    public Filter stringToWordVector() {

        StringToWordVector vectorFilter = new StringToWordVector();

        //vectorFilter.setInputFormat( data );

        vectorFilter.setAttributeNamePrefix( PREFIX );
        vectorFilter.setDoNotOperateOnPerClassBasis( true );
        vectorFilter.setLowerCaseTokens( true );
        vectorFilter.setOutputWordCounts( true );
        vectorFilter.setTFTransform( false );
        vectorFilter.setIDFTransform( true );
        vectorFilter.setStemmer( new SnowballStemmerWrapper() );
        vectorFilter.setStopwords( new File( "/Users/fabiola/workspace/sentiment-analysis-neural-network/src/main/resources/stopwords.txt" ) );
        vectorFilter.setWordsToKeep( 100000 );

        return vectorFilter;
    }

    public Filter classAssigner() {

        ClassAssigner classAssigner = new ClassAssigner();
        classAssigner.setClassIndex( "first" );

        return classAssigner;
    }

    
    
    
    public Filter attributeSelection() throws Exception {

        AttributeSelection attributeSelection = new AttributeSelection();
        DocumentFrequencyAttributeEval documentFrequency = new DocumentFrequencyAttributeEval();
        attributeSelection.setEvaluator( documentFrequency );

        Ranker ranker = new Ranker();
        ranker.setGenerateRanking( true );
        ranker.setStartSet( "1" );
        ranker.setNumToSelect( -1 );
        attributeSelection.setSearch( ranker );

        return attributeSelection;
    }
    
    
    
    public Filter removeFilter( int index ) {
        Remove remove = new Remove();
        remove.setAttributeIndices( "last,1-" + index );
        remove.setInvertSelection( true );
        
        return remove;
    }
    
    
    
    
    
    /**
     * 
     */
    public void removeRepetitions() {

        BufferedReader br = null;

        BufferedWriter bw = null;

        try {

            String sCurrentLine;

            br = new BufferedReader( new FileReader( ORIGINAL_FILE ) );

            bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File( PROCESSED_FILE ) ) ) );

            while ( ( sCurrentLine = br.readLine() ) != null ) {

                if ( sCurrentLine.contains( "#1" ) ) {
                    sCurrentLine = sCurrentLine.replaceAll( "#1", "" );
                    bw.write( sCurrentLine );
                    bw.newLine();
                } else if ( sCurrentLine.contains( "#" ) ) {

                } else {
                    bw.write( sCurrentLine );
                    bw.newLine();
                }
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( br != null )
                    br.close();
                if ( bw != null )
                    bw.close();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }

    public static void main( String... args ) {
        new GIProcessor().removeRepetitions();
    }

}
