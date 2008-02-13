<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : resultToHtml.xsl
    Created on : 16 fevrier 2007, 16:51
    Author     : mella
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    
    <!-- root document correspond to the result branch of main document
    -->
    <xsl:template match="/">
        <html>
            <body>
                <xsl:for-each select="//_fitter">
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
                        <th>standard deviation</th>
                        <th>units</th>
                    </tr>
                    <xsl:for-each select=".//param/*">
                        <tr>
                            <xsl:variable name="i" select="position()"/>
                            <xsl:variable name="paramName" select="name()"/>
                            <!-- <xsl:copy-of
                                select="$fitter//names//td[$i]"/> -->
                            <td><xsl:value-of select="$paramName"/></td>
                            <xsl:copy-of select="$fitter//a//td[$i]"/>
                            <xsl:copy-of select="$fitter//stdev//td[$i]"/>
                            <td><xsl:value-of select="./units"/></td>
                        </tr>
                    </xsl:for-each>
                </table> 

                <h1>Chi2</h1>
                <p>
                <br/> 
                Initial Chi2 = <xsl:value-of select="chi2_tracks//td[1]"/>    -  Final Chi2 = <xsl:value-of select="chi2"/>
                <br/>
                </p>
                <xsl:if test="n_free">
                <p>Initial reduced Chi2 = <xsl:value-of select="chi2_tracks//td[1] div n_free"/>    -  Final reduced Chi2 = <xsl:value-of select="chi2 div n_free"/>
                </p>
                <h1>Degrees of freedom</h1>
                <p>Number of degrees of freedom = <xsl:value-of select="n_free"/>
                </p>
                </xsl:if>
                
                <xsl:if test="conf_level">
                    <h1> Confidence Level = <xsl:value-of select="conf_level"/> </h1>
                </xsl:if>
                
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
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
