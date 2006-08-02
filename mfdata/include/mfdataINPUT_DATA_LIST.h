#ifndef mfdataINPUT_DATA_LIST_H
#define mfdataINPUT_DATA_LIST_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_DATA_LIST.h,v 1.2 2006-07-05 14:05:29 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/06/27 12:05:36  lsauge
 * First import
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of mfdataINPUT_DATA_LIST class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

/*
 * System header 
 */
#include <iostream>
#include <vector>


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
class mfdataINPUT_DATA_LIST
{

public:
    // Class constructor
    mfdataINPUT_DATA_LIST();

    // Class destructor
    virtual ~mfdataINPUT_DATA_LIST();
    
    // Data handling functions
    virtual mcsCOMPL_STAT   AddInputData(mfdataINPUT_DATA *inputData);
    virtual mcsINT16        GetNbOfDataSet();
    virtual mcsINT16        GetNbOfData();
    virtual mfdataINPUT_DATA  *GetDataSet(mcsINT16 index);
    
    virtual mcsLOGICAL IsEmpty();
    mcsCOMPL_STAT Clear();

    // Extraction task
    mcsCOMPL_STAT ExtractVIS2DataFromOIFITS (mcsSTRING256 filename);
    mcsCOMPL_STAT ExtractT3DataFromOIFITS   (mcsSTRING256 filename);

protected:
    
private:
    
    /** vector of inputData */
    std::vector < mfdataINPUT_DATA * > _inputDataList;
    
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfdataINPUT_DATA_LIST(const mfdataINPUT_DATA_LIST&);
    mfdataINPUT_DATA_LIST& operator=(const mfdataINPUT_DATA_LIST&);
};

#endif /*!mfdataINPUT_DATA_LIST_H*/

/*___oOo___*/
