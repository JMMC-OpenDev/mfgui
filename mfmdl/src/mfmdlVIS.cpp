/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlVIS.cpp,v 1.2 2006-06-20 09:25:15 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/31 08:16:30  gzins
 * *** empty log message ***
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of mfmdlVIS class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfmdlVIS.cpp,v 1.2 2006-06-20 09:25:15 lsauge Exp $"; 

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
#include "mfmdlVIS.h"
#include "mfmdlPrivate.h"
#include "mfmdlErrors.h"

/**
 * Class constructor
 *
 * The class constructor allocates array to stored the given number of complex
 * visibilities. If an error occurs when allocating memory the size (see Size()
 * method) is set to 0.
 * 
 * @param size size of the array used to stored complex visibilities  
 */
mfmdlVIS::mfmdlVIS(mcsUINT32 size)
{
    // Alocate memory (if needed)
    if (size != 0)
    {
        //_array = (mcsCOMPLEX*)calloc(size,sizeof(mcsCOMPLEX));
        _array = (mcsCOMPLEX*)malloc(size*sizeof(mcsCOMPLEX));
        if (_array != NULL)
        {
            _size = size;
        }
        else
        {
            errAdd(mfmdlERR_ALLOC_MEM, size*sizeof(mcsCOMPLEX));
            _size = 0;
        }
    }
    else
    {
        _array = NULL;
        _size = 0;
    }
}

/**
 * Class destructor
 */
mfmdlVIS::~mfmdlVIS()
{
    _size = 0 ;
    free(_array);
    _array=NULL;
}

/*
 * Public methods
 */
/**
 * Returns the number of complex visibilities stored in the list.
 *
 * @return The numbers of complex visibilities
 */
mcsUINT32 mfmdlVIS::Size(void) 
{
    return _size;
}

/**
 * Re.
 *
 * @return The numbers of complex visibilities
 */
mcsCOMPL_STAT mfmdlVIS::Resize(mcsUINT32 newSize)
{
    // Free previously allocated memory
    if (_array != NULL)
    {
        free(_array);
    }
      
    // Alocate memory (if needed)
    if (newSize != 0)
    {
        _array = (mcsCOMPLEX*)calloc(newSize, sizeof(mcsCOMPLEX));
        if (_array != NULL)
        {
            _size = newSize;
        }
        else
        {
            // out of bounds 
            errAdd(mfmdlERR_ALLOC_MEM, newSize*sizeof(mcsCOMPLEX));
            _size = 0;
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}

/**
   Set visibility values (real and imaginary part)
   @param   index   index value
   @param   re      real part
   @param   im      imaginary part
   @return  return  SUCCESS on successfull completion, FAILURE otherwise
*/
mcsCOMPL_STAT mfmdlVIS::Set(mcsUINT32 index, mcsDOUBLE re, mcsDOUBLE im)
{
    if(index<_size)
    {
        (*(_array+index)).re = re; 
        (*(_array+index)).im = im; 
    }
    else
    {
        // out of bounds 
        errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmdlVIS::Set",index,0,_size-1);
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
   Get visibility values (real and imaginary part) from the VIS array.
   Values are returned using pass-by-reference arguments
   @param   index   index value
   @param   &re     real part's reference
   @param   &im     imaginary part's reference
   @return  return  SUCCESS on successfull completion, FAILURE otherwise
*/
mcsCOMPL_STAT mfmdlVIS::Get(mcsUINT32 index, mcsDOUBLE &re, mcsDOUBLE &im)
{
    if(index<_size)
    {
        re = (*(_array+index)).re; 
        im = (*(_array+index)).im; 
    }
    else
    {
        // out of bounds 
        errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmdlVIS::Get",index,0,_size-1);
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
   Get visibility's reference (real and imaginary part) from the VIS array.
   @param   index   index value
   @return  the value returned is the reference to a COMPLEX pointer.
            Retrun NULL if the request fails
*/
mcsCOMPLEX* mfmdlVIS::Get(mcsUINT32 index)
{
    if(index<_size)
    {
        return ((_array+index));
    }
    else
    {
    // out of bounds 
        errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmdlVIS::Get",index,0,_size-1);
        return NULL; 
    }
}

/**
   Get visibility's reference (real and imaginary part) from the VIS array and
   compute and return the squared visibility.
   @param   index   index value
   @param   &res    return value's (squared visibility) reference    
   @return  return  SUCCESS on successfull completion, FAILURE otherwise
*/
mcsCOMPL_STAT mfmdlVIS::GetVis2(mcsUINT32 index, mcsDOUBLE &res)
{
    if(index < _size)
    {
        // Get real and imaginary part of the visibility
        mcsDOUBLE re = (*(_array+index)).re; 
        mcsDOUBLE im = (*(_array+index)).im; 
        // Compute squared visibility and exit 
        res = re*re+im*im;  
        return mcsSUCCESS; 
    }
    else
    {
        // out of bounds 
        errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmdlVIS::Get",index,0,_size-1);
        return mcsFAILURE; 
    }
}

mcsDOUBLE mfmdlVIS::GetVis2(mcsUINT32 index)
{
    if(index < _size)
    {
        // Get real and imaginary part of the visibility
        mcsDOUBLE re = (*(_array+index)).re; 
        mcsDOUBLE im = (*(_array+index)).im; 
        // Compute and squared visibility
        return re*re+im*im;  
    }
    else
    {
        // out of bounds 
        errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmdlVIS::Get",index,0,_size-1);
        return -1.0; 
    }
}

mcsCOMPL_STAT mfmdlVIS::Add(mfmdlVIS& vis)
{
    // be sure that the array have the same size
    if(vis.Size() != _size)
    {
        // TODO: insert code here
        return mcsFAILURE;
    }
    // loop other element
    for(mcsUINT32 index=0 ; index < _size ; index++)
    {
        mcsDOUBLE re = 0.0;
        mcsDOUBLE im = 0.0;
        if(vis.Get(index,re,im) != mcsSUCCESS)
        {
            return mcsFAILURE;
        }

        (*(_array+index)).re += re ; 
        (*(_array+index)).im += im ;
    }
    // done
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfmdlVIS::Reset()
{
    // loop other element
    for(mcsUINT32 index=0 ; index < _size ; index++)
    {
        (*(_array+index)).re = 0.0 ;
        (*(_array+index)).im = 0.0 ;
    }
    // done
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
