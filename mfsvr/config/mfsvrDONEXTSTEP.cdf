<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrDONEXTSTEP.cdf,v 1.2 2006-08-02 09:03:05 lsauge Exp $"

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
    <mnemonic>DONEXTSTEP</mnemonic>
    <desc>DoNextStep description</desc>
    <params optional="true">
        <param optional="true">
            <name>iter</name>
            <type>integer</type>
            <minValue><integer>1</integer></minValue>
            <defaultValue><integer>1</integer></defaultValue>
            <desc>Number of iteration if this loop</desc>
        </param>
        <param>
            <name>reset</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>Force to reset the engine</desc>
        </param>
    </params>
</cmd>
