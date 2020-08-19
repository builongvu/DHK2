package vn.vissoft.dashboard.helper.vsa;

import com.viettel.passport.PassportWSService;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import viettel.passport.client.UserToken;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

public class VSAClient {

    private static Logger LOGGER = Logger.getLogger(VSAClient.class);
    private static PassportWSService vsaTransport;

    public static UserToken authen(String vsaUrl,String domain,String vsauser, String pass) {

        try {
            LOGGER.info("Authen : Check vsa username[" + vsauser + "] pass[***]");
            if (VSAClient.getVsaTransport() == null) {
                initVsaWs(vsaUrl);
            }
            String entireResponse = VSAClient.getVsaTransport().getPassportWSPort().validate(vsauser,pass, domain);
//            String entireResponse = fakeData();

            try {
                UserToken userToken = UserToken.parseXMLResponse(entireResponse);
                if (userToken!=null) {
                    LOGGER.info("authenticate successful [username=" + vsauser + "]");
                    return userToken;
                } else {
                    LOGGER.info("authenticate failure [username=" + vsauser + "]");
                }
            } catch (SAXException var6) {
                LOGGER.error(var6.getMessage(),var6);
            }

        } catch (Exception ee) {
            LOGGER.error(ee.getMessage(),ee);
            VSAClient.setVsaTransport(null);
        }

        LOGGER.info("complete check passport for : " + vsauser);
        return null;
    }

    private static String fakeData() {
        return "<Results>\n" +
                "    <UserData>\n" +
                "        <Row>\n" +
                "            <USER_ID>14510907</USER_ID>\n" +
                "            <USER_RIGHT>0</USER_RIGHT>\n" +
                "            <USER_NAME>slgb_admin_bi</USER_NAME>\n" +
                "            <PASSWORD>sNfXNqOfAyECZDLfPvSEJAORGew=</PASSWORD>\n" +
                "            <STATUS>1</STATUS>\n" +
                "            <EMAIL>congth2@viettel.com.vn</EMAIL>\n" +
                "            <CELLPHONE>84984260520</CELLPHONE>\n" +
                "            <TELEPHONE>84984260520</TELEPHONE>\n" +
                "            <GENDER>1</GENDER>\n" +
                "            <LAST_CHANGE_PASSWORD>30/09/2015</LAST_CHANGE_PASSWORD>\n" +
                "            <LOGIN_FAILURE_COUNT>0</LOGIN_FAILURE_COUNT>\n" +
                "            <IDENTITY_CARD>183432046</IDENTITY_CARD>\n" +
                "            <FULL_NAME>Trần Huy Công</FULL_NAME>\n" +
                "            <USER_TYPE_ID>83</USER_TYPE_ID>\n" +
                "            <CREATE_DATE>2011-03-03 00:00:00.0</CREATE_DATE>\n" +
                "            <LOCATION_ID>3</LOCATION_ID>\n" +
                "            <PASSWORDCHANGED>1</PASSWORDCHANGED>\n" +
                "            <LAST_LOGIN>10/01/2020</LAST_LOGIN>\n" +
                "            <PROFILE_ID>83</PROFILE_ID>\n" +
                "            <LAST_RESET_PASSWORD>29/07/2014</LAST_RESET_PASSWORD>\n" +
                "            <IP>192.168.176.190</IP>\n" +
                "            <DEPT_ID>411114</DEPT_ID>\n" +
                "            <POS_ID>2</POS_ID>\n" +
                "            <DEPT_NAME>Trung tâm Phần mềm</DEPT_NAME>\n" +
                "            <IGNORE_CHECK_IP>0</IGNORE_CHECK_IP>\n" +
                "            <LAST_LOCK>2019-03-19 09:00:31.0</LAST_LOCK>\n" +
                "            <CHECK_VALID_TIME>1</CHECK_VALID_TIME>\n" +
                "            <LOGIN_FAIL_ALLOW>0</LOGIN_FAIL_ALLOW>\n" +
                "            <TEMPORARY_LOCK_TIME>0</TEMPORARY_LOCK_TIME>\n" +
                "            <MAX_TMP_LOCK_ADAY>0</MAX_TMP_LOCK_ADAY>\n" +
                "            <PASSWORD_VALID_TIME>0</PASSWORD_VALID_TIME>\n" +
                "            <USER_VALID_TIME>0</USER_VALID_TIME>\n" +
                "            <ALLOW_MULTI_IP_LOGIN>0</ALLOW_MULTI_IP_LOGIN>\n" +
                "            <ALLOW_IP>*.*.*.*</ALLOW_IP>\n" +
                "            <ALLOW_LOGIN_TIME_START>0</ALLOW_LOGIN_TIME_START>\n" +
                "            <ALLOW_LOGIN_TIME_END>23</ALLOW_LOGIN_TIME_END>\n" +
                "            <ID>83</ID>\n" +
                "            <NAME>NO_CHECK</NAME>\n" +
                "            <NEED_CHANGE_PASSWORD>0</NEED_CHANGE_PASSWORD>\n" +
                "            <TIME_TO_CHANGE_PASSWORD>0</TIME_TO_CHANGE_PASSWORD>\n" +
                "            <CHECK_ID>0</CHECK_ID>\n" +
                "            <CHECK_IP_LAN>0</CHECK_IP_LAN>\n" +
                "            <IP_LAN>192.168.159.102</IP_LAN>\n" +
                "            <CHECK_IP>0</CHECK_IP>\n" +
                "            <TIME_TO_PASSWORD_EXPIRE>-1563</TIME_TO_PASSWORD_EXPIRE>\n" +
                "        </Row>\n" +
                "    </UserData>\n" +
                "    <Depts/>\n" +
                "    <Roles>\n" +
                "    </Roles>\n" +
                "    <ObjectAll>\n" +
                "       <Row>\n" +
                "            <OBJECT_ID>2014120623</OBJECT_ID>\n" +
                "            <APP_ID>2588</APP_ID>\n" +
                "            <PARENT_ID>16313</PARENT_ID>\n" +
                "            <STATUS>1</STATUS>\n" +
                "            <ORD>1</ORD>\n" +
                "            <OBJECT_URL>/reportMonthlyPaidAction.do</OBJECT_URL>\n" +
                "            <OBJECT_NAME>Báo cáo Trả phí hàng tháng </OBJECT_NAME>\n" +
                "            <DESCRIPTION>Báo cáo phát triển Viettelpay Pro mới </DESCRIPTION>\n" +
                "            <OBJECT_TYPE_ID>0</OBJECT_TYPE_ID>\n" +
                "        </Row>"+
                "    </ObjectAll>\n" +
                "</Results>";
    }

    private static void initVsaWs(String vsaUrl) throws Exception {
        try {
            URL url = null;

            try {
                URL baseUrl = PassportWSService.class.getResource(".");
                url = new URL(baseUrl, vsaUrl);
            } catch (MalformedURLException var5) {
                LOGGER.error(var5.getMessage(),var5);
            }

            VSAClient.setVsaTransport(new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService")));

        } catch (Exception ex) {
            VSAClient.setVsaTransport(null);

            throw ex;
        }
    }

    public static PassportWSService getVsaTransport() {
        return vsaTransport;
    }

    public static void setVsaTransport(PassportWSService vsaTransport) {
        VSAClient.vsaTransport = vsaTransport;
    }

    public static void main(String[] args) {
        String vsaUrl = "http://10.60.110.75:8069/passportBp/passportWS?wsdl";
        String domain = "bankplus2";
        String user = "congth";
        String password = "123456a@";
        UserToken userToken = VSAClient.authen(vsaUrl,domain,user,password);
        System.out.println(userToken);
    }
}
