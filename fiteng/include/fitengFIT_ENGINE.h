#ifndef fitengFIT_ENGINE_H
#define fitengFIT_ENGINE_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengFIT_ENGINE.h,v 1.3 2006-08-02 08:10:01 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/31 08:11:02  gzins
 * Added parameter and residue lists
 *
 * Revision 1.1  2006/03/20 07:12:11  gzins
 * Added
 *
 * Revision 1.1  2006/03/01 13:19:34  lsauge
 * Creation and First release
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of fitengFIT_ENGINE class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

/*
 * MFIT header
 */
#include "fitengPARAMETER_LIST.h"
#include "fitengRESIDUE_LIST.h"

/*
 * Class declaration
 */

/**
 * Abstract class for FIT engine. 
 * 
 * This class defines API for FIT engine.
 */
class fitengFIT_ENGINE
{

public:
    // Class constructor
    fitengFIT_ENGINE();

    // Class destructor
    virtual ~fitengFIT_ENGINE();
    
    virtual mcsCOMPL_STAT Init(fitengPARAMETER_LIST *parameterList,
                               fitengRESIDUE_LIST   *residueList);
    virtual mcsCOMPL_STAT DoNextStep() = 0;

    virtual const char *GetDescription();
    mcsCOMPL_STAT       GetVersion(mcsUINT16 &major,mcsUINT16 &minor);

protected:
    // Parameter and residue lists
    fitengPARAMETER_LIST *_parameterList;
    fitengRESIDUE_LIST   *_residueList;

    /** Engine description string */
    string _description ;
    /** Versioning number */
    mcsUINT16 _versionMajor;
    mcsUINT16 _versionMinor;

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    fitengFIT_ENGINE(const fitengFIT_ENGINE&);
    fitengFIT_ENGINE& operator=(const fitengFIT_ENGINE&);
};

#endif /*!fitengFIT_ENGINE_H*/

/*___oOo___*/
