package br.com.ufu.lsi.utils;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class TestRemoveFilter {
    
        @Test
        public void test() throws Exception {
          Instances       inst;
          Instances       instNew;
          Remove          remove;
      
          String arffFile = "/Users/fabiola/Doutorado/IA/Trabalho2/dataset/reviews/reviews.arff";
          
          inst   = new Instances(new BufferedReader(new FileReader( arffFile )));
          System.out.println( inst.numAttributes() );
          remove = new Remove();
          remove.setAttributeIndices( "2-2" );
          remove.setInvertSelection( false );
          remove.setInputFormat(inst);
          instNew = Filter.useFilter(inst, remove);
          System.out.println(instNew);
          System.out.println( instNew.numAttributes() );
        }
     
}
