/*******************************************************************************
 * JMMC project
 * 
 * "@(#) $Id: mfsvrEvaluateResiduals.cpp,v 1.2 2006-08-02 08:17:29 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/07/10 12:26:21  lsauge
 * Result of the refactoring process of residuals evaluation between DoNextStep and InitLMEngine method
 *
******************************************************************************/

/**
 * @file
 * 
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfsvrEvaluateResiduals.cpp,v 1.2 2006-08-02 08:17:29 lsauge Exp $"; 

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
#include "mfsvrSERVER.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#define DEGTORAD    1.745329251994329547437168059786927187815308570e-02
#define RADTODEG    5.729577951308232286464772187173366546630859375e+01

/*!
  Evaluate residuals
  @return always return mcsSUCCESS
  */
    mcsCOMPL_STAT
mfsvrSERVER::EvaluateResiduals()
{

    mcsUINT16 idx = 0;
    for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
    {
        mfdataINPUT_DATA *dataNoCast = _data->GetDataSet(dsIdx) ; 

        switch ( dataNoCast->GetType() )
        {
            case mfdataINPUT_VIS2_TYPE:
                ////////////////////////////////////////////////////////////
                //  VIS2
                ////////////////////////////////////////////////////////////
                {
                    mfdataINPUT_VIS2 *dataVis2 = (mfdataINPUT_VIS2 *)dataNoCast ; 

                    mcsINT16  nbOfData = dataVis2->GetNbOfData();

                    mfmdlUV_COORDS uvCoords(nbOfData);
                    mfmdlVIS       visCpt(nbOfData);

                    (void) dataVis2->GetUVCoords(uvCoords);

                    if (_model->Eval(uvCoords,visCpt) != mcsSUCCESS)
                    {
                        return mcsFAILURE; 
                    }

                    for (mcsUINT16 dataIdx=0 ; dataIdx < nbOfData ; dataIdx++)
                    {

                        mcsDOUBLE O = dataVis2->GetVis2Data(dataIdx);
                        mcsDOUBLE E = dataVis2->GetVis2Err (dataIdx);
                        mcsDOUBLE C = visCpt.GetVis2(dataIdx);
                        mcsDOUBLE residue = (O-C)/E; 
                        _residues->SetValue(idx++,residue);
                    }
                }
                break;
            case mfdataINPUT_T3_TYPE:
                ////////////////////////////////////////////////////////////
                //  T3  
                ////////////////////////////////////////////////////////////
                {
                    mfdataINPUT_T3 *dataT3 = (mfdataINPUT_T3 *)dataNoCast ; 
                    mcsINT16  nbOfData = dataT3->GetNbOfData();

                    mcsDOUBLE *oT3Amp ;   
                    mcsDOUBLE *oT3AmpErr ;
                    mcsDOUBLE *oT3Phi ;   
                    mcsDOUBLE *oT3PhiErr;

                    mfmdlUV_COORDS uv1Coords(nbOfData);
                    mfmdlUV_COORDS uv2Coords(nbOfData);
                    mfmdlUV_COORDS uv3Coords(nbOfData);

                    mfmdlVIS vis1Cpt(nbOfData);
                    mfmdlVIS vis2Cpt(nbOfData);
                    mfmdlVIS vis3Cpt(nbOfData);

                    dataT3->GetUVCoords  (uv1Coords,uv2Coords);

                    // Get bispectrum components
                    dataT3->GetT3Amp     (&oT3Amp   );
                    dataT3->GetT3AmpErr  (&oT3AmpErr);
                    dataT3->GetT3Phi     (&oT3Phi   );
                    dataT3->GetT3PhiErr  (&oT3PhiErr);

                    if (uv3Coords.Add(uv1Coords,uv2Coords) == mcsFAILURE )
                    {
                        return mcsFAILURE;
                    }

                    // Evaluate models
                    if ( (_model->Eval(uv1Coords,vis1Cpt) != mcsSUCCESS)||
                         (_model->Eval(uv2Coords,vis2Cpt) != mcsSUCCESS)||
                         (_model->Eval(uv3Coords,vis3Cpt) != mcsSUCCESS))
                    {
                        return mcsFAILURE; 
                    }

                    // Set residual values
                    for (mcsUINT16 dataIdx=0 ; dataIdx < nbOfData ; dataIdx++)
                    {
                        mcsDOUBLE Re1,Im1;
                        mcsDOUBLE Re2,Im2;
                        mcsDOUBLE Re3,Im3;
                        // Get complex visibility components
                        vis1Cpt.Get(dataIdx,Re1,Im1);
                        vis2Cpt.Get(dataIdx,Re2,Im2);
                        vis3Cpt.Get(dataIdx,Re3,Im3);
                        // Compute intermediate values
                        mcsDOUBLE t5 = Im1*Re2 ;
                        mcsDOUBLE t7 = Im1*Im2 ;
                        mcsDOUBLE t1 = Re1*Re2 - t7;
                        mcsDOUBLE t3 = Re1*Im2 + t5;
                        // Compute Bispectrum 
                        mcsDOUBLE cT3Re  =  t1*Re3 + t3*Im3 ;
                        mcsDOUBLE cT3Im  = -t1*Im3 + t3*Re3 ;
                        mcsDOUBLE cT3Amp = sqrt(cT3Re*cT3Re + cT3Im*cT3Im) ; 
                        mcsDOUBLE cT3Phi = atan2(cT3Im,cT3Re) ; 
						
                        // set values
                        mcsDOUBLE res1 = (*(oT3Amp+dataIdx) - cT3Amp) / *(oT3AmpErr+dataIdx) ;
						
                        mcsDOUBLE O = (*(oT3Phi+dataIdx))*DEGTORAD    ; 
                        mcsDOUBLE E = (*(oT3PhiErr+dataIdx))*DEGTORAD ; 
                        mcsDOUBLE C = cT3Phi ;
                        mcsDOUBLE res2 = 2.0*sin(0.5*(O-C))/E ;
                        
						
                        _residues->SetValue(idx++,res1);
                        _residues->SetValue(idx++,res2);
					}
                }

            default:
                break;
        } // -- end of switch
    } // -- end of loop

    return mcsSUCCESS ;

}



/*___oOo___*/
