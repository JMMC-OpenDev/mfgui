/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrServer.cpp,v 1.1 2006-03-31 08:18:49 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Search Calibrators SERVER
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: mfsvrServer.cpp,v 1.1 2006-03-31 08:18:49 gzins Exp $"; 


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
#include "mfsvrSERVER.h"
#include "mfsvrPrivate.h"


/* 
 * Main
 */
int main(int argc, char *argv[])
{
    // Init MCS event server
    mfsvrSERVER server;
    if (server.Init(argc, argv) == mcsFAILURE)
    {
        errCloseStack();
        exit (EXIT_FAILURE);
    }

    // Main loop
    while (server.MainLoop() == mcsFAILURE)
    {
        errDisplayStack();
    }

    // Close MCS services
    server.Disconnect();
    mcsExit();

    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
