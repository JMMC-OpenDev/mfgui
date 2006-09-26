/*
 JMMC
*/
package jmmc.mf.gui;

import hep.aida.*;


/**
 *
 * @author  mella
 */
public class PlotPanel extends javax.swing.JPanel
{
    // Variables declaration - do not modify                     
    private javax.swing.JButton     jButton1;
    private javax.swing.JComboBox   jComboBox1;
    private javax.swing.JComboBox   jComboBox2;
    private javax.swing.JComboBox   jComboBox3;
    private javax.swing.JComboBox   jComboBox4;
    private javax.swing.JLabel      jLabel1;
    private javax.swing.JLabel      jLabel2;
    private javax.swing.JLabel      jLabel3;
    private javax.swing.JLabel      jLabel4;
    private javax.swing.JPanel      jPanel1;
    private javax.swing.JPanel      jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;

    /** Creates new form PlotPanel */
    public PlotPanel()
    {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1     = new javax.swing.JTabbedPane();
        jPanel1          = new javax.swing.JPanel();
        jComboBox1       = new javax.swing.JComboBox();
        jLabel1          = new javax.swing.JLabel();
        jLabel2          = new javax.swing.JLabel();
        jComboBox2       = new javax.swing.JComboBox();
        jComboBox3       = new javax.swing.JComboBox();
        jLabel3          = new javax.swing.JLabel();
        jComboBox4       = new javax.swing.JComboBox();
        jLabel4          = new javax.swing.JLabel();
        jButton1         = new javax.swing.JButton();
        jPanel2          = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "u", "v", "r" }));
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 1;
        gridBagConstraints.gridy      = 0;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jComboBox1, gridBagConstraints);

        jLabel1.setText("x");
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("y");
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 1;
        jPanel1.add(jLabel2, gridBagConstraints);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "u", "v", "r", "vis2", "t3amp", "t3phi" }));
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 1;
        gridBagConstraints.gridy      = 1;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jComboBox2, gridBagConstraints);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "u", "v", "r" }));
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 1;
        gridBagConstraints.gridy      = 2;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jComboBox3, gridBagConstraints);

        jLabel3.setText("dx");
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 2;
        jPanel1.add(jLabel3, gridBagConstraints);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "u", "v", "r", "vis2", "t3amp", "t3phi" }));
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 1;
        gridBagConstraints.gridy      = 3;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jComboBox4, gridBagConstraints);

        jLabel4.setText("dy");
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 3;
        jPanel1.add(jLabel4, gridBagConstraints);

        jButton1.setText("Plot");
        jButton1.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    jButton1ActionPerformed(evt);
                }
            });

        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 2;
        gridBagConstraints.gridy     = 1;
        jPanel1.add(jButton1, gridBagConstraints);

        jTabbedPane1.addTab("2D", jPanel1);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.addTab("3D", jPanel2);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }

    // </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton1ActionPerformed

        IAnalysisFactory     af     = IAnalysisFactory.create();
        ITree                tree   = af.createTreeFactory().create();
        IDataPointSetFactory dpsf   = af.createDataPointSetFactory(tree);
  
        // Create a two dimensional IDataPointSet.
        IDataPointSet dataPointSet = dpsf.create("dataPointSet",
                "two dimensional IDataPointSet", 2);

        // Fill the two dimensional IDataPointSet
        double[] yVals2D = { 0.12, 0.22, 0.35, 0.42, 0.54, 0.61 };
        double[] yErrP2D = { 0.01, 0.02, 0.03, 0.03, 0.04, 0.04 };
        double[] yErrM2D = { 0.02, 0.02, 0.02, 0.04, 0.06, 0.05 };
        double[] xVals2D = { 1.5, 2.6, 3.4, 4.6, 5.5, 6.4 };
        double[] xErrP2D = { 0.5, 0.5, 0.4, 0.4, 0.5, 0.5 };

        for (int i = 0; i < yVals2D.length; i++)
        {
            dataPointSet.addPoint();
            dataPointSet.point(i).coordinate(0).setValue(xVals2D[i]);
            dataPointSet.point(i).coordinate(0).setErrorPlus(xErrP2D[i]);
            dataPointSet.point(i).coordinate(1).setValue(yVals2D[i]);
            dataPointSet.point(i).coordinate(1).setErrorPlus(yErrP2D[i]);
            dataPointSet.point(i).coordinate(1).setErrorMinus(yErrM2D[i]);
        }

        // Display the results
        IPlotter plotter = af.createPlotterFactory()
                             .create("Plot IDataPointSets");
        plotter.createRegions();
        plotter.region(0).plot(dataPointSet);        
        plotter.show();
    }//GEN-LAST:event_jButton1ActionPerformed

    // End of variables declaration                   
}
