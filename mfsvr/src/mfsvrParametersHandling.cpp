/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrParametersHandling.cpp,v 1.3 2006-08-03 14:20:52 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/07/04 08:31:50  lsauge
 * factorizing parameters common handling methods
 *
 * Revision 1.1  2006/06/28 12:35:36  lsauge
 * First implementation
 *
******************************************************************************/

/**
 * @file
 *  Definition of mfsvrParametersHandling class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfsvrParametersHandling.cpp,v 1.3 2006-08-03 14:20:52 lsauge Exp $"; 

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

    mcsCOMPL_STAT
mfsvrSERVER::CopyParameters_ModelToEngine()
{
    // _model and _params must be defined 
    if ((_model == NULL)||(_params == NULL))
    {
        return mcsFAILURE ;
    }

    mfmdlPARAMETER_LIST paramList;

    // copy must be limited to the model parameters array size
    if (_model->GetParameters(&paramList,mcsTRUE) == mcsSUCCESS)
    {
        for(mcsUINT16 i=0 ; i<paramList.Size() ; i++)
        {
            mfmdlPARAMETER  *parmdl = paramList.GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
            fitengPARAMETER *parfit =  _params->GetNextParameter((i==0)?mcsTRUE:mcsFALSE);

            printf("%02d %-20s %g\n",
                    i,
                    (parfit->GetName()).c_str(),
                    parfit->GetValue()
                  );

            if (( parfit->GetName()).c_str()=="rho")
            {
                // Cartesian coordinates 
                mcsDOUBLE X0    = parmdl->GetValue() ;
                i++;
                parmdl = paramList.GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
                mcsDOUBLE Y0    = parmdl->GetValue() ;

                // polar form
                mcsDOUBLE rho   = sqrt( X0*X0 + Y0*Y0 ); 
                mcsDOUBLE theta = atan2(Y0,X0);
                parfit->SetValue(rho); 
                parfit =  _params->GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
                parfit->SetValue(theta); 
            }
            else
            {
                parfit->SetValue     (parmdl->GetValue());
            }
        }
    }
    else
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

    mcsCOMPL_STAT
mfsvrSERVER::CopyParameters_EnginetoModel()
{
    // _model and _params must be defined 
    if ((_model == NULL)||(_params == NULL))
    {
        return mcsFAILURE ;
    }

    mfmdlPARAMETER_LIST paramList;

    // copy must be limited to the model parameters array size
    if (_model->GetParameters(&paramList,mcsTRUE) == mcsSUCCESS)
    {
        for(mcsUINT16 i=0 ; i<paramList.Size() ; i++)
        {
            fitengPARAMETER *parfit =  _params->GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
            mfmdlPARAMETER  *parmdl = paramList.GetNextParameter((i==0)?mcsTRUE:mcsFALSE);

            if (strcmp((parfit->GetName()).c_str(),"rho") == 0)
            {
                // polar form
                mcsDOUBLE rho   = parfit->GetValue();
                i++ ;
                parfit =  _params->GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
                mcsDOUBLE theta = parfit->GetValue();
                // Cartesian coordinates 
                mcsDOUBLE X0    = rho*cos(theta); 
                mcsDOUBLE Y0    = rho*sin(theta);
                parmdl->SetValue(X0);
                parmdl = paramList.GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
                parmdl->SetValue(Y0);

            }
            else
            {
                parmdl->SetValue(parfit->GetValue());
            }
        }
    }
    else
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/*
    Build the parameter list from the model one
    @param  msg message container address of the caller
    @return mcsSUCCESS or mcsFAILURE
*/
mcsCOMPL_STAT
mfsvrSERVER::BuildParameterList( msgMESSAGE &msg )
{
    // _model must be defined 
    if (_model == NULL )
    {
        logError("No model defined. Abort.");
        return mcsFAILURE;
    }
    
    // Get the number of models
    
    mcsINT32 nbOfModels= _model->GetNbOfComposingModels();
    

    // if _params NULL, parameters list should be build from scratch
    if ( _params == NULL )
    {

        // Instantiate a new fitengPARAMETER_LIST and set _params
        _params = new fitengPARAMETER_LIST() ; 
        if ( _params == NULL )
        {
            logError("Cannot create a new parameter list");
            return mcsFAILURE ; 
        }
        
    
        // loop other the individual models
        for (mcsINT32 mdlIdx=0 ; mdlIdx<nbOfModels ; mdlIdx++)
        {
    
            mfmdlMODEL *lModel = NULL;
            if( _model->GetComposingModel(mdlIdx,&lModel) == mcsFAILURE)
            {
                logError("Cannot acces to models. Abort.");
                return mcsFAILURE;
            }

            // Get the model one
    
            mfmdlPARAMETER_LIST  localList ;
            if ( lModel->GetParameters(&localList,mcsTRUE) == mcsFAILURE )
            {
                logError("Cannot acces to the parameter list. Abort.");
                return mcsFAILURE; 
            }
    

            fitengPARAMETER *parfit_X0 = NULL ; 
            fitengPARAMETER *parfit_Y0 = NULL ;

    
            // Copy the param list from the model one to the new one
            for(mcsUINT16 i=0 ; i<localList.Size() ; i++)
            {
                mfmdlPARAMETER  *parmdl = localList.GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
                fitengPARAMETER *parfit = new fitengPARAMETER(parmdl->GetName());
                //
                if (parfit->GetName() == "relativeAbscissae")
                {
                    parfit_X0 = parfit;            
                }
                if (parfit->GetName() == "relativeOrdinate")
                {
                    parfit_Y0 = parfit;            
                }
                // copy 
                parfit->SetValue     (parmdl->GetValue());
                parfit->SetMinValue  (parmdl->GetMinValue());
                parfit->SetMaxValue  (parmdl->GetMaxValue());

                // if scale is null, the parameters must be regularized 
                parfit->SetScale     (parmdl->GetScale());
                parfit->SetFixedValue(parmdl->HasFixedValue());
                // Add at tail
                _params->AddAtTail(parfit);
            }
    

            if ( lModel->IsPolarEnabled() == mcsTRUE )
            {
                mcsDOUBLE X0 = parfit_X0->GetValue() ;  
                mcsDOUBLE Y0 = parfit_Y0->GetValue() ;

                // Rename parameter
                parfit_X0->SetName("rho");
                parfit_Y0->SetName("theta");

                // compute rho and theta
                mcsDOUBLE rho   = sqrt( X0*X0 + Y0*Y0 ) ;
                mcsDOUBLE theta = 0.0 ; 
                if( (X0!=0.0) && (Y0!=0.0))
                {
                    theta = atan2(Y0,X0) ;
                }
                // set parameter values
                parfit_X0->SetValue(rho) ; 
                parfit_X0->SetMinValue(0.0) ; 
                parfit_X0->SetMaxValue(50.0) ;

                parfit_Y0->SetValue(theta) ;
                parfit_Y0->SetMinValue(-M_PI) ; 
                parfit_Y0->SetMaxValue(+M_PI) ;
            }
        }

        // Lagrange multiplier for normalized flux constraint
        {
            fitengPARAMETER *parfit = new fitengPARAMETER("lagrangeMultiplier");
            // set default values
            parfit->SetValue     (1.0) ;
            parfit->SetMinValue  (0.1) ;
            parfit->SetMaxValue  (1e3) ;
            parfit->SetScale     (1.0) ;
            // add at tail
            _params->AddAtTail(parfit);
        }
    }
    else
    {
        // _params no NULL ; a model have been added.
        // The parameters list should not be rebuild from scratch ; in this
        // case, the new parameters description should be inserted in the
        // right place.

        // loop other the individual models
    
        mcsINT32 localPos = 0 ; 

        mfmdlMODEL *lModel = NULL;
        mfmdlPARAMETER_LIST localList ; 
    
        mcsINT32 mdlIdx = 0;
        for (mdlIdx=0 ; mdlIdx<(nbOfModels-1); mdlIdx++)
        {
            if ( _model->GetComposingModel(mdlIdx,&lModel) == mcsFAILURE)
            {
                logError("Cannot acces to models. Abort.");
                return mcsFAILURE;
            }
    
            if (lModel->GetParameters(&localList,mcsTRUE) == mcsFAILURE)
            {
                logError("Cannot acces to the parameter list. Abort.");
                return mcsFAILURE; 
            }

            localPos += localList.Size() ;
        }
            
        fprintf(stdout,"#####\n##### localpos = %d\n",localPos);
        fflush(stdout);
    
        // Now consider the last added model
        if ( _model->GetComposingModel(mdlIdx,&lModel) == mcsFAILURE)
        {
    
            logError("Cannot acces to models. Abort.");
            return mcsFAILURE;
        }

        // Get the parameters list
        if (lModel->GetParameters(&localList,mcsTRUE) == mcsFAILURE)
        {
            logError("Cannot acces to the parameter list. Abort.");
            return mcsFAILURE; 
        }
    
        fitengPARAMETER *parfit_X0 = NULL ; 
        fitengPARAMETER *parfit_Y0 = NULL ;

        // Copy the param list from the model one to the new one
        for(mcsUINT16 i=0 ; i<localList.Size() ; i++)
        {
            mfmdlPARAMETER  *parmdl = localList.GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
            fitengPARAMETER *parfit = new fitengPARAMETER(parmdl->GetName());
            //
            if (parfit->GetName() == "relativeAbscissae")
            {
                parfit_X0 = parfit;            
            }
            if (parfit->GetName() == "relativeOrdinate")
            {
                parfit_Y0 = parfit;            
            }
            // copy 
            parfit->SetValue     (parmdl->GetValue());
            parfit->SetMinValue  (parmdl->GetMinValue());
            parfit->SetMaxValue  (parmdl->GetMaxValue());
            parfit->SetScale     (parmdl->GetScale());
            parfit->SetFixedValue(parmdl->HasFixedValue());
            // Add at tail
            _params->InsertAtPos (parfit,localPos++);
        }

        // If the model use polar coordinate, correct the paremeters name and
        // set the value
        if ( lModel->IsPolarEnabled() == mcsTRUE )
        {
            mcsDOUBLE X0 = parfit_X0->GetValue() ;  
            mcsDOUBLE Y0 = parfit_Y0->GetValue() ;

            // Rename parameter
            parfit_X0->SetName("rho");
            parfit_Y0->SetName("theta");

            // compute rho and theta
            mcsDOUBLE rho   = sqrt( X0*X0 + Y0*Y0 ) ;
            mcsDOUBLE theta = 0.0 ; 
            if( (X0!=0.0) && (Y0!=0.0))
            {
                theta = atan2(Y0,X0) ;
            }
            // set parameter values
            parfit_X0->SetValue(rho) ; 
            parfit_X0->SetMinValue(0.0) ; 
            parfit_X0->SetMaxValue(20.0) ;

            parfit_Y0->SetValue(theta) ;
            parfit_Y0->SetMinValue(-M_PI) ; 
            parfit_Y0->SetMaxValue(+M_PI) ;
    
        }
    }
    
    // Check normalization
    this->CheckNormalization();
        
    // regularize the parameters
    {
        logInfo("Regularize parameters");
        _params->ComputeParameterScale(mcsTRUE);
    }

    return mcsSUCCESS;

}

/*!
    Check the normlization condition
    IF only one model is defined, intensity coefficient value is frozen to
    1.0, and the lagrange's multiplier set to 0
    @return mcsFAILURE or mcsSUCCESS
*/
mcsCOMPL_STAT 
mfsvrSERVER::CheckNormalization()
{
    
    // _model and _params must be defined 
    if ((_model == NULL)||(_params == NULL))
    {
        return mcsFAILURE ;
    }

    // cast the model as a composed one
    mfmdlCOMPOSED_MODEL *model = (mfmdlCOMPOSED_MODEL *) _model;
    
    // Get the nb of models
    mcsUINT16 nbOfModels = model->GetNbOfComposingModels();
    
    if( nbOfModels == 1 )
    {
        fitengPARAMETER *param;
        // Get Lagrange's multiplier
        if ( (param = _params->GetParameter("lagrangeMultiplier")) == NULL)
        {
            return mcsFAILURE ;
        }
        else
        {
            param->SetValue     (0.0) ;
            param->SetMinValue  (0.0) ;    
            param->SetMaxValue  (1.0) ;
            param->SetFixedValue(mcsTRUE) ;
        }

        // Get intensityCoefficient 
        if ( (param = _params->GetParameter("intensityCoefficient")) == NULL)
        {
            return mcsFAILURE ;
        }
        else
        {
            param->SetValue     (1.0) ;
            param->SetMinValue  (0.0) ;    
            param->SetMaxValue  (1.0) ;
            param->SetFixedValue(mcsTRUE) ;
        }
    }
    
    return mcsSUCCESS ;
}

/*___oOo___*/
