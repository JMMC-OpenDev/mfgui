/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengLM_ENGINE.cpp,v 1.5 2006-08-02 08:09:53 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/06/27 08:53:06  lsauge
 * Major release : implementation of pending methods
 * other minor changes
 *
 * Revision 1.2  2006/03/31 08:08:23  gzins
 * Updated Init method; used mfitFIT_ENGINE::Init
 *
 * Revision 1.1  2006/03/20 07:11:39  gzins
 * Added
 *
 * Revision 1.4  2006/03/02 10:39:52  lsauge
 * Perform some test improving robustness
 *
 * Revision 1.3  2006/03/01 14:50:58  lsauge
 * Update documentation
 *
 * Revision 1.2  2006/03/01 14:43:27  lsauge
 * Suppress unused variables
 *
 * Revision 1.1  2006/03/01 13:17:41  lsauge
 * First Release of encapsulation classes of the fit engine
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of fitengLM_ENGINE class.
 */

static char *rcsId="@(#) $Id: fitengLM_ENGINE.cpp,v 1.5 2006-08-02 08:09:53 lsauge Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

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

/*
 * Local Headers 
 */
#include "fitengLM_ENGINE.h"
#include "fitengPrivate.h"
#include "fitengErrors.h"

/*!
  @def SEM usefull macros (debugging) 
  */
#define SEM printf("SEMAPHORE %s %d\n",__FILE__,__LINE__)

/**
 * Class constructor
 */
fitengLM_ENGINE::fitengLM_ENGINE() : fitengFIT_ENGINE() 
{
    
    // Set workspace reference to NULL
    // (corresponding to an unitialized status)
    _workingSpace = NULL ;
   
    // Set the different engine's paremeters to
    // their default value
    _fTol = 1e-8;
    _xTol = 1e-8;
    _gTol = 1e-8; 
    _maxNbOfFunctionEvaluation = 200;
    _LMFactor = 100.0;
   
    // Parameters and residues
    _parameterList = NULL;
    _residueList   = NULL;

    // Set array reference to NULL 
    _scaleFactors  = NULL; 
    _lowerBounds   = NULL; 
    _upperBounds   = NULL; 
    _fitFlags      = NULL;

    // Set the description string and versioning number
    _description.append("LM_with_trust_regions");
    _versionMajor = 0 ;
    _versionMinor = 1 ;

    /*  Set Stage level
        Note: -2 stage level is not defined in the oplm.h file. It correpsonds
        internaly to this class to a unitialized value.
    */
    _stage = -2 ; 
}

/**
 * Class destructor
 */
fitengLM_ENGINE::~fitengLM_ENGINE()
{
    logTrace("fitengLM_ENGINE::~fitengLM_ENGINE");
   
    // Free working space
    if (_workingSpace != NULL)
    {
        oplmFreeWorkspace(_workingSpace);
    }
    
    Free(_scaleFactors);
    Free(_lowerBounds);
    Free(_upperBounds);
    Free(_fitFlags);
}

/*
 * Public methods
 */

/**
 * Initialize the Levenberg-Marquardt engine. 
 *
 * @param parameterList list of parameters to fit
 * @param residueList   list of residues
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */ 
mcsCOMPL_STAT fitengLM_ENGINE::Init(fitengPARAMETER_LIST *parameterList,
                                    fitengRESIDUE_LIST   *residueList)
{
    logTrace("fitengLM_ENGINE::Init()");
    
    // Init generic engine
    if (fitengFIT_ENGINE::Init(parameterList, residueList) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Free working space
    if (_workingSpace != NULL)
    {
        oplmFreeWorkspace(_workingSpace);
        _workingSpace = NULL;
    }

    // Free other array 
    Free(_scaleFactors);
    Free(_lowerBounds);
    Free(_upperBounds);
    Free(_fitFlags);

    // Allocate memory
    mcsUINT32 nbOfParams = parameterList->Size();
    mcsUINT32 arraySize = nbOfParams * sizeof(mcsDOUBLE);
    _scaleFactors = (mcsDOUBLE*)calloc(nbOfParams, sizeof(mcsDOUBLE));    
    if (_scaleFactors == NULL)
    {
        errAdd(fitengERR_ALLOC_MEM, arraySize);
        return(mcsFAILURE);
    }
    _lowerBounds = (mcsDOUBLE*)calloc(nbOfParams, sizeof(mcsDOUBLE));    
    if (_lowerBounds == NULL)
    {
        errAdd(fitengERR_ALLOC_MEM, arraySize);
        return(mcsFAILURE);
    }
    _upperBounds = (mcsDOUBLE*)calloc(nbOfParams, sizeof(mcsDOUBLE));    
    if (_upperBounds == NULL)
    {
        errAdd(fitengERR_ALLOC_MEM, arraySize);
        return(mcsFAILURE);
    }

    arraySize = nbOfParams * sizeof(mcsINT32);
    _fitFlags = (mcsINT32*)calloc(nbOfParams, sizeof(mcsINT32));    
    if (_fitFlags == NULL)
    {
        errAdd(fitengERR_ALLOC_MEM, arraySize);
        return(mcsFAILURE);
    }

    // Set bounds and scale for parameters
    for (mcsUINT32 index=0; index<nbOfParams; index++)
    {
        // Get next parameter
        fitengPARAMETER *param;
        param = parameterList->GetNextParameter((mcsLOGICAL)(index==0));
        if (param == NULL)
        {
            return mcsFAILURE;
        }

        // Upper bound
        if (param->HasMaxValue() == mcsTRUE)
        {
            _upperBounds[index] = param->GetMaxValue();
        }
        else
        {
            _upperBounds[index] = fitengPLUS_INFINITY;
        }

        // Lower bound
        if (param->HasMinValue() == mcsTRUE)
        {
            _lowerBounds[index] = param->GetMinValue();
        }
        else
        {
            _lowerBounds[index] = fitengMINUS_INFINITY;
        }

        // Scale factor 
        _scaleFactors[index] = param->GetScale();
       
        // Free or fixed parameter ? 
        if (param->HasFixedValue() == mcsTRUE)
        {
            _fitFlags[index] = mcsFALSE;
        }
        else
        {
            _fitFlags[index] = mcsTRUE;
        }
    }

    // Instantiate the new workspace
    _workingSpace = oplmNewWorkspace(_residueList->Size(),
                                     _parameterList->Size(), 
                                     _fTol, _xTol, _gTol, 
                                     _maxNbOfFunctionEvaluation, 
                                     _LMFactor,
                                     // WARNING: hard implementation 
                                     _scaleFactors,
                                     _lowerBounds,
                                     _upperBounds,
                                     (const size_t*)_fitFlags);
    if (_workingSpace == NULL)
    {
        return mcsFAILURE;
    }

    // The workspace is now initialized ... ready for fitting !

    return(mcsSUCCESS);
}


/**
 *  Get the next fitting step using the Lm-fit engine
 *
 *  @return return the stage value as defined 
 */
mcsCOMPL_STAT fitengLM_ENGINE::DoNextStep() 
{
    logTrace("fitengLM_ENGINE::GetFitNextStep()");
    
    // Be sure that engine is initialized 
    if(_workingSpace == NULL)
    {
        errAdd(fitengERR_NOT_INITIALISED);
        return mcsFAILURE;
    }

    // Perform one step
    _stage = 0;
    _stage = oplmDifStep(_workingSpace, 
                        _parameterList->GetArray(), 
                        _residueList->GetArray());
    // Save the stage level info
    // Perform the reauired operation required by the engine
    if ((_stage == oplmSTAGE_ERROR) || (_stage == oplmBAD_STAGE)) 
    {    
        logError("op_lm_dif returns an error [stage=%d,info=%d]",
                 _stage, _workingSpace->info);
        return mcsFAILURE;
    }

    // Set new parameter values
    _parameterList->SetParametersFromArray();

    // Exit
    return mcsSUCCESS;
}

/*!
 * Return the stage value, normally set during the call of the DoNextStep()
 * method
 *
 * @return  last value stage
 * */
mcsINT32      
fitengLM_ENGINE::GetStage()
{
    return _stage;
}

/*!
 * Return the info value stored in the current workingspace 
 * (if defined, of course)
 *
 * @return info parameter
 * @note   info is an integer output variable. if the user has
           terminated execution, info is set to the (negative)
           value of iflag. see description of fcn. otherwise,
           info is set as follows.
	   - info = 0  improper input parameters.
	   - info = 1  both actual and predicted relative reductions
		     in the sum of squares are at most fTol.
	   - info = 2  relative error between two consecutive iterates is at
	   most xTol.
	   - info = 3  conditions for info = 1 and info = 2 both hold.
	   - info = 4  the cosine of the angle between fvec and any
		     column of the jacobian is at most gTol in absolute value.
	   - info = 5  number of calls to fcn has reached or exceeded maxfev.
	   - info = 6  fTol is too small. no further reduction in the sum of
	   squares is possible.
	   - info = 7  xTol is too small. no further improvement in the
	   approximate solution x is possible.
	   - info = 8  gTol is too small. fvec is orthogonal to the columns of
	   the jacobian to machine precision.
 */
mcsINT32      
fitengLM_ENGINE::GetInfo()
{
    if(_workingSpace != NULL)
    {
        return _workingSpace->info;
    }
    else
    {
        return -1 ;
    }
}


/*!
	Set tolerance values of the LM fitting engine
	
	@param 	fTol 	fTol is a nonnegative input variable. termination
	occurs when both the actual and predicted relative reductions in the
	sum of squares are at most fTol.  therefore, fTol measures the
	relative error desired in the sum of squares.

	@param 	xTol	xTol is a nonnegative input variable. termination
	occurs when the relative error between two consecutive iterates is at
	most xTol.  therefore, xTol measures the relative error desired in the
	approximate solution.

	@param	gTol 	gTol is a nonnegative input variable. termination
	occurs when the cosine of the angle between fvec and any column of the
	jacobian is at most gTol in absolute value. therefore, gTol measures
	the orthogonality desired between the function vector and the columns
	of the jacobian.

	@return	always mcsSUCCESS

	@sa GetTolerance(mcsDOUBLE &fTol,mcsDOUBLE &xTol,mcsDOUBLE &gTol)

*/
mcsCOMPL_STAT 
fitengLM_ENGINE::SetTolerance(mcsDOUBLE fTol,mcsDOUBLE xTol,mcsDOUBLE gTol)
{
	_fTol = fTol ;
	_xTol = xTol ;
	_gTol = gTol ;

	return mcsSUCCESS;
}

/*!
	Get tolerance values of the LM fitting engine
	
	@param 	fTol 	reference to fTol value 
	@param 	xTol	reference to xTol value
	@param	gTol 	reference to gTol value
	@note	see SetTolerance(mcsDOUBLE fTol,mcsDOUBLE xTol,mcsDOUBLE gTol)
	for a full description of the paramter signification above.

	@return	always mcsSUCCESS

	@sa SetTolerance(mcsDOUBLE fTol,mcsDOUBLE xTol,mcsDOUBLE gTol)

*/
mcsCOMPL_STAT 
fitengLM_ENGINE::GetTolerance(mcsDOUBLE &fTol,mcsDOUBLE &xTol,mcsDOUBLE &gTol)
{
	fTol = _fTol ;
	xTol = _xTol ;
	gTol = _gTol ;

	return mcsSUCCESS;
}

/*!
  	Get the maximum number of function evaluations.
	
	@return	the result is a positive integer input variable.  termination
	occurs when the number of calls to fcn is at least the returning
	unsigned integer by the end of an iteration.
*/
mcsUINT16 
fitengLM_ENGINE::GetMaxFEval()
{
	return _maxNbOfFunctionEvaluation;
}

/*!
  	Set the maximum number of function evaluations.
	
	@param	maxFEv is a positive integer input variable.  termination
	occurs when the number of calls to fcn is at least maxFEv by the end
	of an iteration.

	@result always return mcsSUCCESS
*/
mcsCOMPL_STAT
fitengLM_ENGINE::SetMaxFEval(mcsUINT16 maxFEv)
{
	_maxNbOfFunctionEvaluation = maxFEv;
	return mcsSUCCESS;
}

/*!
	Get the value of the LM factor
	
	@return	value of the LM factor. The factor is a positive input
	variable used in determining the initial step bound. this bound is set
	to the product of factor and the euclidean norm of diag*x if nonzero,
	or else to factor itself. in most cases factor should lie in the
	interval (.1,100.). 100. is a generally recommended value.
*/
mcsDOUBLE fitengLM_ENGINE::GetLMFactor()
{
	return _LMFactor;
}

/*!
	Set the value of the LM factor
	
	@param 	lmFactor 	value of the LM factor. The factor is a
	positive input variable used in determining the initial step bound.
	this bound is set to the product of factor and the euclidean norm of
	diag*x if nonzero, or else to factor itself. in most cases factor
	should lie in the interval (.1,100.). 100. is a generally recommended
	value.
	
	@return	always mcsSUCCESS

*/
mcsCOMPL_STAT 
fitengLM_ENGINE::SetLMFactor(mcsDOUBLE lmFactor)
{
	_LMFactor = lmFactor;	
	return mcsSUCCESS;
}


/*
 * Private methods
 */

/**
 * Safe free method
 * 
 * This method frees previously allocated memory in a safe way; i.e. checks the
 * pointer is non-null, and sets it to null when released. 
 */
void fitengLM_ENGINE::Free(void *ptr)
{
    if (ptr != NULL)
    {
        free(ptr);
        ptr = NULL;
    }
}


/*___oOo___*/
