#ifndef mfmdl_H
#define mfmdl_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdl.h,v 1.4 2006-06-27 08:41:37 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/01/26 09:22:53  mella
 * Added flag to set parameters bounds
 *
 * Revision 1.2  2005/09/08 14:06:11  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * \file
 * mfmdl general header file.
 */


/*
 * Local headers
 */

/** define minus infinity*/
#define mfmdlMINUS_INFINITY -1e300
/** define plus infinity*/
#define mfmdlPLUS_INFINITY 1e300


#include "mfmdlErrors.h" 
#include "mfmdlMODEL.h" 
#include "mfmdlPARAMETER.h" 
#include "mfmdlCOMPOSED_MODEL.h" 

#include "mfmdlPONCT.h" 
#include "mfmdlUNIFORM_DISK.h" 


#endif /*!mfmdl_H*/

/*___oOo___*/
