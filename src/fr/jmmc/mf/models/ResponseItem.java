/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class ResponseItem.
 * 
 * @version $Revision$ $Date$
 */
public class ResponseItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Define a message to be displayed.
     *  
     */
    private fr.jmmc.mf.models.Message _message;

    /**
     * Highest element of settings.
     *  
     */
    private fr.jmmc.mf.models.Settings _settings;

    /**
     * A common representation of every models.
     *  
     */
    private fr.jmmc.mf.models.Model _model;

    /**
     * Result file description.
     *  
     */
    private fr.jmmc.mf.models.ResultFile _resultFile;


      //----------------/
     //- Constructors -/
    //----------------/

    public ResponseItem() {
        super();
    } //-- fr.jmmc.mf.models.ResponseItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'message'. The field 'message'
     * has the following description: Define a message to be
     * displayed.
     *  
     * 
     * @return the value of field 'message'.
     */
    public fr.jmmc.mf.models.Message getMessage()
    {
        return this._message;
    } //-- fr.jmmc.mf.models.Message getMessage() 

    /**
     * Returns the value of field 'model'. The field 'model' has
     * the following description: A common representation of every
     * models.
     *  
     * 
     * @return the value of field 'model'.
     */
    public fr.jmmc.mf.models.Model getModel()
    {
        return this._model;
    } //-- fr.jmmc.mf.models.Model getModel() 

    /**
     * Returns the value of field 'resultFile'. The field
     * 'resultFile' has the following description: Result file
     * description.
     *  
     * 
     * @return the value of field 'resultFile'.
     */
    public fr.jmmc.mf.models.ResultFile getResultFile()
    {
        return this._resultFile;
    } //-- fr.jmmc.mf.models.ResultFile getResultFile() 

    /**
     * Returns the value of field 'settings'. The field 'settings'
     * has the following description: Highest element of settings.
     *  
     * 
     * @return the value of field 'settings'.
     */
    public fr.jmmc.mf.models.Settings getSettings()
    {
        return this._settings;
    } //-- fr.jmmc.mf.models.Settings getSettings() 

    /**
     * Sets the value of field 'message'. The field 'message' has
     * the following description: Define a message to be displayed.
     *  
     * 
     * @param message the value of field 'message'.
     */
    public void setMessage(fr.jmmc.mf.models.Message message)
    {
        this._message = message;
    } //-- void setMessage(fr.jmmc.mf.models.Message) 

    /**
     * Sets the value of field 'model'. The field 'model' has the
     * following description: A common representation of every
     * models.
     *  
     * 
     * @param model the value of field 'model'.
     */
    public void setModel(fr.jmmc.mf.models.Model model)
    {
        this._model = model;
    } //-- void setModel(fr.jmmc.mf.models.Model) 

    /**
     * Sets the value of field 'resultFile'. The field 'resultFile'
     * has the following description: Result file description.
     *  
     * 
     * @param resultFile the value of field 'resultFile'.
     */
    public void setResultFile(fr.jmmc.mf.models.ResultFile resultFile)
    {
        this._resultFile = resultFile;
    } //-- void setResultFile(fr.jmmc.mf.models.ResultFile) 

    /**
     * Sets the value of field 'settings'. The field 'settings' has
     * the following description: Highest element of settings.
     *  
     * 
     * @param settings the value of field 'settings'.
     */
    public void setSettings(fr.jmmc.mf.models.Settings settings)
    {
        this._settings = settings;
    } //-- void setSettings(fr.jmmc.mf.models.Settings) 

    public String toString(){ return "ResponseItem"; } 
}
