/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Contains target elements.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Targets implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Target representation.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Target> _targetList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Targets() {
        super();
        this._targetList = new java.util.ArrayList<fr.jmmc.mf.models.Target>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vTarget
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTarget(
            final fr.jmmc.mf.models.Target vTarget)
    throws java.lang.IndexOutOfBoundsException {
        this._targetList.add(vTarget);
    }

    /**
     * 
     * 
     * @param index
     * @param vTarget
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTarget(
            final int index,
            final fr.jmmc.mf.models.Target vTarget)
    throws java.lang.IndexOutOfBoundsException {
        this._targetList.add(index, vTarget);
    }

    /**
     * Method enumerateTarget.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Target> enumerateTarget(
    ) {
        return java.util.Collections.enumeration(this._targetList);
    }

    /**
     * Method getTarget.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Target at the
     * given index
     */
    public fr.jmmc.mf.models.Target getTarget(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._targetList.size()) {
            throw new IndexOutOfBoundsException("getTarget: Index value '" + index + "' not in range [0.." + (this._targetList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Target) _targetList.get(index);
    }

    /**
     * Method getTarget.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Target[] getTarget(
    ) {
        fr.jmmc.mf.models.Target[] array = new fr.jmmc.mf.models.Target[0];
        return (fr.jmmc.mf.models.Target[]) this._targetList.toArray(array);
    }

    /**
     * Method getTargetCount.
     * 
     * @return the size of this collection
     */
    public int getTargetCount(
    ) {
        return this._targetList.size();
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
     * Method iterateTarget.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Target> iterateTarget(
    ) {
        return this._targetList.iterator();
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
    public void removeAllTarget(
    ) {
        this._targetList.clear();
    }

    /**
     * Method removeTarget.
     * 
     * @param vTarget
     * @return true if the object was removed from the collection.
     */
    public boolean removeTarget(
            final fr.jmmc.mf.models.Target vTarget) {
        boolean removed = _targetList.remove(vTarget);
        return removed;
    }

    /**
     * Method removeTargetAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Target removeTargetAt(
            final int index) {
        java.lang.Object obj = this._targetList.remove(index);
        return (fr.jmmc.mf.models.Target) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vTarget
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTarget(
            final int index,
            final fr.jmmc.mf.models.Target vTarget)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._targetList.size()) {
            throw new IndexOutOfBoundsException("setTarget: Index value '" + index + "' not in range [0.." + (this._targetList.size() - 1) + "]");
        }

        this._targetList.set(index, vTarget);
    }

    /**
     * 
     * 
     * @param vTargetArray
     */
    public void setTarget(
            final fr.jmmc.mf.models.Target[] vTargetArray) {
        //-- copy array
        _targetList.clear();

        for (int i = 0; i < vTargetArray.length; i++) {
                this._targetList.add(vTargetArray[i]);
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
     * @return the unmarshaled fr.jmmc.mf.models.Targets
     */
    public static fr.jmmc.mf.models.Targets unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Targets) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Targets.class, reader);
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
