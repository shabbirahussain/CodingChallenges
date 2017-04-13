package com.ir.featureextraction.controlers.outputwriters.featurestore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Internal model
 * @author shabbirhussain
 */
public interface MFeatureKeyMap extends Serializable {
    /**
     * Gets the numeric equivalent of the given string feature name
     * @param feature is the string feature to lookup
     * @return a unique numeric index for unique features
     */
    public Integer getNumericKey(String feature);

    /**
     * @return Gets the set of keys present in the feature
     */
    public Set<String> getKeySet();
    /**
     * @return Gets the set of keys present in the labels
     */
    public Set<Double> getLabelSet();
    /**
     * Adds a label to the model
     * @param label is the label to add to the model
     */
    public void addLabel(Double label);
}
