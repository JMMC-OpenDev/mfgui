/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSERVER.cpp,v 1.8 2006-08-02 08:22:45 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/07/11 11:18:05  lsauge
 * add a call to RemoveFile in desctructor
 *
 * Revision 1.6  2006/07/10 13:26:00  mella
 * Replcae data.h by mfdata.h...
 *
 * Revision 1.5  2006/07/10 12:23:25  lsauge
 * Register ShowData call back method
 *
 * Revision 1.4  2006/06/28 12:35:11  lsauge
 * update (calllback added)
 *
 * Revision 1.3  2006/06/27 09:03:32  lsauge
 * Update following third party call-back implementation
 *
 * Revision 1.1  2006/03/31 08:18:49  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 * Definition of the mfsvrSERVER class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: mfsvrSERVER.cpp,v 1.8 2006-08-02 08:22:45 lsauge Exp $"; 


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
#include "mfsvrSHOWDATA_CMD.h"
#include "mfsvrPLOTMAP_CMD.h"
#include "mfsvrEXPLOREMC_CMD.h"
#include "mfsvrSETSCALE_CMD.h"
#include "mfsvrPLOTMODEL_CMD.h"
#include "mfsvrSETLMPARAM_CMD.h"
#include "mfsvrSETLMPARAM_CMD.h"
#include "mfsvrINITLMENGINE_CMD.h"
#include "mfsvrDONEXTSTEP_CMD.h"
#include "mfsvrPLOTDATA_CMD.h"
#include "mfsvrSETPARAMETER_CMD.h"
#include "mfsvrSHOWPARAMETERS_CMD.h"
#include "mfsvrSHOWMODEL_CMD.h"
#include "mfsvrADDMODEL_CMD.h"
#include "mfsvrADDMODEL_CMD.h"
#include "mfsvrSETMODEL_CMD.h"
#include "mfsvrSETDATA_CMD.h"
#include "mfsvrGETTIME_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrVersion.h"

#include "mfdata.h"

/*
 * Class constructor
 */
mfsvrSERVER::mfsvrSERVER()
{
    _model      = NULL;
    _residues   = NULL;
    _data       = NULL;
    _fitEngine  = NULL;
    _params     = NULL;
    _pPlot      = NULL;
    _dof        = 0 ;
    _iter       = 0 ;
}

/*
 * Class destructor
 */
mfsvrSERVER::~mfsvrSERVER()
{
    // Delete objects
    if (_model != NULL)
    {
        delete (_model);
    }
    if (_residues != NULL)
    {
        delete (_residues);
    }
    if (_data!= NULL)
    {
        delete (_data);
    }
    if (_fitEngine!= NULL)
    {
        delete (_fitEngine);
    }
    if (_params!= NULL)
    {
        delete (_params);
    }
    if (_pPlot != NULL)
    {
        DeletePlot();
    }
    
    (void) RemoveFiles();
}


/*
 * Public methods
 */
mcsCOMPL_STAT mfsvrSERVER::AppInit()
{
    logTrace("mfsvrSERVER::AppInit()");

	evhCMD_KEY ShowData_Key(mfsvrSHOWDATA_CMD_NAME, mfsvrSHOWDATA_CDF_NAME);
	evhCMD_CALLBACK ShowData_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::ShowDataCB);
	AddCallback(ShowData_Key,ShowData_CB);

	evhCMD_KEY PlotMap_Key(mfsvrPLOTMAP_CMD_NAME, mfsvrPLOTMAP_CDF_NAME);
	evhCMD_CALLBACK PlotMap_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::PlotMapCB);
	AddCallback(PlotMap_Key,PlotMap_CB);

	evhCMD_KEY ExploreMC_Key(mfsvrEXPLOREMC_CMD_NAME, mfsvrEXPLOREMC_CDF_NAME);
	evhCMD_CALLBACK ExploreMC_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::ExploreMCCB);
	AddCallback(ExploreMC_Key,ExploreMC_CB);

	evhCMD_KEY SetScale_Key(mfsvrSETSCALE_CMD_NAME, mfsvrSETSCALE_CDF_NAME);
	evhCMD_CALLBACK SetScale_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::SetScaleCB);
	AddCallback(SetScale_Key,SetScale_CB);

	evhCMD_KEY PlotModel_Key(mfsvrPLOTMODEL_CMD_NAME, mfsvrPLOTMODEL_CDF_NAME);
	evhCMD_CALLBACK PlotModel_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::PlotModelCB);
	AddCallback(PlotModel_Key,PlotModel_CB);

	evhCMD_KEY SetLMParam_Key(mfsvrSETLMPARAM_CMD_NAME, mfsvrSETLMPARAM_CDF_NAME);
	evhCMD_CALLBACK SetLMParam_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::SetLMParamCB);
	AddCallback(SetLMParam_Key,SetLMParam_CB);

	evhCMD_KEY InitLMEngine_Key(mfsvrINITLMENGINE_CMD_NAME, mfsvrINITLMENGINE_CDF_NAME);
	evhCMD_CALLBACK InitLMEngine_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::InitLMEngineCB);
	AddCallback(InitLMEngine_Key,InitLMEngine_CB);

	evhCMD_KEY DoNextStep_Key(mfsvrDONEXTSTEP_CMD_NAME, mfsvrDONEXTSTEP_CDF_NAME);
	evhCMD_CALLBACK DoNextStep_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::DoNextStepCB);
	AddCallback(DoNextStep_Key,DoNextStep_CB);

	evhCMD_KEY PlotData_Key(mfsvrPLOTDATA_CMD_NAME, mfsvrPLOTDATA_CDF_NAME);
	evhCMD_CALLBACK PlotData_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::PlotDataCB);
	AddCallback(PlotData_Key,PlotData_CB);

	evhCMD_KEY SetParameter_Key(mfsvrSETPARAMETER_CMD_NAME, mfsvrSETPARAMETER_CDF_NAME);
	evhCMD_CALLBACK SetParameter_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::SetParameterCB);
	AddCallback(SetParameter_Key,SetParameter_CB);

	evhCMD_KEY ShowParameters_Key(mfsvrSHOWPARAMETERS_CMD_NAME, mfsvrSHOWPARAMETERS_CDF_NAME);
	evhCMD_CALLBACK ShowParameters_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::ShowParametersCB);
	AddCallback(ShowParameters_Key,ShowParameters_CB);

	evhCMD_KEY ShowModel_Key(mfsvrSHOWMODEL_CMD_NAME, mfsvrSHOWMODEL_CDF_NAME);
	evhCMD_CALLBACK ShowModel_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::ShowModelCB);
	AddCallback(ShowModel_Key,ShowModel_CB);

	evhCMD_KEY AddModel_Key(mfsvrADDMODEL_CMD_NAME, mfsvrADDMODEL_CDF_NAME);
	evhCMD_CALLBACK AddModel_CB(this, (evhCMD_CB_METHOD)&mfsvrSERVER::AddModelCB);
	AddCallback(AddModel_Key,AddModel_CB);

    evhCMD_KEY key1(mfsvrSETMODEL_CMD_NAME, mfsvrSETMODEL_CDF_NAME);
    evhCMD_CALLBACK cb1(this, (evhCMD_CB_METHOD)&mfsvrSERVER::SetModeCB);
    AddCallback(key1, cb1);
    
	evhCMD_KEY key2(mfsvrSETDATA_CMD_NAME, mfsvrSETDATA_CDF_NAME);
    evhCMD_CALLBACK cb2(this, (evhCMD_CB_METHOD)&mfsvrSERVER::SetDataCB);
    AddCallback(key2, cb2);

	evhCMD_KEY key3(mfsvrGETTIME_CMD_NAME, mfsvrGETTIME_CDF_NAME);
    evhCMD_CALLBACK cb3(this, (evhCMD_CB_METHOD)&mfsvrSERVER::GetTimeCB);
    AddCallback(key3, cb3);
    
    return mcsSUCCESS;
}
 
/**
 * Return the version number of the software.
 */
const char *mfsvrSERVER::GetSwVersion()
{
    return mfsvrVERSION;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
