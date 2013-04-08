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
 * Contains parameters.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Parameters implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A common representation of every model parameters.
     *  
     */
    private java.util.ArrayList _parameterList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Parameters() {
        super();
        _parameterList = new ArrayList();
    } //-- fr.jmmc.mf.models.Parameters()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addParameter
     * 
     * @param vParameter
     */
    public void addParameter(fr.jmmc.mf.models.Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        _parameterList.add(vParameter);
    } //-- void addParameter(fr.jmmc.mf.models.Parameter) 

    /**
     * Method addParameter
     * 
     * @param index
     * @param vParameter
     */
    public void addParameter(int index, fr.jmmc.mf.models.Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        _parameterList.add(index, vParameter);
    } //-- void addParameter(int, fr.jmmc.mf.models.Parameter) 

    /**
     * Method clearParameter
     */
    public void clearParameter()
    {
        _parameterList.clear();
    } //-- void clearParameter() 

    /**
     * Method enumerateParameter
     */
    public java.util.Enumeration enumerateParameter()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_parameterList.iterator());
    } //-- java.util.Enumeration enumerateParameter() 

    /**
     * Method getParameter
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _parameterList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Parameter) _parameterList.get(index);
    } //-- fr.jmmc.mf.models.Parameter getParameter(int) 

    /**
     * Method getParameter
     */
    public fr.jmmc.mf.models.Parameter[] getParameter()
    {
        int size = _parameterList.size();
        fr.jmmc.mf.models.Parameter[] mArray = new fr.jmmc.mf.models.Parameter[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Parameter) _parameterList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Parameter[] getParameter() 

    /**
     * Method getParameterCount
     */
    public int getParameterCount()
    {
        return _parameterList.size();
    } //-- int getParameterCount() 

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
     * Method removeParameter
     * 
     * @param vParameter
     */
    public boolean removeParameter(fr.jmmc.mf.models.Parameter vParameter)
    {
        boolean removed = _parameterList.remove(vParameter);
        return removed;
    } //-- boolean removeParameter(fr.jmmc.mf.models.Parameter) 

    /**
     * Method setParameter
     * 
     * @param index
     * @param vParameter
     */
    public void setParameter(int index, fr.jmmc.mf.models.Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _parameterList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _parameterList.set(index, vParameter);
    } //-- void setParameter(int, fr.jmmc.mf.models.Parameter) 

    /**
     * Method setParameter
     * 
     * @param parameterArray
     */
    public void setParameter(fr.jmmc.mf.models.Parameter[] parameterArray)
    {
        //-- copy array
        _parameterList.clear();
        for (int i = 0; i < parameterArray.length; i++) {
            _parameterList.add(parameterArray[i]);
        }
    } //-- void setParameter(fr.jmmc.mf.models.Parameter) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Parameters unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Parameters) Unmarshaller.unmarshal(fr.jmmc.mf.models.Parameters.class, reader);
    } //-- fr.jmmc.mf.models.Parameters unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Shared parameters["+getParameterCount()+"]"; } 
}
