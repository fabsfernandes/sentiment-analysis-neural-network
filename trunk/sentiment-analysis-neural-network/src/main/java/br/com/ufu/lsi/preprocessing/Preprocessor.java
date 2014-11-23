package br.com.ufu.lsi.preprocessing;

import java.io.File;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.ClassAssigner;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import br.com.ufu.lsi.utils.SnowballStemmerWrapper;

public class Preprocessor {


    private static final int FEATURES_NUMBER = 500;
    
    public Instances preprocess() throws Exception {

        DataSource source = new DataSource( "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/reviews/reviews.arff" );
        Instances data = source.getDataSet();
        
        Filter [] filters = new Filter[4];
        filters[0] = stringToWordVector();
        filters[1] = classAssigner();
        filters[2] = attributeSelection();
        filters[3] = removeFilter( FEATURES_NUMBER );
        
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters( filters );
        multiFilter.setInputFormat( data );
        
        Instances filteredData = Filter.useFilter( data, multiFilter);
        
        System.out.println( filteredData.attribute( "class" ) );
        System.out.println( "# attributes: " + filteredData.numAttributes() );
        
        for( int i = 0; i < filteredData.numAttributes(); i++ ) {
            System.out.println( i + " " + filteredData.attribute( i ).name() );
        }
        
        return filteredData;
    }
    
    public Filter stringToWordVector() {
        
        StringToWordVector vectorFilter = new StringToWordVector();

        //vectorFilter.setInputFormat( data );
        
        vectorFilter.setAttributeNamePrefix( "PREFIX-" );
        vectorFilter.setDoNotOperateOnPerClassBasis( true );
        vectorFilter.setLowerCaseTokens( true );
        vectorFilter.setOutputWordCounts( true );
        vectorFilter.setTFTransform( true );
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
    
    public Filter attributeSelection() {

        AttributeSelection attributeSelection = new AttributeSelection();
        InfoGainAttributeEval infoGain = new InfoGainAttributeEval();
        attributeSelection.setEvaluator( infoGain );

        Ranker ranker = new Ranker();
        attributeSelection.setSearch( ranker );

        return attributeSelection;
    }
    
    public Filter removeFilter( int index ) {
        Remove remove = new Remove();
        remove.setAttributeIndices( "last,1-" + index );
        remove.setInvertSelection( true );
        
        return remove;
    }

}
