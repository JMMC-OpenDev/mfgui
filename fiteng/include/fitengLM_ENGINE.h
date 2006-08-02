#ifndef fitengLM_ENGINE_H
#define fitengLM_ENGINE_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengLM_ENGINE.h,v 1.2 2006-06-27 08:54:14 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/20 07:12:11  gzins
 * Added
 *
 * Revision 1.2  2006/03/01 14:21:17  gzins
 * Used fiteng module
 *
 * Revision 1.1  2006/03/01 13:19:34  lsauge
 * Creation and First release
 *
 ******************************************************************************/

/**
 * @file
 * Declaration of fitengLM_ENGINE class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

/*
 * Local header
 */
#include "oplm.h"
#include "fitengFIT_ENGINE.h"

/*
 * Class declaration
 */

/**
 * Fit engine using Bounced Levenberg-Marquardt optimization method.
 */
class fitengLM_ENGINE : public fitengFIT_ENGINE
{

public:
    // Class constructor
    fitengLM_ENGINE();

    // Class destructor
    virtual ~fitengLM_ENGINE();
    
    // Init fitting-engine 
    virtual mcsCOMPL_STAT Init(fitengPARAMETER_LIST *parameterList,
                               fitengRESIDUE_LIST   *residueList);
 
    // Perform a simple fitting step 
    virtual mcsCOMPL_STAT DoNextStep();

    // Get the last stage value returned by the engine
    virtual mcsINT32      GetStage();
    // Get the last info value stored in the workingspace environnement
    virtual mcsINT32      GetInfo();

    // Access method to the fit engine tolerance values
    virtual mcsCOMPL_STAT 	GetTolerance(mcsDOUBLE &fTol,
		   			     mcsDOUBLE &xTol,
					     mcsDOUBLE &gTol
					     );
    virtual mcsCOMPL_STAT 	SetTolerance(mcsDOUBLE fTol,
		   			     mcsDOUBLE xTol,
					     mcsDOUBLE gTol
					     );
    // Access method to the fit engine maximum number of function evlauation 
    virtual mcsUINT16 		GetMaxFEval();
    virtual mcsCOMPL_STAT 	SetMaxFEval(mcsUINT16 nFunc);
    // Access method to the fit engine LM factor
    virtual mcsDOUBLE 		GetLMFactor();
    virtual mcsCOMPL_STAT 	SetLMFactor(mcsDOUBLE lmfactor);


private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    fitengLM_ENGINE(const fitengLM_ENGINE&);
    fitengLM_ENGINE& operator=(const fitengLM_ENGINE&);

    // Smart free
    void Free(void *ptr);

    // Working space used by fit engine.
    oplmWORKSPACE *_workingSpace;    

    // Members 
    mcsDOUBLE   _fTol;
    mcsDOUBLE   _xTol;
    mcsDOUBLE   _gTol;
    mcsUINT16   _maxNbOfFunctionEvaluation;
    mcsDOUBLE   _LMFactor;
    mcsINT32    _stage;

    mcsDOUBLE   *_scaleFactors;
    mcsDOUBLE   *_lowerBounds;
    mcsDOUBLE   *_upperBounds;
    mcsINT32    *_fitFlags;

};

#endif /*!fitengLM_ENGINE_H*/

/*___oOo___*/
