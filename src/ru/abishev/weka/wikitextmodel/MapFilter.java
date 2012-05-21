package ru.abishev.weka.wikitextmodel;

import weka.core.*;
import weka.filters.Filter;

public abstract class MapFilter extends Filter {
    private SingleIndex attribute = new SingleIndex("last");

    public String globalInfo() {
        return "map";
    }

    public boolean setInputFormat(Instances instanceInfo) throws Exception {
        super.setInputFormat(instanceInfo);

        attribute.setUpper(instanceInfo.numAttributes() - 1);
        if (!instanceInfo.attribute(attribute.getIndex()).isString()) {
            throw new UnsupportedAttributeTypeException("Chosen attribute not string.");
        }

        setOutputFormat();
        return true;
    }

    public boolean input(Instance instance) throws Exception {
        if (getInputFormat() == null) {
            throw new IllegalStateException("No input instance format defined");
        }
        if (m_NewBatch) {
            resetQueue();
            m_NewBatch = false;
        }
        Instance newInstance = (Instance) instance.copy();
        int index = attribute.getIndex();
        if (!newInstance.isMissing(index)) {
            newInstance.setValue(index, map(instance.stringValue(index)));
        }
        push(newInstance);
        return true;
    }

    abstract public String map(String input);

    public String getAttributeIndex() {
        return attribute.getSingleIndex();
    }

    public void setAttributeIndex(String attIndex) {
        attribute.setSingleIndex(attIndex);
    }

    private void setOutputFormat() {
        // Create new attributes
        FastVector newAtts = new FastVector(getInputFormat().numAttributes());
        for (int j = 0; j < getInputFormat().numAttributes(); j++) {
            newAtts.addElement(getInputFormat().attribute(j).copy());
        }

        // Create new header
        Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
        newData.setClassIndex(getInputFormat().classIndex());
        setOutputFormat(newData);
    }

    public String getRevision() {
        return "1";
    }
}

