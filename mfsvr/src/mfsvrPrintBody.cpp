/*******************************************************************************
 * JMMC project
 *
 * "@(#) $"
 *
 * History
 * -------
 * $
 *
 ******************************************************************************/

/**
 * @file
 * Definition of PrintBody method.
 */

static char *rcsId="@(#) $"; 
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
#include "mfsvrSERVER.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#include <stdarg.h>

/*!
 *  Display a formatted output on the user console. Similar to printf
 *  and other C standard output methods.
 *  @param  msg address to the current message service
 *  @param  fmt string format
 *  @param  ... variable number of argument to be printed
 *  @return Return the completion status of the SendReply method (evhCB_FAILURE
 *  or evhCB_SUCCESS). 
 * */
evhCB_COMPL_STAT 
mfsvrSERVER::PrintBody(msgMESSAGE &msg, mcsLOGICAL lastReply, char *fmt, ...) 
{
    // Init buffer string
    char buffer[1024];
    buffer[0] = '\0';

    va_list argPtr;
    
    // Get ptr to beginning of argument array 
    va_start(argPtr,fmt);

    // Build the string
    vsprintf(buffer, fmt, argPtr);
    
    // Set Body and Send Reply  
    msg.SetBody(buffer);
    if (SendReply(msg, lastReply) == mcsFAILURE)
    {
        va_end(argPtr);
        logError("mfsvrSERVER::PrintBody() : SendReply() caused an error.");
        return evhCB_FAILURE;
    }
    
    va_end(argPtr);
    return evhCB_SUCCESS;

}

/*___oOo___*/


