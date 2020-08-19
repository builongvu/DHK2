/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package vn.vissoft.dashboard.helper;


import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.viettel.security.PassTranformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;
import java.text.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author VinhNDQ
 * @version 1.0
 */
public class DataUtil {

    private static final Logger logger = LogManager.getLogger(DataUtil.class);

    private static final String PHONE_PATTERN = "^[0-9]*$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("<(.*(PASS|PIN|PASSWORD|pass|pin|password).*)>(.+)</\\1>");
    // Pattern dung de loc bo cac ki tu +, 0, 84 o dau isdn
    private static final Pattern ISDN_FORMAT_PATTERN = Pattern.compile("^\\+?(0+|84)(0+|84)?");
    static private String strKey = "iexsbccs";
    static private String algorithm = "DES";

    public static boolean isValidEmail(String email) {
        String emailRegex = "[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";
        return email.matches(emailRegex);
    }

    public static boolean isValidMutilEmail(String email) {
        if (DataUtil.isNullObject(email)) {
            return false;
        }
        String[] input = email.split(";");
        if (!DataUtil.isNullOrEmpty(input) && input.length > 0) {
            for (int i = 0; i < input.length; i++) {
                if (!isValidEmail(input[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\s*(?:\\+?(\\d{1,3}))?[- (]*(\\d{3})[- )]*(\\d{3})[- ]*(\\d{4})(?: *[x/#]{1}(\\d+))?\\s*$");
    }

    // check nhieu so dien thoai
    public static boolean isValidMultiPhoneNumber(String phoneNumber) {
        if (DataUtil.isNullOrEmpty(phoneNumber)) {
            return false;
        }
        String[] input = phoneNumber.split(";");
        if (!DataUtil.isNullOrEmpty(input) && input.length > 0) {
            for (int i = 0; i < input.length; i++) {
                if (!isValidPhoneNumber(input[i])) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void assertNullList(String key, List value) throws LogicException {
        if (value == null || value.isEmpty()) {
            throw new LogicException("ANOL0001", key);
        }
    }

    public static void assertNull(String key, Object value) throws LogicException {
        if (value == null) {
            throw new LogicException("ANOL0001", key);
        }
    }

    public static String assertNullOrLength(String key, String value, int length) throws LogicException {
        if (DataUtil.isNullOrEmpty(value)) {
            throw new LogicException("ANOL0001", key);
        }
        value = value.trim();

        if (length > 0 && value.length() > length) {
            throw new LogicException("ANOL0002", key);
        }

        return value;
    }

    public static String assertNullOrLength(String value, int length, String msgNull, String msgLenght) throws LogicException, Exception {
        if (DataUtil.isNullOrEmpty(value)) {
            throw new LogicException(msgNull);
        }
        value = value.trim();
        if (value.getBytes(StandardCharsets.UTF_8).length > length) {
            throw new LogicException(msgLenght);
        }

        return value;
    }

    public static String addZeroToString(String input, int strLength) {
        String result = input;
        for (int i = 1; i <= strLength - input.length(); i++) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * Copy du lieu tu bean sang bean moi
     * Luu y chi copy duoc cac doi tuong o ngoai cung, list se duoc copy theo tham chieu
     * <p>
     * Chi dung duoc cho cac bean java, khong dung duoc voi cac doi tuong dang nhu String, Integer, Long...
     *
     * @param source
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneBean(T source) {
        try {
            if (source == null) {
                return null;
            }
            T dto = (T) source.getClass().getConstructor().newInstance();
            BeanUtils.copyProperties(source, dto);
            return dto;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Copy 1 list bean sang list moi
     *
     * @param sourceList
     * @param <T>
     * @return
     */
    public static <T> List<T> cloneBean(Iterable<T> sourceList) {
        final List<T> destList = Lists.newArrayList();

        if (sourceList == null) {
            return destList;
        }

        sourceList.forEach(t -> {
            T destObject = cloneBean(t);
            if (destObject != null) {
                destList.add(cloneBean(t));
            }
        });

        return destList;
    }

    /**
     * Clone an object completely
     *
     * @param source
     * @param <T>
     * @return
     * @author KhuongDV
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCloneObject(T source) {
        try {
            if (source == null) {
                return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            T dto = (T) in.readObject();
            in.close();
            return dto;
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * forward page
     *
     * @return
     * @author ThanhNT
     */
    public static String forwardPage(String pageName) {
        if (!DataUtil.isNullOrEmpty(pageName)) {
            return "pretty:" + pageName.trim();
        }
        return "";
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(Double value) {
        return (value == null || value == 0);
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(String value) {
        return (value == null || safeToLong(value).equals(0L));
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(Integer value) {
        return (value == null || value.equals(0));
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrOneNavigate(Long value) {
        return (value == null || value.equals(-1L));
    }

    public static boolean isNullOrOneNavigate(Integer value) {
        return (value == null || value.equals(-1));
    }

    // hungnn, check BigInteger
    public static boolean isNullOrOneBigInteger(BigInteger value) {
        return (value == null || value.equals(-1));
    }

    public static boolean isNullOrNotGreaterZero(Long value) {
        return (value == null || value.compareTo(0L) <= 0);
    }

    /**
     * Kiem tra Bigdecimal bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(BigDecimal value) {
        return (value == null || value.compareTo(BigDecimal.ZERO) == 0);
    }

    /**
     * Upper first character
     *
     * @param input
     * @return
     */
    public static String upperFirstChar(String input) {
        if (DataUtil.isNullOrEmpty(input)) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Lower first characater
     *
     * @param input
     * @return
     */
    public static String lowerFirstChar(String input) {
        if (DataUtil.isNullOrEmpty(input)) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String safeTrim(String obj) {
        if (obj == null) return null;
        return obj.trim();
    }

    public static String safeToUpperCase(String obj) {
        if (obj == null) return null;
        return obj.toUpperCase();
    }

    public static Long safeToLong(Object obj1, Long defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        if (obj1 instanceof BigDecimal) {
            return ((BigDecimal) obj1).longValue();
        }
        if (obj1 instanceof BigInteger) {
            return ((BigInteger) obj1).longValue();
        }

        try {
            return Long.parseLong(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * @param obj1 Object
     * @return Long
     */
    public static Long safeToLong(Object obj1) {
        return safeToLong(obj1, 0L);
    }

    public static Double safeToDouble(Object obj1, Double defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Double safeToDouble(Object obj1) {
        return safeToDouble(obj1, 0.0);
    }

    public static Short safeToShort(Object obj1, Short defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Short safeToShort(Object obj1) {
        return safeToShort(obj1, (short) 0);
    }

    /**
     * @param obj1
     * @param defaultValue
     * @return
     * @author phuvk
     */
    public static int safeToInt(Object obj1, Integer defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * @param obj1 Object
     * @return int
     */
    public static int safeToInt(Object obj1) {
        return safeToInt(obj1, 0);
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1, String defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }

        return obj1.toString();
    }


    public static String safeToLower(String obj1) {
        if (obj1 == null) {
            return null;
        }

        return obj1.toLowerCase();
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1) {
        return safeToString(obj1, "");
    }

    public static Date safeToDate(Object obj1) {
        if (obj1 == null) {
            return null;
        }
        return (Date) obj1;
    }

    /**
     * safe equal
     *
     * @param obj1 Long
     * @param obj2 Long
     * @return boolean
     */
    public static boolean safeEqual(Long obj1, Long obj2) {
        if (obj1 == obj2) return true;
        return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
    }

    /**
     * safe equal
     *
     * @param obj1 Long
     * @param obj2 Long
     * @return boolean
     */
    public static boolean safeEqual(BigInteger obj1, BigInteger obj2) {
        if (obj1 == obj2) return true;
        return (obj1 != null) && (obj2 != null) && obj1.equals(obj2);
    }

    /**
     * @param obj1
     * @param obj2
     * @return
     * @date 09-12-2015 17:43:20
     * @author TuyenNT17
     * @description
     */
    public static boolean safeEqual(Short obj1, Short obj2) {
        if (obj1 == obj2) return true;
        return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
    }

    /**
     * safe equal
     *
     * @param obj1 String
     * @param obj2 String
     * @return boolean
     */
    public static boolean safeEqual(String obj1, String obj2) {
        if (obj1 == obj2) return true;
        return ((obj1 != null) && (obj2 != null) && obj1.equals(obj2));
    }

    /**
     * safe equal
     *
     * @param obj1 String
     * @param obj2 String
     * @return boolean
     */
    public static boolean safeEqualIgnoreCase(String obj1, String obj2) {
        if (obj1 == obj2) return true;
        return ((obj1 != null) && (obj2 != null) && obj1.equalsIgnoreCase(obj2));
    }

    /**
     * check null or empty
     * Su dung ma nguon cua thu vien StringUtils trong apache common lang
     *
     * @param cs String
     * @return boolean
     */
    public static boolean isNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(final Object[] collection) {
        return collection == null || collection.length == 0;
    }

    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Tra ve doi tuong default neu object la null, neu khong thi tra object
     *
     * @param object
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }

    /**
     * Tra ve doi tuong default neu object la null, neu khong thi tra object
     *
     * @param object
     * @param defaultValueSupplier
     * @param <T>
     * @return
     */
    public static <T> T defaultIfNull(final T object, final Supplier<T> defaultValueSupplier) {
        return object != null ? object : defaultValueSupplier.get();
    }

    /**
     * Ham nay mac du nhan tham so truyen vao la object nhung gan nhu chi hoat dong cho doi tuong la string
     * Chuyen sang dung isNullOrEmpty thay the
     *
     * @param obj1
     * @return
     */
    @Deprecated
    public static boolean isStringNullOrEmpty(Object obj1) {
        return obj1 == null || "".equals(obj1.toString().trim());
    }

    /**
     * @param obj1 Object
     * @return BigDecimal
     */
    public static BigDecimal safeToBigDecimal(Object obj1) {
        if (obj1 == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return BigDecimal.ZERO;
        }
    }

    public static BigInteger safeToBigInteger(Object obj1, BigInteger defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return new BigInteger(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static BigInteger safeToBigInteger(Object obj1) {
        return safeToBigInteger(obj1, BigInteger.ZERO);
    }

    public static BigInteger length(@Nonnull BigInteger from, @Nonnull BigInteger to) {
        return to.subtract(from).add(BigInteger.ONE);
    }

    public static BigDecimal add(BigDecimal number1, BigDecimal number2, BigDecimal... numbers) {
        List<BigDecimal> realNumbers = Lists.newArrayList(number1, number2);
        if (!DataUtil.isNullOrEmpty(numbers)) {
            Collections.addAll(realNumbers, numbers);
        }
        return realNumbers.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Long add(Long number1, Long number2, Long... numbers) {
        List<Long> realNumbers = Lists.newArrayList(number1, number2);
        if (!DataUtil.isNullOrEmpty(numbers)) {
            Collections.addAll(realNumbers, numbers);
        }
        return realNumbers.stream()
                .mapToLong(DataUtil::safeToLong)
                .sum();
    }

    /**
     * add
     *
     * @param obj1 BigDecimal
     * @param obj2 BigDecimal
     * @return BigDecimal
     */
    public static BigInteger add(BigInteger obj1, BigInteger obj2) {
        if (obj1 == null) {
            return obj2;
        } else if (obj2 == null) {
            return obj1;
        }

        return obj1.add(obj2);
    }

    public static void main(String[] args) {
        System.out.println(isValidEmail("fbjkasfhbsdjkfhsdjfh"));
//        System.out.println(formatIsdn("0965623990"));
//        System.out.println(formatIsdn("8484965623990"));
//        System.out.println(formatIsdn("084965623990"));
//        System.out.println(formatIsdn("0084965623990"));
//        System.out.println(formatIsdn("+0845623990"));
//        System.out.println(formatIsdn("+845623990"));
//        System.out.println(formatIsdn("845623990"));
//        System.out.println(formatIsdn("0845623990"));
//        System.out.println(formatIsdn("84845623990"));


//        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://mpsRegisterws/\"><soapenv:Header></soapenv:Header><soapenv:Body><ser:mpsRegister><password>Bccs_Omni#213!</password><service>VAS_MCA_MONTHLY_COBAN</service><action>REGISTER</action><msisdn>841626809841</msisdn><command>DK2</command><username>bccs_omni</username></ser:mpsRegister></soapenv:Body></soapenv:Envelope>";
//        System.out.println(replaceSensitiveInfo(request));
    }

    /**
     * @param number
     * @param pattern
     * @return
     * @author KhuongDV Ham format so thuc ve dang co max la 4 chu so thap phan.
     * Trim() so 0 vo nghia
     */
    public static String getFormattedString4Digits(String number, String pattern) {
        double amount = 0;
        try {
            amount = Double.parseDouble(number);
            DecimalFormat formatter = new DecimalFormat(pattern);
            return formatter.format(amount);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return number;
        }
    }

    public static Character safeToCharacter(Object value) {
        return safeToCharacter(value, '0');
    }

    public static Character safeToCharacter(Object value, Character defaulValue) {
        if (value == null) return defaulValue;
        return String.valueOf(value).charAt(0);
    }

    public static Collection<Long> objLstToLongLst(List<Object> list) {
        Collection<Long> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.addAll(list.stream().map(DataUtil::safeToLong).collect(Collectors.toList()));
        }
        return result;
    }

    public static Collection<Short> objLstToShortLst(List<Object> list) {
        Collection<Short> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.addAll(list.stream().map(DataUtil::safeToShort).collect(Collectors.toList()));
        }
        return result;
    }

    public static Collection<BigDecimal> objLstToBigDecimalLst(List<Object> list) {
        Collection<BigDecimal> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.addAll(list.stream().map(DataUtil::safeToBigDecimal).collect(Collectors.toList()));
        }
        return result;
    }

    public static Collection<Double> objLstToDoubleLst(List<Object> list) {
        Collection<Double> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.addAll(list.stream().map(DataUtil::safeToDouble).collect(Collectors.toList()));
        }
        return result;
    }

    public static Collection<Character> objLstToCharLst(List<Object> list) {
        Collection<Character> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.addAll(list.stream().map(item -> item.toString().charAt(0)).collect(Collectors.toList()));
        }

        return result;
    }

    public static Collection<String> objLstToStringLst(List<Object> list) {
        Collection<String> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.addAll(list.stream().map(DataUtil::safeToString).collect(Collectors.toList()));
        }

        return result;
    }

    /**
     * Collect values of a property from an object list instead of doing a for:each then call a getter
     * Consider using stream -> map -> collect of java 8 instead
     *
     * @param source       object list
     * @param propertyName name of property
     * @param returnClass  class of property
     * @return value list of property
     */
    @Deprecated
    public static <T> List<T> collectProperty(@Nonnull Collection<?> source, @Nonnull String propertyName, @Nonnull Class<T> returnClass) {
        List<T> propertyValues = Lists.newArrayList();
        try {
            String getMethodName = "get" + upperFirstChar(propertyName);
            for (Object x : source) {
                Class<?> clazz = x.getClass();
                Method getMethod = clazz.getMethod(getMethodName);
                Object propertyValue = getMethod.invoke(x);
                if (propertyValue != null && returnClass.isAssignableFrom(propertyValue.getClass())) {
                    propertyValues.add(returnClass.cast(propertyValue));
                }
            }
            return propertyValues;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Lists.newArrayList();
        }
    }

    /**
     * Collect distinct values of a property from an object list instead of doing a for:each then call a getter
     * Consider using stream -> map -> collect of java 8 instead
     *
     * @param source       object list
     * @param propertyName name of property
     * @param returnClass  class of property
     * @return value list of property
     */
    @Deprecated
    public static <T> Set<T> collectUniqueProperty(Collection<?> source, String propertyName, Class<T> returnClass) {
        List<T> propertyValues = collectProperty(source, propertyName, returnClass);
        return Sets.newHashSet(propertyValues);
    }

    /**
     * Khong dung ham nay nua ma chuyen sang check thang == null
     *
     * @param obj1
     * @return
     */
    @Deprecated
    public static boolean isNullObject(Object obj1) {
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return isNullOrEmpty(obj1.toString());
        }
        return false;
    }

    /**
     * Chuyen sang dung {@link BeanUtils# copyProperties}
     *
     * @param source
     * @param destClass
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T mapToNewClass(Object source, Class<T> destClass) {
        try {
            if (source == null) {
                return null;
            }
            T dto;
            dto = destClass.getConstructor().newInstance();
            BeanUtils.copyProperties(source, dto);
            return dto;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String toUpper(String input) {
        if (isNullOrEmpty(input)) {
            return input;
        }
        return input.toUpperCase();
    }

    /**
     * Validate Data theo Pattern
     *
     * @author khuongdv
     */
    public static boolean validateStringByPattern(String value, String regex) {
        if (isNullOrEmpty(regex) || isNullOrEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * Bien cac ki tu dac biet ve dang ascii
     *
     * @param input
     * @return
     */
    public static String convertCharacter(String input) {
        if (input == null) {
            return "";
        }
        String[] aList = {"à", "á", "ả", "ã", "ạ", "â", "ầ", "ấ", "ẩ", "ẫ", "ậ", "ă", "ằ", "ắ", "ẳ", "ẵ", "ặ"};
        String[] eList = {"è", "é", "ẻ", "ẽ", "ẹ", "ê", "ề", "ế", "ể", "ễ", "ệ"};
        String[] iList = {"ì", "í", "ỉ", "ĩ", "ị"};
        String[] oList = {"ò", "ó", "ỏ", "õ", "ọ", "ô", "ồ", "ố", "ổ", "ỗ", "ộ", "ơ", "ờ", "ớ", "ở", "ỡ", "ợ"};
        String[] uList = {"ù", "ú", "ủ", "ũ", "ụ", "ư", "ừ", "ứ", "ử", "ữ", "ự"};
        String[] yList = {"ý", "ỳ", "ỷ", "ỹ", "ỵ"};
        String[] AList = {"À", "Á", "Ả", "Ã", "Ạ", "Â", "Ầ", "Ấ", "Ẩ", "Ẫ", "Ậ", "Ă", "Ằ", "Ắ", "Ẳ", "Ẵ", "Ặ"};
        String[] EList = {"È", "É", "Ẻ", "Ẽ", "Ẹ", "Ê", "Ề", "Ế", "Ể", "Ễ", "Ệ"};
        String[] IList = {"Ì", "Í", "Ỉ", "Ĩ", "Ị"};
        String[] OList = {"Ò", "Ó", "Ỏ", "Õ", "Ọ", "Ô", "Ồ", "Ố", "Ổ", "Ỗ", "Ộ", "Ơ", "Ờ", "Ớ", "Ở", "Ỡ", "Ợ"};
        String[] UList = {"Ù", "Ú", "Ủ", "Ũ", "Ụ", "Ư", "Ừ", "Ứ", "Ử", "Ữ", "Ự"};
        String[] YList = {"Ỳ", "Ý", "Ỷ", "Ỹ", "Ỵ"};
        for (String s : aList) {
            input = input.replace(s, "a");
        }
        for (String s : AList) {
            input = input.replace(s, "A");
        }
        for (String s : eList) {
            input = input.replace(s, "e");
        }
        for (String s : EList) {
            input = input.replace(s, "E");
        }
        for (String s : iList) {
            input = input.replace(s, "i");
        }
        for (String s : IList) {
            input = input.replace(s, "I");
        }
        for (String s : oList) {
            input = input.replace(s, "o");
        }
        for (String s : OList) {
            input = input.replace(s, "O");
        }
        for (String s : uList) {
            input = input.replace(s, "u");
        }
        for (String s : UList) {
            input = input.replace(s, "U");
        }
        for (String s : yList) {
            input = input.replace(s, "y");
        }
        for (String s : YList) {
            input = input.replace(s, "Y");
        }
        input = input.replace("đ", "d");
        input = input.replace("Đ", "D");
        return input;
    }

    public static Map<String, String> convertStringToMap(String temp, String regex, String regexToMap) {
        if (isNullOrEmpty(temp)) {
            return null;
        }
        String[] q = temp.split(regex);
        Map<String, String> lstParam = new HashMap<>();
        for (String a : q) {
            String key = a.substring(0, !a.contains(regexToMap) ? 1 : a.indexOf(regexToMap));
            String value = a.substring(a.indexOf(regexToMap) + 1);
            lstParam.put(key.trim(), value.trim());
        }
        return lstParam;
    }

    /*
     * toanld2 ham xu li khoang trang giua xau
     * **/
    public static String replaceSpaceSolr(String inputLocation) {
        if (inputLocation == null || inputLocation.trim().isEmpty()) {
            return "";
        }
        String[] arr = inputLocation.split(" ");
        String result = "";
        for (String s : arr) {
            if (!"".equals(s.trim())) {
                if (!"".equals(result)) {
                    result += "\\ ";
                }
                result += s.trim();
            }
        }
        return result;
    }

    public static boolean isNumber(String string) {
        return !isNullOrEmpty(string) && string.trim().matches("^\\d+$");
    }

    /**
     * Ham format isdn bo +,0,84,084 o dau trong chuoi ISDN nhap vao
     *
     * @param rawIsdn
     * @return
     */
    public static String formatIsdn(String rawIsdn) {
        if (isNullOrEmpty(rawIsdn)) return "";
        String isdn = rawIsdn.trim();
        while (isdn.startsWith("0") || isdn.startsWith("+")) {
            isdn = isdn.substring(1);
        }
        while (isdn.startsWith("84") && isdn.length() > 10) {
            isdn = isdn.substring(2);
        }
        return isdn;
    }

    public static String formatAndAdd84(String isdn) {
        if (isNullOrEmpty(isdn)) return "";
        return "84" + formatIsdn(isdn);
    }


    public static boolean isValidFraction(String str) {
        if (str != null) {
            try {
                String tmp[] = str.split("/");
                if (tmp.length == 2) {
                    if (safeToLong(tmp[0]) < safeToLong(tmp[1])) {
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;

    }

    /**
     * Tim nhung phan tu co o collection a ma khong co o collection b
     *
     * @param a
     * @param b
     * @param <T>
     * @return
     */
    public static <T> List<T> subtract(Collection<T> a, Collection<T> b) {
        if (a == null) {
            return new ArrayList<>();
        }
        if (b == null) {
            return new ArrayList<>(a);
        }
        List<T> list = new ArrayList<>(a);
        list.removeAll(b);
        return list;
    }

    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        if (a == null || b == null) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>(a);
        list.retainAll(b);
        return list;
    }

    public static <T> List<T> add(Collection<T> a, Collection<T> b) {
        if (a == null && b == null) {
            return new ArrayList<>();
        }

        if (a == null) {
            return new ArrayList<>(b);
        }

        if (b == null) {
            return new ArrayList<>(a);
        }

        List<T> list = new ArrayList<>(a);
        list.addAll(b);
        return list;
    }

    public static String removeStartingZeroes(String number) {
        if (DataUtil.isNullOrEmpty(number)) {
            return "";
        }
        return CharMatcher.anyOf("0").trimLeadingFrom(number);
    }

    /**
     * Trim tat ca ki tu trang, bao gom ca ki tu trang 2 byte ma ham trim binh thuong cua java khong trim duoc
     *
     * @param needToTrimString Xau can trim
     * @return "" neu la xau null hoac trang, con lai tra ve xau sau khi trim, "" neu trim xong khong con gi
     */
    public static String trim(String needToTrimString) {
        if (needToTrimString == null) {
            return "";
        }
        return CharMatcher.WHITESPACE.trimFrom(needToTrimString);
    }

    /**
     * Trim string
     *
     * @param str
     * @param alt: sau thay the khi str null
     * @return
     */
    public static String trim(String str, String alt) {
        if (str == null) {
            return alt;
        }
        return str.trim();
    }

    /**
     * Trim tat ca ki tu trang, bao gom ca ki tu trang 2 byte ma ham trim binh thuong cua java khong trim duoc
     * Tra ve xau lowercase cua xau da trim
     *
     * @param needToTrimString Xau can trim
     * @return "" neu la xau null hoac trang, con lai tra ve xau sau khi trim, "" neu trim xong khong con gi
     */
    public static String trimAndLowerCase(String needToTrimString) {
        return StringUtils.lowerCase(trim(needToTrimString));
    }

    public static BigDecimal defaultIfSmallerThanZero(BigDecimal value) {
        return defaultIfSmallerThanZero(value, BigDecimal.ZERO);
    }

    public static BigDecimal defaultIfSmallerThanZero(BigDecimal value, BigDecimal defaultValue) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return defaultValue;
        }
        return value;
    }

    public static String formatStringDateSubAge(String date) {
        if (date == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(date, 4, 6);
        str.append("/");
        str.append(date, 0, 4);
        return str.toString();
    }

    /**
     * @param isdnCheck1
     * @return
     * @desc loai bo cac so 0 o dau truong isdn
     * @author quangkm
     */
    public static String removeZeroAtBeginText(String isdnCheck1) {
        String temp = isdnCheck1;
        if (!DataUtil.isNullObject(temp)) {
            if ("0".equals(DataUtil.safeToString(temp.charAt(0)))) {
                temp = DataUtil.safeToString(temp.substring(1)).trim();
                temp = removeZeroAtBeginText(temp);
            }
        }
        return StringUtils.trimToEmpty(temp);
    }

    public static String stripAccents(String org) {
        String strippedAccent = convertUnicode2Nosign(StringUtils.stripAccents(org));
        return CharMatcher.WHITESPACE.collapseFrom(strippedAccent, ' ');
    }

    public static boolean campareNoSignString(Object obj1, Object obj2) {
        boolean result = false;
        String temp1 = obj1 != null ? convertUnicode2Nosign(obj1.toString()) : "";
        String temp2 = obj2 != null ? convertUnicode2Nosign(obj2.toString()) : "";
        if (temp1.toLowerCase().equals(temp2.toLowerCase())) {
            result = true;
        }
        return result;
    }

    public static String convertUnicode2Nosign(String org) {

        char arrChar[] = org.toCharArray();
        char result[] = new char[arrChar.length];
        for (int i = 0; i < arrChar.length; i++) {
            switch (arrChar[i]) {
                case '\u00E1':
                case '\u00E0':
                case '\u1EA3':
                case '\u00E3':
                case '\u1EA1':
                case '\u0103':
                case '\u1EAF':
                case '\u1EB1':
                case '\u1EB3':
                case '\u1EB5':
                case '\u1EB7':
                case '\u00E2':
                case '\u1EA5':
                case '\u1EA7':
                case '\u1EA9':
                case '\u1EAB':
                case '\u1EAD':
                case '\u0203':
                case '\u01CE': {
                    result[i] = 'a';
                    break;
                }
                case '\u00E9':
                case '\u00E8':
                case '\u1EBB':
                case '\u1EBD':
                case '\u1EB9':
                case '\u00EA':
                case '\u1EBF':
                case '\u1EC1':
                case '\u1EC3':
                case '\u1EC5':
                case '\u1EC7':
                case '\u0207': {
                    result[i] = 'e';
                    break;
                }
                case '\u00ED':
                case '\u00EC':
                case '\u1EC9':
                case '\u0129':
                case '\u1ECB': {
                    result[i] = 'i';
                    break;
                }
                case '\u00F3':
                case '\u00F2':
                case '\u1ECF':
                case '\u00F5':
                case '\u1ECD':
                case '\u00F4':
                case '\u1ED1':
                case '\u1ED3':
                case '\u1ED5':
                case '\u1ED7':
                case '\u1ED9':
                case '\u01A1':
                case '\u1EDB':
                case '\u1EDD':
                case '\u1EDF':
                case '\u1EE1':
                case '\u1EE3':
                case '\u020F': {
                    result[i] = 'o';
                    break;
                }
                case '\u00FA':
                case '\u00F9':
                case '\u1EE7':
                case '\u0169':
                case '\u1EE5':
                case '\u01B0':
                case '\u1EE9':
                case '\u1EEB':
                case '\u1EED':
                case '\u1EEF':
                case '\u1EF1': {
                    result[i] = 'u';
                    break;
                }
                case '\u00FD':
                case '\u1EF3':
                case '\u1EF7':
                case '\u1EF9':
                case '\u1EF5': {
                    result[i] = 'y';
                    break;
                }
                case '\u0111': {
                    result[i] = 'd';
                    break;
                }
                case '\u00C1':
                case '\u00C0':
                case '\u1EA2':
                case '\u00C3':
                case '\u1EA0':
                case '\u0102':
                case '\u1EAE':
                case '\u1EB0':
                case '\u1EB2':
                case '\u1EB4':
                case '\u1EB6':
                case '\u00C2':
                case '\u1EA4':
                case '\u1EA6':
                case '\u1EA8':
                case '\u1EAA':
                case '\u1EAC':
                case '\u0202':
                case '\u01CD': {
                    result[i] = 'A';
                    break;
                }
                case '\u00C9':
                case '\u00C8':
                case '\u1EBA':
                case '\u1EBC':
                case '\u1EB8':
                case '\u00CA':
                case '\u1EBE':
                case '\u1EC0':
                case '\u1EC2':
                case '\u1EC4':
                case '\u1EC6':
                case '\u0206': {
                    result[i] = 'E';
                    break;
                }
                case '\u00CD':
                case '\u00CC':
                case '\u1EC8':
                case '\u0128':
                case '\u1ECA': {
                    result[i] = 'I';
                    break;
                }
                case '\u00D3':
                case '\u00D2':
                case '\u1ECE':
                case '\u00D5':
                case '\u1ECC':
                case '\u00D4':
                case '\u1ED0':
                case '\u1ED2':
                case '\u1ED4':
                case '\u1ED6':
                case '\u1ED8':
                case '\u01A0':
                case '\u1EDA':
                case '\u1EDC':
                case '\u1EDE':
                case '\u1EE0':
                case '\u1EE2':
                case '\u020E': {
                    result[i] = 'O';
                    break;
                }
                case '\u00DA':
                case '\u00D9':
                case '\u1EE6':
                case '\u0168':
                case '\u1EE4':
                case '\u01AF':
                case '\u1EE8':
                case '\u1EEA':
                case '\u1EEC':
                case '\u1EEE':
                case '\u1EF0': {
                    result[i] = 'U';
                    break;
                }
                case '\u00DD':
                case '\u1EF2':
                case '\u1EF6':
                case '\u1EF8':
                case '\u1EF4': {
                    result[i] = 'Y';
                    break;
                }

                case '\u0110':
                case '\u00D0':
                case '\u0089': {
                    result[i] = 'D';
                    break;
                }
                default:
                    result[i] = arrChar[i];
            }
        }
        return new String(result);
    }

    public static Object convertNullToEmpty(Object value) {
        return value == null ? "" : value;
    }

    public static String getTrailingNumber(@Nonnull String textContainNumber) {
        String prefix = CharMatcher.DIGIT.trimTrailingFrom(textContainNumber);
        return StringUtils.removeStart(textContainNumber, prefix);
    }

    //Phund them co dinh
    public static String apList2String(List lstAPModel) {
        String result = "";
        if (lstAPModel != null && !DataUtil.isNullOrEmpty(lstAPModel)) {
            result = Joiner.on("@").skipNulls().join(lstAPModel) + "@";
        }
        return result;
    }

    public static boolean safeEqual(Object obj1, Object obj2) {
        return ((obj1 != null) && (obj2 != null) && obj2.toString().equals(obj1.toString()));
    }

    //thiendn1: format cho don vi tien te khi in hoa don
    public static Object convertCommaToDot(Object value) {
        if (!(value instanceof Number)) {
            return value;
        }
        DecimalFormat formatter = new DecimalFormat("###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    public static String getMimeType(String fileExtension) {
        switch (fileExtension) {
            case "pdf":
                return "application/pdf";
            case "png":
                return "image/png";
            case "jpg":
                return "image/jpeg";
            case "bmp":
                return "image/bmp";
            case "gif":
                return "image/gif";
            case "jpe":
                return "image/jpeg";
            case "jpeg":
                return "image/jpeg";
            default:
                return "";
        }
    }

    public static boolean checkPhone(String input) {
        if (isNullOrEmpty(input)) {
            return true;
        }
        return validateStringByPattern(input, PHONE_PATTERN);
    }

    /**
     * ham compare 2 object Model, chi dung voi cac thuoc tinh kieu nguyen thuy (primitive type)
     *
     * @param oldObj
     * @param newObj
     * @return
     */
    public static boolean compareTwoObj(Object oldObj, Object newObj) {
        try {
            if ((oldObj == null && newObj != null) || (oldObj != null && newObj == null)) {
                return false;
            }
            if (oldObj == null) {
                return true;
            }
            if (!safeEqual(oldObj.getClass().getName(), newObj.getClass().getName())) {
                return false;
            }
            Method[] arrMethod = oldObj.getClass().getDeclaredMethods();
            Method tempMethod;
            for (Method anArrMethod : arrMethod) {
                tempMethod = anArrMethod;
                if (!tempMethod.getName().startsWith("get")) {
                    continue;
                }
                Object oldBO = tempMethod.invoke(oldObj);
                Object newBO = tempMethod.invoke(newObj);
                String oldValue = "";
                if (oldBO != null) {
                    if (oldBO instanceof Date) {
//                        oldValue = DateTimeUtils.convertDateTimeToString((Date) oldBO);
                        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        oldValue = yyyyMMdd.format(oldBO);
                    } else {
                        oldValue = oldBO.toString();
                    }
                }
                String newValue = "";
                if (newBO != null) {
                    if (newBO instanceof Date) {
//                        newValue = DateTimeUtils.convertDateTimeToString((Date) newBO);
                        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        newValue = yyyyMMdd.format(newBO);
                    } else {
                        newValue = newBO.toString();
                    }
                }
                if (!oldValue.equals(newValue)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static String getEndPoint() {
        String endPoint = "";
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            String hostname = InetAddress.getLocalHost().getHostName();
            InetAddress addresses = InetAddress.getByName(hostname);
            for (ObjectName obj : objs) {
                String port = obj.getKeyProperty("port");
                String host = addresses.getHostAddress();
                endPoint = host + ":" + port;
                return endPoint;
            }
            if (isNullOrEmpty(endPoint)) {
                endPoint = addresses.getHostAddress();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return endPoint;
    }

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(number.toString(16));

            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();
        } catch (Exception e) {
            logger.error("File name cannot encrypt: " + input);
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    public static boolean checkDigit(String str) {
        return str.matches("(\\d+)");
    }

    public static boolean checkNotSpecialCharacter(String str) {
        return str.matches("^[A-Za-z0-9_]+");
    }


    static private byte[] getKey(byte[] arrBTmp) throws Exception {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return arrB;
    }

    public static Long safeAbs(Long number) {
        return safeAbs(number, 0L);

    }

    public static Long safeAbs(Long number, Long defaultValue) {
        if (number == null) {
            if (defaultValue == null) {
                return 0L;
            }
            return defaultValue < 0 ? -defaultValue : defaultValue;
        }

        return number < 0 ? -number : number;
    }


    public static String firstNonEmpty(String... strings) {
        for (String string : strings) {
            if (!isNullOrEmpty(string)) {
                return string;
            }
        }
        return "";
    }

    /**
     * ham làm tron so voi so thap phan sau dau phay
     *
     * @param value
     * @param numberAfterDot
     * @return
     */
    public static BigDecimal roundNumber(BigDecimal value, int numberAfterDot) {
        return value.setScale(numberAfterDot, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Ham replace chuoi mat khau plain_text khi luu o ws thanh chuoi ma hoa
     *
     * @param request
     * @return
     */
    public static String replaceSensitiveInfo(String request) {
        try {
            if (!DataUtil.isNullOrEmpty(request)) {
                return PASSWORD_PATTERN.matcher(request).replaceAll("<$1>ENC(" + PassTranformer.encrypt("$2") + ")</$1>");
            }
            return request;
        } catch (Exception e) {
            logger.error("Không mã hóa được password: " + e.getMessage(), e);
            return request;
        }
    }

    public static String getResponse(String description) {
        if (!DataUtil.isNullOrEmpty(description)) {
            String response = StringUtils.substringBetween(description, "<return>", "</return>");
            response = "<return>" + response + "</return>";
            return response;
        }
        return description;
    }

    /**
     * Distinct by key
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    /**
     * ham cat chuoi neu chuoi dai hon max  length truyen vao
     *
     * @param strOr
     * @param lengthMax
     * @return
     */
    public static String subStringIfLonger(String strOr, int lengthMax) {
        String str = DataUtil.deepCloneObject(strOr);
        if (DataUtil.isNullOrEmpty(str)) {
            return str;
        }
        if (lengthMax <= 0) {
            return "";
        }
        if (str.length() > lengthMax) {
            str = str.substring(0, lengthMax);
            return str;
        }
        return str;
    }

    public static boolean isUnicodeString(String org) {
        char[] arrChar = org.toCharArray();

        for (int i = 0; i < arrChar.length; ++i) {
            switch (arrChar[i]) {
                case '\u0089':
                case 'Ð':
                case 'Đ':
                    return true;
                case 'À':
                case 'Á':
                case 'Â':
                case 'Ã':
                case 'Ă':
                case 'Ǎ':
                case 'Ȃ':
                case 'Ạ':
                case 'Ả':
                case 'Ấ':
                case 'Ầ':
                case 'Ẩ':
                case 'Ẫ':
                case 'Ậ':
                case 'Ắ':
                case 'Ằ':
                case 'Ẳ':
                case 'Ẵ':
                case 'Ặ':
                    return true;
                case 'È':
                case 'É':
                case 'Ê':
                case 'Ȇ':
                case 'Ẹ':
                case 'Ẻ':
                case 'Ẽ':
                case 'Ế':
                case 'Ề':
                case 'Ể':
                case 'Ễ':
                case 'Ệ':
                    return true;
                case 'Ì':
                case 'Í':
                case 'Ĩ':
                case 'Ỉ':
                case 'Ị':
                    return true;
                case 'Ò':
                case 'Ó':
                case 'Ô':
                case 'Õ':
                case 'Ơ':
                case 'Ȏ':
                case 'Ọ':
                case 'Ỏ':
                case 'Ố':
                case 'Ồ':
                case 'Ổ':
                case 'Ỗ':
                case 'Ộ':
                case 'Ớ':
                case 'Ờ':
                case 'Ở':
                case 'Ỡ':
                case 'Ợ':
                    return true;
                case 'Ù':
                case 'Ú':
                case 'Ũ':
                case 'Ư':
                case 'Ụ':
                case 'Ủ':
                case 'Ứ':
                case 'Ừ':
                case 'Ử':
                case 'Ữ':
                case 'Ự':
                    return true;
                case 'Ý':
                case 'Ỳ':
                case 'Ỵ':
                case 'Ỷ':
                case 'Ỹ':
                    return true;
                case 'à':
                case 'á':
                case 'â':
                case 'ã':
                case 'ă':
                case 'ǎ':
                case 'ȃ':
                case 'ạ':
                case 'ả':
                case 'ấ':
                case 'ầ':
                case 'ẩ':
                case 'ẫ':
                case 'ậ':
                case 'ắ':
                case 'ằ':
                case 'ẳ':
                case 'ẵ':
                case 'ặ':
                    return true;
                case 'è':
                case 'é':
                case 'ê':
                case 'ȇ':
                case 'ẹ':
                case 'ẻ':
                case 'ẽ':
                case 'ế':
                case 'ề':
                case 'ể':
                case 'ễ':
                case 'ệ':
                    return true;
                case 'ì':
                case 'í':
                case 'ĩ':
                case 'ỉ':
                case 'ị':
                    return true;
                case 'ò':
                case 'ó':
                case 'ô':
                case 'õ':
                case 'ơ':
                case 'ȏ':
                case 'ọ':
                case 'ỏ':
                case 'ố':
                case 'ồ':
                case 'ổ':
                case 'ỗ':
                case 'ộ':
                case 'ớ':
                case 'ờ':
                case 'ở':
                case 'ỡ':
                case 'ợ':
                    return true;
                case 'ù':
                case 'ú':
                case 'ũ':
                case 'ư':
                case 'ụ':
                case 'ủ':
                case 'ứ':
                case 'ừ':
                case 'ử':
                case 'ữ':
                case 'ự':
                    return true;
                case 'ý':
                case 'ỳ':
                case 'ỵ':
                case 'ỷ':
                case 'ỹ':
                    return true;
                case 'đ':
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public static StringBuilder formatPrdId(Long prdId) {

        StringBuilder stringBuilder = new StringBuilder();
        String prd = prdId.toString().trim();
        String year = prd.substring(0, 4);
        String month = prd.substring(4, 6);

        stringBuilder.append(month);
        stringBuilder.append("/");
        stringBuilder.append(year);

        return stringBuilder;
    }

    public static Long convertDateToLong(String d) {
        Date date = new Date(d);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        if (d == null) {
            return 0L;
        }
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        String sdate = dateFormat.format(cal.getTime());
        return Long.valueOf(sdate);
    }

    public static Long convertStringToLong(String month) {
        String month2 = month.replace("/", "");
        String year = month2.substring(2, 6);
        String mon = month2.substring(0, 2);
        String date = year + mon + "01";
        return Long.valueOf(date);

    }

    public static Long convertStringToFirstQuarter(String month) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date d = sdf.parse(month);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        LocalDate date = cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String firstDate = String.valueOf(date);
        String customDate = firstDate.replace("-", "");
        Long firstDateOfQuarter = Long.valueOf(customDate);
        return firstDateOfQuarter;
    }

    public static Long convertStringToFirstYear(String month) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date d = sdf.parse(month);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        LocalDate date = cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String firstDate = String.valueOf(date);
        String customDate = firstDate.replace("-", "");
        Long firstDateOfQuarter = Long.valueOf(customDate);
        return firstDateOfQuarter;
    }

    public static String convertDateForFile(String d) {
        if (isNullOrEmpty(d))
            return "";
        Date date = new Date(d);
        DateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
        String sdate = dateFormat.format(date);
        return sdate;
    }

    public static boolean compareDateInFileAndSystem(String d1) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(d1);
            Date d2 = new Date();
            Date date2 = sdf.parse(sdf.format(d2));

            if (date1.compareTo(date2) >= 0) {
                check = true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static boolean compareFullDateFileAndSystem(String d1) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(d1);
            Date d2 = new Date();
            Date date2 = sdf.parse(sdf.format(d2));
            LocalDate localDate = Instant.ofEpochMilli(date1.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate localDateTwo = Instant.ofEpochMilli(date2.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (localDate.compareTo(localDateTwo) >= 0) {
                check = true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static boolean compareQuarterInFileAndSystem(String d1) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(d1);
            Date d2 = new Date();
            Date date2 = sdf.parse(sdf.format(d2));

            if ((date1.getMonth() / 3) + 1 < (date2.getMonth() / 3) + 1 && date1.getYear() <= date2.getYear()) {
                check = false;
            } else {
                check = true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static boolean compareFromDateAndToDate(String pstrFromDate, String pstrToDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(pstrFromDate);
            Date date2 = sdf.parse(pstrToDate);
            if (pstrToDate != null) {
                if (date1.getMonth() > date2.getMonth() && date1.getYear() >= date2.getYear()) {
                    check = false;
                } else {
                    check = true;
                }
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static boolean compareYearInFileAndSystem(String d1) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(d1);
            Date d2 = new Date();
            Date date2 = sdf.parse(sdf.format(d2));

            if (date1.getYear() < date2.getYear()) {
                check = false;
            } else {
                check = true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static boolean compareDateAndLastSystemDate(String d1) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(d1);
            Instant instant = Instant.ofEpochMilli(date1.getTime());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            LocalDate date = localDateTime.toLocalDate();
            LocalDate date2 = LocalDate.now().minusMonths(1);

            if (date.getMonth().compareTo(date2.getMonth()) >= 0) {
                check = true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static boolean compareDateInFileAnd2100(String d1) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        boolean check = false;
        try {
            Date date1 = sdf.parse(d1);
            Date d2 = new Date("01/01/2100");
            Date date2 = sdf.parse(sdf.format(d2));

            if (date1.compareTo(date2) >= 0) {
                check = true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return check;

    }

    public static Long convertToPrdIdFirstDay(Long d) {
        String sdate;
        if (DataUtil.isNullOrZero(d)) {
            return 0L;
        } else {
            Date date = new Date(d);
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();
            if (DataUtil.isNullOrZero(d)) {
                return 0L;
            }
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            sdate = dateFormat.format(cal.getTime());
        }
        return Long.valueOf(sdate);
    }

    /**
     * chuyển Long về prdId
     *
     * @param d
     * @return Long là prdId trả về
     * @author VuBL
     * @since 2019/10/07
     */
    public static Long convertToPrdId(Long d) {
        String sdate;
        if (DataUtil.isNullOrZero(d)) {
            return 0L;
        } else {
            Date date = new Date(d);
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();

            cal.setTime(date);
            sdate = dateFormat.format(cal.getTime());
        }
        return Long.valueOf(sdate);
    }

    public static String convertLocalDateToString(Long milliseconds, int plngCycleId) {
        LocalDate date = Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate();
        String formattedDate = null;
        if (plngCycleId == 1) {
            formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else if (plngCycleId == 2) {
            formattedDate = date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        } else if (plngCycleId == 3) {
            java.sql.Date sDate = java.sql.Date.valueOf(date);
            formattedDate = "Quý " + ((sDate.getMonth() / 3) + 1) + "/" + date.getYear();
        }
        return formattedDate;
    }

    public static boolean checkJson(JsonElement jsonElement) {
        if (jsonElement == null) {
            return false;
        }

        return true;
    }

    public static Date convertStringToDate(String s) {
        try {
            if (s != null) {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(s);
                return date;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * lay ra ngay dau quy cua ngay duoc chon
     *
     * @param plngMils
     * @return
     * @author DatNT
     * @version 1.0
     * @since 25/10/2019
     */
    public static Long getFirstDayOfQuarter(Long plngMils) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String vstrFirstDateOfQuarter;
        Date date = new Date(plngMils);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        vstrFirstDateOfQuarter = dateFormat.format(cal.getTime());
        return Long.valueOf(vstrFirstDateOfQuarter);
    }

    public static java.sql.Date getFirstDayQuarter(Long plngMils) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String month = String.valueOf(plngMils);
        StringBuilder sb = new StringBuilder(month);
        sb.insert(4, "/");
        sb.insert(7, "/");
        Date date = sdf.parse(sb.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        date = cal.getTime();
        java.sql.Date dateConvert = new java.sql.Date(date.getTime());

        return dateConvert;
    }

    /**
     * lay ra prd_id ngay dau nam
     *
     * @param plngMils
     * @return
     * @author VuBL
     * @since 2019/12
     */
    public static Long getFirstPrdOfYear(Long plngMils) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date = new Date(plngMils);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        date = cal.getTime();
        String sdate = dateFormat.format(date);
        Long ldate = Long.valueOf(sdate);
        return ldate;
    }

    public static java.sql.Date getFirstPrdYear(Long plngMils) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String month = String.valueOf(plngMils);
        StringBuilder sb = new StringBuilder(month);
        sb.insert(4, "/");
        sb.insert(7, "/");
        Date date = sdf.parse(sb.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        date = cal.getTime();
        java.sql.Date dateConvert = new java.sql.Date(date.getTime());
        return dateConvert;
    }


    public static int safeCompare(Long i1, Long i2) {
        if (i1 == null && i2 != null) {
            return -1;
        } else if (i1 != null && i2 == null) {
            return 1;
        } else if (i1 != null && i2 != null) {
            return i1.compareTo(i2);
        } else {
            return 0;
        }
    }

    public static java.sql.Date convertToFirstDateOfMonth(Long prdId) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        java.sql.Date date = null;
        if (!DataUtil.isNullOrZero(prdId)) {
            String month = String.valueOf(prdId);
            StringBuilder sb = new StringBuilder(month);
            sb.insert(4, "/");
            sb.insert(7, "/");
            Date date1 = sdf.parse(sb.toString());
            date = new java.sql.Date(date1.getTime());
        }
        return date;
    }

    public static List<java.sql.Date> getFirstDateBetweenDates(java.util.Date date1, java.util.Date date2) {
        List<java.sql.Date> dates = new ArrayList<>();
        if (date1 != null && date2 != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            cal.add(Calendar.MONTH, -1);
            for (int i = date1.getMonth(); i <= date2.getMonth(); i++) {
                java.util.Date date;
                cal.add(Calendar.MONTH, +1);
                date = cal.getTime();
                date.setDate(1);
                java.sql.Date da = new java.sql.Date(date.getTime());
                dates.add(da);
            }
        }
        return dates;
    }

    //    Lay ngay gio hien tai theo dinh dang yyyy-MM-dd hh:mm:ss
//
//      @return
//      @author Cuongdt
//      @version 1.0
//      @since 07/11/2019
//
    public Date getDateTimeNow() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String local = df.format(date);
        Date datenow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(local);
        return datenow;
    }

    public static String convertPrdToString(Long prdId) {
        String sprdId = String.valueOf(prdId);
        if (sprdId != null) {
            String year = sprdId.substring(0, 4);
            String month = sprdId.substring(4, 6);
            String day = sprdId.substring(6, 8);
            sprdId = day + "/" + month + "/" + year;
        }
        return sprdId;
    }

    /**
     * lay tat ca prd_id trong 1 khoang nhat dinh
     *
     * @author VuBL
     * @since 2020/07
     * @param prdIdStart
     * @param prdIdEnd
     * @return
     */
    public static String getStringPrdIdForWhere(Long prdIdStart, Long prdIdEnd, int cycleId) {
        StringBuilder stringPrdId = new StringBuilder();
        stringPrdId.append(prdIdStart);
        if (cycleId == Constants.CYCLE_ID.DAY) {
            Long prdIdStartAddOne = prdIdStart + 1;
            for (; prdIdStartAddOne <= prdIdEnd; prdIdStartAddOne++) {
                stringPrdId.append("," + prdIdStartAddOne);
            }
        }
        if (cycleId == Constants.CYCLE_ID.MONTH) {
            Long prdIdStartAddOne = prdIdStart + 100;
            for (; prdIdStartAddOne <= prdIdEnd; prdIdStartAddOne = prdIdStartAddOne + 100) {
                stringPrdId.append("," + prdIdStartAddOne);
            }
        }
        if (cycleId == Constants.CYCLE_ID.QUARTER) {
            Long prdIdStartAddOne = prdIdStart + 300;
            for (; prdIdStartAddOne <= prdIdEnd; prdIdStartAddOne = prdIdStartAddOne + 300) {
                stringPrdId.append("," + prdIdStartAddOne);
            }
        }
        return String.valueOf(stringPrdId);
    }

    public static Long convertToPrdIdEndMonth(String monthYear) {
        Long prdId = null;
        DateFormat dateFormat = new SimpleDateFormat("MMyyyy");
        DateFormat dfPrdId = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = dateFormat.parse(monthYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String strPrdId = dfPrdId.format(c.getTime());
        prdId = Long.parseLong(strPrdId);

        return prdId;
    }
}

