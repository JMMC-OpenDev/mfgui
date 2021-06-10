/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Contains responses.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Responses implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Highest element of what generally is a server response of
     * LITpro wrapper.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Response> _responseList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Responses() {
        super();
        this._responseList = new java.util.ArrayList<fr.jmmc.mf.models.Response>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vResponse
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResponse(
            final fr.jmmc.mf.models.Response vResponse)
    throws java.lang.IndexOutOfBoundsException {
        this._responseList.add(vResponse);
    }

    /**
     * 
     * 
     * @param index
     * @param vResponse
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResponse(
            final int index,
            final fr.jmmc.mf.models.Response vResponse)
    throws java.lang.IndexOutOfBoundsException {
        this._responseList.add(index, vResponse);
    }

    /**
     * Method enumerateResponse.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Response> enumerateResponse(
    ) {
        return java.util.Collections.enumeration(this._responseList);
    }

    /**
     * Method getResponse.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Response at the
     * given index
     */
    public fr.jmmc.mf.models.Response getResponse(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._responseList.size()) {
            throw new IndexOutOfBoundsException("getResponse: Index value '" + index + "' not in range [0.." + (this._responseList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Response) _responseList.get(index);
    }

    /**
     * Method getResponse.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Response[] getResponse(
    ) {
        fr.jmmc.mf.models.Response[] array = new fr.jmmc.mf.models.Response[0];
        return (fr.jmmc.mf.models.Response[]) this._responseList.toArray(array);
    }

    /**
     * Method getResponseCount.
     * 
     * @return the size of this collection
     */
    public int getResponseCount(
    ) {
        return this._responseList.size();
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
     * Method iterateResponse.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Response> iterateResponse(
    ) {
        return this._responseList.iterator();
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
    public void removeAllResponse(
    ) {
        this._responseList.clear();
    }

    /**
     * Method removeResponse.
     * 
     * @param vResponse
     * @return true if the object was removed from the collection.
     */
    public boolean removeResponse(
            final fr.jmmc.mf.models.Response vResponse) {
        boolean removed = _responseList.remove(vResponse);
        return removed;
    }

    /**
     * Method removeResponseAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Response removeResponseAt(
            final int index) {
        java.lang.Object obj = this._responseList.remove(index);
        return (fr.jmmc.mf.models.Response) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vResponse
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setResponse(
            final int index,
            final fr.jmmc.mf.models.Response vResponse)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._responseList.size()) {
            throw new IndexOutOfBoundsException("setResponse: Index value '" + index + "' not in range [0.." + (this._responseList.size() - 1) + "]");
        }

        this._responseList.set(index, vResponse);
    }

    /**
     * 
     * 
     * @param vResponseArray
     */
    public void setResponse(
            final fr.jmmc.mf.models.Response[] vResponseArray) {
        //-- copy array
        _responseList.clear();

        for (int i = 0; i < vResponseArray.length; i++) {
                this._responseList.add(vResponseArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.Responses
     */
    public static fr.jmmc.mf.models.Responses unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Responses) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Responses.class, reader);
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

    public String toString(){ return "Plots"; } 
}
