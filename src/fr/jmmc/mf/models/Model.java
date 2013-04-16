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
 * A common representation of every models.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Model implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _type
     */
    private java.lang.String _type;

    /**
     * A common representation of every models.
     *  
     */
    private java.util.ArrayList _modelList;

    /**
     * Field _desc
     */
    private java.lang.String _desc;

    /**
     * Field _code
     */
    private java.lang.String _code;

    /**
     * A common representation of every model parameters.
     *  
     */
    private java.util.ArrayList _parameterList;

    /**
     * Parameter link.
     *  
     */
    private java.util.ArrayList _parameterLinkList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Model() {
        super();
        _modelList = new ArrayList();
        _parameterList = new ArrayList();
        _parameterLinkList = new ArrayList();
    } //-- fr.jmmc.mf.models.Model()


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
     * Method addParameterLink
     * 
     * @param vParameterLink
     */
    public void addParameterLink(fr.jmmc.mf.models.ParameterLink vParameterLink)
        throws java.lang.IndexOutOfBoundsException
    {
        _parameterLinkList.add(vParameterLink);
    } //-- void addParameterLink(fr.jmmc.mf.models.ParameterLink) 

    /**
     * Method addParameterLink
     * 
     * @param index
     * @param vParameterLink
     */
    public void addParameterLink(int index, fr.jmmc.mf.models.ParameterLink vParameterLink)
        throws java.lang.IndexOutOfBoundsException
    {
        _parameterLinkList.add(index, vParameterLink);
    } //-- void addParameterLink(int, fr.jmmc.mf.models.ParameterLink) 

    /**
     * Method clearModel
     */
    public void clearModel()
    {
        _modelList.clear();
    } //-- void clearModel() 

    /**
     * Method clearParameter
     */
    public void clearParameter()
    {
        _parameterList.clear();
    } //-- void clearParameter() 

    /**
     * Method clearParameterLink
     */
    public void clearParameterLink()
    {
        _parameterLinkList.clear();
    } //-- void clearParameterLink() 

    /**
     * Method enumerateModel
     */
    public java.util.Enumeration enumerateModel()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_modelList.iterator());
    } //-- java.util.Enumeration enumerateModel() 

    /**
     * Method enumerateParameter
     */
    public java.util.Enumeration enumerateParameter()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_parameterList.iterator());
    } //-- java.util.Enumeration enumerateParameter() 

    /**
     * Method enumerateParameterLink
     */
    public java.util.Enumeration enumerateParameterLink()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_parameterLinkList.iterator());
    } //-- java.util.Enumeration enumerateParameterLink() 

    /**
     * Returns the value of field 'code'.
     * 
     * @return the value of field 'code'.
     */
    public java.lang.String getCode()
    {
        return this._code;
    } //-- java.lang.String getCode() 

    /**
     * Returns the value of field 'desc'.
     * 
     * @return the value of field 'desc'.
     */
    public java.lang.String getDesc()
    {
        return this._desc;
    } //-- java.lang.String getDesc() 

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
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

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
     * Method getParameterLink
     * 
     * @param index
     */
    public fr.jmmc.mf.models.ParameterLink getParameterLink(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _parameterLinkList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.ParameterLink) _parameterLinkList.get(index);
    } //-- fr.jmmc.mf.models.ParameterLink getParameterLink(int) 

    /**
     * Method getParameterLink
     */
    public fr.jmmc.mf.models.ParameterLink[] getParameterLink()
    {
        int size = _parameterLinkList.size();
        fr.jmmc.mf.models.ParameterLink[] mArray = new fr.jmmc.mf.models.ParameterLink[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.ParameterLink) _parameterLinkList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.ParameterLink[] getParameterLink() 

    /**
     * Method getParameterLinkCount
     */
    public int getParameterLinkCount()
    {
        return _parameterLinkList.size();
    } //-- int getParameterLinkCount() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'type'.
     */
    public java.lang.String getType()
    {
        return this._type;
    } //-- java.lang.String getType() 

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
     * Method removeParameterLink
     * 
     * @param vParameterLink
     */
    public boolean removeParameterLink(fr.jmmc.mf.models.ParameterLink vParameterLink)
    {
        boolean removed = _parameterLinkList.remove(vParameterLink);
        return removed;
    } //-- boolean removeParameterLink(fr.jmmc.mf.models.ParameterLink) 

    /**
     * Sets the value of field 'code'.
     * 
     * @param code the value of field 'code'.
     */
    public void setCode(java.lang.String code)
    {
        this._code = code;
    } //-- void setCode(java.lang.String) 

    /**
     * Sets the value of field 'desc'.
     * 
     * @param desc the value of field 'desc'.
     */
    public void setDesc(java.lang.String desc)
    {
        this._desc = desc;
    } //-- void setDesc(java.lang.String) 

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
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

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
     * Method setParameterLink
     * 
     * @param index
     * @param vParameterLink
     */
    public void setParameterLink(int index, fr.jmmc.mf.models.ParameterLink vParameterLink)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _parameterLinkList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _parameterLinkList.set(index, vParameterLink);
    } //-- void setParameterLink(int, fr.jmmc.mf.models.ParameterLink) 

    /**
     * Method setParameterLink
     * 
     * @param parameterLinkArray
     */
    public void setParameterLink(fr.jmmc.mf.models.ParameterLink[] parameterLinkArray)
    {
        //-- copy array
        _parameterLinkList.clear();
        for (int i = 0; i < parameterLinkArray.length; i++) {
            _parameterLinkList.add(parameterLinkArray[i]);
        }
    } //-- void setParameterLink(fr.jmmc.mf.models.ParameterLink) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(java.lang.String type)
    {
        this._type = type;
    } //-- void setType(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Model unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Model) Unmarshaller.unmarshal(fr.jmmc.mf.models.Model.class, reader);
    } //-- fr.jmmc.mf.models.Model unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return getType(); } 
}
