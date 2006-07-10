/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlMODEL.cpp,v 1.12 2006-06-29 13:00:00 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2006/06/29 12:48:26  lsauge
 * add polar form methods and members
 *
 * Revision 1.10  2006/06/27 08:44:18  lsauge
 * method GetDescription() implemented
 *
 * Revision 1.9  2006/06/20 09:41:47  lsauge
 * fic bad argument declaration of mdmdlPARAM
 *
 * Revision 1.8  2006/06/20 09:25:15  lsauge
 * Implementation of general methods
 *
 * Revision 1.7  2006/05/11 13:04:56  mella
 * Changed rcsId declaration to perform good gcc4 and gcc3 compilation
 *
 * Revision 1.6  2006/03/31 08:17:02  gzins
 * Updated API
 *
 * Revision 1.5  2006/03/01 13:30:51  lsauge
 * Supprees argument mjd, time and wave in the Eval() function description
 *
 * Revision 1.4  2006/02/06 09:53:15  lsauge
 * Added model's type description (protected member and corresponding enum array type)
 *
 * Revision 1.3  2006/02/03 09:40:32  lsauge
 * Change type of the result variable (return by Eval method) from mcsDOUBLE to mcsCOMPLEX
 *
 * Revision 1.2  2006/01/23 10:59:03  mella
 * Added general parameters
 *
 * Revision 1.1  2005/09/08 14:05:47  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of mfmdlMODEL class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlMODEL.cpp,v 1.12 2006-06-29 13:00:00 lsauge Exp $";
/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "mfmdlErrors.h"
#include "mfmdlMODEL.h"
#include "mfmdlPrivate.h"
#include "mfmdlErrors.h"

#include "mfmdlUV_COORDS.h"
#include "mfmdlVIS.h"

#include <mfmdlMODEL.h>

/**
 * Class constructor
 *
 * \param name name of model
 */
mfmdlMODEL::mfmdlMODEL(string name, mcsLOGICAL polar):
    _intensityCoefficient("intensityCoefficient", 1.0," "   ,0.0,1.0),
    _relativeAbscissae   ("relativeAbscissae"   , 0.0, "mas",-50.0,50.0),
    _relativeOrdinate    ("relativeOrdinate"    , 0.0, "mas",-50.0,50.0)
{
    logTrace("mfmdlMODEL::mfmdlMODEL()");
    _name = name;

    // Add parameters
    _paramList.AddAtTail(&_intensityCoefficient);
    _paramList.AddAtTail(&_relativeAbscissae);
    _paramList.AddAtTail(&_relativeOrdinate);
    
    // Reference coordinates
    _refX=0.0;
    _refY=0.0;
    
    // Set coordinate representation 
    _polarCoord = polar ;

    // Model is undefined at this step
    _modelType = mfmdlUNKNOWN_MODEL_TYPE;
    
    _description.append("Simple base model");
}

/**
 * Class destructor
 */
mfmdlMODEL::~mfmdlMODEL()
{
    logTrace("mfmdlMODEL::~mfmdlMODEL()");
}

/*
 * Public methods
 */
/** 
 *  Return the model name 
 *
 *  \returns the name of the model
 */
const char *mfmdlMODEL::GetName()
{
    logTrace("mfmdlMODEL::GetName()");
    if (_name.empty() )
    {
        return "NoName";
    }
    return _name.data();
}

/** 
 *  Return the model description 
 *
 *  \returns the description of the model
 */
const char *mfmdlMODEL::GetDescription()
{
    logTrace("mfmdlMODEL::GetDescription()");
    if (_description.empty() )
    {
        return "No description available";
    }
    return _description.data();
}

/**
 * Get the parameter corresponding to the given name.
 *
 * This method looks for the specified @em parameter in the list.
 *
 * @param name name of the parameter
 * 
 * @return mcsSUCCESS on successful completion or mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT mfmdlMODEL::GetParameter(string name, mfmdlPARAMETER **param)
{
    logExtDbg("mfmdlMODEL::GetParameter()"); 
    
    *param = _paramList.GetParameter(name);
    if (*param == NULL)
    {
        errAdd(mfmdlERR_WRONG_PARAMETER_NAME, name.c_str());
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/** 
 * Get the list of the parameters of the model
 *
 * This method adds its parameters into the given list.
 *
 * @param paramList list int which model's parameters are added
 * @param clear indicate if list has to be cleared first.
 *
 * @returns always mcsSUCCESS  
 */
mcsCOMPL_STAT mfmdlMODEL::GetParameters(mfmdlPARAMETER_LIST *paramList,
                                        mcsLOGICAL clear)
{
    logTrace("mfmdlMODEL::GetParameters()");

    // Clear list is requested
    if (clear == mcsTRUE)
    {
        paramList->Clear();
    }

    // Then add all parameters of the model
    for (unsigned int el = 0; el < _paramList.Size(); el++)
    {
        paramList->AddAtTail(_paramList.GetNextParameter((mcsLOGICAL)(el==0)));
    }

    return mcsSUCCESS; 
}

/** 
 * Set the reference abscissae
 * 
 * @param x reference abscissae
 *
 * @returns always mcsSUCCESS 
 */
mcsCOMPL_STAT mfmdlMODEL::SetRefAbscissae(mcsDOUBLE x)
{
    logTrace("mfmdlMODEL::SetRefAbscissae()");

    _refX = x;
    return mcsSUCCESS;
}

/** 
 * Get the reference abscissae
 *
 * @returns the reference abscissae value
 */
mcsDOUBLE mfmdlMODEL::GetRefAbscissae()
{
    logTrace("mfmdlMODEL::GetRefAbscissae()");
    return _refX;
}

/** 
 * Set the reference ordinate
 *
 * @param y reference ordinate
 *
 * @returns always mcsSUCCESS 
 */
mcsCOMPL_STAT mfmdlMODEL::SetRefOrdinate(mcsDOUBLE y)
{
    logTrace("mfmdlMODEL::IsWorkingInXY()");

    _refY = y;
    return mcsSUCCESS;
}

/** 
 * Get the reference ordinate
 *
 * @returns the reference ordinate value
 */
mcsDOUBLE mfmdlMODEL::GetRefOrdinate()
{
    logTrace("mfmdlMODEL::GetRefOrdinate()");

    return _refY;
}
    
/** 
 * Set the reference coordinates
 *
 * @param x reference abscissae
 * @param y reference ordinate
 * 
 * @returns the reference ordinate value
 */
mcsCOMPL_STAT mfmdlMODEL::SetRefPosition(mcsDOUBLE x, mcsDOUBLE y)
{
    logTrace("mfmdlMODEL::SetRefPosition()");
    
    if (SetRefAbscissae(x) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    return SetRefOrdinate(y);
}

/** 
 * @return the model type.
 */
mfmdlMODEL_TYPE mfmdlMODEL::GetModelType()
{
    logTrace("mfmdlMODEL::GetModelType()");

    return _modelType;
}
    
/**
 * Evaluation method ; return a visibility array where each element 
 * is set to (0,0)
 *
 * @param uvCoords      uv coordinates (input paramater)  
 * @param vis           complex visibility (output parameter)
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT mfmdlMODEL::Eval(mfmdlUV_COORDS &uvCoords, 
                               mfmdlVIS       &visResults)
{
      mcsCOMPL_STAT status = mcsSUCCESS;
      
      // first, be sure that both the uvCoords and the visResult exist
      if((&uvCoords!=NULL)&&(&visResults!=NULL))
      {
        // loop over the uvCoords elements
        mcsUINT32 index;
        for(index=0 ; index<uvCoords.Size() ; index++)
        {
            // set the complex visilibilities resulting array 
            status = visResults.Set(index,0.0,0.0);
            if(status == mcsFAILURE)
            {
                    // Report/Register failure 
            }
        }       
      }
      return status;
}

/*!
 *  Ask to the model if the coordinate representation is polar or cartesian
 *  @return return mcsTRUE if the represnentation is polar or mcsFALSE if it is
 *  cartesian.
 * */
mcsLOGICAL mfmdlMODEL::IsPolarEnabled()
{ 
    logTrace("mfmdlMODEL::IsPolarEnbaled()");
    return  _polarCoord;
}

/*!
 * Set the model representation
 * @param   isEnabled   mcsTRUE for the polar form or mcsFALSE the the catresian
 * one (defualt is mcsTRUE).
 * @return  alwaus mcsSUCCESS
 * */
mcsCOMPL_STAT mfmdlMODEL::SetPolar(mcsLOGICAL isEnabled)
{ 
    logTrace("mfmdlMODEL::GetDescription()");
    _polarCoord = isEnabled ;
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
