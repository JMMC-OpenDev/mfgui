/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * A common representation of every model parameters.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Parameter implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _type.
     */
    private java.lang.String _type;

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _desc.
     */
    private java.lang.String _desc;

    /**
     * Field _units.
     */
    private java.lang.String _units;

    /**
     * Field _value.
     */
    private double _value;

    /**
     * keeps track of state for field: _value
     */
    private boolean _has_value;

    /**
     * Field _minValue.
     */
    private double _minValue;

    /**
     * keeps track of state for field: _minValue
     */
    private boolean _has_minValue;

    /**
     * Field _maxValue.
     */
    private double _maxValue;

    /**
     * keeps track of state for field: _maxValue
     */
    private boolean _has_maxValue;

    /**
     * Field _scale.
     */
    private double _scale;

    /**
     * keeps track of state for field: _scale
     */
    private boolean _has_scale;

    /**
     * Field _hasFixedValue.
     */
    private boolean _hasFixedValue;

    /**
     * keeps track of state for field: _hasFixedValue
     */
    private boolean _has_hasFixedValue;

    /**
     * Field _editable.
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
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteEditable(
    ) {
        this._has_editable= false;
    }

    /**
     */
    public void deleteHasFixedValue(
    ) {
        this._has_hasFixedValue= false;
    }

    /**
     */
    public void deleteMaxValue(
    ) {
        this._has_maxValue= false;
    }

    /**
     */
    public void deleteMinValue(
    ) {
        this._has_minValue= false;
    }

    /**
     */
    public void deleteScale(
    ) {
        this._has_scale= false;
    }

    /**
     */
    public void deleteValue(
    ) {
        this._has_value= false;
    }

    /**
     * Returns the value of field 'desc'.
     * 
     * @return the value of field 'Desc'.
     */
    public java.lang.String getDesc(
    ) {
        return this._desc;
    }

    /**
     * Returns the value of field 'editable'.
     * 
     * @return the value of field 'Editable'.
     */
    public boolean getEditable(
    ) {
        return this._editable;
    }

    /**
     * Returns the value of field 'hasFixedValue'.
     * 
     * @return the value of field 'HasFixedValue'.
     */
    public boolean getHasFixedValue(
    ) {
        return this._hasFixedValue;
    }

    /**
     * Returns the value of field 'id'.
     * 
     * @return the value of field 'Id'.
     */
    public java.lang.String getId(
    ) {
        return this._id;
    }

    /**
     * Returns the value of field 'maxValue'.
     * 
     * @return the value of field 'MaxValue'.
     */
    public double getMaxValue(
    ) {
        return this._maxValue;
    }

    /**
     * Returns the value of field 'minValue'.
     * 
     * @return the value of field 'MinValue'.
     */
    public double getMinValue(
    ) {
        return this._minValue;
    }

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Returns the value of field 'scale'.
     * 
     * @return the value of field 'Scale'.
     */
    public double getScale(
    ) {
        return this._scale;
    }

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'Type'.
     */
    public java.lang.String getType(
    ) {
        return this._type;
    }

    /**
     * Returns the value of field 'units'.
     * 
     * @return the value of field 'Units'.
     */
    public java.lang.String getUnits(
    ) {
        return this._units;
    }

    /**
     * Returns the value of field 'value'.
     * 
     * @return the value of field 'Value'.
     */
    public double getValue(
    ) {
        return this._value;
    }

    /**
     * Method hasEditable.
     * 
     * @return true if at least one Editable has been added
     */
    public boolean hasEditable(
    ) {
        return this._has_editable;
    }

    /**
     * Method hasHasFixedValue.
     * 
     * @return true if at least one HasFixedValue has been added
     */
    public boolean hasHasFixedValue(
    ) {
        return this._has_hasFixedValue;
    }

    /**
     * Method hasMaxValue.
     * 
     * @return true if at least one MaxValue has been added
     */
    public boolean hasMaxValue(
    ) {
        return this._has_maxValue;
    }

    /**
     * Method hasMinValue.
     * 
     * @return true if at least one MinValue has been added
     */
    public boolean hasMinValue(
    ) {
        return this._has_minValue;
    }

    /**
     * Method hasScale.
     * 
     * @return true if at least one Scale has been added
     */
    public boolean hasScale(
    ) {
        return this._has_scale;
    }

    /**
     * Method hasValue.
     * 
     * @return true if at least one Value has been added
     */
    public boolean hasValue(
    ) {
        return this._has_value;
    }

    /**
     * Returns the value of field 'editable'.
     * 
     * @return the value of field 'Editable'.
     */
    public boolean isEditable(
    ) {
        return this._editable;
    }

    /**
     * Returns the value of field 'hasFixedValue'.
     * 
     * @return the value of field 'HasFixedValue'.
     */
    public boolean isHasFixedValue(
    ) {
        return this._hasFixedValue;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     * Sets the value of field 'desc'.
     * 
     * @param desc the value of field 'desc'.
     */
    public void setDesc(
            final java.lang.String desc) {
        this._desc = desc;
    }

    /**
     * Sets the value of field 'editable'.
     * 
     * @param editable the value of field 'editable'.
     */
    public void setEditable(
            final boolean editable) {
        this._editable = editable;
        this._has_editable = true;
    }

    /**
     * Sets the value of field 'hasFixedValue'.
     * 
     * @param hasFixedValue the value of field 'hasFixedValue'.
     */
    public void setHasFixedValue(
            final boolean hasFixedValue) {
        this._hasFixedValue = hasFixedValue;
        this._has_hasFixedValue = true;
    }

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(
            final java.lang.String id) {
        this._id = id;
    }

    /**
     * Sets the value of field 'maxValue'.
     * 
     * @param maxValue the value of field 'maxValue'.
     */
    public void setMaxValue(
            final double maxValue) {
        this._maxValue = maxValue;
        this._has_maxValue = true;
    }

    /**
     * Sets the value of field 'minValue'.
     * 
     * @param minValue the value of field 'minValue'.
     */
    public void setMinValue(
            final double minValue) {
        this._minValue = minValue;
        this._has_minValue = true;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final java.lang.String name) {
        this._name = name;
    }

    /**
     * Sets the value of field 'scale'.
     * 
     * @param scale the value of field 'scale'.
     */
    public void setScale(
            final double scale) {
        this._scale = scale;
        this._has_scale = true;
    }

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(
            final java.lang.String type) {
        this._type = type;
    }

    /**
     * Sets the value of field 'units'.
     * 
     * @param units the value of field 'units'.
     */
    public void setUnits(
            final java.lang.String units) {
        this._units = units;
    }

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(
            final double value) {
        this._value = value;
        this._has_value = true;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled fr.jmmc.mf.models.Parameter
     */
    public static fr.jmmc.mf.models.Parameter unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Parameter) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Parameter.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
