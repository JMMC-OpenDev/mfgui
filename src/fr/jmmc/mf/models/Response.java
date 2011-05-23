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
 * Highest element of that generally is a server response of LITpro
 * wrapper.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Response implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _items
     */
    private java.util.ArrayList _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public Response() {
        super();
        _items = new ArrayList();
    } //-- fr.jmmc.mf.models.Response()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addResponseItem
     * 
     * @param vResponseItem
     */
    public void addResponseItem(fr.jmmc.mf.models.ResponseItem vResponseItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.add(vResponseItem);
    } //-- void addResponseItem(fr.jmmc.mf.models.ResponseItem) 

    /**
     * Method addResponseItem
     * 
     * @param index
     * @param vResponseItem
     */
    public void addResponseItem(int index, fr.jmmc.mf.models.ResponseItem vResponseItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.add(index, vResponseItem);
    } //-- void addResponseItem(int, fr.jmmc.mf.models.ResponseItem) 

    /**
     * Method clearResponseItem
     */
    public void clearResponseItem()
    {
        _items.clear();
    } //-- void clearResponseItem() 

    /**
     * Method enumerateResponseItem
     */
    public java.util.Enumeration enumerateResponseItem()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_items.iterator());
    } //-- java.util.Enumeration enumerateResponseItem() 

    /**
     * Method getResponseItem
     * 
     * @param index
     */
    public fr.jmmc.mf.models.ResponseItem getResponseItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.ResponseItem) _items.get(index);
    } //-- fr.jmmc.mf.models.ResponseItem getResponseItem(int) 

    /**
     * Method getResponseItem
     */
    public fr.jmmc.mf.models.ResponseItem[] getResponseItem()
    {
        int size = _items.size();
        fr.jmmc.mf.models.ResponseItem[] mArray = new fr.jmmc.mf.models.ResponseItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.ResponseItem) _items.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.ResponseItem[] getResponseItem() 

    /**
     * Method getResponseItemCount
     */
    public int getResponseItemCount()
    {
        return _items.size();
    } //-- int getResponseItemCount() 

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
     * Method removeResponseItem
     * 
     * @param vResponseItem
     */
    public boolean removeResponseItem(fr.jmmc.mf.models.ResponseItem vResponseItem)
    {
        boolean removed = _items.remove(vResponseItem);
        return removed;
    } //-- boolean removeResponseItem(fr.jmmc.mf.models.ResponseItem) 

    /**
     * Method setResponseItem
     * 
     * @param index
     * @param vResponseItem
     */
    public void setResponseItem(int index, fr.jmmc.mf.models.ResponseItem vResponseItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        _items.set(index, vResponseItem);
    } //-- void setResponseItem(int, fr.jmmc.mf.models.ResponseItem) 

    /**
     * Method setResponseItem
     * 
     * @param responseItemArray
     */
    public void setResponseItem(fr.jmmc.mf.models.ResponseItem[] responseItemArray)
    {
        //-- copy array
        _items.clear();
        for (int i = 0; i < responseItemArray.length; i++) {
            _items.add(responseItemArray[i]);
        }
    } //-- void setResponseItem(fr.jmmc.mf.models.ResponseItem) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Response unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Response) Unmarshaller.unmarshal(fr.jmmc.mf.models.Response.class, reader);
    } //-- fr.jmmc.mf.models.Response unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Response"; } 
}
