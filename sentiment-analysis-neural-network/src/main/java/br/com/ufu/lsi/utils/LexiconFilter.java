
package br.com.ufu.lsi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

public class LexiconFilter extends SimpleBatchFilter {
    
    private String processedFile;
    
    private List<String> lexiconTerms;
    
    private List<Boolean> attributes;

    public LexiconFilter( String processedFile, String prefix ) {
        this.processedFile = processedFile;
        loadLexicon( false, true, prefix );
    }
    
    public void loadLexicon( boolean sentiment, boolean stemmer, String prefix ){
        
        lexiconTerms = new ArrayList< String >();
        
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader( new FileReader( processedFile ) );

            while ( ( sCurrentLine = br.readLine() ) != null ) {

                boolean adding = false;
                
                if( sentiment ) {
                    
                    String [] tokens = sCurrentLine.split( "," );
                    
                    if( tokens.length > 1 ) {
                        if( tokens.length == 2 ) {
                            if( tokens[1].equals( "Negativ" ) || tokens[1].equals( "Positiv" ) ) {
                                adding = true;
                            }
                        } else if( tokens.length == 3 ) {
                            if( tokens[2].equals( "Negativ" ) || tokens[2].equals( "Positiv" ) ) {
                                adding = true;
                            }
                        }
                    }
                    
                    int index = sCurrentLine.indexOf( ',' );
                    if( index == -1 )
                        index = sCurrentLine.length();
                    sCurrentLine = sCurrentLine.subSequence( 0, index ).toString();
                    //sCurrentLine = sCurrentLine.subSequence( 0, sCurrentLine.indexOf( ',' ) ).toString();
                    
                } else {
                    adding = true;
                    int index = sCurrentLine.indexOf( ',' );
                    if( index == -1 )
                        index = sCurrentLine.length();
                    sCurrentLine = sCurrentLine.subSequence( 0, index ).toString();
                    //sCurrentLine = sCurrentLine.subSequence( 0, sCurrentLine.indexOf( ',' ) ).toString();
                }
                if( stemmer ) {
                    SnowballStemmerWrapper porterStemmer = new SnowballStemmerWrapper();
                   
                    sCurrentLine = porterStemmer.stem( sCurrentLine.toLowerCase() );
                }
                
                if( adding )
                    lexiconTerms.add( prefix + sCurrentLine.toLowerCase() );
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( br != null )
                    br.close();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
        
        System.out.println("======= LEXICON TERMS ======");
        int i = 0;
        for( String s : lexiconTerms )
            System.out.println( (++i) + " " + s );
    }
    
    
    public String globalInfo() {
        return "A simple batch filter that adds an additional attribute 'bla' at the end "
                + "containing the index of the processed instance.";
    }

    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.enableAllAttributes();
        result.enableAllClasses();
        result.enable( Capability.NO_CLASS ); //// filter doesn't need class to be set//
        return result;
    }
    
    private Instances deleteAttributes( Instances result ) {
        for( int i = 0; i < result.numAttributes(); i++ ) {
            if( result.attribute( i ).weight() == -100.0 ) {
                result.deleteAttributeAt( i );
                i--;
            }
        }
        return result;
    }
    
    protected Instances determineOutputFormat( Instances inputFormat ) {
        
        Instances result = new Instances( inputFormat, 0 );
        
        attributes = new ArrayList< Boolean >( result.numAttributes() );
        
        System.out.println("======= REMOVED TERMS ======");
        for( int i = 0, k = 0; i < inputFormat.numAttributes()-1; i++ ) {
            if( !lexiconTerms.contains( inputFormat.attribute( i ).name() ) ) {
                attributes.add( false );
                result.attribute( i ).setWeight( -100.0 );
                System.out.println( (++k) + " Removing: " + inputFormat.attribute( i ).name() );
            } else {
                attributes.add( true );
            }
        }
        attributes.add( true );
        
        result = deleteAttributes( result );
        return result;
    }

    protected Instances process( Instances inst ) {
        Instances result = new Instances( determineOutputFormat( inst ), 0 );
        for ( int i = 0; i < inst.numInstances(); i++ ) {
            double[] values = new double[ result.numAttributes() ];
            int k = 0;
            for ( int n = 0; n < inst.numAttributes(); n++ ) {
                if( attributes.get( n ) )
                    values[ k++ ] = inst.instance( i ).value( n );
            }
            result.add( new Instance( 1, values ) );
        }
        
        System.out.println( "End lexicon filter...");
        return result;
    }
    

    public static void main( String[] args ) {
        runFilter( new LexiconFilter( "", "PREFIX-" ), args );
    }
}
