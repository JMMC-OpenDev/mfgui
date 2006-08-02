#ifndef mfdataINPUT_VIS2_H
#define mfdataINPUT_VIS2_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_VIS2.h,v 1.1 2006-06-27 12:05:36 lsauge Exp $
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of mfdataINPUT_VIS2 class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

#include "mfdataINPUT_DATA.h"
#include "mfdataINPUT_DATA_LIST.h"

#include "oidata.h"
#include "mfmdl.h"
#include "mfmdlUV_COORDS.h"
/*
 * Class declaration
 */

/**
 * This class stores squared visibility informations.
 * @sa mfdataINPUT_DATA
 * 
 */
class mfdataINPUT_VIS2 : public mfdataINPUT_DATA
{

public:
    // Class constructor
    mfdataINPUT_VIS2();

    // Class destructor
    virtual ~mfdataINPUT_VIS2();

    mcsCOMPL_STAT SetVis2Refs(mcsDOUBLE * uCoord,
                              mcsDOUBLE * vCoord,
                              mcsDOUBLE * vis2Data,
                              mcsDOUBLE * vis2Err);
   
    // Get the refercence to the internal array structure
    // MUST BE AVOIDED AND REPLACE IN A NEAR FUTURE !!!
    virtual mcsCOMPL_STAT GetUCoord  (mcsDOUBLE ** uCoord);
    virtual mcsCOMPL_STAT GetVCoord  (mcsDOUBLE ** vCoord);
    virtual mcsCOMPL_STAT GetVis2Data(mcsDOUBLE ** vis2Data);
    virtual mcsCOMPL_STAT GetVis2Err (mcsDOUBLE ** vis2Err);
    
    virtual mcsCOMPL_STAT GetUVCoords(mfmdlUV_COORDS &uvCoords);
    
    // indexed access 
    virtual mcsDOUBLE GetUCoord  (mcsUINT16 index);
    virtual mcsDOUBLE GetVCoord  (mcsUINT16 index);
    virtual mcsDOUBLE GetVis2Data(mcsUINT16 index);
    virtual mcsDOUBLE GetVis2Err (mcsUINT16 index);

protected:
    mcsDOUBLE * _uCoord;               /**< Array1D of uCoord */
    mcsDOUBLE * _vCoord;               /**< Array1D of vcoord */
    mcsDOUBLE * _vis2Data;             /**< Array2D of vis2Data */ 
    mcsDOUBLE * _vis2Err;              /**< Array2D of vis2err */ 

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfdataINPUT_VIS2(const mfdataINPUT_VIS2&);
    mfdataINPUT_VIS2& operator=(const mfdataINPUT_VIS2&);
};

#endif /*!mfdataINPUT_VIS2_H*/

/*___oOo___*/
