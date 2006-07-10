/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlPARAMETER_LIST.cpp,v 1.2 2006-05-11 13:04:56 mella Exp $"
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
 *  Definition of mfmdlPARAMETER_LIST class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlPARAMETER_LIST.cpp,v 1.2 2006-05-11 13:04:56 mella Exp $";
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
#include "mfmdlPARAMETER_LIST.h"
#include "mfmdlPrivate.h"

/**
 * Class constructor
 */
mfmdlPARAMETER_LIST::mfmdlPARAMETER_LIST()
{
    _index = -1;
}

/**
 * Class destructor
 */
mfmdlPARAMETER_LIST::~mfmdlPARAMETER_LIST()
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
mcsLOGICAL mfmdlPARAMETER_LIST::IsEmpty(void)
{
    logTrace("mfmdlPARAMETER_LIST::IsEmpty()");

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
mcsCOMPL_STAT mfmdlPARAMETER_LIST::Clear(void)
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
mcsCOMPL_STAT mfmdlPARAMETER_LIST::AddAtTail(mfmdlPARAMETER *parameter)
{
    logTrace("mfmdlPARAMETER_LIST::AddAtTail()");

    // Put the element in the list
    _list.push_back(parameter);

    return mcsSUCCESS;
}

/**
 * Returns the number of parameters currently stored in the list.
 *
 * @return The numbers of parameters in the list.
 */
mcsUINT32 mfmdlPARAMETER_LIST::Size(void) 
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
mfmdlPARAMETER *mfmdlPARAMETER_LIST::GetNextParameter(mcsLOGICAL init) 
{
    logTrace("mfmdlPARAMETER_LIST::GetNextParameter()");

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
 * @param name name of the parameter
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
mfmdlPARAMETER *mfmdlPARAMETER_LIST::GetParameter(string name) 
{
    logTrace("mfmdlPARAMETER_LIST::GetParameter()");

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
 * Display the list of parameters on stdout.
 */
void mfmdlPARAMETER_LIST::Display(void) 
{
    logTrace("mfmdlPARAMETER_LIST::Display()");

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

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
