/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

/**
 * Target representation.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Target implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ident.
     */
    private java.lang.String _ident;

    /**
     * File link.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.FileLink> _fileLinkList;

    /**
     * A common representation of every models.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Model> _modelList;

    /**
     * Field _normalize.
     */
    private boolean _normalize;

    /**
     * keeps track of state for field: _normalize
     */
    private boolean _has_normalize;

    /**
     * Residuals configuration.
     *  
     */
    private fr.jmmc.mf.models.Residuals _residuals;


      //----------------/
     //- Constructors -/
    //----------------/

    public Target() {
        super();
        this._fileLinkList = new java.util.ArrayList<fr.jmmc.mf.models.FileLink>();
        this._modelList = new java.util.ArrayList<fr.jmmc.mf.models.Model>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vFileLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFileLink(
            final fr.jmmc.mf.models.FileLink vFileLink)
    throws java.lang.IndexOutOfBoundsException {
        this._fileLinkList.add(vFileLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vFileLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFileLink(
            final int index,
            final fr.jmmc.mf.models.FileLink vFileLink)
    throws java.lang.IndexOutOfBoundsException {
        this._fileLinkList.add(index, vFileLink);
    }

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
     */
    public void deleteNormalize(
    ) {
        this._has_normalize= false;
    }

    /**
     * Method enumerateFileLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.FileLink> enumerateFileLink(
    ) {
        return java.util.Collections.enumeration(this._fileLinkList);
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
     * Method getFileLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.FileLink at the
     * given index
     */
    public fr.jmmc.mf.models.FileLink getFileLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fileLinkList.size()) {
            throw new IndexOutOfBoundsException("getFileLink: Index value '" + index + "' not in range [0.." + (this._fileLinkList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.FileLink) _fileLinkList.get(index);
    }

    /**
     * Method getFileLink.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.FileLink[] getFileLink(
    ) {
        fr.jmmc.mf.models.FileLink[] array = new fr.jmmc.mf.models.FileLink[0];
        return (fr.jmmc.mf.models.FileLink[]) this._fileLinkList.toArray(array);
    }

    /**
     * Method getFileLinkCount.
     * 
     * @return the size of this collection
     */
    public int getFileLinkCount(
    ) {
        return this._fileLinkList.size();
    }

    /**
     * Returns the value of field 'ident'.
     * 
     * @return the value of field 'Ident'.
     */
    public java.lang.String getIdent(
    ) {
        return this._ident;
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
     * Returns the value of field 'normalize'.
     * 
     * @return the value of field 'Normalize'.
     */
    public boolean getNormalize(
    ) {
        return this._normalize;
    }

    /**
     * Returns the value of field 'residuals'. The field
     * 'residuals' has the following description: Residuals
     * configuration.
     *  
     * 
     * @return the value of field 'Residuals'.
     */
    public fr.jmmc.mf.models.Residuals getResiduals(
    ) {
        return this._residuals;
    }

    /**
     * Method hasNormalize.
     * 
     * @return true if at least one Normalize has been added
     */
    public boolean hasNormalize(
    ) {
        return this._has_normalize;
    }

    /**
     * Returns the value of field 'normalize'.
     * 
     * @return the value of field 'Normalize'.
     */
    public boolean isNormalize(
    ) {
        return this._normalize;
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
     * Method iterateFileLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.FileLink> iterateFileLink(
    ) {
        return this._fileLinkList.iterator();
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
    public void removeAllFileLink(
    ) {
        this._fileLinkList.clear();
    }

    /**
     */
    public void removeAllModel(
    ) {
        this._modelList.clear();
    }

    /**
     * Method removeFileLink.
     * 
     * @param vFileLink
     * @return true if the object was removed from the collection.
     */
    public boolean removeFileLink(
            final fr.jmmc.mf.models.FileLink vFileLink) {
        boolean removed = _fileLinkList.remove(vFileLink);
        return removed;
    }

    /**
     * Method removeFileLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.FileLink removeFileLinkAt(
            final int index) {
        java.lang.Object obj = this._fileLinkList.remove(index);
        return (fr.jmmc.mf.models.FileLink) obj;
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
     * 
     * 
     * @param index
     * @param vFileLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFileLink(
            final int index,
            final fr.jmmc.mf.models.FileLink vFileLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fileLinkList.size()) {
            throw new IndexOutOfBoundsException("setFileLink: Index value '" + index + "' not in range [0.." + (this._fileLinkList.size() - 1) + "]");
        }

        this._fileLinkList.set(index, vFileLink);
    }

    /**
     * 
     * 
     * @param vFileLinkArray
     */
    public void setFileLink(
            final fr.jmmc.mf.models.FileLink[] vFileLinkArray) {
        //-- copy array
        _fileLinkList.clear();

        for (int i = 0; i < vFileLinkArray.length; i++) {
                this._fileLinkList.add(vFileLinkArray[i]);
        }
    }

    /**
     * Sets the value of field 'ident'.
     * 
     * @param ident the value of field 'ident'.
     */
    public void setIdent(
            final java.lang.String ident) {
        this._ident = ident;
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
     * Sets the value of field 'normalize'.
     * 
     * @param normalize the value of field 'normalize'.
     */
    public void setNormalize(
            final boolean normalize) {
        this._normalize = normalize;
        this._has_normalize = true;
    }

    /**
     * Sets the value of field 'residuals'. The field 'residuals'
     * has the following description: Residuals configuration.
     *  
     * 
     * @param residuals the value of field 'residuals'.
     */
    public void setResiduals(
            final fr.jmmc.mf.models.Residuals residuals) {
        this._residuals = residuals;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled fr.jmmc.mf.models.Target
     */
    public static fr.jmmc.mf.models.Target unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (fr.jmmc.mf.models.Target) org.exolab.castor.xml.Unmarshaller.unmarshal(fr.jmmc.mf.models.Target.class, reader);
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

    public String toString(){ return "Target["+getIdent()+"]"; } 
}
