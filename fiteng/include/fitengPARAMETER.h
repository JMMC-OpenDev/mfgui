#ifndef fitengPARAMETER_H
#define fitengPARAMETER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengPARAMETER.h,v 1.3 2006-07-04 08:25:42 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/06/27 08:54:14  lsauge
 * Fix some problems
 *
 * Revision 1.1  2006/03/20 07:12:11  gzins
 * Added
 *
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of fitengPARAMETER class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System headers
 */
#include <iostream>
#include <string>

/*
 * MCS header
 */
#include "mcs.h"

/** Minus infinity*/
#define fitengMINUS_INFINITY -1e300
/** Plus infinity*/
#define fitengPLUS_INFINITY   1e300

/*
 * Class declaration
 */

/**
 * This class is used by model to make them adjustable.
 * 
 */
class fitengPARAMETER
{

public:
    // Class constructor
    fitengPARAMETER(string name);

    // Class destructor
    virtual ~fitengPARAMETER();

    // Relative to name
    virtual const string  GetName();
    virtual mcsCOMPL_STAT SetName(string name="");

    // Relative to values
    virtual mcsDOUBLE     GetValue();
    virtual mcsCOMPL_STAT SetValue(mcsDOUBLE value);
    
    virtual mcsDOUBLE     GetMinValue();
    virtual mcsCOMPL_STAT SetMinValue(mcsDOUBLE value);
    virtual mcsCOMPL_STAT ResetMinValue();
    
    virtual mcsDOUBLE     GetMaxValue();
    virtual mcsCOMPL_STAT SetMaxValue(mcsDOUBLE value);
    virtual mcsCOMPL_STAT ResetMaxValue();

    virtual mcsDOUBLE     GetScale();
    virtual mcsCOMPL_STAT SetScale(mcsDOUBLE value);
    
    // Relative to flags
    virtual mcsLOGICAL    HasFixedValue();
    virtual mcsCOMPL_STAT SetFixedValue(mcsLOGICAL flag);
    virtual mcsLOGICAL    HasMinValue();
    virtual mcsLOGICAL    HasMaxValue();
    
    friend  std::ostream& operator<< (std::ostream& stream,
                                      const fitengPARAMETER&   parameter);
protected:
    
    string _name;                   /* name of parameter */

    mcsDOUBLE _value;               /* current parameter value */
    mcsDOUBLE _minValue, _maxValue; /* value bounds which cannot be violated
                                       (e.g. because of physical constraints)
                                       they are set by modeler function */
    mcsDOUBLE _scale;               /* scale for parameters normalization */

    // Flags 
    mcsLOGICAL _hasFixedValue;
    mcsLOGICAL _hasMinValue;
    mcsLOGICAL _hasMaxValue;
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    fitengPARAMETER(const fitengPARAMETER&);
    fitengPARAMETER& operator=(const fitengPARAMETER&);

};

#endif /*!fitengPARAMETER_H*/

/*___oOo___*/
