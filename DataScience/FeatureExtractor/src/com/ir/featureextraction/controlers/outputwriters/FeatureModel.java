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
public class FeatureModel implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<String, Integer> featMap;
    Set<Double> labelSet;

    /**
     * Default constructor
     */
    public FeatureModel(){
        this.featMap  = new LinkedHashMap<>();
        this.labelSet = new HashSet<>();
    }
}
