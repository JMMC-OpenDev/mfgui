/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_DATA_LIST.cpp,v 1.5 2006-08-02 08:12:17 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/07/12 12:14:20  lsauge
 * add one line of code in order to provide phase value lying into [-pi,pi]
 *
 * Revision 1.3  2006/07/10 12:15:15  lsauge
 * look for NaN in data file
 *
 * Revision 1.2  2006/07/05 14:06:26  lsauge
 * Add ExtractT3DataFromOIFITS method
 * remove dead code
 *
 * Revision 1.1  2006/06/27 12:05:36  lsauge
 * First import
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of mfdataINPUT_DATA_LIST* class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfdataINPUT_DATA_LIST.cpp,v 1.5 2006-08-02 08:12:17 lsauge Exp $"; 

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
#include "mfdata.h"
#include "mfdataINPUT_DATA_LIST.h"
#include "mfdataPrivate.h"

/**
 * Class constructor
 */
mfdataINPUT_DATA_LIST::mfdataINPUT_DATA_LIST()
{
}

/**
 * Class destructor
 */
mfdataINPUT_DATA_LIST::~mfdataINPUT_DATA_LIST()
{
    // go through the vector and delete DATA_INPUT element
    //this->Clear();
}

/*
 * Public methods
 */

mcsLOGICAL mfdataINPUT_DATA_LIST::IsEmpty()
{
    logTrace("mfdataINPUT_DATA_LIST::IsEmpty()");

    if (_inputDataList.empty() == false)
    {
        return mcsFALSE;
    }

    return mcsTRUE;
}

mcsCOMPL_STAT mfdataINPUT_DATA_LIST::Clear()
{
    for (mcsUINT32 idx = 0; idx< _inputDataList.size() ; idx++)
    {
        delete(_inputDataList[idx]);
    }
    
    // Clear list and set to NULL
    _inputDataList.clear();

    return mcsSUCCESS;
}


/*!
  The purpose of this function is to retrieve all data necessary to fill
  the INPUTDATA object from an OIFITS raw data file.

  @note This function 
  -   opens the file
  -   reads data and paremters from it
  -   allocates and initializes memory block to store data within
  -   correctly sets and fills the INPUTDATA object
  -   closes the file.

  @param    filename    path to OIFITS file
  @return   mcsSUCCESS under successfull completion, mcsFAILURE otherwise
*/

mcsCOMPL_STAT 
mfdataINPUT_DATA_LIST::ExtractVIS2DataFromOIFITS(mcsSTRING256 filename)
/**
 * This function extract all VIS2 table from an OIFITS file
 */
{
    /* Local Declaration */
    mcsCOMPL_STAT readStat; 
    mcsINT16 nbOfRows;
    mcsINT16 nbOfWaves;

    /* Create a new workspace
     * in order to save OIFITS data 
     * */
    oidataFITS *wks = new oidataFITS;

    /* Open and load file
     **/
    mcsLOGICAL  stopForBadFormat = mcsFALSE;
    cout<<"opening file ["<<filename<<"] ...";
    readStat=wks->Load(filename,stopForBadFormat);

    /* successfull opening ... */
    if(readStat==mcsSUCCESS)
    {
        cout<<" done."<<endl;
    } 
    /* or failure - 
     * In this case return FAILURE and exit*/
    else 
    {
        cout<<" error."<<endl;
        cerr<<"oidata::Load returned an error."<<endl;
        delete wks;
        return(mcsFAILURE);
    }
    /* And this step, the OIFITS file is expected to be correctly open.
     * */
    
    /* First, get the number of tables
     * */
    mcsINT16 nbOfOiTable = 0; /* Total number of tables */
    if(wks->GetNumberOfOiTable(&nbOfOiTable) == mcsFAILURE)
    {
        printf("Error getting nb of tables\n");
        return(mcsFAILURE);
    }
    printf("%s contains %d OI tables\n", filename, nbOfOiTable);

    /* Among all table, retrieve all VIS2 table
     * */
    oidataOI_TABLE *table;      /* pointer to table for reference */
    mcsINT16 nbOfVisTable=0;    /* number of VIS table (should be incremented) */
    mcsINT16 tableIndex;        /* simple index */
    
    for(tableIndex = 0; tableIndex < nbOfOiTable; tableIndex++)
    {
        /* 
         * Get table 
         */
        if(wks->GetOiTable(&table, tableIndex) == mcsFAILURE)
        {
            printf("Error getting reference of oiTable number %d\n",tableIndex);
            return(mcsFAILURE);
        }           
        
        /* Retrieve the table's type */ 
        oidataTABLE_TYPE type = table->GetType();
        
        /* Print table's infos */
        cout<<"Table #"<<tableIndex<<" - type: "<<type<<endl;

        /* This table a VIS2 one */
        if(type == OI_VIS2_TYPE)
        {
	        /* pointer to oidataOI_VIS2 for reference */
            oidataOI_VIS2 *oiVis2=(oidataOI_VIS2*)table;
            
            /* Increment the nbOfVisTable counter */
            nbOfVisTable++;
            cout<<"\tfind VIS2 table at position "
                <<tableIndex
                <<" ["<<nbOfVisTable<<"]"
                <<endl;
            
            /////////////////////////////////////////////////////////
            // NUMBER OF DATA (CORRESPONDING TO NUMBER OF ROWS) 
            /////////////////////////////////////////////////////////
                
            /* Get the number of rows */
            nbOfRows = oiVis2->GetNumberOfRows();
            cout<<"\tnumber of rows ="<<nbOfRows<<endl;
            
	
            /////////////////////////////////////////////////////////
            // TARGET ID 
            /////////////////////////////////////////////////////////
           
            /* Get Taget Id 
             * Conversely to the OIFITS format, only one target ID is
             * allowed per object. In case of multiple sources the data set
             * must be split. This feature is not yet implemented. 
             * */
            mcsINT16 *targetIdList = (mcsINT16*)malloc(nbOfRows*sizeof(mcsINT16));
		    if(oiVis2->GetTargetId(targetIdList) == mcsFAILURE)
		    {
    		    printf("Error getting wavelength reference \n");
    		    return(mcsFAILURE);
		    }

            mcsINT16 i;
            for( i=1 ; i<nbOfRows ;  i++)
            {
                if( *(targetIdList) != *(targetIdList+i) )
                {
    		        printf("Error: data in this VIS2 table refer to differents TARGETID. \n");
    		        printf("       This features is not implemented int this version.   \n");
    		        return(mcsFAILURE);
                }
            }
            
            /////////////////////////////////////////////////////////
            // OI_WAVENLENGTH TABLE
            /////////////////////////////////////////////////////////
            
            oidataOI_WAVELENGTH *OiWavelength;
		    if(oiVis2->GetOiWavelengthTable(&OiWavelength) == mcsFAILURE)
		    {
    		    printf("Error getting wavelength reference \n");
    		    return(mcsFAILURE);
		    }
            
            /////////////////////////////////////////////////////////
            // NUMBER OF SPECTRSCOPIC CHANNEL
            /////////////////////////////////////////////////////////

            /* then, get the number of waves */
            nbOfWaves = OiWavelength->GetNumberOfRows();
            cout<<"\tnumber of waves="<<nbOfWaves<<endl;
            
            /////////////////////////////////////////////////////////
            // EFFECTIVE WAVELENGTH OF SPECTROSCOPIC CHANNEL
            /////////////////////////////////////////////////////////
            
            /* and the effective wavelength of each channel */
	        mcsFLOAT  *fEffWaves=(mcsFLOAT*) malloc(nbOfWaves*sizeof(mcsFLOAT));
	        mcsDOUBLE *dEffWaves=(mcsDOUBLE*)malloc(nbOfWaves*sizeof(mcsDOUBLE));
            if( OiWavelength->GetEffWave (fEffWaves) == mcsFAILURE)
            {
    		    printf("Error getting effective wavelength values \n");
    		    return(mcsFAILURE);
            }
            /* Explicit conversion float -> double */
            for( i=0 ; i<nbOfWaves ; i++)
            {
                *(dEffWaves+i) = (mcsDOUBLE)(*(fEffWaves+i));
            }
            free(fEffWaves);

            /////////////////////////////////////////////////////////
            // (U,V) COORDINATES 
            /////////////////////////////////////////////////////////
	
            /* get (u,v) coordinates */
            mcsDOUBLE *uCoord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            mcsDOUBLE *vCoord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            if( (oiVis2->GetUCoord(uCoord) == mcsFAILURE)||
                (oiVis2->GetVCoord(vCoord) == mcsFAILURE))
            {
                printf("Error getting (u,v) coordinates \n");
                return(mcsFAILURE);
            }
            
            /////////////////////////////////////////////////////////
            // SQUARED VISIBILITIES AND ERROR 
            /////////////////////////////////////////////////////////
        
            /* Retrieve squared visibilities and errors */
            mcsDOUBLE *vis2Data=(mcsDOUBLE*)malloc(
                nbOfRows*nbOfWaves*sizeof(mcsDOUBLE)
                );
            if( vis2Data == NULL )
            {
                printf("Error allocating memory \n");
                return(mcsFAILURE);

            };
            mcsDOUBLE *vis2Err =(mcsDOUBLE*)malloc(
                nbOfRows*nbOfWaves*sizeof(mcsDOUBLE)
                );
            if( (oiVis2->GetVis2Err (vis2Err ) == mcsFAILURE)||
                (oiVis2->GetVis2Data(vis2Data) == mcsFAILURE))
            {
                printf("Error getting (VIS2,err) data \n");
                return(mcsFAILURE);
            }
            
            for( i=0 ; i<nbOfRows*nbOfWaves ; i++)
            {
                if(*(vis2Err+i) == 0.0)
                {
                    logError("Ohhh ... I'm in trouble, error=0. Replaced by 0.001\n");
                    *(vis2Err+i) = 0.001; 
                }
            }
            
            /////////////////////////////////////////////////////////
            // UTC START DATE OF OBSERVATION 
            /////////////////////////////////////////////////////////
            mcsSTRING32 dateObs;
            if( oiVis2->GetDateObs(dateObs) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            cout<<"\tObservation date: "<<dateObs<<" (UTC)"<<endl;;
            
            /////////////////////////////////////////////////////////
            // GETTING ARRNAME 
            /////////////////////////////////////////////////////////
            
            mcsSTRING32 arrName;
            if( oiVis2->GetArrName(arrName) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            cout<<"\tArr Name: "<<arrName<<endl;;
            
            /////////////////////////////////////////////////////////
            // GETTING INSNAME 
            /////////////////////////////////////////////////////////
            
            mcsSTRING32 insName;
            if( oiVis2->GetInsName(insName) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            cout<<"\tIns Name: "<<insName<<endl;;
            
            /////////////////////////////////////////////////////////
            // INTEGRATION TIME                      
            /////////////////////////////////////////////////////////
            
            mcsDOUBLE *intTime=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            if( oiVis2->GetIntTime(intTime) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            
            /////////////////////////////////////////////////////////
            // UTC TIME OF INDIVIDUAL OBSERVATIONS
            /////////////////////////////////////////////////////////
            
            mcsDOUBLE *obsTime=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            if( oiVis2->GetTime(obsTime) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            
            ////////////////////////////////////////////////////
            // HERE INSERT THE CODE SET THE INPUTDATA OBJECTS //
            ////////////////////////////////////////////////////
        
            // Fill one array per wavelength
            // UVCoords is the true UVcoords, not the correponding baseline
            for (i=0 ; i<nbOfWaves; i++)
            {
                // instantiate a new data structure
                mfdataINPUT_VIS2 *dataVIS2 = new mfdataINPUT_VIS2(); 
                /* set data references */ 
                mcsCOMPL_STAT status;
                status = dataVIS2->SetDataDimensions (nbOfRows,1);
                status = dataVIS2->SetDataRefs (dateObs, arrName, insName,
                                                *(targetIdList),
                                                dEffWaves+i, obsTime, intTime);


                mcsDOUBLE *true_uCoord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
                mcsDOUBLE *true_vCoord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
                for (mcsUINT16 j=0 ; j<nbOfRows; j++)
                {
                    *(true_uCoord+j)= *(uCoord+j) / *(dEffWaves+i);
                    *(true_vCoord+j)= *(vCoord+j) / *(dEffWaves+i);
                }

                status = dataVIS2->SetVis2Refs ( 
                        true_uCoord, 
                        true_vCoord, 
                        (vis2Data+i*nbOfRows), 
                        (vis2Err +i*nbOfRows)
                        );
                /* Add data to sequencer data container */
                status = this->AddInputData(dataVIS2);
            }

            free(uCoord);
            free(vCoord);

        } /* Endif */
    } /* Endfor - tableIndex */
        
    /* All work should be done now.
     * We shall release de the default working space closing the file.
     * */
    
    delete wks;

    /* Successful completion */
    return(mcsSUCCESS);
}


/*!
  The purpose of this function is to retrieve all data necessary to fill
  the INPUTDATA object from an OIFITS raw data file.

  @note This function 
  -   opens the file
  -   reads data and paremters from it
  -   allocates and initializes memory block to store data within
  -   correctly sets and fills the INPUTDATA object
  -   closes the file.

  @param    filename    path to OIFITS file
  @return   mcsSUCCESS under successfull completion, mcsFAILURE otherwise
*/

mcsCOMPL_STAT 
mfdataINPUT_DATA_LIST::ExtractT3DataFromOIFITS(mcsSTRING256 filename)
/**
 * This function extract all VIS2 table from an OIFITS file
 */
{
    /* Local Declaration */
    mcsCOMPL_STAT readStat; 
    mcsINT16 nbOfRows;
    mcsINT16 nbOfWaves;

    /* Create a new workspace
     * in order to save OIFITS data 
     * */
    oidataFITS *wks = new oidataFITS;

    /* Open and load file
     **/
    mcsLOGICAL  stopForBadFormat = mcsFALSE;
    cout<<"opening file ["<<filename<<"] ...";
    readStat=wks->Load(filename,stopForBadFormat);

    /* successfull opening ... */
    if(readStat==mcsSUCCESS)
    {
        cout<<" done."<<endl;
    } 
    /* or failure - 
     * In this case return FAILURE and exit*/
    else 
    {
        cout<<" error."<<endl;
        cerr<<"oidata::Load returned an error."<<endl;
        delete wks;
        return(mcsFAILURE);
    }
    /* And this step, the OIFITS file is expected to be correctly open.
     * */
    
    /* First, get the number of tables
     * */
    mcsINT16 nbOfOiTable = 0; /* Total number of tables */
    if(wks->GetNumberOfOiTable(&nbOfOiTable) == mcsFAILURE)
    {
        printf("Error getting nb of tables\n");
        return(mcsFAILURE);
    }
    printf("%s contains %d OI tables\n", filename, nbOfOiTable);

    /* Among all table, retrieve all VIS2 table
     * */
    oidataOI_TABLE *table;      /* pointer to table for reference */
    mcsINT16 nbOfVisTable=0;    /* number of VIS table (should be incremented) */
    mcsINT16 tableIndex;        /* simple index */
    
    for(tableIndex = 0; tableIndex < nbOfOiTable; tableIndex++)
    {
        /* 
         * Get table 
         */
        if(wks->GetOiTable(&table, tableIndex) == mcsFAILURE)
        {
            printf("Error getting reference of oiTable number %d\n",tableIndex);
            return(mcsFAILURE);
        }           
        
        /* Retrieve the table's type */ 
        oidataTABLE_TYPE type = table->GetType();
        
        /* This table a VIS2 one */
        if(type == OI_T3_TYPE)
        {
	        /* pointer to oidataOI_VIS2 for reference */
            oidataOI_T3 *oiT3=(oidataOI_T3*)table;
            
            /* Increment the nbOfVisTable counter */
            nbOfVisTable++;
            cout<<"\tfind T3 table at position "
                <<tableIndex
                <<" ["<<nbOfVisTable<<"]"
                <<endl;
            
            /////////////////////////////////////////////////////////
            // NUMBER OF DATA (CORRESPONDING TO NUMBER OF ROWS) 
            /////////////////////////////////////////////////////////
                
            /* Get the number of rows */
            nbOfRows = oiT3->GetNumberOfRows();
            cout<<"\tnumber of rows ="<<nbOfRows<<endl;
            
	
            /////////////////////////////////////////////////////////
            // TARGET ID 
            /////////////////////////////////////////////////////////
           
            /* Get Taget Id 
             * Conversely to the OIFITS format, only one target ID is
             * allowed per object. In case of multiple sources the data set
             * must be split. This feature is not yet implemented. 
             * */
            mcsINT16 *targetIdList = (mcsINT16*)malloc(nbOfRows*sizeof(mcsINT16));
		    if(oiT3->GetTargetId(targetIdList) == mcsFAILURE)
		    {
    		    printf("Error getting wavelength reference \n");
    		    return(mcsFAILURE);
		    }

            mcsINT16 i;
            for( i=1 ; i<nbOfRows ;  i++)
            {
                if( *(targetIdList) != *(targetIdList+i) )
                {
    		        printf("Error: data in this T3 table refer to differents TARGETID. \n");
    		        printf("       This features is not implemented int this version.   \n");
    		        return(mcsFAILURE);
                }
            }
            
            /////////////////////////////////////////////////////////
            // OI_WAVENLENGTH TABLE
            /////////////////////////////////////////////////////////
            
            oidataOI_WAVELENGTH *OiWavelength;
		    if(oiT3->GetOiWavelengthTable(&OiWavelength) == mcsFAILURE)
		    {
    		    printf("Error getting wavelength reference \n");
    		    return(mcsFAILURE);
		    }
            
            /////////////////////////////////////////////////////////
            // NUMBER OF SPECTRSCOPIC CHANNEL
            /////////////////////////////////////////////////////////

            /* then, get the number of waves */
            nbOfWaves = OiWavelength->GetNumberOfRows();
            cout<<"\tnumber of waves="<<nbOfWaves<<endl;
            
            /////////////////////////////////////////////////////////
            // EFFECTIVE WAVELENGTH OF SPECTROSCOPIC CHANNEL
            /////////////////////////////////////////////////////////
            
            /* and the effective wavelength of each channel */
	        mcsFLOAT  *fEffWaves=(mcsFLOAT*) malloc(nbOfWaves*sizeof(mcsFLOAT));
	        mcsDOUBLE *dEffWaves=(mcsDOUBLE*)malloc(nbOfWaves*sizeof(mcsDOUBLE));
            if( OiWavelength->GetEffWave (fEffWaves) == mcsFAILURE)
            {
    		    printf("Error getting effective wavelength values \n");
    		    return(mcsFAILURE);
            }
            /* Explicit conversion float -> double */
            for( i=0 ; i<nbOfWaves ; i++)
            {
                *(dEffWaves+i) = (mcsDOUBLE)(*(fEffWaves+i));
            }
            free(fEffWaves);

            /////////////////////////////////////////////////////////
            // (U,V) COORDINATES 
            /////////////////////////////////////////////////////////
	
            /* get (u,v) coordinates */
            
            mcsDOUBLE *u1Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            mcsDOUBLE *v1Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            mcsDOUBLE *u2Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            mcsDOUBLE *v2Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            
            if( (oiT3->GetU1Coord(u1Coord) == mcsFAILURE)||
                (oiT3->GetV1Coord(v1Coord) == mcsFAILURE)||
                (oiT3->GetU2Coord(u2Coord) == mcsFAILURE)||
                (oiT3->GetV2Coord(v2Coord) == mcsFAILURE)
              )
            {
                printf("Error getting (u,v) coordinates \n");
                return(mcsFAILURE);
            }
            
            /////////////////////////////////////////////////////////
            // BISPECTRUM : AMPLITUDE, PHASE AND CORRESPONDING ERRORS
            /////////////////////////////////////////////////////////
        
            mcsDOUBLE *T3Amp     =(mcsDOUBLE*)malloc(nbOfRows*nbOfWaves*sizeof(mcsDOUBLE));
            mcsDOUBLE *T3AmpErr  =(mcsDOUBLE*)malloc(nbOfRows*nbOfWaves*sizeof(mcsDOUBLE));
            mcsDOUBLE *T3Phase   =(mcsDOUBLE*)malloc(nbOfRows*nbOfWaves*sizeof(mcsDOUBLE));
            mcsDOUBLE *T3PhaseErr=(mcsDOUBLE*)malloc(nbOfRows*nbOfWaves*sizeof(mcsDOUBLE));
            
            
            if((T3Amp      == NULL)|| 
               (T3AmpErr   == NULL)||
               (T3Phase    == NULL)||
               (T3PhaseErr == NULL))
            {
                printf("Error allocating memory \n");
                return(mcsFAILURE);

            };
            
            if( (oiT3->GetT3AmpErr(T3AmpErr  ) == mcsFAILURE)||
                (oiT3->GetT3Amp   (T3Amp     ) == mcsFAILURE)||
                (oiT3->GetT3Phi   (T3Phase   ) == mcsFAILURE)||
                (oiT3->GetT3PhiErr(T3PhaseErr) == mcsFAILURE))
            {
                printf("Error getting bispectrum data \n");
                return(mcsFAILURE);
            }
            
            for( i=0 ; i<nbOfRows*nbOfWaves ; i++)
            {
                // Check the error on amplitude and phase
                mcsDOUBLE phaseErr ;
                mcsDOUBLE ampErr ;
                
                if ((phaseErr = *(T3PhaseErr+i)) == 0.0)
                {
                    logError("Ohhh ... I'm in trouble, error=0.  Correcting data\n");
                    *(T3PhaseErr+i) = 0.05 * *(T3Phase+i); 
                }
                if ( (isnan(phaseErr)!=0) || (isinf(phaseErr)!=0))
                {
                    logError("Ohhh ... I'm in trouble, Nan/Inf found.  Correcting data\n");
                    *(T3PhaseErr+i) = 1e300; 
                    
                }
                
                if ((ampErr=*(T3AmpErr+i)) == 0.0)
                {
                    logError("Ohhh ... I'm in trouble, error=0.  Correcting Data\n");
                    *(T3AmpErr+i) = 0.05 * *(T3Amp+i); 
                }
                if ( (isnan(ampErr)!=0) || (isinf(ampErr)!=0))
                {
                    logError("Ohhh ... I'm in trouble, Nan/Inf found.  Correcting data\n");
                    *(T3AmpErr+i) = 1e300; 
                    
                }

                // phase value, expressed in degrees must lie into [-180,180[
                *(T3Phase+i) = *(T3Phase+i) -360.0*round(*(T3Phase+i)/360.0) ;
                
            }
            
            /////////////////////////////////////////////////////////
            // UTC START DATE OF OBSERVATION 
            /////////////////////////////////////////////////////////
            mcsSTRING32 dateObs;
            if( oiT3->GetDateObs(dateObs) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            cout<<"\tObservation date: "<<dateObs<<" (UTC)"<<endl;;
            
            /////////////////////////////////////////////////////////
            // GETTING ARRNAME 
            /////////////////////////////////////////////////////////
            
            mcsSTRING32 arrName;
            if( oiT3->GetArrName(arrName) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            cout<<"\tArr Name: "<<arrName<<endl;;
            
            /////////////////////////////////////////////////////////
            // GETTING INSNAME 
            /////////////////////////////////////////////////////////
            
            mcsSTRING32 insName;
            if( oiT3->GetInsName(insName) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            cout<<"\tIns Name: "<<insName<<endl;;
            
            /////////////////////////////////////////////////////////
            // INTEGRATION TIME                      
            /////////////////////////////////////////////////////////
            
            mcsDOUBLE *intTime=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            if( oiT3->GetIntTime(intTime) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            
            /////////////////////////////////////////////////////////
            // UTC TIME OF INDIVIDUAL OBSERVATIONS
            /////////////////////////////////////////////////////////
            
            mcsDOUBLE *obsTime=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
            if( oiT3->GetTime(obsTime) == mcsFAILURE)
            {
                printf("Error getting \n");
                return(mcsFAILURE);
            }
            
            ////////////////////////////////////////////////////
            // HERE INSERT THE CODE SET THE INPUTDATA OBJECTS //
            ////////////////////////////////////////////////////
        
            // Fill one array per wavelength
            // UVCoords is the true UVcoords, not the correponding baseline
            for (i=0 ; i<nbOfWaves; i++)
            {
                // instantiate a new data structure
                mfdataINPUT_T3 *dataT3 = new mfdataINPUT_T3(); 
                /* set data references */ 
                mcsCOMPL_STAT status;
                status = dataT3->SetDataDimensions (nbOfRows,1);
                status = dataT3->SetDataRefs (dateObs, arrName, insName,
                                                *(targetIdList),
                                                dEffWaves+i, obsTime, intTime);

                mcsDOUBLE *true_u1Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
                mcsDOUBLE *true_v1Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
                mcsDOUBLE *true_u2Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));
                mcsDOUBLE *true_v2Coord=(mcsDOUBLE*)malloc(nbOfRows*sizeof(mcsDOUBLE));

                for (mcsUINT16 j=0 ; j<nbOfRows; j++)
                {
                    *(true_u1Coord+j)= *(u1Coord+j) / *(dEffWaves+i);
                    *(true_v1Coord+j)= *(v1Coord+j) / *(dEffWaves+i);
                    *(true_u2Coord+j)= *(u2Coord+j) / *(dEffWaves+i);
                    *(true_v2Coord+j)= *(v2Coord+j) / *(dEffWaves+i);
                }

                status = dataT3->SetT3Refs ( 
                         true_u1Coord,
                         true_v1Coord,
                         true_u2Coord,
                         true_v2Coord,
                         (T3Amp     + i*nbOfRows), 
                         (T3AmpErr  + i*nbOfRows),
                         (T3Phase   + i*nbOfRows), 
                         (T3PhaseErr+ i*nbOfRows)
                         );

                /* Add data to sequencer data container */
                status = this->AddInputData(dataT3);
            }

            free(u1Coord);
            free(v1Coord);
            free(u2Coord);
            free(v2Coord);

        } /* Endif */
    } /* Endfor - tableIndex */
        
    
            

    /* All work should be done now.
     * We shall release de the default working space closing the file.
     * */
    
    delete wks;

    /* Successful completion */
    return(mcsSUCCESS);
}



mcsCOMPL_STAT mfdataINPUT_DATA_LIST::AddInputData(mfdataINPUT_DATA *inputData)
{
    logTrace("mfdataINPUT_DATA_LIST::AddInputData()");
    
    _inputDataList.push_back(inputData);
    
    return mcsSUCCESS;
}

mcsINT16 mfdataINPUT_DATA_LIST::GetNbOfDataSet()
{
    logTrace("mfdataINPUT_DATA_LIST::GetNbOfDataSet()");
    return((mcsINT16)_inputDataList.size());
}

mcsINT16 mfdataINPUT_DATA_LIST::GetNbOfData()
{
    logTrace("mfdataINPUT_DATA_LIST::GetNbOfData()");
    // Get the size of the data list
    mcsINT16 listSize;
    listSize=this->GetNbOfDataSet();

    // Pointer to dataINPUt_DATA_LISTINPUT_DATA for further references 
    mfdataINPUT_DATA *inputData;
    
    // Get the number of data in the list 
    mcsINT16 sum=0;
    mcsINT16 listIndex;
    for(listIndex = 0; listIndex<listSize ; listIndex++)
    {
        inputData = _inputDataList[listIndex];
        sum += inputData->GetNbOfData()*
               inputData->GetNbOfWaves();
    }
    // Return the total number of Data
    return(sum);
}

mfdataINPUT_DATA *mfdataINPUT_DATA_LIST::GetDataSet(mcsINT16 index)
{
    logTrace("mfdataINPUT_DATA_LIST::GetDataSet");
    
    mfdataINPUT_DATA *inputDataRef;
    if((index>=0)&&(index<(mcsINT16)_inputDataList.size()))
    {
       inputDataRef =  _inputDataList[index] ;
       return(inputDataRef);
    }
    else
    {
        logError("dataINPUT_DATA_LIST::GetDataSet : Index out of range (index=%d).",index);
        return NULL;
    }
}




/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
