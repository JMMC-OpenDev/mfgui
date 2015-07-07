/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Contains results.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Results implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Result of a fit section.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Result> _resultList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Results() {
        super();
        this._resultList = new java.util.ArrayList<fr.jmmc.mf.models.Result>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResult(
            final fr.jmmc.mf.models.Result vResult)
    throws java.lang.IndexOutOfBoundsException {
        this._resultList.add(vResult);
    }

    /**
     * 
     * 
     * @param index
     * @param vResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResult(
            final int index,
            final fr.jmmc.mf.models.Result vResult)
    throws java.lang.IndexOutOfBoundsException {
        this._resultList.add(index, vResult);
    }

    /**
     * Method enumerateResult.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Result> enumerateResult(
    ) {
        return java.util.Collections.enumeration(this._resultList);
    }

    /**
     * Method getResult.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Result at the
     * given index
     */
    public fr.jmmc.mf.models.Result getResult(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._resultList.size()) {
            throw new IndexOutOfBoundsException("getResult: Index value '" + index + "' not in range [0.." + (this._resultList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Result) _resultList.get(index);
    }

    /**
     * Method getResult.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Result[] getResult(
    ) {
        fr.jmmc.mf.models.Result[] array = new fr.jmmc.mf.models.Result[0];
        return (fr.jmmc.mf.models.Result[]) this._resultList.toArray(array);
    }

    /**
     * Method getResultCount.
     * 
     * @return the size of this collection
     */
    public int getResultCount(
    ) {
        return this._resultList.size();
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
     * Method iterateResult.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Result> iterateResult(
    ) {
        return this._resultList.iterator();
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
    public void removeAllResult(
    ) {
        this._resultList.clear();
    }

    /**
     * Method removeResult.
     * 
     * @param vResult
     * @return true if the object was removed from the collection.
     */
    public boolean removeResult(
            final fr.jmmc.mf.models.Result vResult) {
        boolean removed = _resultList.remove(vResult);
        return removed;
    }

    /**
     * Method removeResultAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Result removeResultAt(
            final int index) {
        java.lang.Object obj = this._resultList.remove(index);
        return (fr.jmmc.mf.models.Result) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setResult(
            final int index,
            final fr.jmmc.mf.models.Result vResult)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._resultList.size()) {
            throw new IndexOutOfBoundsException("setResult: Index value '" + index + "' not in range [0.." + (this._resultList.size() - 1) + "]");
        }

        this._resultList.set(index, vResult);
    }

    /**
     * 
     * 
     * @param vResultArray
     */
    public void setResult(
            final fr.jmmc.mf.models.Result[] vResultArray) {
        //-- copy array
        _resultList.clear();

        for (int i = 0; i < vResultArray.length; i++) {
                this._resultList.add(vResultArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.Results
     */
    public static fr.jmmc.mf.models.Results unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Results) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Results.class, reader);
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
