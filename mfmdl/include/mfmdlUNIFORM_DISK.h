#ifndef mfmdlUNIFORM_DISK_H
#define mfmdlUNIFORM_DISK_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlUNIFORM_DISK.h,v 1.2 2006-07-04 08:37:15 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/06/27 08:42:10  lsauge
 * First implementation
 *
 * 
 ******************************************************************************/

/**
 * \file
 * Declaration of mfmdlUNIFORM_DISK class.
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
class mfmdlUNIFORM_DISK : public mfmdlMODEL
{

public:
    // Class constructor
    mfmdlUNIFORM_DISK(string modelName,mcsLOGICAL polar=mcsFALSE);

    // Class destructor
    virtual ~mfmdlUNIFORM_DISK();

    virtual mcsCOMPL_STAT Eval(mfmdlUV_COORDS &uvCoords, mfmdlVIS &vis);

protected:
    mfmdlPARAMETER  _sourceDiameter; 

    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlUNIFORM_DISK(const mfmdlUNIFORM_DISK&);
    mfmdlUNIFORM_DISK& operator=(const mfmdlUNIFORM_DISK&);

};

#endif /*!mfmdlUNIFORM_DISK_H*/

/*___oOo___*/
