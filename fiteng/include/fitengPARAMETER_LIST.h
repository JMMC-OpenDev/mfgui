#ifndef fitengPARAMETER_LIST_H
#define fitengPARAMETER_LIST_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengPARAMETER_LIST.h,v 1.3 2006-07-04 08:25:36 lsauge Exp $"
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
 * Declaration of fitengPARAMETER_LIST class.
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
#include "fitengPARAMETER.h"



/*
 * Class declaration
 */

/**
 * This class is used to handle the list of parameters to fit.
 */
class fitengPARAMETER_LIST
{

public:
    // Class constructor
    fitengPARAMETER_LIST();

    // Class destructor
    virtual ~fitengPARAMETER_LIST();

    virtual mcsLOGICAL      IsEmpty(void);
    virtual mcsCOMPL_STAT   Clear(void);
    virtual mcsCOMPL_STAT   AddAtTail(fitengPARAMETER *parameter);
    virtual mcsCOMPL_STAT   InsertAtPos(fitengPARAMETER *parameter, mcsUINT16 pos);

    virtual mcsUINT32       Size(void);

    virtual fitengPARAMETER* GetNextParameter(mcsLOGICAL init = mcsFALSE);
    virtual fitengPARAMETER* GetParameter(string name);

    virtual mcsDOUBLE*      GetArray(void);

    virtual void            Display(void);

    virtual mcsCOMPL_STAT   ComputeParameterScale( mcsLOGICAL verbose = mcsTRUE, 
                                                   mcsUINT16 method = 0);
    virtual void            SetParametersFromArray();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    fitengPARAMETER_LIST(const fitengPARAMETER_LIST&);
    fitengPARAMETER_LIST& operator=(const fitengPARAMETER_LIST&);

    std::vector<fitengPARAMETER *> _list;
    mcsINT32                 _index;

    mcsDOUBLE                *_array;
};

#endif /*!fitengPARAMETER_LIST_H*/

/*___oOo___*/
