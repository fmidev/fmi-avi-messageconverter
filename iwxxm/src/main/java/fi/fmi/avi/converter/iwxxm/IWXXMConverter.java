package fi.fmi.avi.converter.iwxxm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

/**
 * Created by rinne on 20/07/17.
 */
public abstract class IWXXMConverter {
    private static JAXBContext jaxbCtx = null;
    private static Map<String, Object> classToObjectFactory = new HashMap<>();
    private static Map<String, Object> objectFactoryMap = new HashMap<>();

    /**
     * Singleton for accessing the shared JAXBContext for IWXXM JAXB handling.
     *
     * @return the context
     */
    protected static synchronized JAXBContext getJAXBContext() throws JAXBException {
        if (jaxbCtx == null) {
            // NOTE: this can take several seconds, needs to scan all the jars in classpath!!
            jaxbCtx = JAXBContext.newInstance("icao.iwxxm21:aero.aixm511:net.opengis.gml32:org.iso19139.ogc2007.gmd:org.iso19139.ogc2007.gco:org"
                    + ".iso19139.ogc2007.gss:org.iso19139.ogc2007.gts:org.iso19139.ogc2007.gsr:net.opengis.om20:net.opengis.sampling:net.opengis.sampling"
                    + ".spatial:wmo.metce2013:wmo.opm2013:org.w3c.xlink11");
        }
        return jaxbCtx;
    }

    protected static <T> T create(final Class<T> clz) throws IllegalArgumentException {
        return create(clz, null);
    }

    protected static <T> T create(final Class<T> clz, final JAXBElementConsumer<T> consumer) throws IllegalArgumentException {
        try {
            Object objectFactory = getObjectFactory(clz);
            String methodName = new StringBuilder("create").append(clz.getSimpleName().substring(0, 1).toUpperCase()).append(clz.getSimpleName().substring(1))
                    .toString();
            Method toCall = objectFactory.getClass().getMethod(methodName);
            Object result = toCall.invoke(objectFactory);
            if (consumer != null) {
                consumer.consume((T)result);
            }
            return (T) result;
        } catch (ClassCastException|NoSuchMethodException|IllegalAccessException|IllegalAccessError|InvocationTargetException|IllegalArgumentException
                e) {
           throw new IllegalArgumentException("Unable to create JAXB element object for type " + clz, e);
        }
    }

    protected static <T> JAXBElement<T> wrap(T element, Class<T> clz) {
        try {
            Object objectFactory = getObjectFactory(clz);
            String methodName = new StringBuilder("create").append(clz.getSimpleName().substring(0, 1).toUpperCase()).append(clz.getSimpleName().substring(1,
                    clz.getSimpleName().lastIndexOf("Type")))
                    .toString();
            Method toCall = objectFactory.getClass().getMethod(methodName, clz);
            Object result = toCall.invoke(objectFactory, element);
            return (JAXBElement<T>) result;
        } catch (ClassCastException|NoSuchMethodException|IllegalAccessException|IllegalAccessError|InvocationTargetException|IllegalArgumentException
                e) {
            throw new IllegalArgumentException("Unable to create JAXBElement wrapper for " + element, e);
        }
    }

    private static Object getObjectFactory(Class<?> clz) {
        Object objectFactory = null;
        try {
            synchronized(objectFactoryMap) {
                objectFactory = classToObjectFactory.get(clz.getCanonicalName());
                if (objectFactory == null) {
                    String objectFactoryName = new StringBuilder().append(clz.getCanonicalName().substring(0, clz.getCanonicalName().lastIndexOf('.') + 1))
                            .append("ObjectFactory")
                            .toString();
                    objectFactory = objectFactoryMap.get(objectFactoryName);
                    if (objectFactory == null) {
                        Class<?> ofClass = IWXXMConverter.class.getClassLoader().loadClass(objectFactoryName);
                        Constructor<?> c = ofClass.getConstructor();
                        objectFactory = c.newInstance();
                        objectFactoryMap.put(objectFactoryName, objectFactory);
                    }
                    classToObjectFactory.put(clz.getCanonicalName(), objectFactory);
                }
            }
            return objectFactory;
        } catch (ClassCastException|NoSuchMethodException|IllegalAccessException|IllegalAccessError|InstantiationException|ClassNotFoundException
                |InvocationTargetException
                e) {
            throw new IllegalArgumentException("Unable to get ObjectFactory for " + clz.getCanonicalName(), e);
        }
    }

    @FunctionalInterface
    interface JAXBElementConsumer<V>  {
        public void consume(V element);
    }
}
