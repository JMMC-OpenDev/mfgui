#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.15 2008-09-26 08:27:18 mella Exp $"
#
# Makefile of mfgui module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.14  2008/03/04 12:16:06  mella
# general commit  for V1.0.9.beta
#
# Revision 1.13  2008/02/18 15:25:57  mella
# added jar dependencies
#
# Revision 1.12  2007/02/26 10:28:05  mella
# General commit for 1.0.5beta
#
# Revision 1.11  2007/02/14 12:52:23  mella
# Do simple cleanup
#
# Revision 1.10  2007/02/14 11:10:13  mella
# remove not working do_xsdMapping target to all
#
# Revision 1.9  2007/02/14 11:09:15  mella
# Added do_all before and after do_xsdMapping target to all
#
# Revision 1.8  2007/02/14 11:07:48  mella
# Added do_xsdMapping target to all
#
# Revision 1.7  2007/02/14 09:28:51  mella
# Moved jmmc.* into fr.jmmc.*
#
# Revision 1.6  2007/02/12 14:27:18  mella
# Jalopyzation
#
# Revision 1.5  2006/11/21 13:17:18  mella
# blah
#
# Revision 1.4  2006/11/10 12:54:20  mella
# Added file resources.properties
#
# Revision 1.3  2006/09/26 12:44:16  mella
# Add mfguiGenerateClasses script
#
# Revision 1.2  2006/09/26 12:13:07  mella
# First revision
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
EXECUTABLES     =
EXECUTABLES_L   = 

#
# <brief description of xxxxx program>
xxxxx_OBJECTS   =
xxxxx_LDFLAGS   =
xxxxx_LIBS      =

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
SCRIPTS_L       = mfguiGenerateClasses

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
JARFILES        = mfgui

#
# other Jar Files
#
JAVA_JAR =

#
# <brief description of mfgui jar files>
mfgui_DIRS     = fr 
mfgui_EXTRAS   = fr/jmmc/mf/gui/Resources.properties \
                 fr/jmmc/mf/gui/*.html \
                 fr/jmmc/mf/gui/*.xml \
                 fr/jmmc/mf/gui/*.xsl \

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES = ModelFitting \
									 mfguiShowXmlPlot

#
# <brief description of ModelFitting java program>
ModelFitting_CLASS     = fr.jmmc.mf.gui.ModelFitting
mfguiShowXmlPlot_CLASS = fr.jmmc.mcs.ImageViewer

#
# Command Definition Files (CDF)
# ---------------------------------
CDF_FILES       =

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
INSTALL_FILES = ../lib/castor.jar \
				../lib/commons-codec-1.2.jar \
				../lib/commons-httpclient-3.0.1.jar \
				../lib/commons-httpclient-3.1-beta1.jar \
				../lib/commons-lang-2.1.jar \
				../lib/commons-logging-1.0.3.jar \
				../lib/topcat-full.jar
																

#
# list of all possible C-sources (used to create automatic dependencies)
# ----------------------------------------------------------------------
CSOURCENAMES = \
	$(foreach exe, $(EXECUTABLES) $(EXECUTABLES_L), $($(exe)_OBJECTS)) \
	$(foreach lib, $(LIBRARIES) $(LIBRARIES_L), $($(lib)_OBJECTS))

#
#>>>>> END OF standard rules

do_castor:
	./mfguiGenerateClasses.sh

#
# INCLUDE STANDARDS
# -----------------
MAKEDIR = $(MCSROOT)/include
include $(MAKEDIR)/mkfMakefile

#
# TARGETS
# -------

all:	do_castor do_all
	@echo " . . . 'all' done" 
	

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

JNLP=ModelFitting
install : install_all
	@echo "Deploy $(JNLP).jnlp and install $(JNLP).jar"
	jmcsDeployJnlp $(JNLP).jnlp
	@cp $$HOME/public_html/$(JNLP)/$(JNLP).jar $(INSTALL_ROOT)/lib
	@echo " . . . installation done"

#___oOo___
