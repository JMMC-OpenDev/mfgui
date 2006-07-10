#ifndef mfmdlVIS_H
#define mfmdlVIS_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlVIS.h,v 1.2 2006-06-20 09:25:15 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/31 08:16:16  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of mfmdlVIS class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"


/*
 * Class declaration
 */

/**
 * Complex visibilities 
 * 
 * This class is used to stored an array of complex visibilities 
 *
 * @usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * @filename fileName1 :  usage description of fileName1
 * @filename fileName2 :  usage description of fileName2
 *
 * @env
 * OPTIONAL. If needed, environmental variables accessed by the class. For
 * each variable, name, and usage description, as below.
 * @envvar envVar1 :  usage description of envVar1
 * @envvar envVar2 :  usage description of envVar2
 * 
 * @warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * @ex
 * OPTIONAL. Code example if needed
 * \n Brief example description.
 * @code
 * Insert your code example here
 * @endcode
 *
 * @sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * @sa modcppMain.C
 * 
 * @bug OPTIONAL. Bugs list if it exists.
 * @bug For example, description of the first bug
 * @bug For example, description of the second bug
 * 
 * @todo OPTIONAL. Things to forsee list, if needed. For example, 
 * @todo add other methods, dealing with operations.
 * 
 */
class mfmdlVIS
{

public:
    // Class constructor
    mfmdlVIS(mcsUINT32 size);

    // Class destructor
    virtual ~mfmdlVIS();

    // Methods related to array size
    mcsUINT32 Size();
    mcsCOMPL_STAT Resize(mcsUINT32 newSize);
    
    // Methods to set/get visibilities 
    mcsCOMPL_STAT Set(mcsUINT32 index, mcsDOUBLE re, mcsDOUBLE im);
    mcsCOMPL_STAT Get(mcsUINT32 index, mcsDOUBLE &re, mcsDOUBLE &im);
    mcsCOMPLEX*   Get(mcsUINT32 index);
    mcsCOMPL_STAT GetVis2(mcsUINT32 index, mcsDOUBLE &res);
    mcsDOUBLE     GetVis2(mcsUINT32 index);

    // Add Method
    mcsCOMPL_STAT Add(mfmdlVIS &vis);
    
    // Reset Method : set all element value to (0,0)
    mcsCOMPL_STAT mfmdlVIS::Reset();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlVIS(const mfmdlVIS&);
    mfmdlVIS& operator=(const mfmdlVIS&);

    mcsUINT32  _size;
    mcsCOMPLEX *_array;
};

#endif /*!mfmdlVIS_H*/

/*___oOo___*/
