/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.jmmc.mf.models;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Contains target elements.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Targets implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Target representation.
     *  
     */
    private java.util.ArrayList _targetList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Targets() {
        super();
        _targetList = new ArrayList();
    } //-- fr.jmmc.mf.models.Targets()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addTarget
     * 
     * @param vTarget
     */
    public void addTarget(fr.jmmc.mf.models.Target vTarget)
        throws java.lang.IndexOutOfBoundsException
    {
        _targetList.add(vTarget);
    } //-- void addTarget(fr.jmmc.mf.models.Target) 

    /**
     * Method addTarget
     * 
     * @param index
     * @param vTarget
     */
    public void addTarget(int index, fr.jmmc.mf.models.Target vTarget)
        throws java.lang.IndexOutOfBoundsException
    {
        _targetList.add(index, vTarget);
    } //-- void addTarget(int, fr.jmmc.mf.models.Target) 

    /**
     * Method clearTarget
     */
    public void clearTarget()
    {
        _targetList.clear();
    } //-- void clearTarget() 

    /**
     * Method enumerateTarget
     */
    public java.util.Enumeration enumerateTarget()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_targetList.iterator());
    } //-- java.util.Enumeration enumerateTarget() 

    /**
     * Method getTarget
     * 
     * @param index
     */
    public fr.jmmc.mf.models.Target getTarget(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _targetList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mf.models.Target) _targetList.get(index);
    } //-- fr.jmmc.mf.models.Target getTarget(int) 

    /**
     * Method getTarget
     */
    public fr.jmmc.mf.models.Target[] getTarget()
    {
        int size = _targetList.size();
        fr.jmmc.mf.models.Target[] mArray = new fr.jmmc.mf.models.Target[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mf.models.Target) _targetList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mf.models.Target[] getTarget() 

    /**
     * Method getTargetCount
     */
    public int getTargetCount()
    {
        return _targetList.size();
    } //-- int getTargetCount() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeTarget
     * 
     * @param vTarget
     */
    public boolean removeTarget(fr.jmmc.mf.models.Target vTarget)
    {
        boolean removed = _targetList.remove(vTarget);
        return removed;
    } //-- boolean removeTarget(fr.jmmc.mf.models.Target) 

    /**
     * Method setTarget
     * 
     * @param index
     * @param vTarget
     */
    public void setTarget(int index, fr.jmmc.mf.models.Target vTarget)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _targetList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _targetList.set(index, vTarget);
    } //-- void setTarget(int, fr.jmmc.mf.models.Target) 

    /**
     * Method setTarget
     * 
     * @param targetArray
     */
    public void setTarget(fr.jmmc.mf.models.Target[] targetArray)
    {
        //-- copy array
        _targetList.clear();
        for (int i = 0; i < targetArray.length; i++) {
            _targetList.add(targetArray[i]);
        }
    } //-- void setTarget(fr.jmmc.mf.models.Target) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mf.models.Targets unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mf.models.Targets) Unmarshaller.unmarshal(fr.jmmc.mf.models.Targets.class, reader);
    } //-- fr.jmmc.mf.models.Targets unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

    public String toString(){ return "Targets"; } 
}
