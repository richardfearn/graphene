/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.epics.util.stats.Ranges;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author carcassi
 */
public class TemporalGraph2DRendererTest {
    
    @Test
    public void aggregateTimeInterval1() {
        Timestamp time = Timestamp.now();
        TimeInterval interval1 = TimeDuration.ofSeconds(1).after(time);
        TimeInterval interval2 = TimeDuration.ofSeconds(2).after(time);
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval1, interval2), sameInstance(interval2));
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval2, interval1), sameInstance(interval2));
    }
    
    @Test
    public void aggregateTimeInterval2() {
        Timestamp time = Timestamp.now();
        TimeInterval interval1 = TimeDuration.ofSeconds(1).after(time);
        TimeInterval interval2 = TimeDuration.ofSeconds(4).around(time);
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval1, interval2), sameInstance(interval2));
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval2, interval1), sameInstance(interval2));
    }
    
    @Test
    public void aggregateTimeInterval4() {
        Timestamp time = Timestamp.now();
        TimeInterval interval1 = TimeDuration.ofSeconds(1).around(time);
        TimeInterval interval2 = TimeDuration.ofSeconds(1).after(time);
        TimeInterval total = TimeInterval.between(interval1.getStart(), interval2.getEnd());
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval1, interval2), equalTo(total));
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval2, interval1), equalTo(total));
    }
    
    @Test
    public void aggregateTimeInterval3() {
        Timestamp time = Timestamp.now();
        TimeInterval interval1 = TimeDuration.ofSeconds(1).before(time);
        TimeInterval interval2 = TimeDuration.ofSeconds(1).after(time.plus(TimeDuration.ofSeconds(1)));
        TimeInterval total = TimeInterval.between(interval1.getStart(), interval2.getEnd());
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval1, interval2), equalTo(total));
        assertThat(TemporalGraph2DRenderer.aggregateTimeInterval(interval2, interval1), equalTo(total));
    }
    
    @Test
    public void calculateRanges1() throws Exception {
        TemporalGraph2DRenderer renderer = new TemporalGraph2DRenderer(300, 200) {

            @Override
            public TemporalGraph2DRendererUpdate newUpdate() {
                return new TemporalGraph2DRendererUpdate();
            }
        };
        
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.g = graphics;
        
        Range initialRange = Ranges.range(0, 10);
        Timestamp now = Timestamp.now();
        TimeInterval initialTimeInterval = TimeInterval.between(now, now.plus(TimeDuration.ofSeconds(1)));
        renderer.calculateRanges(initialRange, initialTimeInterval);
        assertThat(renderer.getPlotRange(), sameInstance(initialRange));
        assertThat(renderer.getPlotTimeInterval(), sameInstance(initialTimeInterval));
        
        Range newRange = Ranges.range(5, 15);
        TimeInterval newTimeInterval = TimeInterval.between(now.minus(TimeDuration.ofSeconds(1)), now);
        renderer.calculateRanges(newRange, newTimeInterval);
        assertThat(renderer.getPlotRange().getMinimum(), equalTo(Ranges.range(0, 15).getMinimum()));
        assertThat(renderer.getPlotRange().getMaximum(), equalTo(Ranges.range(0, 15).getMaximum()));
        assertThat(renderer.getPlotTimeInterval(), sameInstance(newTimeInterval));
    }
    
    @Test
    public void timeGraphArea1() throws Exception {
        TemporalGraph2DRenderer renderer = new TemporalGraph2DRenderer(300, 200) {

            @Override
            public TemporalGraph2DRendererUpdate newUpdate() {
                return new TemporalGraph2DRendererUpdate();
            }
        };
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.g = graphics;
        Timestamp start = TimeScalesTest.create(2013, 1, 1, 12, 0, 0, 0);
        Timestamp end = TimeScalesTest.create(2013, 1, 1, 12, 0, 2, 0);
        renderer.calculateRanges(Ranges.range(0, 10), TimeInterval.between(start, end));
        renderer.calculateGraphArea();
        renderer.drawGraphArea();
        ImageAssert.compareImages("timeGraph2DArea.1", image);
    }
    
}