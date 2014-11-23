package br.com.ufu.lsi.utils;

import org.tartarus.snowball.ext.PorterStemmer;

import weka.core.stemmers.Stemmer;

public class SnowballStemmerWrapper implements Stemmer {

    private static final long serialVersionUID = 1L;

    public String getRevision() {
        
        return null;
    }

    public String stem( String word ) {
        
        PorterStemmer ps = new PorterStemmer();
        ps.setCurrent( word );
        ps.stem();
        return ps.getCurrent();
    }
}
