/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrPlotManaging.cpp,v 1.4 2006-07-11 11:18:14 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/07/10 12:21:01  lsauge
 * Add a printf command for debugging purpose
 *
 * Revision 1.2  2006/06/28 13:40:19  lsauge
 * Suppress debug output
 *
 * Revision 1.1  2006/06/28 12:35:36  lsauge
 * First implementation
 *
 *
 ******************************************************************************/

/**
 * @file
 * Plot managing methods (using gnuplot) 
 */

static char *rcsId="@(#) $Id: mfsvrPlotManaging.cpp,v 1.4 2006-07-11 11:18:14 lsauge Exp $"; 
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

mcsCOMPL_STAT mfsvrSERVER::CreatePlot()
{
    if ( _pPlot == NULL )
    {
        _pPlot = popen("gnuplot","w");
    }
    if ( _pPlot == NULL )
    {
        return mcsFAILURE;
    }
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfsvrSERVER::DeletePlot()
{
    if( _pPlot != NULL )
    {
        fclose ( _pPlot );
        _pPlot = NULL ;
    }
    (void) RemoveFiles();
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfsvrSERVER::PlotSendCommand(const char *fmt,...)
{
    // Init buffer string
    char buffer[1024];
    buffer[0] = '\0';

    va_list argPtr;

    // Get ptr to beginning of argument array 
    va_start(argPtr,fmt);
    // Build the string
    vsprintf(buffer, fmt, argPtr);
   
    // if plotting area is not amready open, create it
    if ( _pPlot == NULL)
    {
        if ( this->CreatePlot() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    fputs(buffer,_pPlot) ;
    fflush(_pPlot);
    printf(buffer) ;

    return mcsSUCCESS;
}

mcsCOMPL_STAT mfsvrSERVER::RegisterFileForRemoving(const char *file)
{
    logInfo("Register file for removing: %s",file);
    _fileList.push_back(file);
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfsvrSERVER::RemoveFiles()
{

    for (mcsUINT16 idx=0 ; idx<_fileList.size() ; idx++)
    {
        char *fileName = NULL;
        fileName = strdup((_fileList.front()).c_str());

        if (fileName != NULL)
        {
            // Remove temporary file
            logInfo("Remove temporary file %s",fileName);        
            if (unlink(fileName) != 0)
            {
                logError("Cannot remove temporary file.");
                return mcsFAILURE;
            }
            _fileList.erase(_fileList.begin());
        }
        else
        {
                logError("Cannot allocate memory.");
                return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}


/*___oOo___*/
