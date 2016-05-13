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
     * Define a message to be displayed.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.Message> _messageList;

    /**
     * Result file description.
     *  
     */
    private java.util.List<fr.jmmc.mf.models.ResultFile> _resultFileList;

    /**
     * Highest element of settings.
     *  
     */
    private fr.jmmc.mf.models.Settings _settings;

    /**
     * A common representation of every models.
     *  
     */
    private fr.jmmc.mf.models.Model _model;

    /**
     * Contains fitters.
     *  
     */
    private fr.jmmc.mf.models.Fitters _fitters;


      //----------------/
     //- Constructors -/
    //----------------/

    public Response() {
        super();
        this._messageList = new java.util.ArrayList<fr.jmmc.mf.models.Message>();
        this._resultFileList = new java.util.ArrayList<fr.jmmc.mf.models.ResultFile>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vMessage
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMessage(
            final fr.jmmc.mf.models.Message vMessage)
    throws java.lang.IndexOutOfBoundsException {
        this._messageList.add(vMessage);
    }

    /**
     * 
     * 
     * @param index
     * @param vMessage
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMessage(
            final int index,
            final fr.jmmc.mf.models.Message vMessage)
    throws java.lang.IndexOutOfBoundsException {
        this._messageList.add(index, vMessage);
    }

    /**
     * 
     * 
     * @param vResultFile
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResultFile(
            final fr.jmmc.mf.models.ResultFile vResultFile)
    throws java.lang.IndexOutOfBoundsException {
        this._resultFileList.add(vResultFile);
    }

    /**
     * 
     * 
     * @param index
     * @param vResultFile
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addResultFile(
            final int index,
            final fr.jmmc.mf.models.ResultFile vResultFile)
    throws java.lang.IndexOutOfBoundsException {
        this._resultFileList.add(index, vResultFile);
    }

    /**
     * Method enumerateMessage.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.Message> enumerateMessage(
    ) {
        return java.util.Collections.enumeration(this._messageList);
    }

    /**
     * Method enumerateResultFile.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends fr.jmmc.mf.models.ResultFile> enumerateResultFile(
    ) {
        return java.util.Collections.enumeration(this._resultFileList);
    }

    /**
     * Returns the value of field 'fitters'. The field 'fitters'
     * has the following description: Contains fitters.
     *  
     * 
     * @return the value of field 'Fitters'.
     */
    public fr.jmmc.mf.models.Fitters getFitters(
    ) {
        return this._fitters;
    }

    /**
     * Method getMessage.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.Message at the
     * given index
     */
    public fr.jmmc.mf.models.Message getMessage(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._messageList.size()) {
            throw new IndexOutOfBoundsException("getMessage: Index value '" + index + "' not in range [0.." + (this._messageList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.Message) _messageList.get(index);
    }

    /**
     * Method getMessage.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.Message[] getMessage(
    ) {
        fr.jmmc.mf.models.Message[] array = new fr.jmmc.mf.models.Message[0];
        return (fr.jmmc.mf.models.Message[]) this._messageList.toArray(array);
    }

    /**
     * Method getMessageCount.
     * 
     * @return the size of this collection
     */
    public int getMessageCount(
    ) {
        return this._messageList.size();
    }

    /**
     * Returns the value of field 'model'. The field 'model' has
     * the following description: A common representation of every
     * models.
     *  
     * 
     * @return the value of field 'Model'.
     */
    public fr.jmmc.mf.models.Model getModel(
    ) {
        return this._model;
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
     * Method getResultFile.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the fr.jmmc.mf.models.ResultFile at the
     * given index
     */
    public fr.jmmc.mf.models.ResultFile getResultFile(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._resultFileList.size()) {
            throw new IndexOutOfBoundsException("getResultFile: Index value '" + index + "' not in range [0.." + (this._resultFileList.size() - 1) + "]");
        }

        return (fr.jmmc.mf.models.ResultFile) _resultFileList.get(index);
    }

    /**
     * Method getResultFile.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public fr.jmmc.mf.models.ResultFile[] getResultFile(
    ) {
        fr.jmmc.mf.models.ResultFile[] array = new fr.jmmc.mf.models.ResultFile[0];
        return (fr.jmmc.mf.models.ResultFile[]) this._resultFileList.toArray(array);
    }

    /**
     * Method getResultFileCount.
     * 
     * @return the size of this collection
     */
    public int getResultFileCount(
    ) {
        return this._resultFileList.size();
    }

    /**
     * Returns the value of field 'settings'. The field 'settings'
     * has the following description: Highest element of settings.
     *  
     * 
     * @return the value of field 'Settings'.
     */
    public fr.jmmc.mf.models.Settings getSettings(
    ) {
        return this._settings;
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
     * Method iterateMessage.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.Message> iterateMessage(
    ) {
        return this._messageList.iterator();
    }

    /**
     * Method iterateResultFile.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends fr.jmmc.mf.models.ResultFile> iterateResultFile(
    ) {
        return this._resultFileList.iterator();
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
    public void removeAllMessage(
    ) {
        this._messageList.clear();
    }

    /**
     */
    public void removeAllResultFile(
    ) {
        this._resultFileList.clear();
    }

    /**
     * Method removeMessage.
     * 
     * @param vMessage
     * @return true if the object was removed from the collection.
     */
    public boolean removeMessage(
            final fr.jmmc.mf.models.Message vMessage) {
        boolean removed = _messageList.remove(vMessage);
        return removed;
    }

    /**
     * Method removeMessageAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.Message removeMessageAt(
            final int index) {
        java.lang.Object obj = this._messageList.remove(index);
        return (fr.jmmc.mf.models.Message) obj;
    }

    /**
     * Method removeResultFile.
     * 
     * @param vResultFile
     * @return true if the object was removed from the collection.
     */
    public boolean removeResultFile(
            final fr.jmmc.mf.models.ResultFile vResultFile) {
        boolean removed = _resultFileList.remove(vResultFile);
        return removed;
    }

    /**
     * Method removeResultFileAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.jmmc.mf.models.ResultFile removeResultFileAt(
            final int index) {
        java.lang.Object obj = this._resultFileList.remove(index);
        return (fr.jmmc.mf.models.ResultFile) obj;
    }

    /**
     * Sets the value of field 'fitters'. The field 'fitters' has
     * the following description: Contains fitters.
     *  
     * 
     * @param fitters the value of field 'fitters'.
     */
    public void setFitters(
            final fr.jmmc.mf.models.Fitters fitters) {
        this._fitters = fitters;
    }

    /**
     * 
     * 
     * @param index
     * @param vMessage
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setMessage(
            final int index,
            final fr.jmmc.mf.models.Message vMessage)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._messageList.size()) {
            throw new IndexOutOfBoundsException("setMessage: Index value '" + index + "' not in range [0.." + (this._messageList.size() - 1) + "]");
        }

        this._messageList.set(index, vMessage);
    }

    /**
     * 
     * 
     * @param vMessageArray
     */
    public void setMessage(
            final fr.jmmc.mf.models.Message[] vMessageArray) {
        //-- copy array
        _messageList.clear();

        for (int i = 0; i < vMessageArray.length; i++) {
                this._messageList.add(vMessageArray[i]);
        }
    }

    /**
     * Sets the value of field 'model'. The field 'model' has the
     * following description: A common representation of every
     * models.
     *  
     * 
     * @param model the value of field 'model'.
     */
    public void setModel(
            final fr.jmmc.mf.models.Model model) {
        this._model = model;
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
     * @param vResultFile
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setResultFile(
            final int index,
            final fr.jmmc.mf.models.ResultFile vResultFile)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._resultFileList.size()) {
            throw new IndexOutOfBoundsException("setResultFile: Index value '" + index + "' not in range [0.." + (this._resultFileList.size() - 1) + "]");
        }

        this._resultFileList.set(index, vResultFile);
    }

    /**
     * 
     * 
     * @param vResultFileArray
     */
    public void setResultFile(
            final fr.jmmc.mf.models.ResultFile[] vResultFileArray) {
        //-- copy array
        _resultFileList.clear();

        for (int i = 0; i < vResultFileArray.length; i++) {
                this._resultFileList.add(vResultFileArray[i]);
        }
    }

    /**
     * Sets the value of field 'settings'. The field 'settings' has
     * the following description: Highest element of settings.
     *  
     * 
     * @param settings the value of field 'settings'.
     */
    public void setSettings(
            final fr.jmmc.mf.models.Settings settings) {
        this._settings = settings;
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
