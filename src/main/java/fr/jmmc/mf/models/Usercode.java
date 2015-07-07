/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Contains user models (with code element) and user functions.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
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
    private java.util.List<fr.jmmc.mf.models.Model> _modelList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Usercode() {
        super();
        this._modelList = new java.util.ArrayList<fr.jmmc.mf.models.Model>();
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
     * Returns the value of field 'common'. The field 'common' has
     * the following description: Define a common code part.
     *  
     * 
     * @return the value of field 'Common'.
     */
    public fr.jmmc.mf.models.Common getCommon(
    ) {
        return this._common;
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
     * Sets the value of field 'common'. The field 'common' has the
     * following description: Define a common code part.
     *  
     * 
     * @param common the value of field 'common'.
     */
    public void setCommon(
            final fr.jmmc.mf.models.Common common) {
        this._common = common;
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
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled fr.jmmc.mf.models.Usercode
     */
    public static fr.jmmc.mf.models.Usercode unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Usercode) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Usercode.class, reader);
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

    public String toString(){ return "Usercode"; } 
}
