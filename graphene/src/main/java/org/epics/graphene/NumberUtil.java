/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class NumberUtil {
    
    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}