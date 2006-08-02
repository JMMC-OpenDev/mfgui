/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengRESIDUE_LIST.cpp,v 1.3 2006-06-27 08:47:54 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/20 07:11:39  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of fitengRESIDUE_LIST class.
 */

static char *rcsId="@(#) $Id: fitengRESIDUE_LIST.cpp,v 1.3 2006-06-27 08:47:54 lsauge Exp $"; 
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
#include "fitengRESIDUE_LIST.h"
#include "fitengPrivate.h"
#include "fitengErrors.h"

/**
 * Class constructor
 */
fitengRESIDUE_LIST::fitengRESIDUE_LIST(mcsUINT32 size)
{
    _array = (mcsDOUBLE *)calloc(size, sizeof(*_array));
    if(_array != NULL)
    {
        _size  = size ; 
    }
}

/**
 * Class destructor
 */
fitengRESIDUE_LIST::~fitengRESIDUE_LIST()
{
    if (_array != NULL)
    {
        free(_array);
    }
}

/*
 * Public methods
 */

/**
 * Returns the number of residues in the list.
 *
 * @return The numbers of parameters in the list.
 */
mcsUINT32 fitengRESIDUE_LIST::Size(void) 
{
    return _size;
}

/**
 * Get the nth residue of the list.
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT fitengRESIDUE_LIST::GetValue(mcsUINT32 index, mcsDOUBLE *value)
{
    logTrace("fitengRESIDUE_LIST::GetValue()");

    // Check if index is out of range 
    if (index >= _size)
    {
        errAdd(fitengERR_INDEX_RANGE, index, _size - 1);
        return mcsFAILURE;
    }
    
    // Get residue value 
    *value = _array[index];

    return mcsSUCCESS;
}

/**
 * Set the nth residue of the list.
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT fitengRESIDUE_LIST::SetValue(mcsUINT32 index, mcsDOUBLE value)
{
    logTrace("fitengRESIDUE_LIST::SetValue()");

    // Check if index is out of range 
    if (index >= _size)
    {
        errAdd(fitengERR_INDEX_RANGE, index, _size - 1);
        return mcsFAILURE;
    }
    
    // Get residue value 
    _array[index] = value;

    return mcsSUCCESS;
}

/**
 * Return to the residue array.
 *
 * @return pointer to the residue array.
 */
mcsDOUBLE *fitengRESIDUE_LIST::GetArray(void)
{
    logTrace("fitengRESIDUE_LIST::GetArray()");

    // Return residue array 
    return _array;
}

/**
 * Return to the chi 2 value corresponding to current residues.
 *
 * @return chi 2.
 */
mcsDOUBLE fitengRESIDUE_LIST::GetChi2(void)
{
    logTrace("fitengRESIDUE_LIST::GetChi2()");

    // Compute Chi2
    mcsDOUBLE chi2 = 0.0;
    
    for (mcsUINT32 index=0; index != _size; index++)
    {
        chi2 += _array[index] * _array[index];
    }

    // Return it 
    return chi2;
}

/*___oOo___*/
