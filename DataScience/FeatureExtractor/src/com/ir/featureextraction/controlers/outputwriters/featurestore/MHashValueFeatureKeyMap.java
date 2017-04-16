package com.ir.featureextraction.controlers.outputwriters.featurestore;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Internal model
 * @author shabbirhussain
 */
public class MHashValueFeatureKeyMap implements MFeatureKeyMap {
    private static final long serialVersionUID = 1L;
    private Set<String> featKeySet;
    private Set<Double> labelSet;

    /**
     * Default constructor
     */
    public MHashValueFeatureKeyMap(){
        this.featKeySet = new HashSet<>(Integer.MAX_VALUE);
        this.labelSet = new HashSet<>();
    }

    @Override
    public Integer getNumericKey(String feature){
        Integer nKey = feature.hashCode();
        this.featKeySet.add(feature);
        return nKey;
    }

    @Override
    public Set<String> getKeySet(){
        return this.featKeySet;
    }

    @Override
    public Set<Double> getLabelSet(){
        return this.labelSet;
    }

    @Override
    public void addLabel(Double label) {
        if(!this.labelSet.contains(label))
            this.addNewLabel(label);
    }
    @Override
    public void loadFromARFF(String filePath) throws IOException {
    }

        /**
         * Adds a new label to the set
         * @param label is the label to add to the model
         */
    private synchronized void addNewLabel(Double label){
        this.labelSet.add(label);
    }

}
