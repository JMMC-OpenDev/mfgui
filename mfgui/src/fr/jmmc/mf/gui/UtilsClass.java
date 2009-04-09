package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mcs.util.Urls;
import fr.jmmc.mf.models.Message;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.ResponseItem;
import fr.jmmc.mf.models.ResultFile;
import fr.jmmc.mf.models.Settings;
import java.awt.BorderLayout;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.Component;

import java.awt.Image;
import java.io.*;

import java.net.URL;

import java.util.Enumeration;

import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import ptolemy.plot.plotml.PlotMLParser;
import ptolemy.plot.Plot;
import ptolemy.plot.plotml.PlotMLFrame;

/**
 * This class is here just to locate code parts that could be moved under
 * one jmmc.mcs.* package
 */
public class UtilsClass {

    static String className = "fr.jmmc.mf.gui.UtilsClass";
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    static java.util.Hashtable alreadyExpandedFiles = new java.util.Hashtable();

    public static PlotMLFrame getPlotMLFrame(String xmlStr, String plotName) throws Exception {
        PlotMLParser plotMLParser;
        // Construct plot and parse xml
        Plot plot = new Plot();
        plotMLParser = new PlotMLParser(plot);
        logger.finest("Trying to plot next document:\n" + xmlStr);
        plotMLParser.parse(null, xmlStr);
        // Show plot into frame
        return new PlotMLFrame("Plotting " + plotName, plot);
    }

    public static File getPlotMLTSVFile(String ptPlotStr) {
        File f = null;
        try {
            //  . is mandatory in suffix if used by SaveFileAction
            f = File.createTempFile("tsvPlot", ".tsv");
            // Contruct xml document to plot            
            String xmlStr = xsl(ptPlotStr, "fr/jmmc/mf/gui/ptplotToTsv.xsl", null);
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            if (xmlStr != null || xmlStr.length() == 0) {
                out.write(xmlStr);
            } else {
                out.write("error");
            }
            out.close();
        } catch (IOException e) {
            new FeedbackReport(e);
        }
        return f;
    }

    /** Build a Jframe that displays the given png file.
     *
     * @param pngFile
     * @return
     * @throws java.io.IOException
     */
    public static JFrame buildFrameFor(File pngFile) throws IOException {
        JFrame frame = new JFrame();
        JLabel label;
        JPanel p = new JPanel(new BorderLayout());
        Image image = ImageIO.read(pngFile);
        // Use a label to display the image
        label = new JLabel(new ImageIcon(image));
        p.add(label, BorderLayout.CENTER);
        frame.getContentPane().add(p);
        frame.pack();
        return frame;
    }

    /**
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
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                column = table.getColumnModel().getColumn(j);

                comp = headerRenderer.getTableCellRendererComponent(null,
                        column.getHeaderValue(), false, false, 0, j);
                headerWidth = comp.getPreferredSize().width;

                comp = table.getDefaultRenderer(model.getColumnClass(j)).getTableCellRendererComponent(table,
                        model.getValueAt(i, j), false, false, i, j);
                cellWidth = comp.getPreferredSize().width;

                column.setPreferredWidth(Math.min(maxWidth, Math.max(headerWidth, cellWidth)));
            }
        }
    }

    public static void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    public static void marshal(Object objectToMarshal, Writer writer) throws IOException, MappingException, MarshalException, ValidationException {
                // Do marshalling
        URL mappingURL = UtilsClass.class.getClassLoader().getResource("fr/jmmc/mf/gui/mapping.xml");
        logger.fine("Using mapping file :" + mappingURL);
        Marshaller marshaller = new Marshaller(writer);
        // old simple code sometimes break xml elements order then use a mapping file
        Mapping mapping = new Mapping();
        mapping.loadMapping(mappingURL);
        marshaller.setMapping(mapping);
        marshaller.setValidation(false);
        marshaller.marshal(objectToMarshal);
        writer.flush();        
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

    public static final String IMAGE_FITS_DATATYPE="image/fits";
    public static final String IMAGE_PNG_DATATYPE="image/png";

    /**
     * Return base 64 href of given file with given datatype
     * @param filenameToEncode
     * @param dataType
     * @return the base64 buffer
     * @throws java.io.IOException
     */
    public static String getBase64Href(String filenameToEncode, String dataType) throws IOException
    {
        java.io.File fileToEncode = new java.io.File(filenameToEncode);
        return getBase64Href(fileToEncode, dataType);
    }

    public static String getBase64Href(java.io.File fileToEncode, String dataType) throws IOException
    {
        String base64DataType = "data:"+dataType+";base64,";      
        // Create a read-only memory-mapped file
        java.nio.channels.FileChannel roChannel = new java.io.RandomAccessFile(fileToEncode, "r").getChannel();
        java.nio.ByteBuffer roBuf = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY,
                0, (int) roChannel.size());
        return base64DataType + new sun.misc.BASE64Encoder().encode(roBuf);
    }

    /**
     * Decode the base64 encoded file and store it into the given file.
     *
     * @param b64 the base64 encoded file
     * @param outputFile the file to store result
     *
     * @return The given outputFile or null if nothing has been decoded.
     *
     * @throws IOException
     */
    public static File saveBASE64ToFile(String b64, File outputFile)
            throws IOException {
        String[] dataTypes = new String[]{
            IMAGE_FITS_DATATYPE, IMAGE_PNG_DATATYPE
        };
        for (int i = 0; i <= dataTypes.length; i++) {
            String base64DataType;
            if (i < dataTypes.length) {
                base64DataType = "data:" + dataTypes[i] + ";base64,";
            } else {
                base64DataType = "data:" + b64.substring(0, 100).substring(5, b64.indexOf(";base64,")) + ";base64,";
            }

            if (b64.startsWith(base64DataType)) {
                logger.fine("decoding '" + base64DataType + "' file into " + outputFile.getAbsolutePath());
                java.util.StringTokenizer st = new java.util.StringTokenizer(b64.substring(base64DataType.length()));
                StringBuffer sb = new StringBuffer();
                while (st.hasMoreTokens()) {
                    sb.append(st.nextToken());
                }
                byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(sb.toString());
                FileOutputStream out = new FileOutputStream(outputFile);
                out.write(buf);
                out.flush();
                out.close();
                return outputFile;
            }
        }

        logger.severe("Nothing has been decoded for given base64 encoded file\n" + b64.substring(0, b64.indexOf(";base64,")));
        return null;
    }

    /**
     * Extract file from given base64 encoded string.
     *
     * @param b64 base64 buffer
     * @param type extension
     * @return the extracted file or null
     *
     * @throws IOException 
     */
    public static File saveBASE64ToFile(String b64, String type)
            throws IOException {
        java.io.File outputFile = java.io.File.createTempFile("tmpB64File", "." + type);
        outputFile.deleteOnExit();
        if (saveBASE64ToFile(b64, outputFile) == null) {
            return null;
        }
        return outputFile;
    }

    public static File saveBASE64ToFile(fr.jmmc.mf.models.File dataFile, String b64)
            throws IOException {
        Object key = dataFile;

        String filename="tmpOifile";
        String fileExtension=".oifits";
        File tmp = new File(dataFile.getName());
        int dotPos = tmp.getName().lastIndexOf(".");
        if(dotPos>1){
            filename = tmp.getName().substring(0,dotPos);
            fileExtension=tmp.getName().substring(dotPos);
        }

        // Search if this file has already been loaded
        java.io.File outputFile = (java.io.File) alreadyExpandedFiles.get(key);

        if (outputFile == null) {
            // Create temp file.
            outputFile = java.io.File.createTempFile(filename, fileExtension);
            // Delete temp file when program exits.
            outputFile.deleteOnExit();
            alreadyExpandedFiles.put(key, outputFile);
            logger.fine("expanding '" + key + "' into " + outputFile.getAbsolutePath());
            return saveBASE64ToFile(b64, outputFile);
        }else{
            logger.fine("file '" + key + "' was already expanded into " + outputFile.getAbsolutePath());
        }
        return outputFile;
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
        if (object == null) {
            return true;
        }
        if (object.isModified()) {
            // Ask the user if he wants to save modifications
            Object[] options = {"Save", "Cancel", "Don't Save"};
            int result = JOptionPane.showOptionDialog(object.getComponent(),
                    "Do you want to save changes to this document before closing ?\nIf you don't save, your changes will be lost.\n\n",
                    null, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                    options[0]);

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
    public static boolean checkUserModificationToQuit(ModifyAndSaveObject[] objects) {
        logger.entering(className, "checkUserModificationAndQuit");
        for (int i = 0; i < objects.length; i++) {
            if (!askToSaveUserModification(objects[i])) {
                return false;
            }
        }
        return true;
    }

    private static String xsl(Source source, String filepath, String[] params) {
        logger.entering(className, "xsl");
        URL xslURL = UtilsClass.class.getClassLoader().getResource(filepath);
        xslURL = Urls.fixJarURL(xslURL);
        logger.fine("using next url for transformation" + xslURL);
        try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(xslURL.openStream()));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the output
            StringWriter sw = new StringWriter();
            Result result = new StreamResult(sw);

            // Apply the xsl file to the source file and return the result                 
            if (params != null) {
                for (int i = 0; i < params.length; i += 2) {
                    xformer.setParameter(params[i], params[i + 1]);
                }
            }

            // Apply the xsl file to the source file and write the result to the output file
            xformer.transform(source, result);
            return sw.toString();
        } catch (TransformerConfigurationException exc) {
            new FeedbackReport(null, true, exc);
        } catch (TransformerException exc) {
            new FeedbackReport(null, true, exc);
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
        logger.fine("End of transformation ");
        logger.exiting(className, "xsl");
        return null;
    }

    /**
     * Returns one string resulting of xslt transformation.
     * If one error occur, then one FeedbackReport show the problem
     *
     * @param params two by two processor parameter list or null
     * @return the xslt output or null if one error occured
     *public static String xsl(String xmlBuffer, URL xslURL, String[] params)
     */
    public static String xsl(String xmlBuffer, String filepath, String[] params) {
        // Prepare the input
        Source source = new StreamSource(new StringReader(xmlBuffer));
        return xsl(source, filepath, params);
    }

    /**
     * Returns one string resulting of xslt transformation.
     * If one error occur, then one FeedbackReport show the problem
     *
     * @param params two by two processor parameter list or null
     * @return the xslt output or null if one error occured
     */
    public static String xsl(java.io.File inFile, String filepath, String[] params) {
        try {
            // Prepare the input and output files
            Source source = new StreamSource(new FileInputStream(inFile));
            return xsl(source, filepath, params);
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
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
    public static Document parseXmlFile(String filename, boolean validating)
            throws ParserConfigurationException, ParserConfigurationException,
            SAXException, IOException {
        logger.entering(className, "parseXmlString");
        // Create a builder factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        // Create the builder and parse the file
        Document doc = factory.newDocumentBuilder().parse(new File(filename));
        return doc;
    }

    /** Parses an XML string and returns a DOM document.
     * If validating is true, the contents is validated against the DTD
     * specified in the file.
     */
    public static Document parseXmlString(String xmlBuffer, boolean validating)
            throws ParserConfigurationException, IOException, SAXException {
        logger.entering(className, "parseXmlString");
        // Create a builder factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        // Create the builder and parse the buffer
        StringReader r = new StringReader(xmlBuffer);
        Document doc = factory.newDocumentBuilder().parse(new InputSource(r));
        return doc;
    }

    /*
     *  Following section give accesses to response content.
     *
     */
    /** Returns first setting element of given response.
     *
     * @param r response used to find settings into
     * @return the first found settings or null
     */
    public static Settings getSettings(Response r) {
        logger.entering(className, "getSettings");
        ResponseItem[] responseItems = r.getResponseItem();
        for (int i = 0; i < responseItems.length; i++) {
            ResponseItem responseItem = responseItems[i];
            if (responseItem.getSettings() != null) {
                return responseItem.getSettings();
            }
        }
        return null;
    }

    /** Returns first setting element of given response.
     *
     * @param r response used to find settings into
     * @return the first found settings or null
     */
    public static Model getModel(Response r) {
        logger.entering(className, "getSettings");
        ResponseItem[] responseItems = r.getResponseItem();
        for (int i = 0; i < responseItems.length; i++) {
            ResponseItem responseItem = responseItems[i];
            if (responseItem.getModel() != null) {
                return responseItem.getModel();
            }
        }
        return null;
    }

    public static ResultFile[] getResultFiles(Response r) {
        logger.entering(className, "getSettings");
        ResponseItem[] responseItems = r.getResponseItem();
        Vector<ResultFile> v = new Vector();
        for (int i = 0; i < responseItems.length; i++) {
            ResponseItem responseItem = responseItems[i];
            if (responseItem.getResultFile() != null) {
                v.add(responseItem.getResultFile());
            }
        }
        return v.toArray(new ResultFile[]{});
    }

    //@todo add list of supported message types actually
    // empty, INFO, MESSAGE, ERROR, WARNING
    public static String getOutputMsg(Response r) {
        logger.entering(className, "getOutputMsg");
        String str = "";
        ResponseItem[] responseItems = r.getResponseItem();
        for (int i = 0; i < responseItems.length; i++) {
            ResponseItem responseItem = responseItems[i];
            Message m = responseItem.getMessage();
            if (m != null) {
                if (m.getType() == null || m.getType().equalsIgnoreCase("INFO") || m.getType().equalsIgnoreCase("MESSAGE")) {
                    str = str + "\n" + m.getContent();
                }
            }
        }
        return str;
    }

    public static String getErrorMsg(Response r) {
        logger.entering(className, "getErrorMsg");
        String str = "";
        ResponseItem[] responseItems = r.getResponseItem();
        for (int i = 0; i < responseItems.length; i++) {
            ResponseItem responseItem = responseItems[i];
            Message m = responseItem.getMessage();
            if (m != null) {
                if (m.getType() != null) {
                    if ((m.getType().equalsIgnoreCase("ERROR")) || (m.getType().equalsIgnoreCase("WARNING"))) {
                        str = str + "\n" + m.getContent();
                        logger.fine("getErrorMsg find a message of type: " + m.getType());
                    }
                }
            }
        }
        return str;
    }


    public static Parameter[] getParameters(Target t){
        logger.entering(className, "getParameters",t);
        Vector<Parameter> v = new Vector();
        Model[] models = t.getModel();
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            Parameter[] params = model.getParameter();
            for (int j = 0; j < params.length; j++) {
                Parameter parameter = params[j];
                v.add(parameter);
            }
            ParameterLink[] paramLinks= model.getParameterLink();
            for (int j = 0; j < paramLinks.length; j++) {
                ParameterLink parameterLink = paramLinks[j];
                v.add((Parameter)parameterLink.getParameterRef());
            }
        }
        return v.toArray(new Parameter[0]);
    }
}
