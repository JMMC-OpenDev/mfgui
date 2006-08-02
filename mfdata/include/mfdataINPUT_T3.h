#ifndef mfdataINPUT_T3_H
#define mfdataINPUT_T3_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_T3.h,v 1.2 2006-07-10 12:15:28 lsauge Exp $
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/06/27 12:05:36  lsauge
 * First import
 *
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of mfdataINPUT_T3 class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

#include "mfdataINPUT_DATA.h"

#include "mfmdl.h"
#include "mfmdlUV_COORDS.h"

/*
 * Class declaration
 */

/**
 * This class store phase cloture informations.
 * @sa mfdataINPUT_DATA
 * 
 */
class mfdataINPUT_T3 : public mfdataINPUT_DATA
{

public:
    // Class constructor
    mfdataINPUT_T3();

    // Class destructor
    virtual ~mfdataINPUT_T3();

    mcsCOMPL_STAT SetT3Refs(mcsDOUBLE * u1Coord,
                            mcsDOUBLE * v1Coord,
                            mcsDOUBLE * u2Coord,
                            mcsDOUBLE * v2Coord,
                            mcsDOUBLE * t3Amp,
                            mcsDOUBLE * t3AmpErr,
                            mcsDOUBLE * t3Phi,
                            mcsDOUBLE * t3PhiErr);

    virtual mcsCOMPL_STAT GetT3Amp(mcsDOUBLE ** t3Amp);
    virtual mcsCOMPL_STAT GetT3AmpErr(mcsDOUBLE ** t3AmpErr);
    virtual mcsCOMPL_STAT GetT3Phi(mcsDOUBLE ** t3Phi);
    virtual mcsCOMPL_STAT GetT3PhiErr(mcsDOUBLE ** t3PhiErr);
    virtual mcsCOMPL_STAT GetU1Coord(mcsDOUBLE ** u1Coord);
    virtual mcsCOMPL_STAT GetV1Coord(mcsDOUBLE ** v1Coord);
    virtual mcsCOMPL_STAT GetU2Coord(mcsDOUBLE ** u2Coord);
    virtual mcsCOMPL_STAT GetV2Coord(mcsDOUBLE ** v2Coord);

    virtual mcsCOMPL_STAT mfdataINPUT_T3::GetUVCoords(mfmdlUV_COORDS &uvCoordsAB,
                                                      mfmdlUV_COORDS &uvCoordsBC);

protected:
    mcsDOUBLE * _u1Coord;               /**< Array1D of u1Coord */
    mcsDOUBLE * _v1Coord;               /**< Array1D of v1Coord */
    mcsDOUBLE * _u2Coord;               /**< Array1D of u2Coord */
    mcsDOUBLE * _v2Coord;               /**< Array1D of v2Coord */
    mcsDOUBLE * _t3Amp;                 /**< Array2D of t3Amp  */
    mcsDOUBLE * _t3AmpErr;              /**< Array2D of t3AmpErr  */
    mcsDOUBLE * _t3Phi;                 /**< Array2D of t3Phi  */
    mcsDOUBLE * _t3PhiErr;              /**< Array2D of t3PhiErr  */

    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfdataINPUT_T3(const mfdataINPUT_T3&);
    mfdataINPUT_T3& operator=(const mfdataINPUT_T3&);    
};

#endif /*!mfdataINPUT_T3_H*/

/*___oOo___*/
