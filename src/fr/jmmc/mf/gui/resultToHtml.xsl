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

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="settings">
        <xsl:apply-templates select=".//result"/>
    </xsl:template>

    <xsl:template match="result">
    <xsl:for-each select=".//error">
            <h1> Error</h1>
            <div class="box">
                <pre>
                    <xsl:copy-of
                        select="."/>
                </pre>
            </div>
        </xsl:for-each>        
        <xsl:for-each select=".//_fitter">
                    <xsl:variable name="fitter" select="."/>
                    <h1>Iterations</h1>
                    <!-- disp iteration info -->
                    <p>
                        Number of iterations:  <xsl:value-of select="iter"/> (Max Number of iterations  <xsl:value-of select="itmax"/> )
                    </p>

                    <!-- disp parameters info -->
                    <h1> Parameters </h1>

                    <table border="1"> 
                        <tr>
                            <th>name</th>
                            <th>final value</th>
                            <th>standard deviation(+/-)</th>
                            <th>units</th>
                        </tr>
                        <xsl:for-each select="$fitter//names/table/tr/*">
                            <xsl:variable name="i" select="position()"/>
                            <xsl:variable name="paramName" select="."/>
                            <xsl:variable name="param" select="$fitter//param/*[name()=$paramName]"/>
                            <tr>
                                <td><xsl:value-of select="$paramName"/></td>
                                <td><xsl:value-of select="$param/value"/></td>
                                <xsl:copy-of select="$fitter//stdev//td[$i]"/>
                                <td><xsl:value-of select="$param/units"/></td>
                            </tr>
                        </xsl:for-each>
                    </table> 

                    <h1>Chi2</h1>
                    <xsl:call-template name="Chi2" mode="html"/>

                    <xsl:if test="n_free">
                        <h1>Degrees of freedom</h1>
                        <p>Number of degrees of freedom = <xsl:value-of select="n_free"/>
                        </p>
                    </xsl:if>
                    <!--
                    <xsl:if test="conf_level">
                        <h1> Confidence Level = <xsl:value-of select="conf_level"/> </h1>
                    </xsl:if>
                    -->
                    <xsl:if test="degen/*">
                        <h1>    Degenerated parameters</h1>
                        <p>Degenerated parameters seen from the jacobian:<br/>
                            <xsl:copy-of select="degen"/>
                        </p>
                    </xsl:if>

                    <h1>Covariance matrix</h1>
                    <table border="1">
                        <!-- append table headers first is empty-->
                        <tr>
                            <th></th>
                            <xsl:for-each select=".//param/*">
                                <th><xsl:value-of select="name()"/></th>
                            </xsl:for-each>
                        </tr>
                        <!-- append param name on first column then the table content-->
                        <xsl:for-each select=".//param/*">
                            <xsl:variable name="lineNb" select="position()"/>
                            <tr><td><xsl:value-of select="name()"/></td>
                                <xsl:copy-of select="$fitter//covar/table/tr[position()=$lineNb]/td"/>
                            </tr>
                        </xsl:for-each>                
                    </table>

                    <h1>Correlation matrix</h1>
                    <table border="1">
                        <!-- append table headers first is empty-->
                        <tr>
                            <th></th>
                            <xsl:for-each select=".//param/*">
                                <th><xsl:value-of select="name()"/></th>
                            </xsl:for-each>
                        </tr>
                        <!-- append param name on first column then the table content-->
                        <xsl:for-each select=".//param/*">
                            <xsl:variable name="lineNb" select="position()"/>
                            <tr><td><xsl:value-of select="name()"/></td>
                                <xsl:copy-of select="$fitter//correl/table/tr[position()=$lineNb]/td"/>
                            </tr>
                        </xsl:for-each>                
                    </table>
                </xsl:for-each>
    </xsl:template>

    <xsl:template name="Chi2" mode="html">
        <p>
            Initial Chi2 = <xsl:value-of select=".//chi2_tracks//td[1]"/>    -  Final Chi2 = <xsl:value-of select=".//chi2"/>
        </p>
        <xsl:if test=".//n_free">
            <p>
                Initial reduced Chi2 = <xsl:value-of select=".//chi2_tracks//td[1] div .//n_free"/>    -  Final reduced Chi2 = <xsl:value-of select=".//chi2 div .//n_free"/>
            </p>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
