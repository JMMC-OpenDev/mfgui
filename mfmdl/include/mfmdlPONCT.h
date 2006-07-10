#ifndef mfmdlPONCT_H
#define mfmdlPONCT_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlPONCT.h,v 1.6 2006-07-04 08:37:15 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2006/06/20 09:25:15  lsauge
 * Implementation of general methods
 *
 * Revision 1.4  2006/03/31 08:16:47  gzins
 * Updated API
 *
 * Revision 1.3  2006/03/01 14:08:21  lsauge
 * Suppress keyword mjd, time and waves from the declaration of Eval() function
 *
 * Revision 1.2  2006/02/03 16:45:00  lsauge
 * Correct herited prototype of Eval method
 *
 * Revision 1.1  2005/09/08 14:06:11  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of mfmdlPONCT class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"


#include "mfmdlMODEL.h"
/*
 * Class declaration
 */

/**
 *  Model of ponctual object brigthness distrib.
 */
class mfmdlPONCT : public mfmdlMODEL
{

public:
    // Class constructor
    mfmdlPONCT(string modelName, mcsLOGICAL polar=mcsTRUE);

    // Class destructor
    virtual ~mfmdlPONCT();

    virtual mcsCOMPL_STAT Eval(mfmdlUV_COORDS &uvCoords, mfmdlVIS &vis);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlPONCT(const mfmdlPONCT&);
    mfmdlPONCT& operator=(const mfmdlPONCT&);

};

#endif /*!mfmdlPONCT_H*/

/*___oOo___*/
