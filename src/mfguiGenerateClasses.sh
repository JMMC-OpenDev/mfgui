#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: mfguiGenerateClasses.sh,v 1.1 2006-09-26 12:13:07 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
#*******************************************************************************

#/**
# @file
# brief description of the shell script, which ends at this dot.
#
# @synopsis
# \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
#                     [\e \<option1\> ... \e \<optionN\>] 
#
# @param param1 : description of parameter 1, if it exists
# @param paramN : description of parameter N, if it exists
#
# @opt
# @optname option1 : description of option 1, if it exists
# @optname optionN : description of option N, if it exists
# 
# @details
# OPTIONAL detailed description of the shell script follows here.
# 
# @usedfiles
# OPTIONAL. If files are used, for each one, name, and usage description.
# @filename fileName1 :  usage description of fileName1
# @filename fileName2 :  usage description of fileName2
#
# @env
# OPTIONAL. If needed, environmental variables accessed by the program. For
# each variable, name, and usage description, as below.
# @envvar envVar1 :  usage description of envVar1
# @envvar envVar2 :  usage description of envVar2
# 
# @warning OPTIONAL. Warning if any (software requirements, ...)
#
# @ex
# OPTIONAL. Command example if needed
# \n Brief example description.
# @code
# Insert your command example here
# @endcode
#
# @sa OPTIONAL. See also section, in which you can refer other documented
# entities. Doxygen will create the link automatically. For example, 
# @sa \<entity to refer\>
# 
# @bug OPTIONAL. Known bugs list if it exists.
# @bug Bug 1 : bug 1 description
#
# @todo OPTIONAL. Things to forsee list.
# @todo Action 1 : action 1 description
# 
# 
# */


# signal trap (if any)



#___oOo___
#!/bin/bash


# Generate the class path for Java
CLASSPATH=`mkfMakeJavaClasspath`
# generate model java source from xml schema
java -classpath $CLASSPATH org.exolab.castor.builder.SourceGenerator -i mfmdl.xsd -f -package jmmc.mf.models  $*

exit 0

cat >> jmmc/mf/models/Model.java << EOM
 /**
     * Returns one string descrition.
     */
    public String toString(){
        return getName()+"["+getType()+"]";
    }

    /**
     * Returns one copy of the model (clone).
     */
    public Model getClone(){
         
         Model m = null;
         try{
           m = (Model) this.clone();
           Parameter[] params=getParameter();
           m.setParameter(params);                      
        }catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }

, Cloneable

EOM
