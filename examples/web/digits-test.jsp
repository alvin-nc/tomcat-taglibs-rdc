<!--Example:Start-->

<% response.setHeader("Cache-Control", "no-cache"); %>


<!--
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rdc" uri="http://jakarta.apache.org/taglibs/rdc-1.0"%>
-->

<vxml version="2.0" xml:lang="en-US"  xmlns="http://www.w3.org/2001/vxml" >
<meta http-equiv="expires" content="0"/>

  <jsp:useBean id="rdcStack" class="java.util.Stack"
               scope="request"/>
  
  <jsp:useBean id="dialogMap"  class="java.util.LinkedHashMap"
               scope="session"/>
    
 
  <rdc:push stack="${rdcStack}" element="${dialogMap}"/>

  <form>    
    <rdc:digits id="digits" minLength="5" maxLength="9"  initial="4" confirm="true" echo="true"/>
  
  </form>

</vxml>
<rdc:pop var="discard" stack="${rdcStack}"/>
<!--Example:End-->
