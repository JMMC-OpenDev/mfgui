<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrSETDATA.cdf,v 1.2 2006-07-05 14:50:57 lsauge Exp $"

 History 
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/06/27 09:05:42  lsauge
 Implementation of callback

 *** empty log message ***

-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>SETDATA</mnemonic>
    <desc>Define the data to be fitted.</desc>
    <params>
        <param>
            <name>fileName</name>
            <type>string</type>
            <desc>Input data filename</desc>
        </param>
        <param>
            <name>add</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>add data to the previous list</desc>
        </param>
    </params>        
</cmd>
