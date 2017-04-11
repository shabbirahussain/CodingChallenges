package com.ir.featureextraction.controlers.outputwriters;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Internal model
 * @author shabbirhussain
 */
public class MFeatureKeyMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> featMap;
    private Set<Double> labelSet;

    /**
     * Default constructor
     */
    public MFeatureKeyMap(){
        this.featMap  = new LinkedHashMap<>();
        this.labelSet = new HashSet<>();
    }

    /**
     * Gets the numeric equivalent of the given string feature name
     * @param feature is the string feature to lookup
     * @return a unique numeric index for unique features
     */
    public Integer getNumericKey(String feature){
        Integer nKey = this.featMap.get(feature);
        if(nKey == null){
            nKey = this.addNumericKey(feature);
        }
        return nKey;
    }

    /**
     * @return Gets the set of keys present in the feature
     */
    public Set<String> getKeySet(){
        return this.featMap.keySet();
    }

    /**
     * @return Gets the set of keys present in the labels
     */
    public Set<Double> getLabelSet(){
        return this.labelSet;
    }

    /**
     * Adds a label to the model
     * @param label is the label to add to the model
     */
    public void addLabel(Double label) {
        if(!this.labelSet.contains(label))
            this.addNewLabel(label);
    }

    /**
     * Adds a new label to the set
     * @param label is the label to add to the model
     */
    private synchronized void addNewLabel(Double label){
        this.labelSet.add(label);
    }

    /**
     * Adds a new neumeric keu for the feature
     * @param feature is the string feature to lookup
     * @return a unique numeric index for unique features
     */
    private synchronized Integer addNumericKey(String feature){
        Integer nKey = this.featMap.size();
        this.featMap.put(feature, nKey);
        return nKey;
    }
}
