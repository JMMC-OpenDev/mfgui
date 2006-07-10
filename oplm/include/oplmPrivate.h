#ifndef oplmPrivate_H
#define oplmPrivate_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: oplmPrivate.h,v 1.2 2006-03-14 15:08:00 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/01 14:10:46  gzins
 * Updated according to Lyon's version
 *
 * Revision 1.1  2006/03/01 09:23:46  sccmgr
 * Fix directory structure and add additional files
 *
 ******************************************************************************/

/**
 * @file
 * oplm private header file.
 */


/*
 * Constants definition
 */

/* Module name */
#define MODULE_ID "oplm"

/* 
 * Macro definition
 */
#define oplmMin(a,b) ((a)<=(b)?(a):(b))

/*
 * Enumerate definition
 */
typedef enum
{
    oplmPARAM_FIXED = 0,
    oplmLOWER_BOUND_ACTIVE,
    oplmUPPER_BOUND_ACTIVE,
    oplmLOWER_BOUND_ACTIVATED,
    oplmUPPER_BOUND_ACTIVATED,
    oplmLOWER_BOUND_ALMOST_ACTIVE,
    oplmUPPER_BOUND_ALMOST_ACTIVE,
    oplmBOUNDS_NOT_ACTIVE
} oplmCONSTRAINTS;

/*                                     
 * Private functions declaration
 */
double oplmENorm(size_t n, const double x[]);

void oplmQRFac(const size_t m, const size_t n, size_t *nfit_ptr, double a[], 
                const size_t lda, const int pivot, size_t ipvt[], 
                const size_t fit[], const size_t constr[], double rdiag[], 
                double acnorm[], double wa[]);
void oplmQRSolv(const size_t n, double r[], const size_t ldr,
                 const size_t ipvt[], const double diag[], 
                 const double qtb[], double x[], double sdiag[], double wa[]);


#endif /*!oplmPrivate_H*/

/*___oOo___*/
