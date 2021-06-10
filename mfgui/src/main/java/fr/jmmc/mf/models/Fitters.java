/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Contains fitters.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Fitters implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A common representation of every fitter.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Fitter> _fitterList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Fitters() {
        super();
        this._fitterList = new java.util.ArrayList<fr.jmmc.mf.models.Fitter>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vFitter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFitter(
            final fr.jmmc.mf.models.Fitter vFitter)
    throws java.lang.IndexOutOfBoundsException {
        this._fitterList.add(vFitter);
    }

    /**
     * 
     * 
     * @param index
     * @param vFitter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFitter(
            final int index,
            final fr.jmmc.mf.models.Fitter vFitter)
    throws java.lang.IndexOutOfBoundsException {
        this._fitterList.add(index, vFitter);
    }

    /**
     * Method enumerateFitter.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Fitter> enumerateFitter(
    ) {
        return java.util.Collections.enumeration(this._fitterList);
    }

    /**
     * Method getFitter.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Fitter at the
     * given index
     */
    public fr.jmmc.mf.models.Fitter getFitter(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fitterList.size()) {
            throw new IndexOutOfBoundsException("getFitter: Index value '" + index + "' not in range [0.." + (this._fitterList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Fitter) _fitterList.get(index);
    }

    /**
     * Method getFitter.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Fitter[] getFitter(
    ) {
        fr.jmmc.mf.models.Fitter[] array = new fr.jmmc.mf.models.Fitter[0];
        return (fr.jmmc.mf.models.Fitter[]) this._fitterList.toArray(array);
    }

    /**
     * Method getFitterCount.
     * 
     * @return the size of this collection
     */
    public int getFitterCount(
    ) {
        return this._fitterList.size();
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
     * Method iterateFitter.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Fitter> iterateFitter(
    ) {
        return this._fitterList.iterator();
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
    public void removeAllFitter(
    ) {
        this._fitterList.clear();
    }

    /**
     * Method removeFitter.
     * 
     * @param vFitter
     * @return true if the object was removed from the collection.
     */
    public boolean removeFitter(
            final fr.jmmc.mf.models.Fitter vFitter) {
        boolean removed = _fitterList.remove(vFitter);
        return removed;
    }

    /**
     * Method removeFitterAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Fitter removeFitterAt(
            final int index) {
        java.lang.Object obj = this._fitterList.remove(index);
        return (fr.jmmc.mf.models.Fitter) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vFitter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFitter(
            final int index,
            final fr.jmmc.mf.models.Fitter vFitter)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fitterList.size()) {
            throw new IndexOutOfBoundsException("setFitter: Index value '" + index + "' not in range [0.." + (this._fitterList.size() - 1) + "]");
        }

        this._fitterList.set(index, vFitter);
    }

    /**
     * 
     * 
     * @param vFitterArray
     */
    public void setFitter(
            final fr.jmmc.mf.models.Fitter[] vFitterArray) {
        //-- copy array
        _fitterList.clear();

        for (int i = 0; i < vFitterArray.length; i++) {
                this._fitterList.add(vFitterArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.Fitters
     */
    public static fr.jmmc.mf.models.Fitters unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Fitters) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Fitters.class, reader);
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

    public String toString(){ return "Fitters"; } 
}
