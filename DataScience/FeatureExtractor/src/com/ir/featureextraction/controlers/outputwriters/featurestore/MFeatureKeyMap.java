package com.ir.featureextraction.controlers.outputwriters.featurestore;

import java.io.IOException;
import java.io.Serializable;
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

    /**
     * Loads feature map from existing ARFF file
     * @param filePath is the full file path of the ARFF file to load
     * @throws IOException when file exception occurs
     */
    public void loadFromARFF(String filePath) throws IOException;
}
