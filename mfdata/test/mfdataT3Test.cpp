/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataT3Test.cpp,v 1.1 2006-07-05 14:04:07 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 * brief description of the program, which ends at this dot.
 *
 * @synopsis
 * \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
 *                     [\e \<option1\> ... \e \<optionN\>] 
 *
 * @param param1 : description of parameter 1, if it exists
 * @param paramN : description of parameter N, if it exists
 *
 * @opt
 * @optname option1 : description of option 1, if it exists
 * @optname optionN : description of option N, if it exists
 * 
 * @details
 * OPTIONAL detailed description of the c main file follows here.
 * 
 * @usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * @filename fileName1 :  usage description of fileName1
 * @filename fileName2 :  usage description of fileName2
 *
 * @env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * @envvar envVar1 :  usage description of envVar1
 * @envvar envVar2 :  usage description of envVar2
 * 
 * @warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * @ex
 * OPTIONAL. Command example if needed
 * \n Brief example description.
 * @code
 * Insert your command example here
 * @endcode
 *
 * @sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * @sa <entity to refer>
 * 
 * @bug OPTIONAL. Known bugs list if it exists.
 * @bug Bug 1 : bug 1 description
 *
 * @todo OPTIONAL. Things to forsee list, if needed. 
 * @todo Action 1 : action 1 description
 * 
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfdataT3Test.cpp,v 1.1 2006-07-05 14:04:07 lsauge Exp $"; 

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
#include "mfdata.h"
#include "mfdataPrivate.h"


/*
 * Local Variables
 */

char usage[]="Usage: %s <filemane>\n";
 

/* 
 * Signal catching functions  
 */

/* Prototypes
 * */
mcsCOMPL_STAT TestDriver(int argc, char *argv[]) ;

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

    //
    // Insert your code here
    //
    if (argc > 1)
    {
        (void) TestDriver(argc,argv); 
    }
    else
    {
        fprintf(stderr,usage,argv[0]);
    }

    // Close MCS services
    mcsExit();
    
    // Exit from the application with SUCCESS
    exit (EXIT_SUCCESS);
}

mcsCOMPL_STAT
TestDriver(int argc, char *argv[])
{
    // local declaration 
    mcsCOMPL_STAT status;
    
    // instantiate new data structure
    mfdataINPUT_DATA_LIST *data = new mfdataINPUT_DATA_LIST(); 
    
    // get data from oifits
    status = data->ExtractT3DataFromOIFITS(argv[1]);
   
    ///////////////////////////////
    // DISPLAY SOME INFOS
    ///////////////////////////////
    {
    
        // Get the number of data set 
        mcsINT16 nbOfDataSet = data->GetNbOfDataSet() ;

        for (mcsINT16 tableIdx=0 ; tableIdx<nbOfDataSet ; tableIdx ++)
        {
            printf("Subtable #%d\n",tableIdx);

            // Access to the next data set
            mfdataINPUT_DATA *ucastTable = data->GetDataSet (tableIdx) ;

            if( ucastTable == NULL )
            {
                logError("Get not get access to data table. Abort.");
                return mcsFAILURE ;
            }

            if(ucastTable->GetType() == mfdataINPUT_T3_TYPE)
            {
                // correctly cast the data structure
                mfdataINPUT_T3 *table = (mfdataINPUT_T3*) ucastTable;  

                mcsDOUBLE *t3Amp ;   
                mcsDOUBLE *t3AmpErr ;
                mcsDOUBLE *t3Phi ;   
                mcsDOUBLE *t3PhiErr;
                mcsDOUBLE *u1Coord ;
                mcsDOUBLE *v1Coord ;
                mcsDOUBLE *u2Coord ;
                mcsDOUBLE *v2Coord ;

                // Get bispectrum components
                table->GetT3Amp    (&t3Amp   );
                table->GetT3AmpErr (&t3AmpErr);
                table->GetT3Phi    (&t3Phi   );
                table->GetT3PhiErr (&t3PhiErr);
                table->GetU1Coord  (&u1Coord );
                table->GetV1Coord  (&v1Coord );
                table->GetU2Coord  (&u2Coord );
                table->GetV2Coord  (&v2Coord );

                // Print infos
                for (mcsINT16 idx=0 ; idx<table->GetNbOfData() ; idx++)
                {
                    printf(" %04d  ",idx);
                    printf(" (% 7.4e,% 7.4e) ",*(u1Coord+idx),*(v1Coord+idx));        
                    printf(" (% 7.4e,% 7.4e) ",*(u2Coord+idx),*(v2Coord+idx));        
                    printf(" Ampl. % 7.4e+/-% 7.4e ",*(t3Amp+idx),*(t3AmpErr+idx));        
                    printf(" Phase % 7.4e+/-% 7.4e ",*(t3Phi+idx),*(t3PhiErr+idx));        
                    printf("\n");
                }
            }
        }
    }

    // all jobs done. clean-up and exit
    delete (data);
    return status;    
}
    
/*___oOo___*/
