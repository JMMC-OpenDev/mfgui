/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Residuals configuration.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Residuals implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Residual description.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Residual> _residualList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Residuals() {
        super();
        this._residualList = new java.util.ArrayList<fr.jmmc.mf.models.Residual>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vResidual
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResidual(
            final fr.jmmc.mf.models.Residual vResidual)
    throws java.lang.IndexOutOfBoundsException {
        this._residualList.add(vResidual);
    }

    /**
     * 
     * 
     * @param index
     * @param vResidual
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResidual(
            final int index,
            final fr.jmmc.mf.models.Residual vResidual)
    throws java.lang.IndexOutOfBoundsException {
        this._residualList.add(index, vResidual);
    }

    /**
     * Method enumerateResidual.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Residual> enumerateResidual(
    ) {
        return java.util.Collections.enumeration(this._residualList);
    }

    /**
     * Method getResidual.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Residual at the
     * given index
     */
    public fr.jmmc.mf.models.Residual getResidual(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._residualList.size()) {
            throw new IndexOutOfBoundsException("getResidual: Index value '" + index + "' not in range [0.." + (this._residualList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Residual) _residualList.get(index);
    }

    /**
     * Method getResidual.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Residual[] getResidual(
    ) {
        fr.jmmc.mf.models.Residual[] array = new fr.jmmc.mf.models.Residual[0];
        return (fr.jmmc.mf.models.Residual[]) this._residualList.toArray(array);
    }

    /**
     * Method getResidualCount.
     * 
     * @return the size of this collection
     */
    public int getResidualCount(
    ) {
        return this._residualList.size();
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
     * Method iterateResidual.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Residual> iterateResidual(
    ) {
        return this._residualList.iterator();
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
    public void removeAllResidual(
    ) {
        this._residualList.clear();
    }

    /**
     * Method removeResidual.
     * 
     * @param vResidual
     * @return true if the object was removed from the collection.
     */
    public boolean removeResidual(
            final fr.jmmc.mf.models.Residual vResidual) {
        boolean removed = _residualList.remove(vResidual);
        return removed;
    }

    /**
     * Method removeResidualAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Residual removeResidualAt(
            final int index) {
        java.lang.Object obj = this._residualList.remove(index);
        return (fr.jmmc.mf.models.Residual) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vResidual
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setResidual(
            final int index,
            final fr.jmmc.mf.models.Residual vResidual)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._residualList.size()) {
            throw new IndexOutOfBoundsException("setResidual: Index value '" + index + "' not in range [0.." + (this._residualList.size() - 1) + "]");
        }

        this._residualList.set(index, vResidual);
    }

    /**
     * 
     * 
     * @param vResidualArray
     */
    public void setResidual(
            final fr.jmmc.mf.models.Residual[] vResidualArray) {
        //-- copy array
        _residualList.clear();

        for (int i = 0; i < vResidualArray.length; i++) {
                this._residualList.add(vResidualArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.Residuals
     */
    public static fr.jmmc.mf.models.Residuals unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Residuals) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Residuals.class, reader);
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

    public String toString(){ return "Residuals"; } 
}
