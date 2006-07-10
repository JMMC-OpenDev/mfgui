/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlUV_COORDS.cpp,v 1.3 2006-07-10 12:57:28 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/06/20 09:25:15  lsauge
 * Implementation of general methods
 *
 * Revision 1.1  2006/03/31 08:16:30  gzins
 * *** empty log message ***
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of mfmdlUV_COORDS class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfmdlUV_COORDS.cpp,v 1.3 2006-07-10 12:57:28 lsauge Exp $"; 

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
#include "mfmdlUV_COORDS.h"
#include "mfmdlPrivate.h"
#include "mfmdlErrors.h"

/**
 * Class constructor
 *
 * The class constructor allocates array to stored the given number of UV
 * coordinates. If an error occurs when allocating memory the size (see Size()
 * method) is set to 0.
 * 
 * @param size size of the array used to stored UV coordinates 
 */
mfmdlUV_COORDS::mfmdlUV_COORDS(mcsUINT32 size)
{
    // Alocate memory (if needed)
    if (size != 0)
    {
        _uArray = (mcsDOUBLE*)calloc(size, sizeof(mcsDOUBLE));
        if (_uArray == NULL)
        {
            errAdd(mfmdlERR_ALLOC_MEM, size*sizeof(mcsDOUBLE));
            _size = 0;
        }
        else
        {
            _vArray = (mcsDOUBLE*)calloc(size, sizeof(mcsDOUBLE));
            if (_vArray == NULL)
            {
                Free(_uArray);

                errAdd(mfmdlERR_ALLOC_MEM, size*sizeof(mcsDOUBLE));
                _size = 0;
            }
            else
            {
                _size = size;
            }
        }
    }
    else
    {
        _uArray = NULL;
        _vArray = NULL;
        _size = 0;
    }
}

/**
 * Class destructor
 */
mfmdlUV_COORDS::~mfmdlUV_COORDS()
{
    _size = 0 ;
    free(_uArray);
    free(_vArray);
    _uArray=NULL;
    _vArray=NULL;
}

/*
 * Public methods
 */


/**
 * Return the current size of the vectors containing the uv coordinates.
 * @return nmuber of uv coordinated stored
 */
mcsUINT32 mfmdlUV_COORDS::Size()
{
    return( _size );
}

/**
 * resize the vectors 
 * @param       newSize new size of the vectors
 * @return      return SUCCESS in case of successfull completion, 
 *              FAILURE otherwise
 */
mcsCOMPL_STAT mfmdlUV_COORDS::Resize(mcsUINT32 newSize)
{
    // Attempting to realloc vectors
    _uArray = (mcsDOUBLE*)realloc(_uArray,newSize*sizeof(mcsDOUBLE));
    _vArray = (mcsDOUBLE*)realloc(_vArray,newSize*sizeof(mcsDOUBLE));

    if(_uArray == NULL)
    {
        errAdd(mfmdlERR_REALLOC_MEM, _uArray);
        return mcsFAILURE;  
    }
    
    if(_vArray == NULL)
    {
        errAdd(mfmdlERR_REALLOC_MEM, _vArray);
        return mcsFAILURE;  
    }

    _size = newSize;
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfmdlUV_COORDS::Get(mcsUINT32 index, mcsDOUBLE &u, mcsDOUBLE &v)
{
        if(index < _size)
        {
            u = *(_uArray+index);
            v = *(_vArray+index);
        }
        else
        {
            errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmfl::Set",index,0,_size-1);
            return mcsFAILURE;
        }
        return mcsSUCCESS;
}

mcsCOMPL_STAT mfmdlUV_COORDS::Set(mcsUINT32 index, mcsDOUBLE u, mcsDOUBLE v)
{
        if(index < _size)
        {
            *(_uArray+index) = u;
            *(_vArray+index) = v;
        }
        else
        {
            errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,"mfmfl::Set",index,0,_size-1);
            return mcsFAILURE;
        }

        return mcsSUCCESS;
}

/*!
    Add two UV_Coords structure
    @param  coords1 first structure to add
    @param  coords2 second structure to add
    @return return mcsSUCCESS under successful completion, mcsFAILURE
    otherwise

    @note   The two passed structures are expecting to have the same size. If
    it is not the case a mcsFAILURE shoud be procuced. Resulting structure
    should be automatically set to the correct size if it is not the case.
*/
mcsCOMPL_STAT
mfmdlUV_COORDS::Add(const mfmdlUV_COORDS &coords1, 
                    const mfmdlUV_COORDS &coords2 )
{
    if (coords1._size == coords2._size)
    {
        if (_size != coords1._size)
        {
            if(this->Resize((mcsUINT32) coords1._size) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
        
        for (mcsUINT16 idx=0 ; idx<_size ; idx++)
        {
            *(_uArray+idx) = *(coords1._uArray+idx) + *(coords2._uArray+idx);
            *(_vArray+idx) = *(coords1._vArray+idx) + *(coords2._vArray+idx);
        }        

        return mcsSUCCESS ;
    }
    
    return mcsFAILURE ;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */
/**
 * Safe free method
 * 
 * This method frees previously allocated memory in a safe way; i.e. checks the
 * pointer is non-null, and sets it to null when released. 
 */
void mfmdlUV_COORDS::Free(void *ptr)
{
    if (ptr != NULL)
    {
        free(ptr);
        ptr = NULL;
    }
}


/*___oOo___*/
