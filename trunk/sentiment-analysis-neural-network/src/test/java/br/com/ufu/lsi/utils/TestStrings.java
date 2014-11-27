package br.com.ufu.lsi.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

public class TestStrings {
    
    //@Test
    public void str(){
        String sCurrentLine = "word, ,negativ";
        
        int index = sCurrentLine.indexOf( ',' );
        if( index == -1 )
            index = sCurrentLine.length();
        sCurrentLine = sCurrentLine.subSequence( 0, index ).toString();
        System.out.println( sCurrentLine);
    }
    
    @Test
    public void stemmer() {
        SnowballStemmerWrapper st = new SnowballStemmerWrapper();
        System.out.println( st.stem( "gostei" ) );
    }
    
    //@Test
    public void scapeString() {
        
        String str = "it's got \" quest for camelot \" is warner bros . ' first feature-length";
        
        String str2 = StringEscapeUtils.escapeJava( str );
        
        System.out.println( str2 );
    }
    
    //@Test
    public void scapeStringPersonalized(){
        String str2 = StringUtils.escapeSingleQuotes( "it's got \" quest for camelot \" is warner bros . ' first feature-length" );
        System.out.println( str2 );
        
    }
    

}
