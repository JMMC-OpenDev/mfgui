/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlTest1.cpp,v 1.2 2006-05-11 13:04:56 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/09/08 14:06:37  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 * Some test lines of mfmdl object use
 *
 * f1, f2 , f3 and f4 are main example functions called by main.
 * lit_job_eval is used to assert that user can handle every member of a complex
 * model to do every needed treatments.
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlTest1.cpp,v 1.2 2006-05-11 13:04:56 mella Exp $";

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


/*
 * Do some stupid things.
 */
mcsCOMPL_STAT lit_job_eval(mfmdlMODEL & cModel)
{
    logTrace("lit_job_eval()");
    cout << "lit_job_eval()";
    
    // prepare u v to be able to run Eval
    mcsDOUBLE u[10];
    mcsDOUBLE v[10];
    mcsDOUBLE time[10];
    mcsDOUBLE mjd[10];
    mcsDOUBLE wave[10];
    mcsDOUBLE result[10];
    for(int i=0; i < 10; i++)
    {
        u[i]=v[i]=i;
    }

       
    cModel.Eval(u, v, time, mjd, wave, 10, result);
    cout << "cModel.Eval(u, v, time, mjd, wave, 10, result)" << endl; 

    for(int i=0; i < 10; i++)
    {
        cout << i << " : u, v = " << u[i] << "," << v[i]
            << " -> result =" <<result[i] << endl; 
        
    }
   
    
    return mcsSUCCESS;
}

void f1(void)
{
    // TEST BLOCK 1 is used to play with construction and
    // to call dummy lit_job_eval()
    cout << "// TEST BLOCK 1" << endl << endl;

    // Build a composite model
    mfmdlMODEL * m1 = new mfmdlMODEL("simple model m1");
    mfmdlMODEL * m2 = new mfmdlMODEL("simple model m2");
    mfmdlCOMPOSED_MODEL myModel1("composite model myModel1");
    myModel1.Add(m1);
    myModel1.Add(m2);

    // Print some informations of model simple model 1
    cout << "m1:" << endl << m1->GetInfo();
    // and composite model 1
    cout << "myModel1:" << endl ;
    cout << myModel1.GetInfo();

    // give composed model 1 to an external function.
    lit_job_eval(myModel1);

    
    // Construct one composite model based on previous one
    mfmdlCOMPOSED_MODEL myModel2("composite model myModel2");
    mfmdlMODEL * m3 = new mfmdlMODEL("simple model m3");
    myModel2.Add(&myModel1);
    myModel2.Add(m3);

    // Print some informations of composite model 2
    cout << "myModel2:" << endl ;
    cout << myModel2.GetInfo();
    cout << endl ;

    // give composed model 2 to an external function.
    lit_job_eval(myModel2);
}


void f2()
{
    // TEST BLOCK 2 is used to play with parameters
    cout << "// TEST BLOCK 2" << endl << endl;

    // Build a composite model
    mfmdlMODEL m1("simple model m1");
    mfmdlMODEL m2("simple model m2");
    mfmdlCOMPOSED_MODEL myModel1("composite model myModel1");
    myModel1.Add(&m1);
    myModel1.Add(&m2);

    // Add parameter to basic models
    mfmdlPARAMETER p1_1("parameter 1_1");
    m1.AddParameter(&p1_1);
    p1_1.SetValue(11);

    mfmdlPARAMETER p1_2("parameter 1_2");
    m1.AddParameter(&p1_2);
    p1_2.SetValue(12);

    mfmdlPARAMETER p2_1("parameter 2_1");
    m2.AddParameter(&p2_1);
    p2_1.SetValue(21);

    // Get myModel1's parameters
    mfmdlPARAMETER_LIST params;
    myModel1.GetParameters(&params);

    cout << "Playing with myModel1 parameters" << endl ;
    mfmdlPARAMETER *p;
    for(unsigned int i = 0; i < params.size(); i++)
    {
        p=params[i];
        cout << "- param " << i << " '"<< p->GetName()<<"' = "
            << p->GetValue() << endl;
    }

}

void f4()
{
    // TEST BLOCK 4 is used to play with ponctual model
    cout << "// TEST BLOCK 4" << endl << endl;

    // Build a composite model
    mfmdlPONCT ponctObj("ponctual model");
    mfmdlCOMPOSED_MODEL myModel1("composite model myModel1");
    myModel1.Add(&ponctObj);

    // Get and Modify intensityCoefficient, and notDefined parameters
    // of ponctualObject model
    mfmdlPARAMETER *param;
    if(ponctObj.GetParameter("intensityCoefficient", &param) == mcsFAILURE)
    {
        errCloseStack();
        return;
    }
    param->SetValue(1.1);
    
    if(ponctObj.GetParameter("notDefined", &param) == mcsFAILURE)
    {
        // Should always go into this branch and display error message
        // on stdout
        errCloseStack();
    }
    
    // Get myModel1's parameters
    mfmdlPARAMETER_LIST params;
    myModel1.GetParameters(&params);

    cout << "Playing with myModel1 parameters" << endl ;
    mfmdlPARAMETER *p;
    cout << "Nb of params " << params.size() << endl;
    for(unsigned int i = 0; i < params.size(); i++)
    {
        p=(mfmdlPARAMETER *)params[i];
        cout << "- param " << i << " '"<< p->GetName()<<"' = "
            << p->GetValue() << endl;
    }

}


void f3()
{
    // TEST BLOCK 3 is used to play with ponctual model
    cout << "// TEST BLOCK 3" << endl << endl;

    // Build a composite model
    mfmdlMODEL m1("simple model m1", mcsTRUE);
    mfmdlMODEL m2("simple model m2");
    mfmdlPONCT m3("ponctual model m3");
    mfmdlCOMPOSED_MODEL myModel1("composite model myModel1");
    myModel1.Add(&m1);
    myModel1.Add(&m2);
    myModel1.Add(&m3);

    // Add parameters to basic models
    mfmdlPARAMETER p1_1("parameter 1_1");
    m1.AddParameter(&p1_1);
    p1_1.SetValue(11);

    mfmdlPARAMETER p2_1("parameter 2_1");
    m2.AddParameter(&p2_1);
    p2_1.SetValue(21);

    mfmdlPARAMETER p3_1("parameter 3_1");
    m3.AddParameter(&p3_1);
    p3_1.SetValue(31);

    // Get info of composite model 1
    cout << "myModel1:" << endl << myModel1.GetInfo();

    // Get myModel1's parameters
    mfmdlPARAMETER_LIST params;
    myModel1.GetParameters(&params);

    cout << "Playing with myModel1 parameters" << endl ;
    mfmdlPARAMETER *p;
    cout << "Nb of params " << params.size() << endl;
    for(unsigned int i = 0; i < params.size(); i++)
    {
        p=(mfmdlPARAMETER *)params[i];
        cout << "- param " << i << " '"<< p->GetName()<<"' = "
            << p->GetValue() << endl;
    }

}

/* 
 * Main
 */

int main(int argc, char *argv[])
{
    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Error handling if necessary

        // Exit from the application with FAILURE
        exit (EXIT_FAILURE);
    }

    // Set high verbosity
    //logSetStdoutLogLevel(logTRACE);

    /*
     */
    f1();    
    f2();    
    f3(); 
    f4(); 



    // Close MCS services
    mcsExit();

    // Exit from the application with SUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
