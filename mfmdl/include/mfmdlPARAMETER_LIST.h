#ifndef mfmdlPARAMETER_LIST_H
#define mfmdlPARAMETER_LIST_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlPARAMETER_LIST.h,v 1.1 2006-03-31 08:16:16 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of mfmdlPARAMETER_LIST class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System headers
 */
#include <iostream>
#include <vector>

/*
 * MCS header
 */
#include "mcs.h"

/*
 * MFIT header
 */
#include "mfmdlPARAMETER.h"



/*
 * Class declaration
 */

/**
 * This class is used to handle the list of parameters to fit.
 */
class mfmdlPARAMETER_LIST
{

public:
    // Class constructor
    mfmdlPARAMETER_LIST();

    // Class destructor
    virtual ~mfmdlPARAMETER_LIST();

    virtual mcsLOGICAL      IsEmpty(void);
    virtual mcsCOMPL_STAT   Clear(void);
    virtual mcsCOMPL_STAT   AddAtTail(mfmdlPARAMETER *parameter);

    virtual mcsUINT32       Size(void);

    virtual mfmdlPARAMETER* GetNextParameter(mcsLOGICAL init = mcsFALSE);
    virtual mfmdlPARAMETER* GetParameter(string name);

    virtual void            Display(void);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlPARAMETER_LIST(const mfmdlPARAMETER_LIST&);
    mfmdlPARAMETER_LIST& operator=(const mfmdlPARAMETER_LIST&);

    vector<mfmdlPARAMETER *> _list;
    mcsINT32                 _index;
};

#endif /*!mfmdlPARAMETER_LIST_H*/

/*___oOo___*/
