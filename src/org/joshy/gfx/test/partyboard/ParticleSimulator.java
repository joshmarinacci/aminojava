package org.joshy.gfx.test.partyboard;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;

import java.util.ArrayList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Nov 14, 2010
* Time: 10:38:37 PM
* To change this template use File | Settings | File Templates.
*/
public class ParticleSimulator extends Node {
    private int tick;
    private PartyBoard main;

    public ParticleSimulator(PartyBoard main) {
        this.main = main;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.BLACK);
        g.fillRect(0,0,main.width,main.height);
        for(Particle p : parts) {
            p.draw(g);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,100,100);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    private List<Particle> parts = new ArrayList<Particle>();
    public void update() {
        this.tick++;
        double grav = Math.sin(Math.toRadians(tick) / 2) * main.gravity;

        if(parts.size() < 20) {
            Particle particle = new Particle(main);
            particle.setFill(FlatColor.GREEN);
            particle.x = main.width/2;
            particle.y = main.height/2;
            particle.vx = (Math.random()*2.0-1.0)*0.5;
            particle.vy = (Math.random()*2.0-1.0)*0.5;
            particle.radius = 30+Math.random()*10;
            particle.setIndex(parts.size());
            parts.add(particle);
        }
        for(Particle part : parts) {
            part.collide(parts,main.spring,main.width,main.height);
            part.move(grav,main.width,main.height);
            //if(tick % (int)(main.color.doubleValue()) == 0) { // only update every other tick
            part.updateColor();
            //}
        }
        setDrawingDirty();

    }
}
