<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrFIT.cdf,v 1.1 2006-06-05 09:49:48 gzins Exp $"

 History 
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/03/31 08:19:50  gzins
 *** empty log message ***

-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>FIT</mnemonic>
    <desc>Fit model to data.</desc>
    <params>
        <param optional="true">
            <name>step</name>
            <type>integer</type>
            <minValue><integer>1</integer></minValue>
            <desc>Number of steps to perform</desc>
        </param>
    </params>        
</cmd>
