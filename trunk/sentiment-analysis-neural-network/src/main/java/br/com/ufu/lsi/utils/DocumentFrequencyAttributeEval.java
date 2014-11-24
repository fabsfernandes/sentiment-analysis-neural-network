
package br.com.ufu.lsi.utils;

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/* Adapted from 
 *    InfoGainAttributeEval.java
 *    Copyright (C) 1999 University of Waikato, Hamilton, New Zealand
 * by Eneldo Loza MencÃ­a
 */

import java.util.Enumeration;
import java.util.Vector;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class DocumentFrequencyAttributeEval extends ASEvaluation implements AttributeEvaluator, OptionHandler {

    /** for serialization */
    static final long serialVersionUID = - 1949849576589218930L;

    /** Treat missing values as a seperate value */
    private boolean m_missingAsZero;

    int[] m_DFs;

    /**
     * Returns a string describing this attribute evaluator
     * 
     * @return a description of the evaluator suitable for displaying in the explorer/experimenter
     *         gui
     */
    public String globalInfo() {
        return "DocumentFrequencyAttributeEval :\n\nReturns a ranking of attributes, ordered by descending document frequency";
    }

    /**
     * Constructor
     */
    public DocumentFrequencyAttributeEval() {
        resetOptions();
    }

    /**
     * Returns an enumeration describing the available options.
     * 
     * @return an enumeration of all the available options.
     **/
    public Enumeration listOptions() {
        Vector newVector = new Vector( 1 );
        newVector.addElement( new Option( "\tdon't treat missing values as a zero.", "M", 0, "-M" ) );
        //    newVector.addElement(new Option("\tjust binarize numeric attributes instead \n" 
        //                                    +"\tof properly discretizing them.", "B", 0, 
        //                                    "-B"));
        return newVector.elements();
    }

    /**
     * Parses a given list of options.
     * <p/>
     *
     <!-- options-start --> Valid options are:
     * <p/>
     * 
     * <pre>
     * -M
     *  treat missing values as a seperate value.
     * </pre>
     * 
     * 
     <!-- options-end -->
     *
     * @param options the list of options as an array of strings
     * @throws Exception if an option is not supported
     */
    public void setOptions( String[] options ) throws Exception {

        resetOptions();
        setMissingAsZero( ! ( Utils.getFlag( 'M', options ) ) );
        //    setBinarizeNumericAttributes(Utils.getFlag('B', options));
    }

    /**
     * Gets the current settings of WrapperSubsetEval.
     *
     * @return an array of strings suitable for passing to setOptions()
     */
    public String[] getOptions() {
        String[] options = new String[ 1 ];
        int current = 0;

        if ( ! getMissingAsZero() ) {
            options[ current++ ] = "-M";
        }

        while ( current < options.length ) {
            options[ current++ ] = "";
        }

        return options;
    }

    /**
     * Returns the tip text for this property
     * 
     * @return tip text for this property suitable for displaying in the explorer/experimenter gui
     */
    public String missingMergeTipText() {
        return "Don't treat missing values as zero, i.e. not occuring .";
    }

    public void setMissingAsZero( boolean b ) {
        m_missingAsZero = b;
    }

    public boolean getMissingAsZero() {
        return m_missingAsZero;
    }

    /**
     * Returns the capabilities of this evaluator.
     *
     * @return the capabilities of this evaluator
     * @see Capabilities
     */
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();

        // attributes
        //    result.enable(Capability.NOMINAL_ATTRIBUTES);
        result.enable( Capability.NUMERIC_ATTRIBUTES );
        //    result.enable(Capability.DATE_ATTRIBUTES);
        result.enable( Capability.MISSING_VALUES );

        // class
        result.enable( Capability.NUMERIC_CLASS );
        result.enable( Capability.NOMINAL_CLASS );
        result.enable( Capability.MISSING_CLASS_VALUES );

        return result;
    }

    /**
     * Initializes an information gain attribute evaluator. Discretizes all attributes that are
     * numeric.
     *
     * @param data set of instances serving as training data
     * @throws Exception if the evaluator has not been generated successfully
     */
    public void buildEvaluator( Instances data ) throws Exception {

        // can evaluator handle data?
        getCapabilities().testWithFail( data );

        int classIndex = data.classIndex();

        int numAttributes = data.numAttributes();
        m_DFs = new int[ numAttributes ];
        Enumeration e = data.enumerateInstances();
        while ( e.hasMoreElements() ) {
            Instance instance = ( Instance ) e.nextElement();
            int numValues = instance.numValues();
            for ( int valueIndex = 0; valueIndex < numValues; valueIndex++ ) {
                int attIndex = instance.index( valueIndex );
                if ( attIndex != classIndex ) {
                    double value = instance.valueSparse( valueIndex );
                    //missingvalues werden also 0 betrachtet.
                    if ( m_missingAsZero ) {
                        if ( ! Instance.isMissingValue( value ) && value != 0.0 ) { //man kÃ¶nnte auch isMissingSparce(valueIndex) verwenden, oder ineffizienterweise isMissing(attIndex)
                            m_DFs[ attIndex ]++ ;
                            //m_DFs[ attIndex ]+=value ;
                        }
                    } else {
                        if ( value != 0.0 ) {
                            m_DFs[ attIndex ]++ ;
                            //m_DFs[ attIndex ]+=value ;
                        }
                    }
                }
            }
        }
    }

    /**
     * Reset options to their default values
     */
    protected void resetOptions() {
        m_DFs = null;
        m_missingAsZero = true;
    }

    /**
     * evaluates an individual attribute by measuring the amount of information gained about the
     * class given the attribute.
     *
     * @param attribute the index of the attribute to be evaluated
     * @return the info gain
     * @throws Exception if the attribute could not be evaluated
     */
    public double evaluateAttribute( int attribute ) throws Exception {

        return m_DFs[ attribute ];
    }

    /**
     * Describe the attribute evaluator
     * 
     * @return a description of the attribute evaluator as a string
     */
    public String toString() {
        StringBuffer text = new StringBuffer();

        if ( m_DFs == null ) {
            text.append( "Document Frequency attribute evaluator has not been built" );
        } else {
            text.append( "\tDocument Frequency Ranking Filter" );
            if ( ! m_missingAsZero ) {
                text.append( "\n\tMissing values treated as zero" );
            }
        }

        text.append( "\n" );
        return text.toString();
    }

    // ============
    // Test method.
    // ============
    /**
     * Main method for testing this class.
     *
     * @param args the options
     */
    public static void main( String[] args ) {
        runEvaluator( new DocumentFrequencyAttributeEval(), args );
    }
}
