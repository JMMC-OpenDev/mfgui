/*
 * ResultPanel.java
 *
 * Created on 16 fevrier 2007, 08:40
 */

package fr.jmmc.mf.gui;
import fr.jmmc.mcs.gui.ReportDialog;

import fr.jmmc.mf.models.Result;

import javax.xml.xpath.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.net.URL;


/**
 *
 * @author  mella
 */
public class ResultPanel extends javax.swing.JPanel {
   static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("fr.jmmc.mf.gui.ResultPanel");
     //ParametersTableModel parametersTableModel;
     Result current;
    SettingsViewerInterface settingsViewer=null;    
    
    /** Creates new form ResultPanel */
    public ResultPanel(SettingsViewerInterface viewer) {
        initComponents();
    }
    
    public static String xsl(String xmlBuffer, URL xslURL) {
            
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
    
                // Apply the xsl file to the source file and write the result to the output file
                xformer.transform(source, result);
                
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
            return null;
        }    
    
    public void show(Result r){
        current=r;
        String xmlContent=""+current.getAnyObject();
         try {
             //
             // Do a simple XPATH request
             /* 
            // Parse the XML as a W3C document.
            DocumentBuilder builder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
            java.io.StringReader reader = new java.io.StringReader(xmlContent);
            Document document = builder.parse(new InputSource(reader));
   
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = "//covar";

            // Obtain the element as a String.
            String covarStr = (String)
                xpath.evaluate(expression, document);
            String htmlContent="<html>"+covarStr+"</html>";
            resultTextArea.setText(htmlContent);
            */
            
            //
             // Do a simple xslt transform
             // 
            java.net.URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/resultToHtml.xsl");
            String htmlStr = xsl(xmlContent, url);
            resultEditorPane.setContentType("text/html");            
            resultEditorPane.setText(htmlStr);  
            resultTextArea.setText(htmlStr);
            
            
        } catch (Exception exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
        
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultEditorPane = new javax.swing.JEditorPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));

        setBorder(javax.swing.BorderFactory.createTitledBorder("Result panel"));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jScrollPane2.setViewportView(resultEditorPane);

        jPanel1.add(jScrollPane2);

        jTabbedPane1.addTab("Result", jPanel1);

        resultTextArea.setColumns(20);
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setRows(5);
        jScrollPane1.setViewportView(resultTextArea);

        jTabbedPane1.addTab("xml", jScrollPane1);

        add(jTabbedPane1);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JEditorPane resultEditorPane;
    private javax.swing.JTextArea resultTextArea;
    // End of variables declaration//GEN-END:variables
    
}
