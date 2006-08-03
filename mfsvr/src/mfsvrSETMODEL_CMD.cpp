/*
 * 
 * This file has been automatically generated
 * 
 * !!!!!!!!!!!  DO NOT MANUALLY EDIT THIS FILE  !!!!!!!!!!!
 */
/**
 * \file
 * Generated for mfsvrSETMODEL_CMD class definition.
 */
 
 
/*
 * System Headers
 */
#include <stdio.h>
#include <iostream>
using namespace std;

/*
 * MCS Headers
 */
#include "log.h"

/*
 * Local Headers
 */
#include "cmd.h"
#include "mfsvrSETMODEL_CMD.h"
#include "mfsvrPrivate.h"

/*
 * Class constructor
 */

/**
 * Constructs a new class for an easier access for parameters of the
 * mfsvrSETMODEL_CMD COMMAND.
 */
 mfsvrSETMODEL_CMD::mfsvrSETMODEL_CMD(string name, string params):cmdCOMMAND(name, params,mfsvrSETMODEL_CDF_NAME)
{
    
}

/*
 * Class destructor
 */

/**
 * Class destructor
 */
mfsvrSETMODEL_CMD::~mfsvrSETMODEL_CMD()
{

}

/*
 * Public methods
 */

/**
 * Get the value of the parameter type.
 *
 * \param _type_ a pointer where to store the parameter.
 * 
 * \return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */ 
mcsCOMPL_STAT mfsvrSETMODEL_CMD::GetType(mcsINT32 *_type_)
{
    logExtDbg("mfsvrSETMODEL_CMD::GetType()");	
    return GetParamValue("type", _type_);
}


/*___oOo___*/
