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
 * Contains user models (with code element) and user functions.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Usercode implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Define a common code part.
     *  
     */
    private fr.jmmc.mf.models.Common _common;

    /**
     * A common representation of every models.
     *  
     */
    private java.util.ArrayList _modelList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Usercode() {
        super();
        _modelList = new ArrayList();
    } //-- fr.jmmc.mf.models.Usercode()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addModel
     * 
     * @param vModel
     */
    public void addModel(fr.jmmc.mf.models.Model vModel)
        throws java.lang.IndexOutOfBoundsException
    {
        _modelList.add(vModel);
    } //-- void addModel(fr.jmmc.mf.models.Model) 

    /**
     * Method addModel
     * 
     * @param index
     * @param vModel
     */
    public void addModel(int index, fr.jmmc.mf.models.Model vModel)
        throws java.lang.IndexOutOfBoundsException
    {
        _modelList.add(index, vModel);
    } //-- void addModel(int, fr.jmmc.mf.models.Model) 

    /**
     * Method clearModel
     */
    public void clearModel()
    {
        _modelList.clear();
    } //-- void clearModel() 

    /**
     * Method enumerateModel
     */
    public java.util.Enumeration enumerateModel()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_modelList.iterator());
    } //-- java.util.Enumeration enumerateModel() 

    /**
     * Returns the value of field 'common'. The field 'common' has
     * the following description: Define a common code part.
     *  
     * 
     * @return the value of field 'common'.
     */
    public fr.jmmc.mf.models.Common getCommon()
    {
        return this._common;
    } //-- fr.jmmc.mf.models.Common getCommon() 

    /**
     * Method getModel
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Model getModel(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _modelList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Model) _modelList.get(index);
    } //-- fr.jmmc.mf.models.Model getModel(int) 

    /**
     * Method getModel
     */
    public fr.jmmc.mf.models.Model[] getModel()
    {
        int size = _modelList.size();
        fr.jmmc.mf.models.Model[] mArray = new fr.jmmc.mf.models.Model[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Model) _modelList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Model[] getModel() 

    /**
     * Method getModelCount
     */
    public int getModelCount()
    {
        return _modelList.size();
    } //-- int getModelCount() 

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
     * Method removeModel
     * 
     * @param vModel
     */
    public boolean removeModel(fr.jmmc.mf.models.Model vModel)
    {
        boolean removed = _modelList.remove(vModel);
        return removed;
    } //-- boolean removeModel(fr.jmmc.mf.models.Model) 

    /**
     * Sets the value of field 'common'. The field 'common' has the
     * following description: Define a common code part.
     *  
     * 
     * @param common the value of field 'common'.
     */
    public void setCommon(fr.jmmc.mf.models.Common common)
    {
        this._common = common;
    } //-- void setCommon(fr.jmmc.mf.models.Common) 

    /**
     * Method setModel
     * 
     * @param index
     * @param vModel
     */
    public void setModel(int index, fr.jmmc.mf.models.Model vModel)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _modelList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _modelList.set(index, vModel);
    } //-- void setModel(int, fr.jmmc.mf.models.Model) 

    /**
     * Method setModel
     * 
     * @param modelArray
     */
    public void setModel(fr.jmmc.mf.models.Model[] modelArray)
    {
        //-- copy array
        _modelList.clear();
        for (int i = 0; i < modelArray.length; i++) {
            _modelList.add(modelArray[i]);
        }
    } //-- void setModel(fr.jmmc.mf.models.Model) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Usercode unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Usercode) Unmarshaller.unmarshal(fr.jmmc.mf.models.Usercode.class, reader);
    } //-- fr.jmmc.mf.models.Usercode unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Usercode"; } 
}
