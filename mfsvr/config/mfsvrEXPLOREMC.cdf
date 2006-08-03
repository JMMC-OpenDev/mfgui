<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrEXPLOREMC.cdf,v 1.2 2006-07-05 14:50:36 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/06/28 12:36:35  lsauge
 First implementation


paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>EXPLOREMC</mnemonic>
    <desc>ExploreMC description</desc>
    <params optional="true">
        <param optional="true">
            <name>iter</name>
            <type>integer</type>
            <minValue><integer>1</integer></minValue>
            <defaultValue><integer>1</integer></defaultValue>
            <desc>Number of iteration if this loop</desc>
        </param>
    </params>
</cmd>
