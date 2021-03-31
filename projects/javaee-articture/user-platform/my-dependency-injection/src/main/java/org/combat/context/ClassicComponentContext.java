package org.combat.context;

import org.combat.function.ThrowableAction;
import org.combat.function.ThrowableFunction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 组件上下文（Web 应用全局使用）
 */
public class ClassicComponentContext implements ComponentContext {

    public static final String CONTEXT_NAME = ClassicComponentContext.class.getName();

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private Map<String, Object> componentsCache = new LinkedHashMap<>();

    private Map<Method, Object> preDestroyMethodCache = new LinkedHashMap<>();

    private static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    private static ServletContext servletContext;

    private Context envContext;

    private ClassLoader classLoader;


    public void init(ServletContext servletContext) throws RuntimeException {
        ClassicComponentContext.servletContext = servletContext;
        servletContext.setAttribute(CONTEXT_NAME, this);

        this.init();
    }

    @Override
    public void init() {
        initClassLoader();
        initEnvContext();
        instantiateComponents();
        initializeComponents();
        registerShowdownHook();
    }

    private void registerShowdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processPreDestroy();
        }));
    }

    private void processPreDestroy() {
        for (Method method : preDestroyMethodCache.keySet()) {
            Object component = preDestroyMethodCache.remove(method);
            ThrowableAction.execute(() -> method.invoke(component));
        }
    }

    private void initClassLoader() {
        this.classLoader = servletContext.getClassLoader();
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    private void initializeComponents() {
        componentsCache.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            // 注入阶段 - {@link Resource}
            injectComponents(component, componentClass);
            // 查询候选方法
            List<Method> candidateMethods = findCandidateMethods(componentClass);
            // 初始化阶段 - {@link PostConstruct}
            processPostConstruct(component, candidateMethods);
            // 实现销毁阶段 - {@link PreDestroy}
            processPreDestroyMetadata(component, candidateMethods);
        });
    }

    private List<Method> findCandidateMethods(Class<?> componentClass) {
        return Stream.of(componentClass.getMethods())
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&
                                method.getParameterCount() == 0)
                .collect(Collectors.toList());
    }

    private void processPreDestroyMetadata(Object component, List<Method> candidateMethods) {
        candidateMethods.stream()
                .filter(method -> method.isAnnotationPresent(PreDestroy.class))
                .forEach(method -> {
                    preDestroyMethodCache.put(method, component);
                });
    }

    private void processPostConstruct(Object component, List<Method> candidateMethods) {
        candidateMethods
                .stream()
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .forEach(method -> {
                    ThrowableAction.execute(() -> method.invoke(component));
                });
    }

    private void injectComponents(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> {
                    int mods = field.getModifiers();
                    return !Modifier.isStatic(mods) &&
                            field.isAnnotationPresent(Resource.class);
                }).forEach(field -> {
            Resource resource = field.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Object injectedObject = lookupComponent(resourceName);
            field.setAccessible(true);
            try {
                field.set(component, injectedObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private void instantiateComponents() {
        // 遍历获取所有的组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(name -> componentsCache.put(name, lookupComponent(name)));
    }

    public <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    private List<String> listComponentNames(String path) {
        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> e = executeInContext(context, ctx -> ctx.list(path), true);

            if (e == null) {
                return Collections.emptyList();
            }
            List<String> fullNames = new LinkedList<>();
            while (e.hasMoreElements()) {
                NameClassPair element = e.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    fullNames.addAll(listComponentNames(element.getName()));
                } else {
                    String fullName = path.startsWith("/") ?
                            element.getName() : path + "/" + element.getName();
                    fullNames.add(fullName);
                }
            }
            return fullNames;
        });
    }

    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(function, false);
    }

    protected <R> R executeInContext(ThrowableFunction<Context, R> function, boolean ignoredException) {
        return executeInContext(this.envContext, function, ignoredException);
    }

    private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function,
                                   boolean ignoredException) throws RuntimeException {
        R result = null;
        try {
            result = ThrowableFunction.execute(context, function);
        } catch (Throwable e) {
            if (ignoredException) {
                logger.warning(e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private void initEnvContext() throws RuntimeException {
        if (this.envContext != null) {
            return;
        }

        Context initCtx = null;
        try {
            initCtx = new InitialContext();
            this.envContext = (Context) initCtx.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(initCtx);
        }
    }

    public static ClassicComponentContext getInstance() {
        return (ClassicComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public <C> C getComponent(String name) throws NoSuchElementException {
        return (C) componentsCache.get(name);
    }

    @Override
    public List<String> getComponentNames() {
        return new ArrayList<>(componentsCache.keySet());
    }

    @Override
    public void destroy() throws RuntimeException {
        processPreDestroy();
        clearCache();
        closeEnvContext();
    }

    private void closeEnvContext() {
        close(this.envContext);
    }

    private void clearCache() {
        componentsCache.clear();
        preDestroyMethodCache.clear();
    }

    public static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }
}
