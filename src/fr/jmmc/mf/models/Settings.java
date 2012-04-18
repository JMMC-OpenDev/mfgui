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

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Highest element of settings.
 *  
 * 
 * @version $Revision$ $Date$
 */
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
     * Field _fitter
     */
    private java.lang.String _fitter;

    /**
     * Contains results.
     *  
     */
    private fr.jmmc.mf.models.Results _results;

    /**
     * Field _userInfo
     */
    private java.lang.String _userInfo;


      //----------------/
     //- Constructors -/
    //----------------/

    public Settings() {
        super();
    } //-- fr.jmmc.mf.models.Settings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'files'. The field 'files' has
     * the following description: Contains files.
     *  
     * 
     * @return the value of field 'files'.
     */
    public fr.jmmc.mf.models.Files getFiles()
    {
        return this._files;
    } //-- fr.jmmc.mf.models.Files getFiles() 

    /**
     * Returns the value of field 'fitter'.
     * 
     * @return the value of field 'fitter'.
     */
    public java.lang.String getFitter()
    {
        return this._fitter;
    } //-- java.lang.String getFitter() 

    /**
     * Returns the value of field 'parameters'. The field
     * 'parameters' has the following description: Contains
     * parameters.
     *  
     * 
     * @return the value of field 'parameters'.
     */
    public fr.jmmc.mf.models.Parameters getParameters()
    {
        return this._parameters;
    } //-- fr.jmmc.mf.models.Parameters getParameters() 

    /**
     * Returns the value of field 'results'. The field 'results'
     * has the following description: Contains results.
     *  
     * 
     * @return the value of field 'results'.
     */
    public fr.jmmc.mf.models.Results getResults()
    {
        return this._results;
    } //-- fr.jmmc.mf.models.Results getResults() 

    /**
     * Returns the value of field 'targets'. The field 'targets'
     * has the following description: Contains target elements.
     *  
     * 
     * @return the value of field 'targets'.
     */
    public fr.jmmc.mf.models.Targets getTargets()
    {
        return this._targets;
    } //-- fr.jmmc.mf.models.Targets getTargets() 

    /**
     * Returns the value of field 'userInfo'.
     * 
     * @return the value of field 'userInfo'.
     */
    public java.lang.String getUserInfo()
    {
        return this._userInfo;
    } //-- java.lang.String getUserInfo() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'files'. The field 'files' has the
     * following description: Contains files.
     *  
     * 
     * @param files the value of field 'files'.
     */
    public void setFiles(fr.jmmc.mf.models.Files files)
    {
        this._files = files;
    } //-- void setFiles(fr.jmmc.mf.models.Files) 

    /**
     * Sets the value of field 'fitter'.
     * 
     * @param fitter the value of field 'fitter'.
     */
    public void setFitter(java.lang.String fitter)
    {
        this._fitter = fitter;
    } //-- void setFitter(java.lang.String) 

    /**
     * Sets the value of field 'parameters'. The field 'parameters'
     * has the following description: Contains parameters.
     *  
     * 
     * @param parameters the value of field 'parameters'.
     */
    public void setParameters(fr.jmmc.mf.models.Parameters parameters)
    {
        this._parameters = parameters;
    } //-- void setParameters(fr.jmmc.mf.models.Parameters) 

    /**
     * Sets the value of field 'results'. The field 'results' has
     * the following description: Contains results.
     *  
     * 
     * @param results the value of field 'results'.
     */
    public void setResults(fr.jmmc.mf.models.Results results)
    {
        this._results = results;
    } //-- void setResults(fr.jmmc.mf.models.Results) 

    /**
     * Sets the value of field 'targets'. The field 'targets' has
     * the following description: Contains target elements.
     *  
     * 
     * @param targets the value of field 'targets'.
     */
    public void setTargets(fr.jmmc.mf.models.Targets targets)
    {
        this._targets = targets;
    } //-- void setTargets(fr.jmmc.mf.models.Targets) 

    /**
     * Sets the value of field 'userInfo'.
     * 
     * @param userInfo the value of field 'userInfo'.
     */
    public void setUserInfo(java.lang.String userInfo)
    {
        this._userInfo = userInfo;
    } //-- void setUserInfo(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Settings unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Settings) Unmarshaller.unmarshal(fr.jmmc.mf.models.Settings.class, reader);
    } //-- fr.jmmc.mf.models.Settings unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Settings"; } 
}
