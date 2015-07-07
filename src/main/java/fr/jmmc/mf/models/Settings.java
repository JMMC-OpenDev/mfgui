/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Highest element of settings.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Settings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Contains files.
     *  
     */
    private fr.jmmc.mf.models.Files _files;

    /**
     * Contains target elements.
     *  
     */
    private fr.jmmc.mf.models.Targets _targets;

    /**
     * Contains parameters.
     *  
     */
    private fr.jmmc.mf.models.Parameters _parameters;

    /**
     * Field _fitter.
     */
    private java.lang.String _fitter;

    /**
     * Contains user models (with code element) and user functions.
     *  
     */
    private fr.jmmc.mf.models.Usercode _usercode;

    /**
     * Contains results.
     *  
     */
    private fr.jmmc.mf.models.Results _results;

    /**
     * Field _userInfo.
     */
    private java.lang.String _userInfo;

    /**
     * Contains responses.
     *  
     */
    private fr.jmmc.mf.models.Responses _responses;


      //----------------/
     //- Constructors -/
    //----------------/

    public Settings() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'files'. The field 'files' has
     * the following description: Contains files.
     *  
     * 
     * @return the value of field 'Files'.
     */
    public fr.jmmc.mf.models.Files getFiles(
    ) {
        return this._files;
    }

    /**
     * Returns the value of field 'fitter'.
     * 
     * @return the value of field 'Fitter'.
     */
    public java.lang.String getFitter(
    ) {
        return this._fitter;
    }

    /**
     * Returns the value of field 'parameters'. The field
     * 'parameters' has the following description: Contains
     * parameters.
     *  
     * 
     * @return the value of field 'Parameters'.
     */
    public fr.jmmc.mf.models.Parameters getParameters(
    ) {
        return this._parameters;
    }

    /**
     * Returns the value of field 'responses'. The field
     * 'responses' has the following description: Contains
     * responses.
     *  
     * 
     * @return the value of field 'Responses'.
     */
    public fr.jmmc.mf.models.Responses getResponses(
    ) {
        return this._responses;
    }

    /**
     * Returns the value of field 'results'. The field 'results'
     * has the following description: Contains results.
     *  
     * 
     * @return the value of field 'Results'.
     */
    public fr.jmmc.mf.models.Results getResults(
    ) {
        return this._results;
    }

    /**
     * Returns the value of field 'targets'. The field 'targets'
     * has the following description: Contains target elements.
     *  
     * 
     * @return the value of field 'Targets'.
     */
    public fr.jmmc.mf.models.Targets getTargets(
    ) {
        return this._targets;
    }

    /**
     * Returns the value of field 'userInfo'.
     * 
     * @return the value of field 'UserInfo'.
     */
    public java.lang.String getUserInfo(
    ) {
        return this._userInfo;
    }

    /**
     * Returns the value of field 'usercode'. The field 'usercode'
     * has the following description: Contains user models (with
     * code element) and user functions.
     *  
     * 
     * @return the value of field 'Usercode'.
     */
    public fr.jmmc.mf.models.Usercode getUsercode(
    ) {
        return this._usercode;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     * Sets the value of field 'files'. The field 'files' has the
     * following description: Contains files.
     *  
     * 
     * @param files the value of field 'files'.
     */
    public void setFiles(
            final fr.jmmc.mf.models.Files files) {
        this._files = files;
    }

    /**
     * Sets the value of field 'fitter'.
     * 
     * @param fitter the value of field 'fitter'.
     */
    public void setFitter(
            final java.lang.String fitter) {
        this._fitter = fitter;
    }

    /**
     * Sets the value of field 'parameters'. The field 'parameters'
     * has the following description: Contains parameters.
     *  
     * 
     * @param parameters the value of field 'parameters'.
     */
    public void setParameters(
            final fr.jmmc.mf.models.Parameters parameters) {
        this._parameters = parameters;
    }

    /**
     * Sets the value of field 'responses'. The field 'responses'
     * has the following description: Contains responses.
     *  
     * 
     * @param responses the value of field 'responses'.
     */
    public void setResponses(
            final fr.jmmc.mf.models.Responses responses) {
        this._responses = responses;
    }

    /**
     * Sets the value of field 'results'. The field 'results' has
     * the following description: Contains results.
     *  
     * 
     * @param results the value of field 'results'.
     */
    public void setResults(
            final fr.jmmc.mf.models.Results results) {
        this._results = results;
    }

    /**
     * Sets the value of field 'targets'. The field 'targets' has
     * the following description: Contains target elements.
     *  
     * 
     * @param targets the value of field 'targets'.
     */
    public void setTargets(
            final fr.jmmc.mf.models.Targets targets) {
        this._targets = targets;
    }

    /**
     * Sets the value of field 'userInfo'.
     * 
     * @param userInfo the value of field 'userInfo'.
     */
    public void setUserInfo(
            final java.lang.String userInfo) {
        this._userInfo = userInfo;
    }

    /**
     * Sets the value of field 'usercode'. The field 'usercode' has
     * the following description: Contains user models (with code
     * element) and user functions.
     *  
     * 
     * @param usercode the value of field 'usercode'.
     */
    public void setUsercode(
            final fr.jmmc.mf.models.Usercode usercode) {
        this._usercode = usercode;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled fr.jmmc.mf.models.Settings
     */
    public static fr.jmmc.mf.models.Settings unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Settings) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Settings.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
