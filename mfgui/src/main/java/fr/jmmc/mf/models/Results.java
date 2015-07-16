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
 * Contains results.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Results implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Result of a fit section.
     *  
     */
    private java.util.ArrayList _resultList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Results() {
        super();
        _resultList = new ArrayList();
    } //-- fr.jmmc.mf.models.Results()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addResult
     * 
     * @param vResult
     */
    public void addResult(fr.jmmc.mf.models.Result vResult)
        throws java.lang.IndexOutOfBoundsException
    {
        _resultList.add(vResult);
    } //-- void addResult(fr.jmmc.mf.models.Result) 

    /**
     * Method addResult
     * 
     * @param index
     * @param vResult
     */
    public void addResult(int index, fr.jmmc.mf.models.Result vResult)
        throws java.lang.IndexOutOfBoundsException
    {
        _resultList.add(index, vResult);
    } //-- void addResult(int, fr.jmmc.mf.models.Result) 

    /**
     * Method clearResult
     */
    public void clearResult()
    {
        _resultList.clear();
    } //-- void clearResult() 

    /**
     * Method enumerateResult
     */
    public java.util.Enumeration enumerateResult()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_resultList.iterator());
    } //-- java.util.Enumeration enumerateResult() 

    /**
     * Method getResult
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Result getResult(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _resultList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Result) _resultList.get(index);
    } //-- fr.jmmc.mf.models.Result getResult(int) 

    /**
     * Method getResult
     */
    public fr.jmmc.mf.models.Result[] getResult()
    {
        int size = _resultList.size();
        fr.jmmc.mf.models.Result[] mArray = new fr.jmmc.mf.models.Result[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Result) _resultList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Result[] getResult() 

    /**
     * Method getResultCount
     */
    public int getResultCount()
    {
        return _resultList.size();
    } //-- int getResultCount() 

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
     * Method removeResult
     * 
     * @param vResult
     */
    public boolean removeResult(fr.jmmc.mf.models.Result vResult)
    {
        boolean removed = _resultList.remove(vResult);
        return removed;
    } //-- boolean removeResult(fr.jmmc.mf.models.Result) 

    /**
     * Method setResult
     * 
     * @param index
     * @param vResult
     */
    public void setResult(int index, fr.jmmc.mf.models.Result vResult)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _resultList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _resultList.set(index, vResult);
    } //-- void setResult(int, fr.jmmc.mf.models.Result) 

    /**
     * Method setResult
     * 
     * @param resultArray
     */
    public void setResult(fr.jmmc.mf.models.Result[] resultArray)
    {
        //-- copy array
        _resultList.clear();
        for (int i = 0; i < resultArray.length; i++) {
            _resultList.add(resultArray[i]);
        }
    } //-- void setResult(fr.jmmc.mf.models.Result) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Results unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Results) Unmarshaller.unmarshal(fr.jmmc.mf.models.Results.class, reader);
    } //-- fr.jmmc.mf.models.Results unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Results"; } 
}
