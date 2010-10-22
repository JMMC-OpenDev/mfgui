#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.27 2010-10-22 11:21:06 mella Exp $"
#
# Makefile of mfgui module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.26  2010/10/21 11:13:04  mella
# add castor generation class in the 'all' target
#
# Revision 1.25  2009/10/02 10:08:32  ccmgr
# generate LITproGUI instead of previous ModelFitting
#
# Revision 1.24  2009/10/02 08:44:58  mella
# fix jnlp file name
#
# Revision 1.23  2009/04/28 11:11:23  mella
# add icons into jar
#
# Revision 1.22  2009/03/23 10:12:42  mella
# add mfguiReadAndWriteXml programm
#
# Revision 1.21  2009/03/20 09:54:04  mella
# do not handle castor in main makefile targets
#
# Revision 1.20  2009/03/20 06:30:29  mella
# Change due to castor 1.3
#
# Revision 1.19  2009/03/12 09:29:05  mella
# add all xsl of fr.jmmc.mf dir
#
# Revision 1.18  2008/11/20 14:28:33  mella
# castor is now in jmcs module
#
# Revision 1.17  2008/10/17 10:11:26  mella
# Jalopization of major refactoring mimicking searchcal template
#
# Revision 1.16  2008/10/06 15:06:29  mella
# set all target as first one
#
# Revision 1.15  2008/09/26 08:27:18  mella
# Force castor to build classe on make 'all'
#
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
                 fr/jmmc/mf/gui/icons/*.png \
                 fr/jmmc/mf/*.xsl \

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES = LITproGUI \
				   mfguiReadAndWriteXml\
									 mfguiShowXmlPlot

#
# <brief description of ModelFitting java program>
LITproGUI_CLASS     = fr.jmmc.mf.gui.ModelFitting
mfguiReadAndWriteXml_CLASS     = fr.jmmc.mf.ReadAndWriteXml
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
INSTALL_FILES = ../lib/topcat-full.jar
																

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

JNLP=LITpro
#
# TARGETS
# -------

all: do_castor do_all
	@echo " . . . 'all' done" 
	

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all
	@echo "Deploy $(JNLP).jnlp and install $(JNLP).jar"
	jmcsDeployJnlp $(JNLP).jnlp
	@cp $$HOME/public_html/$(JNLP)/$(JNLP).jar $(INSTALL_ROOT)/lib
	@echo " . . . installation done"

do_castor:
	./mfguiGenerateClasses.sh

#___oOo___
