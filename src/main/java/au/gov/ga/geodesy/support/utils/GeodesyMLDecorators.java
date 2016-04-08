package au.gov.ga.geodesy.support.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ga.geodesy.exception.GeodesyRuntimeException;
import net.opengis.gml.v_3_2_1.TimePositionType;

/**
 * A number of Decorators are applied to most translated (output) elements to make sure some required fields are set.
 * 
 * The current decorators are:
 * - Ids - most elements required gml:id attributes
 * - Change tracking's DateInserted element is required on most elements. This will add it with date=now().
 * 
 * @author brookes
 *
 */
public class GeodesyMLDecorators {
    private static final Logger logger = LoggerFactory.getLogger(GeodesyMLDecorators.class);

    /**
     * 
     * @param parentElement
     *            that the decorator is added to
     * @param childElement
     *            of the parent which may have some required fields (like dateInstalled). It can be null to indicate that a child was not
     *            relevant in the context that it was being used. If that is the case then any decorators that required a child element
     *            are skipped.
     * @return the parentElement decorated by the nested classes in here
     */
    public static <P, C> P addDecorators(P parentElement, C childElement) {
        try {
            P a = parentElement;
            if (childElement != null) {
                a = ChangeTrackingDecorator.addChangeTracking(parentElement, childElement);
            }
            P b = IdDecorator.addId(a);
            return b;
        } catch (SecurityException | IllegalArgumentException e) {
            throw new GeodesyRuntimeException("Error in decorator call for element: " + parentElement.getClass(), e);
        }
    }

    /**
     * 
     * @param parentElement
     *            that the decorator is added to. Any decorators that required a childElement also (See @link
     *            {@link #addDecorators(Object, Object)}) will not be called.
     * @return the parentElement decorated by the nested classes in here
     */
    public static <P, C> P addDecorators(P parentElement) {
        return addDecorators(parentElement, null);
    }

    public static class ChangeTrackingDecorator<P> { // implements Decorator {

        /**
         * Add change tracking's DateInserted element class type to the element and return. Iff none was there previously.
         * 
         * @param parentElement
         *            that the decorator is added to
         * @param childElement
         *            of the parent which may have some required fields (like dateInstalled)
         * 
         * @return the parentElement decorated with change tracking field(s)
         * @throws SecurityException
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         */
        static <P, C> P addChangeTracking(P parentElement, C childElement) {
            try {
                return addChangeTrackingRunner(parentElement, childElement);
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | ParseException e) {
                throw new GeodesyRuntimeException(
                        "Error adding ChangeTracking for element: " + parentElement.getClass(), e);
            }
        }

        /**
         * 
         * @param parentElement
         *            that the decorator is added to
         * @param childElement
         *            of the parent which may have some required fields (like dateInstalled)
         * @return the parentElement decorated with change tracking field(s)
         * @throws SecurityException
         * @throws IllegalAccessException
         * @throws IllegalArgumentException
         * @throws InvocationTargetException
         * @throws ParseException
         */
        private static <P, C> P addChangeTrackingRunner(P parentElement, C childElement) throws SecurityException,
                IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
            Method setter = null;

            try {
                Method getter = parentElement.getClass().getMethod("getDateInserted");
                if (getter.invoke(parentElement) == null) {
                    setter = parentElement.getClass().getMethod("setDateInserted", TimePositionType.class);
                }
                if (setter != null) {
                    TimePositionType tpt = createTimePositionType(childElement);
                    setter.invoke(parentElement, tpt);
                    logger.debug("Added addChangeTracking.dateInserted to element: "
                            + parentElement.getClass().getSimpleName());
                }
            } catch (NoSuchMethodException e) {
                // method doesn't exist for this element - that is ok
            }
            return parentElement;
        }

        /**
         * Create a TimePositionType with a date that is getDateInstalled() if it exists (it is on equipment). Or now() if not.
         * 
         * @param childElement
         *            of the parent which may have some required fields (like dateInstalled)
         * @return
         * @throws ParseException
         */
        private static <C> TimePositionType createTimePositionType(C childElement) throws ParseException {
            TimePositionType tpt = new TimePositionType();
            Method getter = null;
            Date useThisDate = null;
            try {
                getter = childElement.getClass().getMethod("getDateInstalled");
                try {
                    TimePositionType childTpt = (TimePositionType) getter.invoke(childElement);
                    if (childTpt != null) {
                        String stringDate = childTpt.getValue().get(0);
                        useThisDate = GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_SEC.parse(stringDate);
                    } else {
                        useThisDate = new Date();
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error("Trying to getDateInstalled() on element: " + childElement.getClass().getSimpleName()
                            + "; just use now() instead", e);
                }
            } catch (NoSuchMethodException e) {
                // this is ok
            } catch (SecurityException e) {
                logger.error("Trying to get method getDateInstalled() on element: "
                        + childElement.getClass().getSimpleName() + "; just use now() instead", e);
            }
            if (useThisDate == null) {
                useThisDate = new Date();
            }

            tpt.setValue(Stream.of(GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_SEC.format(useThisDate))
                    .collect(Collectors.toList()));
            return tpt;
        }
    }

    public static class IdDecorator<P> {

        private static Map<Class<?>, Integer> uniqueIdMap = new HashMap<>();

        /**
         * Add unique id per element class type to the element and return.
         * 
         * @param element
         *            - that should have an Id attribute (but will check if so)
         * @param P
         *            - element class
         * @return Same element but with id attribute set to unique id (if it exists)
         * @throws SecurityException
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         */
        public static <P> P addId(P element) {
            try {
                return addIdRunner(element);
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new GeodesyRuntimeException("Error generating id for element: " + element.getClass(), e);
            }
        }

        static <P> P addIdRunner(P element)
                throws SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Method setString = null, setInteger = null;

            try {
                setString = element.getClass().getMethod("setId", String.class);
            } catch (NoSuchMethodException e) {
                // No problem - maybe its setId(Integer)
                try {
                    setInteger = element.getClass().getMethod("setId", Integer.class);
                } catch (NoSuchMethodException e2) {
                    // No problem - mustn't have a setId(Integer or String)
                    return element;
                }
            }
            if (setString != null) {
                String id = getStringId(element.getClass());
                setString.invoke(element, element.getClass().getSimpleName() + "_" + id);
            } else if (setInteger != null) {
                Integer id = getIntegerId(element.getClass());
                setInteger.invoke(element, id);
            }
            return element;
        }

        private static Integer getIntegerId(Class<? extends Object> elementClass) {
            Integer id = null;
            if (uniqueIdMap.containsKey(elementClass)) {
                id = uniqueIdMap.get(elementClass);
            } else {
                id = -1;
            }
            uniqueIdMap.put(elementClass, ++id);
            return id;
        }

        private static String getStringId(Class<? extends Object> elementClass) {
            return getIntegerId(elementClass).toString();
        }

    }
}