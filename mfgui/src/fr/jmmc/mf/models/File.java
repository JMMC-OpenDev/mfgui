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
 * File description.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class File implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _href
     */
    private java.lang.String _href;

    /**
     * oitarget representation.
     *  
     */
    private java.util.ArrayList _oitargetList;


      //----------------/
     //- Constructors -/
    //----------------/

    public File() {
        super();
        _oitargetList = new ArrayList();
    } //-- fr.jmmc.mf.models.File()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addOitarget
     * 
     * @param vOitarget
     */
    public void addOitarget(fr.jmmc.mf.models.Oitarget vOitarget)
        throws java.lang.IndexOutOfBoundsException
    {
        _oitargetList.add(vOitarget);
    } //-- void addOitarget(fr.jmmc.mf.models.Oitarget) 

    /**
     * Method addOitarget
     * 
     * @param index
     * @param vOitarget
     */
    public void addOitarget(int index, fr.jmmc.mf.models.Oitarget vOitarget)
        throws java.lang.IndexOutOfBoundsException
    {
        _oitargetList.add(index, vOitarget);
    } //-- void addOitarget(int, fr.jmmc.mf.models.Oitarget) 

    /**
     * Method clearOitarget
     */
    public void clearOitarget()
    {
        _oitargetList.clear();
    } //-- void clearOitarget() 

    /**
     * Method enumerateOitarget
     */
    public java.util.Enumeration enumerateOitarget()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_oitargetList.iterator());
    } //-- java.util.Enumeration enumerateOitarget() 

    /**
     * Returns the value of field 'href'.
     * 
     * @return the value of field 'href'.
     */
    public java.lang.String getHref()
    {
        return this._href;
    } //-- java.lang.String getHref() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method getOitarget
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Oitarget getOitarget(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _oitargetList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Oitarget) _oitargetList.get(index);
    } //-- fr.jmmc.mf.models.Oitarget getOitarget(int) 

    /**
     * Method getOitarget
     */
    public fr.jmmc.mf.models.Oitarget[] getOitarget()
    {
        int size = _oitargetList.size();
        fr.jmmc.mf.models.Oitarget[] mArray = new fr.jmmc.mf.models.Oitarget[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Oitarget) _oitargetList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Oitarget[] getOitarget() 

    /**
     * Method getOitargetCount
     */
    public int getOitargetCount()
    {
        return _oitargetList.size();
    } //-- int getOitargetCount() 

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
     * Method removeOitarget
     * 
     * @param vOitarget
     */
    public boolean removeOitarget(fr.jmmc.mf.models.Oitarget vOitarget)
    {
        boolean removed = _oitargetList.remove(vOitarget);
        return removed;
    } //-- boolean removeOitarget(fr.jmmc.mf.models.Oitarget) 

    /**
     * Sets the value of field 'href'.
     * 
     * @param href the value of field 'href'.
     */
    public void setHref(java.lang.String href)
    {
        this._href = href;
    } //-- void setHref(java.lang.String) 

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method setOitarget
     * 
     * @param index
     * @param vOitarget
     */
    public void setOitarget(int index, fr.jmmc.mf.models.Oitarget vOitarget)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _oitargetList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _oitargetList.set(index, vOitarget);
    } //-- void setOitarget(int, fr.jmmc.mf.models.Oitarget) 

    /**
     * Method setOitarget
     * 
     * @param oitargetArray
     */
    public void setOitarget(fr.jmmc.mf.models.Oitarget[] oitargetArray)
    {
        //-- copy array
        _oitargetList.clear();
        for (int i = 0; i < oitargetArray.length; i++) {
            _oitargetList.add(oitargetArray[i]);
        }
    } //-- void setOitarget(fr.jmmc.mf.models.Oitarget) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.File unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.File) Unmarshaller.unmarshal(fr.jmmc.mf.models.File.class, reader);
    } //-- fr.jmmc.mf.models.File unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "File["+getName()+"]"; } 
}
