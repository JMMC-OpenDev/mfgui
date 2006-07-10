#ifndef mfmdlPARAMETER_H
#define mfmdlPARAMETER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlPARAMETER.h,v 1.4 2006-06-27 08:38:53 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/03/31 08:16:47  gzins
 * Updated API
 *
 * Revision 1.2  2006/01/26 09:22:53  mella
 * Added flag to set parameters bounds
 *
 * Revision 1.1  2005/09/08 14:06:11  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of mfmdlPARAMETER class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System header
 */
#include <iostream>

/*
 * MCS header
 */
#include "mcs.h"

/*
 * FIT header
 */
#include "fiteng.h"


/*
 * Class declaration
 */

/**
 * This class is used by model to make them adjustable.
 * 
 */
class mfmdlPARAMETER: public fitengPARAMETER
{

public:
    // Class constructor
    mfmdlPARAMETER(string name, 
                   mcsDOUBLE initialValue, 
                   string units=" ",
                   mcsDOUBLE minValue = fitengMINUS_INFINITY,
                   mcsDOUBLE maxValue = fitengPLUS_INFINITY
                   );

    // Class destructor
    virtual ~mfmdlPARAMETER();

    // \todo Q: place some method no more public but friend of model?
    virtual const string GetUnits();
    
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mfmdlPARAMETER(const mfmdlPARAMETER&);
    mfmdlPARAMETER& operator=(const mfmdlPARAMETER&);

    string _units;                  /* units */
};

#endif /*!mfmdlPARAMETER_H*/

/*___oOo___*/
