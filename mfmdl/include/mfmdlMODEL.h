#ifndef mfmdlMODEL_H
#define mfmdlMODEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlMODEL.h,v 1.10 2006-06-29 13:00:09 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2006/06/29 12:48:33  lsauge
 * add polar form methods and members
 *
 * Revision 1.8  2006/06/27 08:40:58  lsauge
 * Add uniform disk type (enum array)
 * add GetDescription() method
 *
 * Revision 1.7  2006/06/20 09:38:52  lsauge
 * Fix bad argument declaration in GetParameters
 *
 * Revision 1.6  2006/06/20 09:25:15  lsauge
 * Implementation of general methods
 *
 * Revision 1.5  2006/03/31 08:16:47  gzins
 * Updated API
 *
 * Revision 1.4  2006/03/01 14:08:21  lsauge
 * Suppress keyword mjd, time and waves from the declaration of Eval() function
 *
 * Revision 1.3  2006/02/06 09:52:54  lsauge
 * Added model's type description (protected member and corresponding enum array type)
 *
 * Revision 1.2  2006/02/03 09:41:49  lsauge
 * Change type of the result varaible (return bythe Eval method) from mcsDOUBLE to mcsCOMPLEX
 *
 * Revision 1.1  2005/09/08 14:06:11  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of mfmdlMODEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

#include <vector>

#include "mfmdlUV_COORDS.h"
#include "mfmdlVIS.h"
#include "mfmdlPARAMETER_LIST.h"


/**
 * Enum array describing model's type 
 */
typedef enum
{
    mfmdlUNKNOWN_MODEL_TYPE = -2,    /* unknown type */
    mfmdlUSER_MODEL_TYPE,            /* external model, supplied by user */
    mfmdlCOMPOSED_MODEL_TYPE,        /* composite model (model container) */
    mfmdlPONCT_MODEL_TYPE,           /* point source model */
    mfmdlUNIFORM_DISK_MODEL_TYPE     /* uniform disk model */
} mfmdlMODEL_TYPE;


/*
 * Class declaration
 */

/**
 * An mfmdlMODEL is the main object used for model fitting. User can use it to
 * build a composed model with mfmdlCOMPOSED_MODEL objects. 
 *  
 * @todo in the futur place XY relative material into a derived class MODEL_XY
 * 
 */
class mfmdlMODEL
{

public:
    // Class constructor
    mfmdlMODEL(string name, mcsLOGICAL polar=mcsFALSE);

    // Class destructor
    virtual ~mfmdlMODEL();

    virtual const char *GetName();
    virtual const char *GetDescription();

    // Methods relative to model parameters 
    virtual mcsCOMPL_STAT GetParameter(string name, mfmdlPARAMETER **param);
    // Methods relative to model parameters 
    virtual mcsCOMPL_STAT GetParameters(mfmdlPARAMETER_LIST *paramList,
                                        mcsLOGICAL clear=mcsTRUE);

    // Main evaluation method for a whole set of uv-coverage position
    virtual mcsCOMPL_STAT mfmdlMODEL::Eval( mfmdlUV_COORDS &uvCoords,
                                            mfmdlVIS       &visResults);

    // Methods relative to reference coordinates
    virtual mcsCOMPL_STAT SetRefAbscissae(mcsDOUBLE x);
    virtual mcsDOUBLE     GetRefAbscissae();
    virtual mcsCOMPL_STAT SetRefOrdinate(mcsDOUBLE y);
    virtual mcsDOUBLE     GetRefOrdinate();
    virtual mcsCOMPL_STAT SetRefPosition(mcsDOUBLE x, mcsDOUBLE y);

    // Methods associated to the Polar coordinate management
    virtual mcsLOGICAL    IsPolarEnabled();
    virtual mcsCOMPL_STAT SetPolar(mcsLOGICAL isEnabled=mcsTRUE);

    // Method relative to the model's type 
    mfmdlMODEL_TYPE GetModelType();
    
protected:
    /** contains list of parameters */
    mfmdlPARAMETER_LIST _paramList;
    
    /** Type of model */
    mfmdlMODEL_TYPE _modelType;

    /** model description */
    string _description;

    /** Minimum required Parameter members */
    mfmdlPARAMETER  _intensityCoefficient; 
    mfmdlPARAMETER  _relativeAbscissae; 
    mfmdlPARAMETER  _relativeOrdinate; 

    /** Polar form */
    mcsLOGICAL      _polarCoord;

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlMODEL(const mfmdlMODEL&);
    mfmdlMODEL& operator=(const mfmdlMODEL&);

    /** model name */
    string _name;
    
    /** Reference coordinates */
    mcsDOUBLE _refX;
    mcsDOUBLE _refY;
    
};

#endif /*!mfmdlMODEL_H*/

/*___oOo___*/
