/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: fitengPARAMETER_LIST.cpp,v 1.5 2006-07-04 08:25:03 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/06/27 08:51:06  lsauge
 * Add SetParametersFromArray() method
 * Add computeParametersScale() method
 * Other minor changes
 *
 * Revision 1.2  2006/03/31 08:10:20  gzins
 * Fixed bug related to list end check
 *
 * Revision 1.1  2006/03/20 07:11:39  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of fitengPARAMETER_LIST class.
 */

static char *rcsId="@(#) $Id: fitengPARAMETER_LIST.cpp,v 1.5 2006-07-04 08:25:03 lsauge Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

#define __MIN(a,b)  ((a<b)?(a):(b))
#define __MAX(a,b)  ((a>b)?(a):(b))

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
#include "fitengPARAMETER_LIST.h"
#include "fitengPrivate.h"

/**
 * Class constructor
 */
fitengPARAMETER_LIST::fitengPARAMETER_LIST()
{
    _index = -1;
    _array = NULL;
}

/**
 * Class destructor
 */
fitengPARAMETER_LIST::~fitengPARAMETER_LIST()
{
}

/*
 * Public methods
 */
/**
 * Return whether the list is empty or not.  
 *
 * @return mcsTRUE if the number of parameter is zero, mcsFALSE otherwise.
 */
mcsLOGICAL fitengPARAMETER_LIST::IsEmpty(void)
{
    logTrace("fitengPARAMETER_LIST::IsEmpty()");

    if (_list.empty() == false)
    {
        return mcsFALSE;
    }

    return mcsTRUE;
}

/**
 * Erase all parameters from the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER_LIST::Clear(void)
{
    // Clear list
    _list.clear();
    _index = -1;

    return mcsSUCCESS;
}

/**
 * Add the given parameter at the end of the list.
 *
 * @param parameter parameter pointer to be added to the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT fitengPARAMETER_LIST::AddAtTail(fitengPARAMETER *parameter)
{
    logTrace("fitengPARAMETER_LIST::AddAtTail()");

    // Put the element in the list
    _list.push_back(parameter);

    return mcsSUCCESS;
}


/**
 * Add the given parameter at a specified position.
 *
 * @param parameter parameter pointer to be added to the list.
 * @param pos       position where parameter should be put
 * @note pos is out of bound, parameter should be inserted at tail
 *
 * @return mcsSUCCESS under successfull completion, mcsFAILURE otherwise.
 */
mcsCOMPL_STAT fitengPARAMETER_LIST::InsertAtPos(
        fitengPARAMETER *parameter,
        mcsUINT16 pos)
{
    logTrace("fitengPARAMETER_LIST::InsertAtPos()");

    // Check bounds
    mcsUINT16 lpos = pos ; 
    if ( lpos > _list.size() )
    {
        lpos = _list.size();    
    }

    // Put the element in the list at position pos
    std::vector<fitengPARAMETER *>::iterator rIter; 
        
    rIter = _list.insert(_list.begin()+lpos,parameter);

    // check the consistency ...
    if ( rIter == _list.end() )
    {
        return mcsFAILURE;
    }
    return mcsSUCCESS;
}


/**
 * Returns the number of parameters currently stored in the list.
 *
 * @return The numbers of parameters in the list.
 */
mcsUINT32 fitengPARAMETER_LIST::Size(void) 
{
    return _list.size();
}

/**
 * Return the next parameter in the list.
 *
 * This method returns the pointer to the next parameter of the list. If @em
 * init is mcsTRUE, it returns the first parameter of the list.
 * 
 * This method can be used to move forward in the list, as shown below:
 * @code
 * for (unsigned int el = 0; el < paramList.Size(); el++)
 * {
 *     cout << paramList.GetNextParameter((mcsLOGICAL)(el==0))->GetName() <<
 *     endl;
 * }
 * @endcode
 *
 * @return pointer to the next parameter of the list or NULL if the end of the
 * list is reached.
 */
fitengPARAMETER *fitengPARAMETER_LIST::GetNextParameter(mcsLOGICAL init) 
{
    logTrace("fitengPARAMETER_LIST::GetNextParameter()");

    // If first parameter is resquested
    if ((init == mcsTRUE) || _index == -1)
    {
        _index = 0;
    }
    // Else go to the next parameter 
    else
    {
        _index++;
    }
    
    // If end of list is reached 
    if ((_index + 1) > (mcsINT32)_list.size())  
    {
        {
            return NULL;
        }
    }

    return (_list[_index]);
}

/**
 * Return the parameter of the list corresponding to the given name.
 *
 * This method looks for the specified @em parameter in the list. If found, it
 * returns the pointer to this element. Otherwise, NULL is returned.
 *
 * This method can be used to discover whether a parameter is in list or not, as
 * shown below:
 * @code
 * if (paramList.GetParameter("p1") == NULL)
 * {
 *     printf ("Parameter 'p1' not found in list !!\n");
 * }
 * @endcode
 *
 * @return pointer to the found element of the list or NULL if element is not
 * found in list.
 */
fitengPARAMETER *fitengPARAMETER_LIST::GetParameter(string name) 
{
    logTrace("fitengPARAMETER_LIST::GetParameter()");

    // Search parameter in the list
    for (mcsUINT32 index=0; index != _list.size(); index++)
    {
        if (_list[index]->GetName() == name)
        {
            return (_list[index]);
        }
    }

    // If nothing found, return NULL pointer
    return NULL;
}

/**
 * Return to the parameter array.
 *
 * @return pointer to the parameter value array.
 */
mcsDOUBLE *fitengPARAMETER_LIST::GetArray(void)
{
    logTrace("fitengRESIDUE_LIST::GetArray()");

    // Free previous array
    if (_array != NULL)
    {
        free (_array);
    }

    // Allocate memory
    _array = (mcsDOUBLE *)calloc(_list.size(), sizeof(*_array));

    // Get parameter value
    for (mcsUINT32 index=0; index != _list.size(); index++)
    {
        _array [index] = _list[index]->GetValue();
    }

    // Return residue array 
    return _array;
}



/**
 * Set the parameter values from array 
 *
 */
void fitengPARAMETER_LIST::SetParametersFromArray()
{
    if(_array != NULL)
    {
        // Set parameter value
        for (mcsUINT32 index=0; index != _list.size(); index++)
        {
            _list[index]->SetValue(_array [index]);
        }
    }
}

/**
 * Display the list of parameters on stdout.
 */
void fitengPARAMETER_LIST::Display(void) 
{
    logTrace("fitengPARAMETER_LIST::Display()");

    // Search parameter in the list
    mcsUINT32 index;
    for (index=0; index != _list.size(); index++)
    {
        printf("\t[%02d] %15s = %7.4e\n",
               index,
               _list[index]->GetName().c_str(),
               _list[index]->GetValue());
    }
}

/*!
    Parameters regularization 
    @param  verbose verbose flag (to be removed in next release)
    @return always success

    relularization algorithm is extracted from the ODRPACK documentation :
    (http://orion.math.iastate.edu/docs/cmlib/odrpack.html)
  
    1   VIII.B  DELTA Scaling
    
        ODRPACK chooses scale values for the estimated errors in the
        independent variables, i.e., for the DELTA's, as follows.
    
        For J = 1 to M do
        let  X_max = the largest nonzero absolute value in the J-th
                        column of array X, and
                X_min = the smallest nonzero absolute value in the J-th
                        column of array X.
    
        For I = 1 to N do
            if X(I,J) = zero then
                scale_X(I,J) = one
            else
                if LOG10(X_max)-LOG10(X_min) < two then
                    scale_X(I,J) = one/X_max
                else
                    scale_X(I,J) = one/ABS(X(I,J)).
    
        Users may substitute their own DELTA scaling values via subroutine
        argument SCLD.
*/
mcsCOMPL_STAT
fitengPARAMETER_LIST::ComputeParameterScale(mcsLOGICAL verbose, mcsUINT16 method)
{
    if(this->IsEmpty() == mcsTRUE)
    {
        return mcsFAILURE;
    }
    // Get the number of parameters 
    mcsUINT32 na = this->Size();  
   
    mcsDOUBLE *tmp ;
    
    switch ( method )
    {
        case 1:
            // allocate memory
            tmp = (mcsDOUBLE *)malloc(na*sizeof(mcsDOUBLE)) ;
            for (mcsUINT32 index=0; index != na ; index++)
            {
                mcsDOUBLE val = fabs(_list[index]->GetMinValue())   ;
                val = __MAX(val,fabs(_list[index]->GetMaxValue()))  ;
                val = __MAX(val,fabs(_list[index]->GetValue()))     ;
                tmp [index] = val ;
            }
            break;
        case 0:
        default:
            tmp = this->GetArray();
    }

    // Get the extremum values from parameter array
    mcsDOUBLE maxValue = -1.0e300;
    mcsDOUBLE minValue =  1.0e300;
    for(mcsUINT16 index=0 ; index<na ; index++)
    {
        // Get Maximum
        if (tmp[index]>maxValue)
        {
            maxValue = fabs(tmp[index]);
        }
        // Get Minimum
        if ((tmp[index]<minValue)&&(tmp[index]!=0.0))
        {
            minValue = fabs(tmp[index]);
        }
    }

    for(mcsUINT16 index=0 ; index<na ; index++)
    {
        // Get the parameter address
        fitengPARAMETER *item = this->GetNextParameter(
                index == 0 ? mcsTRUE : mcsFALSE
                ) ;
        if(item == NULL)
        {
            // do some action
        }
    
        // Get the value 
        mcsDOUBLE value = item->GetValue(); 
        // Get and save the old scale value
        mcsDOUBLE oldScaleValue = item->GetScale();
        
        // Perform the scaling of the parameter
        if(value == 0.0)
            item->SetScale(10.0/minValue);
        else if ((log10(maxValue)-log10(minValue))>1.0)
            item->SetScale(1./fabs(value));
        else 
            item->SetScale(1./maxValue);

        // if verbose is set to true, print some info
        if(verbose == mcsTRUE)
        {
        printf("\t[%02d] %-30s = %7.4e old scale value: %7.4e replaced by %7.4e\n",
               index,
               item->GetName().c_str(),
               item->GetValue(),
               oldScaleValue,
               item->GetScale()
               );
        }
    }

    // free memory if required
    if (method == 1)
    {
        free(tmp);
        tmp = NULL;
    }
    // Return Success
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
