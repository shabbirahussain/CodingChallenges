package com.ir.featureextraction.controlers.outputwriters.featurestore;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Internal model
 * @author shabbirhussain
 */
public class MHashFeatureKeyMap implements MFeatureKeyMap {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> featMap;
    private Set<Double> labelSet;

    /**
     * Default constructor
     */
    public MHashFeatureKeyMap(){
        this.featMap  = new LinkedHashMap<>(Integer.MAX_VALUE);
        this.labelSet = new HashSet<>();
    }

    @Override
    public Integer getNumericKey(String feature){
        Integer nKey = this.featMap.get(feature);
        if(nKey == null){
            nKey = this.addNumericKey(feature);
        }
        return nKey;
    }

    @Override
    public Set<String> getKeySet(){
        return this.featMap.keySet();
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
    public void loadFromARFF(String filePath) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line;
        while((line=br.readLine())!=null){
            if(line.startsWith("@ATTRIBUTE")){
                String[] tokens = line.split(" ");
                addNumericKey(tokens[1]);
            }
            if(line.startsWith("@DATA")) break;
        }
        addLabel(1.0);addLabel(0.0);
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
        Integer nKey = this.featMap.get(feature);
        if(nKey == null){
            nKey = this.featMap.size();
            this.featMap.put(feature, nKey);
        }
        return nKey;
    }
}
