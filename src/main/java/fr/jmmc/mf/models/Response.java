/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Highest element of what generally is a server response of LITpro
 * wrapper.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Response implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _items.
     */
    private java.util.List<fr.jmmc.mf.models.ResponseItem> _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public Response() {
        super();
        this._items = new java.util.ArrayList<fr.jmmc.mf.models.ResponseItem>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vResponseItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResponseItem(
            final fr.jmmc.mf.models.ResponseItem vResponseItem)
    throws java.lang.IndexOutOfBoundsException {
        this._items.add(vResponseItem);
    }

    /**
     * 
     * 
     * @param index
     * @param vResponseItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResponseItem(
            final int index,
            final fr.jmmc.mf.models.ResponseItem vResponseItem)
    throws java.lang.IndexOutOfBoundsException {
        this._items.add(index, vResponseItem);
    }

    /**
     * Method enumerateResponseItem.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.ResponseItem> enumerateResponseItem(
    ) {
        return java.util.Collections.enumeration(this._items);
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
     * Method getResponseItem.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.ResponseItem at
     * the given index
     */
    public fr.jmmc.mf.models.ResponseItem getResponseItem(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("getResponseItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.ResponseItem) _items.get(index);
    }

    /**
     * Method getResponseItem.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.ResponseItem[] getResponseItem(
    ) {
        fr.jmmc.mf.models.ResponseItem[] array = new fr.jmmc.mf.models.ResponseItem[0];
        return (fr.jmmc.mf.models.ResponseItem[]) this._items.toArray(array);
    }

    /**
     * Method getResponseItemCount.
     * 
     * @return the size of this collection
     */
    public int getResponseItemCount(
    ) {
        return this._items.size();
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
     * Method iterateResponseItem.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.ResponseItem> iterateResponseItem(
    ) {
        return this._items.iterator();
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
    public void removeAllResponseItem(
    ) {
        this._items.clear();
    }

    /**
     * Method removeResponseItem.
     * 
     * @param vResponseItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeResponseItem(
            final fr.jmmc.mf.models.ResponseItem vResponseItem) {
        boolean removed = _items.remove(vResponseItem);
        return removed;
    }

    /**
     * Method removeResponseItemAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.ResponseItem removeResponseItemAt(
            final int index) {
        java.lang.Object obj = this._items.remove(index);
        return (fr.jmmc.mf.models.ResponseItem) obj;
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
     * @param vResponseItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setResponseItem(
            final int index,
            final fr.jmmc.mf.models.ResponseItem vResponseItem)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("setResponseItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }

        this._items.set(index, vResponseItem);
    }

    /**
     * 
     * 
     * @param vResponseItemArray
     */
    public void setResponseItem(
            final fr.jmmc.mf.models.ResponseItem[] vResponseItemArray) {
        //-- copy array
        _items.clear();

        for (int i = 0; i < vResponseItemArray.length; i++) {
                this._items.add(vResponseItemArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.Response
     */
    public static fr.jmmc.mf.models.Response unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Response) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Response.class, reader);
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

    public String toString(){ return getName(); } 
}
