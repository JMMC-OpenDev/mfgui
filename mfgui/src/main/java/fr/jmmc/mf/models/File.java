/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * File description.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class File implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _href.
     */
    private java.lang.String _href;

    /**
     * oitarget representation.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Oitarget> _oitargetList;


      //----------------/
     //- Constructors -/
    //----------------/

    public File() {
        super();
        this._oitargetList = new java.util.ArrayList<fr.jmmc.mf.models.Oitarget>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vOitarget
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addOitarget(
            final fr.jmmc.mf.models.Oitarget vOitarget)
    throws java.lang.IndexOutOfBoundsException {
        this._oitargetList.add(vOitarget);
    }

    /**
     * 
     * 
     * @param index
     * @param vOitarget
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addOitarget(
            final int index,
            final fr.jmmc.mf.models.Oitarget vOitarget)
    throws java.lang.IndexOutOfBoundsException {
        this._oitargetList.add(index, vOitarget);
    }

    /**
     * Method enumerateOitarget.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Oitarget> enumerateOitarget(
    ) {
        return java.util.Collections.enumeration(this._oitargetList);
    }

    /**
     * Returns the value of field 'href'.
     * 
     * @return the value of field 'Href'.
     */
    public java.lang.String getHref(
    ) {
        return this._href;
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
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Method getOitarget.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Oitarget at the
     * given index
     */
    public fr.jmmc.mf.models.Oitarget getOitarget(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._oitargetList.size()) {
            throw new IndexOutOfBoundsException("getOitarget: Index value '" + index + "' not in range [0.." + (this._oitargetList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Oitarget) _oitargetList.get(index);
    }

    /**
     * Method getOitarget.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Oitarget[] getOitarget(
    ) {
        fr.jmmc.mf.models.Oitarget[] array = new fr.jmmc.mf.models.Oitarget[0];
        return (fr.jmmc.mf.models.Oitarget[]) this._oitargetList.toArray(array);
    }

    /**
     * Method getOitargetCount.
     * 
     * @return the size of this collection
     */
    public int getOitargetCount(
    ) {
        return this._oitargetList.size();
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
     * Method iterateOitarget.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Oitarget> iterateOitarget(
    ) {
        return this._oitargetList.iterator();
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
    public void removeAllOitarget(
    ) {
        this._oitargetList.clear();
    }

    /**
     * Method removeOitarget.
     * 
     * @param vOitarget
     * @return true if the object was removed from the collection.
     */
    public boolean removeOitarget(
            final fr.jmmc.mf.models.Oitarget vOitarget) {
        boolean removed = _oitargetList.remove(vOitarget);
        return removed;
    }

    /**
     * Method removeOitargetAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Oitarget removeOitargetAt(
            final int index) {
        java.lang.Object obj = this._oitargetList.remove(index);
        return (fr.jmmc.mf.models.Oitarget) obj;
    }

    /**
     * Sets the value of field 'href'.
     * 
     * @param href the value of field 'href'.
     */
    public void setHref(
            final java.lang.String href) {
        this._href = href;
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
     * @param vOitarget
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setOitarget(
            final int index,
            final fr.jmmc.mf.models.Oitarget vOitarget)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._oitargetList.size()) {
            throw new IndexOutOfBoundsException("setOitarget: Index value '" + index + "' not in range [0.." + (this._oitargetList.size() - 1) + "]");
        }

        this._oitargetList.set(index, vOitarget);
    }

    /**
     * 
     * 
     * @param vOitargetArray
     */
    public void setOitarget(
            final fr.jmmc.mf.models.Oitarget[] vOitargetArray) {
        //-- copy array
        _oitargetList.clear();

        for (int i = 0; i < vOitargetArray.length; i++) {
                this._oitargetList.add(vOitargetArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.File
     */
    public static fr.jmmc.mf.models.File unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.File) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.File.class, reader);
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

    public String toString(){ return "File["+getName()+"]"; } 
}
