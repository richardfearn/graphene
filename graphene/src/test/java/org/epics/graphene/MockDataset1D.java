/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class MockDataset1D implements Dataset1D {
    
    private double[] data;
    private double minValue = Double.POSITIVE_INFINITY;
    private double maxValue = Double.NEGATIVE_INFINITY;

    public MockDataset1D(double[] data) {
        for (int i = 0; i < data.length; i++) {
            double d = data[i];
            if (d > maxValue)
                maxValue = d;
            if (d < minValue)
                minValue = d;
        }
        this.data = data;
    }
    
    

    @Override
    public IteratorDouble getValues() {
        return new IteratorDouble() {
            
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < data.length;
            }

            @Override
            public double next() {
                double value = data[index];
                index++;
                return value;
            }
        };
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }
    
    
    public static Dataset1D uniform(double minValue, double maxValue, int nSamples) {
        return new MockDataset1D(RangeUtil.createBins(minValue, maxValue, nSamples));
    }
}