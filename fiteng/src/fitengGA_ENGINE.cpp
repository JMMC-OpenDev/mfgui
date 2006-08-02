/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengGA_ENGINE.cpp,v 1.1 2006-06-27 08:47:05 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 *  Definition of toto class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: fitengGA_ENGINE.cpp,v 1.1 2006-06-27 08:47:05 lsauge Exp $";
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
#include 'mfsvrSERVER.h'
#include "fitengGA_ENGINE.h"
#include "fiteng.h"
#include "fitengPrivate.h"

/**
 * Class constructor
 */
fitengGA_ENGINE::fitengGA_ENGINE(mfsvrSERVER *referent)
{
    _server = referent ;
    
    _sampleSize   		= 128;
    _mutationRate 		= 0.005;
    _pcross       		= 0.85;
    _fdif         		= 1.0;
    _irep         		= 1;
    _ellitism     		= 1;
    _adjustMutateRate 	= 1;
    _doubleCrossover 	= 1;
    
    _chi2Value 			= 1e300;

    _blackSheepNumber 	    = 28;
    _blackSheepMutateRate   = 0.10 ;
    _encodingSize     	    = 6;
    _encodingFactor   	    = pow(10.0,(mcsDOUBLE)_encodingSize);
}

/**
 * Class destructor
 */
fitengGA_ENGINE::~fitengGA_ENGINE()
{
    logTrace("fitengGA_ENGINE::~mseqGEN_ENGINE()");
   
    // delete all previously reserved memory blocks
    mcsINT16 popIndex ;
    for(popIndex=0 ; popIndex<_sampleSize ; popIndex++)
    {
        free(*(_parameterList+popIndex));
    }
    free(_parameterList);
    free(_fitness);

    free(_freeParam);
    free(_aFactor);
    free(_bFactor);
    free(_cFactor);
}

/*
 * Public methods
 */

/** Initialisation procedure of the engine
 * */
mcsCOMPL_STAT fitengGA_ENGINE::Init()
{
    logTrace("fitengGA_ENGINE::Init()");
   
    // Get the number of data set and the total of number
    // of data linked to the (root) sequencer 
    mcsINT32 nbOfData = (_server::_data)->GetNbOfData();
    
    // Then set (copy) the number of data to the current engine structure
    (void) this->SetNbOfData(nbOfData);

    // Get the model container's reference 
    mfmdlMODEL *model=NULL;
    model = _sequencer->GetModelContainerRef();
    if( model == NULL )
    {
        logError("mfseqLM_ENGINE::Init() No model container defined. Abort.");
        return mcsFAILURE;
    }
   
    
    // Get the reference to the parameter's list
    mfmdlPARAMETER_LIST parameterList;
    model->GetParameters(&parameterList);

    // Retrieve the number of parameters
    mcsINT32 nbOfParam = parameterList.size();
    _firstParamIndex = 0;
    this->SetNbOfParameters(nbOfParam);
    
    // Degree-of-Freedom must be greater than 0
    if(nbOfParam > nbOfData)
    {
        logError("Number of parameters must be smaller than the number of data.");
        return(mcsFAILURE);
    }
    
    ////////////////////////////////////////////////////////////////////////
    //                             MAIN WORK 
    ////////////////////////////////////////////////////////////////////////
    // allocate memory block in order to save parameter list ...
    _parameterList=(mcsDOUBLE**)malloc(_sampleSize*sizeof(mcsDOUBLE*));
    // and instantiate aFactor, bFactor and cFactor arrays
    _aFactor   = (mcsDOUBLE*)malloc(nbOfParam*sizeof(mcsDOUBLE));
    _bFactor   = (mcsDOUBLE*)malloc(nbOfParam*sizeof(mcsDOUBLE));
    _cFactor   = (mcsDOUBLE*)malloc(nbOfParam*sizeof(mcsDOUBLE));
    _freeParam = (mcsLOGICAL*)malloc(nbOfParam*sizeof(mcsLOGICAL));
    
    if(_parameterList == NULL)
    {
        logError("Error during memory allocation");
        return mcsFAILURE;
    }
    
    // ... and for the stirage of the chormosone performance 
    _fitness=(mcsDOUBLE*)malloc(_sampleSize*sizeof(mcsDOUBLE));

    // Initialize the counter of the param status (fixed or free)
    _nbOfFixedParam = 0;
    _nbOfFreeParam  = 0;
    
    // Get the status
    mfmdlPARAMETER *parameter;
    mcsINT16  paramIndex;
    mcsDOUBLE x;
    for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
    {
        parameter = parameterList[_firstParamIndex+paramIndex];
        if(mcsTRUE == parameter->HasFixedValue())
        {
            _nbOfFixedParam++;
            *(_freeParam+paramIndex) = mcsFALSE ;
        }
        else
        {
            _nbOfFreeParam++ ;
            *(_freeParam+paramIndex) = mcsTRUE ;
        }
        // Compute _aFactor, _bFactor and _cFactor
        *(_aFactor+paramIndex) = parameter->GetMinValue();
        x = (parameter->GetMaxValue()-parameter->GetMinValue());
        *(_bFactor+paramIndex) = 1.0/x ;  
        *(_cFactor+paramIndex) = x ;
        printf("Factor : %+7.4e %+7.4e %+7.4e %d\n",
               *(_aFactor+paramIndex), 
               *(_bFactor+paramIndex), 
               *(_cFactor+paramIndex),
               *(_freeParam+paramIndex));
    }

    // Get the number of parameters 
    // Create a first population, randomly choosen
    mcsINT16 popIndex ;
    for(popIndex=0 ; popIndex<_sampleSize ; popIndex++)
    {
        mcsDOUBLE *newParams=(mcsDOUBLE*)malloc(nbOfParam*sizeof(mcsDOUBLE));
        *(_parameterList+popIndex) = newParams;
        // Perform some jobs on the parameter's range and scale factor
        for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
        {
            // Get each parameter's references
            parameter = parameterList[_firstParamIndex+paramIndex];
            // And set the value 
            if(mcsTRUE == parameter->HasFixedValue())
            {
                *(newParams+paramIndex) = parameter->GetValue();
            }
            else
            {
                *(newParams+paramIndex) = this->RUnif(parameter->GetMinValue(),
                                                      parameter->GetMaxValue());
            }
        }
    }
    
    return(mcsSUCCESS);
}

/** Get the next fit step  
 * */
mcsINT16 fitengGA_ENGINE::DoNextStep()
{
    // local declaration
    mfseqINPUT_DATA *inputData;
    mcsINT32 paramIndex;
    mcsDOUBLE  modValue ;
    mcsDOUBLE  dataValue ;
    mcsDOUBLE  dataErr ;
    mcsDOUBLE  *uCoord ;
    mcsDOUBLE  *vCoord ;
    mcsDOUBLE  *vis2Data ;
    mcsDOUBLE  *vis2Err ;
    mcsDOUBLE  *effWaves ;
    mcsCOMPLEX *complexVIS ;
    mcsDOUBLE  *trueUCoord ;
    mcsDOUBLE  *trueVCoord ;
    mcsDOUBLE  re, im;
    mcsDOUBLE  chi2Value = 0.0 ;
    mcsCOMPL_STAT status; // dummy variable
    mfmdlPARAMETER *parameter;
    mcsINT32 nbOfData     = _sequencer->GetNbOfData();
   
    // we must : 
    // 1- Evaluate chromosome performance
    //    a- Evaluate model for each parameter set
    //    b- Evaluation Chi2 (=chromosome's performance or _fitness)
    // 2- Sort the population in function of the performance
    // 3- Generate a new population (define criteria)
    //      - crossover, mutation 
    // 4- Black sheep catastroph could introduced 

    // EVALUATE PERFORMANCE OF EACH CHROMOSOME
    // Get the number of data set and the total number of data they contain
    mcsINT32 nbOfDataSets = _sequencer->GetNbOfDataList();
    // Get the model's reference
    mfmdlMODEL *model=_sequencer->GetModelContainerRef();
    // Retrieve the number of parameters
    mcsINT32 nbOfParam = this->GetNbOfParameters();

    mcsINT16 popIndex ;
    mcsINT16 dataSetIndex ;
        
    // Set the parameters
    mfmdlPARAMETER_LIST parameterList;
    model->GetParameters(&parameterList);

    for(popIndex=0 ; popIndex<_sampleSize ; popIndex++)
    {
        chi2Value = 0.0 ;
        mcsDOUBLE constraint=1.0;

        mcsDOUBLE value;
        for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
        {
            value = *(*(_parameterList+popIndex)+paramIndex);
            // Get each parameter's references
            parameter = parameterList[_firstParamIndex+paramIndex];
            parameter->SetValue(value);
            if(parameter->GetName()=="intensityCoefficient") constraint-=value;
        }

        for( dataSetIndex = 0 ; dataSetIndex<nbOfDataSets ; dataSetIndex++)
        {
            // Get the data set reference 
            inputData = _sequencer->GetDataSet(dataSetIndex);
            
            // The operation will be performed depend on the bature of data
            // Only the VIS2 case is impleted yet.
            if((inputData->GetType()) == mfseqINPUT_VIS2_TYPE)
            {
                // First we must cast the inputData to the good inputDataType ...
                mfseqINPUT_VIS2* inputDataVIS2=(mfseqINPUT_VIS2*)inputData;
                // Get the numer of elements 
                mcsINT32 setNbOfData =inputData->GetNbOfData();
                mcsINT32 setNbOfWaves=inputData->GetNbOfWaves();

                // reserve memory block in order to stock Complex visibility ...
                complexVIS = (mcsCOMPLEX*)malloc(setNbOfData*sizeof(mcsCOMPLEX));
                // ... and true (u,v) coordinates
                trueUCoord = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));
                trueVCoord = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));
                
                // Get the data
                (void) inputDataVIS2->GetUCoord  (&uCoord);
                (void) inputDataVIS2->GetVCoord  (&vCoord);
                (void) inputDataVIS2->GetVis2Data(&vis2Data);
                (void) inputDataVIS2->GetVis2Err (&vis2Err);
                (void) inputDataVIS2->GetLambda  (&effWaves);

                mcsINT16 wavesIndex = 0 ; // spectroscopic channel
                mcsINT16  rowsIndex = 0 ; // (u,v) base
                mcsDOUBLE lEffWaves;
                for( wavesIndex=0 ; wavesIndex<setNbOfWaves ; wavesIndex++)
                {
                    // We work here for each spectroscopic channel
                    lEffWaves = effWaves[wavesIndex] ;
                    // First : we need to compute the true (u,v) coordinates.
                    // OIFTITS (and therefore the INPUT_DATA format) contain
                    // only baseline coordinates (Bx,By)
                    for( rowsIndex=0 ; rowsIndex<setNbOfData ; rowsIndex++)
                    {
                        trueUCoord[rowsIndex] =  uCoord[rowsIndex]/lEffWaves;
                        trueVCoord[rowsIndex] =  vCoord[rowsIndex]/lEffWaves;
                    }

                    // Compute complex visibility of the composite model
                    status = model->Eval(trueUCoord,trueVCoord,setNbOfData,complexVIS); 
                    // If some error ocvured during the model's evaluation step,
                    // return failure 
                    if(status == mcsFAILURE)
                    {
                        logError("fitengGA_ENGINE::GetFitNextStep() :  error during model evaluation");
                        return OP_LM_STAGE_ERROR;
                    }

                    for( rowsIndex=0 ; rowsIndex<setNbOfData ; rowsIndex++)
                    {
                        // fetch input data
                        dataValue = *(vis2Data+rowsIndex*setNbOfWaves+wavesIndex); 
                        dataErr   = *(vis2Err +rowsIndex*setNbOfWaves+wavesIndex);
                        // Compute the corresponding theoritical value
                        // Here for the moment, only the squared visibility is
                        // computed
                        re = complexVIS[rowsIndex].re ; 
                        im = complexVIS[rowsIndex].im ; 
                        modValue = re*re+im*im ;
                        // Compute Chi2    
                        chi2Value += pow((dataValue-modValue)/dataErr,2.0);
                    } // endfor - loop over rows index 
                } // endfor - loop over wavesIndex;
                free(complexVIS);
                free(trueUCoord);
                free(trueVCoord);
            } // endfor - loop over dataSetIndex
            *(_fitness+popIndex) = 1./(chi2Value+pow(constraint,2.0));
        } // endfor - loop over popIndex 
    } 


    // SORTING FITNESS
    // Initialize the sorting list
    mcsINT32 *sortIndexList=(mcsINT32*)malloc(_sampleSize*sizeof(mcsINT32));
    // Sort the fitness vector applying quick sort algorithm 
    (void) this->SortVec(_fitness,sortIndexList,_sampleSize);

    // REPRODUCTION STAGE 
    char     **genotypeList = (char**)malloc(_sampleSize*sizeof(char*));
    mcsINT32 genotypeSize = _encodingSize*_nbOfFreeParam;

    // Introduce black sheep catastrophe here
    // This population is normally introduced at the end of the previous
    // reproduction stage. But numerically it is better to do it now 

    if(_blackSheepNumber >= (_sampleSize-1))
    {
        logWarning("Black sheep number too large ! setting to 0.");
        _blackSheepNumber = 0;
    }

    (void) this->BlackSheepCatastrophe(sortIndexList,
                                       parameterList,
                                       genotypeSize
                                      );

    mcsINT32  P1Index,rank1;
    mcsINT32  P2Index,rank2;

    mcsDOUBLE value;

    for(popIndex=0 ; popIndex<_sampleSize ; popIndex+=2)
    {
        // Selection of the progenitors
        P1Index = popIndex;
        P2Index = popIndex+1;
        
        rank1 = SelectParents(sortIndexList,&P1Index);
        
        do{
            rank2 = SelectParents(sortIndexList,&P2Index);
        }while(P2Index==P1Index);

        
        // Breed
        char *genotypeO1 = (char*)malloc((genotypeSize+1)*sizeof(char));
        char *genotypeO2 = (char*)malloc((genotypeSize+1)*sizeof(char));
       
        // register the genotype in the list
        *(genotypeList+popIndex+0) = genotypeO1;
        *(genotypeList+popIndex+1) = genotypeO2;

        // Apply crossover and mutate operator ...
        (void) CrossoverOperator(P1Index,P2Index,
                                 genotypeO1,genotypeO2,
                                 genotypeSize,
                                 nbOfParam
                                 );
        (void) MutateOperator(genotypeO1,genotypeSize);
        (void) MutateOperator(genotypeO2,genotypeSize);
    }
    

    // replace the population (new generation is comming !)
    // decode the offspring's genotype

    // Save best parameters 
    for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
    {
        value = *(*(_parameterList+*(sortIndexList))+paramIndex);
        // Save best parameters
        parameter = parameterList[_firstParamIndex+paramIndex];
        parameter->SetValue(value);
        printf("%+7.4e  ",value);
    }

    // Decode genotype of all individual
    for(popIndex=(_ellitism==1 ? 1 : 0) ; popIndex<_sampleSize ; popIndex++)
    {
        if(popIndex != *(sortIndexList))
        {
            (void) DecodeGenotype(popIndex,
                                  *(genotypeList+popIndex),
                                  nbOfParam);
        }
    }
    
    // Free previously allocated memory
    for(popIndex=0 ; popIndex<_sampleSize ; popIndex++)
    {
        free(*(genotypeList+popIndex));
    }
    free(genotypeList);
    free(sortIndexList);

    // Save the best chi2 value ...
    mcsDOUBLE dof = (mcsDOUBLE)(nbOfData-nbOfParam) ;
    _chi2Value    = 1./(*_fitness)/dof;    
    _medChi2Value = 1./(*(_fitness+(int)(0.5*(mcsDOUBLE)_sampleSize)))/dof;    

    // adjust the mutation rate if necessary 
    if( 1 == _adjustMutateRate)
    {
        mcsDOUBLE  rdif = fabs((_medChi2Value-_chi2Value)/(_medChi2Value+_chi2Value));
        if(rdif<=0.05)
        {
            _mutationRate *= 1.5;
            if(_mutationRate>_mutationRateMax) _mutationRate=_mutationRateMax;
        }
        else if(rdif>=0.25)
        {
            _mutationRate /= 1.5;
            if(_mutationRate<_mutationRateMin) _mutationRate=_mutationRateMin;
        }
    }
    printf("best chi2=%g [med=%g (%g)]\n",_chi2Value,_medChi2Value,_mutationRate);
    
    return 0;
}

void fitengGA_ENGINE::DumpParamValues()
{
    // local declaration     
    mcsINT32 popIndex;
    mcsINT32 paramIndex;
    mcsINT32 nbOfParam = this->GetNbOfParameters();
    
    FILE *fDesc = fopen("Genetic-results.txt","w");

    for(popIndex=0 ; popIndex<_sampleSize ; popIndex++)
    {
        fprintf(fDesc,"[%03d] ",popIndex);
        for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
        {
            fprintf(fDesc,"%+7.4e  ",*(*(_parameterList+popIndex)+paramIndex));
        }
        fprintf(fDesc," (%7.4e)\n",*(_fitness+popIndex));
    }
    // close file
    fclose(fDesc);
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/**
 * compute random number uniformely distributed between a and b
 * */
mcsDOUBLE fitengGA_ENGINE::RUnif(const mcsDOUBLE a, const mcsDOUBLE b)
{
    if(a == b)
        return a;
    else
    {
        mcsDOUBLE x = (mcsDOUBLE)rand()/(mcsDOUBLE)RAND_MAX ;
        return (a+x*(b-a));
    }
}

mcsINT32 fitengGA_ENGINE::RUnifInt(const mcsINT32 a, const mcsINT32 b)
{
    if( a==b )
        return a;
    else
        return((mcsINT32)RUnif((mcsDOUBLE)a,(mcsDOUBLE)(b+1)));
}


    
void fitengGA_ENGINE::SortVec(
				mcsDOUBLE *vec, 
				mcsINT32 *index, 
				mcsINT32 iMax) 
{
    // local declaration
    mcsDOUBLE nbOfSwap = 0;
    mcsDOUBLE dblBuffer;
    mcsINT32  intBuffer;
    mcsINT32  i;
    
    for(i=0 ; i<iMax ; i++) *(index+i) = i ;
    
    do{
        nbOfSwap = 0;
        for(i=0 ; i<(iMax-1); i++)
        {
            if(vec[i+1]>vec[i])
            {
                nbOfSwap++;
                dblBuffer  = vec[ i ];
                vec[ i ]   = vec[i+1];
                vec[i+1]   = dblBuffer;
                // build swapping table
                intBuffer  = index[i+1] ;
                index[i+1] = index[i] ;
                index[ i ] = intBuffer;
            }
        }
    }while(nbOfSwap!=0);
}
   
#define intsprintf(n,str,num) sprintf((str),"%0"#n"d",(num)) 

void fitengGA_ENGINE::EncodeGenotype(mcsINT16 individualIndex,
                                     char *genotype,
                                     mcsINT32 nbOfParam
                                     )
{
    // Retrieve the number of parameters
    mcsDOUBLE value;
    mcsINT16  paramIndex;
    mcsINT16  freeParamIndex=-1;
    for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
    {
        if(*(_freeParam+paramIndex) == mcsTRUE)
        {
            value=*(*(_parameterList+individualIndex)+paramIndex);
            value-= *(_aFactor+paramIndex);
            value*= *(_bFactor+paramIndex);

            freeParamIndex++ ;

            int n; 
            mcsDOUBLE tmp = value;
            for(int i=0; i<_encodingSize; i++)
            {
                n   = (int)(tmp*=10.0);
                tmp -= (double)n; 
                genotype[_encodingSize*freeParamIndex+i]=48+n;
            } //endfor 
        } // endif
        genotype[_encodingSize*_nbOfFreeParam]='\0'; 
    } // endfor
}

void fitengGA_ENGINE::DecodeGenotype(mcsINT16 individualIndex,
                                     char *genotype,
                                     mcsINT32 nbOfParam
                                     )
{

    char *phenotype= (char*)malloc((_encodingSize+1)*sizeof(char));

    mcsDOUBLE value;
    mcsINT16  paramIndex;
    mcsINT16  freeParamIndex = -1;
    for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
    {
        if(mcsTRUE == *(_freeParam+paramIndex))
        {
            // Split Genotype in phneotype
            (void)memcpy(phenotype,
                         genotype+_encodingSize*(++freeParamIndex),
                         _encodingSize);
            *(phenotype+_encodingSize) = '\0';
            value=atof(phenotype)/_encodingFactor;
            if(value>1)
            {
                printf("gen: |%s|\n",genotype);
                printf("|%s| (%f)\n",phenotype,value);
                printf("Phenotype missinterpreted\n");
                exit(-1);
            };
            value*= *(_cFactor+paramIndex);
            value+= *(_aFactor+paramIndex);
            // Set the new value
            *(*(_parameterList+individualIndex)+paramIndex)=value;
        }
    }
    // free Memory
    free(phenotype);
}

void fitengGA_ENGINE::CrossoverOperator(mcsINT16 p1Index,mcsINT16 p2Index,
                                        char*genotypeO1, char*genotypeO2,
                                        mcsINT16 genotypeSize,
                                        mcsINT32 nbOfParam
                                       )
{
    char *genotypeP1 = (char*)malloc((genotypeSize+1)*sizeof(char));
    char *genotypeP2 = (char*)malloc((genotypeSize+1)*sizeof(char));

    (void) EncodeGenotype(p1Index,genotypeP1,nbOfParam);
    (void) EncodeGenotype(p2Index,genotypeP2,nbOfParam);

    //printf("genotype size : %d\n",genotypeSize);
    //printf("genP1: %04d |%s|\n",p1Index,genotypeP1);
    //printf("genP2: %04d |%s|\n",p2Index,genotypeP2);

    if(RUnif(0.0,1.0)<_pcross)
    {
        mcsINT16 allele1;
        mcsINT16 allele2;
        allele1 = RUnifInt(0,genotypeSize-1); 
        // If double crossover compute the terminal allele position
        allele2 = genotypeSize;
        if(_doubleCrossover==1)
        {
            do{
                allele2 = RUnifInt(allele1,genotypeSize-1); 
            }while(allele2<allele1);
        }

        mcsINT16 geneIndex;
        for(geneIndex=0; geneIndex<=genotypeSize; geneIndex++)
        {
            if((geneIndex>=allele1)&&(geneIndex<=allele2))
            {
                genotypeO1[geneIndex] = genotypeP1[geneIndex] ; 
                genotypeO2[geneIndex] = genotypeP2[geneIndex] ; 
            }
            else
            {
                genotypeO1[geneIndex] = genotypeP2[geneIndex] ; 
                genotypeO2[geneIndex] = genotypeP1[geneIndex] ; 
            }
        }
    }
    else
    {
        (void) strcpy(genotypeO1,genotypeP1);
        (void) strcpy(genotypeO2,genotypeP2);
    }

    free(genotypeP1);
    free(genotypeP2);

}

void fitengGA_ENGINE::MutateOperator(char*genotype,mcsINT16 genotypeSize)
{
    mcsINT16 geneIndex;
    for(geneIndex=0; geneIndex<genotypeSize; geneIndex++)
    {
        if(RUnif(0.0,1.0)<_mutationRate)
            genotype[geneIndex]=RUnifInt(48,57);
    }
}

/** 
 * Progenitors are choosen using roulette wheel
 * */
    mcsINT32
fitengGA_ENGINE::SelectParents(mcsINT32 *index,mcsINT32 *idad)
{
    mcsDOUBLE n1 = (mcsDOUBLE)(_sampleSize+1);
    mcsDOUBLE n  = (mcsDOUBLE)_sampleSize;
    mcsDOUBLE dice = RUnif(0.0,1.0)*(n*n1);
    mcsDOUBLE rtfit=0.0;
    mcsINT32  i;

    for(i=0;i<_sampleSize;i++)
    {
        rtfit += n1 + (n1-2.0*(mcsDOUBLE)(i));
        if(rtfit>=dice) 
        {
            *idad=*(index+i);
            return i;
        }
    }
    return -1;
}

/** 
 * */
mcsCOMPL_STAT fitengGA_ENGINE::SaveFitResultsOnDisk(const char *filename) 
{

    // local declaration
    mfseqINPUT_DATA *inputData;
    mcsDOUBLE  		modValue ;
    mcsDOUBLE  		dataValue ;
    mcsDOUBLE  		dataErr ;
    mcsDOUBLE  		*uCoord ;
    mcsDOUBLE  		*vCoord ;
    mcsDOUBLE  		*vis2Data ;
    mcsDOUBLE  		*vis2Err ;
    mcsDOUBLE  		*effWaves ;
    mcsCOMPLEX 		*complexVIS ;
    mcsDOUBLE  		*trueUCoord ;
    mcsDOUBLE  		*trueVCoord ;
    mcsDOUBLE  		*trueProjB ;
    mcsDOUBLE  		re, im;
    mcsCOMPL_STAT 	status; // dummy variable

    // Initialize file pointer for output 
    FILE *fdesc;
    if((fdesc=fopen(filename,"w")) == NULL)
    {
        logError("EmfseqLM_ENGINE::SaveDataOnDisk : error during file opening.");
        return mcsFAILURE;
    }
    logInfo("Save data in %s",filename);

    // Get the number of data set and the total number of data they contain
    mcsINT32 nbOfDataSets = _sequencer->GetNbOfDataList();
    // Get the model's reference
    mfmdlMODEL *model=_sequencer->GetModelContainerRef();
    
    // Set the parameters
    mfmdlPARAMETER_LIST parameterList;
    model->GetParameters(&parameterList);

    mcsINT16 dataSetIndex ;
    for( dataSetIndex = 0 ; dataSetIndex<nbOfDataSets ; dataSetIndex++)
    {

        // Get the data set reference 
        inputData = _sequencer->GetDataSet(dataSetIndex);

        // The operation will be performed depend on the bature of data
        // Only the VIS2 case is impleted yet.
        if((inputData->GetType()) == mfseqINPUT_VIS2_TYPE)
        {
            // First we must cast the inputData to the good inputDataType ...
            mfseqINPUT_VIS2* inputDataVIS2=(mfseqINPUT_VIS2*)inputData;
            // Get the numer of elements 
            mcsINT32 setNbOfData =inputData->GetNbOfData();
            mcsINT32 setNbOfWaves=inputData->GetNbOfWaves();

            // reserve memory block in order to stock Complex visibility ...
            complexVIS = (mcsCOMPLEX*)malloc(setNbOfData*sizeof(mcsCOMPLEX));
            // ... and true (u,v) coordinates
            trueUCoord = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));
            trueVCoord = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));
            trueProjB  = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));

            // Get the data
            (void) inputDataVIS2->GetUCoord  (&uCoord);
            (void) inputDataVIS2->GetVCoord  (&vCoord);
            (void) inputDataVIS2->GetVis2Data(&vis2Data);
            (void) inputDataVIS2->GetVis2Err (&vis2Err);
            (void) inputDataVIS2->GetLambda  (&effWaves);

            mcsINT16 wavesIndex = 0 ; // spectroscopic channel
            mcsINT16  rowsIndex = 0 ; // (u,v) base
            mcsDOUBLE lEffWaves;
            for( wavesIndex=0 ; wavesIndex<setNbOfWaves ; wavesIndex++)
            {
                // We work here for each spectroscopic channel
                lEffWaves = effWaves[wavesIndex] ;
                // First : we need to compute the true (u,v) coordinates.
                // OIFTITS (and therefore the INPUT_DATA format) contain
                // only baseline coordinates (Bx,By)
                for( rowsIndex=0 ; rowsIndex<setNbOfData ; rowsIndex++)
                {
                    trueUCoord[rowsIndex] =  uCoord[rowsIndex]/lEffWaves;
                    trueVCoord[rowsIndex] =  vCoord[rowsIndex]/lEffWaves;
                    trueProjB [rowsIndex] = sqrt( trueUCoord[rowsIndex]*trueUCoord[rowsIndex] + 
                                                  trueVCoord[rowsIndex]*trueVCoord[rowsIndex] 
                                                );
                }

                // Compute complex visibility of the composite model
                status = model->Eval(trueUCoord,trueVCoord,setNbOfData,complexVIS); 
                // If some error ocvured during the model's evaluation step,
                // return failure 
                if(status == mcsFAILURE)
                {
                    logError("fitengGA_ENGINE::GetFitNextStep() :  error during model evaluation");
                    return mcsFAILURE; 
                }

                for( rowsIndex=0 ; rowsIndex<setNbOfData ; rowsIndex++)
                {
                    // fetch input data
                    dataValue = *(vis2Data+rowsIndex*setNbOfWaves+wavesIndex); 
                    dataErr   = *(vis2Err +rowsIndex*setNbOfWaves+wavesIndex);
                    // Compute the corresponding theoritical value
                    // Here for the moment, only the squared visibility is
                    // computed
                    re = complexVIS[rowsIndex].re ; 
                    im = complexVIS[rowsIndex].im ; 
                    modValue = re*re+im*im ;
                    // Save data on disk
                    fprintf(fdesc," %7.4e\t %7.4e\t %7.4e\t %7.4e %7.4e\n ", 
                            trueProjB[rowsIndex],
                            dataValue,
                            dataErr,
                            modValue,
                            fabs((dataValue-modValue)/dataErr)
                           );
                } // endfor - loop over rows index 
            } // endfor - loop over wavesIndex;
            free(complexVIS);
            free(trueUCoord);
            free(trueVCoord);
            free(trueProjB );
        } // endfor - loop over dataSetIndex
    } 
    // close file
    fclose(fdesc);

    return mcsSUCCESS;
}


mcsCOMPL_STAT fitengGA_ENGINE::DumpGeneration(const char *filename) 
{

    // local declaration
    mcsINT32    paramIndex;
    mcsDOUBLE   value =  0.0;
    mcsINT16    popIndex = 0 ;

    // Initialize file pointer for output 
    FILE *fdesc;
    if((fdesc=fopen(filename,"w")) == NULL)
    {
        logError("EfitengGA_ENGINE::DumpGeneration: error during file opening.");
        return mcsFAILURE;
    }
    logInfo("Save data in %s",filename);

    // Retrieve the number of parameters
    mcsINT32 nbOfParam = this->GetNbOfParameters();

    for(popIndex=0 ; popIndex<_sampleSize ; popIndex++)
    {
        for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
        {
            value = *(*(_parameterList+popIndex)+paramIndex);
            // Save data on disk
            fprintf(fdesc,"%7.4e\t",value);
        }
        fprintf(fdesc,"\n",value);
    }// endfor - loop over popIndex
    // close file
    fclose(fdesc);

    return mcsSUCCESS;
}


void fitengGA_ENGINE::BlackSheepCatastrophe(mcsINT32 *sortIndexList,
                                            mfmdlPARAMETER_LIST parameterList,
                                            mcsINT32 genotypeSize
                                           )
{
    // Local declaration
    mcsINT32    	index;
    mcsINT32    	popIndex;
    mfmdlPARAMETER 	*parameter;
    mcsDOUBLE   	value;
    mcsINT32    	paramIndex;
    mcsDOUBLE   	modValue ;
    mcsDOUBLE   	dataValue ;
    mcsDOUBLE   	dataErr ;
    mcsDOUBLE   	*uCoord ;
    mcsDOUBLE   	*vCoord ;
    mcsDOUBLE   	*vis2Data ;
    mcsDOUBLE   	*vis2Err ;
    mcsDOUBLE   	*effWaves ;
    mcsCOMPLEX  	*complexVIS ;
    mcsDOUBLE   	*trueUCoord ;
    mcsDOUBLE   	*trueVCoord ;
    mcsINT16    	dataSetIndex ;
    mcsDOUBLE   	re, im ;
    mcsINT32    	nbOfParam = this->GetNbOfParameters();
    mcsINT32    	nbOfDataSets = _sequencer->GetNbOfDataList() ;
    mfmdlMODEL  	*model=_sequencer->GetModelContainerRef() ;
    mfseqINPUT_DATA *inputData ;
    mcsCOMPL_STAT 	status ;

    /* 1- Get the fittest genotype
     * 2- Apply the mutation orperator in order to alterate genotype, wioth a
     * high mutate rate
     * 4- Compute the performance of the new individual (NI)
     * 5- Put the NI in the population
     * */  
    for(index = (_sampleSize-_blackSheepNumber) ;
        index < _sampleSize ;
        index++)
    {
        popIndex = sortIndexList[index];
        // Get the fittest individual's gentoype
        char *fittestGenotype = (char*)malloc((genotypeSize+1)*sizeof(char));
        (void) EncodeGenotype(sortIndexList[0],
                              fittestGenotype,
                              nbOfParam);
        // save the current value of the mutation rate
        mcsDOUBLE savedMutationRate = _mutationRate;  
        _mutationRate = _blackSheepMutateRate;
        // Alter fittest genotype 
        (void) MutateOperator(fittestGenotype,genotypeSize);
        _mutationRate = savedMutationRate;
        (void) DecodeGenotype(popIndex,
                              fittestGenotype,
                              nbOfParam);

        mcsDOUBLE chi2Value  = 0.0 ;
        mcsDOUBLE constraint = 1.0 ;
        
        for( paramIndex=0 ; paramIndex<nbOfParam ; paramIndex++ )
        {
            value = *(*(_parameterList+popIndex)+paramIndex);
            // Get each parameter's references
            parameter = parameterList[_firstParamIndex+paramIndex];
            parameter->SetValue(value);
            if(parameter->GetName()=="intensityCoefficient") constraint-=value;
        }

        for( dataSetIndex = 0 ; dataSetIndex<nbOfDataSets ; dataSetIndex++)
        {
            // Get the data set reference 
            inputData = _sequencer->GetDataSet(dataSetIndex);

            // The operation will be performed depend on the bature of data
            // Only the VIS2 case is impleted yet.
            if((inputData->GetType()) == mfseqINPUT_VIS2_TYPE)
            {
                // First we must cast the inputData to the good inputDataType ...
                mfseqINPUT_VIS2* inputDataVIS2=(mfseqINPUT_VIS2*)inputData;
                // Get the numer of elements 
                mcsINT32 setNbOfData =inputData->GetNbOfData();
                mcsINT32 setNbOfWaves=inputData->GetNbOfWaves();

                // reserve memory block in order to stock Complex visibility ...
                complexVIS = (mcsCOMPLEX*)malloc(setNbOfData*sizeof(mcsCOMPLEX));
                // ... and true (u,v) coordinates
                trueUCoord = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));
                trueVCoord = (mcsDOUBLE*)malloc(setNbOfData*sizeof(mcsDOUBLE));

                // Get the data
                (void) inputDataVIS2->GetUCoord  (&uCoord);
                (void) inputDataVIS2->GetVCoord  (&vCoord);
                (void) inputDataVIS2->GetVis2Data(&vis2Data);
                (void) inputDataVIS2->GetVis2Err (&vis2Err);
                (void) inputDataVIS2->GetLambda  (&effWaves);

                mcsINT16 wavesIndex = 0 ; // spectroscopic channel
                mcsINT16  rowsIndex = 0 ; // (u,v) base
                mcsDOUBLE lEffWaves;
                for( wavesIndex=0 ; wavesIndex<setNbOfWaves ; wavesIndex++)
                {
                    // We work here for each spectroscopic channel
                    lEffWaves = effWaves[wavesIndex] ;
                    // First : we need to compute the true (u,v) coordinates.
                    // OIFTITS (and therefore the INPUT_DATA format) contain
                    // only baseline coordinates (Bx,By)
                    for( rowsIndex=0 ; rowsIndex<setNbOfData ; rowsIndex++)
                    {
                        trueUCoord[rowsIndex] =  uCoord[rowsIndex]/lEffWaves;
                        trueVCoord[rowsIndex] =  vCoord[rowsIndex]/lEffWaves;
                    }

                    // Compute complex visibility of the composite model
                    status = model->Eval(trueUCoord,trueVCoord,setNbOfData,complexVIS); 
                    // If some error ocvured during the model's evaluation step,
                    // return failure 
                    if(status == mcsFAILURE)
                    {
                        logError("fitengGA_ENGINE::GetFitNextStep() :  error during model evaluation");
                        return ;
                    }

                    for( rowsIndex=0 ; rowsIndex<setNbOfData ; rowsIndex++)
                    {
                        // fetch input data
                        dataValue = *(vis2Data+rowsIndex*setNbOfWaves+wavesIndex); 
                        dataErr   = *(vis2Err +rowsIndex*setNbOfWaves+wavesIndex);
                        // Compute the corresponding theoritical value
                        // Here for the moment, only the squared visibility is
                        // computed
                        re = complexVIS[rowsIndex].re ; 
                        im = complexVIS[rowsIndex].im ; 
                        modValue = re*re+im*im ;
                        // Compute Chi2    
                        chi2Value += pow((dataValue-modValue)/dataErr,2.0);
                    } // endfor - loop over rows index 
                } 

                free(complexVIS);
                free(trueUCoord);
                free(trueVCoord);
            }
        }
        // Replace the chi2Value 
        // BE CAREFULL : because the _fitness array is sorted, 
        // we must use index instead of popIndex !
        *(_fitness+index) = 1./(chi2Value+pow(constraint,2.0));
    }
    // All works done. 
}


mcsDOUBLE fitengGA_ENGINE::GetMutationRate()
{
    return _mutationRate;
}

mcsDOUBLE fitengGA_ENGINE::GetMedChi2Value()
{
    return _medChi2Value;
}


/*___oOo___*/
