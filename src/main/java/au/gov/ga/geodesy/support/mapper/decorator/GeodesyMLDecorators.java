package au.gov.ga.geodesy.support.mapper.decorator;

import static au.gov.ga.geodesy.support.utils.GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_MILLISEC;
import static au.gov.ga.geodesy.support.utils.GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_SEC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ga.geodesy.exception.GeodesyRuntimeException;
import au.gov.ga.geodesy.support.utils.GMLDateUtils;
import au.gov.ga.geodesy.support.utils.GMLReflectionUtils;
import net.opengis.gml.v_3_2_1.TimePositionType;

/**
 * A number of Decorators are applied to most translated (output) elements to make sure some required fields are set.
 * <p>
 * The current decorators are:
 * - Ids - most elements required gml:id attributes
 * - Change tracking's DateInserted element is required on most elements. This will add it with date=now().
 *
 * @author brookes
 */
public class GeodesyMLDecorators {
    private static final Logger logger = LoggerFactory.getLogger(GeodesyMLDecorators.class);
    public static final String EXPECTING_THIS_EXCEPTION = "Expecting this exception: ";

    // Hide constructor
    private GeodesyMLDecorators() {
    }

    /**
     * @param parentElement that the decorator is added to
     * @param childElement  of the parent which may have some required fields (like dateInstalled). It can be null to indicate that a child was not
     *                      relevant in the context that it was being used. If that is the case then any decorators that required a child element
     *                      are skipped.
     * @return the parentElement decorated by the nested classes in here
     */
    public static <P, C> P addDecorators(P parentElement, C childElement) {
        try {
            P a = parentElement;
            if (childElement != null) {
                a = ChangeTrackingDecorator.addChangeTracking(parentElement, childElement);
            }
            return IdDecorator.addId(a);
        } catch (SecurityException | IllegalArgumentException e) {
            throw new GeodesyRuntimeException("Error in decorator call for element: " + parentElement.getClass(), e);
        }
    }

    /**
     * @param parentElement that the decorator is added to. Any decorators that required a childElement also (See @link
     *                      {@link #addDecorators(Object, Object)}) will not be called.
     * @return the parentElement decorated by the nested classes in here
     */
    public static <P> P addDecorators(P parentElement) {
        return addDecorators(parentElement, null);
    }

    /**
     * This classs decorates the element objects with change tracking elemnent objects since as XML change tracking is required.
     */
    public static class ChangeTrackingDecorator {
        // Hide constructor
        private ChangeTrackingDecorator() {
        }

        /**
         * Add change tracking's DateInserted element class type to the element and return. Iff none was there previously.
         *
         * @param parentElement that the decorator is added to
         * @param childElement  of the parent which may have some required fields (like dateInstalled)
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
         * @param parentElement that the decorator is added to
         * @param childElement  of the parent which may have some required fields (like dateInstalled)
         * @return the parentElement decorated with change tracking field(s)
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @throws ParseException
         */
        private static <P, C> P addChangeTrackingRunner(P parentElement, C childElement) throws IllegalAccessException,
                InvocationTargetException, ParseException {
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
                logger.debug(EXPECTING_THIS_EXCEPTION, e);
            }
            return parentElement;
        }

        /**
         * Create a TimePositionType with a date that is getDateInstalled() if it exists (it is on equipment). Or now() if not.
         *
         * @param childElement of the parent which may have some required fields (like dateInstalled)
         * @return
         * @throws ParseException
         */
        private static <C> TimePositionType createTimePositionType(C childElement) throws ParseException {
            TimePositionType tpt = new TimePositionType();
            Method getter = null;
            Instant useThisDate = null;
            try {
                getter = childElement.getClass().getMethod("getDateInstalled");
                useThisDate = buildInstant(childElement, getter);
            } catch (NoSuchMethodException e) {
                // this is ok
                logger.debug(EXPECTING_THIS_EXCEPTION, e);
            } catch (SecurityException e) {
                logger.error("Trying to get method getDateInstalled() on element: "
                        + childElement.getClass().getSimpleName() + "; just use now() instead", e);
            }
            useThisDate = useThisDate != null ? useThisDate :Instant.now();
            tpt.setValue(Stream.of(GEODESYML_DATE_FORMAT_TIME_SEC.format(useThisDate))
                    .collect(Collectors.toList()));
            return tpt;
        }

        private static <C> Instant buildInstant(C childElement, Method getter) {
            Instant useThisDate = null;
            try {
                TimePositionType childTpt = (TimePositionType) getter.invoke(childElement);
                if (childTpt != null) {
                    String stringDate = childTpt.getValue().get(0);
                    String useThisDateString = GMLDateUtils.stringToDateToStringMultiParsers(stringDate);
                    useThisDate = GMLDateUtils.stringToDate(useThisDateString, GEODESYML_DATE_FORMAT_TIME_MILLISEC);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("Trying to getDateInstalled() on element: " + childElement.getClass().getSimpleName()
                        + "; just use now() instead", e);
            }
            return useThisDate;
        }
    }

    /**
     * This classs decorates the element objects with ID elemnent objects since as XML ID elements are required.
     */
    public static class IdDecorator {

        private static Map<Class<?>, Integer> uniqueIdMap = new HashMap<>();

        // Hide constructor
        private IdDecorator() {
        }

        /**
         * Add unique id per element class type to the element and return.
         * Also apply recursively to all child fields that have a non-primitive type.
         *
         * @param element - that should have an Id attribute (but will check if so)
         * @return Same element but with id attribute set to unique id (if it exists)
         * @throws SecurityException
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         */
        public static <P> P addId(P element) {
            try {
                return addIdRunnerOuter(element);
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new GeodesyRuntimeException("Error generating id for element: " + element.getClass(), e);
            }
        }

        // Implement depth-first recursion
        static <P> P addIdRunnerOuter(P element) throws IllegalAccessException, InvocationTargetException {
            logger.debug("addIdRunnerOuter - starting el:" + element.getClass().getSimpleName());
            for (Method m : GMLReflectionUtils.getNonPrimitiveGetters(element)) {
                Object o = m.invoke(element);
                if (o != null) {
                    addIdRunnerOuter(o);
                }
            }
            return addIdRunner(element);
        }

        static <P> P addIdRunner(P element) throws IllegalAccessException, InvocationTargetException {
            Method setString = null;
            Method setInteger = null;
            try {
                setString = element.getClass().getMethod("setId", String.class);
            } catch (NoSuchMethodException e) {
                // No problem - maybe its setId(Integer)
                logger.debug(EXPECTING_THIS_EXCEPTION, e);
                try {
                    setInteger = element.getClass().getMethod("setId", Integer.class);
                } catch (NoSuchMethodException e2) {
                    // No problem - mustn't have a setId(Integer or String)
                    logger.debug(EXPECTING_THIS_EXCEPTION, e2);
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
            Integer id;
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
