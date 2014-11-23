package br.com.ufu.lsi.utils;

import java.util.regex.Matcher;

public class StringUtils {
    
    public static String escapeSingleQuotes( String str ) {
        
        return str.replaceAll( "'", Matcher.quoteReplacement( "\\'" ) );
    }

}
