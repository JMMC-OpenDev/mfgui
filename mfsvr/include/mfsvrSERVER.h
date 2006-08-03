#ifndef mfsvrSERVER_H
#define mfsvrSERVER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSERVER.h,v 1.10 2006-08-03 14:19:52 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2006/08/02 09:03:26  lsauge
 * updates
 *
 * Revision 1.8  2006/07/11 11:16:58  lsauge
 * add RegisterFileForRemoving() and RemoveFiles() methods ;
 *
 * Revision 1.7  2006/07/10 12:27:10  lsauge
 * Minor changes
 *
 * Revision 1.6  2006/07/05 14:52:38  lsauge
 * Minor changes due to third party updates
 *
 * Revision 1.5  2006/07/04 08:27:36  lsauge
 * updated
 *
 * Revision 1.4  2006/06/28 12:36:57  lsauge
 * update
 *
 * Revision 1.3  2006/06/27 09:06:23  lsauge
 * Update following third party call-back implementation
 *
 * Revision 1.1  2006/03/31 08:19:31  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 * mfsvrSERVER class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#define SEM printf("SEMAPHORE %s %d\n",__FILE__,__LINE__)

/*
 * MCS Headers 
 */
#include "evh.h"

/*
 * FIT Headers 
 */
#include "fiteng.h"
#include "mfmdl.h"
#include "mfdata.h"

/*
 * Class declaration
 */
/**
 * Main class for the model-fitting server.
 *
 * This class is the event handler of the model-fitting server; i.e. it handles
 * all external commands intended to this application.
 */
class mfsvrSERVER : public evhSERVER 
{

public:
    // Constructor
    mfsvrSERVER();

    // Destructor
    virtual ~mfsvrSERVER();
    
    // Application initialization 
    virtual mcsCOMPL_STAT AppInit();
    
    // Software version 
    virtual const char *GetSwVersion();

    // Command callbacks
	virtual evhCB_COMPL_STAT ShowDataCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT PlotMapCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT ExploreMCCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT SetScaleCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT PlotModelCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT SetLMParamCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT InitLMEngineCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT DoNextStepCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT PlotDataCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT SetParameterCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT ShowParametersCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT ShowModelCB(msgMESSAGE &msg, void*);
	virtual evhCB_COMPL_STAT AddModelCB(msgMESSAGE &msg, void*);
    virtual evhCB_COMPL_STAT SetModeCB(msgMESSAGE &msg, void*);
    virtual evhCB_COMPL_STAT SetDataCB(msgMESSAGE &msg, void*);
    virtual evhCB_COMPL_STAT GetTimeCB(msgMESSAGE &msg, void*);
   
    // other method defined elsewhere
    mcsCOMPL_STAT CopyParameters_ModelToEngine() ;
    mcsCOMPL_STAT CopyParameters_EnginetoModel() ;
    mcsCOMPL_STAT BuildParameterList( msgMESSAGE &msg ) ;
    mcsCOMPL_STAT CheckNormalization() ;

    mcsCOMPL_STAT CreatePlot() ;
    mcsCOMPL_STAT DeletePlot() ; 
    mcsCOMPL_STAT PlotSendCommand(const char *fmt,...) ;

    // Managing temporary file (mfvsrPlotManaging.cpp)
    mcsCOMPL_STAT RegisterFileForRemoving(const char *file) ;
    mcsCOMPL_STAT RemoveFiles() ;

	// 
    virtual mcsCOMPL_STAT ExpandModelTree(mfmdlCOMPOSED_MODEL *model) ;
	virtual mcsCOMPL_STAT DumpParameters(mfmdlMODEL *model) ;

    virtual mcsCOMPL_STAT EvaluateResiduals() ;
    virtual mcsCOMPL_STAT InitLMEngine(msgMESSAGE msg) ; 

    virtual mcsCOMPL_STAT ResetEngine() { _iter = 0; } ;
    
    // Print Body method
    virtual evhCB_COMPL_STAT PrintBody (msgMESSAGE &msg, mcsLOGICAL lastReply, char *fmt, ...);
    
protected:

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfsvrSERVER(const mfsvrSERVER&);
    mfsvrSERVER& operator=(const mfsvrSERVER&); 

    /* Fitting engine */
    fitengLM_ENGINE     *_fitEngine;

    /* Model */
    mfmdlCOMPOSED_MODEL  *_model;
    
    /* Parameters */
    fitengPARAMETER_LIST *_params;
    
    /* Data */
    mfdataINPUT_DATA_LIST  *_data;

    /* Residues */
    fitengRESIDUE_LIST   *_residues;

    /* Gnuplot Pipe */
    FILE *_pPlot;
    
    /* List of temporary file */
    std::vector<string> _fileList; 

    /* Number of degrees of freedom */
    mcsUINT16 _dof ;

    /* Number of performed iterations */
    mcsUINT16 _iter ;
};

#endif /*!mfsvrSERVER_H*/

/*___oOo___*/
