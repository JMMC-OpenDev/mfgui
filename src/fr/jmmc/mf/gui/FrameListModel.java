/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.jmmc.mf.gui;

import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JFrame;

/**
 *
 * @author mella
 */
public class FrameListModel extends AbstractListModel{

    Vector <JFrame> frames;
    Vector <String> titles;

    public FrameListModel() {
        frames = new Vector();
        titles = new Vector();
    }

    public int getSize() {
        return titles.size();
    }

    public Object getElementAt(int index) {
        return titles.get(index);
    }

    void add(JFrame frame, String title) {
        frames.add(frame);
        titles.add(title);
        int index = getSize();
        this.fireIntervalAdded(this, index, index);
    }

    JFrame getFrame(int index) {
        return frames.get(index);
    }

    void remove(JFrame frame){
        int i = frames.indexOf(frame);
        if (i>0){
            frames.remove(i);
            titles.remove(i);
            this.fireIntervalRemoved(this, i, i);
        }
    }
}
