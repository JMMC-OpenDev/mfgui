/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengPARAMETER.cpp,v 1.5 2006-07-04 08:25:23 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/06/27 08:48:57  lsauge
 * Set _scale default value to 0.0 (constructor)
 * Other minor changes
 *
 * Revision 1.2  2006/03/20 07:59:07  gzins
 * Minor documentation change
 *
 * Revision 1.1  2006/03/20 07:11:39  gzins
 * Added
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of fitengPARAMETER class.
 */

static char *rcsId="@(#) $Id: fitengPARAMETER.cpp,v 1.5 2006-07-04 08:25:23 lsauge Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "fiteng.h"
#include "fitengPARAMETER.h"
#include "fitengErrors.h"
#include "fitengPrivate.h"

/**
 * Class constructor
 */
fitengPARAMETER::fitengPARAMETER(string name)
{
    logTrace("fitengPARAMETER::fitengPARAMETER()");

    _name     = name; 

    _minValue = 0.0; 
    _maxValue = 0.0;
    _value    = 0.0;
    _scale    = 0.0;

    _hasFixedValue  = mcsFALSE;
    _hasMinValue    = mcsFALSE;
    _hasMaxValue    = mcsFALSE;
}

/**
 * Class destructor
 */
fitengPARAMETER::~fitengPARAMETER()
{
}

/*
 * Public methods
 */
/**

 * Set the parameter name 
 *
 * @param  name the parameter name
 * @return alwais mcsSUCCESS
 */
mcsCOMPL_STAT fitengPARAMETER::SetName(string name)
{
    logTrace("fitengPARAMETER::SetName()");
    _name     = name; 
    return mcsSUCCESS;
}


/**
 * Get the parameter name 
 *
 * @return the parameter name
 */
const string fitengPARAMETER::GetName()
{
    logTrace("fitengPARAMETER::GetName()");

    return _name;
}

/**
 * Get the current value previously set by SetValue()
 *
 * @return the current value
 */
mcsDOUBLE fitengPARAMETER::GetValue()
{
    logTrace("fitengPARAMETER::GetValue()");
    
    return _value;
}

/**
 * Set the current value
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::SetValue(mcsDOUBLE value)
{
    logTrace("fitengPARAMETER::SetValue()");

    _value = value;
    
    return mcsSUCCESS;
}

/**
 * Get the minimum value previously set by SetMinValue()
 *
 * @return the minimum value
 */
mcsDOUBLE fitengPARAMETER::GetMinValue()
{
    logTrace("fitengPARAMETER::GetMinValue()");

    return _minValue;
}

/**
 * Set the minimum value
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::SetMinValue(mcsDOUBLE value)
{
    logTrace("fitengPARAMETER::SetMinValue()");
    
    _minValue = value;
    _hasMinValue = mcsTRUE;
    
    return mcsSUCCESS;
}

/**
 * Reset the minimum value
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::ResetMinValue()
{
    logTrace("fitengPARAMETER::ResetMinValue()");

    _minValue    = 0.0;
    _hasMinValue = mcsFALSE;

    return mcsSUCCESS;
}

/**
 * Get the maximum value previously set by SetMaxValue()
 *
 * @return the maximum value
 */
mcsDOUBLE fitengPARAMETER::GetMaxValue()
{
    logTrace("fitengPARAMETER::GetMaxValue()");

    return _maxValue;
}

/**
 * Set the maximum value
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::SetMaxValue(mcsDOUBLE value)
{
    logTrace("fitengPARAMETER::SetMaxValue()");

    _maxValue = value;
    _hasMaxValue = mcsTRUE;
    
    return mcsSUCCESS;
}

/**
 * Reset the maximum value
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::ResetMaxValue()
{
    logTrace("fitengPARAMETER::ResetMaxValue()");

    _maxValue    = 0.0;
    _hasMaxValue = mcsFALSE;

    return mcsSUCCESS;
}

/**
 * Get scale used to normalize parameter previously set by SetScale()
 *
 * @return the scale value
 */
mcsDOUBLE fitengPARAMETER::GetScale()
{
    logTrace("fitengPARAMETER::GetScale()");

    return _scale;
}

/**
 * Set scale used to normalize parameter
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::SetScale(mcsDOUBLE value)
{
    logTrace("fitengPARAMETER::SetScale()");

    _scale=value;
    return mcsSUCCESS;
}

/**
 * Return whether the parameter has fixed value or not.  
 *
 * @return mcsTRUE if the parameter has fixed value, mcsFALSE otherwise.
 */
mcsLOGICAL fitengPARAMETER::HasFixedValue()
{
    logTrace("fitengPARAMETER::HasFixedValue()");

    return _hasFixedValue;
}

/**
 * Fix/free parameter. 
 *
 * @return always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER::SetFixedValue(mcsLOGICAL flag)
{
    logTrace("fitengPARAMETER::SetFixedValue()");

    _hasFixedValue = flag;
    return mcsSUCCESS;
}

/**
 * Return whether the parameter has minimum value or not.  
 *
 * @return mcsTRUE if the parameter has minimum value, mcsFALSE otherwise.
 */
mcsLOGICAL fitengPARAMETER::HasMinValue()
{
    logTrace("fitengPARAMETER::HasNoMinValue()");

    return _hasMinValue;
}

/**
 * Return whether the parameter has maximum value or not.  
 *
 * @return mcsTRUE if the parameter has maximum value, mcsFALSE otherwise.
 */
mcsLOGICAL fitengPARAMETER::HasMaxValue()
{
    logTrace("fitengPARAMETER::HasNoMaxValue()");

    return _hasMaxValue;
}

std::ostream& operator<< (      std::ostream&  stream,
                          const fitengPARAMETER&    parameter)
{
    stream << "Parameter '" << parameter._name << "'" << endl;
    return stream;
}

/*___oOo___*/
