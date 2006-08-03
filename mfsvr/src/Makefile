#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.8 2006-08-02 08:14:04 lsauge Exp $"
#
# Makefile of mfsvr module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.7  2006/07/10 12:26:55  lsauge
# Minor changes
#
# Revision 1.6  2006/07/05 14:52:38  lsauge
# Minor changes due to third party updates
#
# Revision 1.5  2006/06/28 12:34:15  lsauge
# update
#
# Revision 1.4  2006/06/27 08:58:40  lsauge
# Minor changes
#
# Revision 1.2  2006/03/31 08:19:11  gzins
# Updated for server generation
#
# Revision 1.1  2006/03/20 08:03:04  sccmgr
# Fix directory structure and add additional files
#
#*******************************************************************************
# REMARKS
#    Makefile based on the VLT/ALMA Makefile
#
#-------------------------------------------------------------------------------

#
# user definable C-compilation flags
#USER_CFLAGS = 

#
# user definable javac compilation flags
#USER_JFLAGS = 

#
# additional include and library search paths
#USER_INC = 
#USER_LIB = 

#
# MODULE CODE DESCRIPTION:
# ------------------------
# As a general rule:  public file are "cleaned" and "installed"  
#                     local (_L) are not "installed".

#
# C programs (public and local)
# -----------------------------
EXECUTABLES     = mfsvrServer
EXECUTABLES_L   = 

#
# <brief description of model-fitting server program>
mfsvrServer_OBJECTS   = mfsvrSHOWDATA_CMD 	\
	 mfsvrShowDataCB 	\
	 mfsvrPLOTMAP_CMD 	\
	 mfsvrPlotMapCB 	\
	 mfsvrEXPLOREMC_CMD 	\
	 mfsvrExploreMCCB 	\
	 mfsvrSETSCALE_CMD 	\
	 mfsvrSetScaleCB 	\
	 mfsvrPLOTMODEL_CMD 	\
	 mfsvrPlotModelCB 	\
	 mfsvrSETLMPARAM_CMD 	\
	 mfsvrSetLMParamCB 	\
	 mfsvrINITLMENGINE_CMD 	\
	 mfsvrInitLMEngineCB 	\
	 mfsvrDONEXTSTEP_CMD 	\
	 mfsvrDoNextStepCB 	\
	 mfsvrPLOTDATA_CMD 	\
	 mfsvrPlotDataCB 	\
	 mfsvrSETPARAMETER_CMD 	\
	 mfsvrSetParameterCB 	\
	 mfsvrSHOWPARAMETERS_CMD 	\
	 mfsvrShowParametersCB 	\
	 mfsvrSHOWMODEL_CMD 	\
	 mfsvrShowModelCB 	\
	 mfsvrADDMODEL_CMD 	\
	 mfsvrAddModelCB 	\
	 					mfsvrSERVER	         \
						mfsvrSETMODEL_CMD    \
						mfsvrSETDATA_CMD     \
						mfsvrGETTIME_CMD     \
						mfsvrSetModelCB      \
						mfsvrSetDataCB       \
						mfsvrGetTimeCB       \
						mfsvrParametersHandling\
						mfsvrEvaluateResiduals \
						mfsvrInitLMEngine \
	 					mfsvrPrintBody       \
						mfsvrPlotManaging	\
						mfsvrExpandModelTree\
						mfsvrServer
mfsvrServer_LDFLAGS   =
mfsvrServer_LIBS      = MCS C++ oplm fiteng mfdata oidata mfmdl cfitsio

#
# special compilation flags for single c sources
#yyyyy_CFLAGS   = 

#
# Includes (.h) files (public only)
# ---------------------------------
INCLUDES        =

#
# Libraries (public and local)
# ----------------------------
LIBRARIES       =
LIBRARIES_L     =

#
# <brief description of lllll library>
lllll_OBJECTS   =

#
# Scripts (public and local)
# --------------------------
SCRIPTS         =
SCRIPTS_L       =

#
# TCL scripts (public and local)
# ------------------------------
TCL_SCRIPTS     =
TCL_SCRIPTS_L   =

#
# <brief description of tttttt tcl-script>
tttttt_OBJECTS  =
tttttt_TCLSH    = 
tttttt_LIBS     = 

#
# TCL libraries (public and local)
# --------------------------------
TCL_LIBRARIES   =
TCL_LIBRARIES_L =

#
# <brief description of tttlll library>
tttlll_OBJECTS  = 

#
# Python scripts (public and local)
# ---------------------------------
PY_SCRIPTS     =
PY_SCRIPTS_L   =

#
# Jarfiles and their directories
# ---------------------------------
JARFILES        =

#
# <brief description of jjjfff jar files>
jjjfff_DIRS     =
jjjfff_EXTRAS   = 

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES=

#
# <brief description of jjjjjj java program>
jjjjjj_CLASS    =

#
# Command Definition Files (CDF)
# ---------------------------------
CDF_FILES       = mfsvrSHOWDATA 	\
	 mfsvrPLOTMAP 	\
	 mfsvrEXPLOREMC 	\
	 mfsvrSETSCALE 	\
	 mfsvrPLOTMODEL 	\
	 mfsvrSETLMPARAM 	\
	 mfsvrINITLMENGINE 	\
	 mfsvrDONEXTSTEP 	\
	 mfsvrPLOTDATA 	\
	 mfsvrSETPARAMETER 	\
	 mfsvrSHOWPARAMETERS 	\
	 mfsvrSHOWMODEL 	\
	 mfsvrADDMODEL 	\
	   mfsvrSETMODEL	\
					mfsvrSETDATA	\
					mfsvrGETTIME

#
# man pages to be done
# --------------------
MANSECTIONS =
MAN1 =
MAN3 =
MAN5 =
MAN7 =
MAN8 =

#
# other files to be installed
#----------------------------
INSTALL_FILES =

#
# list of all possible C-sources (used to create automatic dependencies)
# ----------------------------------------------------------------------
CSOURCENAMES = \
	$(foreach exe, $(EXECUTABLES) $(EXECUTABLES_L), $($(exe)_OBJECTS)) \
	$(foreach lib, $(LIBRARIES) $(LIBRARIES_L), $($(lib)_OBJECTS))

#
#>>>>> END OF standard rules

#
# INCLUDE STANDARDS
# -----------------
MAKEDIR = $(MCSROOT)/include
include $(MAKEDIR)/mkfMakefile

#
# TARGETS
# -------
all:	do_all
	@echo " . . . 'all' done" 

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all
	@echo " . . . installation done"

#___oOo___
