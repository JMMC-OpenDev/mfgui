/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlPARAMETER.cpp,v 1.6 2006-06-27 08:39:29 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2006/05/11 13:04:56  mella
 * Changed rcsId declaration to perform good gcc4 and gcc3 compilation
 *
 * Revision 1.4  2006/03/31 08:17:02  gzins
 * Updated API
 *
 * Revision 1.3  2006/01/23 10:54:18  mella
 * Add flag to set/unset bound limits
 *
 * Revision 1.2  2006/01/17 13:31:37  mella
 * Changed constructor
 *
 * Revision 1.1  2005/09/08 14:05:47  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of mfmdlPARAMETER class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlPARAMETER.cpp,v 1.6 2006-06-27 08:39:29 lsauge Exp $";
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
#include "mfmdl.h"
#include "mfmdlPARAMETER.h"
#include "mfmdlErrors.h"
#include "mfmdlPrivate.h"

/**
 * Class constructor
 */
mfmdlPARAMETER::mfmdlPARAMETER(string name, 
                               mcsDOUBLE initialValue,
                               string units,
                               mcsDOUBLE minValue,
                               mcsDOUBLE maxValue
                               ):
fitengPARAMETER(name)
{
    logTrace("mfmdlPARAMETER::mfmdlPARAMETER()");

    SetValue(initialValue);
     _units    = units;
     _minValue = minValue ; 
     _maxValue = maxValue ;
    
     // Check if min/max value are defined
     if( (minValue!=fitengMINUS_INFINITY) && (minValue!=fitengPLUS_INFINITY))
     {
        _hasMinValue = mcsTRUE;
     }
     if( (maxValue!=fitengMINUS_INFINITY) && (maxValue!=fitengPLUS_INFINITY))
     {
        _hasMaxValue = mcsTRUE;
     }
     _scale = 0.0;
}

/**
 * Class destructor
 */
mfmdlPARAMETER::~mfmdlPARAMETER()
{
}

/*
 * Public methods
 */
/**
 * Get the parameter units 
 *
 * @return the parameter units
 */
const string mfmdlPARAMETER::GetUnits()
{
    logTrace("mfmdlPARAMETER::GetUnits()");

    return _units;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
