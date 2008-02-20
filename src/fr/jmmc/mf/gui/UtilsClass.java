/*
 * NewClass.java
 *
 * Created on 4 octobre 2006, 15:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ReportDialog;

import org.w3c.dom.*;
import org.w3c.dom.Document;

import org.xml.sax.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.Component;

import java.io.*;

import java.net.URL;

import java.util.Enumeration;

import javax.swing.*;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;


/**
 * This class is here just to locate code parts that should be moved under
 * one jmmc.mcs.* package
 * @author mella
 */
public class UtilsClass {
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.McsClass");
    static String className = "UtilsClass";

    //
    //
    // Base64 content handling
    //
    static java.util.Hashtable alreadyExpandedFiles = new java.util.Hashtable();

    /*
     * This method picks good column sizes with given maxWidth limit.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     *@todo move this function into one mcs common area
     */
    public static void initColumnSizes(JTable table, int maxWidth) {
        TableModel model = table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer = table.getTableHeader()
                                                .getDefaultRenderer();

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                column = table.getColumnModel().getColumn(j);

                comp = headerRenderer.getTableCellRendererComponent(null,
                        column.getHeaderValue(), false, false, 0, j);
                headerWidth = comp.getPreferredSize().width;

                comp = table.getDefaultRenderer(model.getColumnClass(j))
                            .getTableCellRendererComponent(table,
                        model.getValueAt(i, j), false, false, i, j);
                cellWidth = comp.getPreferredSize().width;

                column.setPreferredWidth(Math.min(maxWidth,
                        Math.max(headerWidth, cellWidth)));
            }
        }
    }

    //
    //
    //  JTREES
    //
    //

    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public static void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    public static String saveBASE64OifitsToFile(String b64, File outputFile)
        throws IOException {
        // fill file with href content
        StringBuffer sb = new StringBuffer();
        java.util.StringTokenizer st = new java.util.StringTokenizer(b64.substring(
                    23));

        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
        }

        if (b64.startsWith("data:image/fits;base64,")) {
            logger.fine("decoding base64 file into " +
                outputFile.getAbsolutePath());

            byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(sb.toString());
            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(buf);
            out.flush();
            out.close();
        }

        return outputFile.getAbsolutePath();
    }

    public static String saveBASE64OifitsToFile(String fileId, String b64)
        throws IOException {
        Object key = fileId;

        // Search if this string has already been loaded
        java.io.File outputFile = (java.io.File) alreadyExpandedFiles.get(key);
        ;

        if (outputFile == null) {
            // Create temp file.
            outputFile = java.io.File.createTempFile("tmpOifile", ".oifits");
            // Delete temp file when program exits.
            outputFile.deleteOnExit();
            alreadyExpandedFiles.put(key, outputFile);

            return saveBASE64OifitsToFile(b64, outputFile);
        }

        logger.fine("file '" + key + "' was already expanded into " +
            outputFile.getAbsolutePath());

        return outputFile.getAbsolutePath();
    }

    //
    // General application methods
    //

    /**
     * @return true indicates that user has saved data or that object has
     * not been modified
     */
    public static boolean askToSaveUserModification(ModifyAndSaveObject object) {
        logger.entering(className, "askToSaveUserModification");

        if (object.isModified()) {
            // Ask the user if he wants to save modifications
            Object[] options = { "Save", "Cancel", "Don't Save" };
            int result = JOptionPane.showOptionDialog(object.getComponent(),
                    "Do you want to save changes to this document before closing ?\nIf you don't save, your changes will be lost.\n\n",
                    null, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            // If the User clicked the "Save" button, save
            if (result == 0) {
                object.save();

                // assert that file has been saved
                if (object.isModified()) {
                    //ask again
                    return askToSaveUserModification(object);
                }
            }

            // If the user clicked the "Cancel" button, return true
            if (result == 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check that every objects does not have been modify before application quitting.
     * If one object has been modified, ask object one filename and propose a dialog to save
     */
    public static void checkUserModificationAndQuit(
        ModifyAndSaveObject[] objects) {
        logger.entering(className, "checkUserModificationAndQuit");

        for (int i = 0; i < objects.length; i++) {
            if (!askToSaveUserModification(objects[i])) {
                return;
            }
        }

        // Quitting
        logger.fine("Exiting application");
        System.exit(0);
    }

    //
    //
    // Files
    //

    /*
    public static File askToSaveFile(java.io.File fileToSave, String selectedDirectory){
        //logger.entering(""+this.getClass(), "askToSaveFile");

            JFileChooser fileChooser = new JFileChooser();

            // Set in previous save directory
            if (selectedDirectory != null) {
                fileChooser.setCurrentDirectory(new java.io.File(selectedDirectory));
            }

            try {
                // Open filechooser
                int returnVal = fileChooser.showSaveDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();

                    // Ask to overwrite
                    if (file.exists()) {
                        String message = "File '" + file.getName() +
                            "' already exists\nDo you want to overwrite this file?";
                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);

                        if (answer == JOptionPane.YES_OPTION) {
                            // TODO copy file
                            return file.getParent();
                        }
                    } else {
                        saveSettingsFile(file);
                        return file.getParent();
                    }
                }
            } catch (Exception exc) {
                ReportDialog dialog = new ReportDialog(null, true, exc);
                dialog.setVisible(true);
                // if (dialog.returnedValue="Report")
            }
            return null;

    }

    public static File askToSaveFile(java.io.File fileToSave){
        return askToSaveFile(fileToSave, null);
    }
     */

    //
    // XML transformations
    //

    /**
     * Returns one string resulting of xslt transformation.
     * If one error occur, then one ReportDialog show the problem
     *
     * @param params two by two processor parameter list or null
     * @return the xslt output or null if one error occured
     */
    public static String xsl(String xmlBuffer, URL xslURL, String[] params) {
        logger.entering(className, "xsl");

        try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(
                        xslURL.openStream()));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(new StringReader(xmlBuffer));
            StringWriter sw = new StringWriter();
            javax.xml.transform.Result result = new StreamResult(sw);

            // Apply the xsl file to the source file and return the result                 
            if (params != null) {
                for (int i = 0; i < params.length; i += 2) {
                    xformer.setParameter(params[i], params[i + 1]);
                }
            }

            xformer.transform(source, result);
            logger.exiting(className, "xsl");

            return sw.toString();
        } catch (TransformerConfigurationException exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);

            // An error occurred in the XSL file
        } catch (TransformerException exc) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = exc.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        } catch (Exception exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }

        logger.exiting(className, "xsl");

        return null;
    }

    //
    // XML Parsing
    // 

    /** Parses an XML file and returns a DOM document.
     * If validating is true, the contents is validated against the DTD
     * specified in the file.
     */
    public static Document parseXmlFile(String filename, boolean validating) {
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse(new File(filename));

            return doc;
        } catch (SAXException exc) {
            // A parsing error occurred; the xml input is not valid
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        } catch (ParserConfigurationException exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        } catch (IOException exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }

        return null;
    }

    /** Parses an XML string and returns a DOM document.
     * If validating is true, the contents is validated against the DTD
     * specified in the file.
     */
    public static Document parseXmlString(String xmlBuffer, boolean validating) {
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the buffer            
            StringReader r = new StringReader(xmlBuffer);
            Document doc = factory.newDocumentBuilder().parse(new InputSource(r));

            return doc;
        } catch (SAXException exc) {
            // A parsing error occurred; the xml input is not valid
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        } catch (ParserConfigurationException exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        } catch (IOException exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }

        return null;
    }
}
