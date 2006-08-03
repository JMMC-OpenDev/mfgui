<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrADDMODEL.cdf,v 1.2 2006-07-04 08:26:51 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/06/27 09:05:41  lsauge
 Implementation of callback


paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>ADDMODEL</mnemonic>
    <desc>AddModel description</desc>
    <params optional="false">
        <param>
            <name>type</name>
            <type>integer</type>
            <minValue><integer>1</integer></minValue>
            <maxValue><integer>2</integer></maxValue>
            <desc>model type</desc>
        </param>
        <param>
            <name>polar</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>Set polar form</desc>
        </param>    
    </params>  
</cmd>
