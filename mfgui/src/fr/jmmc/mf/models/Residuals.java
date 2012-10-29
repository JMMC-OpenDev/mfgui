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
 * Residuals configuration.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Residuals implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Residual description.
     *  
     */
    private java.util.ArrayList _residualList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Residuals() {
        super();
        _residualList = new ArrayList();
    } //-- fr.jmmc.mf.models.Residuals()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addResidual
     * 
     * @param vResidual
     */
    public void addResidual(fr.jmmc.mf.models.Residual vResidual)
        throws java.lang.IndexOutOfBoundsException
    {
        _residualList.add(vResidual);
    } //-- void addResidual(fr.jmmc.mf.models.Residual) 

    /**
     * Method addResidual
     * 
     * @param index
     * @param vResidual
     */
    public void addResidual(int index, fr.jmmc.mf.models.Residual vResidual)
        throws java.lang.IndexOutOfBoundsException
    {
        _residualList.add(index, vResidual);
    } //-- void addResidual(int, fr.jmmc.mf.models.Residual) 

    /**
     * Method clearResidual
     */
    public void clearResidual()
    {
        _residualList.clear();
    } //-- void clearResidual() 

    /**
     * Method enumerateResidual
     */
    public java.util.Enumeration enumerateResidual()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_residualList.iterator());
    } //-- java.util.Enumeration enumerateResidual() 

    /**
     * Method getResidual
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Residual getResidual(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _residualList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Residual) _residualList.get(index);
    } //-- fr.jmmc.mf.models.Residual getResidual(int) 

    /**
     * Method getResidual
     */
    public fr.jmmc.mf.models.Residual[] getResidual()
    {
        int size = _residualList.size();
        fr.jmmc.mf.models.Residual[] mArray = new fr.jmmc.mf.models.Residual[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Residual) _residualList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Residual[] getResidual() 

    /**
     * Method getResidualCount
     */
    public int getResidualCount()
    {
        return _residualList.size();
    } //-- int getResidualCount() 

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
     * Method removeResidual
     * 
     * @param vResidual
     */
    public boolean removeResidual(fr.jmmc.mf.models.Residual vResidual)
    {
        boolean removed = _residualList.remove(vResidual);
        return removed;
    } //-- boolean removeResidual(fr.jmmc.mf.models.Residual) 

    /**
     * Method setResidual
     * 
     * @param index
     * @param vResidual
     */
    public void setResidual(int index, fr.jmmc.mf.models.Residual vResidual)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _residualList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _residualList.set(index, vResidual);
    } //-- void setResidual(int, fr.jmmc.mf.models.Residual) 

    /**
     * Method setResidual
     * 
     * @param residualArray
     */
    public void setResidual(fr.jmmc.mf.models.Residual[] residualArray)
    {
        //-- copy array
        _residualList.clear();
        for (int i = 0; i < residualArray.length; i++) {
            _residualList.add(residualArray[i]);
        }
    } //-- void setResidual(fr.jmmc.mf.models.Residual) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Residuals unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Residuals) Unmarshaller.unmarshal(fr.jmmc.mf.models.Residuals.class, reader);
    } //-- fr.jmmc.mf.models.Residuals unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Residuals"; } 
}
