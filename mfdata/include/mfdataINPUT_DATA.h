#ifndef mfdataINPUT_DATA_H
#define mfdataINPUT_DATA_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_DATA.h,v 1.1 2006-06-27 12:05:36 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Declaration of mfdataINPUT_DATA class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"

/**
 * Used to indicate type of INPUT_DATA objects
 */
typedef enum {
   mfdataINPUT_VIS2_TYPE,     /**< data class containing squared visibilities.*/
   mfdataINPUT_T3_TYPE        /**< data class containing phase clotures.*/
} mfdataInputDataType;


/*
 * Class declaration
 */

/**
 * This mother class manages required informations that must be given in every
 * input data.
 * 
 */
class mfdataINPUT_DATA
{

public:
    // Class constructor
    mfdataINPUT_DATA();

    // Class destructor
    virtual ~mfdataINPUT_DATA();

    // Utility methods
    mfdataInputDataType GetType();
    
    mcsCOMPL_STAT SetDataDimensions(mcsINT16 nbOfData,
                                            mcsINT16 nbOfWaves);
    mcsCOMPL_STAT SetDataRefs(mcsSTRING32 dateObs,
                              mcsSTRING32 arrName,
                              mcsSTRING32 insName,
                              mcsINT16 targetId,
                              mcsDOUBLE * lambda,
                              mcsDOUBLE * time,
                              mcsDOUBLE * intTime);

    char * GetDateObs();
    char * GetArrName();
    char * GetInsName();
    mcsINT16 GetTargetId();

    mcsINT16 GetNbOfData();
    mcsINT16 GetNbOfWaves();
   
    mcsCOMPL_STAT GetLambda(mcsDOUBLE ** lambda);
    mcsCOMPL_STAT GetTime(mcsDOUBLE ** time);
    mcsCOMPL_STAT GetIntTime(mcsDOUBLE **intTime);

protected:
    mfdataInputDataType _inputDataType; /**< Indicates type of contained data */
    
    mcsSTRING32 _dateObs;              /**< Observation date */
    mcsSTRING32 _arrName;               /**< Array name */
    mcsSTRING32 _insName;               /**< Instrument name */ 
    mcsINT16 _targetId;                 /**< TargetId */
    
    mcsINT16 _nbOfData;                /**< Number of stored data */ 
    mcsINT16 _nbOfWaves;               /**< Number of waves data */ 

    mcsDOUBLE * _lambda;               /**< Array of wavelengths */
    mcsDOUBLE * _time;                 /**< UTC time of obs. (s) */
    mcsDOUBLE * _intTime;               /**< Integration time (s) */
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfdataINPUT_DATA(const mfdataINPUT_DATA&);
    mfdataINPUT_DATA& operator=(const mfdataINPUT_DATA&);

};

#endif /*!mfdataINPUT_DATA_H*/

/*___oOo___*/
