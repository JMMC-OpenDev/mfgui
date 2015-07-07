/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * A common representation of every models.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Model implements java.io.Serializable {


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
     * Field _polar.
     */
    private boolean _polar;

    /**
     * keeps track of state for field: _polar
     */
    private boolean _has_polar;

    /**
     * Field _stretched.
     */
    private boolean _stretched;

    /**
     * keeps track of state for field: _stretched
     */
    private boolean _has_stretched;

    /**
     * A common representation of every models.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Model> _modelList;

    /**
     * Field _desc.
     */
    private java.lang.String _desc;

    /**
     * Field _shortdesc.
     */
    private java.lang.String _shortdesc;

    /**
     * Field _code.
     */
    private java.lang.String _code;

    /**
     * A common representation of every model parameters.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Parameter> _parameterList;

    /**
     * Parameter link.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.ParameterLink> _parameterLinkList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Model() {
        super();
        this._modelList = new java.util.ArrayList<fr.jmmc.mf.models.Model>();
        this._parameterList = new java.util.ArrayList<fr.jmmc.mf.models.Parameter>();
        this._parameterLinkList = new java.util.ArrayList<fr.jmmc.mf.models.ParameterLink>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vModel
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addModel(
            final fr.jmmc.mf.models.Model vModel)
    throws java.lang.IndexOutOfBoundsException {
        this._modelList.add(vModel);
    }

    /**
     * 
     * 
     * @param index
     * @param vModel
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addModel(
            final int index,
            final fr.jmmc.mf.models.Model vModel)
    throws java.lang.IndexOutOfBoundsException {
        this._modelList.add(index, vModel);
    }

    /**
     * 
     * 
     * @param vParameter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addParameter(
            final fr.jmmc.mf.models.Parameter vParameter)
    throws java.lang.IndexOutOfBoundsException {
        this._parameterList.add(vParameter);
    }

    /**
     * 
     * 
     * @param index
     * @param vParameter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addParameter(
            final int index,
            final fr.jmmc.mf.models.Parameter vParameter)
    throws java.lang.IndexOutOfBoundsException {
        this._parameterList.add(index, vParameter);
    }

    /**
     * 
     * 
     * @param vParameterLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addParameterLink(
            final fr.jmmc.mf.models.ParameterLink vParameterLink)
    throws java.lang.IndexOutOfBoundsException {
        this._parameterLinkList.add(vParameterLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vParameterLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addParameterLink(
            final int index,
            final fr.jmmc.mf.models.ParameterLink vParameterLink)
    throws java.lang.IndexOutOfBoundsException {
        this._parameterLinkList.add(index, vParameterLink);
    }

    /**
     */
    public void deletePolar(
    ) {
        this._has_polar= false;
    }

    /**
     */
    public void deleteStretched(
    ) {
        this._has_stretched= false;
    }

    /**
     * Method enumerateModel.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Model> enumerateModel(
    ) {
        return java.util.Collections.enumeration(this._modelList);
    }

    /**
     * Method enumerateParameter.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Parameter> enumerateParameter(
    ) {
        return java.util.Collections.enumeration(this._parameterList);
    }

    /**
     * Method enumerateParameterLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.ParameterLink> enumerateParameterLink(
    ) {
        return java.util.Collections.enumeration(this._parameterLinkList);
    }

    /**
     * Returns the value of field 'code'.
     * 
     * @return the value of field 'Code'.
     */
    public java.lang.String getCode(
    ) {
        return this._code;
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
     * Method getModel.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Model at the
     * given index
     */
    public fr.jmmc.mf.models.Model getModel(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._modelList.size()) {
            throw new IndexOutOfBoundsException("getModel: Index value '" + index + "' not in range [0.." + (this._modelList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Model) _modelList.get(index);
    }

    /**
     * Method getModel.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Model[] getModel(
    ) {
        fr.jmmc.mf.models.Model[] array = new fr.jmmc.mf.models.Model[0];
        return (fr.jmmc.mf.models.Model[]) this._modelList.toArray(array);
    }

    /**
     * Method getModelCount.
     * 
     * @return the size of this collection
     */
    public int getModelCount(
    ) {
        return this._modelList.size();
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
     * Method getParameter.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Parameter at the
     * given index
     */
    public fr.jmmc.mf.models.Parameter getParameter(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._parameterList.size()) {
            throw new IndexOutOfBoundsException("getParameter: Index value '" + index + "' not in range [0.." + (this._parameterList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Parameter) _parameterList.get(index);
    }

    /**
     * Method getParameter.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Parameter[] getParameter(
    ) {
        fr.jmmc.mf.models.Parameter[] array = new fr.jmmc.mf.models.Parameter[0];
        return (fr.jmmc.mf.models.Parameter[]) this._parameterList.toArray(array);
    }

    /**
     * Method getParameterCount.
     * 
     * @return the size of this collection
     */
    public int getParameterCount(
    ) {
        return this._parameterList.size();
    }

    /**
     * Method getParameterLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.ParameterLink at
     * the given index
     */
    public fr.jmmc.mf.models.ParameterLink getParameterLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._parameterLinkList.size()) {
            throw new IndexOutOfBoundsException("getParameterLink: Index value '" + index + "' not in range [0.." + (this._parameterLinkList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.ParameterLink) _parameterLinkList.get(index);
    }

    /**
     * Method getParameterLink.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.ParameterLink[] getParameterLink(
    ) {
        fr.jmmc.mf.models.ParameterLink[] array = new fr.jmmc.mf.models.ParameterLink[0];
        return (fr.jmmc.mf.models.ParameterLink[]) this._parameterLinkList.toArray(array);
    }

    /**
     * Method getParameterLinkCount.
     * 
     * @return the size of this collection
     */
    public int getParameterLinkCount(
    ) {
        return this._parameterLinkList.size();
    }

    /**
     * Returns the value of field 'polar'.
     * 
     * @return the value of field 'Polar'.
     */
    public boolean getPolar(
    ) {
        return this._polar;
    }

    /**
     * Returns the value of field 'shortdesc'.
     * 
     * @return the value of field 'Shortdesc'.
     */
    public java.lang.String getShortdesc(
    ) {
        return this._shortdesc;
    }

    /**
     * Returns the value of field 'stretched'.
     * 
     * @return the value of field 'Stretched'.
     */
    public boolean getStretched(
    ) {
        return this._stretched;
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
     * Method hasPolar.
     * 
     * @return true if at least one Polar has been added
     */
    public boolean hasPolar(
    ) {
        return this._has_polar;
    }

    /**
     * Method hasStretched.
     * 
     * @return true if at least one Stretched has been added
     */
    public boolean hasStretched(
    ) {
        return this._has_stretched;
    }

    /**
     * Returns the value of field 'polar'.
     * 
     * @return the value of field 'Polar'.
     */
    public boolean isPolar(
    ) {
        return this._polar;
    }

    /**
     * Returns the value of field 'stretched'.
     * 
     * @return the value of field 'Stretched'.
     */
    public boolean isStretched(
    ) {
        return this._stretched;
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
     * Method iterateModel.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Model> iterateModel(
    ) {
        return this._modelList.iterator();
    }

    /**
     * Method iterateParameter.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Parameter> iterateParameter(
    ) {
        return this._parameterList.iterator();
    }

    /**
     * Method iterateParameterLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.ParameterLink> iterateParameterLink(
    ) {
        return this._parameterLinkList.iterator();
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
     */
    public void removeAllModel(
    ) {
        this._modelList.clear();
    }

    /**
     */
    public void removeAllParameter(
    ) {
        this._parameterList.clear();
    }

    /**
     */
    public void removeAllParameterLink(
    ) {
        this._parameterLinkList.clear();
    }

    /**
     * Method removeModel.
     * 
     * @param vModel
     * @return true if the object was removed from the collection.
     */
    public boolean removeModel(
            final fr.jmmc.mf.models.Model vModel) {
        boolean removed = _modelList.remove(vModel);
        return removed;
    }

    /**
     * Method removeModelAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Model removeModelAt(
            final int index) {
        java.lang.Object obj = this._modelList.remove(index);
        return (fr.jmmc.mf.models.Model) obj;
    }

    /**
     * Method removeParameter.
     * 
     * @param vParameter
     * @return true if the object was removed from the collection.
     */
    public boolean removeParameter(
            final fr.jmmc.mf.models.Parameter vParameter) {
        boolean removed = _parameterList.remove(vParameter);
        return removed;
    }

    /**
     * Method removeParameterAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Parameter removeParameterAt(
            final int index) {
        java.lang.Object obj = this._parameterList.remove(index);
        return (fr.jmmc.mf.models.Parameter) obj;
    }

    /**
     * Method removeParameterLink.
     * 
     * @param vParameterLink
     * @return true if the object was removed from the collection.
     */
    public boolean removeParameterLink(
            final fr.jmmc.mf.models.ParameterLink vParameterLink) {
        boolean removed = _parameterLinkList.remove(vParameterLink);
        return removed;
    }

    /**
     * Method removeParameterLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.ParameterLink removeParameterLinkAt(
            final int index) {
        java.lang.Object obj = this._parameterLinkList.remove(index);
        return (fr.jmmc.mf.models.ParameterLink) obj;
    }

    /**
     * Sets the value of field 'code'.
     * 
     * @param code the value of field 'code'.
     */
    public void setCode(
            final java.lang.String code) {
        this._code = code;
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
     * 
     * 
     * @param index
     * @param vModel
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setModel(
            final int index,
            final fr.jmmc.mf.models.Model vModel)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._modelList.size()) {
            throw new IndexOutOfBoundsException("setModel: Index value '" + index + "' not in range [0.." + (this._modelList.size() - 1) + "]");
        }

        this._modelList.set(index, vModel);
    }

    /**
     * 
     * 
     * @param vModelArray
     */
    public void setModel(
            final fr.jmmc.mf.models.Model[] vModelArray) {
        //-- copy array
        _modelList.clear();

        for (int i = 0; i < vModelArray.length; i++) {
                this._modelList.add(vModelArray[i]);
        }
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
     * 
     * 
     * @param index
     * @param vParameter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setParameter(
            final int index,
            final fr.jmmc.mf.models.Parameter vParameter)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._parameterList.size()) {
            throw new IndexOutOfBoundsException("setParameter: Index value '" + index + "' not in range [0.." + (this._parameterList.size() - 1) + "]");
        }

        this._parameterList.set(index, vParameter);
    }

    /**
     * 
     * 
     * @param vParameterArray
     */
    public void setParameter(
            final fr.jmmc.mf.models.Parameter[] vParameterArray) {
        //-- copy array
        _parameterList.clear();

        for (int i = 0; i < vParameterArray.length; i++) {
                this._parameterList.add(vParameterArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vParameterLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setParameterLink(
            final int index,
            final fr.jmmc.mf.models.ParameterLink vParameterLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._parameterLinkList.size()) {
            throw new IndexOutOfBoundsException("setParameterLink: Index value '" + index + "' not in range [0.." + (this._parameterLinkList.size() - 1) + "]");
        }

        this._parameterLinkList.set(index, vParameterLink);
    }

    /**
     * 
     * 
     * @param vParameterLinkArray
     */
    public void setParameterLink(
            final fr.jmmc.mf.models.ParameterLink[] vParameterLinkArray) {
        //-- copy array
        _parameterLinkList.clear();

        for (int i = 0; i < vParameterLinkArray.length; i++) {
                this._parameterLinkList.add(vParameterLinkArray[i]);
        }
    }

    /**
     * Sets the value of field 'polar'.
     * 
     * @param polar the value of field 'polar'.
     */
    public void setPolar(
            final boolean polar) {
        this._polar = polar;
        this._has_polar = true;
    }

    /**
     * Sets the value of field 'shortdesc'.
     * 
     * @param shortdesc the value of field 'shortdesc'.
     */
    public void setShortdesc(
            final java.lang.String shortdesc) {
        this._shortdesc = shortdesc;
    }

    /**
     * Sets the value of field 'stretched'.
     * 
     * @param stretched the value of field 'stretched'.
     */
    public void setStretched(
            final boolean stretched) {
        this._stretched = stretched;
        this._has_stretched = true;
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
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled fr.jmmc.mf.models.Model
     */
    public static fr.jmmc.mf.models.Model unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Model) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Model.class, reader);
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
