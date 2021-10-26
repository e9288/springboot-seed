package com.iandna.gateway.hikVision;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Component(value = "hikVision")
public class HikvisionCommunicator {

	private static final Logger logger = LoggerFactory.getLogger(HikvisionCommunicator.class);

	@Value("${hikVision.appKey}")
	private String appKey
	@Value("${hikVision.appSecret}")
	private String appSecret;


	private static HttpsURLConnection conn;

	public static HttpsURLConnection getHttpsConn(String url, String path) {
		try {
			URL uri = new URL(url + path);
			conn = (HttpsURLConnection) uri.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setUseCaches(true);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		} catch (Exception e) {
			conn = null;
		}
		return conn;
	}

	public static String getResponse(HttpsURLConnection conn, String body) {
		StringBuffer response = null;
		try {
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

			wr.write(body);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.error("Input/Output Stream ERROR.");
			e.printStackTrace();
		}
		return response.toString();
	}

	public JSONObject getAcsToken() throws Exception {
		JSONObject param = new JSONObject();
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/token/get");
			
			String body = "appKey=" + appKey + "&appSecret=" + appSecret;
			logger.debug("#### body : " + body);
			String res = getResponse(conn, body);

			logger.debug(res);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			JSONObject data = JSONObject.fromObject(JSONSerializer.toJSON(jsonObject.get("data")));
			return data;
		} catch (Exception e) {
			throw new Exception("E998");
		}
	}

	/**
	 * 
	 * @param accessToken : root token
	 * @param accountName : custId
	 * @param password    : MD5(custPw)
	 * @return { code, accountId }
	 * @throws Exception
	 */
	public JSONObject createSubAccount(Map<String, Object> map) throws Exception {
		JSONObject result = new JSONObject();
		int code = 0;
		
		String accessToken = getAcsToken().getString("accessToken");
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/account/create");
			String body = "accessToken=" + accessToken + "&accountName=" + map.get("accountName").toString() + "&password=" + map.get("password").toString();
			String res = getResponse(conn, body);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			JSONObject data = JSONObject.fromObject(JSONSerializer.toJSON(jsonObject.get("data")));
			code = jsonObject.getInt("code");
			if (code == 200) {
				result = data;
				result.put("code", code);
			} else {
				logger.error("Create Sub Account Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.createSubAccount()", e);
			throw new Exception("ERROR createSubAccount");
		}

		if (code != 200) {
			throw new Exception("서브 어카운트 생성에 실패하였습니다. 상태코드 : " + code);
		}
		return result;
	}

	/**
	 * 
	 * @param accessToken : root token
	 * @param accountId   : sub Account ID
	 * @return { accountId, accountToken, expireTime }
	 * @throws Exception
	 */
	public JSONObject getSubAcsToken(String accountId) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		JSONObject result = new JSONObject();
		int code = 0;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/token/get");
			String body = "accessToken=" + accessToken + "&accountId=" + accountId;

			String res = getResponse(conn, body);
			logger.debug("####" + res);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			JSONObject data = JSONObject.fromObject(JSONSerializer.toJSON(jsonObject.get("data")));
			logger.debug("####" + data.toString());
			code = jsonObject.getInt("code");
			if (code == 200) {
				result = data;
				result.put("accountId", accountId);
				result.put("accountToken", result.getString("accessToken"));
				result.put("code", code);
				logger.error("Get Sub Account Info Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getSubAcsToken()", e);
			throw new Exception("하이크비전 서버 접근에 문제가 발생했습니다.");
		}

		if (code != 200) {
			throw new Exception("서브 어카운트 토큰 갱신에 실패하였습니다. 상태코드 : " + code);
		}
		return result;
	}

	public JSONObject getSubAccList() throws Exception {
		JSONObject result = new JSONObject();
		String accessToken = getAcsToken().getString("accessToken");
		int code = 0;
		ArrayList<JSONObject> subAccList = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/account/list");
			String body = "accessToken=" + accessToken;
			String res = getResponse(conn, body);
			logger.debug(res);

			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");
			if (code == 200) {
				JSONArray subList = (JSONArray) jsonObject.get("data");

				subAccList = new ArrayList<JSONObject>();
				JSONObject tData = new JSONObject();
				int index = 0;
				for (int i = 0; i < subList.size(); i++) {
					tData = subList.getJSONObject(index);
					if (tData.getString("policy") == null) {
						tData.put("policy", "");
					}
					subAccList.add(tData);

					index++;
				}
				result.put("subAccList", subAccList);
			} else {
				logger.error("Get Sub Account List Error. Status : " + code);
			}
			
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getSubAccList()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 서브 어카운트 목록을 가져오지 못했습니다. 상태코드 : " + code);
		}
		return result;
	}

	public JSONObject addDevice(Map<String, Object> map) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		JSONObject result;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/device/add");
			String body = "accessToken=" + accessToken + "&deviceSerial=" + map.get("deviceSerial").toString() + "&validateCode="
					+ map.get("validateCode").toString();
			String res = getResponse(conn, body);
			result = JSONObject.fromObject(JSONSerializer.toJSON(res));
			
			logger.debug(res);
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.addDevice()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return result;
	}

	public int deleteDevice(String deviceSerial) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		int status = 0;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/device/delete");
			String body = "accessToken=" + accessToken + "&deviceSerial=" + deviceSerial;
			String res = getResponse(conn, body);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			status = jsonObject.getInt("code");

			logger.debug(res);
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.deleteDevice()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return status;
	}

	public int addSubAccountPermission(Map<String, Object> map) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		int status = 0;
		try {
			JSONObject statement = new JSONObject();
			statement.put("Permission", map.get("permitMethods").toString());
			statement.put("Resource", (ArrayList<String>)map.get("devList"));
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/statement/add");
			String body = "accessToken=" + accessToken + "&accountId=" + map.get("accountId").toString() + "&statement="
					+ statement.toString();
			logger.debug(body);
			String res = getResponse(conn, body);
			logger.debug(res);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			status = jsonObject.getInt("code");
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.addSubAccountPermission()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return status;
	}

	public int editSubAccountPermission(Map<String, Object> map) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		int status = 0;
		try {
			JSONArray statements = new JSONArray();
			JSONObject statement = new JSONObject();
			JSONObject policy = new JSONObject();
			statement.put("Permission", map.get("permitMethods").toString());
			statement.put("Resource", (ArrayList<String>)map.get("devList"));
			statements.add(statement);
			policy.put("Statement", statements);
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/policy/set");
			String body = "accessToken=" + accessToken + "&accountId=" + map.get("accountId") + "&policy=" + policy.toString();
			logger.debug(body);
			String res = getResponse(conn, body);
			logger.debug(res);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			status = jsonObject.getInt("code");
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.addSubAccountPermission()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return status;
	}

	public int delSubAccountPermission(Map<String, Object> map) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		int status = 0;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/statement/delete");
			String body = "accessToken=" + accessToken + "&accountId=" + map.get("accountId").toString() + "&deviceSerial=" + map.get("deviceSerial").toString();
			logger.debug(body);
			String res = getResponse(conn, body);
			logger.debug(res);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			status = jsonObject.getInt("code");
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.delSubAccountPermission()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return status;
	}

	public JSONObject getCameraList() throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		JSONObject result = new JSONObject();
		int code = 0;
		ArrayList<JSONObject> camList = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/camera/list");
			String body = "accessToken=" + accessToken;
			String res = getResponse(conn, body);
			logger.debug(res);

			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");
			if (code == 200) {
				JSONArray jCamList = (JSONArray) jsonObject.get("data");

				camList = new ArrayList<JSONObject>();
				JSONObject tData = new JSONObject();
				int index = 0;
				for (int i = 0; i < jCamList.size(); i++) {
					tData = jCamList.getJSONObject(index);
					camList.add(tData);
					index++;
				}
				result.put("camList", camList);
			} else {
				logger.error("Get Camera List Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getCameraList()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 카메라 리스트 조회에 실패하였습니다. 상태코드 : " + code);
		}
		return result;
	}

	public JSONObject getAlarmInfoByDevice(JSONObject param) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		JSONObject result = new JSONObject();
		int code = 0;
		ArrayList<JSONObject> dataList = null;
		String body = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/alarm/device/list");
			body = "accessToken=" + accessToken + "&deviceSerial=" + param.getString("deviceSerial");
			if (!param.getString("pageSize").isEmpty()) {
				body += "&pageSize=" + param.getInt("pageSize");
			}
			if (!param.getString("pageStart").isEmpty()) {
				body += "&pageStart=" + param.getInt("pageStart");
			}
			if (!param.getString("startTime").isEmpty()) {
				body += "&startTime=" + param.getLong("startTime");
			}
			if (!param.getString("endTime").isEmpty()) {
				body += "&endTime=" + param.getLong("endTime");
			}
			if (!param.getString("status").isEmpty()) {
				body += "&status=" + param.getInt("status");
			}
			String res = getResponse(conn, body);

			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");
			if (code == 200) {
				JSONArray jDataList = (JSONArray) jsonObject.get("data");

				dataList = new ArrayList<JSONObject>();
				JSONObject tData = new JSONObject();
				int index = 0;
				for (int i = 0; i < jDataList.size(); i++) {
					tData = jDataList.getJSONObject(index);
					dataList.add(tData);
					index++;
				}
				result.put("alarmList", dataList);
			} else {
				logger.error("Get Alarm Info Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getCameraList()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 장치 알람정보 조회에 실패했습니다. 상태코드 : " + code);
		}
		return result;
	}

	public JSONObject getDevList() throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		JSONObject result = new JSONObject();
		int code = 0;
		ArrayList<JSONObject> dataList = null;
		String body = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/device/list");
			body = "accessToken=" + accessToken;
			String res = getResponse(conn, body);

			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");
			if (code == 200) {
				JSONArray jDataList = (JSONArray) jsonObject.get("data");

				dataList = new ArrayList<JSONObject>();
				JSONObject tData = new JSONObject();
				int index = 0;
				for (int i = 0; i < jDataList.size(); i++) {
					tData = jDataList.getJSONObject(index);
					dataList.add(tData);
					index++;
				}
				result.put("devList", dataList);
			} else {
				logger.error("Get Dev List Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getCameraList()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 장치 목록 조회에 실패했습니다. 상태코드 : " + code);
		}
		return result;
	}

	public JSONObject getDevInfo(JSONObject param) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		String body = null;
		int code = 0;
		JSONObject result = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/device/info");
			body = "accessToken=" + accessToken + "&deviceSerial=" + param.getString("deviceSerial");
			;
			String res = getResponse(conn, body);
			logger.debug(res);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");
			if (code == 200) {
				result = (JSONObject) jsonObject.get("data");
			} else {
				logger.error("Get Sub Account List Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getCameraList()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 장치 정보 조회에 실패하였습니다. 상태코드 : " + code);
		}
		return result;
	}

	public JSONObject getDevChInfo(JSONObject param) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		JSONObject result = new JSONObject();
		int code = 0;
		ArrayList<JSONObject> dataList = null;
		String body = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/device/camera/list");
			body = "accessToken=" + accessToken + "&deviceSerial=" + param.getString("deviceSerial");
			String res = getResponse(conn, body);

			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");
			if (code == 200) {
				JSONArray jDataList = (JSONArray) jsonObject.get("data");

				dataList = new ArrayList<JSONObject>();
				result = jDataList.getJSONObject(0);
				
			} else {
				logger.error("Get Dev Channel Info Error. Status : " + code);
			}
		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getDevChInfo()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 장치 채널 조회에 실패했습니다. 상태코드 : " + code);
		}
		return result;
	}

	public int editSubAccPw(JSONObject param) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		String body = null;
		int code = 0;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/account/updatePassword");
			body = "accessToken=" + accessToken + "&accountId=" + param.getString("accountId")
					+ "&oldPassword=" + param.getString("oldPassword") + "&newPassword="
					+ param.getString("newPassword");
			logger.debug(body);
			String res = getResponse(conn, body);
			logger.debug(res);

			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));

			code = Integer.parseInt(jsonObject.get("code").toString());

		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.getCameraList()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return code;
	}

	/**
	 * deviceName 변경 시 channelName도 같이 변경됨.
	 * 
	 * @param param param.accountToken : sub Account Token param.deviceSerial :
	 *              device serial no param.deviceName : 변경할 device Name
	 * @return
	 * @throws Exception
	 */
	public int editDeviceName(JSONObject param) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		int code = 0;
		String body = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/device/name/update");
			body = "accessToken=" + accessToken + "&deviceSerial=" + param.getString("deviceSerial")
					+ "&deviceName=" + param.getString("deviceName");
			String res = getResponse(conn, body);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");

		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.editDeviceName()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		if (code != 200) {
			throw new Exception("900 장치 이름 변경에 실패하였습니다. 상태코드 : " + code);
		}
		return code;
	}

	public int deleteSubAccount(JSONObject param) throws Exception {
		String accessToken = getAcsToken().getString("accessToken");
		int code = 0;
		String body = null;
		try {
			conn = getHttpsConn(ezBizUrl, "/api/lapp/ram/account/delete");
			body = "accessToken=" + accessToken + "&accountId=" + param.getString("accountId");
			String res = getResponse(conn, body);
			JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(res));
			code = jsonObject.getInt("code");

		} catch (Exception e) {
			logger.error("ERROR IN HikvisionCommunicater.editDeviceName()", e);
			throw new Exception("100 하이크비전 서버 접근에 문제가 발생했습니다.");
		}
		return code;
	}

	public static void main(String[] args) throws Exception {
//		logger.debug(getAcsToken().getString("accessToken"));
	}
}
