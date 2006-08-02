/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengLM_ENGINETest.cpp,v 1.1 2006-05-31 14:14:58 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * This file is a simple test file for the class fitengLM_ENGINE
 * 
 * It internally generates set of "observed" data from a simple Normal or
 * Gaussian curve, adds noise and performs a fit on these data
 * All result could be dumped to a file using the command line argument
 * \c --dump. All intermediate fitting results are displayed by default, which
 * it leads \b TAT (Tool for Automated Testing) to fail. To avoid this problem,
 * use \c --tat command line argument (which is NOT the default).
 *
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: fitengLM_ENGINETest.cpp,v 1.1 2006-05-31 14:14:58 lsauge Exp $"; 

/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>

/**
 * @namespace std
 * Export standard iostream objects (cin, cout,...).
 */
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

// fiting engine facilities
#include "fiteng.h"
#include "fitengPrivate.h"

#include "fitengPARAMETER.h"
#include "fitengPARAMETER_LIST.h"
#include "fitengRESIDUE_LIST.h"

// random number generator
#include "rand.h"

/*!
 @def SEM a simple but very usefull macro for fast debugging
*/
#define SEM printf("SEMAPHORE %s %d\n",__FILE__,__LINE__)

/*
 * Function and routine prototypes
 */

mcsCOMPL_STAT TestDriver(int, char**);
mcsCOMPL_STAT SetValandMinMax(
        fitengPARAMETER* param,
        mcsDOUBLE value,
        mcsDOUBLE min,
        mcsDOUBLE max );
mcsDOUBLE BellCurve(mcsDOUBLE x, fitengPARAMETER_LIST &params );
mcsCOMPL_STAT TestDriver(int argc, char **argv);
 
/* 
 * Signal catching functions  
 */

/* 
 * Main
 */

int 
main(int argc, char *argv[])
{
    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Error handling if necessary
        
        // Exit from the application with FAILURE
        exit (EXIT_FAILURE);
    }

    // === call main test routine
    mcsCOMPL_STAT retStatus = TestDriver(argc,argv);
    // 

    // Close MCS services
    mcsExit();
    
    // Exit from the application with SUCCESS
    exit (EXIT_SUCCESS);
}



/* ===========================================================================
                            ROUTINES AND FUNCTION
   =========================================================================== */



/* =========================================================================== */
/*!
  Usefull macro which permits to set current, maximum and minimum values of 
  a parameter in one command
  
  @param    param   parameter
  @param    value   current value
  @param    min     minimum parameter value allowed
  @param    max     maximum parameter value allowed

  @return   mcsSUCCESS in case of successfull completion, mcsFAILURE
  otherwise.
*/
mcsCOMPL_STAT
SetValandMinMax(
        fitengPARAMETER* param,
        mcsDOUBLE value,
        mcsDOUBLE min,
        mcsDOUBLE max
        )
{
    if(param != NULL)
    {
        if(param->SetValue(value) == mcsFAILURE)
        {
            logError("Cannot set the parameter value");
            return mcsFAILURE;
        }
        if(param->SetMinValue(min) == mcsFAILURE)
        {
            logError("Cannot set the minimium parameter value");
            return mcsFAILURE;
        }
        if(param->SetMaxValue(max) == mcsFAILURE)
        {
            logError("Cannot set the maximum parameter value");
            return mcsFAILURE;
        }
        return mcsSUCCESS;
    }

    logError("Parameter address set to NULL");
    return mcsFAILURE;
}

/* =========================================================================== */
/*!
   Evaluate Bell curve value at given abcissa
   @param   x       abcissa value
   @param   params  list of paramaters of the Curve (see note below)
   @return  value
   @note    the parameter list must contain the following element
            - a amplitude
            - b peak position
            - c width (dispersion) of the peak
            Default value of these parameters are 0.0. If c is set to 0.0, the
            returned value is set to 0.0
*/
mcsDOUBLE
BellCurve(mcsDOUBLE x, fitengPARAMETER_LIST &params )
{
    // Get the parameter values
    fitengPARAMETER* paramBuffer = NULL;
    mcsDOUBLE a = 0.0;
    mcsDOUBLE b = 0.0;
    mcsDOUBLE c = 0.0;

    if((paramBuffer = params.GetParameter("a")) != NULL)
    {
        a = paramBuffer->GetValue();
    }
    
    if((paramBuffer = params.GetParameter("b")) != NULL)
    {
        b = paramBuffer->GetValue();
    }
    
    if((paramBuffer = params.GetParameter("c")) != NULL)
    {
        c = paramBuffer->GetValue();
    }
   
    // The dispersion of the peak must be defined ; otherwise, return 0.0
    if(c != 0)
    {
        mcsDOUBLE y   = (x-b)/c;
        mcsDOUBLE res = a*exp(-0.5*y*y);
        return res;
    }
    return 0.0;
}


/* =========================================================================== */
/*!
        Check if a string exists in an argument vector

        @param  argv    argument vector
        @param  string  string to find
        @return return mcsTRUE if the substring is found, mcsFALSE otherwise
*/
mcsLOGICAL
FindStringInArgv(
        char **argv,
        const char* string)
{
    // local declaration
    char *subString = NULL;
    mcsUINT16 idx=0;

    // loop other vector elements
    while((subString=argv[idx++]) != NULL)
    {
        if(strcmp(subString,string)==0)
            return mcsTRUE;
    }
    return mcsFALSE;
}


/* =========================================================================== */
/*!
  Compute factorial number of an integer value
  @param  n   argument 
  @return factorial 
  */
mcsUINT32
Factorial(mcsUINT32 n)
{
    if(n>0)
        return n*Factorial(n-1);
    return 1;
}

/* =========================================================================== */
/*!
  chi2 (\f$\chi^2\f$) of argument z and for n degrees of freedom.
  For simplicity, n must be an even integer ; the user must correctly set
  the dof accordingly. 

  @param  z   argument
  @param  n   degrees of freedom (dof)

  @return chi2 value

  @note The corresponding probability function is given by 
  \f[
  f(z,n) = 
  {
  z^{n/2-1}{\rm e}^{-z/2}
  \over
  2^{n/2}\Gamma(n/2)        
  },\ z\ge 0.
  \f]
*/
mcsDOUBLE
Chi2Distribution(mcsDOUBLE z,mcsUINT32 n)
{
    if(n%2!=0) 
        return 0;
    mcsUINT32 m = n/2; 
    mcsDOUBLE t1 = pow(z,(mcsDOUBLE)m-1.0)*exp(-0.5*z) ;
    mcsDOUBLE t2 = pow(2.0,(mcsDOUBLE)m)*((mcsDOUBLE)Factorial(m-1)) ;
    return t1/t2;
}

/* =========================================================================== */
/*!
  Compute the goodness-of-fit corresponding to a given \f$\chi^2\f$ with n of
  degrees of freedom
  @param  chi2  chi2 value (NOT REDUCED!)
  @param  dof   degrees of freedom (dof)
    
  @return p-value

  @note p-value is evaluated as follow :
  \f[
    p=1-\int_0^{\chi^2}{\rm d}z\,f(z,n_{\rm dof})
  \f]
  with 
    \f[
        f(z,n) = 
        {
        z^{n/2-1}{\rm e}^{-z/2}
        \over
        2^{n/2}\Gamma(n/2)        
        },\ z\ge 0.
    \f]

*/
mcsDOUBLE
GoodnessOfFit(mcsDOUBLE chi2, mcsUINT32 dof)
{
    mcsDOUBLE sum  = 0.0 ;
    mcsUINT16 size = 10000 ;
    mcsDOUBLE dz   = chi2/((mcsDOUBLE)size-1) ;
    for(mcsUINT16 step = 1 ; step<(size-1) ; step++)
    {
        sum += Chi2Distribution(dz*(mcsDOUBLE)step,dof);
    }
    return 1.0-(sum+0.5*Chi2Distribution(chi2,dof))*dz;
}

/* =========================================================================== */
/*!
    Main routine performing the test
    @param  argc    number of element of the argument vector
    @param  argv    argument vector
    @return mcsSUCCESS on successfull completion, mcsFAILURE otherwise
*/
mcsCOMPL_STAT
TestDriver(int argc, char **argv)
{
    // is the test code running in tat mode ?
    mcsLOGICAL runIntoTat = FindStringInArgv(argv,"--tat");

    // defien a new parameter list
    fitengPARAMETER_LIST *params  = new fitengPARAMETER_LIST();

    fitengPARAMETER *p1  = new fitengPARAMETER("a");
    fitengPARAMETER *p2  = new fitengPARAMETER("b");
    fitengPARAMETER *p3  = new fitengPARAMETER("c");
    // set parameter values
    p1->SetValue(1.0); 
    p2->SetValue(0.0); 
    p3->SetValue(1.0); 
    // add to the list
    params->AddAtTail(p1);
    params->AddAtTail(p2);
    params->AddAtTail(p3);

    // define computation nodes
    mcsUINT32 nbOfNodes = 20 ; 
   
    // compute the number of degree of freedom (must be >0)
    // Reset the number of node in order that the dof is even ; this
    // condition simplify the computation of the goodness-of-fit
    mcsUINT16 dof = (nbOfNodes-params->Size()) ;
    while((dof%2 != 0)&&(dof > 0))
    {
        dof = (++nbOfNodes-params->Size()) ;
    }
    
    mcsDOUBLE nodeMin   = -5.0 ;
    mcsDOUBLE nodeMax   = +5.0 ;
    mcsDOUBLE nodeInc   = (nodeMax-nodeMin)/((mcsDOUBLE)(nbOfNodes-1)) ;
    
    mcsDOUBLE *node      = (mcsDOUBLE*)malloc(nbOfNodes*sizeof(mcsDOUBLE));
    mcsDOUBLE *valAtNode = (mcsDOUBLE*)malloc(nbOfNodes*sizeof(mcsDOUBLE));
    mcsDOUBLE *wghAtNode = (mcsDOUBLE*)malloc(nbOfNodes*sizeof(mcsDOUBLE));

    // ==== generate noisy synthetic data to fit. 
    logInfo("Generate new data");

    mcsDOUBLE sigma = 0.025;
    randNORM *rg = new randNORM(); /* normal random number generator */
    for(mcsUINT16 idx=0 ; idx<nbOfNodes ; idx++)
    {
        *(node     +idx) = nodeMin + (mcsDOUBLE)(idx)*nodeInc ; 
        mcsDOUBLE signal = BellCurve(*(node+idx),*params) ; 
        mcsDOUBLE noisy  = rg->GetRNorm(signal,sigma) ;
        *(wghAtNode+idx) = sigma ;
        *(valAtNode+idx) = noisy ; 
    }
    delete(rg);
    
    // === init / set initial values
    (void) SetValandMinMax(p1,1.0,0.1 ,1e1);
    (void) SetValandMinMax(p2,nodeMin+0.25*(nodeMax-nodeMin),nodeMin,nodeMax);
    (void) SetValandMinMax(p3,2.0,0.1 ,5.0);
    // be sure that all of thus are free 
    p1->SetFixedValue(mcsFALSE);
    p2->SetFixedValue(mcsFALSE);
    p3->SetFixedValue(mcsFALSE);

    // Rescale paramters automatically 
    params->ComputeParameterScale(mcsFALSE);
    
    // Init the residue vector
    fitengRESIDUE_LIST   *residues= new fitengRESIDUE_LIST(nbOfNodes);

    // compute residue 
    for(mcsUINT16 idx=0 ; idx<nbOfNodes ; idx++)
    {
        // compute and store residue
        mcsDOUBLE locResidue = 
            *(valAtNode+idx) - BellCurve(*(node+idx),*params);
        (void) residues->SetValue(idx,locResidue/(*(wghAtNode+idx)));
    }

    // === Begin the fit intialisation
    logInfo("Init new fit session");

    fitengLM_ENGINE *fitSession = new fitengLM_ENGINE();
    fitSession->Init(params,residues);

    // === Some Init
    mcsUINT16 fMaxEval = fitSession->GetMaxFEval() ;
    fitSession->SetMaxFEval(5000) ;
    logInfo("fMaxEval before: %d, after: %d",
            fMaxEval,
            fitSession->GetMaxFEval());
    logInfo("Reset tolerance parameters");
    fitSession->SetTolerance(1e-6,1e-6,1e-6) ;
    logInfo("Current Levenberg-Marquardt factor set to %f",
            fitSession->GetLMFactor());


    // chi2 value ; set to an unresonnable high value in order to pass the
    // first step in the do/while loop below  
    mcsDOUBLE chi2 = 1e+300;

    // === Begin the fitting loop
    mcsDOUBLE chi2Accept = 1.0 ;
    mcsCOMPL_STAT status ; 
    do
    {
        // new step 
        status=fitSession->DoNextStep();
        
        // computation of residue
        for(mcsUINT16 idx=0 ; idx<nbOfNodes ; idx++)
        {
            mcsDOUBLE locResidue = 
                (*(valAtNode+idx) 
                 - BellCurve(*(node+idx),*params))/(*(wghAtNode+idx));
            (void) residues->SetValue(idx,locResidue);
        }
       
        // If we are not in tat mode, display the new parameter estimation
        if(!runIntoTat)
        {
            printf("[%03d - %03d] a=%-7.3e b=%-7.3e c=%-7.3e chi2/dof=%7.4f\n",
                    fitSession->GetStage(),
                    fitSession->GetInfo(),
                    p1->GetValue(),
                    p2->GetValue(),
                    p3->GetValue(),
                    chi2 = (residues->GetChi2()/(mcsDOUBLE)dof)
                  );
        }
        // Get the chi2/dof for the current step
        chi2 = (residues->GetChi2()/(mcsDOUBLE)dof);

    } while((chi2>=chi2Accept)&&(status == mcsSUCCESS));
   
    logInfo("End of session");

    // compute the goodness of fit
    mcsDOUBLE pValue=GoodnessOfFit(chi2*((mcsDOUBLE)dof),dof);
    if(!runIntoTat)
    {
        logInfo("Goodness-of-fits (p-value) = %e",pValue);
    }
    // Display the result in a human readable format
    if(pValue>0.1)
    {
        logInfo("Fitting procedure completed with p-value>10%%.");
    }
    else 
    {
        logWarning("Fitting procedure completed with p-value<10%%.");
    }

    // dump the file if required
    if(FindStringInArgv(argv,"--dump"))
    {
        // Save data into file
        FILE *fileDesc = fopen("fitresult.data","w");
        for(mcsUINT16 idx=0 ; idx<nbOfNodes ; idx++)
        {
            fprintf(fileDesc,"%e\t%e\t%e\t%e\n",
                    *(node+idx),
                    *(valAtNode+idx),
                    *(wghAtNode+idx),
                    BellCurve(*(node+idx),*params)
                   ) ;
        }
        fclose(fileDesc);
       
        logInfo("Results have been saved into fitresult.data");
    }

    // clean up and free memory
    delete(fitSession);

    free(node);
    free(valAtNode);
    free(wghAtNode);
    
    delete(params);
    delete(residues);

    return mcsSUCCESS;
};

/*___oOo___*/
