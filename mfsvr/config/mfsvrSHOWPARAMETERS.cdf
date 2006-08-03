<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrSHOWPARAMETERS.cdf,v 1.2 2006-06-29 10:22:54 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/06/27 09:05:42  lsauge
 Implementation of callback


paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>SHOWPARAMETERS</mnemonic>
    <desc>View Parameters list and values</desc>
    <params optional="true">
        <param optinal="true">
            <name>model</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>show model parameters</desc>
        </param>
        <param>
            <name>engine</name>
            <type>boolean</type>
            <defaultValue><boolean>true</boolean></defaultValue>
            <desc>show parameters passed to the fitengine</desc>
        </param>
    </params>
</cmd>
