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
 * Contains responses.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Responses implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Highest element of what generally is a server response of
     * LITpro wrapper.
     *  
     */
    private java.util.ArrayList _responseList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Responses() {
        super();
        _responseList = new ArrayList();
    } //-- fr.jmmc.mf.models.Responses()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addResponse
     * 
     * @param vResponse
     */
    public void addResponse(fr.jmmc.mf.models.Response vResponse)
        throws java.lang.IndexOutOfBoundsException
    {
        _responseList.add(vResponse);
    } //-- void addResponse(fr.jmmc.mf.models.Response) 

    /**
     * Method addResponse
     * 
     * @param index
     * @param vResponse
     */
    public void addResponse(int index, fr.jmmc.mf.models.Response vResponse)
        throws java.lang.IndexOutOfBoundsException
    {
        _responseList.add(index, vResponse);
    } //-- void addResponse(int, fr.jmmc.mf.models.Response) 

    /**
     * Method clearResponse
     */
    public void clearResponse()
    {
        _responseList.clear();
    } //-- void clearResponse() 

    /**
     * Method enumerateResponse
     */
    public java.util.Enumeration enumerateResponse()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_responseList.iterator());
    } //-- java.util.Enumeration enumerateResponse() 

    /**
     * Method getResponse
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Response getResponse(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _responseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Response) _responseList.get(index);
    } //-- fr.jmmc.mf.models.Response getResponse(int) 

    /**
     * Method getResponse
     */
    public fr.jmmc.mf.models.Response[] getResponse()
    {
        int size = _responseList.size();
        fr.jmmc.mf.models.Response[] mArray = new fr.jmmc.mf.models.Response[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Response) _responseList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Response[] getResponse() 

    /**
     * Method getResponseCount
     */
    public int getResponseCount()
    {
        return _responseList.size();
    } //-- int getResponseCount() 

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
     * Method removeResponse
     * 
     * @param vResponse
     */
    public boolean removeResponse(fr.jmmc.mf.models.Response vResponse)
    {
        boolean removed = _responseList.remove(vResponse);
        return removed;
    } //-- boolean removeResponse(fr.jmmc.mf.models.Response) 

    /**
     * Method setResponse
     * 
     * @param index
     * @param vResponse
     */
    public void setResponse(int index, fr.jmmc.mf.models.Response vResponse)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _responseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _responseList.set(index, vResponse);
    } //-- void setResponse(int, fr.jmmc.mf.models.Response) 

    /**
     * Method setResponse
     * 
     * @param responseArray
     */
    public void setResponse(fr.jmmc.mf.models.Response[] responseArray)
    {
        //-- copy array
        _responseList.clear();
        for (int i = 0; i < responseArray.length; i++) {
            _responseList.add(responseArray[i]);
        }
    } //-- void setResponse(fr.jmmc.mf.models.Response) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Responses unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Responses) Unmarshaller.unmarshal(fr.jmmc.mf.models.Responses.class, reader);
    } //-- fr.jmmc.mf.models.Responses unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Responses"; } 
}
