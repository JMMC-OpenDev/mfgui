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
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * A common representation of every model parameters.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Parameter implements java.io.Serializable {


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
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _desc
     */
    private java.lang.String _desc;

    /**
     * Field _units
     */
    private java.lang.String _units;

    /**
     * Field _value
     */
    private double _value;

    /**
     * keeps track of state for field: _value
     */
    private boolean _has_value;

    /**
     * Field _minValue
     */
    private double _minValue;

    /**
     * keeps track of state for field: _minValue
     */
    private boolean _has_minValue;

    /**
     * Field _maxValue
     */
    private double _maxValue;

    /**
     * keeps track of state for field: _maxValue
     */
    private boolean _has_maxValue;

    /**
     * Field _scale
     */
    private double _scale;

    /**
     * keeps track of state for field: _scale
     */
    private boolean _has_scale;

    /**
     * Field _hasFixedValue
     */
    private boolean _hasFixedValue;

    /**
     * keeps track of state for field: _hasFixedValue
     */
    private boolean _has_hasFixedValue;

    /**
     * Field _editable
     */
    private boolean _editable;

    /**
     * keeps track of state for field: _editable
     */
    private boolean _has_editable;


      //----------------/
     //- Constructors -/
    //----------------/

    public Parameter() {
        super();
    } //-- fr.jmmc.mf.models.Parameter()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteEditable
     */
    public void deleteEditable()
    {
        this._has_editable= false;
    } //-- void deleteEditable() 

    /**
     * Method deleteMaxValue
     */
    public void deleteMaxValue()
    {
        this._has_maxValue= false;
    } //-- void deleteMaxValue() 

    /**
     * Method deleteMinValue
     */
    public void deleteMinValue()
    {
        this._has_minValue= false;
    } //-- void deleteMinValue() 

    /**
     * Method deleteScale
     */
    public void deleteScale()
    {
        this._has_scale= false;
    } //-- void deleteScale() 

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
     * Returns the value of field 'editable'.
     * 
     * @return the value of field 'editable'.
     */
    public boolean getEditable()
    {
        return this._editable;
    } //-- boolean getEditable() 

    /**
     * Returns the value of field 'hasFixedValue'.
     * 
     * @return the value of field 'hasFixedValue'.
     */
    public boolean getHasFixedValue()
    {
        return this._hasFixedValue;
    } //-- boolean getHasFixedValue() 

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
     * Returns the value of field 'maxValue'.
     * 
     * @return the value of field 'maxValue'.
     */
    public double getMaxValue()
    {
        return this._maxValue;
    } //-- double getMaxValue() 

    /**
     * Returns the value of field 'minValue'.
     * 
     * @return the value of field 'minValue'.
     */
    public double getMinValue()
    {
        return this._minValue;
    } //-- double getMinValue() 

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
     * Returns the value of field 'scale'.
     * 
     * @return the value of field 'scale'.
     */
    public double getScale()
    {
        return this._scale;
    } //-- double getScale() 

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
     * Returns the value of field 'units'.
     * 
     * @return the value of field 'units'.
     */
    public java.lang.String getUnits()
    {
        return this._units;
    } //-- java.lang.String getUnits() 

    /**
     * Returns the value of field 'value'.
     * 
     * @return the value of field 'value'.
     */
    public double getValue()
    {
        return this._value;
    } //-- double getValue() 

    /**
     * Method hasEditable
     */
    public boolean hasEditable()
    {
        return this._has_editable;
    } //-- boolean hasEditable() 

    /**
     * Method hasHasFixedValue
     */
    public boolean hasHasFixedValue()
    {
        return this._has_hasFixedValue;
    } //-- boolean hasHasFixedValue() 

    /**
     * Method hasMaxValue
     */
    public boolean hasMaxValue()
    {
        return this._has_maxValue;
    } //-- boolean hasMaxValue() 

    /**
     * Method hasMinValue
     */
    public boolean hasMinValue()
    {
        return this._has_minValue;
    } //-- boolean hasMinValue() 

    /**
     * Method hasScale
     */
    public boolean hasScale()
    {
        return this._has_scale;
    } //-- boolean hasScale() 

    /**
     * Method hasValue
     */
    public boolean hasValue()
    {
        return this._has_value;
    } //-- boolean hasValue() 

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
     * Sets the value of field 'desc'.
     * 
     * @param desc the value of field 'desc'.
     */
    public void setDesc(java.lang.String desc)
    {
        this._desc = desc;
    } //-- void setDesc(java.lang.String) 

    /**
     * Sets the value of field 'editable'.
     * 
     * @param editable the value of field 'editable'.
     */
    public void setEditable(boolean editable)
    {
        this._editable = editable;
        this._has_editable = true;
    } //-- void setEditable(boolean) 

    /**
     * Sets the value of field 'hasFixedValue'.
     * 
     * @param hasFixedValue the value of field 'hasFixedValue'.
     */
    public void setHasFixedValue(boolean hasFixedValue)
    {
        this._hasFixedValue = hasFixedValue;
        this._has_hasFixedValue = true;
    } //-- void setHasFixedValue(boolean) 

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
     * Sets the value of field 'maxValue'.
     * 
     * @param maxValue the value of field 'maxValue'.
     */
    public void setMaxValue(double maxValue)
    {
        this._maxValue = maxValue;
        this._has_maxValue = true;
    } //-- void setMaxValue(double) 

    /**
     * Sets the value of field 'minValue'.
     * 
     * @param minValue the value of field 'minValue'.
     */
    public void setMinValue(double minValue)
    {
        this._minValue = minValue;
        this._has_minValue = true;
    } //-- void setMinValue(double) 

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
     * Sets the value of field 'scale'.
     * 
     * @param scale the value of field 'scale'.
     */
    public void setScale(double scale)
    {
        this._scale = scale;
        this._has_scale = true;
    } //-- void setScale(double) 

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
     * Sets the value of field 'units'.
     * 
     * @param units the value of field 'units'.
     */
    public void setUnits(java.lang.String units)
    {
        this._units = units;
    } //-- void setUnits(java.lang.String) 

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(double value)
    {
        this._value = value;
        this._has_value = true;
    } //-- void setValue(double) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Parameter unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Parameter) Unmarshaller.unmarshal(fr.jmmc.mf.models.Parameter.class, reader);
    } //-- fr.jmmc.mf.models.Parameter unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Parameter["+getName()+"]"; } 
}
