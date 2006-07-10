/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlUNIFORM_DISK.cpp,v 1.2 2006-07-04 08:37:27 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/06/27 08:45:39  lsauge
 * First implementation and release
 *
 * 
 ******************************************************************************/

/**
 * \file
 *  Definition of mfmdlUNIFORM_DISK class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlUNIFORM_DISK.cpp,v 1.2 2006-07-04 08:37:27 lsauge Exp $";
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
#include "mfmdlUNIFORM_DISK.h"
#include "mfmdlPrivate.h"

/**
 * Class constructor
 */
mfmdlUNIFORM_DISK::mfmdlUNIFORM_DISK(string modelName, mcsLOGICAL polar) : 
mfmdlMODEL(modelName,polar),
    _sourceDiameter ("sourceDiameter", 0.0, "mas",0.0,5.0)
{
    logTrace("mfmdlUNIFORM_DISK::mfmdlUNIFORM_DISK()");
    
    // Add parameters
    _paramList.AddAtTail(&_sourceDiameter);

    _modelType = mfmdlUNIFORM_DISK_MODEL_TYPE;

    _description.append("Uniform disk (resolved source)");
}

/**
 * Class destructor
 */
mfmdlUNIFORM_DISK::~mfmdlUNIFORM_DISK()
{
     logTrace("mfmdlUNIFORM_DISK::~mfmdlUNIFORM_DISK()");
}

/*
 * Public methods
 */

/**
 * Compute complex visibilities for an uniform disk model.
 *
 * @param uvCoords  (U,V) TRUE coordinate
 * @param vis       complex visibilities
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT mfmdlUNIFORM_DISK::Eval(mfmdlUV_COORDS &uvCoords, mfmdlVIS &vis)
{
    // Get main parameters values
    mcsDOUBLE phi;
    mcsDOUBLE x0    = RAD_MAS * _relativeAbscissae.GetValue();
    mcsDOUBLE y0    = RAD_MAS * _relativeOrdinate.GetValue();
    mcsDOUBLE theta = RAD_MAS * _sourceDiameter.GetValue();
    mcsDOUBLE F0    = _intensityCoefficient.GetValue();

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

        // Evaluate "Form Factor" of the uniform disk (regardinf unresolved/
        // ponctual source)
        mcsDOUBLE arg       = M_PI * theta * sqrt(u*u + v*v) ; 
        mcsDOUBLE formFactor= F0 ; 
        if(arg != 0.0 )
        {
            // j1 is the ususal first order Bessel function of the first kind
            // This function is standardly iomplemented in math.h
            formFactor *= 2.0 * j1(arg)/arg ;
        }
            
        /* Compute the corresponding complex visibility */
        mcsDOUBLE re, im;
        re = formFactor * cos(phi);
        im = formFactor * sin(phi);
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
