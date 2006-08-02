#ifndef fitengRESIDUE_LIST_H
#define fitengRESIDUE_LIST_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengRESIDUE_LIST.h,v 1.1 2006-03-20 07:12:11 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of fitengRESIDUE_LIST class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System headers
 */
#include <iostream>
#include <string>
#include <vector>

/*
 * MCS header
 */
#include "mcs.h"


/*
 * Class declaration
 */

/**
 * This class is used by model to store residue values used to fit parameters.
 */
class fitengRESIDUE_LIST
{

public:
    // Class constructor
    fitengRESIDUE_LIST(mcsUINT32 size);

    // Class destructor
    virtual ~fitengRESIDUE_LIST();

    // Relative to size
    virtual mcsUINT32       Size(void);

    // Relative to values
    virtual mcsCOMPL_STAT GetValue(mcsUINT32 index, mcsDOUBLE *value);
    virtual mcsCOMPL_STAT SetValue(mcsUINT32 index, mcsDOUBLE value);

    virtual mcsDOUBLE *GetArray(void);

    virtual mcsDOUBLE GetChi2(void);
    
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    fitengRESIDUE_LIST(const fitengRESIDUE_LIST&);
    fitengRESIDUE_LIST& operator=(const fitengRESIDUE_LIST&);

    mcsDOUBLE *_array;
    mcsUINT32 _size;

    // Flags 
};

#endif /*!fitengRESIDUE_LIST_H*/

/*___oOo___*/
