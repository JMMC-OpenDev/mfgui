#ifndef fitengGA_ENGINE_H
#define fitengGA_ENGINE_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengGA_ENGINE.h,v 1.1 2006-06-27 08:53:40 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/


/**
 * @file
 * Declaration of fitengGA_ENGINE class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "fiteng.h"

// class prototype
class fitengFIT_ENGINE;

/*
 * Class declaration
 */

/**
 * Brief description of the class, which ends at this dot.
 * 
 * OPTIONAL detailed description of the class follows here.
 *
 * @usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * @filename fileName1 :  usage description of fileName1
 * @filename fileName2 :  usage description of fileName2
 *
 * @env
 * OPTIONAL. If needed, environmental variables accessed by the class. For
 * each variable, name, and usage description, as below.
 * @envvar envVar1 :  usage description of envVar1
 * @envvar envVar2 :  usage description of envVar2
 * 
 * @warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * @ex
 * OPTIONAL. Code example if needed
 * \n Brief example description.
 * @code
 * Insert your code example here
 * @endcode
 *
 * @sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * @sa modcppMain.C
 * 
 * @bug OPTIONAL. Bugs list if it exists.
 * @bug For example, description of the first bug
 * @bug For example, description of the second bug
 * 
 * @todo OPTIONAL. Things to forsee list, if needed. For example, 
 * @todo add other methods, dealing with operations.
 * 
 */
class fitengGA_ENGINE : public fitengFIT_ENGINE 
{

public:
    // Class constructor
    fitengGA_ENGINE(mfsvrSERVER *referent);

    // Class destructor
    virtual ~fitengGA_ENGINE();
    
    virtual mcsCOMPL_STAT   InitEngine();
    virtual mcsINT16        GetFitNextStep();
    mcsCOMPL_STAT           SaveFitResultsOnDisk(const char *filename);
    mcsCOMPL_STAT           DumpGeneration(const char *filename);
    mcsDOUBLE               GetMutationRate();
    mcsDOUBLE               GetMedChi2Value();

    void                    DumpParamValues();

protected:

    mcsINT16        _firstParamIndex;
    mcsINT16        _sampleSize;
    mcsDOUBLE**     _parameterList;
    mcsDOUBLE*      _fitness;

    mcsDOUBLE       _mutationRate;
    const mcsDOUBLE _mutationRateMin=0.01;
    const mcsDOUBLE _mutationRateMax=0.5;
    mcsDOUBLE       _pcross;
    mcsDOUBLE       _fdif;
    mcsINT16        _irep;
    mcsINT16        _ellitism;
    mcsINT16        _doubleCrossover;
    mcsINT16        _adjustMutateRate;

    mcsDOUBLE       _medChi2Value;
    mcsINT16        _encodingSize;
    mcsDOUBLE       _encodingFactor;
    mcsINT32        _blackSheepNumber;
    mcsDOUBLE       _blackSheepMutateRate;

    mcsDOUBLE       *_aFactor;
    mcsDOUBLE       *_bFactor;
    mcsDOUBLE       *_cFactor;
    mcsLOGICAL      *_freeParam;

    // referent server
    mfscrSERVER     *_server;
   
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    fitengGA_ENGINE(const fitengGA_ENGINE&);
    fitengGA_ENGINE& operator=(const fitengGA_ENGINE&);

    mcsINT32 _nbOfFixedParam;
    mcsINT32 _nbOfFreeParam;
    
    mcsDOUBLE RUnif(mcsDOUBLE a,mcsDOUBLE b);  
    mcsINT32  RUnifInt(const mcsINT32 a, const mcsINT32 b);
    void SortVec(mcsDOUBLE *vec, mcsINT32 *index, mcsINT32 iMax);
    void fitengGA_ENGINE::EncodeGenotype(mcsINT16 individualIndex,
                                         char *genotype,
                                         mcsINT32 nbOfParam
                                        );
    void fitengGA_ENGINE::DecodeGenotype(mcsINT16 individualIndex,
                                         char *genotype,
                                         mcsINT32 nbOfParam
                                         );
    void fitengGA_ENGINE::CrossoverOperator(mcsINT16 p1Index,mcsINT16 p2Index,
                                        char*genotypeO1, char*genotypeO2,
                                        mcsINT16 genotypeSize,
                                        mcsINT32 nbOfParam
                                        );
    void        fitengGA_ENGINE::MutateOperator(char*genotype,mcsINT16 genotypeSize);
    mcsINT32    fitengGA_ENGINE::SelectParents(mcsINT32 *index,mcsINT32 *idad);
    void        fitengGA_ENGINE::BlackSheepCatastrophe( 
					mcsINT32 *sortIndexList,
					mfmdlPARAMETER_LIST parameterList,
					mcsINT32 genotypeSize);
};

#endif /*!fitengGA_ENGINE_H*/

/*___oOo___*/
