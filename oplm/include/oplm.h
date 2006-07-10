#ifndef oplm_H
#define oplm_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: oplm.h,v 1.4 2006-03-31 08:13:56 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/03/20 06:56:52  gzins
 * Removed 'info' parameter in oplmNewWorkspace()
 *
 * Revision 1.2  2006/03/14 15:07:51  gzins
 * Renamed to be MCS compliant
 *
 * Revision 1.1  2006/03/01 09:23:46  sccmgr
 * Fix directory structure and add additional files
 *
 ******************************************************************************/

/**
 * @file
 * Main header file, holding all the public APIs of this module.
 */

/* The following piece of code alternates the linkage type to C for all 
functions declared within the braces, which is necessary to use the 
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Enumerate definition
 */
typedef enum
{
    oplmSTAGE_ERROR = -1, /** Some error occurred. */
    oplmSTAGE_INIT,
    oplmSTAGE_FN,  /** Calculate the functions at X and return this vector in
                      FVEC.  Do not alter FJAC. */
    oplmSTAGE_JAC, /** A new acceptable step has been made and is available for
                       examination in X. If the caller want to proceed with
                       next step, calculate the Jacobian at X and return this
                       matrix in FJAC.  Do not alter FVEC. */
    oplmSTAGE_END  /** Algorithm has converged. */
} oplmSTAGE;

#define oplmFTOL_OK          1 /* Both actual and predicted relative
                                  reductions in the sum of squares are at
                                  most FTOL. */
#define oplmXTOL_OK          2 /* relative error between two consecutive
                                  iterates is at most XTOL. */
#define oplmFTOL_AND_XTOL_OK 3 /* conditions for FTOL_OK and XTOL_OK both
                                  hold. */
#define oplmGRAD_NORM        4 /* The cosine of the angle between FVEC
                                  and any column of the Jacobian is at
                                  most GTOL in absolute value. */
#define oplmTOO_MANY_FEV     5 /* Number of calls to FCN has reached or
                                  exceeded MAXFEV. */
#define oplmFTOL_TOO_SMALL   6 /* FTOL is too small.  No further
                                  reduction in the sum of squares is
                                  possible. */
#define oplmXTOL_TOO_SMALL   7 /* XTOL is too small.  No further
                                  improvement in the approximate solution
                                  X is possible. */
#define oplmGTOL_TOO_SMALL   8 /* GTOL is too small.  FVEC is orthogonal
                                  to the columns of the Jacobian to
                                  machine precision. */
#define oplmPARAM_AT_BOUNDS  9 /* All parameters aer stucked to their bounds*/

#define oplmSUCCESS       0
#define oplmBAD_DIMS     -1
#define oplmBAD_DIAG     -2
#define oplmBAD_STAGE    -3
#define oplmBAD_ARG      -4
#define oplmNO_MEM       -5
#define oplmFCN_ERR      -6
#define oplmOVER_BOUNDS  -7
#define oplmSTART_OVER_B -8

#define oplmUSER_SCALING      2  /* user provide scaling factors in DIAG */

typedef struct 
{
    double ftol;
    double xtol;
    double gtol;
    double factor;
    double delta;
    double fnorm;
    double xnorm;
    double gnorm;
    double par;
    double pnorm;

    double xsave; /* saved parameter value when computing partial derivatives
                     by finite difference */

    size_t  *ipvt;
    double **fjac;
    double  *fjac1;
    double  *fjac2;
    double  *diag;
    double  *qtf;
    double  *wa1;
    double  *wa2;
    double  *wa3;
    double  *wa4;
    double  *lowb;
    double  *upb;
    size_t  *fit;
    size_t  *constr;

    size_t m;
    size_t n;
    size_t nfit;
    size_t maxfev;  /* maximum number of function evaluations */
    size_t nfev;    /* number of function evaluations */
    size_t njev;    /* number of Jacobian evaluations */
    size_t iter;	  /* number of iterarions */

    unsigned int flags;

    int jac_in_progress;
    size_t jsave; /* index of parameter value when computing partial derivatives
                     by finite difference */
    int info;
    oplmSTAGE stage;
} oplmWORKSPACE;

/*                                     
 * Public functions declaration
 */
oplmWORKSPACE *oplmNewWorkspace(const size_t m,
                                const size_t n,
                                const double ftol,
                                const double xtol,
                                const double gtol,
                                const size_t maxfev,
                                const double factor,
                                const double diag[],
                                const double lowb[],
                                const double upb[],
                                const size_t fit[]);
void oplmFreeWorkspace(oplmWORKSPACE *ws);
int oplmDifStep(oplmWORKSPACE *ws, double x[], double fvec[]);
int oplmDerStep(oplmWORKSPACE *ws, double x[], double fvec[]);
void oplmPar(const size_t n, const size_t nalmost, double r[], 
             const size_t ldr, const size_t ipvt[], 
             const double diag[], const double qtb[], double delta, 
             double *par_ptr, double x[], double sdiag[], 
             double wa1[], double wa2[]);

#ifdef __cplusplus
}
#endif


#endif /*!oplm_H*/

/*___oOo___*/
