/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.MessagePane.ConfirmSaveChanges;
import fr.jmmc.jmcs.util.FileUtils;

import fr.jmmc.jmcs.util.UrlUtils;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Message;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.ResponseItem;
import fr.jmmc.mf.models.ResultFile;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.oitools.model.OIFitsFile;
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
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
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
import org.exolab.castor.xml.Unmarshaller;
import ptolemy.plot.plotml.PlotMLParser;
import ptolemy.plot.Plot;
import ptolemy.plot.plotml.PlotMLFrame;

/**
 * This class is here just to locate code parts that could be moved under
 * one jmmc.mcs.* package
 */
public class UtilsClass {

    static final String className = UtilsClass.class.getName();
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    /** Mapping obect used by unmarshalling operations (inited into getMapping()) */
    private static Mapping mapping = null;
    /** Number of first displayed chars if one unmarshalling operation fails */
    protected static final int HeadSizeToDisplayOnError = 80;

    /** This method wal along the targets and check if no one of them are linked to the given file.
     *
     * @param settingsModel model that contains target which can contain given file.
     * @param file file to search.
     * @return true if one target is liked to the given file.
     */
    public static boolean containsFile(SettingsModel settingsModel, fr.jmmc.mf.models.File file) {
        Target[] targets = settingsModel.getRootSettings().getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            FileLink[] fileLinks = target.getFileLink();
            for (int j = 0; j < fileLinks.length; j++) {
                FileLink fileLink = fileLinks[j];
                fr.jmmc.mf.models.File f = (fr.jmmc.mf.models.File) fileLink.getFileRef();
                if (f == file) {
                    return true;
                }
            }
        }
        return false;
    }

    public static PlotMLFrame getPlotMLFrame(String xmlStr, String plotName) {
        PlotMLParser plotMLParser;
        // Construct plot and parse xml
        Plot plot = new Plot();
        plotMLParser = new PlotMLParser(plot);
        logger.finest("Trying to plot next document:\n" + xmlStr);
        try {
            plotMLParser.parse(null, xmlStr);
        } catch (Exception ex) { // only Exception are returned by ptolemy tool
            throw new IllegalStateException("Cannot build plot", ex);
        }

        // Save laf to restore it after ptolemy stuff which change it
        LookAndFeel laf = UIManager.getLookAndFeel();

        // Show plot into frame        
        final PlotMLFrame plotMLFrame = new PlotMLFrame("Plotting " + plotName, plot);
        try {
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException ex) {
            logger.log(Level.WARNING, null, ex);
        }
        return plotMLFrame;
    }

    public static File getPlotMLTSVFile(String ptPlotStr) {
        // Contruct xml document to plot
        String xmlStr = xsl(ptPlotStr, "fr/jmmc/mf/gui/ptplotToTsv.xsl", null);

        // Write content into a temporary file
        File f = FileUtils.getTempFile("tsvPlot", ".tsv");
        try {
            FileUtils.writeFile(f, xmlStr);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can't store plot data into one tsv temporary file", ioe);
        }
        return f;
    }

    /**
     * Build a Jframe that displays the given png file(s).
     *
     * @param pngFile image file
     * @param filenames initial file names (may replace temporary file names of given files)
     * @return a frame that contains the image
     * @throws IllegalStateException
     *      if image can't be read from file
     */
    public static JFrame buildFrameFor(File[] pngFiles, String[] filenames) throws IllegalStateException {
        if (pngFiles == null || pngFiles.length == 0) {
            return null;
        }

        JPanel p;

        if (pngFiles.length == 1) {
            p = new JPanel(new BorderLayout());
            
            JLabel label;

            Image image = getImage(pngFiles[0]);
            if (image == null) {
                return null;
            }
            // Use a label to display the image
            label = new JLabel(new ImageIcon(image));
            p.add(label, BorderLayout.CENTER);
        }else{
            p = new AnimationPanel(pngFiles, filenames);
        }

        JFrame frame = frame = new JFrame();
        frame.getContentPane().add(p);
        frame.pack();
        return frame;
    }  

    /**
     * Load and return one swing image for given file
     * @param pngFile file to load
     * @return the associated image or null if problem occurs
     * @throws IllegalStateException if image can't be read from file
     */
    public static Image getImage(File pngFile) throws IllegalStateException {
        Image image = null;
        try {
            image = ImageIO.read(pngFile);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can't read image data from " + pngFile, ioe);
        }
        return image;
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

    /**
     * Do the same job as marsghal(Object, Writer) but do not throw IO since
     * it only work on memory.
     * @param objectToMarshal
     * @param stringwriter
     * @throws IllegalStateException
     *   if marshalling operation fails
     *   if io occurs
     */
    public static void marshal(Object objectToMarshal, StringWriter stringwriter) throws IllegalStateException {
        try {
            marshal(objectToMarshal, (Writer) stringwriter);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't read input data to build one object properly", ex);
        }
    }

    /**
     * Return one mapping object to be used by unmarshalling operation.
     * @throws IllegalStateException
     *           if unmarshaling can't be done (no marshalling file or invalid)     
     */
    private static synchronized Mapping getMapping() throws IllegalStateException {
        if (mapping == null) {
            URL mappingURL = UtilsClass.class.getClassLoader().getResource("fr/jmmc/mf/gui/mapping.xml");
            try {
                logger.fine("Unmarshal will use mapping file :" + mappingURL);
                // old simple code sometimes break xml elements order then use a mapping file
                mapping = new Mapping();
                mapping.loadMapping(mappingURL);
            } catch (IOException ex) {
                throw new IllegalStateException("Can't find mapping file " + mappingURL, ex);
            } catch (MappingException ex) {
                throw new IllegalStateException("Can't use mapping file" + mappingURL, ex);
            }
        }
        return mapping;
    }

    /**
     * Marshal the given object into the given writer.
     * 
     * @param objectToMarshal
     * @param writer
     * @throws IOException
     *   if object can't be serialized into given file for io reason.
     * @throws IOException if io errors occur onto the writer
     * @throws IllegalArgumentException if marshalling operation fails from given object
     * @throws IllegalStateException if Mapping Exception occurs     
     */
    public static void marshal(Object objectToMarshal, Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        // Do marshalling      
        Marshaller marshaller = new Marshaller(writer);
        try {
            Mapping m = getMapping();
            // TODO check if following statement is still required to be synchronized
            synchronized (m) {
                marshaller.setMapping(m);
            }
            marshaller.setValidation(false);
            marshaller.marshal(objectToMarshal);
            writer.flush();
        } catch (MarshalException ex) {
            throw new IllegalArgumentException("Can't marshal given object", ex);
        } catch (ValidationException ex) {
            throw new IllegalArgumentException("Can't marshal given object", ex);
        } catch (MappingException ex) {
            throw new IllegalStateException("Can't use mapping object", ex);
        }
        logger.fine("End of marshal");
    }

    /**
     * Perfome the unmarsalling operation to transformed serialized object
     * contained in reader into one new Object.
     * 
     * @param c template object
     * @param reader container of xml data
     * @return
     * @throws IllegalStateException if unmarshaling can't be done (no marshalling file or invalid)
     * @throws IllegalArgumentException if input data are not well formed
     */
    public static Object unmarshal(Class c, String xml) throws IllegalStateException, IllegalArgumentException {
        // TODO do not instantiate mapping on each call
        Object o = null;
        Reader reader = new java.io.StringReader(xml);
        try {
            // Do marshalling   
            Unmarshaller unmarshaller = new Unmarshaller();
            Mapping m = getMapping();
            // TODO check if following statement is still required to be synchronized
            synchronized (m) {
                unmarshaller.setMapping(m);
            }
            //marshaller.setMapping(mapping);
            unmarshaller.setValidation(false);
            o = Unmarshaller.unmarshal(c, reader);
            logger.fine("End of unmarshal");
        } catch (MarshalException ex) {
            logger.warning(xml);
            throw new IllegalArgumentException("Can't read input data properly:\n[" + xml.substring(0, HeadSizeToDisplayOnError) + "...]", ex);
        } catch (ValidationException ex) {
            logger.warning(xml);
            throw new IllegalArgumentException("Can't read input data properly:\n[" + xml.substring(0, HeadSizeToDisplayOnError) + "...]", ex);
        } catch (MappingException ex) {
            throw new IllegalStateException("Can't use mapping object", ex);
        }
        return o;
    }

    /** 
     * Clone the given object using castor specific machinery.
     * @param o
     * @return 
     */
    public static Object clone(Object o) {
        // Clone selected object using castor marshalling
        StringWriter writer = new StringWriter();
        UtilsClass.marshal(o, writer);
        return UtilsClass.unmarshal(o.getClass(), writer.toString());
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
    public static final String IMAGE_FITS_DATATYPE = "image/fits";
    public static final String IMAGE_PNG_DATATYPE = "image/png";

    /**
     * Read the given file and return one href containing base 64 encoded data.
     * @param filenameToEncode name of file to encode in base64
     * @param dataType mime type of given file to insert into href
     * @return the base64 buffer
     * @throws IllegalStateException
     *         if one io occurs or converter fails
     */
    public static String getBase64Href(String filenameToEncode, String dataType)
            throws IllegalStateException {
        java.io.File fileToEncode = new java.io.File(filenameToEncode);
        return getBase64Href(fileToEncode, dataType);
    }

    /**
     * Read the given file and return one href containing base 64 encoded data.
     * @param fileToEncode file to encode in base64
     * @param dataType mime type of given file to insert into href
     * @return the base64 buffer
     * @throws IllegalStateException
     *         if one io occurs or converter fails
     */
    public static String getBase64Href(java.io.File fileToEncode, String dataType)
            throws IllegalStateException {
        try {
            String base64DataType = "data:" + dataType + ";base64,";
            // Create a read-only memory-mapped file
            java.nio.channels.FileChannel roChannel = new java.io.RandomAccessFile(fileToEncode, "r").getChannel();
            java.nio.ByteBuffer roBuf = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size());
            return base64DataType + new sun.misc.BASE64Encoder().encode(roBuf);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't encode file '" + fileToEncode.getAbsolutePath() + "' in base64", ex);
        }
    }

    /**
     * Decode the base64 encoded file and return the associated original buffer.
     *
     * @param b64 the base64 encoded file
     *
     * @return The original file content.
     *  @ todo do cleanup with probably duplicated mime type constants...
     * @throws IOException
     */
    public static String saveBASE64ToString(String b64) {
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
                logger.fine("start of decoding '" + base64DataType);
                java.util.StringTokenizer st = new java.util.StringTokenizer(b64.substring(base64DataType.length()));

                StringBuilder sb = new StringBuilder(base64DataType.length());
                while (st.hasMoreTokens()) {
                    sb.append(st.nextToken());
                }
                ByteArrayOutputStream out = null;
                GZIPInputStream gzipInputStream = null;
                try {
                    byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(sb.toString());
                    if (base64DataType.contains("x-gzip")) {
                        logger.fine("base64 file was gzipped, unzipping'" + base64DataType);
                        gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(buf));
                        out = new ByteArrayOutputStream(base64DataType.length());
                        // Transfer bytes from the compressed file to the output file
                        byte[] b = new byte[1024];
                        int len;
                        while ((len = gzipInputStream.read(b)) > 0) {
                            out.write(b, 0, len);
                        }
                    } else {
                        out.write(buf);
                    }

                } catch (IOException ioe) {
                    throw new IllegalStateException("Can't manage base64 data in memory", ioe);
                } finally {
                    FileUtils.closeStream(gzipInputStream);
                    FileUtils.closeStream(out);
                }
                logger.fine("end of decoding '" + base64DataType);
                logger.finest("decoded content is: '" + out + "'");
                return out.toString();
            }
        }

        logger.severe("Nothing has been decoded for given base64 encoded file\n" + b64.substring(0, b64.indexOf(";base64,")));
        return null;
    }

    /**
     * Decode the base64 encoded file and store it into the given file.
     *
     * @param b64 the base64 encoded file
     * @param outputFile the file to store result
     *
     * @return The given outputFile or null if nothing has been decoded.
     *
     */
    public static File saveBASE64ToFile(String b64, File outputFile) {
        String[] dataTypes = new String[]{
            IMAGE_FITS_DATATYPE, IMAGE_PNG_DATATYPE
        };
        for (int i = 0; i <= dataTypes.length; i++) {
            String base64DataType;
            if (i < dataTypes.length) {
                base64DataType = "data:" + dataTypes[i] + ";base64,";
            } else {
                base64DataType = "data:" + b64.substring(0, Math.min(100, b64.length())).substring(5, b64.indexOf(";base64,")) + ";base64,";
            }

            if (b64.startsWith(base64DataType)) {
                logger.fine("start of decoding '" + base64DataType + "' file into " + outputFile.getAbsolutePath());
                java.util.StringTokenizer st = new java.util.StringTokenizer(b64.substring(base64DataType.length()));
                StringBuilder sb = new StringBuilder(base64DataType.length());
                while (st.hasMoreTokens()) {
                    sb.append(st.nextToken());
                }
                // This should not occur because data should have been computed by software only
                FileOutputStream out = null;
                try {
                    byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(sb.toString());
                    out = new FileOutputStream(outputFile);
                    out.write(buf);
                } catch (IOException ioe) {
                    throw new IllegalArgumentException("Can't write data into following file '" + outputFile + "'", ioe);
                } finally {
                    FileUtils.closeStream(out);
                }
                logger.fine("end of decoding '" + base64DataType + "' file into " + outputFile.getAbsolutePath());
                return outputFile;
            }
        }

        logger.severe("Nothing has been decoded for given base64 encoded file\n" + b64.substring(0, b64.indexOf(";base64,")));
        return null;
    }

    /**
     * Decode base64 encoded string and store it into one temporary file.
     *
     * @param b64 base64 buffer
     * @param type extension
     * @return the temporary file with decoded data or null
     *
     * @throws IOException
     */
    public static File saveBASE64ToFile(String b64, String type) {
        java.io.File outputFile = FileUtils.getTempFile("tmpB64File", "." + type);
        outputFile.deleteOnExit();
        if (saveBASE64ToFile(b64, outputFile) == null) {
            return null;
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

        if (object != null && object.isModified()) {

            // Ask the user if he wants to save modifications
            final ConfirmSaveChanges result = MessagePane.showConfirmSaveChangesBeforeClosing();

            // Handle user choice
            switch (result) {
                // If the user clicked the "Save" button, save and exit
                case Save:
                    object.save();
                    // assert that file has been saved
                    if (object.isModified()) {
                        //ask again
                        return askToSaveUserModification(object);
                    }
                    break;

                // If the user clicked the "Don't Save" button, exit
                case Ignore:
                    break;

                // If the user clicked the "Cancel" button or pressed 'esc' key, don't exit
                case Cancel:
                default: // Any other case
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
    private static TransformerFactory factory_ = null;

    private static TransformerFactory getTransformerFactoryInstance() throws TransformerConfigurationException {
        if (factory_ == null) {
            // Create transformer factory
            factory_ = TransformerFactory.newInstance();

            // @todo try to suppress this kind of workarround
            // Allow use of xslt with SECURE set to False in JNLP mode
            try {
                System.setSecurityManager(null);
            } catch (SecurityException se) {
                // This case occurs with java netx and
                // OpenJDK Runtime Environment (IcedTea6 1.6) (rhel-1.13.b16.el5-x86_64)
                logger.warning("Can't set security manager to null");
            }

            // allow use of extensions
            factory_.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, false);
        }

        return factory_;
    }

    private static String xsl(Source source, String filepath, String[] params) {
        logger.entering(className, "xsl");
        URL xslURL = UtilsClass.class.getClassLoader().getResource(filepath);
        xslURL = UrlUtils.fixJarURL(xslURL);
        logger.fine("using next url for transformation" + xslURL);
        try {

            // Use the factory to create a template containing the xsl file
            Templates template = getTransformerFactoryInstance().newTemplates(new StreamSource(xslURL.openStream()));

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
            logger.fine("End of transformation ");
            return sw.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Can't use xslt function", ex);
        } catch (TransformerException ex) {
            throw new IllegalStateException("Can't use xslt function", ex);
        } finally {
            logger.exiting(className, "xsl");

        }
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
        // Prepare the input and output files
        Source source = null;
        try {
            source = new StreamSource(new FileInputStream(inFile));
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }

        logger.exiting(className, "xsl");
        return xsl(source, filepath, params);
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
                if (m.getType() == null
                        || m.getType().equalsIgnoreCase("INFO")
                        || m.getType().equalsIgnoreCase("MESSAGE")) {
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
                    if ((m.getType().equalsIgnoreCase("ERROR"))
                            || m.getType().equalsIgnoreCase("USAGE")
                            || m.getType().equalsIgnoreCase("WARNING")) {
                        str = str + "\n" + m.getContent();
                        logger.fine("getErrorMsg find a message of type: " + m.getType());
                    }
                }
            }
        }
        return str;
    }

    /** Return the parameter and source of shared parameters for the given target.
     *
     * @param t the target to search parameters into
     * @return one parameter list
     */
    public static Parameter[] getParameters(Target t) {
        logger.entering(className, "getParameters", t);
        Vector<Parameter> v = new Vector();
        Model[] models = t.getModel();
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            Parameter[] params = model.getParameter();
            for (int j = 0; j < params.length; j++) {
                Parameter parameter = params[j];
                v.add(parameter);
            }
            ParameterLink[] paramLinks = model.getParameterLink();
            for (int j = 0; j < paramLinks.length; j++) {
                ParameterLink parameterLink = paramLinks[j];
                v.add((Parameter) parameterLink.getParameterRef());
            }
        }
        return v.toArray(new Parameter[0]);
    }

    /**
     * Returns the list of model that contains some parameterLink that point to
     * the given parameter.
     * @param settingsModel main settingsModel to search into.
     * @param parameter is one shared parameter
     * @return model list
     */
    public static Model[] getSharedParameterOwners(SettingsModel settingsModel, Parameter sharedParameter) {
        logger.entering(className, "getOwners");
        Vector<Model> v = new Vector();

        Target[] targets = settingsModel.getRootSettings().getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                ParameterLink[] paramLinks = model.getParameterLink();
                for (int k = 0; k < paramLinks.length; k++) {
                    ParameterLink paramLink = paramLinks[k];
                    if (paramLink.getParameterRef() == sharedParameter) {
                        if (!v.contains(model)) {
                            v.add(model);
                        }
                    }
                }
            }
        }


        return v.toArray(new Model[0]);
    }

    /** Return a string that describe the rho theta information of the given
     * model.
     * @param m
     * @return the rho theta informations
     */
    public static String getRhoTheta(Model m) {
        // compute rho theta from x y parameters
        double x = 0;
        double y = 0;

        Parameter[] params = m.getParameter();
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = params[i];
            if (parameter.getType().equals("x")) {
                x = parameter.getValue();
            }
            if (parameter.getType().equals("y")) {
                y = parameter.getValue();
            }
        }
        ParameterLink[] paramLinks = m.getParameterLink();
        for (int i = 0; i < paramLinks.length; i++) {
            ParameterLink parameterLink = paramLinks[i];
            Parameter parameter = (Parameter) parameterLink.getParameterRef();
            if (parameterLink.getType().equals("x")) {
                x = parameter.getValue();
            }
            if (parameterLink.getType().equals("y")) {
                y = parameter.getValue();
            }
        }

        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        return "rho='" + nf.format(getRho(x, y)) + "' PA='" + nf.format(getTheta(x, y)) + "'";
    }

    // TODO move into jmal
    public static double getRho(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    // TODO move into jmal
    public static double getTheta(double x, double y) {
        double r = Math.atan2(x, y) / (Math.PI / 180);
        if (r >= 0) {
            return r;
        }
        return r + 360.0;
    }
}
