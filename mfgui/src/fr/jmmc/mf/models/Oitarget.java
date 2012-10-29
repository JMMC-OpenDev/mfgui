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
 * oitarget representation.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Oitarget implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _target
     */
    private java.lang.String _target;

    /**
     * Field _infos
     */
    private java.lang.String _infos;


      //----------------/
     //- Constructors -/
    //----------------/

    public Oitarget() {
        super();
    } //-- fr.jmmc.mf.models.Oitarget()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'infos'.
     * 
     * @return the value of field 'infos'.
     */
    public java.lang.String getInfos()
    {
        return this._infos;
    } //-- java.lang.String getInfos() 

    /**
     * Returns the value of field 'target'.
     * 
     * @return the value of field 'target'.
     */
    public java.lang.String getTarget()
    {
        return this._target;
    } //-- java.lang.String getTarget() 

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
     * Sets the value of field 'infos'.
     * 
     * @param infos the value of field 'infos'.
     */
    public void setInfos(java.lang.String infos)
    {
        this._infos = infos;
    } //-- void setInfos(java.lang.String) 

    /**
     * Sets the value of field 'target'.
     * 
     * @param target the value of field 'target'.
     */
    public void setTarget(java.lang.String target)
    {
        this._target = target;
    } //-- void setTarget(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Oitarget unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Oitarget) Unmarshaller.unmarshal(fr.jmmc.mf.models.Oitarget.class, reader);
    } //-- fr.jmmc.mf.models.Oitarget unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Oitarget"; } 
}
