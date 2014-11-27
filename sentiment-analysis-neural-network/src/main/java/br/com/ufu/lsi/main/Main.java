
package br.com.ufu.lsi.main;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import br.com.ufu.lsi.preprocessing.GIProcessor;
import br.com.ufu.lsi.rebfnetwork.RBFClassifier;

public class Main {

    public static void main( String... args ) throws Exception {

        long timestamp = System.currentTimeMillis();
        
        //Preprocessor p = new Preprocessor();
        GIProcessor p = new GIProcessor();
        //OpinionProcessor p = new OpinionProcessor();

        Instances instances = p.preprocess();
        
        Classifier cls = getClassifier();
        
        System.out.println( "##" + instances.numAttributes() );
        instances.setClassIndex( 0 );
        // split
        int trainSize = ( int ) Math.round( instances.numInstances() * 0.66 );
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances( instances, 0, trainSize );
        
        Attribute att = train.attribute( "class" );
        System.out.println( att );
        System.out.println( train.attribute( 0 ));
        
        Instances test = new Instances( instances, trainSize, testSize );

        cls.buildClassifier( train );

        Evaluation eval = new Evaluation( test );
        eval.evaluateModel( cls, test );
        System.out.println( eval.toSummaryString( "\nResults\n======\n", true ) );
        System.out.println( eval.toClassDetailsString() );
        System.out.println( eval.toMatrixString() );
        
        double t = (System.currentTimeMillis() - timestamp);
        double d = (t/1000.0)/60.0;
        System.out.println( "=======> " + d + "min" );
        
    }
    
    
    
    public static Classifier getClassifier(){
        //RBFNetwork rbf = new RBFNetwork();
        //return rbf;
        
        /*MultilayerPerceptron mlp = new MultilayerPerceptron();
        
        mlp.setHiddenLayers( "15" );
        mlp.setLearningRate( 0.01 );
        mlp.setMomentum( 0.8 );
        mlp.setTrainingTime( 500 );
        mlp.setReset( false );
        mlp.setNominalToBinaryFilter( false );
        mlp.setGUI( false );
        
        return mlp;*/
        
        RBFClassifier rbfClassifier = new RBFClassifier();
        rbfClassifier.setNumFunctions( 3 );
        rbfClassifier.setNumThreads( 2 );
        return rbfClassifier;
    }

}
