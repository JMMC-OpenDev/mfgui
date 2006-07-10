/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlTest2.cpp,v 1.3 2006-05-11 13:04:56 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/31 08:17:52  gzins
 * Updated
 *
 * Revision 1.1  2006/02/03 16:41:47  lsauge
 * add test code for the composite model
 *
 *
 ******************************************************************************/

/**
 * \file
 * Test code for the point source model (mfmdlPONCT) 
 *
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlTest2.cpp,v 1.3 2006-05-11 13:04:56 mella Exp $";
/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>

#include <string>

/**
 * \namespace std
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
#include "mfmdl.h"
#include "mfmdlPrivate.h"

mcsCOMPL_STAT mfmdlEvalPointSourceModel(const mcsDOUBLE flux1,
                                        const mcsDOUBLE flux2,
                                        const mcsDOUBLE sep)
{
    /* Declare point source model */ 
    mfmdlPONCT * m1 = new mfmdlPONCT("First component");
    mfmdlPONCT * m2 = new mfmdlPONCT("First component");
    
    /* Declare a composite model ... */
    mfmdlCOMPOSED_MODEL * cModel = new mfmdlCOMPOSED_MODEL("Composite Model");
    /* ...and add the two previous point source model */
    cModel->AddAtTail(m1);
    cModel->AddAtTail(m2);

    fitengPARAMETER_LIST paramList;
    if (cModel->GetParameters(&paramList, mcsTRUE) == mcsFAILURE)
    {
        logError("Get parameters failed ...");
        return mcsFAILURE;
    }
    paramList.Display();

    /* Get reference of the main model parameters of each component */
    mfmdlPARAMETER *f1, *f2;
    mfmdlPARAMETER *x1, *x2;
    mfmdlPARAMETER *y1, *y2;
    
    if (m1->GetParameter("intensityCoefficient", &f1) == mcsFAILURE)
    {
        logError("Get intensityCoefficient failed ...");
        return mcsFAILURE;
    }
    if (m1->GetParameter("relativeAbscissae", &x1) == mcsFAILURE)
    {
        logError("Get relativeAbscissae failed ...");
        return mcsFAILURE;
    }
    if (m1->GetParameter("relativeOrdinate", &y1) == mcsFAILURE)
    {
        logError("Get relativeOrdinate failed ...");
        return mcsFAILURE;
    }
    
    if (m2->GetParameter("intensityCoefficient", &f2) == mcsFAILURE)
    {
        logError("Get intensityCoefficient failed ...");
        return mcsFAILURE;
    }
    if (m2->GetParameter("relativeAbscissae", &x2) == mcsFAILURE)
    {
        logError("Get relativeAbscissae failed ...");
        return mcsFAILURE;
    }
    if (m2->GetParameter("relativeOrdinate", &y2) == mcsFAILURE)
    {
        logError("Get relativeOrdinate failed ...");
        return mcsFAILURE;
    }
    
    /* Set parameters values  */
    f1->SetValue(flux1);
    x1->SetValue(0.0);
    y1->SetValue(0.0);

    f2->SetValue(flux2);
    x2->SetValue(0.0);
    y1->SetValue(sep);

    
    /* Sampling parameters of the (u,v) plane */
    const mcsINT32 nbOfData=  1000;
    const mcsDOUBLE uvMax  = 100.0;
    const mcsDOUBLE uvMin  =  0.0;
    mcsDOUBLE uvInc = (uvMax-uvMin)/(mcsDOUBLE)(nbOfData-1);
    mcsDOUBLE projB ;   /* Projected baseline */
    mcsDOUBLE vis2m1  ; /* Squared visibility */
    mcsDOUBLE vis2m2  ; /* Squared visibility */
    mcsDOUBLE vis2cModel ; /* Squared visibility */
    mcsDOUBLE vis2Norm   ; /* Squared visibility Norm */
    
    /* Squared visibilty norm for the composed model */
    vis2Norm  = f1->GetValue()+f2->GetValue();
    vis2Norm *= vis2Norm ; 
   
    /* Memory allocation and cast */
    mcsDOUBLE *u    = (mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    mcsDOUBLE *v    = (mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    mcsCOMPLEX *compVISm1     = (mcsCOMPLEX*)malloc(nbOfData*sizeof(mcsCOMPLEX));
    mcsCOMPLEX *compVISm2     = (mcsCOMPLEX*)malloc(nbOfData*sizeof(mcsCOMPLEX));
    mcsCOMPLEX *compVIScModel = (mcsCOMPLEX*)malloc(nbOfData*sizeof(mcsCOMPLEX));

    /* Initialize (u,v) coordinates */
    mcsINT16 i;
    for( i=0 ; i<nbOfData ; i++)
    {
       *(u+i) = uvMin + ((mcsDOUBLE)i)*uvInc; 
       *(v+i) = *(u+i) ;
    } /* End-for */
   
    /* Evaluate models */
    if (m1->Eval(u, v, nbOfData, compVISm1) == mcsFAILURE)
    {
        logError("1rst model evaluation failed ...");
        return mcsFAILURE;
    }
    if (m2->Eval(u, v, nbOfData, compVISm2) == mcsFAILURE)
    {
        logError("1rst model evaluation failed ...");
        return mcsFAILURE;
    }
    if (cModel->Eval(u, v, nbOfData, compVIScModel) == mcsFAILURE)
    {
        logError("Composite model evaluation failed ...");
        return mcsFAILURE;
    }

    FILE *fDesc;
    mcsDOUBLE re;
    mcsDOUBLE im;
    /* Compute the squared visibility as function as the projected baseline
     * value, and dump all the results in output file */
    fDesc = fopen("mfmdlTest2.data","w");
    if(fDesc == NULL)
    {
        logError("File creation failed ...");
        return(mcsFAILURE);
    }
    for( i=0 ; i<nbOfData ; i++)
    {
        /* Projected baseline */
        projB = sqrt( (*(u+i))* (*(u+i))+ (*(u+i))* (*(u+i)));

        /* Squared visibility of model 1 */
        re = compVISm1[i].re;
        im = compVISm1[i].im;
        vis2m1 = re*re + im*im; 

        /* Squared visibility of model 1 */
        re = compVISm2[i].re;
        im = compVISm2[i].im;
        vis2m2 = re*re + im*im; 
        
        /* Squared visibility of the composite model */
        re = compVIScModel[i].re;
        im = compVIScModel[i].im;
        vis2cModel = (re*re + im*im)/vis2Norm; 
   
        fprintf(fDesc,"% 7.4e\t% 7.4e\t %7.4e\t %7.4e\n", 
               projB,
               vis2m1,
               vis2m2,
               vis2cModel
               );
    } /* End-for */
    fclose(fDesc);

    cout<<"all results are saved in mfmdlTest2.data"<<endl;
    cout<<"format description: "<<endl;
    cout<<"\tcolumn 1:\t projected baseline"<<endl;
    cout<<"\tcolumn 2:\t squared visibility of the first component (point sourced)"<<endl;
    cout<<"\tcolumn 3:\t squared visibility of the second (point source)"<<endl;
    cout<<"\tcolumn 4:\t squared visibility of the composite model (=binary unresolved system.)"<<endl;

    /* free previously allocated memory */
    free(u);
    free(v); 
    free(compVISm1);
    free(compVISm2);
    free(compVIScModel);
    
    delete cModel;
    
    return(mcsSUCCESS);
}


/* 
 * Main
 */

int main(int argc, char *argv[])
{
    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Exit from the application with FAILURE
        errCloseStack();
        exit (EXIT_FAILURE);
    }

    // Set high verbosity
    logSetStdoutLogLevel(logTEST);
    
    /* local declaration */
    if(argc == 4 )
    {
        if (mfmdlEvalPointSourceModel(atof(argv[1]),
                                      atof(argv[2]),
                                      atof(argv[3])) == mcsFAILURE)
        {
            errCloseStack();
            exit (EXIT_FAILURE);
        }
    }
    else
    {
        cout <<"Illegal number of arguments"<<endl;
        cout <<"usage: "<<argv[0]<<" flux1 flux2 sep "<<endl;
        cout <<"where \t flux1: flux of the first component"<<endl;
        cout <<"      \t flux2: flux of the second component"<<endl;
        cout <<"      \t   sep: separation."<<endl;
        exit (EXIT_FAILURE);
    }

    // Close MCS services
    mcsExit();

    // say bye-bye ! 
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
