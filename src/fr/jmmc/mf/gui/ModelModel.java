/*
 JMMC
*/
package jmmc.mf.gui;

import jmmc.mcs.gui.*;

import jmmc.mcs.log.MCSLogger;

import jmmc.mf.models.Model;

import java.io.*;

import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;


public class ModelModel implements TreeModel
{
    public final static String COMPOSED_MODEL_TYPE = "composite";
    private Vector             treeModelListeners  = new Vector();
    private Model              rootModel;
    public Action loadModelAction;
    public Action saveModelAction;

    public ModelModel()
    {
        rootModel                                  = new Model();
        rootModel.setName("myModel");
        rootModel.setType(COMPOSED_MODEL_TYPE);
        loadModelAction     = new LoadModelAction();
        saveModelAction     = new SaveModelAction();
    }

    public void setRootModel(Model newRootModel)
    {
        rootModel = newRootModel;
        fireTreeStructureChanged(rootModel);
    }

    public void loadFile(File fileToLoad)
    {
        MCSLogger.trace();

        try
        {
            FileReader reader   = new FileReader(fileToLoad);
            Model      newModel = (Model) Model.unmarshal(reader);
            setRootModel(newModel);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void saveFile(File fileToSave)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException,
            org.exolab.castor.xml.ValidationException
    {
        MCSLogger.trace();

        // Read a File to unmarshal from
        FileWriter writer = new FileWriter(fileToSave);
        // Marshal the person object
        rootModel.marshal(writer);
    }

    public void addModel(Model parent, Model child)
    {
        jmmc.mcs.log.MCSLogger.trace();
        parent.addModel(child);
        fireTreeStructureChanged(rootModel);
    }

    public void addModel(Model child)
    {
        jmmc.mcs.log.MCSLogger.trace();
        rootModel.addModel(child);
        fireTreeStructureChanged(rootModel);
    }

    public void removeModel(Model child)
    {
        jmmc.mcs.log.MCSLogger.trace();
        removeModel(child, rootModel);
    }

    public void removeModel(Model child, Model parent)
    {
        Model[] children = parent.getModel();

        for (int i = 0; i < children.length; i++)
        {
            Model m = children[i];

            if (m.equals(child))
            {
                parent.removeModel(i);
                fireTreeStructureChanged(parent);
            }
            else
            {
                removeModel(child, m);
            }
        }
    }

    //////////////// Fire events //////////////////////////////////////////////

    /**
     * One event raised by this model is TreeStructureChanged with the
     * root as path, i.e. the whole tree has changed.
     */
    protected void fireTreeStructureChanged(Model oldRoot)
    {
        int            len = treeModelListeners.size();
        TreeModelEvent e   = new TreeModelEvent(this, new Object[] { oldRoot });

        for (int i = 0; i < len; i++)
        {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeStructureChanged(e);
        }
    }

    /**
     * One event raised by this model is TreeStructureChanged with the
     * changed model in argument.
     */
    protected void fireTreeNodesChanged(Model changedModel)
    {
        int            len = treeModelListeners.size();
        TreeModelEvent e   = new TreeModelEvent(this,
                new Object[] { changedModel });

        for (int i = 0; i < len; i++)
        {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
    }

    //////////////// TreeModel interface implementation ///////////////////////

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l)
    {
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index)
    {
        Model p = (Model) parent;

        return p.getModel(index);
    }

    /**
     * Returns the number of children of parent.
     */
    public int getChildCount(Object parent)
    {
        Model p = (Model) parent;

        return p.getModelCount();
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child)
    {
        if ((parent == null) || (child == null))
        {
            return 0;
        }

        Model   p        = (Model) parent;
        Model[] children = p.getModel();
        int     count    = 0;

        for (int i = 0; i < children.length; i++)
        {
            if (children[i].equals(child))
            {
                return count;
            }

            count++;
        }

        return 0;
    }

    /**
     * Returns the root of the tree.
     */
    public Object getRoot()
    {
        return rootModel;
    }

    /**
     * Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node)
    {
        Model p = (Model) node;

        return p.getModelCount() == 0;
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l)
    {
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        jmmc.mcs.log.MCSLogger.trace();

        Model m = (Model) path.getLastPathComponent();
        m.setName("" + newValue);
        fireTreeNodesChanged(m);
    }

    protected class LoadModelAction extends MFAction
    {
        public String lastDir = System.getProperty("user.dir");

        public LoadModelAction()
        {
            super("loadModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            JFileChooser fileChooser = new JFileChooser();

            // Set in previous load directory
            if (lastDir != null)
            {
                fileChooser.setCurrentDirectory(new File(lastDir));
            }

            // Open file chooser
            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fileChooser.getSelectedFile();
                lastDir = file.getParent();
                loadFile(file);
            }
        }
    }

    protected class SaveModelAction extends MFAction
    {
        public String lastDir = System.getProperty("user.dir");

        public SaveModelAction()
        {
            super("saveModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            JFileChooser fileChooser = new JFileChooser();

            // Set in previous save directory
            if (lastDir != null)
            {
                fileChooser.setCurrentDirectory(new File(lastDir));
            }

            try
            {
                // Open filechooser
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileChooser.getSelectedFile();
                    lastDir = file.getParent();

                    // Ask to overwrite
                    if (file.exists())
                    {
                        String message = "File '" + file.getName() +
                            "' already exists\nDo you want to overwrite this file?";

                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);

                        if (answer == JOptionPane.YES_OPTION)
                        {
                            saveFile(file);
                        }
                    }
                    else
                    {
                        saveFile(file);
                    }
                }
            }
            catch (Exception exc)
            {
                ReportDialog dialog = new ReportDialog(null, true, exc);
                dialog.setVisible(true);

                // if (dialog.returnedValue="Report")                
            }
        }
    }
}
