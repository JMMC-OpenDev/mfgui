/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlPONCT.cpp,v 1.10 2006-07-04 08:37:27 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2006/06/27 08:45:00  lsauge
 * Introduce MAS unit in parameter descriptions
 *
 * Revision 1.8  2006/06/20 09:20:59  lsauge
 * Add methods
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
 * Revision 1.3  2006/02/03 14:32:10  lsauge
 * *** empty log message ***
 *
 * Revision 1.2  2006/01/23 10:58:35  mella
 * Remove already deleted part
 *
 * Revision 1.1  2005/09/08 14:05:47  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of mfmdlPONCT class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlPONCT.cpp,v 1.10 2006-07-04 08:37:27 lsauge Exp $";
/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

#define RAD_MAS .484813681109535993e-8

/*
 * Local Headers 
 */
#include "mfmdlPONCT.h"
#include "mfmdlPrivate.h"

/**
 * Class constructor
 */
mfmdlPONCT::mfmdlPONCT(string modelName,mcsLOGICAL polar) : 
mfmdlMODEL(modelName,polar)
{
    logTrace("mfmdlPONCT::mfmdlPONCT()");

    _modelType = mfmdlPONCT_MODEL_TYPE;

    _description.append("Ponctual object brigthness distribution");
}

/**
 * Class destructor
 */
mfmdlPONCT::~mfmdlPONCT()
{
     logTrace("mfmdlPONCT::~mfmdlPONCT()");
}

/*
 * Public methods
 */

/**
 * Compute complex visibilities for point-like source 
 *
 * In this case, the Fourier Transform is real and equals the unity for all
 * values of (u,v).  
 *
 * @param uvCoords  (U,V) TRUE coordinate
 * @param vis       complex visibilities
 * 
 * @return always mcsSUCCESS
 */

mcsCOMPL_STAT mfmdlPONCT::Eval(mfmdlUV_COORDS &uvCoords, mfmdlVIS &vis)
{
    // Get main parameters values
    mcsDOUBLE phi;
    mcsDOUBLE x0 = RAD_MAS * _relativeAbscissae.GetValue();
    mcsDOUBLE y0 = RAD_MAS * _relativeOrdinate.GetValue();
    mcsDOUBLE F0 = _intensityCoefficient.GetValue();

    // Loop other (u,v) coordinates
    for (mcsUINT32 i = 0; i < uvCoords.Size(); i++)
    {
        mcsDOUBLE u, v;

        // Get U, V coordinates
        if (uvCoords.Get(i, u, v) == mcsFAILURE)
        {
            return mcsFAILURE;
        }

        // Evaluate the phase
        phi = (-2.0 * M_PI)*(u*x0 + v*y0);

        /* Compute the corresponding complex visibility */
        mcsDOUBLE re, im;
        re = F0*cos(phi);
        im = F0*sin(phi);
        if (vis.Set(i, re, im)  == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    } 

    /* Computation are done yet */
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
