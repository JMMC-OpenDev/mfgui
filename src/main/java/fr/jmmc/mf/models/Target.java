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
 * Target representation.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Target implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ident
     */
    private java.lang.String _ident;

    /**
     * File link.
     *  
     */
    private java.util.ArrayList _fileLinkList;

    /**
     * A common representation of every models.
     *  
     */
    private java.util.ArrayList _modelList;

    /**
     * Field _normalize
     */
    private boolean _normalize;

    /**
     * keeps track of state for field: _normalize
     */
    private boolean _has_normalize;

    /**
     * Residuals configuration.
     *  
     */
    private fr.jmmc.mf.models.Residuals _residuals;


      //----------------/
     //- Constructors -/
    //----------------/

    public Target() {
        super();
        _fileLinkList = new ArrayList();
        _modelList = new ArrayList();
    } //-- fr.jmmc.mf.models.Target()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addFileLink
     * 
     * @param vFileLink
     */
    public void addFileLink(fr.jmmc.mf.models.FileLink vFileLink)
        throws java.lang.IndexOutOfBoundsException
    {
        _fileLinkList.add(vFileLink);
    } //-- void addFileLink(fr.jmmc.mf.models.FileLink) 

    /**
     * Method addFileLink
     * 
     * @param index
     * @param vFileLink
     */
    public void addFileLink(int index, fr.jmmc.mf.models.FileLink vFileLink)
        throws java.lang.IndexOutOfBoundsException
    {
        _fileLinkList.add(index, vFileLink);
    } //-- void addFileLink(int, fr.jmmc.mf.models.FileLink) 

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
     * Method clearFileLink
     */
    public void clearFileLink()
    {
        _fileLinkList.clear();
    } //-- void clearFileLink() 

    /**
     * Method clearModel
     */
    public void clearModel()
    {
        _modelList.clear();
    } //-- void clearModel() 

    /**
     * Method deleteNormalize
     */
    public void deleteNormalize()
    {
        this._has_normalize= false;
    } //-- void deleteNormalize() 

    /**
     * Method enumerateFileLink
     */
    public java.util.Enumeration enumerateFileLink()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_fileLinkList.iterator());
    } //-- java.util.Enumeration enumerateFileLink() 

    /**
     * Method enumerateModel
     */
    public java.util.Enumeration enumerateModel()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_modelList.iterator());
    } //-- java.util.Enumeration enumerateModel() 

    /**
     * Method getFileLink
     * 
     * @param index
     */
    public fr.jmmc.mf.models.FileLink getFileLink(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fileLinkList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.FileLink) _fileLinkList.get(index);
    } //-- fr.jmmc.mf.models.FileLink getFileLink(int) 

    /**
     * Method getFileLink
     */
    public fr.jmmc.mf.models.FileLink[] getFileLink()
    {
        int size = _fileLinkList.size();
        fr.jmmc.mf.models.FileLink[] mArray = new fr.jmmc.mf.models.FileLink[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.FileLink) _fileLinkList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.FileLink[] getFileLink() 

    /**
     * Method getFileLinkCount
     */
    public int getFileLinkCount()
    {
        return _fileLinkList.size();
    } //-- int getFileLinkCount() 

    /**
     * Returns the value of field 'ident'.
     * 
     * @return the value of field 'ident'.
     */
    public java.lang.String getIdent()
    {
        return this._ident;
    } //-- java.lang.String getIdent() 

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
     * Returns the value of field 'normalize'.
     * 
     * @return the value of field 'normalize'.
     */
    public boolean getNormalize()
    {
        return this._normalize;
    } //-- boolean getNormalize() 

    /**
     * Returns the value of field 'residuals'. The field
     * 'residuals' has the following description: Residuals
     * configuration.
     *  
     * 
     * @return the value of field 'residuals'.
     */
    public fr.jmmc.mf.models.Residuals getResiduals()
    {
        return this._residuals;
    } //-- fr.jmmc.mf.models.Residuals getResiduals() 

    /**
     * Method hasNormalize
     */
    public boolean hasNormalize()
    {
        return this._has_normalize;
    } //-- boolean hasNormalize() 

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
     * Method removeFileLink
     * 
     * @param vFileLink
     */
    public boolean removeFileLink(fr.jmmc.mf.models.FileLink vFileLink)
    {
        boolean removed = _fileLinkList.remove(vFileLink);
        return removed;
    } //-- boolean removeFileLink(fr.jmmc.mf.models.FileLink) 

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
     * Method setFileLink
     * 
     * @param index
     * @param vFileLink
     */
    public void setFileLink(int index, fr.jmmc.mf.models.FileLink vFileLink)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fileLinkList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _fileLinkList.set(index, vFileLink);
    } //-- void setFileLink(int, fr.jmmc.mf.models.FileLink) 

    /**
     * Method setFileLink
     * 
     * @param fileLinkArray
     */
    public void setFileLink(fr.jmmc.mf.models.FileLink[] fileLinkArray)
    {
        //-- copy array
        _fileLinkList.clear();
        for (int i = 0; i < fileLinkArray.length; i++) {
            _fileLinkList.add(fileLinkArray[i]);
        }
    } //-- void setFileLink(fr.jmmc.mf.models.FileLink) 

    /**
     * Sets the value of field 'ident'.
     * 
     * @param ident the value of field 'ident'.
     */
    public void setIdent(java.lang.String ident)
    {
        this._ident = ident;
    } //-- void setIdent(java.lang.String) 

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
     * Sets the value of field 'normalize'.
     * 
     * @param normalize the value of field 'normalize'.
     */
    public void setNormalize(boolean normalize)
    {
        this._normalize = normalize;
        this._has_normalize = true;
    } //-- void setNormalize(boolean) 

    /**
     * Sets the value of field 'residuals'. The field 'residuals'
     * has the following description: Residuals configuration.
     *  
     * 
     * @param residuals the value of field 'residuals'.
     */
    public void setResiduals(fr.jmmc.mf.models.Residuals residuals)
    {
        this._residuals = residuals;
    } //-- void setResiduals(fr.jmmc.mf.models.Residuals) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Target unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Target) Unmarshaller.unmarshal(fr.jmmc.mf.models.Target.class, reader);
    } //-- fr.jmmc.mf.models.Target unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Target["+getIdent()+"]"; } 
}
