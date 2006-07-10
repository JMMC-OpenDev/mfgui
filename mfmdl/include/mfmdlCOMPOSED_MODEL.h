#ifndef mfmdlCOMPOSED_MODEL_H
#define mfmdlCOMPOSED_MODEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlCOMPOSED_MODEL.h,v 1.6 2006-06-20 09:41:47 lsauge Exp $"
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
 * Revision 1.2  2006/02/03 16:46:13  lsauge
 * Added prototype of Eval method. Added also include call of mfmdlPONCT.h
 *
 * Revision 1.1  2005/09/08 14:06:11  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of mfmdlCOMPOSED_MODEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

#include "mfmdlMODEL.h"
#include "mfmdlPONCT.h"

#include <vector>

/*
 * Class declaration
 */

/**
 * This model is the base of a composed model.
 * mfmdlMODEL can be added to this to elaborate more complex ones.
 */
class mfmdlCOMPOSED_MODEL: public mfmdlMODEL
{

public:
    // Class constructor
    mfmdlCOMPOSED_MODEL(string name);

    // Class destructor
    virtual ~mfmdlCOMPOSED_MODEL();

    virtual mcsCOMPL_STAT GetParameters(mfmdlPARAMETER_LIST *paramList,
                                        mcsLOGICAL clear=mcsTRUE);

    virtual mcsCOMPL_STAT AddAtTail(mfmdlMODEL *model);
    virtual mcsINT32      GetNbOfComposingModels();
    virtual mcsCOMPL_STAT GetComposingModel(mcsUINT32 index, 
                                            mfmdlMODEL **model);
    
    virtual mcsCOMPL_STAT Eval(mfmdlUV_COORDS &uvCoords, mfmdlVIS &vis);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlCOMPOSED_MODEL(const mfmdlCOMPOSED_MODEL&);
    mfmdlCOMPOSED_MODEL& operator=(const mfmdlCOMPOSED_MODEL&);

    /** contains our composing parts */
    vector <mfmdlMODEL *> _composingModels;
};

#endif /*!mfmdlCOMPOSED_MODEL_H*/

/*___oOo___*/
