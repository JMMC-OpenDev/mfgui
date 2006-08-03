#ifndef mfsvrVersion_H
#define mfsvrVersion_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrVersion.h,v 1.1 2006-03-31 08:19:31 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Definition of the software version.
 */

/* The following piece of code alternates the linkage type to C for all 
functions declared within the braces, which is necessary to use the 
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif

// Software version 
#define mfsvrVERSION "0.1+"

#ifdef __cplusplus
}
#endif

#endif /*!mfsvrVersion_H*/

/*___oOo___*/
