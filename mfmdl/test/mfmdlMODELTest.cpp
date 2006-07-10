/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlMODELTest.cpp,v 1.1 2006-06-20 09:30:30 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
******************************************************************************/

/**
 * @file
 * A simple test bed program for the mfmdlMODEL class. 
 *
 * @details
 * This code is dedicated to test of the mfmdlMODEL class. 
 * 
 */

// ======================================================================
// TODO :   verbosity level of log prints are not correctly set.
//          code review
// ======================================================================

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfmdlMODELTest.cpp,v 1.1 2006-06-20 09:30:30 lsauge Exp $"; 

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

#define SEM printf("SEMAPHORE %s %d\n",__FILE__,__LINE__)

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "mfmdl.h"
#include "mfmdlPrivate.h"
#include "mfmdlCOMPOSED_MODEL.h"

/*
 * Declaration of the function prototypes.
 */

mcsCOMPL_STAT TestDriver(
        const mcsDOUBLE fluxRatio,
        const mcsDOUBLE sep,
        const mcsDOUBLE angle,
        const char*     filename
        );

mcsCOMPL_STAT DumpToFile(
        const char*filename,
        mfmdlUV_COORDS &uvCoords, 
        mfmdlVIS &vis
        );

mcsCOMPL_STAT computeAndStoreUVTracks(
        const mcsDOUBLE c_Be,          
        const mcsDOUBLE c_Bn,          
        const mcsDOUBLE c_Bz,         
        const mcsDOUBLE c_lambda,
        const mcsDOUBLE c_latitude,
        const mcsDOUBLE c_sourceDec,
        const mcsDOUBLE c_sourceHAmin,
        const mcsDOUBLE c_sourceHAmax,
        const mcsUINT16 nbOfHAPoints,
        mfmdlUV_COORDS  &uvCoords
        );

mcsDOUBLE computeResidualsFromExactFormula(
        mfmdlCOMPOSED_MODEL &model,
        mfmdlUV_COORDS      &uvCoords, 
        mfmdlVIS            &vis
        );

mcsCOMPL_STAT initSeedFromClock();

mcsDOUBLE runif(
        const mcsDOUBLE min,
        const mcsDOUBLE max
        );

mcsLOGICAL SetFilenameAndStore(char **argv);


/*
 * Local Variables
 */



/* 
 * Signal catching functions  
 */



/*  ======================================================================
 *                                 M A I N 
 *  ====================================================================== */

int main(int argc, char *argv[])
{
    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Exit from the application with FAILURE
        exit (EXIT_FAILURE);
    }

    // Do the main job
    mcsCOMPL_STAT status;

    // Set the number of iteration
    const mcsUINT32 nbOfIteration = 10 ;  

    // Allocate memory for the filename
    char *filename = (char*)malloc(1024*sizeof(char));

    // Init the seed of the random numebr generator
    (void) initSeedFromClock();

    // perform loop other iterations
    for(mcsUINT32 index = 0 ; index<nbOfIteration ; index ++)
    {
        // Set filename if necessary ; check if it is necessary, looking
        // the coomand line arguments
        if(SetFilenameAndStore(argv) == mcsTRUE)
        {
            sprintf(filename,"data-%03d.dat",index);
        }

        // Run the TestDriver routine, setting the point-like source
        // binary parameters randomly
        status = TestDriver(
                    runif(0.1, 0.9),        // flux ratio
                    runif(5.0,20.0)*1e-3,   // projected separation (as)
                    runif(0.0,90.0),        // angle (degrees)
                    filename                // filename (no output if set to "")
                );

        // If ane error occured, then exit the loop 
        if(status == mcsFAILURE) 
        {
            logInfo("Error detected. Abort.");
            break;
        }
    }

    // Free previously allocated memory
    free(filename);
    // Close MCS services
    mcsExit();

    // Exit from the application with SUCCESS
    exit (EXIT_SUCCESS);
}

/*! ==========================================================================
        Main routine for testing MODEL class
        
        @param flusRatio        Flux ration (no unit)
        @param sep              angular separation on sky (arcsec)
        @param theta            angle (degree)
        @param filename         set the output filename. If it is set to "",
        no output should be performed
        @return SUCCESS or FAILURE according to the result of the test
  */
mcsCOMPL_STAT 
TestDriver(
        const mcsDOUBLE fluxRatio,
        const mcsDOUBLE sep, 
        const mcsDOUBLE theta, 
        const char*     filename 
        )
{
    mcsCOMPL_STAT status = mcsSUCCESS ;

    logInfo("Enter in the TestDriver main routine.");

    // Print the parameter values
    logInfo("flux ratio=%7.4f sep.=%7.4f mas angle=%7.4f deg. (NOTAT)",
            fluxRatio, 
            sep, 
            theta);

    // Instantiate a set of UV-coordinates
    const mcsUINT32 size = 10; 
    mfmdlUV_COORDS *uvCoords   = new mfmdlUV_COORDS(size);
    mfmdlVIS       *visResults = new mfmdlVIS(size);

    // Compute the uv-track
    (void) computeAndStoreUVTracks(20.0,20.0,0.0, // Baseline
            2.2,         // Wavelength (micrometer)
            31.68,       // latitude (Whipple obs), (degrees)
            11.87,       // GWOri DEC (degrees) 
            -12.0,       // HA min value (hours) 
            12.0,        // HA max value (hours)
            288,         // HA Sampling  (points)
            *uvCoords);

    // uvCoords could be resized accordingly the previous function call.
    // Then, resize visResults accordingly. 
    mcsUINT32 curSize = size ;
    if((curSize = uvCoords->Size()) != visResults->Size())
    {
        logInfo("Resize visResults array.");
        if(visResults->Resize(curSize) != mcsSUCCESS)
        {
            logInfo("Error: could not resize visResults array. Abort.");
            exit(mcsFAILURE);
        }
    }

    // Instantiate models
    mfmdlMODEL          *c1Model = new mfmdlPONCT("1rs component");
    mfmdlMODEL          *c2Model = new mfmdlPONCT("2nd component");
    mfmdlCOMPOSED_MODEL * cModel = new mfmdlCOMPOSED_MODEL("Composite Model");

    // Add simple model to the composite one
    cModel->AddAtTail(c1Model);
    cModel->AddAtTail(c2Model);

    // Get and Set parameters values
    mfmdlPARAMETER *f1, *f2;
    mfmdlPARAMETER *x1, *x2;
    mfmdlPARAMETER *y1, *y2;

    c1Model->GetParameter("intensityCoefficient", &f1 );
    c1Model->GetParameter("relativeAbscissae"   , &x1 );
    c1Model->GetParameter("relativeOrdinate"    , &y1 );
    c2Model->GetParameter("intensityCoefficient", &f2 );
    c2Model->GetParameter("relativeAbscissae"   , &x2 );
    c2Model->GetParameter("relativeOrdinate"    , &y2 );

    // First component 
    f1->SetValue(fluxRatio/(1.0+fluxRatio));
    x1->SetValue(0.0);
    y1->SetValue(0.0);

    // Second component 
    const mcsDOUBLE degToRad = 0.017453292 ;

    f2->SetValue(1.0-f1->GetValue());
    x2->SetValue(sep*cos(theta*degToRad));
    y2->SetValue(sep*sin(theta*degToRad));

    // Evaluate model 
    cModel->Eval(*uvCoords,*visResults);

    // Compute residuals
    mcsDOUBLE residual = computeResidualsFromExactFormula(
            *cModel,
            *uvCoords,
            *visResults);

    // Dump the result into an output file
    if(memcmp(filename,"",strlen(filename)) != 0)
    {
        if(DumpToFile(filename,*uvCoords,*visResults) != mcsSUCCESS)
        {
            // TODO: return error
        }
        else
            logInfo("Results stored into %s.",filename);
    }

    // Check the results
    if((residual <= 1e-15*((mcsDOUBLE)uvCoords->Size())) &&
            (residual >= 0.0))
    {
        logInfo("Sum of Residuals looks good.");
        status = mcsSUCCESS;
    }
    else
    {
        logInfo("Residual = %e. An error occured. Test Failed.",residual);
        status = mcsFAILURE;
    }

    // release the previously allocated structures
    // NOTE: remove message send to a composed model should be cascaded to all
    // individual components
    delete cModel;
    delete visResults;
    delete uvCoords;

    // bye
    return status;
}


/*! ==========================================================================
        Dump results into file
        @return SUCCESS or FAILURE
 */
mcsCOMPL_STAT 
DumpToFile(
        const char*filename,
        mfmdlUV_COORDS &uvCoords, 
        mfmdlVIS &vis)
{

    FILE *fileDesc ; 

    // Get the stream address after opening file
    if((fileDesc = fopen(filename,"w")) == NULL)
    {
        logInfo("Error: cannot create file.");
        return mcsFAILURE;
    }
    else
    {
        // loop other array elements
        for(mcsUINT32 index=0 ; index<vis.Size() ; index++)
        {
            // Get the baseline (Bx,By)
            mcsDOUBLE Bx;
            mcsDOUBLE By;

            if(uvCoords.Get(index,Bx,By) == mcsSUCCESS)
            {
                // Compute the projected baseline and get the
                // corresponding squared visibilities
                mcsDOUBLE B    = sqrt(Bx*Bx + By*By);
                mcsDOUBLE vis2 = vis.GetVis2(index);
                // write into file
                fprintf(fileDesc,"%7.4f %7.4f %7.4f %7.4f\n",
                        Bx,By,B,vis2 );
            }
            // on error, return error and quit
            else
                return mcsFAILURE;
        }

        // close file
        if(fclose(fileDesc) != 0)
            return mcsFAILURE;
        else
            return mcsSUCCESS;
    }

}


/*! ==========================================================================
        Compute track in the uv-plane, according source and observation site
        coordinates.

        @param c_Be           Baseline east   (m)
        @param c_Bn                    north  (m)
        @param c_Bz                    zenith (m)
        @param c_lambda       wavelength (microm)
        @param c_latitude     latitude      (deg)
        @param c_sourceDec    source dec    (rad)
        @param c_sourceHAmin  Hour angle min value (h) 
        @param c_sourceHAmax  Hour angle max value (h)
        @param nbOfHAPoints   Size of the sample
        @param uvCoords       output UV_COORDS instance 

        @return SUCCESS or FAILURE
*/
mcsCOMPL_STAT 
computeAndStoreUVTracks(
        // parameter descriptions and units
        const mcsDOUBLE c_Be,          // Baseline east   (m)
        const mcsDOUBLE c_Bn,          //          north  (m)
        const mcsDOUBLE c_Bz,          //          zenith (m)
        const mcsDOUBLE c_lambda,      // wavelength (microm)
        const mcsDOUBLE c_latitude,    // latitude      (deg)
        const mcsDOUBLE c_sourceDec,   // source dec    (rad)
        const mcsDOUBLE c_sourceHAmin, // Hour angle min value (h) 
        const mcsDOUBLE c_sourceHAmax, // Hour angle max value (h)
        const mcsUINT16 nbOfHAPoints,  // Size of the sample
        mfmdlUV_COORDS  &uvCoords      // output UV_COORDS instance 
        )
{
    // First of all, resize the uvCoords array to the correct size
    if(uvCoords.Resize(nbOfHAPoints) == mcsFAILURE)
    {
        logInfo("Error: uvCoords could not be resized.");
        return mcsFAILURE;
    }

    // Convert all parameter values 
    const mcsDOUBLE degToRad = 0.017453292 ;
    const mcsDOUBLE HAToRad  = 0.261799387 ;

    mcsDOUBLE Be          = c_Be;                
    mcsDOUBLE Bn          = c_Bn;        
    mcsDOUBLE Bz          = c_Bz;        
    mcsDOUBLE lambda      = 1e-6     * c_lambda     ;    
    mcsDOUBLE latitude    = degToRad * c_latitude   ;  
    mcsDOUBLE sourceDec   = degToRad * c_sourceDec  ; 
    mcsDOUBLE sourceHAmin = HAToRad  * c_sourceHAmin;
    mcsDOUBLE sourceHAmax = HAToRad  * c_sourceHAmax;

    mcsDOUBLE  HA = sourceHAmin;
    mcsDOUBLE dHA = (sourceHAmax-sourceHAmin)
        /((mcsDOUBLE)(nbOfHAPoints -1));

    // Loop other HA elements
    mcsUINT16 index;
    for(index = 0 ; index <nbOfHAPoints ; index++)
    {
        // Compute (u,v) 
        mcsDOUBLE u = (
                Be*cos(HA) 
                - Bn*sin(latitude)*sin(HA) 
                + Bz*cos(latitude)*sin(HA)
                )/(206265.0*lambda) ;

        mcsDOUBLE v = (
                Be*sin(sourceDec)*sin(HA) 
                + Bn*(sin(latitude)*sin(sourceDec)*cos(HA)+cos(latitude)*cos(sourceDec))
                - Bz*(cos(latitude)*sin(sourceDec)*cos(HA)-sin(latitude)*cos(sourceDec))
                )/(206265.0*lambda) ;
        // add the the current point to the track  
        if(uvCoords.Set(index,u,v) != mcsSUCCESS)
        {
            logInfo("Could not set element #%d of array uvCoords",index);
        }
        // increment HA value
        HA += dHA ;
    }

    // return
    return mcsSUCCESS;

}


/*! ==========================================================================
        From the uv-plane coordinates and the model parameters, compute the
        exact squared vissibility values for a point-like binary sources.  The
        function return the sum of squared residual between the theoritical
        and the given values.

        @param  model       Composite model id 
        @param  uvCoords    uv-plane coordinate  
        @param  vis         Complex visiblities compute 'model' evaluation

        @return             Sum of squared residual 
  */
mcsDOUBLE 
computeResidualsFromExactFormula(
        mfmdlCOMPOSED_MODEL &model,
        mfmdlUV_COORDS      &uvCoords, 
        mfmdlVIS            &vis
        )
{
    const mcsDOUBLE _2PI = 6.28318530717958647692528676656 ; 

    if(uvCoords.Size() != vis.Size())
    {
        logInfo("uvCoords and vis arrays must have the same size. Abort.");
        return -1.0;
    }

    mcsDOUBLE res = 0.0 ;

    // Get the parameters from model
    // 
    // First, the model must be a binary source. Check 
    //      1- the model passed to this function is composite
    //      2- the number of components is 2
    //      3- Each one of the element lit is point-like source.

    // TODO: check sattus of the method calls

    mcsDOUBLE F1 ;
    mcsDOUBLE F2 ;
    mcsDOUBLE r  ;
    mcsDOUBLE da ;
    mcsDOUBLE db ;

    if(model.GetModelType() == mfmdlCOMPOSED_MODEL_TYPE)
    {
        if(model.GetNbOfComposingModels() == 2)
        {
            mfmdlMODEL *model1 ;
            mfmdlMODEL *model2 ;
            model.GetComposingModel(0,&model1) ;
            model.GetComposingModel(1,&model2) ;

            if((model1->GetModelType() == mfmdlPONCT_MODEL_TYPE) &&            
                    (model2->GetModelType() == mfmdlPONCT_MODEL_TYPE))
            {
                // now we can get the parameters value
                mfmdlPARAMETER *f1, *f2;
                mfmdlPARAMETER *x1, *x2;
                mfmdlPARAMETER *y1, *y2;

                model1->GetParameter("intensityCoefficient", &f1 );
                model1->GetParameter("relativeAbscissae"   , &x1 );
                model1->GetParameter("relativeOrdinate"    , &y1 );
                model2->GetParameter("intensityCoefficient", &f2 );
                model2->GetParameter("relativeAbscissae"   , &x2 );
                model2->GetParameter("relativeOrdinate"    , &y2 );

                // Get the flux of the sources 
                F1 = f1->GetValue(); 
                F2 = f2->GetValue();
                // Compute the flux ratio
                if((F1*F2) != 0.0 )
                {
                    r = F1/F2;
                }
                else
                {
                    logInfo("One source flux is set to 0. Abort.");
                    return -1.0;
                }
                // Get the the projected separation
                da = x1->GetValue() - x2->GetValue(); 
                db = y1->GetValue() - y2->GetValue(); 
            }
            else
            {
                logInfo("Individual models must be point-like sources. Abort.");
                logInfo("\t==> model1=%d  model2=%d ",
                        model1->GetModelType(),
                        model2->GetModelType());
                return -1.0;
            }
        }
        else
        {
            logInfo("The composite model must have 2 components. Abort.");
            return -1.0;
        }
    }
    else
    {
        logInfo("The model must be composite. Abort.");
        return -1.0;
    }

    /* composite model (model container) */
    mcsUINT32 index;
    for(index = 0 ; index <uvCoords.Size() ; index++)
    {

        mcsDOUBLE u = 0.0 ;
        mcsDOUBLE v = 0.0 ;
        if(uvCoords.Get(index,u,v) == mcsSUCCESS)
        {
            // Compute theoritical squared visibility
            mcsDOUBLE t0 = 2.0*r/(1.0+r)/(1.0+r) ;
            mcsDOUBLE t1 = _2PI*(u*da+v*db) ;
            mcsDOUBLE t2 = cos(t1) ;
            mcsDOUBLE t3 = 1.0+t0*(t2-1.0) ;
            // compute residual
            mcsDOUBLE t4 = t3 - vis.GetVis2(index);
            mcsDOUBLE t5 = t4*t4;
            // Add to the global sum
            res += t5 ;
        }
        else
            return -1.0;
    }

    // return the result and quit
    return res;
}


/*! ==========================================================================
        Init the pseudo-random generator of number using the unix's clock
        
        @return always SUCCES
 */
    mcsCOMPL_STAT
initSeedFromClock()
{
    // Get time
    time_t timer = time (NULL);
    // Call srand function
    (void) srand((unsigned)timer);

    return mcsSUCCESS;
}

/*! ==========================================================================
        Return a pseudo-random real number uniformely distributed between in a
        given interval 

        @param  min lower bound of the interval
        @param  max upper bound of the interval
        
        @return pseudo-random real number
  */
mcsDOUBLE 
runif(  const mcsDOUBLE min,
        const mcsDOUBLE max
     )
{
    if (min == max)
        // Not need to get a rand. num ...
        return min;
    else
    {
        mcsDOUBLE unif_rand = (mcsDOUBLE)rand() / (mcsDOUBLE)RAND_MAX ;
        return min + (max - min) * unif_rand;
    }
}

/*! ==========================================================================
        Check in the passed argument vector if the option --dump exists

        @param    argv    an argument vector, usually that one of the main routine
        @return mcsTRUE if "--dump" have been found, mcsFALSE otherwise
  */
    mcsLOGICAL
SetFilenameAndStore(char **argv)
{
    // local declaration
    unsigned index=0;
    char     *curArg=NULL;

    // loop other argument vector items 
    while((curArg=argv[++index]) != NULL) 
    {
        // get the minimum size (avoiding out of bound)
        size_t a;
        size_t b;
        size_t size = (a=strlen(curArg)) < (b=strlen("--dump")) ? a : b ;
        // check if the current option is different from --dump
        if(memcmp(curArg,"--dump",size) == 0)
        {
            // find --dump ! return TRUE and exit the loop
            return(mcsTRUE);
        }
    }
    // not found ! 
    return mcsFALSE;
}

/*___oOo___*/
