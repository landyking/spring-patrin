package com.github.landyking.patrin;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PatrinRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    public static final String PREFIX_PKG = "controller.";
    public static final String SUFFIX_CLASS = "Controller";
    private String prefixPkg = PREFIX_PKG;
    private String suffixClass = SUFFIX_CLASS;
    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Override
    public void afterPropertiesSet() {
        this.prefixPkg = getApplicationContext().getEnvironment().getProperty("patrin.prefixPkg", PREFIX_PKG);
        this.suffixClass = getApplicationContext().getEnvironment().getProperty("patrin.suffixClass", SUFFIX_CLASS);
        super.afterPropertiesSet();
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        super.setEmbeddedValueResolver(resolver);
        this.embeddedValueResolver = resolver;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        if (inPackage(handlerType)) {
            return getMappingForMethodInternal(method, handlerType);
        } else {
            return super.getMappingForMethod(method, handlerType);
        }
    }

    private boolean inPackage(Class<?> handlerType) {
        return handlerType.getName().startsWith(prefixPkg);
    }

    private RequestMappingInfo getMappingForMethodInternal(Method method, Class<?> handlerType) {
        RequestMapping ma = collectMethodRequestMapping(method);
        RequestMappingInfo info = createRequestMappingInfo(ma, method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(collectHandlerRequestMapping(handlerType), handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            String prefix = getPathPrefix(handlerType);
            if (prefix != null) {
                info = RequestMappingInfo.paths(prefix).build().combine(info);
            }
        }
        return info;
    }

    private RequestMapping collectHandlerRequestMapping(Class<?> handlerType) {
        RequestMapping rm = AnnotatedElementUtils.findMergedAnnotation(handlerType, RequestMapping.class);
        if (rm == null) {
            String sb = processHandlerName(handlerType.getName());
            rm = createReplaceAnnotation(new AbstractMap.SimpleEntry<>(sb,null));
        }
        return rm;
    }

    protected String processHandlerName(String name) {
        boolean skipFirst = true;
        String[] arr = name.substring(prefixPkg.length()).split("\\.");
        String[] pkgs;
        String ctl;
        if (arr.length > 1) {
            ctl = arr[arr.length - 1];
            pkgs = Arrays.copyOf(arr, arr.length - 1);
        } else {
            ctl = arr[0];
            pkgs = new String[0];
        }
        StringBuilder sb = new StringBuilder();
        for (String p : pkgs) {
            if (skipFirst) {
                skipFirst = false;
            } else {
                sb.append("/");
            }
            if (isPathVar(p)) {
                sb.append("{").append(p, 1, p.length() - 1).append("}");
            } else {
                if (p.endsWith("_")) {
                    sb.append(p, 0, p.length() - 1);
                } else {
                    sb.append(p);
                }
            }
        }
        Assert.isTrue(ctl.endsWith(suffixClass), String.format("Class: %s not end with: %s", name, suffixClass));

        String classPrefix = ctl.substring(0, ctl.length() - suffixClass.length());
        if (!classPrefix.equals("__")) {
            if (skipFirst) {
                skipFirst = false;
            } else {
                sb.append("/");
            }
            String[] tar = classPrefix.split("_");
            boolean flag = false;
            for (String t : tar) {
                if (StringUtils.hasText(t)) {
                    if (!flag) {
                        flag = true;
                    } else {
                        sb.append("_");
                    }
                    sb.append(firstCharLowerWord(t));
                }
            }
        }
        return sb.toString();
    }

    private String firstCharLowerWord(String t) {
        return t.substring(0, 1).toLowerCase() + t.substring(1);
    }

    private boolean isPathVar(String p) {
        return p.length() > 2 && p.startsWith("$") && p.endsWith("$");
    }

    private RequestMapping collectMethodRequestMapping(Method method) {
        RequestMapping rm = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (rm == null) {
            //未加RequestMapping注解，但是为public方法，默认识别为RequestMapping
            if (Modifier.isPublic(method.getModifiers())) {
                rm = createReplaceAnnotation(processMethodName(method.getName()));
            }
        } else {
            //加有RequestMapping注解，但是未指定Path，将方法名作为Path
            if (rm.value().length == 0 && rm.path().length == 0) {
                rm = createReplaceAnnotation(processMethodName(method.getName()), rm);
            }
        }
        return rm;
    }

    static Map<String, RequestMethod> HTTP_METHODS = Arrays.stream(RequestMethod.values()).collect(Collectors.toMap(enu -> enu.name().toUpperCase() + "__", Function.identity()));

    protected Map.Entry<String, RequestMethod> processMethodName(String name) {
        RequestMethod requestMethod = null;
        for (String httpMethod : HTTP_METHODS.keySet()) {
            if (name.startsWith(httpMethod)) {
                name = name.substring(httpMethod.length());
                requestMethod = HTTP_METHODS.get(httpMethod);
                break;
            }
        }
        if (!StringUtils.hasText(name)) {
            return new AbstractMap.SimpleEntry<>("", requestMethod);
        }
        String[] arr = name.split("__");
        StringBuilder sb = new StringBuilder();
        for (String p : arr) {
            if (StringUtils.hasText(p)) {
                sb.append("/");
                if (isPathVar(p)) {
                    sb.append("{").append(p, 1, p.length() - 1).append("}");
                } else {
                    sb.append(p);
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(sb.toString(), requestMethod);
    }

    private String getPathPrefix(Class<?> handlerType) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : this.getPathPrefixes().entrySet()) {
            if (entry.getValue().test(handlerType)) {
                String prefix = entry.getKey();
                if (this.embeddedValueResolver != null) {
                    prefix = this.embeddedValueResolver.resolveStringValue(prefix);
                }
                return prefix;
            }
        }
        return null;
    }

    private RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, AnnotatedElement element) {

        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }

    private RequestMapping createReplaceAnnotation(final Map.Entry<String, RequestMethod> meta, final RequestMapping old) {
        String name = meta.getKey();
        return new RequestMapping() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return old.annotationType();
            }

            @Override
            public String name() {
                return old.name();
            }

            @Override
            public String[] value() {
                return new String[]{name};
            }

            @Override
            public String[] path() {
                return new String[]{name};
            }

            @Override
            public RequestMethod[] method() {
                if (meta.getValue() != null && old.method().length <= 0) {
                    return new RequestMethod[]{meta.getValue()};
                }
                return old.method();
            }

            @Override
            public String[] params() {
                return old.params();
            }

            @Override
            public String[] headers() {
                return old.headers();
            }

            @Override
            public String[] consumes() {
                return old.consumes();
            }

            @Override
            public String[] produces() {
                return old.produces();
            }
        };
    }

    private RequestMapping createReplaceAnnotation(final Map.Entry<String, RequestMethod> meta) {
        String name = meta.getKey();

        return new RequestMapping() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public String[] value() {
                return new String[]{name};
            }

            @Override
            public String[] path() {
                return new String[]{name};
            }

            @Override
            public RequestMethod[] method() {
                if (meta.getValue() != null) {
                    return new RequestMethod[]{meta.getValue()};
                }
                return new RequestMethod[0];
            }

            @Override
            public String[] params() {
                return new String[0];
            }

            @Override
            public String[] headers() {
                return new String[0];
            }

            @Override
            public String[] consumes() {
                return new String[0];
            }

            @Override
            public String[] produces() {
                return new String[0];
            }
        };
    }
}
