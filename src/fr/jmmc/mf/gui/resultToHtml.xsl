<?xml version="1.0" encoding="UTF-8" ?>

<!--
Document   : resultToHtml.xsl
Created on : 16 fevrier 2007, 16:51
Author     : mella
Description:
Generate Html view of given xml settings files .
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exslt="http://exslt.org/common"
    xmlns:math="http://exslt.org/math"
    xmlns:date="http://exslt.org/dates-and-times"
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings"
    xmlns:dyn="http://exslt.org/dynamic"
    xmlns:saxon="http://icl.com/saxon"
    xmlns:xt="http://www.jclark.com/xt"
    xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
    xmlns:test="http://xmlsoft.org/XSLT/"
    extension-element-prefixes="exslt math date func set str dyn saxon xt libxslt test"
    exclude-result-prefixes="math str" >
 
    <xsl:output method="html"/>

    <!-- The input document may not contain all settings related part.
    Result should always be present -->
    <xsl:variable name="root" select="/" />

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="settings">
        <xsl:apply-templates select=".//result[position()=last()]"/>
    </xsl:template>

    <xsl:template name="get_standard_deviation">
        <xsl:param name="world"/>
        <xsl:param name="paramName"/>
        <xsl:variable name="worldCopy">
            <xsl:copy-of select="exslt:node-set($world)/*"/>
        </xsl:variable>
        <xsl:if test="exslt:node-set($worldCopy)//_fitter/names[text()=$paramName]">
            <xsl:value-of select="exslt:node-set($worldCopy)//_fitter//stdev/text()"/>
        </xsl:if>
        <xsl:for-each select="exslt:node-set($worldCopy)//_fitter/names//td">
            <xsl:variable name="pos" select="position()"/>
            <xsl:if test="text()=$paramName">
                <xsl:value-of select="exslt:node-set($worldCopy)//_fitter//stdev//td[position()=$pos]"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="hasFlag">
    <xsl:param name="p_value"/>
    <xsl:param name="p_flag"/>
    <xsl:variable name="value" select="$p_value"/>
    <xsl:variable name="flag" select="$p_flag"/>
    <xsl:variable name="refs">
            <v v="0">
                <f v="1">0</f>
                <f v="2">0</f>
                <f v="4">0</f>
                <f v="8">0</f>
            </v>
            <v v="1">
                <f v="1">1</f>
                <f v="2">0</f>
                <f v="4">0</f>
                <f v="8">0</f>
            </v>
            <v v="2">
                <f v="1">0</f>
                <f v="2">1</f>
                <f v="4">0</f>
                <f v="8">0</f>
            </v>
            <v v="3">
                <f v="1">1</f>
                <f v="2">1</f>
                <f v="4">0</f>
                <f v="8">0</f>
            </v>
            <v v="4">
                <f v="1">0</f>
                <f v="2">0</f>
                <f v="4">1</f>
                <f v="8">0</f>
            </v>
            <v v="5">
                <f v="1">1</f>
                <f v="2">0</f>
                <f v="4">1</f>
                <f v="8">0</f>
            </v>
            <v v="6">
                <f v="1">0</f>
                <f v="2">1</f>
                <f v="4">1</f>
                <f v="8">0</f>
            </v>
            <v v="7">
                <f v="1">1</f>
                <f v="2">1</f>
                <f v="4">1</f>
                <f v="8">0</f>
            </v>
            <v v="8">
                <f v="1">0</f>
                <f v="2">0</f>
                <f v="4">0</f>
                <f v="8">1</f>
            </v>
            <v v="9">
                <f v="1">1</f>
                <f v="2">0</f>
                <f v="4">0</f>
                <f v="8">1</f>
            </v>
            <v v="10">
                <f v="1">0</f>
                <f v="2">1</f>
                <f v="4">0</f>
                <f v="8">1</f>
            </v>
            <v v="11">
                <f v="1">1</f>
                <f v="2">1</f>
                <f v="4">0</f>
                <f v="8">1</f>
            </v>
            <v v="12">
                <f v="1">0</f>
                <f v="2">0</f>
                <f v="4">1</f>
                <f v="8">1</f>
            </v>
            <v v="13">
                <f v="1">1</f>
                <f v="2">0</f>
                <f v="4">1</f>
                <f v="8">1</f>
            </v>
            <v v="14">
                <f v="1">0</f>
                <f v="2">1</f>
                <f v="4">1</f>
                <f v="8">1</f>
            </v>
            <v v="15">
                <f v="1">1</f>
                <f v="2">1</f>
                <f v="4">1</f>
                <f v="8">1</f>
            </v>
    </xsl:variable>
    <xsl:value-of select="exslt:node-set($refs)//v[@v=$value]/f[@v=$flag]"/>
    </xsl:template>

    <xsl:template name="show_parameters">
        <xsl:param name="world"/>
        <xsl:variable name="worldCopy">
            <xsl:copy-of select="exslt:node-set($world)/*"/>
        </xsl:variable>
        <h1> Parameters </h1>
        <table border="1">
            <tr>
                <th>name</th>
                <th>value</th>
                <th>standard deviation(+/-)</th>
                <th>prev_val</th>
                <th>vmin</th>
                <th>vmax</th>
                <th>scale</th>
                <th>fixed</th>
                <th>units</th>
            </tr>
            <xsl:for-each select="exslt:node-set($worldCopy)/param/*">
                <xsl:variable name="paramName" select="name()"/>
                <tr>
                    <td>
                        <xsl:value-of select="name()"/>
                    </td>
                    <td>
                        <xsl:value-of select="value"/>
                    </td>
                    <td>
                        <xsl:call-template name="get_standard_deviation">
                            <xsl:with-param name="world" select="$worldCopy"/>
                            <xsl:with-param name="paramName" select="$paramName"/>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:choose>
                            <xsl:when test="exslt:node-set($worldCopy)//_fitter/a_tracks">
                                <xsl:for-each select="exslt:node-set($worldCopy)/_fitter/names//td">
                                    <xsl:variable name="paramPosition" select="position()"/>
                                    <xsl:if test=".=$paramName">
                                        <xsl:value-of select="exslt:node-set($worldCopy)//_fitter/a_tracks//tr[position()=$paramPosition]//td[1]"/>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'---'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                    <td>
                        <xsl:value-of select="vmin"/>
                        </td>
                        <td>
                            <xsl:value-of select="vmax"/>
                        </td>
                        <td>
                            <xsl:choose>
                                <xsl:when test="scale/text()">
                                    <xsl:value-of select="scale"/>
                                </xsl:when>
                                <xsl:otherwise><xsl:value-of select="'AUTO'"/></xsl:otherwise>
                            </xsl:choose>
                            
                        </td>
                        <td>
                            <xsl:call-template name="hasFlag">
                                <xsl:with-param name="p_value" select="flags"/>
                                <xsl:with-param name="p_flag" select="1"/>
                            </xsl:call-template>
                        </td>
                    <td>
                        <xsl:value-of select="units"/>
                    </td>                    
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>

    <xsl:template match="result">
        <div class="box">
      
            <xsl:variable name="paramTable">
                <xsl:call-template name="show_parameters">
                    <xsl:with-param name="world" select=".//world"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:for-each select=".//error">
                <h1> Error</h1>
                <div class="box">
                    <pre>
                        <xsl:copy-of select="."/>
                    </pre>
                </div>
            </xsl:for-each>
            <xsl:for-each select=".//_fitter">
                <xsl:variable name="fitter" select="."/>
                <h1>Iterations</h1>
                    <!-- disp iteration info -->
                        Number of iterations:  
                <xsl:value-of select="iter"/> (Max Number of iterations
                <xsl:value-of select="itmax"/> )
                <!-- disp parameters info -->
                <xsl:copy-of select="$paramTable"/>

                <h1>Chi2</h1>
                <xsl:call-template name="Chi2"/>

                <xsl:if test="n_free">
                    <h1>Degrees of freedom</h1>
                    <p>Number of degrees of freedom =
                        <xsl:value-of select="n_free"/>
                    </p>
                </xsl:if>
                    <!--
                    <xsl:if test="conf_level">
                        <h1> Confidence Level = <xsl:value-of select="conf_level"/> </h1>
                    </xsl:if>
                    -->
                <xsl:if test="degen/*">
                    <h1>    Degenerated parameters</h1>
                       Degenerated parameters seen from the jacobian:
                    <br/>
                    <xsl:copy-of select="degen"/>
                 
                </xsl:if>

                <h1>Covariance matrix</h1>
                <table border="1">
                        <!-- append table headers first is empty-->
                    <tr>
                        <th></th>
                        <xsl:for-each select=".//param/*">
                            <th>
                                <xsl:value-of select="name()"/>
                            </th>
                        </xsl:for-each>
                    </tr>
                        <!-- append param name on first column then the table content-->
                    <xsl:for-each select=".//param/*">
                        <xsl:variable name="lineNb" select="position()"/>
                        <tr>
                            <td>
                                <xsl:value-of select="name()"/>
                            </td>
                            <xsl:copy-of select="$fitter//covar/table/tr[position()=$lineNb]/td"/>
                            <xsl:if test="$fitter//covar[not(table)]">
                                <td>
                                    <xsl:value-of select="$fitter//covar/text()"/>
                                </td>
                            </xsl:if>
                        </tr>
                    </xsl:for-each>
                </table>

                <h1>Correlation matrix</h1>
                <table border="1">
                        <!-- append table headers first is empty-->
                    <tr>
                        <th></th>
                        <xsl:for-each select=".//param/*">
                            <th>
                                <xsl:value-of select="name()"/>
                            </th>
                        </xsl:for-each>
                    </tr>
                        <!-- append param name on first column then the table content-->
                    <xsl:for-each select=".//param/*">
                        <xsl:variable name="lineNb" select="position()"/>
                        <tr>
                            <td>
                                <xsl:value-of select="name()"/>
                            </td>
                            <xsl:if test="$fitter//correl[not(table)]">
                                <td>
                                    <xsl:value-of select="$fitter//correl/text()"/>
                                </td>
                            </xsl:if>
                            <xsl:copy-of select="$fitter//correl/table/tr[position()=$lineNb]/td"/>
                        </tr>
                    </xsl:for-each>
                </table>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template name="Chi2">
        <xsl:if test=".//chi2_tracks//td[1]">    Initial Chi2 =
            <xsl:value-of select=".//chi2_tracks//td[1]"/>
        </xsl:if>    -  Final Chi2 =
        <xsl:value-of select=".//chi2"/>
   
        <xsl:if test=".//n_free">
            <br/>
            <xsl:if test=".//chi2_tracks//td[1]">     Initial reduced Chi2 =
                <xsl:value-of select=".//chi2_tracks//td[1] div .//n_free"/>
            </xsl:if>    -  Final reduced Chi2 =
            <xsl:value-of select=".//chi2 div .//n_free"/>
        </xsl:if>
    </xsl:template>

    <!-- Do not show any part of resultFile -->
    <xsl:template match="resultFile"></xsl:template>
</xsl:stylesheet>
