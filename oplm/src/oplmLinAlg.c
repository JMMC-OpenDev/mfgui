/*******************************************************************************
 * JMMC project
 * 
 * "@(#) $Id: oplmLinAlg.c,v 1.3 2006-05-11 13:04:56 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/31 08:13:03  gzins
 * Cleaned up file history
 *
 ******************************************************************************/

/**
 * @file
 * This file contains functions for solving nonlinear equations and nonlinear
 * least squares problems. These functions have been converted from Fortran 77
 * source code (MINPACK project) to C with some improvements.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: oplmLinAlg.c,v 1.3 2006-05-11 13:04:56 mella Exp $";

/* 
 * System Headers
 */
#include <stdio.h>
#include <math.h>
#include <float.h>

/* 
 * Local Headers
 */
#include "oplm.h"
#include "oplmPrivate.h"

/**
 * Calculate euclidean norm of vector.
 * 
 * This function avoids overflows and performs two passes through the vector:
 * one to find the maximum absolute value, the other to compute the normalized
 * sum of squares.
 * 
 * @param n is a positive integer input variable.
 * @param x is an input array of length n.
 *
 * @return euclidean norm of vector
 */
double oplmENorm(size_t n, const double x[])
{
    const double zero = 0.0;
    const double one = 1.0;
    double a, b, s, t;
    size_t i;

    /* First pass, find maximum absolute value.  This avoids the numerous
       renormalizations required when only one pass is performed and the
       maximum absolute value is updated as element are read (therefore less
       operations and less rounding errors). */
    a = b = zero;
    for (i=0 ; i<n ; ++i) 
    {
        if (x[i] < a) a = x[i];
        if (x[i] > b) b = x[i];
    }
    a = -a;
    if (a < b) a = b; /* A is max(abs(X)) */
    if (! a) return zero;

    /* Second pass, accumulate the sum of (normalized) squares. */
    b = one/a;
    s = zero;
    for (i=0 ; i<n ; ++i) 
    {
        t = b*x[i];
        s += t*t;
    }

    return a*sqrt(s);
}

/**
 * Compute QR factorization of rectangular matrix.
 * 
 * This function uses Householder transformations with column pivoting
 * (optional) to compute a QR factorization of the \e m by N matrix A. That
 * is, oplmQRFac determines an orthogonal matrix Q, a permutation matrix P, and
 * an upper trapezoidal matrix R with diagonal elements of nonincreasing
 * magnitude, such that A*P = Q*R.  The Householder transformation for
 * column K, K = 1,2,...,min(\e m,N), is of the form
 * 
 *                       t
 *       I - (1/U(K))*U*U
 * 
 * where U has zeros in the first K-1 positions.  The form of this
 * transformation and the method of pivoting first appeared in the
 * corresponding LINPACK subroutine.
 * 
 * @param m is a positive integer input variable set to the number of rows of
 * <em>a</em>.
 * 
 * @param n is a positive integer input variable set to the number of columns of
 * <em>a</em>.
 *
 * @param nFitPtr ???
 * 
 * @param a is an <em>m</em> by <em>n</em> array. On input <em>a</em> contains
 * the matrix for which the QR factorization is to be computed. on output the
 * strict upper trapezoidal part of <em>a</em> contains the strict upper
 * trapezoidal part of r, and the lower trapezoidal part of <em>a</em> contains
 * a factored form of q (the non-trivial elements of the u vectors described
 * above).
 * 
 * @param lda is a positive integer input variable not less than <em>m</em>
 * which specifies the leading dimension of the array <em>a</em>.
 * 
 * @param pivot is a logical input variable. If <em>pivot</em> is set true, then
 * column pivoting is enforced. If  <em>pivot</em> is set false, then no column
 * pivoting is done.
 * 
 * @param ipvt is an integer output array of length LIPVT. <em>ipvt</em> defines
 * the permutation matrix <em>p</em> such that <em>a*p = q*r</em>.  column j of
 * P is column <em>ipvt(j)</em> of the identity matrix.  If <em>pivot</em> is
 * false, <em>ipvt</em> is not referenced.  If <em>pivot</em> is true,
 * <em>ipvt</em> must have at least <em>n</em> elements.
 * 
 * @param fit ???
 *
 * @param constr ???
 *
 * @param rdiag is an output array of length <em>n</em> which contains the
 * diagonal elements of <em>r</em>.
 * 
 * @param acnorm is an output array of length <em>n</em> which contains the
 * norms of the corresponding columns of the input matrix <em>a</em>.  if this
 * information is not needed, then <em>acnorm</em> can coincide with
 * <em>rdiag</em>.
 * 
 * @param wa is a work array of length <em>n</em>. If <em>pivot</em> is false,
 * then <em>wa</em> can coincide with <em>rdiag</em>.
 * 
 * @author
 * \li Argonne National Laboratory.  MINPACK project.  March 1980.  Burton S.
 * Garbow, Kenneth E. Hillstrom, Jorge J. More
 * 
 * \li Centre de Recherches Astronomiques de Lyon.  Conversion to C with some
 * improvements.  July 2004 - June 2005.  Eric Thiébaut, Clémentine Béchet.
 */
void oplmQRFac(const size_t m, const size_t n, size_t *nFitPtr, double a[], 
                const size_t lda, const int pivot, size_t ipvt[], 
                const size_t fit[], const size_t constr[], double rdiag[], 
                double acnorm[], double wa[])
{
    const double one = 1.0;
    const double zero = 0.0;
    const double eps = 20.0*DBL_EPSILON; /* DBL_EPSILON is the machine
                                            relative precision */
    double ajnorm, sum, temp;
    size_t i, j, jp1, k, kmax, minmn, idx=0, ninact=0, nalmost=0;
    size_t flag_code;
    FILE *file;

    /* Definitions and adjustments to mimic FORTRAN indexing. */
#undef a_
#define a_(i1,i2) (a[(i2)*lda + (i1)])

    /* 
     * temporarily, ipvt is used to associate index in the N-list of parameters
     * with the index inside the reordered Jacobian 
     */
    if (pivot != 0 || fit != NULL) 
    {
        for (j=0; j<n; ++j) 
        {
            if (fit == NULL) 
            {
                ipvt[j] = j;
            }
            else if (fit[j] != 0)
            {
                ipvt[fit[j]-1] = j;
            }
        }

        /*
         * IPVT must be initialized so as to take into account active
         * constraints. It means that the active constraints indices are
         * displaced to the last columns of the jacobian matrix. 
         */
        if (constr != NULL)
        {
            flag_code = 6;
            k = 0;
            for (j=0; j<*nFitPtr; ++j) 
            {
                /* 
                 * Permutation is going to be proceeded between column J and
                 * column FIT[K]-1 inside FJAC 
                 */
                while (k < *nFitPtr && constr[ipvt[k]] <= flag_code)
                {
                    k = k+1;
                } 
                if (k < *nFitPtr) 
                {
                    file = fopen("debugoptim.txt", "a");
                    fprintf(file, "constr :  %d    k: %d    flag_code: %d \n", 
                            constr[ipvt[k]], k, flag_code);
                    fclose(file);
                }
                if (k == *nFitPtr)
                {
                    flag_code = flag_code + (flag_code ? -2 : 0);
                    k = j+1;
                    continue;
                }
                else 
                {
                    if (k != j) 
                    {
                        /* J is the column index in FJAC after permutation */
                        idx = ipvt[j];
                        ipvt[j] = ipvt[k];
                        ipvt[k] = idx;
                        for (i=0 ; i<m ; ++i) 
                        {
                            /* #warning "speedup possible with op_swap_d" */
                            temp = a_(i,k);
                            a_(i,k) = a_(i,j);
                            a_(i,j) = temp;
                        }
                    }
                    if (flag_code >= 5) 
                    {
                        wa[j] = rdiag[j] = oplmENorm(m, &a_(0,j));
                        acnorm[fit[ipvt[j]]-1] = wa[j];
                    }
                    else  wa[j] = rdiag[j] = acnorm[fit[ipvt[j]]-1] = zero;
                    k=j+1;
                }
            }
        }
    }
    /* 
     * Review the IPVT values so that the indices correspond to the original
     * columns inside A_
     */
    for (j=0; j<*nFitPtr; ++j)
    {
        if (fit == NULL)
        {
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
            ipvt[j] = j+1;
#else
            ipvt[j] = j;
#endif
        }
        else 
        {
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
            ipvt[j] = fit[ipvt[j]];
#else
            ipvt[j] = fit[ipvt[j]]-1;
#endif
        }
    } 

    if (fit == NULL)
    {
        for (j=0; j<*nFitPtr; ++j)
        {
            wa[j] = rdiag[j] = acnorm[j] = oplmENorm(m, &a_(0,j));
        } 
        ninact = n;  
    }
    else 
    {
        for (j=0; j<n; ++j)
        {
            ninact += (constr[j] == 7 ? 1 : 0);
            nalmost +=(constr[j] == 5 || constr[j] == 6 ? 1 : 0);
        }
    }
    file = fopen("debugoptim.txt", "a");
    if (constr != NULL)
    {
        fprintf(file, "constr: \n");
        for (j=0; j<n; ++j)
        {
            fprintf(file, " %d \n", constr[j]);
        }
        fprintf(file, "ipvt:\n");
        for (j=0; j<*nFitPtr; ++j)
        {
            fprintf(file, " %d \n", ipvt[j]);
        }
    }
    fclose(file);


    /* Reduce A to R with Householder transformations. */
    minmn = oplmMin(m,ninact);
    for (j=0 ; j<minmn ; ++j)
    {
        jp1 = j + 1;
        if (pivot != 0 || constr != NULL)
        {
            /* Bring the column of largest norm into the pivot position. */
            kmax = j;
            temp = rdiag[kmax];
            for (k=jp1 ; k<ninact ; ++k) 
            {
                if (rdiag[k] > temp)
                {
                    kmax = k;
                    temp = rdiag[kmax];
                }
            }
            if (kmax != j)
            {
                for (i=0 ; i<m ; ++i)
                {
                    /* #warning "speedup possible with op_swap_d" */
                    temp = a_(i,j);
                    a_(i,j) = a_(i,kmax);
                    a_(i,kmax) = temp;
                }
                rdiag[kmax] = rdiag[j];
                wa[kmax] = wa[j];
                k = ipvt[j];
                ipvt[j] = ipvt[kmax];
                ipvt[kmax] = k;
            }
        }

        /* Compute the Householder transformation to reduce the
           j-th column of A to a multiple of the j-th unit vector. */
        ajnorm = oplmENorm(m-j, &a_(j,j));
        if (ajnorm == zero)
        {
            continue;
        }
        if (a_(j,j) < zero) 
        {
            ajnorm = -ajnorm;
        }
        temp = one/ajnorm;
        for (i=j ; i<m ; ++i) 
        {
            a_(i,j) *= temp;
        }
        a_(j,j) += one;

        /* Apply the transformation to the remaining columns
           and update the norms. */
        for (k=jp1 ; k<ninact ; ++k)
        {
            sum = zero;
            /* Compute TEMP :
             * about dot product between column J of the submatrix and U 
             */
            for (i=j ; i<m ; ++i)
            {
                /* #warning "speedup possible with op_dot_d" */
                sum += a_(i,j)*a_(i,k);
            }
            temp = sum/a_(j,j);
            /* Apply to column J*/
            for (i=j ; i<m ; ++i)
            {
                /* #warning "speedup possible with op_axpy_d" */
                a_(i,k) -= temp*a_(i,j);
            }
            if ((pivot != 0 || constr != NULL) && rdiag[k] != zero)
            {
                temp = a_(j,k)/rdiag[k];
                temp = one - temp*temp;
                if (temp > zero)
                {
                    rdiag[k] *= sqrt(temp);
                }
                else 
                {
                    rdiag[k] = zero;
                }
                temp = rdiag[k]/wa[k];
                if (temp*temp <= eps)
                {
                    rdiag[k] = oplmENorm(m-jp1, &a_(jp1,k));
                    wa[k] = rdiag[k];
                }
            }
        }
        rdiag[j] = -ajnorm;
    }

    for (j=ninact; j<ninact+nalmost; ++j)
    {
        jp1 = j + 1;
        /* Bring the column of largest norm into the pivot position. */
        kmax = j;
        temp = rdiag[kmax];
        for (k=jp1 ; k<ninact ; ++k)
        {
            if (rdiag[k] > temp)
            {
                kmax = k;
                temp = rdiag[kmax];
            }
        }
        if (kmax != j)
        {
            for (i=0 ; i<m ; ++i) 
            {
                /* #warning "speedup possible with op_swap_d" */
                temp = a_(i,j);
                a_(i,j) = a_(i,kmax);
                a_(i,kmax) = temp;
            }
            rdiag[kmax] = rdiag[j];
            wa[kmax] = wa[j];
            k = ipvt[j];
            ipvt[j] = ipvt[kmax];
            ipvt[kmax] = k;
        }
        for (i=0; i<m; ++i)
        {
            a_(i,j) = zero;
        }
    } 
    *nFitPtr = ninact+nalmost;

#undef a_
}

/**
 * Complete solution of least squares problem.
 *
 * Given an \e m by \e n matrix \e a, an \e n by \e n diagonal matrix \e d, and
 * an M-vector \e b, the problem is to determine an \e x which solves the system
 *
 * @code
 *       a*x = b ,     d*x = 0 ,
 * @endcode
 *
 * in the least squares sense.
 *
 * This subroutine completes the solution of the problem if it is provided with
 * the necessary information from the QR factorization, with column pivoting, of
 * \e a. That is, if <em>a*p = q*r</em>, where \e p is a permutation matrix, \e
 * Q has orthogonal columns, and \e R is an upper triangular matrix with
 * diagonal elements of nonincreasing magnitude, then oplmQRSolv expects the
 * full upper triangle of \e R, the permutation matrix P, and the first \e n
 * components of <em>(q transpose)*b</em>. The system <em>a*x = b, d*x = 0</em>,
 * is then equivalent to
 *
 * @code
 *       r*z = q *b ,  p *d*p*z = 0 ,
 * @endcode
 *
 * where <em>X = P*Z</em>. If this system does not have full rank, then a least
 * squares solution is obtained. On output oplmQRSolv also provides an upper
 * triangular matrix \e s such that
 *
 * @code
 *       p *(a *a + d*d)*p = s *s .
 * @endcode
 *
 * @param n is a positive integer input variable set to the order of \e r.
 *
 * @param r is an n by n array. on input the full upper triangle must contain
 * the full upper triangle of the matrix r.  on output the full upper triangle
 * is unaltered, and the strict lower triangle contains the strict upper
 * triangle (transposed) of the upper triangular matrix s.
*
 * @param ldr is a positive integer input variable not less than n
 *         which specifies the leading dimension of the array r.
 *
 * @param ipvt is an integer input array of length n which defines the
 *         permutation matrix p such that a*p = q*r. column j of p
 *         is column ipvt(j) of the identity matrix.
 *
 * @param diag is an input array of length n which must contain the
 *         diagonal elements of the matrix d.
 *
 * @param qtb is an input array of length n which must contain the first
 *         n elements of the vector (q transpose)*b.
 *
 * @param x is an output array of length n which contains the least
 *         squares solution of the system a*x = b, d*x = 0.
 *
 * @param sdiag is an output array of length n which contains the
 *         diagonal elements of the upper triangular matrix s.
 *
 * @param wa is a work array of length n.
 *
 * @author
 * \li Argonne National Laboratory.  MINPACK project.  March 1980.  Burton S.
 * Garbow, Kenneth E. Hillstrom, Jorge J. More
 * 
 * \li Centre de Recherches Astronomiques de Lyon.  Conversion to C with some
 * improvements.  July 2004 - June 2005.  Eric Thiébaut, Clémentine Béchet.
 */
void oplmQRSolv(const size_t n, double r[], const size_t ldr,
                 const size_t ipvt[], const double diag[], 
                 const double qtb[], double x[], double sdiag[], double wa[])
{
    const double zero = 0.0;
    const double one = 1.0;
    double qtbpj,sum,temp;
    double gcos, gsin; /* coefficients of Givens rotation */
    size_t i,j,k,l,rank;

    /* Definitions and adjustments to mimic FORTRAN indexing. */
#undef r_
#define r_(i1,i2) r[(i2)*ldr + (i1)]

    /* Copy R and (Q transpose)*B to preserve input and initialize S.
       In particular, save the diagonal elements of R in X. */
    for (j=0 ; j<n ; ++j) 
    {
        x[j] = r_(j,j);
        for (i=j+1 ; i<n ; ++i) 
        {
            r_(i,j) = r_(j,i);
        }
    }
    for (j=0 ; j<n ; ++j)
    {
        wa[j] = qtb[j];
    }

    /* Eliminate the diagonal matrix D using a Givens rotation. */
    for (j=0 ; j<n ; ++j)
    {

        /* Prepare the row of D to be eliminated, locating the
           diagonal element using P from the QR factorization. */
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
        l = ipvt[j] - 1;
#else
        l = ipvt[j];
#endif
        if (diag[l] != zero) 
        {
            sdiag[j] = diag[l];
            for (k=j+1 ; k<n ; ++k)
            {
                sdiag[k] = zero;
            }

            /* The transformations to eliminate the row of D modify
               only a single element of (Q transpose)*B beyond the
               first N, which is initially zero. */
            qtbpj = zero;
            for (k=j ; k<n ; ++k) 
            {
                /* Determine a Givens rotation which eliminates the
                   appropriate element in the current row of D. */
                if (sdiag[k] == zero) 
                {
                    continue;
                }
                if (fabs(r_(k,k)) <= fabs(sdiag[k]))
                {
                    temp = r_(k,k)/sdiag[k]; /* cotangent of Givens angle */
                    gsin = one/sqrt(one + temp*temp);
                    gcos = gsin*temp;
                }
                else 
                {
                    temp = sdiag[k]/r_(k,k); /* tangent of Givens angle */
                    gcos = one/sqrt(one + temp*temp);
                    gsin = gcos*temp;
                }
                /* Compute the modified diagonal element of R and
                   the modified element of ((Q transpose)*B,0). */
                r_(k,k) = gcos*r_(k,k) + gsin*sdiag[k];
                temp  = gsin*qtbpj + gcos*wa[k];
                qtbpj = gcos*qtbpj - gsin*wa[k];
                wa[k] = temp;
                /* Accumulate the tranformation in the row of S. */
                for (i=k+1 ; i<n ; ++i)
                {
                    temp     = gsin*sdiag[i] + gcos*r_(i,k);
                    sdiag[i] = gcos*sdiag[i] - gsin*r_(i,k);
                    r_(i,k) = temp;
                }
            }
        }

        /* Store the diagonal element of S and restore
           the corresponding diagonal element of R. */
        sdiag[j] = r_(j,j);
        r_(j,j) = x[j];
    }

    /* Solve the triangular system for Z.  If the system is
       singular, then obtain a least squares solution. */
    rank = n;
    for (j=0 ; j<n ; ++j)
    {
        if (sdiag[j] == zero)
        {
            rank = j;
            break;
        }
    }
    for (j=rank ; j<n ; ++j)
    {
        wa[j] = zero;
    }
    for (k=1 ; k<=rank ; ++k)
    {
        j = rank - k;
        sum = zero;
        for (i=j+1 ; i<rank ; ++i) 
        {
            sum += r_(i,j)*wa[i];
        }
        wa[j] = (wa[j] - sum)/sdiag[j];
    }

    /* Permute the components of Z back to components of X. */
    for (j=0 ; j<n ; ++j)
    {
#if defined(FORTRAN_INDEXING) && (FORTRAN_INDEXING != 0)
        l = ipvt[j] - 1;
#else
        l = ipvt[j];
#endif
        x[l] = wa[j];
    }
#undef r_
#undef ipvt
}

/*___oOo___*/
