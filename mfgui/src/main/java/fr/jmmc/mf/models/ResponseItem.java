/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Class ResponseItem.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ResponseItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

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
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue(
    ) {
        return this._choiceValue;
    }

    /**
     * Returns the value of field 'message'. The field 'message'
     * has the following description: Define a message to be
     * displayed.
     *  
     * 
     * @return the value of field 'Message'.
     */
    public fr.jmmc.mf.models.Message getMessage(
    ) {
        return this._message;
    }

    /**
     * Returns the value of field 'model'. The field 'model' has
     * the following description: A common representation of every
     * models.
     *  
     * 
     * @return the value of field 'Model'.
     */
    public fr.jmmc.mf.models.Model getModel(
    ) {
        return this._model;
    }

    /**
     * Returns the value of field 'resultFile'. The field
     * 'resultFile' has the following description: Result file
     * description.
     *  
     * 
     * @return the value of field 'ResultFile'.
     */
    public fr.jmmc.mf.models.ResultFile getResultFile(
    ) {
        return this._resultFile;
    }

    /**
     * Returns the value of field 'settings'. The field 'settings'
     * has the following description: Highest element of settings.
     *  
     * 
     * @return the value of field 'Settings'.
     */
    public fr.jmmc.mf.models.Settings getSettings(
    ) {
        return this._settings;
    }

    /**
     * Sets the value of field 'message'. The field 'message' has
     * the following description: Define a message to be displayed.
     *  
     * 
     * @param message the value of field 'message'.
     */
    public void setMessage(
            final fr.jmmc.mf.models.Message message) {
        this._message = message;
        this._choiceValue = message;
    }

    /**
     * Sets the value of field 'model'. The field 'model' has the
     * following description: A common representation of every
     * models.
     *  
     * 
     * @param model the value of field 'model'.
     */
    public void setModel(
            final fr.jmmc.mf.models.Model model) {
        this._model = model;
        this._choiceValue = model;
    }

    /**
     * Sets the value of field 'resultFile'. The field 'resultFile'
     * has the following description: Result file description.
     *  
     * 
     * @param resultFile the value of field 'resultFile'.
     */
    public void setResultFile(
            final fr.jmmc.mf.models.ResultFile resultFile) {
        this._resultFile = resultFile;
        this._choiceValue = resultFile;
    }

    /**
     * Sets the value of field 'settings'. The field 'settings' has
     * the following description: Highest element of settings.
     *  
     * 
     * @param settings the value of field 'settings'.
     */
    public void setSettings(
            final fr.jmmc.mf.models.Settings settings) {
        this._settings = settings;
        this._choiceValue = settings;
    }

    public String toString(){ return "ResponseItem"; } 
}
