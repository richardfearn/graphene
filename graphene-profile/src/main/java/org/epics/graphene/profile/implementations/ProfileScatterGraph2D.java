/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene.profile.implementations;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.epics.graphene.*;
import org.epics.graphene.profile.ProfileGraph2D;

/**
 * Handles profiling for <code>ScatterGraph2DRenderer</code>.
 * Takes a <code>Point2DDataset</code> dataset and repeatedly renders through a <code>ScatterGraph2DRenderer</code>.
 * 
 * @author asbarber
 */
public class ProfileScatterGraph2D extends ProfileGraph2D<ScatterGraph2DRenderer, Point2DDataset>{

    /**
     * Gets a set of random Gaussian 2D point data.
     * @return the appropriate <code>Point2DDataset</code> data
     */    
    @Override
    protected Point2DDataset getDataset() {
        return ProfileGraph2D.makePoint2DGaussianRandomData(getNumDataPoints());
    }

    /**
     * Returns the renderer used in the render loop.
     * The 2D point is rendered by a <code>ScatterGraph2DRenderer</code>.
     * @param imageWidth width of rendered image in pixels
     * @param imageHeight height of rendered image in pixels
     * @return a scatter graph to draw the data
     */        
    @Override
    protected ScatterGraph2DRenderer getRenderer(int imageWidth, int imageHeight) {
        return new ScatterGraph2DRenderer(imageWidth, imageHeight);
    }

    /**
     * Draws the 2D point data in a scatter graph.
     * Primary method in the render loop.
     * @param graphics where image draws to
     * @param renderer what draws the image
     * @param data the 2D point data being drawn
     */      
    @Override
    protected void render(Graphics2D graphics, ScatterGraph2DRenderer renderer, Point2DDataset data) {
        renderer.draw(graphics, data);
    }
    
    /**
     * Returns the name of the graph being profiled.
     * @return <code>ScatterGraph2DRenderer</code> title
     */       
    @Override
    public String getGraphTitle() {
        return "ScatterGraph2D";
    }   

    @Override
    public LinkedHashMap<String, Graph2DRendererUpdate> getVariations() {
        LinkedHashMap<String, Graph2DRendererUpdate> map = new LinkedHashMap<>();
        
        map.put("None", new Graph2DRendererUpdate());
        map.put("Linear Interpolation", new ScatterGraph2DRendererUpdate().interpolation(InterpolationScheme.LINEAR));
        map.put("Cubic Interpolation", new ScatterGraph2DRendererUpdate().interpolation(InterpolationScheme.CUBIC));
        
        return map;    
    }
}