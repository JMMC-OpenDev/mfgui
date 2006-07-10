/*******************************************************************************
 * JMMC project
 * 
 * "@(#) $Id: oplmEngine.c,v 1.4 2006-03-31 08:12:33 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/03/20 06:58:07  gzins
 * Removed 'info' parameter in oplmNewWorkspace()
 *
 * Revision 1.2  2006/03/14 15:49:16  gzins
 * Fixed bug related to error Id
 *
 * Revision 1.1  2006/03/14 15:08:32  gzins
 * Added
 *
 *
 ******************************************************************************/

/**
 * @file
 * This file contains implementation of Levenberg-Marquardt algorithm.
 *
 * These functions have been converted from Fortran 77
 * source code (MINPACK project) to C with some improvements.
 */

/* 
 * System Headers
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <float.h>

/* 
 * MCS Headers
 */
#include "log.h"
#include "err.h"

/* 
 * Local Headers
 */
#include "oplm.h"
#include "oplmPrivate.h"
#include "oplmErrors.h"

/* Private function */
/**
 * Calculate scaled euclidean norm of vector.
 * 
 * @param n is a positive integer input variable.
 * @param scale is scale factor to be applied on input variables 
 * @param x is an input array of length n.
 * @param fit is an input array of length n containing flags are used to
 * determining which values of x must be considered
 * @param ws is an output array containing scaled input variables.
 *
 * @return scaled euclidean norm of vector
 *
 * @sa oplmENorm
 */
static double oplmScaledENorm(const size_t n, const double scale[],
                               const double x[], const size_t fit[],
                               double ws[])
{
    size_t j, f=0;

    for (j=0; j<n; ++j) 
    {
        /* NELEM must be less or equal to X length */
        if (fit != NULL)
        {
            /* If X and SCL are likely not to have the same length, FIT flags
             * are used to determine which values of X must be considered 
             */
            if (fit[j] != 0) 
            {
                ws[f] = scale[f] * x[j];
                f++;
            }
        } 
        else 
        {
            /* Otherwise, every value of X is asssociated to a value of SCL */
            ws[j] = scale[j]*x[j];
        }
    }

    return (fit == NULL ? oplmENorm(n, ws) : oplmENorm(f, ws));
}

/**
 * Look for active constraints
 * 
 * @param n is a positive integer input variable.
 * @param m is a positive integer input variable.
 * @param x is an input array of length n containing variable values.
 * @param fvec is an input array of length m.
 * @param constr is an input array of length n containing the variable
 * constraints
 * @param lowBounds is an input array of length n containing the lower bounds
 * @param upBounds is an input array of length n containing the upper bounds
 * @param fjac is an input matrix of dimension [f, m], where f is the number of
 * non-fixed variables.
 *
 * @return upon successful completion, the number of active constraints is
 * returned. Otherwise, -1 is returned an error is placed in global error stack.
 */
static size_t oplmGetActiveConstraints(const size_t m, const size_t n, 
                                        const double x[], const double fvec[],
                                        oplmCONSTRAINTS constr[], 
                                        const double lowBounds[], 
                                        const double upBounds[], 
                                        const double fjac[])
{
    size_t i, j, f=0, nmatrx=0;
    double gradient, fact;
    const double activeTol = 1e-08;

    /* Function must be called only if some constraints exist */
    if (constr == NULL)
    {
        errAdd(oplmERR_NULL_POINTER, "constr");
        return -1;
    }

    for (j=0; j<n; ++j)
    {
        /* 
         * Minimal check on X values:
         * At this stage X is not supposed to overtake its bounds
         */
        if ((lowBounds != NULL) && (x[j] < lowBounds[j])) 
        {
            errAdd(oplmERR_OVER_LOWER_BOUND, j, x[j], lowBounds[j]);
            return -1;
        }
        if ((upBounds != NULL) && (upBounds[j] < x[j]))
        {
            errAdd(oplmERR_OVER_UPPER_BOUND, j, x[j], upBounds[j]);
            return -1;
        }

        /* If parameter is not fixed */
        if (constr[j] != oplmPARAM_FIXED) 
        {
            /* Compute the j-th component of the gradient, to determine the
             * descent direction. 
             */
            gradient = 0.0;
            for (i=0; i<m; ++i) 
            {
                gradient += fjac[f*m+i]*fvec[i];
            }
            
            /* If lower bounds exist */
            if (lowBounds != NULL)
            {
                if (fabs(lowBounds[j]) < activeTol)
                {
                    fact = 1.0;
                }
                else
                {
                    fact = fabs(lowBounds[j]);
                }

                logTest("x: %f   Lowb+eps: %f   grad: %f \n", x[j],
                        lowBounds[j] + activeTol * fact, gradient);

                /* Check if lower bound is active */
                if ((x[j] <= lowBounds[j] + activeTol * fact) && 
                    (gradient > 0.0)) 
                {
                    constr[j] = oplmLOWER_BOUND_ACTIVE;
                }
            }

            /* If upper bounds exist */
            if (upBounds != NULL) 
            {
                if (fabs(upBounds[j]) < activeTol) 
                {
                    fact = 1.0;
                }
                else
                {
                    fact = fabs(upBounds[j]);
                }

                logTest("x: %f   Upb-eps: %f   grad: %f \n", x[j],
                        upBounds[j] - activeTol * fact, gradient);
                
                /* Check if upper bound is active */
                if ((x[j] >= upBounds[j] - activeTol * fact) && 
                    (gradient < 0.0))
                {
                    constr[j] = oplmUPPER_BOUND_ACTIVE;
                }
            }
            f++;

            /* GZI: devrait etre oplmUPPER_BOUND_ALMOST_ACTIVE !! */
            nmatrx += (constr[j] >= oplmLOWER_BOUND_ALMOST_ACTIVE ? 1 : 0);
        }
    }
    return nmatrx;
}

/**
 * Compute a new step
 * 
 */
static double oplmFindStep(const size_t n, const size_t nfit, 
                            const size_t nmatrx, const size_t nalmost,
                            double x[], double fjac[], const long ldfjac,
                            const double diag[], const size_t fit[], 
                            const size_t ipvt[], const double qtf[], 
                            double p[], double savedx[], const double delta,
                            double *par, double wa3[], double wa4[])
{
    size_t j, f=0;
    double temp;
    size_t *idx = NULL;

    /* When OP_LMPAR is called, P must be a scratch array of length NFIT */
    for (j=0; j<nfit; ++j)
    {
        p[j] = 0.0;
    }

    /* Determine the Levenberg-Marquardt parameter. On output, P will
       be set with the (anti-)step and SDIAG will be set with the
       diagonal of matrix S.  WA3 and WA4 are purely scratch arrays. */
    oplmPar(nmatrx, nalmost, fjac, ldfjac, ipvt, diag, qtf, delta, par,
             p,  /*sdiag*/savedx, /*scratch*/wa3, /*scratch*/wa4);

    /* Store the (anti-)direction P and X - P.  Calculate the norm of P. 
     * and save previous values of X inside SAVEDX 
     */
    for (j=0 ; j<n ; ++j) 
    {
        if (fit == NULL || fit[j] != 0)
        {
            temp = x[j];
            x[j] -= p[f];
            savedx[f] = temp;
            ++f;
        }
    }
    return oplmScaledENorm(f, diag, p, idx, wa3);
}

/* Definitions and adjustments to mimic FORTRAN indexing. */

#undef fjac_
#define fjac_(i1,i2) (fjac[(i2)*m + (i1)])

#undef ipvt_
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
# define ipvt_(i1) (ipvt[i1] - 1)
#else
# define ipvt_(i1) (ipvt[i1])
#endif


#define oplmFIRST_ITERATION  (oplmUSER_SCALING << 1)

#define AUTO_SCALE(FLAGS) (!((FLAGS)&oplmUSER_SCALING))

static double oplmFactorize(const size_t m, const size_t n, const size_t nfit,
                            const double fvec[], double fjac[], 
                            const size_t ldfjac, double diag[],
                            const unsigned int flags, const size_t fit[], 
                            const size_t constr[],
                            const double fnorm, size_t ipvt[], double qtf[], 
                            double rdiag[],	double acnorm[], double wa3[],
                            double wa4[])
{
    const double zero = 0.0;
    const double one = 1.0;
    double sum, temp1, temp2, temp, gnorm;
    size_t j, i, l; 
    size_t nmatrx;
    nmatrx = nfit; /* NFIT :dimension of the sub-problem at the beginning*/;
    FILE *file;

    /* Compute the QR factorization of the jacobian. */
    oplmQRFac(m, n, &nmatrx, fjac, ldfjac, 1, ipvt, fit, constr, rdiag, acnorm,
               /*scratch*/wa3);
    /* Now N is the number of fitted parameters: N = NINACT */
    file = fopen("debugoptim.txt", "a");
    if (fit != NULL) {
        fprintf(file, "nmatrx: %d \n", nmatrx);
        fprintf(file, "FIT:    constr:   ipvt:\n");
        for (j=0; j<n; ++j) {
            fprintf(file, " %d   %d   ", fit[j], constr[j]);
            if (fit[j] != 0) fprintf(file, "%d    \n",ipvt[fit[j]-1]);
            else fprintf(file, "\n");
        }
    }
    fclose(file);


    /* Form (Q transpose)*FVEC and store the first N components in QTF.
       WA4 is used as a scratch array for this computation. */
    for (i=0 ; i<m ; ++i) 
    {
        wa4[i] = fvec[i];
    }
    for (j=0 ; j<nmatrx ; ++j)
    {
        if (fjac_(j,j))
        {
            sum = zero;
            for (i=j ; i<m ; ++i)
            {
                sum += fjac_(i,j)*wa4[i];
            }
            temp = sum/fjac_(j,j);
            for (i=j ; i<m ; ++i)
            {
                wa4[i] -= fjac_(i,j)*temp;
            }
        }
        /* Build the R triangular matrix with its diagonal elements */
        fjac_(j,j) = rdiag[j];
        /* Fill in QTF matrix */
        qtf[j] = wa4[j];
    }

    /* Compute the norm of the scaled gradient. */
    gnorm = zero;
    if (fnorm)
    {
        temp1 = one/fnorm;
        for (j=0 ; j<nmatrx ; ++j)
        {
            l = ipvt_(j);
            temp = acnorm[l];
            if (temp) {
                sum = zero;
                for (i=0 ; i<=j ; ++i) 
                {
                    sum += fjac_(i,j)*(qtf[i]*temp1);
                }
                if (gnorm < (temp2 = fabs(sum/temp))) gnorm = temp2;
            }
        }
    }

    /* Update parameter scaling if necessary. */
    if (AUTO_SCALE(flags))
    {
        if ((flags & oplmFIRST_ITERATION) != 0)
        {
            /* On the first iteration and if auto-scaling is allowed, scale
             * according to the norms of the columns of the initial Jacobian. */
            for (j=0 ; j<nfit ; ++j)
            {
                diag[j] = (acnorm[j] ? acnorm[j] : one);
            }
        }
        else 
        {
            for (j=0 ; j<nfit ; ++j)
            {
                if (diag[j] < acnorm[j]) 
                {
                    diag[j] = acnorm[j];
                }
            }
        }
    }

    return gnorm;
}

/**
 * Allocate working space for fitting engine 
 * 
 * @param m number of residual values
 * @param n number of parameters to fit
 * @param ftol ...
 * @param xtol ...
 * @param gtol ...
 * @param maxfev maximum number of function evaluation
 * @param factor ...
 * @param diag scale factor array to normalize parameters
 * @param lowb lower boud array for parameters
 * @param upb upper boud array for parameters
 * @param fit flag array indicating whether parameter is fixed or not 
 *
 * @return upon successful completion, the pointer to the new working space.
 * Otherwise, NULL is returned an error is placed in global error stack.
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
                                const size_t fit[])
{
    oplmWORKSPACE *ws;
    size_t size, j, off_ipvt, off_fjac, off_fjac1, off_fjac2, off_lowb, off_upb;
    size_t off_diag, off_qtf, off_wa1, off_wa2, off_wa3, off_wa4;
    size_t off_fit, off_constr;
    size_t f, nfit = 0, n2 = 0;

    /* Check the input parameters for errors. */
    if (n <= 0)
    {
        errAdd(oplmERR_WRONG_INT_VALUE, n, "number of parameters");
        return (void *)NULL;
    }
    if (m < n)
    {
        errAdd(oplmERR_WRONG_INT_VALUE, m, "number of residual values");
        return (void *)NULL;
    }
    if (ftol < 0.0)
    {
        errAdd(oplmERR_WRONG_FLOAT_VALUE, ftol, "ftol");
        return (void *)NULL;
    }
    if (xtol < 0.0)
    {
        errAdd(oplmERR_WRONG_FLOAT_VALUE, xtol, "xtol");
        return (void *)NULL;
    }
          if (gtol < 0.0)
    {
        errAdd(oplmERR_WRONG_FLOAT_VALUE, gtol, "gtol");
        return (void *)NULL;
    }
    if (maxfev < 0.0)
    {
        errAdd(oplmERR_WRONG_FLOAT_VALUE, maxfev, 
               "maximum number of function evaluation");
        return (void *)NULL;
    }
    if (factor < 0.0)
    {
        errAdd(oplmERR_WRONG_FLOAT_VALUE, factor, "factor");
        return (void *)NULL;
    }

    if (diag != NULL) 
    {
        for (j=0 ; j<n ; ++j) 
        {
            if (diag[j] <= 0.0) 
            {
                errAdd(oplmERR_WRONG_FLOAT_VALUE, diag[j], "scale factor");
                return (void *)NULL;
            }
        }
    }

    if (lowb != NULL && upb != NULL)
    {
        for (j=0; j<n; ++j) 
        {
            if (lowb[j] > upb[j]) 
            {
                errAdd(oplmERR_WRONG_FLOAT_VALUE, lowb[j],
                       "upper/lower bounds");
                return (void *)NULL;
            }
        }
    }

    if (fit != NULL) 
    {
        for (j=0 ; j<n ; ++j) 
        {
            if (fit[j] != 0) 
            {
                ++nfit;
            }
        }
    } 
    else
    {
        nfit = n;
    }


#define ROUND_UP(a,b) ((((a) + (b) - 1)/(b))*(b))
#undef _
#define _(NPREV, PREV, MEMBER) \
    ROUND_UP(off_##PREV + (NPREV)*sizeof(*ws->PREV), sizeof(*ws->MEMBER))
        off_ipvt = ROUND_UP(sizeof(oplmWORKSPACE), sizeof(*ws->ipvt));
    off_fjac  = _(nfit,   ipvt,   fjac);
    off_fjac1 = _(n,      fjac,  fjac1);
    off_fjac2 = _(nfit*m, fjac1, fjac2);
    off_diag  = _(n2*m,   fjac2, diag);
    off_qtf   = _(nfit,   diag,   qtf);
    off_wa1   = _(nfit,   qtf,   wa1);
    off_wa2   = _(nfit,   wa1,   wa2);
    off_wa3   = _(nfit,   wa2,   wa3);
    off_wa4   = _(nfit,   wa3,   wa4);
#undef _
    size      = off_wa4 + m*sizeof(*ws->wa4);
    off_lowb  = ROUND_UP(size, sizeof(*ws->lowb));
    if (lowb != NULL) 
    {
        size = off_lowb + n*sizeof(*ws->lowb);
    }
    off_upb = ROUND_UP(size, sizeof(*ws->upb));;
    if (upb != NULL) 
    {
        size = off_upb + n*sizeof(*ws->upb);
    }
    off_fit = ROUND_UP(size, sizeof(*ws->fit));
    if (fit != NULL || lowb != NULL || upb != NULL)
    {
        size = off_fit + n*sizeof(*ws->fit);
    }
    off_constr = ROUND_UP(size, sizeof(*ws->constr));
    if (off_fit != size) 
    {
        size = off_constr + n*sizeof(*ws->constr);
    }

    ws = malloc(size);
    if (! ws) 
    {
        errAdd(oplmERR_ALLOC_MEM, size);
        return (void *)NULL;
    }
    memset(ws, 0, sizeof(oplmWORKSPACE));
    ws->m = m;
    ws->n = n;
    ws->nfit = nfit;
    ws->maxfev = maxfev;
    ws->ftol = ftol;
    ws->xtol = xtol;
    ws->gtol = gtol;
    ws->factor = factor;

    ws->jac_in_progress = 0;

    ws->flags = (diag ? oplmUSER_SCALING : 0);
    ws->info  = 0;
    ws->stage = oplmSTAGE_INIT;

    ws->ipvt  = (size_t *)((char *)ws + off_ipvt);
    ws->fjac  = (double **)((char *)ws + off_fjac);
    ws->fjac1 = (double *)((char *)ws + off_fjac1);
    ws->fjac2 = (double *)((char *)ws + off_fjac2);
    ws->diag  = (double *)((char *)ws + off_diag);
    ws->qtf   = (double *)((char *)ws + off_qtf);
    ws->wa1   = (double *)((char *)ws + off_wa1);
    ws->wa2   = (double *)((char *)ws + off_wa2);
    ws->wa3   = (double *)((char *)ws + off_wa3);
    ws->wa4   = (double *)((char *)ws + off_wa4);

    if (lowb != NULL) 
    {
        ws->lowb = (double *)((char *)ws + off_lowb);
        memcpy(ws->lowb, lowb, n*sizeof(double));
    } 
    else 
    {
        ws->lowb = NULL;
    }
    if (upb != NULL) 
    {
        ws->upb = (double *)((char *)ws + off_upb);
        memcpy(ws->upb, upb, n*sizeof(double));
    } 
    else 
    {
        ws->upb = NULL;
    }

    if (fit != NULL || lowb != NULL || upb != NULL) 
    {
        ws->fit = (size_t *)((char *)ws + off_fit);
        ws->constr = (size_t *)((char *)ws + off_constr);
        f = 0;
        for (j=0; j<n; ++j)
        {
            if (fit == NULL || fit[j] != 0) 
            {
                ws->fit[j] = ++f;
                ws->constr[j] = 7;
            } 
            else 
            {
                ws->fit[j] = 0;
                ws->constr[j] = 0;
            }
        }
    } 
    else 
    {
        ws->fit = NULL;
        ws->constr = NULL;
    }

    if (fit != NULL) 
    {
        f = 0;
        for (j=0; j<n; ++j)
        {
            if (fit[j] != 0)
            {
                if (diag != NULL)
                {
                    ws->diag[f] = diag[j];
                }
                ws->fjac[j] = ws->fjac1 + f*m;
                ++f;
            } 
            else
            {
                ws->fjac[j] = NULL;
            }
        }  
    } 
    else 
    {
        if (diag != NULL)
        {
            memcpy(ws->diag, diag, n*sizeof(*diag)); 
        }
        for (j=0; j<n; ++j) 
        {
            ws->fjac[j] = ws->fjac1 + j*m;
        }
    }

    return ws;
}

void oplmFreeWorkspace(oplmWORKSPACE *ws)
{
    if (ws) 
    {
        free(ws);
    }
}

int oplmDifStep(oplmWORKSPACE *ws, double x[], double fvec[])
{
    const double tiny = 1e-6;
    const double p1 = 1.0e-1;
    const double one = 1.0;
    double s, fact;
    size_t j, idxdiag=0, n = ws->n;
    if (ws->stage == oplmSTAGE_JAC) {
        /* Start finite difference Jacobian. */
        ws->jac_in_progress = 0;
        for (j=0 ; j<n ; ++j) {
            /* Search for first free parameter. */
            if (ws->fjac[j] != NULL) {
                if (ws->fit != NULL) idxdiag = ws->fit[j]-1;
                else idxdiag = j;
                ws->jsave = j;
                ws->jac_in_progress = 1;
                memcpy(ws->wa4, fvec, ws->m*sizeof(*fvec));
                break;
            }
        }
        if (! ws->jac_in_progress) {
#warning "FIXME: no parameter to fit!"
            return oplmDerStep(ws, x, fvec);
        }
    } else if (ws->jac_in_progress) {
        double *fjac = ws->fjac[ws->jsave], *fsave = ws->wa4;
        /*double *fjac = ws->fjac1 + ws->jsave * ws->m, *fsave = ws->wa4;*/
        size_t i, m = ws->m;
        s = 1.0/(x[ws->jsave] - ws->xsave);
        x[ws->jsave] = ws->xsave; /* restore parameter value prior to perturbation */
        for (i=0 ; i<m ; ++i) {
            fjac[i] = (fvec[i] - fsave[i])*s;
        }

        /* Search for next free parameter. */
        ws->jac_in_progress = 0;
        for (j = ws->jsave + 1 ; j < n ; ++j) {
            if (ws->fjac[j] != NULL) {
                if (ws->fit != NULL) idxdiag = ws->fit[j]-1;
                else idxdiag = j;
                ws->jsave = j;
                ws->jac_in_progress = 1;
                break;
            }
        }
        if (! ws->jac_in_progress) {
            /* Achieved finite difference Jacobian. */
            ws->stage = oplmSTAGE_JAC;
            memcpy(fvec, ws->wa4, ws->m*sizeof(*fvec));
            return oplmDerStep(ws, x, fvec);
        }
    } else {
        return oplmDerStep(ws, x, fvec);
    }


    /* Continue with finite difference Jacobian. */
    ws->xsave = x[ws->jsave];/* Save parameter value prior to perturbation */
    fact = (ws->diag != NULL ? ws->diag[idxdiag] : fabs(ws->xsave))*tiny;

    if (ws->upb == NULL) {
        /* Default is to choose forward finite differences */
        s = fact;
    } else if (ws->lowb == NULL) {
        s = -one * fact;
    } else {
        /* Or choose between forward or backward tiny step */
        double forwd_space = ws->upb[ws->jsave] - ws->xsave;
        double backd_space = ws->xsave - ws->lowb[ws->jsave];
        if (forwd_space > backd_space) {
            s = fact;
            /* FIXME: s used to be s = fabs(ws->xsave)*tiny; */
            if (s > forwd_space) s = p1 * forwd_space;
        } else {
            s = -one * fact;
            if (fabs(s) > backd_space) s = -p1 * backd_space;
        }
    }

    if (! s) s = tiny;
    /* add the perturbation to JSAVE-th element of X and 
     * the function will be reevaluated at this new position 
     */
    x[ws->jsave] += s;
    return (ws->stage = oplmSTAGE_FN);
}

#undef ipvt_
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
# define ipvt_(i1) (ipvt[i1] - 1)
#else
# define ipvt_(i1) (ipvt[i1])
#endif

int oplmDerStep(oplmWORKSPACE *ws, double x[], double fvec[])
{
    const double zero = 0.0;
    const double one = 1.0;
    const double two = 2.0;
    const double half = 5.0e-1;
    const double p1 = 1.0e-1;
    const double p25 = 2.5e-1; /* used to update size of trust region */
    const double p5 = 5e-1; /* used to update active/almost active constraints */
    const double p75 = 7.5e-1; /* used to update size of trust region */
    const double min_ratio = 1.0e-4; /* minimum successful relative reduction */
    const double epsmch = DBL_EPSILON; /* EPSMCH is the machine precision. */
    double *diag = ws->diag;
    size_t *constr = ws->constr;
    size_t *fit  = ws->fit;
    size_t *ipvt = ws->ipvt;
    double *lowb = ws->lowb;
    double *upb  = ws->upb;
    double *fjac = ws->fjac1;
    double *qtf = ws->qtf;
    double *wa1 = ws->wa1;
    double *wa2 = ws->wa2;
    double *wa3 = ws->wa3;
    double *wa4 = ws->wa4;
    size_t i, j, l, f=0, m = ws->m, n = ws->n, nfit = ws->nfit;
    size_t nmatrx, nalmost;
    double temp, temp1, temp2;
    FILE *file;

    if (ws->stage == oplmSTAGE_INIT)
    {
        /* Check that starting values of parameters do not overtake the optional
         * bounds. 
         */
        for (j=0; j<n; ++j)
        {
            if ((lowb != NULL && lowb[j] > x[j]) || 
                (upb != NULL && upb[j] <x[j])) 
            {
                ws->info = oplmSTART_OVER_B;
                return (ws->stage = oplmSTAGE_ERROR);
            }
        }

        /* Evaluate the function at the starting point
           and calculate its norm. */
        ws->iter = 1;
        ws->nfev = 0;
        return (ws->stage = oplmSTAGE_FN);

    } 
    else if (ws->stage == oplmSTAGE_FN && ws->nfev <= 0) 
    {

        /* Initialize Levenberg-Marquardt parameter and iteration counter. */
        ws->nfev = 1;
        ws->fnorm = oplmENorm(m, fvec);    
        ws->par = zero;

        file = fopen("debugoptim.txt","a");
        fprintf(file, " fnorm:%f  \n", ws->fnorm);
        if (constr != NULL) {
            fprintf(file, "Constraints flags: \n");
            for (j=0; j<n; ++j) {
                fprintf(file, "%d \n", constr[j]);
            }
        }
        fclose(file);
        return (ws->stage = oplmSTAGE_JAC);

    } else if (ws->stage == oplmSTAGE_JAC) {

        /* look for active constraints */
        if (constr != NULL) {
            nmatrx=oplmGetActiveConstraints(m, n, x, fvec, constr, lowb, upb, fjac);
            /* Now, constr is equal to 0 where the parameter value is fixed
               1 where the lower bound is active
               2 where the upper bound is active
               3 where the lower bound is activated
               4 where the upper bound is activated
               5 where the lower bound is almost active
               6 where the upper bound is almost active
               7 where the bounds are not active
             */
        } else nmatrx = nfit;
        file = fopen("debugoptim.txt","a");
        fprintf(file, " iter: %d   Ninact: %d  \n", ws->iter, nmatrx);
        fclose(file);

        if (nmatrx == 0) {
            /* all parameters are supposed to reach their bounds, so there is no need
               to invert a matrix system */
            f = 0;
            for (j=0; j<n; ++j) {
                if (constr[j] >= 3) {
                    /* Save previous value of X inside WA2...*/
                    wa2[f] = x[j];
                    /* ...and apply activated bound */
                    x[j] = (constr[j] == 3 ? lowb[j] : upb[j]);
                }
                if (constr[j] != 0) ++f;
            }
            return  (ws->stage = oplmSTAGE_FN);
        }

        /* Got the Jacobian matrix. */
#warning "replace ITER==1 by NFEV==..."
        int first_iteration = (ws->iter == 1);
        unsigned int flags = (ws->flags & oplmUSER_SCALING);
        if (first_iteration) flags |= oplmFIRST_ITERATION;
        ++ws->njev;
        file = fopen("debugoptim.txt","a");
        if (fit != NULL) {
            fprintf(file, "FIT: \n");
            for (j=0; j<n; ++j) {
                fprintf(file, " %d  \n", fit[j]);
            }
        }
        fclose(file);

        ws->gnorm = oplmFactorize(m, n, nfit, fvec, fjac, m, diag, flags, fit,
                                  constr, ws->fnorm, ipvt, qtf, /*rdiag*/wa1,
                                  /*acnorm*/wa2, wa3, wa4);

        if (first_iteration) {
            /* On the first iteration, calculate the norm of the scaled X
               and initialize the step bound DELTA. */
            ws->xnorm = oplmScaledENorm(n, diag, x, constr, wa3);
            ws->delta = ws->factor*ws->xnorm;
            if (ws->delta == zero) ws->delta = ws->factor;
        }
        file = fopen("debugoptim.txt","a");
        fprintf(file, " gnorm: %f   delta: %f   xnorm: %f  \n", ws->gnorm, 
                ws->delta, ws->xnorm);
        fclose(file);

        /* Test for convergence of the gradient norm. */
        if (ws->gnorm <= ws->gtol) {
            ws->info = 4;
            return (ws->stage = oplmSTAGE_END);
        }

    } else if (ws->stage == oplmSTAGE_FN) {

        double actred,dirder,fnorm1;
        double prered,ratio;

        ++ws->nfev;
        fnorm1 = oplmENorm(m, fvec);
        /* Re-compute the number of parameters without active constraints */
        nmatrx = n;
        nalmost = 0;
        file = fopen("debugoptim.txt","a");
        fprintf(file, " fnorm1: %f  \n", fnorm1);
        fclose(file);
        if (constr != NULL) {
            for (j=0; j<n; ++j) {
                nmatrx -= (constr[j] <= 4);
                nalmost += (constr[j] == 5 || constr[j] == 6);
            }
        }

        /* Compute the scaled actual reduction. */
        if (p1*fnorm1 < ws->fnorm) {
            temp1 = fnorm1/ws->fnorm;
            actred = one - temp1*temp1;
        } else {
            actred = -one;
        }

        /* Compute the scaled predicted reduction and the scaled
         * directional derivative.  The (anti-)step P is stored into
         * array WA1. 
         */
        for (j=0 ; j<nmatrx ; ++j) {
            wa3[j] = zero;
            l = ipvt_(j);
            temp1 = wa1[l];
            for (i=0 ; i<=j ; ++i) {
                wa3[i] -= fjac_(i,j)*temp1;
            }
        }

        temp1 = oplmENorm(nmatrx, wa3)/ws->fnorm;
        temp2 = (sqrt(ws->par)*ws->pnorm)/ws->fnorm;
        prered = temp1*temp1 + two*temp2*temp2;
        dirder = -(temp1*temp1 + temp2*temp2);

        /* Compute the ratio of the actual to the predicted reduction. */
        ratio = (prered ? actred/prered : zero);
        file = fopen("debugoptim.txt","a");
        fprintf(file, " actred: %f   prered: %f   dirder: %f  ratio: %f \n", 
                actred, prered, dirder, ratio);
        fclose(file);

        /* Update the step bound. */
        if (ratio <= p25) {
            if (nalmost == 0 ) {
                temp1 = (actred >= zero ? half : dirder/(two*dirder + actred));
#warning "FIXME: cf. above.... division par zero"
                if (temp1 < p1 || p1*fnorm1 >= ws->fnorm) temp1 = p1;
                temp2 = ws->pnorm/p1;
                ws->delta = temp1*oplmMin(ws->delta, temp2);
                ws->par /= temp1;
            }
        } else if (! ws->par || ratio >= p75) {
            ws->delta = two*ws->pnorm;
            ws->par *= half;
        }

        /* Test for successful iteration. */
        if (ratio >= min_ratio) {
            /* Successful iteration.  Update X, FVEC, and their norms. */
            ws->xnorm = oplmScaledENorm(n, diag, x, constr, wa2);
            ws->fnorm = fnorm1;
            ++ws->iter;
            if (constr != NULL) {
                /* Need to modify constraint flags so that any would be equal to
                 * 1, 2, 3, 4, 5 nor 6 (=activated and almost active constraints
                 * codes)
                 */
                for (j=0; j<n; ++j) {
                    if (constr[j] != 0) constr[j]=7;
                }
            }
        } else {
            /* Unsuccessful iteration.  MOdify "almost active" constraint list
             * if any, and restore X and FVEC as they were at the beginning of
             * the step.
             */
            if (nalmost != 0) constr[ws->jsave] = (size_t )ws->xsave;
            f = 0;
            for (j=0 ; j<n ; ++j) {
                if (constr == NULL || constr[j] != 0) {
                    x[j] = wa2[f];
                    ++f;
                }
            }
            for (i=0 ; i<m ; ++i) {
                fvec[i] = wa4[i];
            }
        }

        /* Tests for convergence or for termination and stringent tolerances. */
        ws->info = 0;
        if (fabs(actred) <= ws->ftol && prered <= ws->ftol && ratio <= two) {
            if (ws->delta <= ws->xtol*ws->xnorm) {
                ws->info = oplmFTOL_AND_XTOL_OK;
            } else {
                ws->info = oplmFTOL_OK;
            }
        } else if (ws->delta <= ws->xtol*ws->xnorm) {
            ws->info = oplmXTOL_OK;
        } else if (ws->gnorm <= epsmch) {
            ws->info = oplmGTOL_TOO_SMALL;
        } else if (ws->delta <= epsmch*ws->xnorm) {
            ws->info = oplmXTOL_TOO_SMALL;
        } else if (fabs(actred) <= epsmch && prered <= epsmch && ratio <= two) {
            ws->info = oplmFTOL_TOO_SMALL;
        } else if (ws->nfev >= ws->maxfev) {
            ws->info = oplmTOO_MANY_FEV;
        }
        if (ws->info) {
            return (ws->stage = oplmSTAGE_END);
        }
        if (ratio >= min_ratio) {
            return (ws->stage = oplmSTAGE_JAC);
        }

    } else {
        ws->info = oplmBAD_STAGE;
        return (ws->stage = oplmSTAGE_ERROR);
    }

    /* Re-compute the number of "almost activated" constraints, because it may 
     * have changed ...
     */
    nalmost = 0; 
    if (constr != NULL) {
        for (j=0; j<n; ++j) {
            if (constr[j] == 5 || constr[j] == 6) ++nalmost;
        }
    }  
    file = fopen("debugoptim.txt","a");
    fprintf(file, " nmatrx: %d   nalmost: %d \n",nmatrx, nalmost);
    fclose(file);

    /* ... and compute a new step. */
    ws->pnorm = oplmFindStep(n, nfit, nmatrx, nalmost, /*oldx -> newx*/x, fjac, m, 
                          diag, fit, ipvt, qtf, /*p / anti-step*/wa1, 
                          /*scratch ->saved old x*/wa2, ws->delta, &ws->par, 
                          wa3, wa4);
    file = fopen("debugoptim.txt","a");
    fprintf(file, " first p: \n");
    for (j=0; j<nfit; ++j) {
        fprintf(file, " %f \n", wa1[j]);
    }
    fprintf(file, " pnorm: %f \n", ws->pnorm);
    fprintf(file, " constraints??: %d \n", (lowb != NULL || upb != NULL));
    fclose(file);

    /* Shorten the step if necessary to satisfy the constraints */
    if (lowb != NULL || upb != NULL) {

        /* FIXME: use WA1 instead of a new array:
         * double *short_p = (double*) malloc(nfit*sizeof(double));
         */
        temp = temp1 = temp2 = zero;
        size_t idmax1 = 0, idmax2 = 0;
        file = fopen("debugoptim.txt","a");
        fprintf(file, " nalmost: %d ninact: %d\n", nalmost, nmatrx);

        if (nalmost == 0) {
            for (j=0; j<n; ++j) {
                if (fit[j] != 0) {
                    f=fit[j]-1;
                    if (lowb != NULL && constr[j] == 7 && x[j] < lowb[j]) {
                        temp = fabs(x[j] - lowb[j])*diag[f];
                        if (temp > p5*ws->delta/(double)nmatrx)
                            constr[j] = 3;
                        else {
                            constr[j] = 5;
                            if (temp1 < temp) {
                                idmax1 = j+1;
                                temp1 = temp;
                            }
                        }
                    } else if (upb != NULL && constr[j] == 7 && x[j] > upb[j]) {
                        temp = fabs(x[j] - upb[j])*diag[f];
                        if (temp > p5*ws->delta/(double)nmatrx)
                            constr[j] = 4;
                        else {
                            constr[j] = 6;
                            if (temp2 < temp) {
                                idmax2 = j+1;
                                temp2=temp;
                            }
                        }
                    }
                }
            }
        } else {
            for (j=0; j<n; ++j) {
                if (fit[j] != 0) {
                    f = fit[j] - 1;
                    if ((constr[j] == 5) &&
                        (x[j] < lowb[j]) &&
                        (temp1 < fabs(x[j] - lowb[j])*diag[f])) {
                        idmax1 = j+1;
                        temp1 = fabs(x[j] - lowb[j])*diag[f];
                    } else if ((constr[j] == 6) &&
                               (x[j] > upb[j]) &&
                               (temp2 < fabs(x[j] - upb[j])*diag[f])) {
                        idmax2 = j+1;
                        temp2 = fabs(x[j] - upb[j])*diag[f];
                    } 
                }
            }
        }
        fprintf(file, " idmax1: %d   idmax2: %d  \n", idmax1, idmax2);
        fprintf(file, " temp1: %f   temp2: %f    \n", temp1, temp2);
        fclose(file);

        ws->jsave = (idmax1 ? idmax1-1 : 0);
        ws->xsave = 3;
        if (temp1 < temp2) {
            ws->jsave = (idmax2 ? idmax2-1 : 0);
            ws->xsave = 4;
        }
        for (j=0; j<n; ++j) {
            if (lowb != NULL && x[j] < lowb[j]) x[j] = lowb[j];
            if (upb != NULL && x[j] > upb[j]) x[j] = upb[j];
            if (fit[j] != 0) wa1[fit[j]-1] = wa2[fit[j]-1] - x[j];
        }

        size_t *idx = NULL;
        file = fopen("debugoptim.txt","a");
        fprintf(file, " new p: \n");
        for (j=0; j<nfit; ++j) {
            fprintf(file, " %f \n",wa1[j]);
        }
        fclose(file);
        ws->pnorm = oplmScaledENorm(nfit, diag, wa1, idx, wa3);
    }

    file = fopen("debugoptim.txt","a");
    fprintf(file, " new pnorm: %f \n",ws->pnorm);
    fclose(file);

    /* On the first iteration, adjust the initial step bound. */
    if (ws->iter == 1 && ws->delta > ws->pnorm) ws->delta = ws->pnorm;

    /* Evaluate the function at X + P and calculate its norm.
       Save FVEC. */
    for(i=0 ; i<m ; ++i) {
        wa4[i] = fvec[i];
    }
    file = fopen("debugoptim.txt","a");
    fprintf(file, " ready to return");
    fclose(file);

    return (ws->stage = oplmSTAGE_FN);
}

#undef jfac_
#undef ipvt_


/*---------------------------------------------------------------------------*/
/*
 * Function LMPAR
 *
 * Given an M by N matrix A, an N by N nonsingular diagonal matrix D, an
 * M-vector B, and a positive number DELTA, the problem is to determine a
 * value for the parameter PAR such that if X solves the system
 *
 *       A*X = B ,     sqrt(PAR)*D*X = 0 ,
 *
 * in the least squares sense, and DXNORM is the Euclidean
 * norm of D*X, then either PAR is zero and
 *
 *          (DXNORM - DELTA) <= 0.1*DELTA ,
 *
 * or PAR is positive and
 *
 *       abs(DXNORM - DELTA) <= 0.1*DELTA .
 *
 * this subroutine completes the solution of the problem if it is provided
 * with the necessary information from the QR factorization, with column
 * pivoting, of A.  That is, if A*P = Q*R, where P is a permutation matrix,
 * Q has orthogonal columns, and R is an upper triangular matrix with
 * diagonal elements of nonincreasing magnitude, then LMPAR expects the
 * full upper triangle of R, the permutation matrix P, and the first N
 * components of (Q transpose)*B.  On output LMPAR also provides an upper
 * triangular matrix S such that
 *
 *        t   t                   t
 *       P *(A *A + PAR*D*D)*P = S *S .
 *
 * S is employed within LMPAR and may be of separate interest.
 *
 * only a few iterations are generally needed for convergence of the
 * algorithm.  If, however, the limit of 10 iterations is reached, then the
 * output PAR will contain the best value obtained so far.
 *
 * The subroutine statement is
 *
 *   function LMPAR(N,R,LDR,IPVT,DIAG,QTB,DELTA,PAR,X,SDIAG,WA1,WA2)
 *
 * where
 *
 *   N is a positive integer input variable set to the order of R.
 *
 *   R is an N by N array.  On input the full upper triangle must contain
 *     the full upper triangle of the matrix R.  On output the full upper
 *     triangle is unaltered, and the strict lower triangle contains the
 *     strict upper triangle (transposed) of the upper triangular matrix S.
 *
 *   LDR is a positive integer input variable not less than N which
 *     specifies the leading dimension of the array R.
 *
 *   IPVT is an integer input array of length N which defines the
 *     permutation matrix P such that A*P = Q*R.  Column j of P is column
 *     IPVT(j) of the identity matrix.
 *
 *   DIAG is an input array of length N which must contain the diagonal
 *     elements of the matrix D.
 *
 *   QTB is an input array of length N which must contain the first N
 *     elements of the vector (Q transpose)*B.
 *
 *   DELTA is a positive input variable which specifies an upper
 *     bound on the euclidean norm of D*X.
 *
 *   PAR is a nonnegative variable. on input PAR contains an initial
 *     estimate of the Levenberg-Marquardt parameter.  On output the
 *     function returns the final estimate.
 *
 *   X is an output array of length N which contains the least squares
 *     solution of the system A*X = B, sqrt(PAR)*D*X = 0, for the output
 *     PAR.
*
*   SDIAG is an output array of length N which contains the diagonal
*     elements of the upper triangular matrix S.
*
*   WA1 and WA2 are work arrays of length N.
*
* Subprograms called
*
*   MINPACK-supplied ... dpmpar,enorm,qrsolv
*
*   FORTRAN-supplied ... dabs,dmax1,dmin1,sqrt
*
* Argonne National Laboratory.  MINPACK project.  March 1980.
* Burton S. Garbow, Kenneth E. Hillstrom, Jorge J. More
*
* Centre de Recherches Astronomiques de Lyon.  Conversion to C with some
* improvements.  July 2004 - June 2005.  Eric Thiébaut, Clémentine Béchet.
*/
void oplmPar(const size_t n, const size_t nalmost, double r[], 
              const size_t ldr, const size_t ipvt[], const double diag[], 
              const double qtb[], const double delta, double *par_ptr, 
              double x[], 
              double sdiag[], double wa1[], double wa2[])
{
#define MAXITER 10 /* maximum number of iterations */
    const double p1 = 0.1;
    const double p001 = 1.0e-3;
    const double zero = 0.0;
    const double one = 1.0;
    const double dwarf = DBL_MIN; /* DWARF is the smallest positive magnitude */
    double dxnorm,fp,gnorm,par,parc,parl,paru,sum,temp;
    size_t i, j, k, l, n1, iter, rank;
    FILE *file;

    /* Definitions and adjustments to mimic FORTRAN indexing. */
#undef r_
#define r_(i1,i2) r[(i2)*ldr + (i1)]
#undef ipvt_
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
# define ipvt_(i1) (ipvt[i1] - 1)
#else
# define ipvt_(i1) (ipvt[i1])
#endif

    /* Compute and store in X the Gauss-Newton direction.  If the Jacobian is
       rank-deficient, obtain a least squares solution. */
    n1 = n - nalmost;
    for (j=n1; j<n; ++j) {
        wa1[j] = qtb[j];
    }
    rank = n1;
    for (j=0 ; j<n1 ; ++j) {
        if (r_(j,j) == zero) {
            rank = j;
            do {
                wa1[j] = zero;
            } while (++j < n1);
            break;
        }
        wa1[j] = qtb[j];
    }
    for (j=n1; j<n; ++j) {
        wa1[j] /= r_(j,j);
    }

    for (k=1 ; k<=rank ; ++k) {
        j = rank - k;
        wa1[j] /= r_(j,j);
        temp = wa1[j];
        for (i=0 ; i<j ; ++i) {
            wa1[i] -= r_(i,j)*temp;
        }
    }
    for (j=0 ; j<n ; ++j) {
        l = ipvt_(j);
        x[l] = wa1[j];
    }

    /* Evaluate the function at the origin, and test for acceptance of the
       Gauss-Newton direction. */
    for (j=0 ; j<n ; ++j) {
        wa2[j] = diag[j]*x[j];
    }
    dxnorm = oplmENorm(n, wa2);
    fp = dxnorm - delta;
    if (fp <= p1*delta) {
        *par_ptr = zero;
        return;
    }

    /* If the Jacobian is not rank deficient, the Newton step provides a
       lower bound, PARL, for the zero of the function.  Otherwise set this
       bound to zero. */
    if (rank < n) {
        parl = zero;
    } else {
        temp = one/dxnorm;
        for (j=0 ; j<n ; ++j) {
            l = ipvt_(j);
            wa1[j] = (wa2[l]*temp)*diag[l];
        }
        for (j=0 ; j<n ; ++j) {
            sum = zero;
            for (i=0 ; i<j ; ++i) {
                sum += r_(i,j)*wa1[i];
            }
            wa1[j] = (wa1[j] - sum)/r_(j,j);
        }
        temp = oplmENorm(n, wa1);
        parl = ((fp/delta)/temp)/temp;
    }

    /* Calculate an upper bound, PARU, for the zero of the function. */
    for (j=0 ; j<n ; ++j) {
        sum = zero;
        for (i=0 ; i<=j ; ++i) {
            sum += r_(i,j)*qtb[i];
        }
        l = ipvt_(j);
        wa1[j] = sum/diag[l];
    }
    gnorm = oplmENorm(n, wa1);
    paru = gnorm/delta;
    if (paru == zero) paru = dwarf/oplmMin(delta,p1);

    /* If the input PAR lies outside of the interval (PARL,PARU),
       set PAR to the closer endpoint. */
    par = *par_ptr;
    if (par < parl) par = parl;
    if (par > paru) par = paru;
    if (par == zero) par = gnorm/dxnorm;

    /* Beginning of an iteration. */
    for (iter=1 ; ; ++iter) {

        /* Evaluate the function at the current value of PAR. */
        if (par == zero && (par = p001*paru) < dwarf) {
            par = dwarf;
        }
        temp = sqrt(par);
        for (j=0 ; j<n ; ++j) {
            wa1[j] = temp*diag[j];
        }
        oplmQRSolv(n, r, ldr, ipvt, wa1, qtb, x, sdiag, wa2);
        /*instead of...
          for (j=0 ; j<n ; ++j) {
          wa2[j] = diag[j]*x[j];
          }
         */
        for (j=0 ; j<n ; ++j) {
            l = ipvt_(j);
            wa2[j] = diag[l]*x[l];/* FIXME: try this code...*/
        }
        dxnorm = oplmENorm(n, wa2);
        temp = fp;
        fp = dxnorm - delta;
        file = fopen("debugoptim.txt","a");
        fprintf(file, " through oplmPar");
        fprintf(file, " dxnorm: %f \n", dxnorm);
        fclose(file);

        /* If the function is small enough, accept the current value of PAR.
           Also test for the exceptional cases where PARL is zero or the number
           of iterations has reached MAXITER. */
        if (iter >= MAXITER || fabs(fp) <= p1*delta
            || (parl == zero && fp <= temp && temp < zero)) {
            break;
        }

        /* Compute the Newton correction. */
        temp = one/dxnorm;
        /* and modified this too !!
           for (j=0 ; j<n ; ++j) {
           l = ipvt_(j);
           wa1[j] = (wa2[l]*temp)*diag[l];
           }
         */
        /* For this !! */
        for (j=0 ; j<n ; ++j) {
            l = ipvt_(j);
            wa1[j] = (wa2[j]*temp)*diag[l];
        }

        for (j=0 ; j<n ; ++j) {
            file = fopen("debugoptim.txt","a");
            fprintf(file, " sdiag: %f  wa1:  %f\n", sdiag[j], wa1[j]);
            fclose(file);
            /* FIXME: there nmust be a bugg here, because no check is done on SDIAG 
             * nullity . I modified the code... */
            if (wa1[j] != zero) wa1[j] /= sdiag[j];
            temp = wa1[j];
            for (i=j+1 ; i<n ; ++i) {
                wa1[i] -= r_(i,j)*temp;
            }
        }
        temp = oplmENorm(n, wa1);
        file = fopen("debugoptim.txt","a");
        fprintf(file, " temp: %f \n", temp);
        fclose(file);
        parc = ((fp/delta)/temp)/temp;

        /* Depending on the sign of the function, update PARL or PARU. */
        if (fp > zero && parl < par) parl = par;
        if (fp < zero && paru > par) paru = par;

        /* Compute an improved estimate for PAR. */
        if ((par += parc) < parl) par = parl;
        file = fopen("debugoptim.txt","a");
        fprintf(file, "an iter inside oplmPar");
        fclose(file);
    }

    /* Save new value of Levenberg-Marquardt parameter. */
    *par_ptr = par;
    file = fopen("debugoptim.txt","a");
    fprintf(file, " exited oplmPar");
    fclose(file);

#undef MAXITER
#undef r_
#undef ipvt_
}

/*---------------------------------------------------------------------------*/
