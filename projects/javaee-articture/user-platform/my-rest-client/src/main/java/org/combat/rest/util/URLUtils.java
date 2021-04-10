package org.combat.rest.util;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author zhangwei
 * @Description URLUtils
 * @Date: 2021/4/8 00:25
 */
public interface URLUtils {

    String AND = "&";

    String EQUAL = "=";

    String TEMPLATE_VARIABLE_START = "{";

    String TEMPLATE_VARIABLE_END = "}";

    String DEFAULT_ENCODING = System.getProperty("org.combat.url.encoding", "UTF-8");

    static Map<String, List<String>> resolveParameters(String query) {
        if (StringUtils.isNotBlank(query)) {
            Map<String, List<String>> parameterMap = new LinkedHashMap<>();
            String[] queryParams = StringUtils.split(query, AND);

            if (queryParams != null) {
                for (String queryParam : queryParams) {
                    String[] paramNameAndValue = StringUtils.split(queryParam, EQUAL);
                    if (paramNameAndValue.length > 0) {
                        String paramName = paramNameAndValue[0];
                        String paramValue = paramNameAndValue.length > 1 ? paramNameAndValue[1] : StringUtils.EMPTY;
                        List<String> paramValueList = parameterMap.get(paramName);
                        if (paramValueList == null) {
                            paramValueList = new LinkedList<>();
                            parameterMap.put(paramName, paramValueList);
                        }
                        paramValueList.add(paramValue);
                    }
                }
            }
            return Collections.unmodifiableMap(parameterMap);
        }
        return Collections.emptyMap();
    }

    static String resolveVariables(String template, Map<String, ?> templateValues, boolean encoded) {
        if (StringUtils.isBlank(template)) {
            return null;
        }

        if (templateValues == null || templateValues.isEmpty()) {
            return template;
        }

        StringBuilder resolvedTemplate = new StringBuilder(template);

        int start = 0;
        int end = 0;

        for (; ; ) {
            start = resolvedTemplate.indexOf(TEMPLATE_VARIABLE_START, end);
            end = resolvedTemplate.indexOf(TEMPLATE_VARIABLE_END, start);

            if (start == -1 || end == -1) {
                break;
            }

            String variableName = resolvedTemplate.substring(start + 1, end);
            Object value = templateValues.get(variableName);

            if (value == null) {
                continue;
            }

            String variableValue = String.valueOf(value);

            if (encoded) {
                variableValue = encode(variableValue);
            }

            resolvedTemplate.replace(start, end + 1, variableValue);
        }

        return resolvedTemplate.toString();
    }

    static MultivaluedMap<String, String> resolveParams(MultivaluedMap<String, String> params,
                                                        Map<String, ?> templateValues, boolean encoded) {
        MultivaluedMap<String, String> resolvedParams = new MultivaluedHashMap<>();

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            String name = entry.getKey();
            String resolvedName = resolveVariables(name, templateValues, encoded);

            for (String element : entry.getValue()) {
                resolvedParams.add(resolvedName, resolveVariables(element, templateValues, encoded));
            }
        }

        return resolvedParams;
    }

    static String encode(String variableValue) {
        return encode(variableValue, DEFAULT_ENCODING);
    }

    static String encode(String variableValue, String encoding) {
        String encodedContent;
        try {
            encodedContent = URLEncoder.encode(variableValue, encoding);
        } catch (UnsupportedEncodingException | NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
        return encodedContent;
    }

    static Map<String, Object> encodeSlash(Map<String, ?> templateValues, boolean encodeSlashInPath) {
        final Map<String, Object> encodedSlashTemplateValues;

        if (encodeSlashInPath) {
            encodedSlashTemplateValues = new HashMap<>();
            for (Map.Entry<String, ?> entry : templateValues.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String) {
                    String str = (String) value;
                    value = StringUtils.replace(str, PathUtils.SLASH, PathUtils.ENCODED_SLASH);
                }

                encodedSlashTemplateValues.put(name, value);
            }
        } else {
            encodedSlashTemplateValues = (Map<String, Object>) templateValues;
        }

        return encodedSlashTemplateValues;
    }

    static String toQueryString(Map<String, List<String>> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return null;
        }

        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String paramName = entry.getKey();
            for (String paramValue : entry.getValue()) {
                queryStringBuilder.append(paramName).append(EQUAL).append(paramValue).append(AND);
            }
        }
        // remove last "&"
        return queryStringBuilder.substring(0, queryStringBuilder.length() - 1);
    }

    static Map<String, Object> toTemplateVariables(String template, Object... values) {
        if (StringUtils.isBlank(template)) {
            return Collections.emptyMap();
        }

        int start = 0;
        int end = 0;

        int index = 0;

        final int length = values == null ? 0 : values.length;

        Map<String, Object> templateVariables = new LinkedHashMap<>();

        for (; ; ) {
            start = template.indexOf(TEMPLATE_VARIABLE_START, end);
            end = template.indexOf(TEMPLATE_VARIABLE_END, start);

            if (start == -1 || end == -1) {
                break;
            }

            String variableName = template.substring(start + 1, end);

            if (!templateVariables.containsKey(variableName)) {
                Object variableValue = index < length ? values[index++] : null;
                templateVariables.put(variableName, variableValue);
            }
        }

        return Collections.unmodifiableMap(templateVariables);
    }

}
