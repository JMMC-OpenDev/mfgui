#!/usr/bin/gnuplot -raise -persist

plot [] [0:1]"mfmdlTest2.data" using 1:4 with lines

set xlabel "Projected baseline"
set ylabel "Squared visibility"

set term postscript
set output "mfmdlTest2.eps"
replot
