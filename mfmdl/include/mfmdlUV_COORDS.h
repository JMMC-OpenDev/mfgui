#ifndef mfmdlUV_COORDS_H
#define mfmdlUV_COORDS_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlUV_COORDS.h,v 1.3 2006-07-10 12:57:34 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/06/20 09:25:15  lsauge
 * Implementation of general methods
 *
 * Revision 1.1  2006/03/31 08:16:16  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of mfmdlUV_COORDS class.
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
 * Brief description of the class, which ends at this dot.
 * 
 * OPTIONAL detailed description of the class follows here.
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
class mfmdlUV_COORDS
{

public:
    // Class constructor
    mfmdlUV_COORDS(mcsUINT32 size);

    // Class destructor
    virtual ~mfmdlUV_COORDS();

    // Methods related to array size
    mcsUINT32 Size();
    mcsCOMPL_STAT Resize(mcsUINT32 newSize);
  
    // Methods to set/get UV coordinates 
    mcsCOMPL_STAT Set(mcsUINT32 index, mcsDOUBLE u, mcsDOUBLE v);
    mcsCOMPL_STAT Get(mcsUINT32 index, mcsDOUBLE &u, mcsDOUBLE &v);
   
    // Operator 
    mcsCOMPL_STAT Add(const mfmdlUV_COORDS &coords1,
                      const mfmdlUV_COORDS &coords2);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlUV_COORDS(const mfmdlUV_COORDS&);
    mfmdlUV_COORDS& operator=(const mfmdlUV_COORDS&);

    void Free(void *ptr);

    mcsUINT32  _size;
    mcsDOUBLE *_uArray;
    mcsDOUBLE *_vArray;

};

#endif /*!mfmdlUV_COORDS_H*/

/*___oOo___*/
