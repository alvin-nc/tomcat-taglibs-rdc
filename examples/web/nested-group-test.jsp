<!--Example:Start-->
<%--
  Copyright 2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<!--$Id$-->
<!--
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rdc" uri="http://jakarta.apache.org/taglibs/rdc-1.0"%>
-->

<vxml version="2.0" xml:lang="en-US"  xmlns="http://www.w3.org/2001/vxml" >

  <jsp:useBean id="rdcStack" class="java.util.Stack" scope="request"/>
  <jsp:useBean id="dialogMap"  class="java.util.LinkedHashMap" scope="session"/>

  <rdc:push stack="${rdcStack}" element="${dialogMap}"/>
  <c:url var="submit" value="${pageContext.request.servletPath}"/>

  <form>    
    <rdc:group id="pickup" strategy="org.apache.taglibs.rdc.dm.SimpleDirectedDialog" submit="${submit}" >
      <rdc:group id="trip" strategy="org.apache.taglibs.rdc.dm.SimpleDirectedDialog" submit="${submit}" >
        <rdc:date id="outDate" minDate="01012004" maxDate="01012005" />
        <rdc:date id="inDate" minDate="01012004" maxDate="01012005" />
      </rdc:group>	
      <rdc:date id="pickupDate" minDate="01012004" maxDate="01012005" />
      <rdc:time id="pickupTime" minTime="xxxxxx" maxTime="xx45xx" />
      <rdc:duration id ="pickupDuration" minDuration="P04D" maxDuration="P09D" />
    </rdc:group>
  </form>

  <rdc:pop var="discard" stack="${rdcStack}"/>
</vxml>
<!--Example:End-->
