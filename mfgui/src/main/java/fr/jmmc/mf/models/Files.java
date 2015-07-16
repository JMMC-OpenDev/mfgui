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
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Contains files.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Files implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * File description.
     *  
     */
    private java.util.ArrayList _fileList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Files() {
        super();
        _fileList = new ArrayList();
    } //-- fr.jmmc.mf.models.Files()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addFile
     * 
     * @param vFile
     */
    public void addFile(fr.jmmc.mf.models.File vFile)
        throws java.lang.IndexOutOfBoundsException
    {
        _fileList.add(vFile);
    } //-- void addFile(fr.jmmc.mf.models.File) 

    /**
     * Method addFile
     * 
     * @param index
     * @param vFile
     */
    public void addFile(int index, fr.jmmc.mf.models.File vFile)
        throws java.lang.IndexOutOfBoundsException
    {
        _fileList.add(index, vFile);
    } //-- void addFile(int, fr.jmmc.mf.models.File) 

    /**
     * Method clearFile
     */
    public void clearFile()
    {
        _fileList.clear();
    } //-- void clearFile() 

    /**
     * Method enumerateFile
     */
    public java.util.Enumeration enumerateFile()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_fileList.iterator());
    } //-- java.util.Enumeration enumerateFile() 

    /**
     * Method getFile
     * 
     * @param index
     */
    public fr.jmmc.mf.models.File getFile(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fileList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.File) _fileList.get(index);
    } //-- fr.jmmc.mf.models.File getFile(int) 

    /**
     * Method getFile
     */
    public fr.jmmc.mf.models.File[] getFile()
    {
        int size = _fileList.size();
        fr.jmmc.mf.models.File[] mArray = new fr.jmmc.mf.models.File[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.File) _fileList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.File[] getFile() 

    /**
     * Method getFileCount
     */
    public int getFileCount()
    {
        return _fileList.size();
    } //-- int getFileCount() 

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
     * Method removeFile
     * 
     * @param vFile
     */
    public boolean removeFile(fr.jmmc.mf.models.File vFile)
    {
        boolean removed = _fileList.remove(vFile);
        return removed;
    } //-- boolean removeFile(fr.jmmc.mf.models.File) 

    /**
     * Method setFile
     * 
     * @param index
     * @param vFile
     */
    public void setFile(int index, fr.jmmc.mf.models.File vFile)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fileList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _fileList.set(index, vFile);
    } //-- void setFile(int, fr.jmmc.mf.models.File) 

    /**
     * Method setFile
     * 
     * @param fileArray
     */
    public void setFile(fr.jmmc.mf.models.File[] fileArray)
    {
        //-- copy array
        _fileList.clear();
        for (int i = 0; i < fileArray.length; i++) {
            _fileList.add(fileArray[i]);
        }
    } //-- void setFile(fr.jmmc.mf.models.File) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Files unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Files) Unmarshaller.unmarshal(fr.jmmc.mf.models.Files.class, reader);
    } //-- fr.jmmc.mf.models.Files unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Files"; } 
}
