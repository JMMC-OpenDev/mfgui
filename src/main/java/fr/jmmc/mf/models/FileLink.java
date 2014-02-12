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
 * File link.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class FileLink implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _fileRef
     */
    private java.lang.Object _fileRef;


      //----------------/
     //- Constructors -/
    //----------------/

    public FileLink() {
        super();
    } //-- fr.jmmc.mf.models.FileLink()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'fileRef'.
     * 
     * @return the value of field 'fileRef'.
     */
    public java.lang.Object getFileRef()
    {
        return this._fileRef;
    } //-- java.lang.Object getFileRef() 

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
     * Sets the value of field 'fileRef'.
     * 
     * @param fileRef the value of field 'fileRef'.
     */
    public void setFileRef(java.lang.Object fileRef)
    {
        this._fileRef = fileRef;
    } //-- void setFileRef(java.lang.Object) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.FileLink unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.FileLink) Unmarshaller.unmarshal(fr.jmmc.mf.models.FileLink.class, reader);
    } //-- fr.jmmc.mf.models.FileLink unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "File["+((File)getFileRef()).getName()+"]"; } 
}
