/*
 * 
 * This file has been automatically generated
 * 
 * !!!!!!!!!!!  DO NOT MANUALLY EDIT THIS FILE  !!!!!!!!!!!
 */
#ifndef mfsvrSETMODEL_CMD_H
#define mfsvrSETMODEL_CMD_H

/**
 * \file
 * Generated for mfsvrSETMODEL_CMD class declaration.
 * This file has been automatically generated. If this file is missing in your
 * modArea, just type make all to regenerate.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * MCS Headers
 */
#include "cmd.h"

/*
 * Command name definition
 */
#define mfsvrSETMODEL_CMD_NAME "SETMODEL"

/*
 * Command definition file
 */
#define mfsvrSETMODEL_CDF_NAME "mfsvrSETMODEL.cdf"

/*
 * Class declaration
 */
        
    
/**
 * This class is intented to be used for a
 * reception of the SETMODEL command 
 */

class mfsvrSETMODEL_CMD: public cmdCOMMAND
{
public:
    mfsvrSETMODEL_CMD(string name, string params);
    virtual ~mfsvrSETMODEL_CMD();


    virtual mcsCOMPL_STAT GetType(mcsINT32 *_type_);

protected:

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
     mfsvrSETMODEL_CMD(const mfsvrSETMODEL_CMD&);
     mfsvrSETMODEL_CMD& operator=(const mfsvrSETMODEL_CMD&);

};

#endif /*!mfsvrSETMODEL_CMD_H*/

/*___oOo___*/
