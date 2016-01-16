/*
 * Copyright 2015 Karl Dahlgren
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortmocks.web.mock.soap.support;

import com.fortmocks.core.basis.model.http.domain.HttpHeader;
import com.fortmocks.core.basis.model.http.dto.HttpHeaderDto;
import com.fortmocks.web.mock.soap.model.SoapException;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * The class provides functionality and support methods specifiably for SOAP messages.
 * @author Karl Dahlgren
 * @since 1.0
 */
public class SoapMessageSupport {

    protected static final String DIVIDER = ":";
    protected static final String VARIABLE = "#";
    private static final String BODY = "Body";
    private static final Logger LOGGER = Logger.getLogger(SoapMessageSupport.class);

    /**
     * The default constructor for SoapMessageSupport. It is marked as private
     * to prohibit creation of instances of this class.
     */
    private SoapMessageSupport(){
        // The constructor should be empty
    }

    /**
     * The method extract the operation name from the SOAP body
     * @param body The body that contains the operation name
     * @return The extracted operation name
     */
    public static String extractSoapRequestName(final String body){
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final InputSource inputSource = new InputSource(new StringReader(body));
            final Document document = documentBuilder.parse(inputSource);
            final String rootName = document.getDocumentElement().getNodeName();
            final String prefix = getElement(rootName, 0);

            final NodeList nodeList = document.getElementsByTagName(prefix + DIVIDER + BODY);

            final Node bodyNode = nodeList.item(0);
            final NodeList bodyChildren = bodyNode.getChildNodes();

            if (bodyChildren.getLength() == 0) {
                throw new SoapException("Invalid count of body children");
            }

            String serviceNameWithPrefix = null;
            for (int index = 0; index < bodyChildren.getLength(); index++) {
                if (!bodyChildren.item(index).getNodeName().contains(VARIABLE)) {
                    serviceNameWithPrefix = bodyChildren.item(index).getNodeName();
                }
            }
            if (serviceNameWithPrefix == null) {
                throw new SoapException("Unable to extract the service name");
            }

            return getElement(serviceNameWithPrefix, 1);
        }catch(Exception exception){
            LOGGER.error("Unable to extract SOAP request name", exception);
            throw new SoapException(exception.getMessage());
        }
    }

    /**
     * Returns the element name or prefix
     * @param element The element with both the name and namespace
     * @param index Index is used to indicate what should be retrieved. Index 0 = namespace, Index 1 = element name
     * @return Either the element name or namespace
     */
    public static String getElement(String element, int index){
        Preconditions.checkArgument(index >= 0, "The index can't be less than zero");
        Preconditions.checkArgument(index <= 1, "The index can't be more than one");

        final String[] elementDivided = element.split(DIVIDER);

        if (elementDivided.length == 1) {
            return elementDivided[0];
        }
        if (elementDivided.length == 2) {
            return elementDivided[index];
        }

        throw new IllegalArgumentException("Unable to find the name or prefix in the XML element");
    }

    /**
     * The getBody method is used to extract the body from the incoming request
     * @param httpServletRequest The incoming request that contains the request body
     * @return The request body as a String
     */
    public static String getBody(final HttpServletRequest httpServletRequest) {
        try {
            final StringBuilder buffer = new StringBuilder();
            final BufferedReader reader = httpServletRequest.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            LOGGER.error("Unable to read the incoming file", e);
            throw new SoapException("Unable to extract the request body");
        }
    }

    /**
     * Extract HTTP headers from provided Http Servlet Request
     * @param httpServletRequest Incoming Http Servlet Request that contains the headers which will be extracted
     * @return A list of HTTP headers extracted from the provided httpServletRequest
     */
    public static List<HttpHeaderDto> extractHttpHeaders(final HttpServletRequest httpServletRequest){
        final List<HttpHeaderDto> httpHeaders = new ArrayList<HttpHeaderDto>();
        final Enumeration<String> headers = httpServletRequest.getHeaderNames();
        while(headers.hasMoreElements()){
            final String headerName = headers.nextElement();
            final String headerValue = httpServletRequest.getHeader(headerName);
            final HttpHeaderDto httpHeader = new HttpHeaderDto();
            httpHeader.setName(headerName);
            httpHeader.setValue(headerValue);
            httpHeaders.add(httpHeader);
        }
        return httpHeaders;
    }
}
