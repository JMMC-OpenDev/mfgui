/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengFIT_ENGINE.cpp,v 1.5 2006-08-02 08:09:53 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/06/27 08:53:18  lsauge
 * Minor changes
 *
 * Revision 1.2  2006/03/31 08:07:35  gzins
 * Added Init method
 *
 * Revision 1.1  2006/03/20 07:11:39  gzins
 * Added
 *
 * Revision 1.2  2006/03/01 14:50:58  lsauge
 * Update documentation
 *
 * Revision 1.1  2006/03/01 13:17:41  lsauge
 * First Release of encapsulation classes of the fit engine
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of fitengFIT_ENGINE class.
 */

static char *rcsId="@(#) $Id: fitengFIT_ENGINE.cpp,v 1.5 2006-08-02 08:09:53 lsauge Exp $"; 
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
#include "fitengFIT_ENGINE.h"
#include "fitengPrivate.h"
#include "fitengErrors.h"

/**
 * Class constructor
 */
fitengFIT_ENGINE::fitengFIT_ENGINE()
{
    logTrace("fitengFIT_ENGINE::mseqFIT_ENGINE()");

    _versionMajor = 0;
    _versionMinor = 0;
}

/**
 * Class destructor
 */
fitengFIT_ENGINE::~fitengFIT_ENGINE()
{
    logTrace("fitengFIT_ENGINE::~mseqFIT_ENGINE()");
}

mcsCOMPL_STAT fitengFIT_ENGINE::Init(fitengPARAMETER_LIST *parameterList,
                                     fitengRESIDUE_LIST   *residueList)
{
    logExtDbg("fitengFIT_ENGINE::Init()");

    // Check null pointer
    if (parameterList == NULL)
    {
        errAdd(fitengERR_NULL_PARAM, "parameter list");
        return(mcsFAILURE);
    }
    if (residueList == NULL)
    {
        errAdd(fitengERR_NULL_PARAM, "residue list");
        return(mcsFAILURE);
    }

    // Check degree-of-Freedom is greater than 0
    if(parameterList->Size() > residueList->Size())
    {
        errAdd(fitengERR_FREEDOM, parameterList->Size(), residueList->Size());
        return(mcsFAILURE);
    }

    // Saved pointer to parameter and residue lists
    _parameterList = parameterList;
    _residueList   = residueList;

    return mcsSUCCESS;
}

/*!
  Get the engine description string
  @return   string contenaing the engine type description
*/
const char *fitengFIT_ENGINE::GetDescription()
{
    logTrace("fitengFITENGINE::GetDescription()");
    if (_description.empty() )
    {
        return "No description available";
    }
    return _description.data();
}

/*!
    Get the versioning number of the current engine
    Version number consist in a major and minor unsigned integer.
    @param major    major version number
    @param minor    minor version number
    @return always mcsSUCCESS
*/
mcsCOMPL_STAT fitengFIT_ENGINE::GetVersion(
        mcsUINT16 &major,
        mcsUINT16 &minor
        )
{
    logTrace("fitengFITENGINE::GetVersion()");

    major = _versionMajor;
    minor = _versionMinor;
    
    return mcsSUCCESS;
}



/*___oOo___*/
